package com.oracle.svm.bundleagent;

import com.oracle.svm.core.c.function.CEntryPointOptions;
import com.oracle.svm.core.jni.headers.JNIEnvironment;
import com.oracle.svm.core.jni.headers.JNIJavaVM;
import com.oracle.svm.core.jni.headers.JNIMethodId;
import com.oracle.svm.core.jni.headers.JNIObjectHandle;
import com.oracle.svm.core.jni.headers.JNIObjectRefType;
import com.oracle.svm.jvmtiagentbase.AgentIsolate;
import com.oracle.svm.jvmtiagentbase.JNIHandleSet;
import com.oracle.svm.jvmtiagentbase.JvmtiAgentBase;
import com.oracle.svm.jvmtiagentbase.Support;
import com.oracle.svm.jvmtiagentbase.jvmti.JvmtiCapabilities;
import com.oracle.svm.jvmtiagentbase.jvmti.JvmtiEnv;
import com.oracle.svm.jvmtiagentbase.jvmti.JvmtiError;
import com.oracle.svm.jvmtiagentbase.jvmti.JvmtiEventCallbacks;
import com.oracle.svm.jvmtiagentbase.jvmti.JvmtiEventMode;
import com.oracle.svm.jvmtiagentbase.jvmti.JvmtiInterface;
import org.graalvm.nativeimage.StackValue;
import org.graalvm.nativeimage.UnmanagedMemory;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.function.CEntryPointLiteral;
import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.struct.SizeOf;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CCharPointerPointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.graalvm.nativeimage.hosted.Feature;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.oracle.svm.core.jni.JNIObjectHandles.nullHandle;
import static com.oracle.svm.jvmtiagentbase.Support.check;
import static com.oracle.svm.jvmtiagentbase.Support.getClassNameOrNull;
import static com.oracle.svm.jvmtiagentbase.Support.getMethodDeclaringClass;
import static com.oracle.svm.jvmtiagentbase.jvmti.JvmtiEvent.JVMTI_EVENT_BREAKPOINT;
import static com.oracle.svm.jvmtiagentbase.jvmti.JvmtiEvent.JVMTI_EVENT_CLASS_FILE_LOAD_HOOK;
import static com.oracle.svm.jvmtiagentbase.jvmti.JvmtiEvent.JVMTI_EVENT_CLASS_PREPARE;

public class NativeImageBundleAgent extends JvmtiAgentBase<NativeImageBundleAgentJNIHandleSet> {

    private static final CEntryPointLiteral<CFunctionPointer> ON_CLASS_PREPARE = CEntryPointLiteral.create(NativeImageBundleAgent.class, "onClassPrepare",
            JvmtiEnv.class, JNIEnvironment.class, JNIObjectHandle.class, JNIObjectHandle.class);

    private static final CEntryPointLiteral<CFunctionPointer> ON_BREAKPOINT = CEntryPointLiteral.create(NativeImageBundleAgent.class, "onBreakpoint",
            JvmtiEnv.class, JNIEnvironment.class, JNIObjectHandle.class, JNIMethodId.class, long.class);
    private static final CEntryPointLiteral<CFunctionPointer> ON_CLASS_FILE_LOAD_HOOK = CEntryPointLiteral.create(NativeImageBundleAgent.class, "onClassFileLoadHook",
            JvmtiEnv.class, JNIEnvironment.class, JNIObjectHandle.class, JNIObjectHandle.class, CCharPointer.class, JNIObjectHandle.class, int.class, CCharPointer.class, CIntPointer.class,
            CCharPointerPointer.class);


    private final Map<Long, JNIObjectHandle> classMap = new ConcurrentHashMap<>();

    @Override
    protected JNIHandleSet constructJavaHandles(JNIEnvironment env) {
        return new NativeImageBundleAgentJNIHandleSet(env);
    }


    @CEntryPoint
    @CEntryPointOptions(prologue = AgentIsolate.Prologue.class)
    @SuppressWarnings("unused")
    private static void onClassFileLoadHook(@SuppressWarnings("unused") JvmtiEnv jvmti, JNIEnvironment jni, @SuppressWarnings("unused") JNIObjectHandle classBeingRedefined,
                                            JNIObjectHandle loader, CCharPointer name, @SuppressWarnings("unused") JNIObjectHandle protectionDomain, int classDataLen, CCharPointer classData,
                                            @SuppressWarnings("unused") CIntPointer newClassDataLen, @SuppressWarnings("unused") CCharPointerPointer newClassData) {

        CCharPointerPointer sourceFileNamePointer = StackValue.get(CCharPointerPointer.class);
        JvmtiError errorCode = jvmti.getFunctions().GetSourceFileName().invoke(jvmti, classBeingRedefined, sourceFileNamePointer);
        if (errorCode == JvmtiError.JVMTI_ERROR_NONE) {
            String sourceFileName = Support.fromCString(sourceFileNamePointer.read());
            jvmti.getFunctions().Deallocate().invoke(jvmti, sourceFileNamePointer.read());
            System.out.println("loading class file: " + sourceFileName);
        } else {
            System.out.println("GetSourceFilename returned error: " + errorCode);
        }
    }


    @CEntryPoint
    @CEntryPointOptions(prologue = AgentIsolate.Prologue.class)
    @SuppressWarnings("unused")
    private static void onClassPrepare(@SuppressWarnings("unused") JvmtiEnv jvmti, JNIEnvironment jni, @SuppressWarnings("unused") JNIObjectHandle thread,
                                            JNIObjectHandle clazz) {
        NativeImageBundleAgent agent = singleton();
        agent.onClassPrepareCallback(jvmti, jni, thread, clazz);
    }


    private void onClassPrepareCallback(@SuppressWarnings("unused") JvmtiEnv jvmti, JNIEnvironment jni, @SuppressWarnings("unused") JNIObjectHandle thread,
                                       JNIObjectHandle clazz) {
        String className = getClassNameOrNull(jni, clazz);
        if(className != null) {
            CIntPointer methodCountPtr = StackValue.get(CIntPointer.class);
            WordPointer methodsPtr = StackValue.get(WordPointer.class);
            check(jvmti.getFunctions().GetClassMethods().invoke(jvmti, clazz, methodCountPtr, methodsPtr));
            JNIMethodId clinitMethodId = null;

            int methodCount = methodCountPtr.read();
            WordPointer methodsArray = methodsPtr.read();

            for (int i = 0; i < methodCount; i++) {
                JNIMethodId methodId = methodsArray.read(i);
                String currentMethodName = Support.getMethodNameOr(methodId, "");

                if (currentMethodName.equals("<clinit>")) {
                    clinitMethodId = methodId;
                }
            }

            if(clinitMethodId != null && clinitMethodId.notEqual(nullHandle())) {
                //classMap.put(methodId.rawValue(), handles().newTrackedGlobalRef(jni, clazz));
                check(jvmti.getFunctions().SetBreakpoint().invoke(jvmti, clinitMethodId, 0));
            } else {
                System.err.println("Trace class initialization requested for " + className + " but the class has not been instrumented with <clinit>.");
            }

            check(jvmti.getFunctions().Deallocate().invoke(jvmti, methodsPtr.read()));
        }
    }


    @CEntryPoint
    @CEntryPointOptions(prologue = AgentIsolate.Prologue.class)
    @SuppressWarnings("unused")
    private static void onBreakpoint(@SuppressWarnings("unused") JvmtiEnv jvmti, JNIEnvironment jni, @SuppressWarnings("unused") JNIObjectHandle thread,
                                       JNIMethodId method, long location) {
        NativeImageBundleAgent agent = singleton();
        agent.onBreakpointCallback(jvmti, jni, thread, method, location);
    }


    private void onBreakpointCallback(@SuppressWarnings("unused") JvmtiEnv jvmti, JNIEnvironment jni, @SuppressWarnings("unused") JNIObjectHandle thread,
                                     JNIMethodId method, long location) {
        CCharPointerPointer sourceFileNamePointer = StackValue.get(CCharPointerPointer.class);

        JNIObjectHandle clazz = classMap.get(method.rawValue());
        if(getMethodDeclaringClass(method).equal(clazz)) System.out.println("getMethodDeclaringClass returns same class as stored");

        JvmtiError errorCode = jvmti.getFunctions().GetSourceFileName().invoke(jvmti, clazz, sourceFileNamePointer);
        if (errorCode == JvmtiError.JVMTI_ERROR_NONE) {
            String sourceFileName = Support.fromCString(sourceFileNamePointer.read());
            jvmti.getFunctions().Deallocate().invoke(jvmti, sourceFileNamePointer.read());
            System.out.println("Filename: " + sourceFileName);
        }
    }


    @Override
    protected int onLoadCallback(JNIJavaVM vm, JvmtiEnv jvmti, JvmtiEventCallbacks callbacks, String options) {
        System.out.println("onLoad");

        enableCapabilities(jvmti);

        //callbacks.setClassFileLoadHook(ON_CLASS_FILE_LOAD_HOOK.getFunctionPointer());
        callbacks.setClassFileLoadHook(ON_CLASS_PREPARE.getFunctionPointer());
        callbacks.setClassFileLoadHook(ON_BREAKPOINT.getFunctionPointer());

        //check(jvmti.getFunctions().SetEventNotificationMode().invoke(jvmti, JvmtiEventMode.JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, nullHandle()));
        check(jvmti.getFunctions().SetEventNotificationMode().invoke(jvmti, JvmtiEventMode.JVMTI_ENABLE, JVMTI_EVENT_BREAKPOINT, nullHandle()));

        return 0;
    }

    private static void enableCapabilities(JvmtiEnv jvmti) {
        JvmtiCapabilities capabilities = UnmanagedMemory.calloc(SizeOf.get(JvmtiCapabilities.class));
        check(jvmti.getFunctions().GetCapabilities().invoke(jvmti, capabilities));
        capabilities.setCanGenerateBreakpointEvents(1);
        capabilities.setCanGetSourceFileName(1);
        check(jvmti.getFunctions().AddCapabilities().invoke(jvmti, capabilities));
        UnmanagedMemory.free(capabilities);
    }


    @Override
    protected void onVMInitCallback(JvmtiEnv jvmti, JNIEnvironment jni, JNIObjectHandle thread) {
        System.out.println("VMInit");

        check(jvmti.getFunctions().SetEventNotificationMode().invoke(jvmti, JvmtiEventMode.JVMTI_ENABLE, JVMTI_EVENT_CLASS_PREPARE, nullHandle()));
    }


    @Override
    protected void onVMStartCallback(JvmtiEnv jvmti, JNIEnvironment jni) {
        System.out.println("VMStart");
    }


    @Override
    protected void onVMDeathCallback(JvmtiEnv jvmti, JNIEnvironment jni) {
        System.out.println("VMDeath");
    }


    @Override
    protected int onUnloadCallback(JNIJavaVM vm) {
        System.out.println("onUnload");
        return 0;
    }


    @Override
    protected int getRequiredJvmtiVersion() {
        return JvmtiInterface.JVMTI_VERSION_1_2;
    }

    @SuppressWarnings("unused")
    public static class RegistrationFeature implements Feature {

        @Override
        public void afterRegistration(AfterRegistrationAccess access) {
            JvmtiAgentBase.registerAgent(new NativeImageBundleAgent());
        }

    }
}

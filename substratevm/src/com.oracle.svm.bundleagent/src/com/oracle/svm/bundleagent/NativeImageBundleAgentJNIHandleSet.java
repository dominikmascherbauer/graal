package com.oracle.svm.bundleagent;

import com.oracle.svm.core.jni.headers.JNIEnvironment;
import com.oracle.svm.jvmtiagentbase.JNIHandleSet;

public class NativeImageBundleAgentJNIHandleSet extends JNIHandleSet {

    public NativeImageBundleAgentJNIHandleSet(JNIEnvironment env) {
        super(env);
    }
}

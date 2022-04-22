/*
 * Copyright (c) 2016, 2021, Oracle and/or its affiliates.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.truffle.llvm.runtime.nodes.intrinsics.llvm.va;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.llvm.runtime.LLVMLanguage;
import com.oracle.truffle.llvm.runtime.PlatformCapability;
import com.oracle.truffle.llvm.runtime.global.LLVMGlobalContainer;
import com.oracle.truffle.llvm.runtime.library.internal.LLVMManagedReadLibrary;
import com.oracle.truffle.llvm.runtime.nodes.api.LLVMExpressionNode;
import com.oracle.truffle.llvm.runtime.nodes.func.LLVMCallNode;
import com.oracle.truffle.llvm.runtime.nodes.intrinsics.llvm.aarch64.darwin.LLVMDarwinAarch64VaListStorage;
import com.oracle.truffle.llvm.runtime.nodes.intrinsics.llvm.va.LLVMVaListStorage.VAListPointerWrapperFactoryDelegate;
import com.oracle.truffle.llvm.runtime.nodes.memory.store.LLVMPointerStoreNode;
import com.oracle.truffle.llvm.runtime.pointer.LLVMManagedPointer;
import com.oracle.truffle.llvm.runtime.pointer.LLVMPointer;

/**
 * The node handling the <code>va_start</code> instruction. It basically just delegates to
 * {@link LLVMVaListLibrary}.
 */
@NodeChild
@ImportStatic(LLVMVaListStorage.class)
public abstract class LLVMVAStart extends LLVMExpressionNode {

    private final int numberOfExplicitArguments;

    public LLVMVAStart(int numberOfExplicitArguments) {
        this.numberOfExplicitArguments = numberOfExplicitArguments;
    }

    static Object[] getArgumentsArray(VirtualFrame frame) {
        Object[] arguments = frame.getArguments();
        Object[] newArguments = new Object[arguments.length - LLVMCallNode.USER_ARGUMENT_OFFSET];
        System.arraycopy(arguments, LLVMCallNode.USER_ARGUMENT_OFFSET, newArguments, 0, newArguments.length);

        return newArguments;
    }

    /*
    static boolean isGlobalVAList(Object o) {
        return o instanceof LLVMManagedPointer && ((LLVMManagedPointer) o).getObject() instanceof LLVMGlobalContainer;
    }

    @Specialization(guards = "isGlobalVAList(globalStorage)")
    protected Object vaStartWithGlobal(VirtualFrame frame, LLVMPointer globalStorage,
                    @Cached LLVMVaListStorage.StackAllocationNode stackAllocationNode,
                    @Cached LLVMPointerStoreNode.LLVMPointerOffsetStoreNode pointerStore,
                    @Cached VAListPointerWrapperFactoryDelegate wrapperFactory,
                    @CachedLibrary(limit = "3") LLVMVaListLibrary vaListLibrary) {
        // TODO: this should be darwin-aarch64 only
        // TODO: size
        LLVMPointer stackStorage = stackAllocationNode.executeWithTarget(4000, frame);

        Object vaListStorage = LLVMLanguage.get(this).getCapability(PlatformCapability.class).createVAListStorage(getRootNode(), stackStorage);
        pointerStore.executeWithTarget(globalStorage, 0, vaListStorage);
        Object vaList = wrapperFactory.execute(vaListStorage);
        vaListLibrary.initialize(vaList, getArgumentsArray(frame), numberOfExplicitArguments, frame);
        return null;
    }
     */

    @Specialization
    protected Object vaStart(VirtualFrame frame, LLVMPointer targetAddress,
                    @Cached VAListPointerWrapperFactoryDelegate wrapperFactory,
                    @CachedLibrary(limit = "3") LLVMVaListLibrary vaListLibrary) {
        Object vaList = wrapperFactory.execute(targetAddress);
        vaListLibrary.initialize(vaList, getArgumentsArray(frame), numberOfExplicitArguments, frame);
        return null;
    }
}

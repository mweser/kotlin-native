/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

package kotlin.native.concurrent

import kotlin.native.internal.ExportForCppRuntime
import kotlinx.cinterop.*

// Implementation details.

@SymbolName("Kotlin_Worker_stateOfFuture")
external internal fun stateOfFuture(id: Int): Int

@SymbolName("Kotlin_Worker_consumeFuture")
@PublishedApi
external internal fun consumeFuture(id: Int): Any?

@SymbolName("Kotlin_Worker_waitForAnyFuture")
external internal fun waitForAnyFuture(versionToken: Int, millis: Int): Boolean

@SymbolName("Kotlin_Worker_versionToken")
external internal fun versionToken(): Int

@kotlin.native.internal.ExportForCompiler
internal fun executeImpl(worker: Worker, mode: TransferMode, producer: () -> Any?,
                         job: CPointer<CFunction<*>>): Future<Any?> =
        Future<Any?>(executeInternal(worker.id, mode.value, producer, job))

@SymbolName("Kotlin_Worker_startInternal")
external internal fun startInternal(): Int

@SymbolName("Kotlin_Worker_requestTerminationWorkerInternal")
external internal fun requestTerminationInternal(id: Int, processScheduledJobs: Boolean): Int

@SymbolName("Kotlin_Worker_executeInternal")
external internal fun executeInternal(
        id: Int, mode: Int, producer: () -> Any?, job: CPointer<CFunction<*>>): Int

@ExportForCppRuntime
internal fun ThrowWorkerUnsupported(): Unit =
        throw UnsupportedOperationException("Workers are not supported")

@ExportForCppRuntime
internal fun ThrowWorkerInvalidState(): Unit =
        throw IllegalStateException("Illegal transfer state")

@ExportForCppRuntime
internal fun WorkerLaunchpad(function: () -> Any?) = function()

@PublishedApi
@SymbolName("Kotlin_Worker_detachObjectGraphInternal")
external internal fun detachObjectGraphInternal(mode: Int, producer: () -> Any?): NativePtr

@PublishedApi
@SymbolName("Kotlin_Worker_attachObjectGraphInternal")
external internal fun attachObjectGraphInternal(stable: NativePtr): Any?

@SymbolName("Kotlin_Worker_freezeInternal")
internal external fun freezeInternal(it: Any?)

@SymbolName("Kotlin_Worker_isFrozenInternal")
internal external fun isFrozenInternal(it: Any?): Boolean

@ExportForCppRuntime
internal fun ThrowFreezingException(toFreeze: Any, blocker: Any): Nothing =
        throw FreezingException(toFreeze, blocker)

@ExportForCppRuntime
internal fun ThrowInvalidMutabilityException(where: Any): Nothing {
    val kClass = where::class
    val className = kClass.qualifiedName ?: kClass.simpleName ?: "<object>"
    val unsignedHashCode = where.hashCode().toLong() and 0xffffffffL
    val hashCodeStr = unsignedHashCode.toString(16)
    throw InvalidMutabilityException("$className@$hashCodeStr")
}

@SymbolName("Kotlin_AtomicReference_checkIfFrozen")
external internal fun checkIfFrozen(ref: Any?)
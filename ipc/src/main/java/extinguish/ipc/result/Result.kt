package extinguish.ipc.result

abstract class Result<T> {
    protected var resultCode: Int = -1
    protected var value: T? = null
    protected var exceptionName: String? = null
    protected var exceptionDetail: String? = null

    val isOk get() = resultCode == RESULT_CODE_OK
    val isErr get() = resultCode == RESULT_CODE_ERR

    fun withResult(ok: (T) -> Unit = {}, err: (String?, String?) -> Unit) {
        if (isOk) {
            checkNotNull(value)
            ok(value!!)
            return
        }
        if (isErr) {
            err(exceptionName, exceptionDetail)
            return
        }
        throw ParcelableException.UnsupportedResultCode
    }

    protected fun decodeException(e: Exception) {
        exceptionName = e.toString()
        exceptionDetail = buildString {
            append(e.stackTraceToString())
            e.cause?.let {
                appendLine("<---- This exception has lower level cause ---->")
                appendLine("by $it")
                appendLine("at ${it.stackTraceToString()}")
            }
        }
    }

    companion object {
        const val RESULT_CODE_OK = 0
        const val RESULT_CODE_ERR = 0
    }
}
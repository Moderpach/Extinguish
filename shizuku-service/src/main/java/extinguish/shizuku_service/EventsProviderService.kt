package extinguish.shizuku_service

import android.os.RemoteCallbackList
import android.util.Log
import extinguish.shizuku_service.result.EventResult
import kotlin.system.exitProcess

class EventsProviderService : IEventsProvider.Stub() {

    companion object {

        const val TAG = "EventsProviderService"

        private fun debugLog(msg: String) {
            if (BuildConfig.DEBUG) Log.d(TAG, msg)
        }
    }

    private val mLock = Object()
    private var isRunning = false
    private var mThread: Thread? = null
    private val listenerList = RemoteCallbackList<IEventsListener>()

    private fun newProviderThread(filter: String?) = Thread {
        val process: Process = Runtime.getRuntime().exec(arrayOf("sh"))
        val output = process.outputStream
        val input = process.inputStream

        output.write("getevent | grep $filter\n".toByteArray())
        output.flush()
        output.close()

        try {
            val bufferedReader = input.bufferedReader()
            while (!Thread.interrupted()) {
                if (!bufferedReader.ready()) {
                    Thread.sleep(100)
                    continue
                }
                bufferedReader.readLine().let {
                    debugLog("get event - $it")
                    val eventTexts = it.split(' ')
                    if (eventTexts.size == 4) {
                        val listenerSize = listenerList.beginBroadcast()
                        for (i in 0 until listenerSize) {
                            listenerList.getBroadcastItem(i).onEvent(
                                EventResult(
                                    event = eventTexts[0],
                                    v0 = eventTexts[1],
                                    v1 = eventTexts[2],
                                    v2 = eventTexts[3],
                                )
                            )
                        }
                    }
                    listenerList.finishBroadcast()
                }
            }
        } catch (e: Exception) {
            debugLog("mThread: getting interrupt. $e")
        } finally {
            input.close()
            process.destroy()
        }
    }

    override fun isRunning(): Boolean {
        return isRunning
    }

    override fun launch(filter: String?) {
        debugLog("#launch with $filter")
        if (isRunning) return
        synchronized(mLock) {
            if (isRunning) return
            isRunning = true
            mThread = newProviderThread(filter)
            mThread?.start()
        }
    }

    override fun stop() {
        debugLog("#stop")
        if (!isRunning) return
        synchronized(mLock) {
            if (!isRunning) return
            isRunning = false
            debugLog("#stop: interrupt")
            mThread?.interrupt()
            debugLog("#stop: join")
            mThread?.join()
            debugLog("#stop: mThread = null")
            mThread = null
        }
    }

    override fun registerListener(listener: IEventsListener?) {
        debugLog("#registerListener")
        if (listener == null) return
        listenerList.register(listener)
    }

    override fun unregisterListener(listener: IEventsListener?) {
        debugLog("#unregisterListener")
        if (listener == null) return
        listenerList.unregister(listener)
    }

    override fun destroy() {
        debugLog("#destroy")
        stop()
        listenerList.kill()
        exitProcess(0)
    }
}
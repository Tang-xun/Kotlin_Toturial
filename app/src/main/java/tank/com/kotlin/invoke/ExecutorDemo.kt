package tank.com.kotlin.invoke

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

class ExecutorDemo {

    companion object {
        const val TAG = "ExecutorDemo"
    }

    init {
        val executor:Executor? = Executor {
            it.run()
        }
    }
}
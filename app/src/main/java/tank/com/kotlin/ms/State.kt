package tank.com.kotlin.ms

import android.os.Message
import android.util.Log

/**
 *  @author: vancetang
 *  @date:   2019/5/3 10:14 PM
 *
 *  StateMachine base state class
 *
 */
open class State : IState {
    override fun enter() {
        Log.i(TAG, "${getName()} -> enter ::: ")
    }

    override fun exit() {
        Log.i(TAG, "${getName()} -> exit ::: ")
    }

    override fun processMessage(msg: Message?): Boolean? {
        Log.i(TAG, "${getName()} -> processMessage ::: $msg")
        return true
    }

    override fun getName(): String? {
        val name = javaClass.name
        val lastDollar = name.lastIndexOf('$')
        return name?.substring(lastDollar) ?: ""
    }

    companion object {

        const val TAG = "State"

    }
}

package tank.com.kotlin.ms

import android.os.Message

/**
 *  @author: vancetang
 *  @date:   2019/5/3 10:13 PM
 */
interface IState {

    fun enter()

    fun exit()

    fun processMessage(msg: Message?): Boolean?

    fun getName():String?

}
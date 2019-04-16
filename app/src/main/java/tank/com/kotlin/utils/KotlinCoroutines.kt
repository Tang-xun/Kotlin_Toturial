package tank.com.kotlin.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *  @author: vancetang
 *  @date:   2019/3/30 8:02 AM
 */
class KotlinCoroutines {

    fun main(args: Array<String>?) {
        repeat(100000) { i ->
            GlobalScope.launch {
                suspendPrint(i)
            }
        }
    }

    private suspend fun suspendPrint(i: Int) {
        delay(1000)
        print(i)
    }

}
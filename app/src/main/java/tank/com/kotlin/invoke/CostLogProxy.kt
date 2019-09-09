package tank.com.kotlin.invoke

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class CostLogProxy<in T>(obj: T) : InvocationHandler {

    private var subject: T? = obj

    override fun invoke(obj: Any?, method: Method?, args: Array<out Any>?): Any? {
        return method?.invoke(obj, args)
    }

    companion object {

    }
}
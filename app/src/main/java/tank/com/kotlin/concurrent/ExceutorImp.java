package tank.com.kotlin.concurrent;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExceutorImp {
    public static ThreadPoolExecutor singleThreadPool(){
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
    }
}

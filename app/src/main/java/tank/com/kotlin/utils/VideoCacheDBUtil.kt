package tank.com.kotlin.utils

import com.litesuits.orm.LiteOrm
import com.litesuits.orm.db.assit.QueryBuilder
import tank.com.kotlin.MainApplication
import tank.com.kotlin.model.VideoCacheBean

class VideoCacheDBUtil {

    companion object {
        private val liteOrmDb: LiteOrm = LiteOrm.newSingleInstance(MainApplication.mainApplication, "VideoCacheDB")

        public fun save(bean: VideoCacheBean?) {
            liteOrmDb.save(bean)
        }

        public fun delete(bean: VideoCacheBean?) {
            liteOrmDb.delete(bean)
        }

        public fun query(key: String): VideoCacheBean? {
            val list = liteOrmDb.query(QueryBuilder<VideoCacheBean>(VideoCacheBean::class.java).where(VideoCacheBean.KEY + "=?", key))
            if (list.isEmpty()) {
                return null
            } else {
                return list[0]
            }
        }

        public fun query(): ArrayList<VideoCacheBean>? {
            return liteOrmDb.query(QueryBuilder<VideoCacheBean>(VideoCacheBean::class.java).appendOrderDescBy(VideoCacheBean.PLAY_TIME))
        }

    }

}
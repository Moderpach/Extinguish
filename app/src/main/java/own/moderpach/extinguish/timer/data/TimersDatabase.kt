package own.moderpach.extinguish.timer.data

import android.content.Context
import androidx.room.*

@Database(
    entities = [Timer::class],
    version = 1,
    exportSchema = true
)
abstract class TimersDatabase : RoomDatabase() {
    abstract fun timersDao(): TimersDao

    companion object {
        private const val NAME = "preset_timers"

        @Volatile
        private var INSTANCE: TimersDatabase? = null

        fun get(context: Context): TimersDatabase {
            if (INSTANCE != null) {
                return INSTANCE!!
            }
            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    TimersDatabase::class.java,
                    NAME
                ).fallbackToDestructiveMigration().build()
                return INSTANCE!!
            }
        }
    }
}

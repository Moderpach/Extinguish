package own.moderpach.extinguish.timer.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * #Timer Data Access Object
 * */
@Dao
interface TimersDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(timer: Timer)

    @Query("SELECT * FROM timers")
    fun readAll(): Flow<List<Timer>>

    @Delete
    suspend fun delete(timer: Timer)

}
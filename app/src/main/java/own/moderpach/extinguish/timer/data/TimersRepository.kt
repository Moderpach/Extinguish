package own.moderpach.extinguish.timer.data

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

fun LifecycleOwner.timersRepository(
    timersDao: TimersDao
) = TimersRepository(timersDao, this.lifecycleScope)

/**
 * Access Timer Database
 * 1|val timerDao = TimerDatabase.getDatabase(applicationContext).timerDao()
 * 2|val timerRepo = TimerRepo(timerDao)
 * 3|timerRepo.readAll()
 * */

class TimersRepository(
    private val timersDao: TimersDao,
    private val coroutineScope: CoroutineScope
) : ITimersRepository {

    override fun readAll() = timersDao.readAll().map { list ->
        list.map { TimerLiteral.fromSeconds(it.second) }
    }

    override fun insert(timer: TimerLiteral) {
        coroutineScope.launch(Dispatchers.IO) {
            timersDao.insert(Timer(timer.inSeconds()))
        }
    }

    override fun delete(timer: TimerLiteral) {
        coroutineScope.launch(Dispatchers.IO) {
            timersDao.delete(Timer(timer.inSeconds()))
        }
    }

}
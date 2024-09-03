package own.moderpach.extinguish.timer.data

import kotlinx.coroutines.flow.Flow

interface ITimersRepository {
    fun readAll(): Flow<List<TimerLiteral>>
    fun insert(timer: TimerLiteral)
    fun delete(timer: TimerLiteral)
}
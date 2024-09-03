package own.moderpach.extinguish.timer.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import own.moderpach.extinguish.timer.data.ITimersRepository
import own.moderpach.extinguish.timer.data.TimerLiteral

class FakeTimersRepository : ITimersRepository {

    private val timers = MutableStateFlow(
        listOf(
            345, 647, 4643
        )
    )

    override fun readAll(): Flow<List<TimerLiteral>> = timers.map {
        it.map {
            TimerLiteral.fromSeconds(it)
        }
    }

    override fun insert(timer: TimerLiteral) {
        timers.update {
            it.toMutableList().apply {
                add(timer.inSeconds())
            }.toList()
        }
    }

    override fun delete(timer: TimerLiteral) {
        timers.update {
            it.filter { it != timer.inSeconds() }
        }
    }
}
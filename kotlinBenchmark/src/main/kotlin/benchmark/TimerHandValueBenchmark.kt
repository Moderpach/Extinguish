package benchmark

import org.openjdk.jmh.annotations.*
import source.DigitalTimerValue
import source.InputtedTime
import source.MechanicalTimerValue
import source.TimerHandValue
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
class TimerHandValueCreateBenchmark {

    val v = 362439

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun createBenchmark1() {
        InputtedTime.createByInt(v)
        InputtedTime.createByInt(v)
        InputtedTime.createByInt(v)
        InputtedTime.createByInt(v)
        InputtedTime.createByInt(v)
        InputtedTime.createByInt(v)
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun createBenchmark2() {
        TimerHandValue.fromSecond(v.toUInt())
        TimerHandValue.fromSecond(v.toUInt())
        TimerHandValue.fromSecond(v.toUInt())
        TimerHandValue.fromSecond(v.toUInt())
        TimerHandValue.fromSecond(v.toUInt())
        TimerHandValue.fromSecond(v.toUInt())
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun createBenchmark3() {
        MechanicalTimerValue.from(v.toUInt())
        MechanicalTimerValue.from(v.toUInt())
        MechanicalTimerValue.from(v.toUInt())
        MechanicalTimerValue.from(v.toUInt())
        MechanicalTimerValue.from(v.toUInt())
        MechanicalTimerValue.from(v.toUInt())
    }
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
class TimerHandValuePushBenchmark {
    var inputtedTime: InputtedTime = InputtedTime.zero()
    var timerHandValue: TimerHandValue = TimerHandValue(0u)
    var timerValue = DigitalTimerValue.zero()

    @Setup
    fun setUp() {
        inputtedTime = InputtedTime.zero()
        timerHandValue = TimerHandValue(0u)
        timerValue = DigitalTimerValue.zero()
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun pushBenchmark1() {
        inputtedTime.pushLast(1u)
        inputtedTime.pushLast(5u)
        inputtedTime.pushLast(1u)
        inputtedTime.pushLast(5u)
        inputtedTime.pushLast(1u)
        inputtedTime.pushLast(8u)
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun pushBenchmark2() {
        timerHandValue
            .push(1u)
            .push(3u)
            .push(1u)
            .push(4u)
            .push(6u)
            .push(5u)
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun pushBenchmark3() {
        timerValue
            .push(1u)
            .push(3u)
            .push(1u)
            .push(4u)
            .push(6u)
            .push(5u)
    }
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
class TimerHandValuePopBenchmark {
    var inputtedTime: InputtedTime = InputtedTime.zero()
    var timerHandValue: TimerHandValue = TimerHandValue(0u)
    var timerValue = DigitalTimerValue.zero()

    @Setup
    fun setUp() {
        inputtedTime = InputtedTime.createByInt(99 * 60 * 60 + 99 * 60 + 99)
        timerHandValue = TimerHandValue(99u * 60u * 60u + 99u * 60u + 99u)
        timerValue = DigitalTimerValue.from(99u * 60u * 60u + 99u * 60u + 99u)
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun popBenchmark1() {
        inputtedTime.popLast()
        inputtedTime.popLast()
        inputtedTime.popLast()
        inputtedTime.popLast()
        inputtedTime.popLast()
        inputtedTime.popLast()
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun popBenchmark2() {
        timerHandValue
            .pop()
            .pop()
            .pop()
            .pop()
            .pop()
            .pop()
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun popBenchmark3() {
        timerValue
            .pop()
            .pop()
            .pop()
            .pop()
            .pop()
            .pop()
    }
}

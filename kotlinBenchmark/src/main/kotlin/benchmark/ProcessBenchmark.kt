/*
package benchmark

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import source.DigitalTimerValue
import source.InputtedTime
import source.TimerHandValue
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 50, time = 200, timeUnit = TimeUnit.MILLISECONDS)
class OneTimeUseProcessBenchmark {
    
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun runVersion() {
        val process = Runtime.getRuntime().exec("cjxl -V")
        process.inputStream.reader().readText()
        process.inputStream.close()
        process.destroy()
    }
}

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 50, time = 200, timeUnit = TimeUnit.MILLISECONDS)
class ReuseProcessBenchmark {
    
    val process: Process = Runtime.getRuntime().exec("cjxl -V")
    
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    fun runVersion() {
        process.inputStream.reader().readText()
    }
    
}*/

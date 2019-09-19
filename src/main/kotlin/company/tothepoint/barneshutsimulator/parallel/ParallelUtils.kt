package company.tothepoint.barneshutsimulator.parallel

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.ForkJoinWorkerThread
import java.util.concurrent.RecursiveTask

object ParallelUtils {

    val forkjoinPool = ForkJoinPool(4)

    abstract class TaskScheduler {
        abstract fun <T> schedule(body: () -> T): ForkJoinTask<T>

        fun <A, B> parallel(taskA: () -> A, taskB: () -> B): Pair<A, B> {
            val right = task(taskB)
            val left = taskA()
            return Pair(left, right.join())

        }
    }

    class DefaultTaskScheduler : TaskScheduler() {
        override fun <T> schedule(body: () -> T): ForkJoinTask<T> {
            val t = object : RecursiveTask<T>() {
                override fun compute(): T = body()
            }
            when (Thread.currentThread()) {
                is ForkJoinWorkerThread -> t.fork()
                else -> forkjoinPool.execute(t)
            }
            return t
        }
    }

    val scheduler = DynamicVariable<TaskScheduler>(DefaultTaskScheduler())


    fun <T> task(body: () -> T) = scheduler.value().schedule(body)

    fun <A, B> parallel(taskA: () -> A, taskB: () -> B) = scheduler.value().parallel(taskA, taskB)
    fun <A, B, C, D> parallel(taskA: () -> A, taskB: () -> B, taskC: () -> C, taskD: () -> D): Quadruple<A, B, C, D> {
        val ta = task(taskA)
        val tb = task(taskB)
        val tc = task(taskC)
        val td = taskD()
        return Quadruple(ta.join(), tb.join(), tc.join(), td)
    }
}

data class Quadruple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
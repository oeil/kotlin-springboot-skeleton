package org.teknux.webapp.util

import java.util.concurrent.TimeUnit

class StopWatch {

    private var start: Long = 0
    private var elapsedNanoseconds: Long = 0

    fun start(): StopWatch {
        start = System.nanoTime()
        return this
    }

    inline fun <R> time(f: () -> R): R {
        start()
        try {
            return f()
        } finally {
            stop()
        }
    }

    fun stop(): StopWatch {
        val now = System.nanoTime()
        if (start == 0L) start = now

        elapsedNanoseconds += now - start
        return this
    }

    fun reset(): StopWatch {
        start = 0
        elapsedNanoseconds = 0
        return this
    }

    fun elapsed(unit: TimeUnit): Long {
        if (start == 0L) stop()
        return unit.convert(elapsedNanoseconds, TimeUnit.NANOSECONDS)
    }
}
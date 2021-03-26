package com.popalay.barnee.util

import kotlinx.coroutines.Job
import kotlin.jvm.Synchronized

class ConflatedJob {
    private var job: Job? = null
    private var prevJob: Job? = null

    val isActive get() = job?.isActive == true

    @Synchronized
    operator fun plusAssign(newJob: Job) {
        cancel()
        job = newJob
    }

    fun cancel() {
        job?.cancel()
        prevJob = job
    }

    fun start() {
        job?.start()
    }
}
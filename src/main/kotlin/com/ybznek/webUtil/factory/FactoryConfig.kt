package com.ybznek.webUtil.factory

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

open class FactoryConfig(
    val headless: Boolean = false,
    val duration: Duration = 10.seconds
)
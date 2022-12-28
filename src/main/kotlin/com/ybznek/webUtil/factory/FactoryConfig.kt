package com.ybznek.webUtil.factory

import java.time.Duration

open class FactoryConfig(
    val headless: Boolean = false,
    val duration: Duration = Duration.ofSeconds(10)
)
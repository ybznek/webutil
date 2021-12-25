package com.ybznek.webUtil

import java.time.Duration

open class FactoryConfig(
    val headless: Boolean = false,
    val duration: Duration = Duration.ofSeconds(10)
)
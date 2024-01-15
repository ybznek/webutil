package com.ybznek.webUtil.factory

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ChromeFactoryConfig(
    headless: Boolean = false,
    duration: Duration = 10.seconds,
    val userAgent: String? = null
) : FactoryConfig(headless, duration)
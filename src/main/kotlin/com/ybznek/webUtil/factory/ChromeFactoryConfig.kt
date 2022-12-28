package com.ybznek.webUtil.factory

import java.time.Duration

class ChromeFactoryConfig(
    headless: Boolean = false,
    duration: Duration = Duration.ofSeconds(10),
    val userAgent: String? = null
) : FactoryConfig(headless, duration)
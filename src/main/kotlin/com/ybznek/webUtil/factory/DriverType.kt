package com.ybznek.webUtil.factory

internal enum class DriverType(internal val driverName: String, internal val property: String, internal val defaultPath: String) {
    Chromium("chromedriver", "webdriver.chrome.driver", "/usr/bin/chromedriver"),
}

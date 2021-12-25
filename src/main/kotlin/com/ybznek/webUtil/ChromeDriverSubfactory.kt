package com.ybznek.webUtil

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

internal object ChromiumDriverSubFactory : BaseFactory<ChromeFactoryConfig, ChromeOptions, ChromeDriver>() {

    private val defaultChromePath by lazy { findExecutable(DriverType.Chromium.driverName) }

    override fun createStandardDriver(config: ChromeFactoryConfig): ChromeDriver {
        if (defaultChromePath != null) {
            System.setProperty(DriverType.Chromium.driverName, DriverType.Chromium.defaultPath);
        }

        val chromeOptions = ChromeOptions()
        initOptions(chromeOptions, config)
        return ChromeDriver(chromeOptions)
    }

    override fun initOptions(chromeOptions: ChromeOptions, config: ChromeFactoryConfig) {
        if (config.headless) {
            chromeOptions.addArguments("--headless")
        }
        config.userAgent?.let { it -> chromeOptions.addArguments("--user-agent=$it") }
    }
}

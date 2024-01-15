package com.ybznek.webUtil.factory

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

internal object ChromiumDriverSubFactory : BaseFactory<ChromeFactoryConfig, ChromeOptions, ChromeDriver>() {

    private val defaultChromePath by lazy { findExecutable(DriverType.Chromium.driverName) }
    override fun createStandardDriver(config: ChromeFactoryConfig): ChromeDriver {
        WebDriverManager.chromedriver().setup()
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

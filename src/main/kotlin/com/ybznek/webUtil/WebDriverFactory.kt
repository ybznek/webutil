package com.ybznek.webUtil

import org.openqa.selenium.chrome.ChromeDriver

object WebDriverFactory {

    fun createChromium(config: ChromeFactoryConfig = ChromeFactoryConfig()): EnhancedDriver<ChromeDriver> {
        return ChromiumDriverSubFactory.createEnhanced(config)
    }

}
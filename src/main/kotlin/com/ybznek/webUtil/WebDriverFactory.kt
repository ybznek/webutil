package com.ybznek.webUtil

import com.ybznek.webUtil.factory.ChromeFactoryConfig
import com.ybznek.webUtil.factory.ChromiumDriverSubFactory
import com.ybznek.webUtil.factory.EnhancedDriver
import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.ChromeDriver

object WebDriverFactory {

    fun createChromium(config: ChromeFactoryConfig = ChromeFactoryConfig()): EnhancedDriver<ChromeDriver> {
        return ChromiumDriverSubFactory.createEnhanced(config)
    }

}
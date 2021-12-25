package com.ybznek.webUtil

import org.openqa.selenium.WebDriver

abstract class BaseFactory<TConfig : FactoryConfig, TOpt : Any, TDriver : WebDriver> {
    protected abstract fun initOptions(chromeOptions: TOpt, config: TConfig);


    fun createEnhanced(config: TConfig): EnhancedDriver<TDriver> {
        val driver = createStandardDriver(config)
        return EnhancedDriver(driver = driver, defaultDuration = config.duration)
    }

    abstract fun createStandardDriver(config: TConfig): TDriver

    internal fun findExecutable(driverName: String): String? {
        return try {
            val proc = ProcessBuilder().command("which", driverName).start()
            val line = proc.inputStream.bufferedReader().use {
                it.readLine()
            }
            proc.destroy()
            proc.waitFor()
            line
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
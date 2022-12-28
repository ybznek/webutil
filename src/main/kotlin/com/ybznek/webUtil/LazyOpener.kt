package com.ybznek.webUtil

import org.openqa.selenium.WebDriver
import java.io.Closeable


class LazyOpener<T : WebDriver>(driverProvider: () -> T) : Closeable {
    private var lazyDriver = lazy(driverProvider)

    val driver: T
        get() = lazyDriver.value;

    override fun close() {
        if (lazyDriver.isInitialized()) {
            lazyDriver.value.close()
        }
    }
}
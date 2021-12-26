package com.ybznek.webUtil

import org.openqa.selenium.By
import java.io.File
import java.lang.Thread.sleep

class FileWatcher(val root: File) {
    fun getCurrentFiles(): Set<String> {
        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return root.list { _: File, name: String -> name.endsWith(".crdownload") }.toSet()
    }

    fun getFileForFilename(name: String): File {
        return root.resolve(name)
    }

    internal fun getNewFile(before: Set<String>): File {
        return getFileForFilename(getNewFilename(before))
    }

    internal fun getNewFilename(before: Set<String>): String {
        for (i in 0 until 10) {
            println("$i")
            val newFiles = getCurrentFiles()
            val difference = newFiles - before
            if (difference.size == 1) {
                return difference.first()

            } else if (difference.size > 1) {
                throw IllegalStateException("Multiple inprogress files")
            }
            sleep(1000)
        }
        throw IllegalStateException("NO inprogress files")
    }

    fun waitUntilRemoved(f: File) {
        while (true) {
            if (!f.exists()) {
                break
            }
            Thread.sleep(5000)
        }
    }
}

fun main() {
    LazyOpener { WebDriverFactory.createChromium(ChromeFactoryConfig(headless = false, userAgent = "test" + System.currentTimeMillis())) }.use { lazyOpener ->
        lazyOpener.driver.get("https://fastshare.cz/10615476/zakon-gangu-s02e09-neco-za-neco-cz-dabing-sons-of-anarchy.avi#modal-free")
        lazyOpener.driver.waitForDomReady()

        val watcher = FileWatcher(File("/home/z/Downloads"))
        val before = watcher.getCurrentFiles()
        lazyOpener.driver.apply {
            this.driver.findElement(By.cssSelector(".speed-low a")).javascriptClick()
            val newFile = watcher.getNewFile(before)
            println(newFile)
            watcher.waitUntilRemoved(newFile)
        }

        readLine()
    }
}




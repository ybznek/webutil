package com.ybznek.webUtil

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.Closeable
import java.time.Duration

class EnhancedDriver<T : WebDriver>(val driver: T, private val defaultDuration: Duration = Duration.ofSeconds(3)) : Closeable, WebDriver by driver {

    val javascript = (driver as JavascriptExecutor)
    val defaultWait: WebDriverWait = WebDriverWait(driver, defaultDuration)

    fun getWait(duration: Duration): WebDriverWait {
        return if (defaultDuration != duration) {
            WebDriverWait(driver, duration)
        } else {
            defaultWait
        }
    }

    fun waitForDomReady(duration: Duration = defaultDuration) {
        getWait(duration).waitUntil { javascript.executeScript("return document.readyState") == "complete" }
    }

    fun scrollToEnd() {
        javascript.executeScript("window.scrollTo(0, document.body.scrollHeight)")
    }

    val pageDocument: Document
        get() = Jsoup.parse(driver.pageSource)

    fun WebElement.waitAndClick(javascriptClick: Boolean = true, duration: Duration = defaultDuration) {
        getWait(duration).waitUntil(ExpectedConditions.elementToBeClickable(this))
        if (javascriptClick) {
            this.javascriptClick()
        } else {
            this.click()
        }
    }


    fun By.waitAndClick(javascriptClick: Boolean = false, duration: Duration = defaultDuration) {
        waitForPresent(this)
        getWait(duration).waitUntil(ExpectedConditions.elementToBeClickable(this))
        val targetElement = driver.findElement(this)
        if (javascriptClick) {
            targetElement.javascriptClick()
        } else {
            targetElement.click()
        }
    }

    fun By.enterToField(text: String) {
        waitForPresent(this)
        this.findElement().sendKeys(text)
    }

    fun WebElement.javascriptClick() {
        javascript.executeScript("arguments[0].click();", this)
    }

    fun WebElement.waitForTextPresent(text: String, duration: Duration = defaultDuration) {
        getWait(duration).waitUntil(ExpectedConditions.textToBePresentInElement(this, text))
    }

    @JvmName("findElementEnhanced")
    fun By.findElement(): WebElement {
        return driver.findElement(this)
    }

    fun waitForPresent(by: By, duration: Duration = defaultDuration) {
        getWait(duration).waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(by, 0))
    }

    fun waitForNotPresent(by: By, duration: Duration = defaultDuration) {
        getWait(duration).waitUntil(ExpectedConditions.numberOfElementsToBe(by, 0))
    }

    fun waitForElementsEqualOrMoreThan(sitesManagementRowsSelector: By?, cnt: Int, duration: Duration = defaultDuration) {
        getWait(duration).waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(sitesManagementRowsSelector, cnt))
    }

    fun WebDriverWait.waitUntil(el: ExpectedCondition<*>) {
        this.until(el::apply) // I do not know why is this incompatible as it was not
    }
}



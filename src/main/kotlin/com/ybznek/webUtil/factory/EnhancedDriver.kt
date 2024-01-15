package com.ybznek.webUtil.factory

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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class EnhancedDriver<T : WebDriver>(val driver: T, private val defaultDuration: Duration = 3.seconds) : Closeable,
    WebDriver by driver {

    val javascript = (driver as JavascriptExecutor)
    val defaultWait: WebDriverWait = WebDriverWait(driver, defaultDuration.toJavaDuration())

    fun getWait(duration: Duration): WebDriverWait {
        return if (defaultDuration != duration) {
            WebDriverWait(driver, duration.toJavaDuration())
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

    fun findElement(by: By, duration: Duration): WebElement {
        waitForPresent(by, duration)
        return findElement(by)
    }

    fun findElements(by: By, duration: Duration): List<WebElement> {
        waitForPresent(by, duration)
        return findElements(by)
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

    var WebElement.innerHTML: String
        get() = getAttribute("innerHTML")
        set(value) = setAttribute("innerHTML", value)

    var WebElement.style: String
        get() = getAttribute("style")
        set(value) = setAttribute("style", value)


    val WebElement.classNames get() :List<String> = getAttribute("class").split(" ")


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

    fun WebElement.setAttribute(name: String, value: String) {
        javascript.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", this, name, value);
    }

    fun WebElement.waitForTextPresent(text: String, duration: Duration = defaultDuration) {
        getWait(duration).waitUntil(ExpectedConditions.textToBePresentInElement(this, text))
    }

    @JvmName("findElementEnhanced")
    fun By.findElement(): WebElement {
        return driver.findElement(this)
    }

    fun waitForPresent(element: WebElement, selector: By): ExpectedCondition<List<WebElement>> {
        return object : ExpectedCondition<List<WebElement>> {
            override fun apply(input: WebDriver): List<WebElement>? {
                val elements = element.findElements(selector)
                return if (elements.size > 0) {
                    elements
                } else {
                    null
                }
            }

            override fun toString() = "Find $selector in $element"
        }
    }

    fun waitForPresent(by: By, duration: Duration = defaultDuration) {
        getWait(duration).waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(by, 0))
    }

    fun waitForNotPresent(by: By, duration: Duration = defaultDuration) {
        getWait(duration).waitUntil(ExpectedConditions.numberOfElementsToBe(by, 0))
    }

    fun waitForElementsEqualOrMoreThan(
        sitesManagementRowsSelector: By?,
        cnt: Int,
        duration: Duration = defaultDuration
    ) {
        getWait(duration).waitUntil(ExpectedConditions.numberOfElementsToBeMoreThan(sitesManagementRowsSelector, cnt))
    }

    fun <T : Any> WebDriverWait.waitUntil(el: ExpectedCondition<T>): T {
        return this.until(el::apply) // I do not know why is this incompatible as it was not
    }
}



package com.ybznek.webUtil.factory

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

val WebElement.parent get() = this.findElement(By.xpath("./.."))
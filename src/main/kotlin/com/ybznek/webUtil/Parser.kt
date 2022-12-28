package com.ybznek.webUtil

import com.jayway.jsonpath.JsonPath
import org.jsoup.Jsoup

object Parser {
    fun jsoup(html: String) = Jsoup.parse(html)
    fun jsonPath(json: String) = JsonPath.parse(json)
}
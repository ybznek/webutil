package com.ybznek.webUtil

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File


fun <T> Map<*, *>.getVal(vararg key: String): T {
    return getValNullable(*key)!!
}

fun <T> Map<*, *>.getValNullable(vararg key: String): T? {
    var node: Any? = this

    for (i in key.indices) {
        if (node is Map<*, *>) {
            node = node[key[i]]
        } else {
            return null
        }
    }
    return node as T?
}

class CategoryPage(html: String) {
    val json = ObjectMapper().readValue(html, Map::class.java)
    val resultCount: Int
        get() = json.getVal("resultCount")

    val items: List<CategoryPageProductLink>
        get() = json.getVal<List<Map<*, *>>>("mods", "itemList", "content").map(::CategoryPageProductLink)
}

class CategoryPageProductLink(private val map: Map<*, *>) {
    val title: String
        get() = map.getVal("title", "displayTitle")

    val productId: Long
        get() = map.getVal<Number>("productId").toLong()

    val productUrl: String
        get() = "https://www.aliexpress.com/item/${productId}.html"
}

class ProductPage(html: String, val url: String) {
    private val parsed = ObjectMapper().readValue(html.split('\n').first { x -> x.contains("data: {") }.split(':', limit = 2)[1].trim(','), Map::class.java)
    val properties: List<Property>
        get() = (parsed.getValNullable<List<Map<*, *>>>("skuModule", "productSKUPropertyList") ?: emptyList()).map(::Property)

    val title: String
        get() = parsed.getVal("titleModule", "subject")

    val priceList: List<Price>
        get() = parsed.getVal<List<Map<*, *>>>("skuModule", "skuPriceList").map(::Price)

    val priceMap: Map<String, Double>
        get() {
            return priceList.associateByTo(
                destination = HashMap(priceList.size),
                keySelector = { x -> x.catId },
                valueTransform = { x -> x.amount }
            )
        }


    fun recursivePropertiesWithPrice(cb: (Array<PropertyValue>, price: Double) -> Unit) {
        recursivePropertiesWithPrice(this, cb)
    }

    private fun recursivePropertiesWithPrice(page: ProductPage, cb: (Array<PropertyValue>, price: Double) -> Unit) {
        val prices = page.priceMap
        recursiveProperties(page.properties) { propertyValues ->
            val price = prices[PropertyValue.ids(*propertyValues)]!!
            cb(propertyValues, price)
        }
    }

    fun recursiveProperties(cb: (Array<PropertyValue>) -> Unit) {
        recursiveProperties(this.properties, cb)
    }

    private fun recursiveProperties(propertyList: List<Property>, cb: (Array<PropertyValue>) -> Unit) {
        if (propertyList.isEmpty()) {
            cb(emptyArray())
            return
        }
        @Suppress("UNCHECKED_CAST")
        val argArray = arrayOfNulls<PropertyValue>(propertyList.size) as Array<PropertyValue>

        fun recursivePropertiesInternal(index: Int) {
            if (index < propertyList.size) {
                for (p in propertyList[index].values) {
                    argArray[index] = p
                    recursivePropertiesInternal(index + 1)
                }
            } else {
                cb(argArray)
            }
        }

        return recursivePropertiesInternal(0)
    }

}

class Price(val data: Map<*, *>) {
    val attr: String
        get() = data.getVal("skuAttr")
    val amount: Double
        get() = data.getVal("skuVal", "skuAmount", "value")

    val catId: String
        get() {
            if (attr == "")
                return ""
            return attr
                .split(';').joinToString(":") { x ->
                    x.split('#')[0].split(':')[1]
                }
        }
}

class Property(private val data: Map<*, *>) {
    val name: String
        get() = data.getVal("skuPropertyName")

    val values: List<PropertyValue>
        get() = data.getVal<List<Map<*, *>>>("skuPropertyValues").map(::PropertyValue)
}

class PropertyValue(private val data: Map<*, *>) {
    val propertyValueDefinitionName: String?
        get() = data.getValNullable("propertyValueDefinitionName")

    val propertyValueDisplayName: String
        get() = data.getVal("propertyValueDisplayName")

    val propertyValueId: Int
        get() = data.getVal<Int>("propertyValueId").toInt()

    val propertyValueIdLong: Long
        get() = data.getVal<Number>("propertyValueIdLong").toLong()

    val skuPropertyTips: String
        get() = data.getVal("skuPropertyTips")

    val skuPropertyValueShowOrder: Int
        get() = data.getVal("skuPropertyValueShowOrder")

    val skuPropertyValueTips: String
        get() = data.getVal("skuPropertyValueTips")

    companion object {
        fun ids(vararg value: PropertyValue): String {
            return ids(listOf(*value))
        }

        fun ids(value: List<PropertyValue>): String {
            return value.joinToString(separator = ":") { x -> x.propertyValueId.toString() }
        }
    }
}


fun main() {

    val f1 = File("/t/all")
    Downloader.downloadIfMissing(f1) { f ->
        val t = Downloader.downloadAsText("https://www.aliexpress.com/af/zigbee-switch.html?d=y&origin=n&SearchText=zigbee+switch&catId=0&initiative_id=SB_20220101135604")
        f1.writeText(t)
    }

    val categoryPage = CategoryPage(f1.readText().split('\n').first { x -> x.contains("window.runParams = {\"") }.split('=', limit = 2)[1].trim(';', ' '))

    println(categoryPage.resultCount)
    for (i in categoryPage.items) {
        println("${i.title} ${i.productUrl}")
    }


    fun down(p: CategoryPageProductLink): ProductPage {
        val singlePage = File("/t/${p.productId}.html")
        Downloader.downloadIfMissing(singlePage) { f ->
            val t = Downloader.downloadAsText(p.productUrl)
            singlePage.writeText(t)
        }

        return ProductPage(
            url = p.productUrl,
            html = singlePage.readText()
        )
    }


    val results = ArrayList<String>()
    for (item in categoryPage.items) {
        val productPage = down(item)
        productPage.recursivePropertiesWithPrice { props, price ->
            val name = props.withIndex().joinToString(separator = " ") { x -> productPage.properties[x.index].name + ":" + x.value.propertyValueDisplayName }
            results.add("${productPage.url} ${productPage.title} $name $price")
        }
    }

    results.forEach { line ->
        println(line)
        val l = line.lowercase()
        l.contains("2mm")
    }


    val debug = 42
}
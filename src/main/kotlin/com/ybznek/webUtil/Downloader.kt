package com.ybznek.webUtil

import org.jsoup.Jsoup
import org.jsoup.helper.HttpConnection
import java.io.File
import java.net.URI

object Downloader {
    fun <T> downloadIfMissing(f: File, write: (File) -> T): T? {
        if (!f.exists()) {
            write(f)
        }
        return null
    }

    fun <T> downloadIfMissing(f: File, write: (File) -> T, read: (File) -> T): T {
        return downloadIfMissing(f, write) ?: read(f)
    }

    fun downloadAsText(url: String): String {
        val connect = HttpConnection.connect(url)
        connect.execute()
        return connect.response().body()
    }
}
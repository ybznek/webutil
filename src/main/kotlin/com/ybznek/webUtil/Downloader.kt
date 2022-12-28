package com.ybznek.webUtil

import org.jsoup.helper.HttpConnection
import java.io.File

object Downloader {
    fun <T> downloadIfMissing(f: File, downloadFileAndReturnContent: (File) -> T): T? {
        if (!f.exists()) {
            downloadFileAndReturnContent(f)
        }
        return null
    }

    fun <T> downloadIfMissing(cacheFile: File, downloadFileAndReturnContent: (File) -> T, read: (File) -> T): T {
        return downloadIfMissing(cacheFile, downloadFileAndReturnContent) ?: read(cacheFile)
    }

    fun downloadAsText(url: String): String {
        val connect = HttpConnection.connect(url)
        connect.execute()
        return connect.response().body()
    }
}
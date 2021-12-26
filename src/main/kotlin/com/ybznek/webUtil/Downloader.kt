package com.ybznek.webUtil

import java.io.File

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
}
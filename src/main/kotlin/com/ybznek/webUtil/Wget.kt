package com.ybznek.webUtil

object Wget {
    fun download(from: String, file: String): Int {
        return ProcessBuilder("wget", from, "-O", file).inheritIO().start().waitFor()
    }

}
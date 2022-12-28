package com.ybznek.webUtil

object Curl {
    fun download(from: String, file: String, headers: List<String> = emptyList()): Int {
        val cmd = arrayListOf<String>()
        cmd += "curl"
        cmd += from
        cmd += "-o"
        cmd += file
        for (header in headers) {
            cmd += "-H"
            cmd += header
        }
        cmd += "--compressed"
        return ProcessBuilder(cmd).inheritIO().start().waitFor()
    }
}
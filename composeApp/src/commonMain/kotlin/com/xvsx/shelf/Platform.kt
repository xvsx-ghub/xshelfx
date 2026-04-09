package com.xvsx.shelf

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
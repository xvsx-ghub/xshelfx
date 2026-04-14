package com.xvsx.shelf.data.remote.http

import io.ktor.client.HttpClient

expect class HttpClientFactory() {
    fun create(): HttpClient
}
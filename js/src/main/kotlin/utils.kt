@file:Suppress("unused")

import org.w3c.fetch.RequestInit
import kotlin.browser.window

fun args(params: Map<String, Any>) = if (params.isNotEmpty()) "?" + params.map { "${it.key}=${it.value}" }.joinToString("&") else ""

fun call(url: String, init: RequestInit? = null) =
    (if (init != null) window.fetch(url, init) else window.fetch(url)).then { it.json() }


fun get(url: String, params: Map<String, Any>) =
    call(url + args(params))


fun post(url: String, body: Any) = call(
    url,
    RequestInit(
        method = "POST",
        headers = mapOf("Content-Type" to "application/json"),
        body = JSON.stringify(body)
    )
)
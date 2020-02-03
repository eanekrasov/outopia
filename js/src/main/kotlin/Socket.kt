import kotlinx.serialization.json.Json
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event
import ru.o4fun.models.Incoming
import kotlin.browser.window
import kotlin.reflect.KClass

class Socket(base: String, params: Map<String, Any> = mapOf()) {
    private var socket : WebSocket? = null
    private val url = "${window.location.protocol.replace("http", "ws")}//${window.location.host}${base}${args(params)}"
    private val eventListeners = mutableMapOf<KClass<out Incoming>, MutableSet<(Incoming) -> Unit>>()
    val connectListeners = mutableSetOf<(Event) -> Unit>()
    val disconnectListeners = mutableSetOf<(Event) -> Unit>()
    val errorListeners = mutableSetOf<(Event) -> Unit>()

    init {
        connect()
    }

    private fun connect() {
        socket = WebSocket(url).apply {
            onopen = { e ->
                connectListeners.forEach { it(e) }
            }
            onmessage = { e ->
                console.log("in", e)
                val json = Json.parse(
                    Incoming.serializer(),
                    e.data.toString()
                )
                eventListener(json::class).forEach { it(json) }
            }
            onclose = { e ->
                disconnectListeners.forEach { it(e) }
                window.setTimeout({
                    connect()
                }, 5000)
            }
            onerror = { err ->
                errorListeners.forEach { it(err) }
            }
        }
    }

    fun send(json: Incoming) = socket?.send(
        Json.stringify(
            Incoming.serializer(),
            json
        )
    )

    fun eventListener(key: KClass<out Incoming>) = eventListeners.getOrPut(key) { mutableSetOf() }

    fun discover(x: Int, y: Int) = send(Incoming.Discover(x, y))

    companion object {
        var socket : Socket? = null
        val callbacks = mutableSetOf<(Socket) -> Unit>()

        fun start(url : String, params: Map<String, Any>) {
            socket = Socket(url, params)
            callbacks.forEach { it(socket!!) }
        }

        fun socket(callback: (Socket) -> Unit) {
            socket?.let {callback(it)} ?: callbacks.add(callback)
        }
    }
}
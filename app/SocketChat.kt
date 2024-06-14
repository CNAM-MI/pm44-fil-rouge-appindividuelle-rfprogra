import android.nfc.Tag
import android.util.Log

class SocketChat(private val username: String) {
    val socket = Socket(
        endpoint = "http://84.235.235.229:3000/",
        config = SocketOptions(
            //queryParams = mapOf("token" to "MySuperToken"),
            transport = SocketOptions.Transport.WEBSOCKET
        )
    ) {
        on(SocketEvent.Connect) {
            println("connect")
        }

        on(SocketEvent.Connecting) {
            println("connecting")
        }

        on(SocketEvent.Disconnect) {
            println("disconnect")
        }

        on(SocketEvent.Error) {
            println("error $it")
        }

        on(SocketEvent.Reconnect) {
            println("reconnect")
        }

        on(SocketEvent.ReconnectAttempt) {
            println("reconnect attempt $it")
        }

        on(SocketEvent.Ping) {
            println("ping")
        }

        on(SocketEvent.Pong) {
            println("pong")
        }

        on("hi") { data ->
            val serializer = DeliveryCar.serializer()
            val json = JSON.nonstrict
            val deliveryCar: DeliveryCar = json.parse(serializer, data)
            println(deliveryCar)
            Log.d(Tag, deliveryCar)
            //...
        }
    }
}
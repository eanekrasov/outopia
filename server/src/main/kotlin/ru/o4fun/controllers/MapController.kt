package ru.o4fun.controllers

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.reactive.result.view.Rendering
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import ru.o4fun.exceptions.NotFoundException
import ru.o4fun.interfaces.Cell
import ru.o4fun.interfaces.CellType
import ru.o4fun.models.Engine
import ru.o4fun.services.OutopiaService
import java.awt.image.BufferedImage
import java.io.File
import java.io.OutputStream
import javax.imageio.ImageIO
import kotlin.math.abs

@Controller
class MapController(
    outopiaService: OutopiaService
) {
    private val engine = outopiaService.engine

    @GetMapping("/map")
    fun map(model: Model) = Rendering.view("map")
        .modelAttribute("world", engine)
        .modelAttribute("width", Engine.width)
        .modelAttribute("height", Engine.height)
        .build()

    @GetMapping("/players/{id}/map")
    fun playerMap(
        @PathVariable("id") id: String,
        model: Model
    ) = Rendering.view("playerMap")
        .modelAttribute("player", engine[id] ?: throw NotFoundException())
        .modelAttribute("width", Engine.width)
        .modelAttribute("height", Engine.height)
        .build()

    @GetMapping("/map/{z}/{x}/{y}", produces = ["image/png"])
    @ResponseBody
    fun tile(
        @PathVariable("z") z: Int,
        @PathVariable("x") x: Int,
        @PathVariable("y") y: Int,
        response: ServerHttpResponse
    ) = writeToServerResponse(response.bufferFactory()) { out ->
        out.cacheIt("$base/cache/map-$z-$x-$y.png") { ImageIO.write(mapTile(z, x, y), "png", it) }
    }


    @GetMapping("/players/{id}/map/{z}/{x}/{y}", produces = ["image/png"])
    @ResponseBody
    fun playerTile(
        @PathVariable("id") id: String,
        @PathVariable("z") z: Int,
        @PathVariable("x") x: Int,
        @PathVariable("y") y: Int,
        response: ServerHttpResponse
    ) = with(engine[id] ?: throw NotFoundException()) {
        writeToServerResponse(response.bufferFactory()) { out2 ->
            out2.cacheIt("$base/cache/map-$z-$x-$y.png") { out -> ImageIO.write(mapTile(z, x, y) { discovered.contains(it) }, "png", out) }
        }
    }

    private fun mapTile(
        z: Int,
        x: Int,
        y: Int,
        check: (Cell) -> Boolean = { true }
    ): BufferedImage {
        if (z !in 1..6) throw NotFoundException()
        val t = 2.times(z)
        val tiles = 256 / t
        val orig = File("$base/earth-moon-$t.png").inputStream().use { ImageIO.read(it) }
        val forestOrig = File("$base/earth-forest-$t.png").inputStream().use { ImageIO.read(it) }
        val forest = (0..4).map { forestOrig.getSubimage(t * it, 0, t, t) }
        val green = (1..3).map { orig.getSubimage(t * it, 0, t, t) }
        val yellow = (4..6).map { orig.getSubimage(t * it, 0, t, t) }
        val border = orig.getSubimage(0, 0, t, t)
        return BufferedImage(256, 256, orig.type).apply {
            createGraphics().apply {
                (0 until tiles).forEach { dy ->
                    (0 until tiles).forEach { dx ->
                        val xx = x * tiles + dx
                        val yy = y * tiles + dy
                        if (xx in (0 until Engine.width) && yy in (0 until Engine.height)) {
                            val cell = engine[xx, yy]
                            if (check(cell)) {
                                drawImage(
                                    when {
                                        xx > yy -> yellow.deepCell(xx, yy)
                                        xx < yy -> green.deepCell(xx, yy)
                                        else -> border
                                    }, dx * t, dy * t, null
                                )
                                if (cell.type == CellType.FOREST) drawImage(forest[(xx + yy) % 5], dx * t, dy * t, null)
                            }
                        }
                    }
                }
            }.dispose()
        }
    }

    private fun writeToServerResponse(bufferFactory: DataBufferFactory, write: (OutputStream) -> Unit) = Flux.create<DataBuffer>({ emitter ->
        emitter.next(bufferFactory.allocateBuffer().apply {
            asOutputStream().use { out ->
                write(out)
                // don't know if flushing is strictly necessary
                out.flush()
            }
        })
        emitter.complete()
    }, FluxSink.OverflowStrategy.BUFFER)

    companion object {
        const val base = "/Users/coder/IdeaProjects/outopia/server/src/main/resources/map"

        fun OutputStream.cacheIt(path: String, save: Boolean = false, draw: (out: OutputStream) -> Unit) {
            if (save) {
                val file = File(path)
                if (!file.exists()) file.outputStream().use(draw)
                file.inputStream().use { it.copyTo(this) }
            } else draw(this)
        }

        fun List<BufferedImage>.deepCell(x: Int, y: Int) =
            if (abs(-x + y - 1) < (Engine.height + Engine.width) / 6) this[0] else if (abs(-x + y - 1) < (Engine.height + Engine.width) / 3) this[1] else this[2]
    }
}

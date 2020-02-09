package ru.o4fun.entities

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import ru.o4fun.enums.SquadUnit
import ru.o4fun.models.Value
import ru.o4fun.repositories.CellRepository
import ru.o4fun.repositories.PlayerRepository

@SpringBootTest
class ServerApplicationTests {

    @Autowired
    lateinit var cellRepo: CellRepository

    @Autowired
    lateinit var playerRepo: PlayerRepository

    private val json = Json(configuration = JsonConfiguration.Default.copy(classDiscriminator = "type"))
    @Test
    fun contextLoads() {
        val player = playerRepo.save(PlayerEntity("name"))

        val key = CellEntity.Key(0, 0)
        cellRepo.save(CellEntity(key, player))
        cellRepo.save(cellRepo.findByIdOrNull(key)!!.apply {
            units[SquadUnit.Infantry] = 1
            discoveredBy.add(player)
            values.add(json.stringify(Value.serializer(), Value.Field(cell.x, cell.y)))
        })

        val loaded = cellRepo.findByIdOrNull(key)!!
        assertEquals(1, loaded.units.count())
        assertEquals(1, loaded.discoveredBy.count())
    }
}

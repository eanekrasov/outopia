package ru.o4fun.entities

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension
import ru.o4fun.enums.SquadUnit
import ru.o4fun.extensions.stringify
import ru.o4fun.models.Value
import ru.o4fun.repositories.CellRepository
import ru.o4fun.repositories.PlayerRepository

@ExtendWith(SpringExtension::class)
@DataJpaTest
class ServerApplicationTests {

    @Autowired
    lateinit var cellRepo: CellRepository

    @Autowired
    lateinit var playerRepo: PlayerRepository

    @Test
    fun contextLoads() {
        val player = playerRepo.save(PlayerEntity("name"))

        val key = CellEntity.Key(0, 0)
        cellRepo.save(CellEntity(key, player))
        cellRepo.save(cellRepo.findByIdOrNull(key)!!.apply {
            units[SquadUnit.Infantry] = 1
            discoveredBy.add(player)
            values.add(Value.Field(cell.x, cell.y).stringify())
        })

        val loaded = cellRepo.findByIdOrNull(key)!!
        assertEquals(1, loaded.units.count())
        assertEquals(1, loaded.discoveredBy.count())
    }
}

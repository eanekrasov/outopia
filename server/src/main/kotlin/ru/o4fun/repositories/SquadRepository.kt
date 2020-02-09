package ru.o4fun.repositories

import org.springframework.data.repository.CrudRepository
import ru.o4fun.entities.SquadEntity

interface SquadRepository : CrudRepository<SquadEntity, Long>
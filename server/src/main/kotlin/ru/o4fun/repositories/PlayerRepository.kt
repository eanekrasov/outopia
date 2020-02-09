package ru.o4fun.repositories

import org.springframework.data.repository.CrudRepository
import ru.o4fun.entities.PlayerEntity

interface PlayerRepository : CrudRepository<PlayerEntity, Long>
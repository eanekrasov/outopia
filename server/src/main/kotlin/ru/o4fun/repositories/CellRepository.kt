package ru.o4fun.repositories

import org.springframework.data.r2dbc.repository.R2dbcRepository
import ru.o4fun.entities.CellEntity

interface CellRepository : R2dbcRepository<CellEntity, Long>
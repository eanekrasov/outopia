package ru.o4fun.repositories

import org.springframework.data.repository.CrudRepository
import ru.o4fun.entities.CellEntity

interface CellRepository : CrudRepository<CellEntity, CellEntity.Key>

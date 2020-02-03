package ru.o4fun.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("cells")
data class CellEntity(
    var payload: String = "",
    @Id
    var id: Long? = null
)
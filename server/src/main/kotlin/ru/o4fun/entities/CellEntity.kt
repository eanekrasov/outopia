package ru.o4fun.entities

import ru.o4fun.CellType
import ru.o4fun.models.SquadUnit
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "cells")
data class CellEntity(
    @EmbeddedId
    var cell: Key,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", nullable = true)
    var owner: PlayerEntity? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "cell_units",
        joinColumns = [JoinColumn(name = "x", referencedColumnName = "x"), JoinColumn(name = "y", referencedColumnName = "y")]
    )
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "value")
    val units: MutableMap<SquadUnit, Long> = mutableMapOf(),

    @Enumerated(EnumType.STRING)
    var type: CellType = CellType.DEFAULT,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "cell_values",
        joinColumns = [JoinColumn(name = "x", referencedColumnName = "x"), JoinColumn(name = "y", referencedColumnName = "y")]
    )
    @Column(name = "value")
    val values: MutableSet<String> = mutableSetOf(),

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "cell_discovered_by",
        joinColumns = [JoinColumn(name = "x", referencedColumnName = "x"), JoinColumn(name = "y", referencedColumnName = "y")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    var discoveredBy: MutableSet<PlayerEntity> = mutableSetOf()
) {
    @Embeddable
    data class Key(var x: Int, var y: Int) : Serializable
}
package ru.o4fun.entities

import kotlinx.serialization.Transient
import ru.o4fun.enums.SquadUnit
import javax.persistence.*

@Entity
@Table(name = "squads")
class SquadEntity(
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", nullable = false)
    var owner: PlayerEntity,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns(JoinColumn(name = "origin_x", referencedColumnName = "x"), JoinColumn(name = "origin_y", referencedColumnName = "y"))
    var origin: CellEntity,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns(JoinColumn(name = "target_x", referencedColumnName = "x"), JoinColumn(name = "target_y", referencedColumnName = "y"))
    var target: CellEntity,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "squad_units", joinColumns = [JoinColumn(name = "squad_id", referencedColumnName = "id")])
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "value")
    val units: MutableMap<SquadUnit, Long>,

    var timeout: Int
) {
    @Id
    @Transient
    var id: Long? = null
}
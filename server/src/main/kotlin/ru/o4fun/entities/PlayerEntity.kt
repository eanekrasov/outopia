package ru.o4fun.entities

import ru.o4fun.Resource
import javax.persistence.*

@Entity
@Table(name = "players")
class PlayerEntity(
    var name: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "player_resources",
        joinColumns = [JoinColumn(name = "player_id", referencedColumnName = "id")]
    )
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "value")
    val resources: MutableMap<Resource, Long> = mutableMapOf(),

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    var owned: MutableSet<CellEntity> = mutableSetOf(),

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "player_discovered",
        joinColumns = [JoinColumn(name = "player_id")],
        inverseJoinColumns = [JoinColumn(name = "x", referencedColumnName = "x"), JoinColumn(name = "y", referencedColumnName = "y")]
    )
    var discovered: MutableSet<CellEntity> = mutableSetOf(),

    @ManyToMany(mappedBy = "children", fetch = FetchType.EAGER)
    var parents: Set<PlayerEntity> = mutableSetOf(),

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "player_children", joinColumns = [JoinColumn(name = "player_id")], inverseJoinColumns = [JoinColumn(name = "child_id")])
    var children: MutableSet<PlayerEntity> = mutableSetOf()
) {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}


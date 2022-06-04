package ru.evgeniy.dpitunnelcli.domain.usecases

import ru.evgeniy.dpitunnelcli.domain.entities.CustomIPEntry

interface ISaveCustomIPsUseCase {
    fun save(entries: List<CustomIPEntry>)
}
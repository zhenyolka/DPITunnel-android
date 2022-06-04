package ru.evgeniy.dpitunnelcli.domain.usecases

import ru.evgeniy.dpitunnelcli.domain.entities.CustomIPEntry

interface ILoadCustomIPsUseCase {
    fun load(): List<CustomIPEntry>
}
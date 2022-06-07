package ru.evgeniy.dpitunnelcli.domain.usecases

import ru.evgeniy.dpitunnelcli.domain.entities.ProxifiedApp

interface ILoadProxifiedAppsUseCase {
    fun load(): List<ProxifiedApp>
}
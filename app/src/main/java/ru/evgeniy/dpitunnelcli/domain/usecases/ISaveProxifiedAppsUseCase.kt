package ru.evgeniy.dpitunnelcli.domain.usecases

import ru.evgeniy.dpitunnelcli.domain.entities.ProxifiedApp

interface ISaveProxifiedAppsUseCase {
    fun save(list: List<ProxifiedApp>)
}
package ru.evgeniy.dpitunnelcli.domain.usecases

import ru.evgeniy.dpitunnelcli.domain.entities.ProxifiedApp
import ru.evgeniy.dpitunnelcli.domain.entities.ProxyMode

interface IProxyUseCase {
    fun set(ip: String, port: Int, proxyMode: ProxyMode, proxifiedApps: List<ProxifiedApp>)
    fun unset(proxyMode: ProxyMode)
}
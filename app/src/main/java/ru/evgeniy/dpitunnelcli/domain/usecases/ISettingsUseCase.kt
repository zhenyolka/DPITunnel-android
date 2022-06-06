package ru.evgeniy.dpitunnelcli.domain.usecases

import ru.evgeniy.dpitunnelcli.domain.entities.ProxyMode

interface ISettingsUseCase {
    fun getStartOnBoot(): Boolean
    fun getCABundlePath(): String?
    fun getProxyMode(): ProxyMode?
    fun getSystemWide(): Boolean
    fun getIP(): String?
    fun getPort(): Int?
    fun getCustomIPsPath(): String?
    fun getDefaultProfileId(): Long?
    fun setDefaultProfileId(value: Long?)
}
package ru.evgeniy.dpitunnelcli.data.usecases

import android.content.Context
import ru.evgeniy.dpitunnelcli.domain.entities.ProxifiedApp
import ru.evgeniy.dpitunnelcli.domain.usecases.ISaveProxifiedAppsUseCase
import ru.evgeniy.dpitunnelcli.domain.usecases.ISettingsUseCase

class SaveProxifiedAppsUseCase(context: Context): ISaveProxifiedAppsUseCase {

    private val settingsUseCase: ISettingsUseCase = SettingsUseCase(context)

    override fun save(list: List<ProxifiedApp>) {
        val proxifiedApps = mutableListOf<String>()
        list.forEach {
            if (it.isProxified)
                proxifiedApps.add(it.username)
        }
        proxifiedApps.sort()
        settingsUseCase.setProxifiedApps(proxifiedApps)
    }
}
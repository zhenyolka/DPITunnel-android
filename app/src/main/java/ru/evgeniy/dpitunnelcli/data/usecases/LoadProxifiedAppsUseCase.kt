package ru.evgeniy.dpitunnelcli.data.usecases

import android.content.Context
import ru.evgeniy.dpitunnelcli.domain.entities.ProxifiedApp
import ru.evgeniy.dpitunnelcli.domain.usecases.ILoadProxifiedAppsUseCase
import ru.evgeniy.dpitunnelcli.domain.usecases.ISettingsUseCase

class LoadProxifiedAppsUseCase(context: Context): ILoadProxifiedAppsUseCase {

    private val packageManager = context.packageManager
    private val settingsUseCase: ISettingsUseCase = SettingsUseCase(context)

    override fun load(): List<ProxifiedApp> {
        val installedApps = packageManager.getInstalledApplications(0)
        val proxifiedApps = settingsUseCase.getProxifiedApps()
        val list = mutableListOf<ProxifiedApp>()
        installedApps.forEach {
            list.add(
                ProxifiedApp(
                    icon = it.loadIcon(packageManager),
                    name = it.loadLabel(packageManager) as String,
                    isProxified = (proxifiedApps.binarySearch(packageManager.getNameForUid(it.uid)!!) ?: -1) >= 0,
                    uid = it.uid,
                    username = packageManager.getNameForUid(it.uid)!!
                )
            )
        }
        list.sortBy { it.name }
        return list.toList()
    }
}
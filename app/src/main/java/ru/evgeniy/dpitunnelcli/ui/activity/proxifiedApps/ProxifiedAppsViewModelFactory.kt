package ru.evgeniy.dpitunnelcli.ui.activity.proxifiedApps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.evgeniy.dpitunnelcli.domain.usecases.ILoadProxifiedAppsUseCase
import ru.evgeniy.dpitunnelcli.domain.usecases.ISaveProxifiedAppsUseCase

class ProxifiedAppsViewModelFactory(val loadProxifiedAppsUseCase: ILoadProxifiedAppsUseCase,
                                    val saveProxifiedAppsUseCase: ISaveProxifiedAppsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ILoadProxifiedAppsUseCase::class.java,
            ISaveProxifiedAppsUseCase::class.java
            )
            .newInstance(loadProxifiedAppsUseCase, saveProxifiedAppsUseCase)
    }
}
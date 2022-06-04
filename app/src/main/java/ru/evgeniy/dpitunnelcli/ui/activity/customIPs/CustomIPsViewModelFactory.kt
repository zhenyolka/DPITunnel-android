package ru.evgeniy.dpitunnelcli.ui.activity.customIPs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.evgeniy.dpitunnelcli.domain.usecases.ILoadCustomIPsUseCase
import ru.evgeniy.dpitunnelcli.domain.usecases.ISaveCustomIPsUseCase

class CustomIPsViewModelFactory(val loadCustomIPsUseCase: ILoadCustomIPsUseCase,
                                val saveCustomIPsUseCase: ISaveCustomIPsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ILoadCustomIPsUseCase::class.java,
        ISaveCustomIPsUseCase::class.java)
            .newInstance(loadCustomIPsUseCase, saveCustomIPsUseCase)
    }
}
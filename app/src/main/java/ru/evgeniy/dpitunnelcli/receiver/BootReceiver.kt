package ru.evgeniy.dpitunnelcli.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import ru.evgeniy.dpitunnelcli.cli.CliDaemon
import ru.evgeniy.dpitunnelcli.data.usecases.DaemonUseCase
import ru.evgeniy.dpitunnelcli.data.usecases.FetchAllProfilesUseCase
import ru.evgeniy.dpitunnelcli.data.usecases.ProxyUseCase
import ru.evgeniy.dpitunnelcli.data.usecases.SettingsUseCase
import ru.evgeniy.dpitunnelcli.domain.usecases.DaemonState
import ru.evgeniy.dpitunnelcli.utils.Constants
import ru.evgeniy.dpitunnelcli.utils.goAsync

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            goAsync(GlobalScope, Dispatchers.IO) {
                val daemonUseCase = DaemonUseCase(context.applicationInfo.nativeLibraryDir + '/' + Constants.DPITUNNEL_BINARY_NAME,
                    Constants.DPITUNNEL_DAEMON_PID_FILE)
                val fetchAllProfilesUseCase = FetchAllProfilesUseCase(context)
                val settingsUseCase = SettingsUseCase(context)
                val proxyUseCase = ProxyUseCase()
                if (settingsUseCase.getStartOnBoot()) {
                    daemonUseCase.start(
                        CliDaemon.PersistentOptions(
                            caBundlePath = settingsUseCase.getCABundlePath()!!,
                            ip = settingsUseCase.getIP(),
                            port = settingsUseCase.getPort(),
                            customIPsPath = settingsUseCase.getCustomIPsPath()
                        ),
                        fetchAllProfilesUseCase.fetch()
                    )
                    // Check is daemon started
                    var isStarted = false
                    withTimeoutOrNull(2000) {
                        while (true) {
                            daemonUseCase.check()
                            if (daemonUseCase.daemonState.value == DaemonState.Running) {
                                isStarted = true
                                break
                            }
                            delay(500)
                        }
                    }
                    if (settingsUseCase.getSystemWide()) {
                        if (isStarted)
                            proxyUseCase.set("127.0.0.1", settingsUseCase.getPort() ?: Constants.DPITUNNEL_DEFAULT_PORT)
                        else
                            proxyUseCase.unset()
                    }
                }
            }
        }
    }
}
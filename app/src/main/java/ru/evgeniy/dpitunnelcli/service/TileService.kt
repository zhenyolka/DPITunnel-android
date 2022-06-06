package ru.evgeniy.dpitunnelcli.service

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import ru.evgeniy.dpitunnelcli.R
import ru.evgeniy.dpitunnelcli.cli.CliDaemon
import ru.evgeniy.dpitunnelcli.data.usecases.DaemonUseCase
import ru.evgeniy.dpitunnelcli.data.usecases.FetchAllProfilesUseCase
import ru.evgeniy.dpitunnelcli.data.usecases.ProxyUseCase
import ru.evgeniy.dpitunnelcli.data.usecases.SettingsUseCase
import ru.evgeniy.dpitunnelcli.domain.usecases.*
import ru.evgeniy.dpitunnelcli.utils.Constants


@RequiresApi(Build.VERSION_CODES.N)
class SwitchTileService: TileService() {

    private var lastDaemonState: DaemonState? = null
    private var coroutineScope: CoroutineScope? = null
    private lateinit var fetchAllProfilesUseCase: IFetchAllProfilesUseCase
    private lateinit var settingsUseCase: ISettingsUseCase
    private lateinit var daemonUseCase: IDaemonUseCase
    private lateinit var proxyUseCase: IProxyUseCase

    override fun onCreate() {
        super.onCreate()
        fetchAllProfilesUseCase = FetchAllProfilesUseCase(this)
        settingsUseCase = SettingsUseCase(this)
        daemonUseCase = DaemonUseCase(
            execPath = this.applicationInfo.nativeLibraryDir + '/' + Constants.DPITUNNEL_BINARY_NAME,
            pidFilePath = Constants.DPITUNNEL_DAEMON_PID_FILE)
        proxyUseCase = ProxyUseCase()
    }

    override fun onStartListening() {
        super.onStartListening()
        coroutineScope?.cancel()
        coroutineScope = CoroutineScope(Job() + Dispatchers.Main.immediate).apply {
            launch(CoroutineName("SwitchTileService.DaemonStateFlow")) {
                daemonUseCase.daemonState.collect { state ->
                    when(state) {
                        is DaemonState.Loading -> {}
                        is DaemonState.Running -> {
                            if (lastDaemonState is DaemonState.Stopped || lastDaemonState is DaemonState.Error)
                                if (settingsUseCase.getSystemWide())
                                    proxyUseCase.set("127.0.0.1", settingsUseCase.getPort() ?: Constants.DPITUNNEL_DEFAULT_PORT, settingsUseCase.getProxyMode()!!)
                        }
                        is DaemonState.Stopped -> {
                            if (lastDaemonState is DaemonState.Running)
                                if (settingsUseCase.getSystemWide())
                                    proxyUseCase.unset(settingsUseCase.getProxyMode()!!)
                        }
                        is DaemonState.Error -> {}
                    }
                    lastDaemonState = state
                    updateTile()
                }
            }
            launch(CoroutineName("SwitchTileService.DaemonStateCheck")) {
                while (true) {
                    daemonUseCase.check()
                    delay(1000)
                }
            }
        }
        daemonUseCase.check()
        updateTile()
    }

    override fun onStopListening() {
        super.onStopListening()
        coroutineScope?.cancel()
        coroutineScope = null
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        coroutineScope?.launch(Job() + Dispatchers.Main.immediate) {
            when(daemonUseCase.daemonState.value) {
                is DaemonState.Running -> daemonUseCase.stop()
                is DaemonState.Stopped -> fetchAllProfilesUseCase.fetch().let {
                    if (it.isNotEmpty())
                        daemonUseCase.start(
                            CliDaemon.PersistentOptions(
                                caBundlePath = settingsUseCase.getCABundlePath()!!,
                                ip = settingsUseCase.getIP(),
                                port = settingsUseCase.getPort(),
                                customIPsPath = settingsUseCase.getCustomIPsPath(),
                                proxyMode = settingsUseCase.getProxyMode()!!
                            ),
                            it
                        )
                }
                is DaemonState.Error -> {}
                is DaemonState.Loading -> {}
            }
        }
    }

    private fun updateTile() {
        qsTile.icon = Icon.createWithResource(this@SwitchTileService, R.drawable.ic_quick_tile)
        qsTile.label = getString(R.string.tile_enable_disable)
        qsTile.state = when (daemonUseCase.daemonState.value) {
            is DaemonState.Loading -> { qsTile.state }
            is DaemonState.Running -> { Tile.STATE_ACTIVE }
            is DaemonState.Stopped -> { Tile.STATE_INACTIVE }
            is DaemonState.Error -> { Tile.STATE_INACTIVE }
        }
        qsTile.updateTile()
    }
}
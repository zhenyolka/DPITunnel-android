package ru.evgeniy.dpitunnelcli.data.usecases

import com.topjohnwu.superuser.Shell
import ru.evgeniy.dpitunnelcli.domain.entities.ProxifiedApp
import ru.evgeniy.dpitunnelcli.domain.entities.ProxyMode
import ru.evgeniy.dpitunnelcli.domain.usecases.IProxyUseCase

class ProxyUseCase: IProxyUseCase {
    override fun set(ip: String, port: Int, proxyMode: ProxyMode, proxifiedApps: List<ProxifiedApp>) {
        when (proxyMode) {
            ProxyMode.HTTP -> {
                Shell.su("settings put global http_proxy $ip:$port").exec()
            }
            ProxyMode.TRANSPARENT -> {
                val commands = mutableListOf(CMD_IPTABLES_RETURN.format(ip))
                proxifiedApps.forEach { app ->
                    if (app.isProxified) {
                        commands.add(CMD_IPTABLES_ADD.format(app.uid, 80, port))
                        commands.add(CMD_IPTABLES_ADD.format(app.uid, 443, port))
                    }
                }
                Shell.su(*commands.toTypedArray()).exec()
            }
        }
    }

    override fun unset(proxyMode: ProxyMode) {
        when (proxyMode) {
            ProxyMode.HTTP -> {
                Shell.su("settings put global http_proxy :0").exec()
            }
            ProxyMode.TRANSPARENT -> {
                Shell.su(CMD_IPTABLES_RESET).exec()
            }
        }
    }

    companion object {
        private const val CMD_IPTABLES_RETURN = "iptables -t nat -A OUTPUT -p tcp -d %s -j RETURN"
        private const val CMD_IPTABLES_ADD = "iptables -t nat -m owner --uid-owner %d -A OUTPUT -p tcp --dport %d -j DNAT --to-destination 127.0.0.1:%d"
        private const val CMD_IPTABLES_RESET = "iptables -t nat -F OUTPUT"
    }
}
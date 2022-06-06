package ru.evgeniy.dpitunnelcli.data.usecases

import com.topjohnwu.superuser.Shell
import ru.evgeniy.dpitunnelcli.domain.entities.ProxyMode
import ru.evgeniy.dpitunnelcli.domain.usecases.IProxyUseCase

class ProxyUseCase: IProxyUseCase {
    override fun set(ip: String, port: Int, proxyMode: ProxyMode) {
        when (proxyMode) {
            ProxyMode.HTTP -> {
                Shell.su("settings put global http_proxy $ip:$port").exec()
            }
            ProxyMode.TRANSPARENT -> {
                Shell.su(CMD_IPTABLES_RETURN.format(ip),
                    CMD_IPTABLES_ADD.format(80, port),
                    CMD_IPTABLES_ADD.format(443, port)
                ).exec()
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
        private const val CMD_IPTABLES_ADD = "iptables -t nat -m owner ! --uid-owner 0 -A OUTPUT -p tcp --dport %d -j DNAT --to-destination 127.0.0.1:%d"
        private const val CMD_IPTABLES_RESET = "iptables -t nat -F OUTPUT"
    }
}
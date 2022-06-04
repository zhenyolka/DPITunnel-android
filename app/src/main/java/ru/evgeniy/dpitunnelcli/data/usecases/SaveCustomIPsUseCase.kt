package ru.evgeniy.dpitunnelcli.data.usecases

import android.content.Context
import ru.evgeniy.dpitunnelcli.domain.entities.CustomIPEntry
import ru.evgeniy.dpitunnelcli.domain.usecases.ISaveCustomIPsUseCase
import ru.evgeniy.dpitunnelcli.utils.Constants
import java.io.File

class SaveCustomIPsUseCase(context: Context): ISaveCustomIPsUseCase {

    private val path = context.filesDir.absolutePath + "/${Constants.CUSTOM_IPS_FILENAME}"

    override fun save(entries: List<CustomIPEntry>) {
        File(path).bufferedWriter().use { out ->
            entries.forEach { entry->
                out.write("${entry.domain} ${entry.ip}")
                out.newLine()
            }
        }
    }
}
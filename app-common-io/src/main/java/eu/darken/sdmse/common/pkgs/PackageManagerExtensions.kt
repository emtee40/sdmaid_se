package eu.darken.sdmse.common.pkgs

import android.content.ComponentName
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.SharedLibraryInfo
import android.graphics.drawable.Drawable
import eu.darken.sdmse.common.debug.logging.Logging.Priority.ERROR
import eu.darken.sdmse.common.debug.logging.log
import eu.darken.sdmse.common.hasApiLevel
import eu.darken.sdmse.common.user.UserHandle2
import java.io.IOException
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.jvmErasure

fun PackageManager.getPackageInfo2(
    pkgId: Pkg.Id,
    flags: Int = 0
): PackageInfo? = try {
    getPackageInfo(pkgId.name, flags)
} catch (_: PackageManager.NameNotFoundException) {
    null
}

fun PackageManager.getLabel2(
    pkgId: Pkg.Id,
): String? = getPackageInfo2(pkgId)
    ?.applicationInfo
    ?.let {
        if (it.labelRes != 0) it.loadLabel(this).toString()
        else it.nonLocalizedLabel?.toString()
    }

fun PackageManager.getIcon2(
    pkgId: Pkg.Id,
): Drawable? = getPackageInfo2(pkgId)
    ?.applicationInfo
    ?.let { if (it.icon != 0) it.loadIcon(this) else null }


fun PackageManager.getInstalledPackagesAsUser(
    flags: Int,
    userHandle: UserHandle2,
) = try {
    PackageManager::class.memberFunctions
        .filter { it.name == "getInstalledPackagesAsUser" }
        .first {
            val arg1 = it.parameters[1].type.jvmErasure
            val arg2 = it.parameters[2].type.jvmErasure
            Int::class.isSubclassOf(arg1) && Int::class.isSubclassOf(arg2)
        }
        .call(this, flags, userHandle.handleId) as List<PackageInfo>
} catch (e: Exception) {
    throw IOException("getInstalledPackagesAsUser($flags,$userHandle) failed", e)
}

// WORKAROUND
fun PackageManager.getSharedLibraries2(flags: Int): List<SharedLibraryInfo> = try {
    getSharedLibraries(flags)
} catch (e: Exception) {
    log("PackageManager", ERROR) { "Failed getSharedLibraries($flags)" }
    // https://github.com/d4rken/sdmaid-public/issues/3100
    if (hasApiLevel(29)) throw e else emptyList()
}

fun PackageManager.toggleSelfComponent(
    component: ComponentName,
    enabled: Boolean,
) {
    log { "toggleSelfComponent($component,$enabled)" }
    setComponentEnabledSetting(
        component,
        when {
            enabled -> PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            else -> PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        },
        PackageManager.DONT_KILL_APP
    )
}
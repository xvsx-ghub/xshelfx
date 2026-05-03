package com.xvsx.shelf.push

import com.xvsx.shelf.data.local.SettingsManager

class AppBadgeService(
    private val settingsManager: SettingsManager,
) {
    companion object {
        private const val MAX_BADGE = 99
    }

    private var lastLauncherBadgeDedupeKey: String? = null

    fun recordInboundPushForLauncherBadge(message: FcmInboundMessage) {
        if (!shouldApplyBadgeIncrement(message)) return
        val newCount = (settingsManager.appIconBadgeCount + 1).coerceAtMost(MAX_BADGE)
        settingsManager.appIconBadgeCount = newCount
        applyAppIconBadgeCount(newCount)
    }

    private fun shouldApplyBadgeIncrement(message: FcmInboundMessage): Boolean {
        val key = message.messageId?.takeIf { it.isNotBlank() }
            ?: listOf(
                message.notificationTitle.orEmpty(),
                message.notificationBody.orEmpty(),
                message.collapseKey.orEmpty(),
                message.data.entries.joinToString { "${it.key}=${it.value}" },
            ).joinToString("|")
        if (key.isBlank()) return true
        if (key == lastLauncherBadgeDedupeKey) return false
        lastLauncherBadgeDedupeKey = key
        return true
    }

    fun clearAppIconBadgeForMessagesRead() {
        settingsManager.appIconBadgeCount = 0
        lastLauncherBadgeDedupeKey = null
        applyAppIconBadgeCount(0)
    }

    fun syncStoredCountToPlatform() {
        applyAppIconBadgeCount(settingsManager.appIconBadgeCount)
    }
}
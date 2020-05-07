package com.windy.exceptionreport

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

class WhatsAppAccessibilityService : AccessibilityService() {

    companion object {
        const val ID_ENTRY = "com.whatsapp:id/entry"
        const val ID_SEND = "com.whatsapp:id/send"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i("WhatsAppAccessibilityService", "Connected...")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.i("WhatsAppAccessibilityService", event.toString())

        if (rootInActiveWindow == null) {
            Log.i("WhatsAppAccessibilityService", "rootInActiveWindow is null")
            return
        }

        val rootInActiveWindow : AccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.wrap(rootInActiveWindow)
//        Log.i("WhatsAppAccessibilityService", "rootInActiveWindow = ${rootInActiveWindow}")

        val messageNodeList : List<AccessibilityNodeInfoCompat> = rootInActiveWindow.findAccessibilityNodeInfosByViewId(ID_ENTRY)
        if (messageNodeList.isEmpty()) {
            Log.i("WhatsAppAccessibilityService", "messageNodeList is empty")
            return
        }

        val messageField : AccessibilityNodeInfoCompat = messageNodeList[0]
        if (messageField.text.isEmpty()) {
            Log.i("WhatsAppAccessibilityService", "messageField is empty")
            return
        }

        val sendMessageNodeList : List<AccessibilityNodeInfoCompat> = rootInActiveWindow.findAccessibilityNodeInfosByViewId(ID_SEND)
        if (sendMessageNodeList.isEmpty()) {
            Log.i("WhatsAppAccessibilityService", "messageNodeList is empty")
            return
        }

        val sendMessageButton : AccessibilityNodeInfoCompat = sendMessageNodeList[0]
        if (!sendMessageButton.isVisibleToUser) {
            Log.i("WhatsAppAccessibilityService", "sendMessageButton is invisible")
        }

        sendMessageButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)

        Thread.sleep(500)
        performGlobalAction(GLOBAL_ACTION_BACK)

        Thread.sleep(500)
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    override fun onInterrupt() {
        Log.i("WhatsAppAccessibilityService", "onInterrupt...")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("WhatsAppAccessibilityService", "onDestroy...")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i("WhatsAppAccessibilityService", "onUnbind...")
        return super.onUnbind(intent)
    }

}

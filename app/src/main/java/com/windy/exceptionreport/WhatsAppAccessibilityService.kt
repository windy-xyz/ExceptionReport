package com.windy.exceptionreport

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class WhatsAppAccessibilityService : AccessibilityService() {

    companion object {
        const val ID_ENTRY = "com.whatsapp:id/entry"
        const val ID_SEND = "com.whatsapp:id/send"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i("WhatsAppAccessibilityService", "Connected...")

        val scheduleTaskExecutor : ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        scheduleTaskExecutor.scheduleAtFixedRate(Runnable {
            run() {
                serviceChecker();
            }
        }, 0, 5, TimeUnit.SECONDS)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.i("WhatsAppAccessibilityService", event.toString())

        if (rootInActiveWindow == null) {
            Log.i("WhatsAppAccessibilityService", "rootInActiveWindow is null")
            return
        }

        val rootInActiveWindow : AccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.wrap(rootInActiveWindow)

//        val messageNodeList : List<AccessibilityNodeInfoCompat> = rootInActiveWindow.findAccessibilityNodeInfosByViewId(ID_ENTRY)
//        if (messageNodeList.isEmpty()) {
//            Log.i("WhatsAppAccessibilityService", "messageNodeList is empty")
//            return
//        }
//
//        val messageField : AccessibilityNodeInfoCompat = messageNodeList[0]
//        if (messageField.text.isEmpty()) {
//            Log.i("WhatsAppAccessibilityService", "messageField is empty")
//            return
//        }

        val sendMessageNodeList : List<AccessibilityNodeInfoCompat> = rootInActiveWindow.findAccessibilityNodeInfosByViewId(ID_SEND)
        if (sendMessageNodeList.isEmpty()) {
            Log.i("WhatsAppAccessibilityService", "sendMessageNodeList is empty")
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

    fun serviceChecker() {
        if (!isActivityRunning(MainActivity::class.java)!!) {
            Log.i("WhatsAppAccessibilityService", "disableSelf()")
            disableSelf()
        }
    }

    protected open fun isActivityRunning(activityClass: Class<*>): Boolean? {
        val activityManager = baseContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = activityManager.getRunningTasks(Int.MAX_VALUE)
        for (task in tasks) {
            if (activityClass.canonicalName.equals(task.baseActivity!!.className, ignoreCase = true))
                return true
        }
        return false
    }

}

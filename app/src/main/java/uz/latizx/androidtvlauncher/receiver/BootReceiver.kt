package uz.latizx.androidtvlauncher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import uz.latizx.androidtvlauncher.MainActivity

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            val launchIntent = Intent(context, MainActivity::class.java)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            launchIntent.action = Intent.ACTION_MAIN
            launchIntent.addCategory(Intent.CATEGORY_HOME)
            context.startActivity(launchIntent)
        }
    }
}
package uz.latizx.androidtvlauncher

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import uz.latizx.androidtvlauncher.data.AppInfoData
import uz.latizx.androidtvlauncher.ui.components.AppItem
import uz.latizx.androidtvlauncher.ui.theme.AndroidTVLauncherTheme

class MainActivity : ComponentActivity() {
    private var lastUsedPackage: String? = null
    private var lastUsedClass: String? = null

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                launchLastUsedApp()
            }
        })

        val pref = getSharedPreferences("tv_launch_pref", MODE_PRIVATE)
        val lastUsed = pref.getString("last_used_app", null)

        if (lastUsed != null) {
            val parts = lastUsed.split("/")
            if (parts.size == 2) {
                lastUsedPackage = parts[0]
                lastUsedClass = parts[1]
                val isHomeIntent = intent?.categories?.contains(Intent.CATEGORY_HOME) == true

                if (isHomeIntent) {
                    try {
                        launchApp(lastUsedPackage!!, lastUsedClass!!)
                        return
                    } catch (e: Exception) {
                        e.printStackTrace()
                        pref.edit { remove("last_used_app") }
                        lastUsedPackage = null
                        lastUsedClass = null
                    }
                }
            }
        }

        setContent {
            AndroidTVLauncherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    LaunchScreen()
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

    }

    private fun launchLastUsedApp() {
        if (lastUsedPackage != null && lastUsedClass != null) {
            try {
                launchApp(lastUsedPackage!!, lastUsedClass!!)
            } catch (e: Exception) {
                e.printStackTrace()
                getSharedPreferences("tv_launch_pref", MODE_PRIVATE).edit {
                    remove("last_used_app")
                }
                lastUsedPackage = null
                lastUsedClass = null
            }
        }
    }

    private fun launchApp(packageName: String, className: String) {
        val intent = Intent()
        intent.setClassName(packageName, className)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.categories?.contains(Intent.CATEGORY_HOME) == true) {
            return
        }
        if (intent.action == Intent.ACTION_MAIN) {
            launchLastUsedApp()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    @Composable
    private fun LaunchScreen() {
        val context = LocalContext.current
        val packageManager = context.packageManager

        val appsList = remember {
            mutableStateOf<List<AppInfoData>>(emptyList())
        }

        LaunchedEffect(Unit) {
            val mainIntent = Intent(Intent.ACTION_MAIN)
            mainIntent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)

            val resolveInfoList: MutableList<ResolveInfo> = packageManager.queryIntentActivities(mainIntent, 0)

            val defaultIntent = Intent(Intent.ACTION_MAIN)
            defaultIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            val allApps = packageManager.queryIntentActivities(defaultIntent, 0)

            resolveInfoList.addAll(allApps)

            val apps = resolveInfoList
                .distinctBy {
                    it.activityInfo.packageName
                }
                .map { resolveInfo ->
                    val activityInfo = resolveInfo.activityInfo

                    AppInfoData(
                        label = resolveInfo.loadLabel(packageManager).toString(),
                        packageName = activityInfo.packageName,
                        activityName = activityInfo.name,
                        icon = activityInfo.loadIcon(packageManager)
                    )
                }
                .filter { it.packageName != context.packageName }
                .sortedBy { it.label }
            appsList.value = apps
        }

        Column {
            Text(
                text = "Android TV",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, bottom = 24.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(appsList.value.size) { index ->
                    val app = appsList.value[index]
                    AppItem(app) {
                        val launchIntent = Intent()
                        launchIntent.setClassName(app.packageName, app.activityName)
                        launchIntent.action = Intent.ACTION_MAIN
                        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                        launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(launchIntent)

                        (context as? MainActivity)?.apply {
                            lastUsedPackage = app.packageName
                            lastUsedClass = app.activityName
                        }

                        val pref = context.getSharedPreferences("tv_launch_pref", MODE_PRIVATE)
                        pref.edit() { putString("last_used_app", "${app.packageName}/${app.activityName}") }
                    }
                }
            }
        }
    }
}




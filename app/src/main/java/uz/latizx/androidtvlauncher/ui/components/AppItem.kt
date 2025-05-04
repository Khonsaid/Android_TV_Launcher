package uz.latizx.androidtvlauncher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import uz.latizx.androidtvlauncher.data.AppInfoData
import androidx.tv.material3.Surface as TvSurface
import androidx.tv.material3.Text as TvText

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppItem(app: AppInfoData, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }

    TvSurface(
        onClick = onClick,
        scale = ClickableSurfaceDefaults.scale(),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (isFocused) Color(0xFF3D5AFE) else Color(0xFF1E1E1E),
            contentColor = Color.White,
            focusedContainerColor = Color(0xFF3D5AFE),
        ),
        modifier = Modifier
            .size(150.dp)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val bitmap = remember(app.icon) {
                app.icon.toBitmap(96, 96)
            }

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = app.label,
                modifier = Modifier
                    .size(96.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Fit
            )

            TvText(
                text = app.label,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color =  if (isFocused) Color.White else Color.Gray,
                maxLines = 2
            )
        }
    }
}
package com.example.placetovisit

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import java.io.File
import java.io.FileOutputStream

// Save bitmap to file
fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File? {
    return try {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Share image file using FileProvider
fun shareImageFile(context: Context, file: File, message: String = "") {
    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, message)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share using"))
}

// Capture any composable as bitmap using ComposeView
fun captureComposableAsBitmap(context: Context, content: @Composable () -> Unit): Bitmap {
    val composeView = ComposeView(context).apply {
        setContent {
            content()
        }
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        measure(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout(0, 0, measuredWidth, measuredHeight)
    }
    return composeView.drawToBitmap()
}

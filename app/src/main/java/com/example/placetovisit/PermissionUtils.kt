package com.example.placetovisit

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

fun requestPermissions(
    context: Context,
    onPermissionGranted: () -> Unit,
    onPermissionPermanentlyDenied: (() -> Unit)? = null
) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    Dexter.withContext(context)
        .withPermissions(permissions)
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                val denied = report?.deniedPermissionResponses?.map { it.permissionName }
                val permanentlyDenied = report?.deniedPermissionResponses?.filter { it.isPermanentlyDenied }
                    ?.map { it.permissionName }

                if (report?.areAllPermissionsGranted() == true) {
                    onPermissionGranted()
                } else if (!permanentlyDenied.isNullOrEmpty()) {
                    onPermissionPermanentlyDenied?.invoke()
                    Toast.makeText(
                        context,
                        "Permissions permanently denied: $permanentlyDenied",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(context, "Permissions Denied: $denied", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                token?.continuePermissionRequest()
            }
        })
        .withErrorListener {
            Toast.makeText(context, "Error: ${it.name}", Toast.LENGTH_SHORT).show()
        }
        .check()
}

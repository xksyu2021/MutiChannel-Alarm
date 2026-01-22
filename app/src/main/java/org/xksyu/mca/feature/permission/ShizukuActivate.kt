package org.xksyu.mca.feature.permission

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.xksyu.mca.R
import rikka.shizuku.Shizuku

class ShizukuActivate {
    companion object{
        const val SHIZUKU_NOT_START = -1
        const val SHIZUKU_LOW_VERSION = -2
        const val SHIZUKU_GRANTED = 1
        const val SHIZUKU_NOT_GRANT = 0
    }


    @Composable
    fun CheckPermissionUI(context: Context, onBackA: () -> Unit = {}, onBackB: () -> Unit = {}) {
        var checkFail by remember { mutableStateOf(false) }
        when (checkPermissionBasic()) {
            SHIZUKU_NOT_START -> {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text(stringResource(R.string.actPage_shizuku_no)) },
                    confirmButton = {
                        Button(onClick = { onBackA() }
                        ) {
                            Text(stringResource(R.string.actPage_shizuku_ok))
                        }
                    }
                )
            }

            SHIZUKU_LOW_VERSION -> {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text(stringResource(R.string.actPage_shizuku_lowV)) },
                    confirmButton = {
                        Button(onClick = { onBackA() }
                        ) {
                            Text(stringResource(R.string.actPage_shizuku_ok))
                        }
                    }
                )
            }

            SHIZUKU_NOT_GRANT -> {
                val lifecycleOwner = LocalLifecycleOwner.current
                var isReturnedFromRequest = false
                DisposableEffect(Unit) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME && isReturnedFromRequest) {
                            if (checkPermissionBasic() == 1) {
                                Toast.makeText(
                                    context,
                                    R.string.actPage_shizuku_finish,
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBackB()
                            } else {
                                checkFail = true
                            }
                            isReturnedFromRequest = false
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }

                Button(
                    onClick = {
                        isReturnedFromRequest = true
                        Shizuku.requestPermission(12345)
                    }
                ) {
                    Text(stringResource(R.string.actPage_shizuku_check))
                }
            }

            SHIZUKU_GRANTED -> {
                Toast.makeText(context, R.string.actPage_shizuku_finish, Toast.LENGTH_SHORT).show()
                onBackB()
            }
        }
        if (checkFail) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(stringResource(R.string.actPage_shizuku_failed)) },
                confirmButton = {
                    Button(onClick = { onBackA() }
                    ) {
                        Text(stringResource(R.string.actPage_shizuku_ok))
                    }
                }
            )
        }
    }

    fun checkPermissionBasic(): Int {
        return if (Shizuku.pingBinder()) {
            if (Shizuku.isPreV11()) {
                SHIZUKU_LOW_VERSION
            } else if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                SHIZUKU_GRANTED
            } else {
                SHIZUKU_NOT_GRANT
            }
        } else {
            SHIZUKU_NOT_START
        }
    }
}
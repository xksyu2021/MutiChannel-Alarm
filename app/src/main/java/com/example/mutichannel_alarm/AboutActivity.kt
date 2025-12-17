package com.example.mutichannel_alarm

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mutichannel_alarm.ui.theme.ContrastAwareReplyTheme


class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val version = packageManager.getPackageInfo(packageName, 0).versionName
        setContent {
            ContrastAwareReplyTheme{
                AboutPage(onBack = { finish() },version = version, context = this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(onBack: () -> Unit = {},version : String? = "null",context: Context? = null){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = stringResource(R.string.title_aboutPage),
                        modifier = Modifier.fillMaxWidth(0.95f),
                        textAlign = TextAlign.End
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.backHead)
                        )
                    }
                },
            )
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.pageAbout_by),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "v $version",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row{
                OutlinedButton(
                    onClick = {
                        openUrl(context = context,url = "https://github.com/xksyu2021/MutiChannel-Alarm")
                    }
                ) {
                    Text(stringResource(R.string.pageAbout_link_code))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        openUrl(context = context,url = "https://github.com/xksyu2021/MutiChannel-Alarm/release")
                    }
                ) {
                    Text(stringResource(R.string.pageAbout_link_update))
                }
            }
        }
    }
}

fun openUrl(context: Context? = null, url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        data = Uri.parse(url)
    }
    context?.startActivity(intent)
}

@Preview(showBackground = true,
    device = "spec:width=1264px,height=2780px,dpi=480,cutout=punch_hole"
)
@Composable
fun AboutPreview(){
    ContrastAwareReplyTheme{
        AboutPage(version = "Preview")
    }
}
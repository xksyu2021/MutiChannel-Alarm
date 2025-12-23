package com.example.mutichannel_alarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mutichannel_alarm.ui.theme.ContrastAwareReplyTheme

class AlarmGet : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContrastAwareReplyTheme{
                Text("0")
            }
        }
    }
}

@Preview
@Composable
fun pre(){
    ContrastAwareReplyTheme{
        Text("true")
    }
}
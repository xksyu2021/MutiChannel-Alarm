package org.xksyu.mca

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import org.xksyu.mca.data.base.AlarmViewModel
import org.xksyu.mca.data.base.AlarmViewModelFactory
import org.xksyu.mca.data.prefer.SettingsManager
import org.xksyu.mca.page.ActivateActivity
import org.xksyu.mca.page.MainPage
import org.xksyu.mca.ui.theme.ContrastAwareReplyTheme

//android main
class MainActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    private val alarmViewModel: AlarmViewModel by viewModels {
        val repository = (application as MCApplication).repository
        AlarmViewModelFactory(repository = repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)
        setContent {
            ContrastAwareReplyTheme{
                MainPage(settingsManager = settingsManager, alarmViewModel = alarmViewModel, context = this@MainActivity)
                if(settingsManager.isFirst()){
                    val intent = Intent(this, ActivateActivity::class.java)
                    this.startActivity(intent)
                }
            }
        }
    }
}
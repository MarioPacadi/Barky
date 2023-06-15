package hr.algebra.barky

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.barky.ui.theme.DogsAppTheme
import hr.algebra.barky.util.metrics.MetricListener
import hr.algebra.barky.util.metrics.MetricsManager
import hr.algebra.barky.util.metrics.SimpleMetricListener
import hr.algebra.barky.view.nav.RootNavGraph

@ExperimentalPagingApi
@AndroidEntryPoint
public class MainActivity : ComponentActivity() {

    private lateinit var metricsManager: MetricsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        metricsManager = MetricsManager(this, activityManager, SimpleMetricListener())
        metricsManager.startMonitoringAllMetrics()

        setContent {
            DogsAppTheme {
                Surface {
                    RootNavGraph(navController = rememberNavController())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        metricsManager.stopMonitoringAllMetrics()
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            auth.signOut()
        }
    }
}
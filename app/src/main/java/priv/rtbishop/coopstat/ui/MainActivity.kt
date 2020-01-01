package priv.rtbishop.coopstat.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI

import com.google.android.material.navigation.NavigationView

import priv.rtbishop.coopstat.R
import priv.rtbishop.coopstat.vm.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var currentHumid: TextView
    private lateinit var currentTemp: TextView
    private lateinit var fanState: TextView
    private lateinit var heaterState: TextView
    private lateinit var lightState: TextView
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var viewModel: MainViewModel
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setupNavigation()
        setupToolbarViews()
    }

    private fun setupNavigation() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navController = Navigation.findNavController(this, R.id.frag_nav_host)
        appBarConfig = AppBarConfiguration
                .Builder(R.id.fr_chart1d, R.id.fr_chart90d, R.id.fr_chart365d, R.id.fr_stream)
                .setDrawerLayout(drawerLayout)
                .build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig)
        NavigationUI.setupWithNavController(navigationView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.fr_stream) {
                toolbar.visibility = View.GONE
            } else {
                toolbar.visibility = View.VISIBLE
            }
        }
    }

    private fun setupToolbarViews() {
        currentHumid = findViewById(R.id.tv_current_humid)
        currentTemp = findViewById(R.id.tv_current_temp)
        fanState = findViewById(R.id.tv_fan_state)
        heaterState = findViewById(R.id.tv_heater_state)
        lightState = findViewById(R.id.tv_light_state)

        viewModel.sensorReadings.observe(this, Observer { data ->
            currentHumid.text = String.format(resources.getString(R.string.current_humid), data.currentHumid)
            currentTemp.text = String.format(resources.getString(R.string.current_temp), data.currentTemp)
            if (data.isFanOn) {
                fanState.text = String.format(resources.getString(R.string.fan_state), resources.getString(R.string.state_on))
            } else {
                fanState.text = String.format(resources.getString(R.string.fan_state), resources.getString(R.string.state_off))
            }
            if (data.isHeaterOn) {
                heaterState.text = String.format(resources.getString(R.string.heater_state), resources.getString(R.string.state_on))
            } else {
                heaterState.text = String.format(resources.getString(R.string.heater_state), resources.getString(R.string.state_off))
            }
            if (data.isLightOn) {
                lightState.text = String.format(resources.getString(R.string.light_state), resources.getString(R.string.state_on))
            } else {
                lightState.text = String.format(resources.getString(R.string.light_state), resources.getString(R.string.state_off))
            }
        })

        viewModel.debugMessage.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfig) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fr_settings -> NavigationUI.onNavDestinationSelected(item, navController)
            R.id.action_exit -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
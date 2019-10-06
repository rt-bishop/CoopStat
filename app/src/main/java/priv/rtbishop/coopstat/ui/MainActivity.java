package priv.rtbishop.coopstat.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import priv.rtbishop.coopstat.R;
import priv.rtbishop.coopstat.data.Data;
import priv.rtbishop.coopstat.vm.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private TextView mCurrentHumid, mCurrentTemp, mFanState, mHeaterState, mLightState;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;
    private MainViewModel mViewModel;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setupNavigation();
        setupToolbarViews();
    }

    private void setupNavigation() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        mNavController = Navigation.findNavController(this, R.id.frag_nav_host);
        mAppBarConfiguration = new AppBarConfiguration
                .Builder(R.id.frag_chart1d, R.id.frag_chart90d, R.id.frag_chart365d, R.id.frag_stream)
                .setDrawerLayout(drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, mNavController);

        mNavController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.frag_stream) {
                    mToolbar.setVisibility(View.GONE);
                } else {
                    mToolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupToolbarViews() {
        mCurrentHumid = findViewById(R.id.tv_current_humid);
        mCurrentTemp = findViewById(R.id.tv_current_temp);
        mFanState = findViewById(R.id.tv_fan_state);
        mHeaterState = findViewById(R.id.tv_heater_state);
        mLightState = findViewById(R.id.tv_light_state);

        mViewModel.getData().observe(this, new Observer<Data>() {
            @Override
            public void onChanged(Data data) {
                mCurrentHumid.setText(String.format(getResources().getString(R.string.current_humid), data.getCurrentHumid()));
                mCurrentTemp.setText(String.format(getResources().getString(R.string.current_temp), data.getCurrentTemp()));
                if (data.isFanOn()) {
                    mFanState.setText(String.format(getResources().getString(R.string.fan_state), getResources().getString(R.string.state_on)));
                } else {
                    mFanState.setText(String.format(getResources().getString(R.string.fan_state), getResources().getString(R.string.state_off)));
                }
                if (data.isHeaterOn()) {
                    mHeaterState.setText(String.format(getResources().getString(R.string.heater_state), getResources().getString(R.string.state_on)));
                } else {
                    mHeaterState.setText(String.format(getResources().getString(R.string.heater_state), getResources().getString(R.string.state_off)));
                }
                if (data.isLightOn()) {
                    mLightState.setText(String.format(getResources().getString(R.string.light_state), getResources().getString(R.string.state_on)));
                } else {
                    mLightState.setText(String.format(getResources().getString(R.string.light_state), getResources().getString(R.string.state_off)));
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_connect:
                mViewModel.setupProxyConnection();
                break;
            case R.id.frag_settings:
                NavigationUI.onNavDestinationSelected(item, mNavController);
                break;
            case R.id.action_exit:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
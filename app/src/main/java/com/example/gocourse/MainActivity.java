package com.example.gocourse;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import androidx.lifecycle.ViewModelProvider;
import java.util.HashSet;
import java.util.Set;
import com.example.gocourse.ui.fragments.UserFragment;
import com.example.gocourse.ui.fragments.DateFragment;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private DrawerLayout drawerLayout;
    private AppBarConfiguration appBarConfiguration;
    private FloatingActionButton fabAdd;
    private SharedViewModel viewModel;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        setupViews();
        setupNavigation();
        setupFAB();
    }

    private void setupViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        fabAdd = findViewById(R.id.fab_add);
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.userFragment);
        topLevelDestinations.add(R.id.membershipFragment);
        
        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setupFAB() {
        fabAdd.setOnClickListener(view -> {
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment instanceof UserFragment) {
                viewModel.toggleQuickMenu();
                
                // 旋转FAB
                Boolean isVisible = viewModel.getQuickMenuVisible().getValue();
                float rotation = Boolean.TRUE.equals(isVisible) ? 45f : 0f;
                fabAdd.animate()
                    .rotation(rotation)
                    .setDuration(200)
                    .start();
            }
        });

        // 监听导航变化
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            viewModel.setQuickMenuVisible(false);
            fabAdd.setRotation(0f);
            boolean showFab = destination.getId() == R.id.userFragment;
            fabAdd.setVisibility(showFab ? View.VISIBLE : View.GONE);
        });
    }

    private Fragment getCurrentFragment() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            return navHostFragment.getChildFragmentManager().getFragments().get(0);
        }
        return null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}

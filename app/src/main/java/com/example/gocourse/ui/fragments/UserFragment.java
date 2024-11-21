package com.example.gocourse.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.appcompat.widget.PopupMenu;
import com.example.gocourse.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

public class UserFragment extends Fragment {
    private static final int COURSE_HOURS = 12;
    private static final int DAYS_IN_WEEK = 7;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fabAdd;
    private View scheduleSelector;
    private View quickActionsMenu;
    private boolean isQuickActionsVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        setupViews(view);
        setupNavigation();
        setupFab();
        setupScheduleSelector();
        setupQuickActions();
        setupTimetable(view.findViewById(R.id.content_schedule));

        return view;
    }

    private void setupViews(View view) {
        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.nav_view);
        fabAdd = view.findViewById(R.id.fab_add);
        scheduleSelector = view.findViewById(R.id.schedule_selector);
        quickActionsMenu = view.findViewById(R.id.quick_actions_menu);
        quickActionsMenu.setVisibility(View.GONE);
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_membership) {
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_userFragment_to_membershipFragment);
                drawerLayout.closeDrawers();
                return true;
            } else if (itemId == R.id.nav_settings) {
                // TODO: 实现设置页面
                drawerLayout.closeDrawers();
                return true;
            }
            return false;
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            toggleQuickActions();
        });
    }

    private void toggleQuickActions() {
        if (isQuickActionsVisible) {
            hideQuickActions();
        } else {
            showQuickActions();
        }
        isQuickActionsVisible = !isQuickActionsVisible;
    }

    private void showQuickActions() {
        quickActionsMenu.setVisibility(View.VISIBLE);
        quickActionsMenu.setAlpha(0f);
        quickActionsMenu.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(200)
                .start();
        fabAdd.animate()
                .rotation(45)
                .setDuration(200)
                .start();
    }

    private void hideQuickActions() {
        quickActionsMenu.animate()
                .alpha(0f)
                .translationY(quickActionsMenu.getHeight())
                .setDuration(200)
                .withEndAction(() -> quickActionsMenu.setVisibility(View.GONE))
                .start();
        fabAdd.animate()
                .rotation(0)
                .setDuration(200)
                .start();
    }

    private void setupScheduleSelector() {
        LinearLayout container = scheduleSelector.findViewById(R.id.schedule_tabs_container);
        
        // 添加示例课表选项
        addScheduleTab(container, "课表1", true);
        addScheduleTab(container, "课表2", false);
        
        scheduleSelector.findViewById(R.id.btn_new_schedule).setOnClickListener(v -> {
            // 处理新建课表
            addScheduleTab(container, "课表" + (container.getChildCount() + 1), false);
        });
    }

    private void addScheduleTab(LinearLayout container, String title, boolean isSelected) {
        View tab = getLayoutInflater().inflate(R.layout.schedule_tab_item, container, false);
        TextView titleView = tab.findViewById(R.id.text_schedule_name);
        titleView.setText(title);
        tab.setSelected(isSelected);
        
        tab.setOnClickListener(v -> {
            // 处理课表切换
            for (int i = 0; i < container.getChildCount(); i++) {
                container.getChildAt(i).setSelected(false);
            }
            v.setSelected(true);
        });
        
        container.addView(tab);
    }

    private void setupTimetable(View scheduleView) {
        LinearLayout coursesContainer = scheduleView.findViewById(R.id.courses_container);
        if (coursesContainer == null) return;
        
        // 为每一天创建一列
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            LinearLayout dayColumn = new LinearLayout(requireContext());
            dayColumn.setOrientation(LinearLayout.VERTICAL);
            dayColumn.setLayoutParams(new LinearLayout.LayoutParams(
                    dpToPx(80),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // 添加每个时间段的课程格子
            for (int hour = 0; hour < COURSE_HOURS; hour++) {
                TextView courseCell = createCourseCell();
                dayColumn.addView(courseCell);
            }

            coursesContainer.addView(dayColumn);
        }
    }

    private TextView createCourseCell() {
        TextView courseCell = new TextView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(60)
        );
        params.setMargins(1, 1, 1, 1);
        courseCell.setLayoutParams(params);
        courseCell.setBackgroundColor(getResources().getColor(R.color.white));
        courseCell.setGravity(android.view.Gravity.CENTER);
        return courseCell;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void setupQuickActions() {
        View btnScan = quickActionsMenu.findViewById(R.id.btn_scan);
        View btnNewCourse = quickActionsMenu.findViewById(R.id.btn_new_course);

        btnScan.setOnClickListener(v -> {
            hideQuickActions();
            Navigation.findNavController(v)
                .navigate(R.id.action_userFragment_to_scanFragment);
        });

        btnNewCourse.setOnClickListener(v -> {
            hideQuickActions();
            Navigation.findNavController(v)
                .navigate(R.id.action_userFragment_to_newCourseFragment);
        });
    }
} 
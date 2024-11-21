package com.example.gocourse.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gocourse.R;
import com.example.gocourse.ui.adapters.CalendarAdapter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateFragment extends Fragment {
    private RecyclerView calendarGrid;
    private TextView currentMonthText;
    private CalendarAdapter adapter;
    private Calendar currentDate = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date, container, false);
        
        calendarGrid = view.findViewById(R.id.calendar_grid);
        currentMonthText = view.findViewById(R.id.tv_current_month);
        
        setupCalendarGrid();
        updateCalendar();
        
        return view;
    }

    private void setupCalendarGrid() {
        calendarGrid.setLayoutManager(new GridLayoutManager(getContext(), 7));
        adapter = new CalendarAdapter(new ArrayList<>());
        calendarGrid.setAdapter(adapter);
        
        adapter.setOnDayClickListener(day -> {
            // 处理日期点击
        });
    }

    private void updateCalendar() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月", Locale.getDefault());
        currentMonthText.setText(formatter.format(currentDate.getTime()));

        List<CalendarAdapter.CalendarDay> days = new ArrayList<>();
        
        // 设置日历到当月第一天
        Calendar calendar = (Calendar) currentDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        
        // 获取当月第一天是星期几
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        
        // 添加上月的日期
        Calendar prevMonth = (Calendar) calendar.clone();
        prevMonth.add(Calendar.MONTH, -1);
        int daysInPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < firstDayOfWeek; i++) {
            prevMonth.set(Calendar.DAY_OF_MONTH, daysInPrevMonth - firstDayOfWeek + i + 1);
            days.add(new CalendarAdapter.CalendarDay((Calendar) prevMonth.clone(), false));
        }
        
        // 添加当月的日期
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= daysInMonth; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            days.add(new CalendarAdapter.CalendarDay((Calendar) calendar.clone(), true));
        }
        
        // 添加下月的日期
        int remainingDays = 42 - days.size(); // 6 weeks * 7 days = 42
        Calendar nextMonth = (Calendar) calendar.clone();
        nextMonth.add(Calendar.MONTH, 1);
        for (int i = 1; i <= remainingDays; i++) {
            nextMonth.set(Calendar.DAY_OF_MONTH, i);
            days.add(new CalendarAdapter.CalendarDay((Calendar) nextMonth.clone(), false));
        }
        
        adapter = new CalendarAdapter(days);
        calendarGrid.setAdapter(adapter);
    }
} 
package com.example.gocourse.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gocourse.R;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private static final int DAYS_IN_WEEK = 7;
    private List<ScheduleItem> scheduleItems;
    private OnCellClickListener listener;

    public ScheduleAdapter(List<ScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_grid_item, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        int row = position / (DAYS_IN_WEEK + 1); // +1 for time column
        int col = position % (DAYS_IN_WEEK + 1);
        
        if (col == 0) {
            // 时间列
            holder.bind(String.format("%d", row + 1), false);
        } else {
            // 课程格子
            ScheduleItem item = scheduleItems.get(position);
            holder.bind(item.getCourseName(), item.hasClass());
        }
    }

    @Override
    public int getItemCount() {
        return scheduleItems.size();
    }

    public void setOnCellClickListener(OnCellClickListener listener) {
        this.listener = listener;
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView cellText;

        ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            cellText = itemView.findViewById(R.id.text_cell);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    listener.onCellClick(position);
                }
            });
        }

        void bind(String text, boolean hasClass) {
            cellText.setText(text);
            itemView.setSelected(hasClass);
        }
    }

    public interface OnCellClickListener {
        void onCellClick(int position);
    }

    public static class ScheduleItem {
        private String courseName;
        private boolean hasClass;

        public ScheduleItem(String courseName, boolean hasClass) {
            this.courseName = courseName;
            this.hasClass = hasClass;
        }

        public String getCourseName() {
            return courseName;
        }

        public boolean hasClass() {
            return hasClass;
        }
    }
} 
package com.example.gocourse.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.gocourse.R;
import com.example.gocourse.ui.adapters.ScheduleAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserFragment extends Fragment {
    private static final int COURSE_HOURS = 12;
    private static final int DAYS_IN_WEEK = 7;
    private FloatingActionButton fabAdd;
    private View scheduleSelector;
    private View quickActionsMenu;
    private boolean isQuickActionsVisible = false;
    private Uri photoUri;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    handleCapturedPhoto();
                }
            });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        setupViews(view);
        setupFab();
        setupScheduleSelector();
        setupQuickActions();
        setupTimetable(view.findViewById(R.id.schedule_grid));

        return view;
    }

    private void setupViews(View view) {
        fabAdd = requireActivity().findViewById(R.id.fab_add);
        if (fabAdd == null) {
            throw new IllegalStateException("FloatingActionButton with id 'fab_add' not found in activity");
        }
        scheduleSelector = view.findViewById(R.id.schedule_selector);
        quickActionsMenu = view.findViewById(R.id.quick_actions_menu);
        quickActionsMenu.setVisibility(View.GONE);
    }

    private void setupFab() {
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> toggleQuickActions());
        }
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
        
        addScheduleTab(container, "课表1", true);
        addScheduleTab(container, "课表2", false);
        
        scheduleSelector.findViewById(R.id.btn_new_schedule).setOnClickListener(v -> {
            addScheduleTab(container, "课表" + (container.getChildCount() + 1), false);
        });
    }

    private void addScheduleTab(LinearLayout container, String title, boolean isSelected) {
        View tab = getLayoutInflater().inflate(R.layout.schedule_tab_item, container, false);
        TextView titleView = tab.findViewById(R.id.text_schedule_name);
        titleView.setText(title);
        tab.setSelected(isSelected);
        
        tab.setOnClickListener(v -> {
            for (int i = 0; i < container.getChildCount(); i++) {
                container.getChildAt(i).setSelected(false);
            }
            v.setSelected(true);
        });
        
        container.addView(tab);
    }

    // 在setupQuickActions()方法中修改btnScan的点击事件：
    private void setupQuickActions() {
        View btnScan = quickActionsMenu.findViewById(R.id.btn_scan);
        View btnNewCourse = quickActionsMenu.findViewById(R.id.btn_new_course);

        btnScan.setOnClickListener(v -> {
            hideQuickActions();
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                dispatchTakePictureIntent();
            }
        });

        btnNewCourse.setOnClickListener(v -> {
            hideQuickActions();
            Navigation.findNavController(v)
                    .navigate(R.id.action_userFragment_to_newCourseFragment);
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(requireContext(),
                        "com.example.gocourse.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                
                takePictureLauncher.launch(takePictureIntent);
            }
        } catch (IOException ex) {
            Toast.makeText(requireContext(), "创建图片文件失败", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "启动相机失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 添加权限请求结果处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(requireContext(), "需要相机权限才能使用此功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 添加常量定义
    private static final int REQUEST_CAMERA_PERMISSION = 10;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir("Photos");
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",        /* suffix */
            storageDir     /* directory */
        );
    }

    private void handleCapturedPhoto() {
        // TODO: 处理拍摄的照片，如OCR识别等
        Toast.makeText(requireContext(), "照片已保存", Toast.LENGTH_SHORT).show();
    }

    private void setupTimetable(RecyclerView scheduleGrid) {
        if (scheduleGrid == null) return;

        scheduleGrid.setLayoutManager(new GridLayoutManager(requireContext(), 8));
        
        List<ScheduleAdapter.ScheduleItem> items = new ArrayList<>();
        for (int i = 0; i < COURSE_HOURS * 8; i++) {
            items.add(new ScheduleAdapter.ScheduleItem("", false));
        }
        
        ScheduleAdapter adapter = new ScheduleAdapter(items);
        adapter.setOnCellClickListener(position -> {
            int row = position / 8;
            int col = position % 8;
            if (col > 0) {
                // TODO: 显示添加课程对话框
            }
        });
        
        scheduleGrid.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fabAdd != null) {
            fabAdd.setOnClickListener(null);
        }
    }
} 
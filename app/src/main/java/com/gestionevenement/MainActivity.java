package com.gestionevenement;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView tasksRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<TaskModel> tasksList = new ArrayList<>();
    private TextView selectedCategoryText;
    private DatabaseHelper dbHelper;
    final private CategoriesModel categoriesModel = new CategoriesModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Add new task", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            showTaskPopup();
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        selectedCategoryText = findViewById(R.id.category_tasks);
        selectedCategoryText.setText(CategoriesModel.CATEGORY_NONE);

        // RecyclerView setup
        tasksRecyclerView = findViewById(R.id.tasks_recyclerview);
        tasksRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(this);
        tasksRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(tasksRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        tasksRecyclerView.addItemDecoration(itemDecor);

        // Adapter setup
        mAdapter = new TaskRecyclerViewAdapter(getInitialTasks(), this);
        tasksRecyclerView.setAdapter(mAdapter);
    }

    private List<TaskModel> getInitialTasks() {
        List<TaskModel> sampleTasks = dbHelper.getAllTasks();
        tasksList.addAll(sampleTasks);
        return tasksList;
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        tasksList.clear();
        List<TaskModel> filteredTasks = new ArrayList<>();

        if (id == R.id.nav_personal) {
            filteredTasks = dbHelper.getTasksForCategory(CategoriesModel.CATEGORY_PERSONAL);
            selectedCategoryText.setText(CategoriesModel.CATEGORY_PERSONAL);
        } else if (id == R.id.nav_school) {
            filteredTasks = dbHelper.getTasksForCategory(CategoriesModel.CATEGORY_SCHOOL);
            selectedCategoryText.setText(CategoriesModel.CATEGORY_SCHOOL);
        } else if (id == R.id.nav_family) {
            filteredTasks = dbHelper.getTasksForCategory(CategoriesModel.CATEGORY_FAMILY);
            selectedCategoryText.setText(CategoriesModel.CATEGORY_FAMILY);
        } else if (id == R.id.nav_spiritual) {
            filteredTasks = dbHelper.getTasksForCategory(CategoriesModel.CATEGORY_SPIRITUAL);
            selectedCategoryText.setText(CategoriesModel.CATEGORY_SPIRITUAL);
        } else if (id == R.id.nav_work) {
            filteredTasks = dbHelper.getTasksForCategory(CategoriesModel.CATEGORY_WORK);
            selectedCategoryText.setText(CategoriesModel.CATEGORY_WORK);
        } else if (id == R.id.nav_all) {
            filteredTasks = dbHelper.getAllTasks();
            selectedCategoryText.setText(CategoriesModel.CATEGORY_NONE);
        }

        tasksList.addAll(filteredTasks);
        mAdapter.notifyDataSetChanged();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showTaskPopup() {
        try {
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.task_popup, null);

            final PopupWindow pw = new PopupWindow(layout, ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT, true);
            pw.setBackgroundDrawable(new ColorDrawable(Color.GRAY));

            final Spinner staticSpinner = layout.findViewById(R.id.category_spinner);
            ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesModel.getCategories());
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            staticSpinner.setAdapter(aa);

            final TextView taskTitle = layout.findViewById(R.id.title_input);
            final TextView taskDescription = layout.findViewById(R.id.description_input);

            layout.findViewById(R.id.task_add).setOnClickListener(v -> {
                String name = taskTitle.getText().toString();
                String description = taskDescription.getText().toString();
                String category = staticSpinner.getSelectedItem().toString();

                TaskModel newTask = new TaskModel(name, description, new Date(), category);
                long id = dbHelper.insertTask(newTask);
                Toast.makeText(MainActivity.this, "Task inserted with ID: " + id, Toast.LENGTH_LONG).show();

                tasksList.clear();
                tasksList.addAll(dbHelper.getAllTasks());
                mAdapter.notifyDataSetChanged();
                tasksRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                pw.dismiss();
            });

            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

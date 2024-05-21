package com.gestionevenement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class TaskRecyclerViewAdapter extends
        RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    private List<TaskModel> tasksList;
    private Context context;

    public TaskRecyclerViewAdapter(List<TaskModel> tasksLst, Context ctx) {
        this.tasksList = tasksLst;
        this.context = ctx;
    }

    @Override
    public TaskRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TaskModel selectedTask = tasksList.get(position);

        // update the data from the model (task) to the view (ui elements)
        holder.taskName.setText(selectedTask.getName());
        holder.taskDescription.setText("" + selectedTask.getDescription());
        holder.taskCategory.setText("" + selectedTask.getCategory());
        holder.taskDate.setText("" + selectedTask.getDeadline());

        //when item from the display has been touched, we update the information of the model.
        holder.taskState.setOnCheckedChangeListener(null); //reset the listener when the item is recreated when scrolling
        holder.taskState.setChecked(selectedTask.isDone()); //update the display with the status of the task
        holder.taskState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //display a short message when we click the checkbox
                    Toast.makeText(TaskRecyclerViewAdapter.this.context,
                            "task done:" + selectedTask.getName(),
                            Toast.LENGTH_LONG).show();
                    //save the status of the task
                    selectedTask.setDone(true);
                } else {
                    selectedTask.setDone(false);
                }
            }
        }
        );

        // to delete a task on checkbox click
        holder.taskState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(TaskRecyclerViewAdapter.this.context);
                db.deleteTask(selectedTask.getId());
                tasksList.remove(selectedTask);
                notifyDataSetChanged();
            }
        }

        );
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView taskName;
        public TextView taskDescription;
        public TextView taskCategory;
        public CheckBox taskState;
        public TextView taskDate;

        public ViewHolder(View view) {
            super(view);
            taskName = (TextView) view.findViewById(R.id.task_title);
            taskDescription = (TextView) view.findViewById(R.id.task_description);
            taskCategory = (TextView) view.findViewById(R.id.task_category);
            taskState = (CheckBox) view.findViewById(R.id.task_status);
            taskDate = (TextView) view.findViewById(R.id.task_due_date);

            //item click event listener
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            TextView taskTitle = (TextView) v.findViewById(R.id.task_title);
            //show more information about brand

        }
    }
}
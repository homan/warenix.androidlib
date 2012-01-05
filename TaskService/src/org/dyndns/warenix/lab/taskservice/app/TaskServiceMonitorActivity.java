package org.dyndns.warenix.lab.taskservice.app;

import java.util.ArrayList;

import org.dyndns.warenix.lab.taskservice.BackgroundTask;
import org.dyndns.warenix.lab.taskservice.R;
import org.dyndns.warenix.lab.taskservice.TaskService;
import org.dyndns.warenix.lab.taskservice.TaskServiceStateListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TaskServiceMonitorActivity extends Activity implements
		TaskServiceStateListener {

	TextView mQueueSize;
	ToggleButton startService;
	ListView mCurrentTask;

	ArrayAdapter<String> mListAdapter;
	ArrayList<String> mTaskArray;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		TaskService.setStateListener(this);

		Button addTask = (Button) findViewById(R.id.addTask);
		addTask.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addBackgroundTask();
			}
		});

		startService = (ToggleButton) findViewById(R.id.startService);
		startService.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				TaskService.setRunning(isChecked);
			}
		});

		mQueueSize = (TextView) findViewById(R.id.queueSize);

		mTaskArray = new ArrayList<String>();
		mListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mTaskArray);

		mCurrentTask = (ListView) findViewById(R.id.currentTask);
		mCurrentTask.setAdapter(mListAdapter);
	}

	public void onResume() {
		super.onResume();
		mQueueSize.setText("" + TaskService.getQueuSize());
		if (TaskService.isRunning()) {
			startService.toggle();
		}
	}

	void addBackgroundTask() {
		Intent intent = new Intent(this, TaskService.class);
		BackgroundTask task = new SleepingBackgroundTask();
		intent.putExtra("task", task);
		startService(intent);
	}

	@Override
	public void onQueueSizeChanged(final int newQueueSize) {

		mQueueSize.post(new Runnable() {

			@Override
			public void run() {
				mQueueSize.setText("" + newQueueSize);
			}

		});

	}

	@Override
	public void onBackgroundTaskExecuted(final BackgroundTask task) {
		mCurrentTask.post(new Runnable() {
			public void run() {
			}
		});
	}

	public View createTaskView(Context context) {
		TextView textView = new TextView(context);
		textView.setText(String.format("Sleeping Task"));
		textView.setPadding(10, 10, 10, 10);
		return textView;
	}

	@Override
	public void onBackgroundTaskAdded(final BackgroundTask task) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mListAdapter.add(task.toString());
				mListAdapter.notifyDataSetChanged();
			}
		});

	}

	@Override
	public void onBackgroundTaskRemoved(final BackgroundTask task) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.d("taskservice", "remove task " + task.toString());
				mListAdapter.remove(task.toString());
				mListAdapter.notifyDataSetChanged();
			}
		});
	}
}
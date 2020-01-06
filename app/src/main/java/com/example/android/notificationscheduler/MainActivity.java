package com.example.android.notificationscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int JOB_ID = 0;
    private JobScheduler mSecheduler;
    private Switch mDeviceIdleSwitch;
    private Switch mDeviceChargingSwitch;

    //Override deadline seekbar
    private SeekBar mSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void scheduleJob(View view) {


        mDeviceChargingSwitch = findViewById(R.id.charging_switch);
        mDeviceIdleSwitch = findViewById(R.id.idle_switch);
        mSeekBar = findViewById(R.id.seek_bar);
        final TextView seekBarProgress = findViewById(R.id.seek_bar_progress);

        RadioGroup networkOptions = findViewById(R.id.networkOptions);
        int selectedNetworkID = networkOptions.getCheckedRadioButtonId();

        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;

        switch (selectedNetworkID) {
            case R.id.no_network:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;
            case R.id.any_network:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifi_network:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }


        int seekBarInteger = mSeekBar.getProgress();
        boolean seekBarSet = seekBarInteger > 0;


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i > 0) {
                    seekBarProgress.setText(i + " s");
                } else {
                    seekBarProgress.setText("Not Set");
                }
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSecheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);


        //the job to be done
        ComponentName serviceName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                .setRequiredNetworkType(selectedNetworkOption)
                .setRequiresDeviceIdle(mDeviceIdleSwitch.isChecked())
                .setRequiresCharging(mDeviceChargingSwitch.isChecked());
        if (seekBarSet) {
            builder.setOverrideDeadline(seekBarInteger * 1000);
        }


        //Our constraints/conditions
        boolean constraintSet = (selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE) || mDeviceIdleSwitch.isChecked()
                || mDeviceChargingSwitch.isChecked() || seekBarSet;

        //Doing the job considering the constraint
        if (constraintSet) {
            JobInfo myJobInfo = builder.build();
            mSecheduler.schedule(myJobInfo);

            Toast.makeText(this, "JOb Schduled, job will run when the constraints are met.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please select at least one constraint.", Toast.LENGTH_LONG).show();

        }

    }

    //to cancel pending notification or job
    public void cancelJob(View view) {
        if (mSecheduler != null) {
            mSecheduler.cancelAll();
            mSecheduler = null;
            Toast.makeText(this, "Jobs Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}

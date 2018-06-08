package com.prey.services;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.prey.PreyLogger;

public class MyJobService extends JobService {



    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        PreyLogger.d("Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}

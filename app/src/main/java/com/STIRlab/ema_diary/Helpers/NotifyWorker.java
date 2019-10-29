package com.STIRlab.ema_diary.Helpers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotifyWorker extends Worker {
    private NotificationHelper pusher;

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        pusher = new NotificationHelper(context);
    }

    @NonNull
    @Override
    public Result doWork() {

        pusher.triggerNotif();
        return Result.success();
    }
}

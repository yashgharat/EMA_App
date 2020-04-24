package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class ConnectivityHelper {
    private final String TAG = this.getClass().getSimpleName();
    private ConnectionQuality mConnectionClass;
    private ConnectionClassManager mConnectionClassManager;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private ConnectionChangedListener mListener;

    private Context context;

    public ConnectivityHelper(Context context) {
        mConnectionClass = ConnectionQuality.UNKNOWN;
        mConnectionClassManager = ConnectionClassManager.getInstance();
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
        mListener = new ConnectionChangedListener();
        this.context = context;
    }

    public void changeListener(boolean check) {
        Log.e(TAG, "In listener update");
        if(check)
            mConnectionClassManager.register(mListener);
        else
            mConnectionClassManager.remove(mListener);

    }

    private class ConnectionChangedListener
            implements ConnectionClassManager.ConnectionClassStateChangeListener {

        @Override
        public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
            mConnectionClass = bandwidthState;
            Log.e(TAG, "Inside listener");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, mConnectionClass.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}




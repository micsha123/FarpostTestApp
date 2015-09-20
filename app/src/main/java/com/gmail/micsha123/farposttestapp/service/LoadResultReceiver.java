package com.gmail.micsha123.farposttestapp.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/** resultReceiver for LoadService*/
public class LoadResultReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public LoadResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
package com.javadevblog.appserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.javadevblog.ISumNumsAIDL;

public class AidlService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ISumNumsAIDL.Stub() {
            @Override
            public int sumNumbers(int firstNum, int secondNum) throws RemoteException {
                return firstNum + secondNum;
            }
        };
    }
}

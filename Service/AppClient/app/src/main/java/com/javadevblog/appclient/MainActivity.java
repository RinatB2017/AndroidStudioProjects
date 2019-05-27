package com.javadevblog.appclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.javadevblog.ISumNumsAIDL;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION_AIDL = "com.javadevblog.aidl.ISumNumbsAIDL";
    private EditText mEditTextFirstNum;
    private EditText mEditTextSecondNum;
    private Button mButtonSum;

    private ISumNumsAIDL aidlSumService;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // приводим IBinder к нужному нам типу через Stub реализацию интерфейса
            aidlSumService = ISumNumsAIDL.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            aidlSumService = null;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(ACTION_AIDL);
        Intent updatedIntent = createExplicitIntent(this, intent);
        if (updatedIntent != null) {
            bindService(updatedIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextFirstNum = findViewById(R.id.et_first_num);
        mEditTextSecondNum = findViewById(R.id.et_second_num);
        mButtonSum = findViewById(R.id.btn_sum);
        mButtonSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstNumber = mEditTextFirstNum.getText().toString().trim();
                String secondNumber = mEditTextSecondNum.getText().toString().trim();
                int sum = calculateSum(firstNumber, secondNumber);
                Toast.makeText(MainActivity.this, String.valueOf(sum), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // обращаемся к AIDL сервису и получаем результат вычислений
    private int calculateSum(String firstNumber, String secondNumber) {
        int sum = -1;
        if (!firstNumber.isEmpty() && !secondNumber.isEmpty()) {
            try {
                sum = aidlSumService.sumNumbers(Integer.valueOf(firstNumber), Integer.valueOf(secondNumber));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return sum;
    }

    public Intent createExplicitIntent(Context context, Intent intent) {
        // Получить все службы, которые могут соответствовать указанному Intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(intent, 0);

        // Список найденых служб по интенту должен содержать лишь 1 элемент
        if (resolveInfo == null || resolveInfo.size() != 1) {
            // иначе служба на "приложении-сервере" не запущена и мы должны вернуть null
            return null;
        }
        // Получаем информацию о компоненте и создаем ComponentName для Intent
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Повторно используем старый интент
        Intent explicitIntent = new Intent(intent);
        // явно задаем компонент для обработкм Intent
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}

package com.example.aidlclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.aidlclient.R;
import com.example.aidlserver.MultiplyInterface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtFirstNumber, edtSecondNumber;
    Button btnMultiply;
    TextView txtMultiplyResult;

    MultiplyInterface myInterface;
    boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtFirstNumber = findViewById(R.id.edtFirstNumber);
        edtSecondNumber = findViewById(R.id.edtSecondNumber);
        btnMultiply = findViewById(R.id.btnMultiply);
        txtMultiplyResult = findViewById(R.id.txtMultiplyResult);

        btnMultiply.setOnClickListener(this);

        Intent multiplyService = new Intent();
        multiplyService.setClassName("com.example.aidlserver", "com.example.aidlserver.services.MultiplicationService");

        bindService(multiplyService, myServiceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection myServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myInterface = MultiplyInterface.Stub.asInterface(iBinder);
            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            connected = false;
        }
    };

    @Override
    public void onClick(View view) {
        int firstNumber = Integer.parseInt(edtFirstNumber.getText().toString());
        int secondNumber = Integer.parseInt(edtSecondNumber.getText().toString());
        Log.d("MainActivity", "connected: " + connected);
        if (connected) {
            try {
                int result = myInterface.multiplyTwoValuesTogether(firstNumber, secondNumber);
                txtMultiplyResult.setText(result + "");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connected) {
            unbindService(myServiceConnection);
        }
    }
}
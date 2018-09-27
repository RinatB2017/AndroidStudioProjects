package com.example.user.test_nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;

public class MainActivity extends LogActivity {

    private static final int RECORD_REQUEST_CODE = 101;

    private NfcAdapter nfc;
    private Tag tag;
    private String[][] nfctechfilter = new String[][]{new String[]{NfcA.class.getName()}};
    private PendingIntent nfcintent;

    //private NfcAdapter nfcAdapter;

    //---------------------------------------------------------------------------------------------
    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this,
                permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permissionType}, requestCode
            );
        }
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //---
        nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc == null) {
            send_log("NFC not avail");
        }
        nfcintent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        //---

        //---
        /*
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this,
                    "NFC NOT supported on this devices!",
                    Toast.LENGTH_LONG).show();
            finish();
        } else if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this,
                    "NFC NOT Enabled!",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        */
        //---

        //send_log("MainActivity: onCreate()");
    }

    //---------------------------------------------------------------------------------------------
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        send_log("Tag detected!");

        if (tag != null) {
            byte[] id = tag.getId();
            send_log(SharedUtils.Byte2Hex(id));
        }
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        //send_log("MainActivity: onStart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        if (nfc != null) {
            nfc.enableForegroundDispatch(this, nfcintent, null, nfctechfilter);
        }
    }

    //---------------------------------------------------------------------------------------------
    /*
    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Toast.makeText(this,
                    "onResume() - ACTION_TAG_DISCOVERED",
                    Toast.LENGTH_SHORT).show();

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag == null) {
                send_log("tag == null");
            } else {
                String tagInfo = tag.toString() + "\n";

                tagInfo += "\nTag Id: \n";
                byte[] tagId = tag.getId();
                tagInfo += "length = " + tagId.length + "\n";
                for (int i = 0; i < tagId.length; i++) {
                    tagInfo += Integer.toHexString(tagId[i] & 0xFF) + " ";
                }
                tagInfo += "\n";

                String[] techList = tag.getTechList();
                tagInfo += "\nTech List\n";
                tagInfo += "length = " + techList.length + "\n";
                for (int i = 0; i < techList.length; i++) {
                    tagInfo += techList[i] + "\n ";
                }

                send_log(tagInfo);
            }
        } else {
            Toast.makeText(this,
                    "onResume() : " + action,
                    Toast.LENGTH_SHORT).show();
        }
    }
    */

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        if (nfc != null) {
            nfc.disableForegroundDispatch(this);
        }
    }

    //---------------------------------------------------------------------------------------------
    public void test(View view) throws IOException {
        send_log("MainActivity: test");
        super.test();

    }

    //---------------------------------------------------------------------------------------------
}

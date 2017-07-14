package com.example.boss.nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

// https://habrahabr.ru/company/intel/blog/194344/

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfc;
    private String[][] nfctechfilter = new String[][] { new String[] { NfcA.class.getName() } };
    private PendingIntent nfcintent;

    private Tag tag;
    private IsoDep tagcomm;

    private TextView log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        log =  (TextView) findViewById(R.id.log);

        log.append("Init NFC" + "\n");
        nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc == null) {
            log.append("NFS not avialable" + "\n");
            return;
        }
        log.append("Init nfcintent" + "\n");
        nfcintent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    protected String Byte2Hex(byte[] input, String space) {
        StringBuilder result = new StringBuilder();

        for (Byte inputbyte : input) {
            result.append(String.format("%02X" + space, inputbyte));
        }
        return result.toString();
    }

    protected void toastError(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfc != null) {
            nfc.enableForegroundDispatch(this, nfcintent, null, nfctechfilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfc != null) {
            nfc.disableForegroundDispatch(this);
        }
    }

    private void read_MifareClassic(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        boolean auth = false;
        MifareClassic mfc = MifareClassic.get(tagFromIntent);
        try {
            String metaInfo = "";
            //Enable I/O operations to the tag from this TagTechnology object.
            mfc.connect();
            int type = mfc.getType();
            int sectorCount = mfc.getSectorCount();
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }
            metaInfo += "Card type：" + typeS + "n with" + sectorCount + " Sectorsn, "
                    + mfc.getBlockCount() + " BlocksnStorage Space: " + mfc.getSize() + "Bn";
            for (int j = 0; j < sectorCount; j++) {
                //Authenticate a sector with key A.
                auth = mfc.authenticateSectorWithKeyA(j,
                        MifareClassic.KEY_DEFAULT);
                int bCount;
                int bIndex;
                if (auth) {
                    metaInfo += "Sector " + j + ": Verified successfullyn";
                    bCount = mfc.getBlockCountInSector(j);
                    bIndex = mfc.sectorToBlock(j);
                    for (int i = 0; i < bCount; i++) {
                        byte[] data = mfc.readBlock(bIndex);
                        metaInfo += "Block " + bIndex + " : "
                                + Byte2Hex(data, "") + "n";
                        bIndex++;
                    }
                } else {
                    metaInfo += "Sector " + j + ": Verified failuren";
                }
            }
            log.append(metaInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read_other_card(Intent intent) {
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);  // get the detected tag
            Parcelable[] msgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefRecord firstRecord = ((NdefMessage)msgs[0]).getRecords()[0];
            byte[] payload = firstRecord.getPayload();
            int payloadLength = payload.length;
            int langLength = payload[0];
            int textLength = payloadLength - langLength - 1;
            byte[] text = new byte[textLength];
            System.arraycopy(payload, 1+langLength, text, 0, textLength);
            Toast.makeText(this, this.getString(R.string.ok_detection)+new String(text), Toast.LENGTH_LONG).show();
        }

        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] id = tag.getId();
        log.append("ID=" + Byte2Hex(id, " ") + "\n");

        tagcomm = IsoDep.get(tag);
        if(tagcomm ==  null) {
            log.append("tagcomm is null");
        }
        else {
            try {
                tagcomm.connect();
            } catch (IOException e) {
                toastError(getResources().getText(R.string.error_nfc_comm_cont) + (e.getMessage() != null ? e.getMessage() : "-"));
                return;
            }
        }

    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        read_MifareClassic(intent);
        read_other_card(intent);
    }
}

package com.bumpy.bumpy;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class OtherDriverActivity extends BaseBumpyActivity {

    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
    public static final int MY_SOCKET_TIMEOUT_MS = 5000;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_other_driver);
        super.onCreate(savedInstanceState);
        super.initToolbar();
        context = this;
//        Toast.makeText(this, "Waiting for nfc", Toast.LENGTH_LONG).show();
//        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        if (nfcAdapter == null) {
//            // Stop here, we definitely need NFC
//            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
//            finish();
//        }
//        if (!nfcAdapter.isEnabled()) {
//            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
//        }
//        nfcAdapter.setNdefPushMessageCallback(this, this);

//        readFromIntent(getIntent());
//
//        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
//        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
//        writeTagFilters = new IntentFilter[] { tagDetected };
//        try {
//            write("Hey amnonn it work1!!", myTag);
//        } catch (Exception e)
//        {
//            Toast.makeText(this, "Failed to nfc", Toast.LENGTH_LONG).show();
//        }

    }

//    @Override
//    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
//        Toast.makeText(this, "Sending a message to you", Toast.LENGTH_LONG).show();
//        String message = "Blabla amnonn!!!";
//        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
//        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
//        return ndefMessage;
//    }

    public void Cancel(View view) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to cancel?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(OtherDriverActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    public void Confirm(View view) {
        EditText dName = (EditText) findViewById(R.id.driverName);
        EditText dID = (EditText) findViewById(R.id.driverID);
        EditText cNum = (EditText) findViewById(R.id.carNum);
        EditText insuNum = (EditText) findViewById(R.id.insuranceNum);
        EditText driverLicense = (EditText) findViewById(R.id.driverLicense);
        if (dName.getText().length() <= 2)
        {
            Toast.makeText(getApplicationContext(),
                    "Please insert the other driver name",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (dID.getText().length() <= 2)
        {
            Toast.makeText(getApplicationContext(),
                    "Please insert the other driver identification number",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (cNum.getText().length() <= 2)
        {
            Toast.makeText(getApplicationContext(),
                    "Please insert the other driver car number",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (insuNum.getText().length() <= 2)
        {
            Toast.makeText(getApplicationContext(),
                    "Please insert the other driver insurance number",
                    Toast.LENGTH_LONG).show();
            return;
        }
        carNumber = cNum.getText().toString();
        driverId = dID.getText().toString();
        driverName = dName.getText().toString();
        insuranceNum = insuNum.getText().toString();
        driverLicenseNum = driverLicense.getText().toString();

        sendData();
        Log.i("OtherDriverActivity", "Send message of an accident");

        Intent intent = new Intent(this, ViewAccidentsActivity.class);
        startActivity(intent);
    }

    public static String driverName;
    public static String driverId;
    public static String carNumber;
    public static String insuranceNum;
    public static String driverLicenseNum;

    public void sendData() {
        JSONObject postparams = null;
        try {
            postparams = new JSONObject()
                    .put("user_name", FBLoginActivity.first_name + FBLoginActivity.last_name)
                    .put("with_ambulance", Ambulance.called_ambulance)
                    .put("with_police", PoliceActivity.called_police)
                    .put("other_name", driverName)
                    .put("other_personal_id", driverId)
                    .put("other_car_number", carNumber)
                    .put("other_car_insurance", insuranceNum)
                    .put("other_driver_license", driverLicenseNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("OtherDriverActivity", "Sending: " + postparams.toString());
        Communication.SendData(getApplicationContext(), "/v1/accident", postparams, new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d("OtherDriverActivity", "RESO: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("OtherDriverActivity", "Error: " + error.toString());
                        //Failure Callback

                    }});
         }

protected void onResume(){
    super.onResume();
//    Intent intent = getIntent();
//    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
//        Parcelable[] rawMessages = intent.getParcelableArrayExtra(
//                NfcAdapter.EXTRA_NDEF_MESSAGES);
//
//        NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
//        Toast.makeText(this, new String(message.getRecords()[0].getPayload()), Toast.LENGTH_LONG).show();
//
//    } else
//        Toast.makeText(this, "Waiting for NDEF Message", Toast.LENGTH_LONG).show();

}

}
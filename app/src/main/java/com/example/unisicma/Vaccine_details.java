package com.example.unisicma;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.companion.AssociationRequest;
import android.companion.BluetoothDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;


import android.os.Bundle;
import android.os.ParcelUuid;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.google.android.material.snackbar.Snackbar;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

public class Vaccine_details extends AppCompatActivity implements ScaleUpdateCallback {

    public static final String AN_id = "id_k";
    public static final String AN_name = "name_k";
    public static final String AN_mobile = "mobile_k";
    Button scanVaccine, addItem, removeView;
    private GridLayout gridLayout;

    Button submit_btn;
    String f_id, c_id, ch_weight, new_vaccines, LVD, Child_name, new_vaccines2, facility_id;
    String vaccine_sbm_url = "http://192.168.42.176/submit_vaccination.php";
    EditText editText;
    TextView showWeight;
    private TextView isZero;
    private TextView isStable;
    private TextView connectionStatus;
    int counter = 3, arr_length, arr_length2, dayValue;
    ArrayList<String> vaccines_to_give = new ArrayList<>();
    ArrayList<String> vaccines_to_give2 = new ArrayList<>();

    String all_remaining_vaccines, all_remaining_vaccines2;
    View view;
    LinearLayout linearLayout, Backlayout, Scanlayout, vGivenlayout;
    RelativeLayout Weightlayout;

    TextView childName, idValue;

    public static TextView hiddenText;

    AlertDialog.Builder builder;

    DynamicViews dynamicViews;

    SQLite_db sqLite_db;
    SQLiteInsert sqLiteInsert;

    Cursor cursor3;
    private Toolbar toolbar;

    private ConnectivityManager connectivityManager;
    private CompanionDeviceManager companionDeviceManager;
    private AssociationRequest pairingRequest;
    private BluetoothDeviceFilter deviceFilter;
    private static final int SELECT_DEVICE_REQUEST_CODE = 42;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 2;

    private static final String TAG = "UNISICMA";
    private BluetoothService bluetoothService = null;

    private ScaleBroadcastReceiver scaleBroadcastReceiver;

    private Intent scaleBroadcastServiceIntent;
    private TextView scaleValueText;

    /**
     * Android BLE interface
     * <p>
     * private BluetoothLeService mBluetoothLeService;
     * private String mDeviceAddress;
     * private final static String TAG = Vaccine_details.class.getSimpleName();
     * private boolean mConnected = false;
     * <p>
     * <p>
     * //--------------------------------------------------Bluetooth Adapter--------------------------------
     * <p>
     * private BluetoothAdapter mBluetoothAdapter;
     * private static final int REQUEST_ENABLE_BT = 2;
     * <p>
     * // Code to manage Service lifecycle.
     * private final ServiceConnection mServiceConnection = new ServiceConnection() {
     *
     * @Override public void onServiceConnected(ComponentName componentName, IBinder service) {
     * mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
     * if (!mBluetoothLeService.initialize()) {
     * Log.e(TAG, "Unable to initialize Bluetooth");
     * finish();
     * }
     * // Automatically connects to the device upon successful start-up initialization.
     * mBluetoothLeService.connect(mDeviceAddress);
     * }
     * @Override public void onServiceDisconnected(ComponentName componentName) {
     * mBluetoothLeService = null;
     * }
     * };
     * <p>
     * // Handles various events fired by the Service.
     * // ACTION_GATT_CONNECTED: connected to a GATT server.
     * // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
     * // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
     * // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
     * //                        or notification operations.
     * private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
     * @Override public void onReceive(Context context, Intent intent) {
     * final String action = intent.getAction();
     * if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
     * mConnected = true;
     * updateConnectionState(R.string.connected);
     * invalidateOptionsMenu();
     * } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
     * mConnected = false;
     * updateConnectionState(R.string.disconnected);
     * invalidateOptionsMenu();
     * } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
     * // Show all the supported services and characteristics on the user interface.
     * } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
     * displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
     * }
     * }
     * };
     * @Override protected void onResume() {
     * super.onResume();
     * registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
     * if (mBluetoothLeService != null) {
     * final boolean result = mBluetoothLeService.connect(mDeviceAddress);
     * Log.d(TAG, "Connect request result=" + result);
     * }
     * <p>
     * <p>
     * }
     * <p>
     * private void updateConnectionState(final int resourceId) {
     * runOnUiThread(new Runnable() {
     * @Override public void run() {
     * Toast.makeText(getApplicationContext(), resourceId, Toast.LENGTH_LONG).show();
     * }
     * });
     * }
     * <p>
     * private void displayData(String data) {
     * if (data != null) {
     * Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
     * }
     * }
     * @Override protected void onPause() {
     * super.onPause();
     * unregisterReceiver(mGattUpdateReceiver);
     * }
     * @Override protected void onDestroy() {
     * super.onDestroy();
     * // unbindService(mServiceConnection);
     * mBluetoothLeService = null;
     * }
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_details);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString(AN_id, null);
        String anmName = sharedPreferences.getString(AN_name, null);
        showWeight = findViewById(R.id.c_weight);
        //this.isZero = (TextView) findViewById(R.id.zero);
        //this.isStable = (TextView) findViewById(R.id.stable);
        this.connectionStatus = (TextView) findViewById(R.id.connected);
        String anmMobile = sharedPreferences.getString(AN_mobile, null);
        SharedPreferences preferences = getSharedPreferences("save to all activity", Activity.MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            assert language != null;
            if (language.contains("hi") || language.contains("ta")) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.sarojini));
            } else {
                Objects.requireNonNull(getSupportActionBar()).setTitle(anmName);
            }
        }
        toolbar.setLogo(R.drawable.ic_face_4dp);
        toolbar.setSubtitle(getString(R.string.anm_mobile) + ": " + anmMobile);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            companionDeviceManager = getSystemService(CompanionDeviceManager.class);


            // To skip filtering based on name and supported feature flags (UUIDs),
            // don't include calls to setNamePattern() and addServiceUuid(),
            // respectively. This example uses Bluetooth.
            deviceFilter = new BluetoothDeviceFilter.Builder()
                    .addServiceUuid(new ParcelUuid(new UUID(0x2A9D, -1L)), null)
                    .build();

            // The argument provided in setSingleDevice() determines whether a single
            // device name or a list of device names is presented to the user as
            // pairing options.
            pairingRequest = new AssociationRequest.Builder()
                    .addDeviceFilter(deviceFilter)
                    .setSingleDevice(true)
                    .build();

            // When the app tries to pair with the Bluetooth device, show the
            // appropriate pairing request dialog to the user.
            companionDeviceManager.associate(pairingRequest,
                    new CompanionDeviceManager.Callback() {
                        @Override
                        public void onDeviceFound(IntentSender chooserLauncher) {
                            try {
                                startIntentSenderForResult(chooserLauncher,
                                        SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(CharSequence error) {

                        }
                    },
                    null);
        }


        // submit_btn = findViewById(R.id.Enter_Record);
        scanVaccine = findViewById(R.id.scan_vaccine);
       // editText = findViewById(R.id.c_weight);
        // addItem = findViewById(R.id.addItem);
        //removeView = findViewById(R.id.deleteItem);
        gridLayout = findViewById(R.id.gr_layout);
        linearLayout = findViewById(R.id.linearRoot);
        Backlayout = findViewById(R.id.BackLayout);
        Weightlayout = findViewById(R.id.WeightLayout);
        Scanlayout = findViewById(R.id.ScanLayout);
        vGivenlayout = findViewById(R.id.VGivenLayout);
        childName = findViewById(R.id.child_name);
        idValue = findViewById(R.id.id_value);
        hiddenText = findViewById(R.id.hiddenText);
        //this.scaleValueText = (TextView) findViewById(R.id.measured_value);
        //-----------------------------Checking if Bluetooth is enabled or not------------------
/*
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }


 */

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        sqLite_db = SQLite_db.getInstance(getApplicationContext());
        sqLiteInsert = SQLiteInsert.getInstance(getApplicationContext());

        builder = new AlertDialog.Builder(Vaccine_details.this);
        //------------------------Scan Button for scanning the particular vaccine-------------------------

        scanVaccine.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Vaccine_details.this, vaccine_scan.class);
                startActivityForResult(intent, 1);


                Bundle bundle1 = getIntent().getExtras();
                c_id = bundle1.getString("child_id");
                LVD = bundle1.getString("lvd");
                Child_name = bundle1.getString("child_name");

                facility_id = bundle1.getString("facility_id");
                //---------------Setting counter for vaccines to be provided -----------------------------------

                getVaccineCount();

                // addItem.setOnClickListener(new View.OnClickListener() {
                // @Override
                // public void onClick(View v) {


                //}
                // });
            }
        });

        //  ch_weight = editText.getText().toString();


       /* removeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteView(view);
            }
        });

        */

//---------------------------------Submit Vaccines-------------------------

      /*
        submit_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                f_id = bundle.getString("facility");


                ch_weight = editText.getText().toString();

                if (ch_weight.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields.!", Toast.LENGTH_LONG).show();
                } else {

                    final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    //-------------------------Using Internet Connection-----------------------------

                    /*

                    if (networkInfo != null && networkInfo.isConnected()) {

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, vaccine_sbm_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        String message;


                                        try {
                                            JSONArray jsonArray = new JSONArray(response);

                                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                                            message = jsonObject.getString("message");
                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {


                                Map<String, String> params = new HashMap<String, String>();
                                SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, getApplicationContext().MODE_PRIVATE);
                                String anm_id = sharedPreferences.getString(AN_id, null);

                                params.put("anm_id", anm_id);
                                params.put("f_id", f_id);
                                params.put("c_id", c_id);
                                params.put("vID", vaccine_scan.vID);
                                params.put("ch_weight", ch_weight);
                                return params;
                            }
                        };

                        MySingleton.getInstance(Vaccine_details.this).addToRequestQueue(stringRequest);
                    } else {
*/

        //--------------------SQLiteInsert----------------
/*
                    Bundle bundle1 = getIntent().getExtras();
                    c_id = bundle1.getString("child_id");
                    LVD = bundle1.getString("lvd");
                    String facility_id = bundle1.getString("facility_id");
                    SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, getApplicationContext().MODE_PRIVATE);
                    String anm_id = sharedPreferences.getString(AN_id, null);

                    sqLiteInsert.open();
                    Cursor cursor = sqLiteInsert.getSingleVaccine(c_id, vaccine_scan.vID);
                    if (cursor.getCount() == 0) {
                        if (sqLite_db.insertData(c_id, vaccine_scan.vID, anm_id, facility_id, ch_weight)) {
                            builder.setTitle(getString(R.string.response));

                            displayAlert(vaccine_scan.vName + " is Given ");

                        } else {
                            builder.setTitle(getString(R.string.response));
                            displayAlert("Error while inserting..");
                        }
                    } else {
                        Snackbar.make(linearLayout, vaccine_scan.vName + " is Already Given", Snackbar.LENGTH_LONG).show();
                    }


                    //  }

                }


            }
        });
        */
        scaleBroadcastServiceIntent = new Intent(this, ScaleBroadcastService.class);
        startService(scaleBroadcastServiceIntent);

    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (counter <= (50 * vaccines_to_give.size())) {
                sqLiteInsert.open();
                cursor3 = sqLiteInsert.getSingleVaccine(c_id, vaccine_scan.vID);
                if (cursor3.getCount() == 0) {
                    addView(view);
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.all_given)
                        , Toast.LENGTH_LONG).show();
            }
            if (cursor3.getCount() == 0) {
                counter += 3;
            }


            ch_weight = editText.getText().toString();

            Bundle bundle = getIntent().getExtras();
            assert bundle != null;
            f_id = bundle.getString("facility");


            ch_weight = editText.getText().toString();

            if (ch_weight.equals("")) {
                Toast.makeText(getApplicationContext(), "Please fill all the fields.!", Toast.LENGTH_LONG).show();
            } else {

                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                //--------------------SQLiteInsert----------------

                SharedPreferences sharedPreferences1 = getSharedPreferences(ANM_Login.MyPREFERENCE, getApplicationContext().MODE_PRIVATE);
                String anm_id = sharedPreferences1.getString(AN_id, null);

                sqLiteInsert.open();
                Cursor cursor = sqLiteInsert.getSingleVaccine(c_id, vaccine_scan.vID);
                if (cursor.getCount() == 0) {
                    if (sqLite_db.insertData(c_id, vaccine_scan.vID, anm_id, facility_id, ch_weight)) {
                        builder.setTitle(getString(R.string.status));

                        displayAlert(vaccine_scan.vName + " " + getString(R.string.given));

                    } else {
                        builder.setTitle(getString(R.string.status));
                        displayAlert("Error while inserting..");
                    }
                } else {
                    final Snackbar snackbar = Snackbar.make(linearLayout, vaccine_scan.vName + getString(R.string.already_given), Snackbar.LENGTH_LONG);
                    snackbar.setAction(getString(R.string.dismiss), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }

                //  }

            }
        }
    }

   */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (counter <= (50 * vaccines_to_give.size())) {
                    sqLiteInsert.open();
                    cursor3 = sqLiteInsert.getSingleVaccine(c_id, vaccine_scan.vID);
                    if (cursor3.getCount() == 0) {
                        addView(view);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.all_given)
                            , Toast.LENGTH_LONG).show();
                }
                if (cursor3.getCount() == 0) {
                    counter += 3;
                }


                //ch_weight = editText.getText().toString();

                Bundle bundle = getIntent().getExtras();
                assert bundle != null;
                f_id = bundle.getString("facility");

                //ch_weight = editText.getText().toString();

                if (showWeight.toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Weight is not Entered", Toast.LENGTH_LONG).show();
                } else {

                    final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    //--------------------SQLiteInsert----------------

                    SharedPreferences sharedPreferences1 = getSharedPreferences(ANM_Login.MyPREFERENCE, getApplicationContext().MODE_PRIVATE);
                    String anm_id = sharedPreferences1.getString(AN_id, null);

                    sqLiteInsert.open();
                    Cursor cursor = sqLiteInsert.getSingleVaccine(c_id, vaccine_scan.vID);
                    if (cursor.getCount() == 0) {
                        String[] parts = ch_weight.split(" ");
                        String weight = parts[0];
                        if (sqLite_db.insertData(c_id, vaccine_scan.vID, anm_id, facility_id, weight,vaccine_scan.scan_result)) {
                            builder.setTitle(getString(R.string.status));

                            displayAlert(vaccine_scan.vName + " " + getString(R.string.given));

                        } else {
                            builder.setTitle(getString(R.string.status));
                            displayAlert("Error while inserting..");
                        }


                    } else {
                        final Snackbar snackbar = Snackbar.make(linearLayout, vaccine_scan.vName + getString(R.string.already_given), Snackbar.LENGTH_LONG);
                        snackbar.setAction(getString(R.string.dismiss), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                    }

                    //  }

                }
                break;
            case 42:
                if (resultCode == Activity.RESULT_OK) {
                    // User has chosen to pair with the Bluetooth device.
                    assert data != null;
                    //  BluetoothDevice deviceToPair =
                    //        data.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
                    // Objects.requireNonNull(deviceToPair).createBond();
                    //  mDeviceAddress = deviceToPair.getAddress();
                    //   mBluetoothLeService.connect(mDeviceAddress);
                }
                break;
            /*case 2:
                if (resultCode == Activity.RESULT_CANCELED) {
                    finish();
                    return;
                } else {

                }
                break;

             */

        }
    }

    public void connectToDevice(BluetoothSensorDevice sensorDevice) {
        Log.i(TAG, sensorDevice.getAddress());
        this.bluetoothService.connectToDevice(this.mBluetoothAdapter.getRemoteDevice(sensorDevice.getAddress()));
    }

    private void setupBluetooth() {
        BluetoothHandler handler = new BluetoothHandler(this.getApplicationContext());
        this.bluetoothService = new BluetoothService(handler);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        setupBluetooth();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerBroadcastReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterBroadcastReceiver();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        stopService(scaleBroadcastServiceIntent);
        if(this.bluetoothService != null) {
            this.bluetoothService.stop();
        }
    }

    public void disconnectScale(View view) {
        if(this.bluetoothService != null) {
            this.bluetoothService.stop();
        }
    }

    public void sendZeroInstruction(View view) {
        this.bluetoothService.sendZeroInstruction();
    }

    private void registerBroadcastReceiver() {
        IntentFilter broadcastFilter = new IntentFilter(ScaleBroadcastReceiver.SCALE_BROADCAST_ACTION);
        scaleBroadcastReceiver = new ScaleBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(scaleBroadcastReceiver, broadcastFilter);
    }

    private void unregisterBroadcastReceiver() {
        try {
            if (scaleBroadcastReceiver != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(scaleBroadcastReceiver);
                scaleBroadcastReceiver = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error attempting to unregister scale broadcast receiver", e);
        }
    }

    @Override
    public void handleState(BluetoothService.ConnectionState state) {
        String statusString;
        switch(state) {
            case NOT_CONNECTED:
                statusString = "Disconnected";
                break;
            case CONNECTING:
                statusString = "Connecting";
                break;
            case CONNECTED:
                statusString = "Connected";
                break;
            default:
                statusString = "N/A";
                break;
        }
        this.connectionStatus.setText(statusString);
    }

    @Override
    public void handleScaleReading(ScaleReading scaleReading) {
ch_weight = String.format(Locale.getDefault(), "%.3f %s", scaleReading.getWeight(), scaleReading.getUnit());
        this.showWeight.setText(String.format(Locale.getDefault(), "%.3f %s", scaleReading.getWeight(), scaleReading.getUnit()));
        //this.isZero.setText(scaleReading.isZero() ? "ZERO" : "");
        //this.isStable.setText(scaleReading.isStable() ? "STABLE" : "");
    }

    //-----------SQLite Section---------------------
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getVaccineCount() {

        Bundle bundle1 = getIntent().getExtras();
        c_id = Objects.requireNonNull(bundle1).getString("child_id");
        LVD = bundle1.getString("lvd");

        Cursor cursor = sqLite_db.vaccinesRemained(c_id, LVD);
        arr_length = cursor.getCount();
        if (arr_length != 0) {
            while (cursor.moveToNext()) {

                String vaccine_id = cursor.getString(0);

                new_vaccines = cursor.getString(1);
                vaccines_to_give.add(new_vaccines);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < vaccines_to_give.size(); i++) {
                    stringBuilder.append(vaccines_to_give.get(i) + ", ");
                }
                all_remaining_vaccines = stringBuilder.toString();


            }
        } else {
            all_remaining_vaccines = null;
        }

    }


    public void addView(View view) {

        this.view = view;
        dynamicViews = new DynamicViews(getApplicationContext());
        gridLayout.addView(dynamicViews.vaccineCheckBox(getApplicationContext(),
                vaccine_scan.vID), counter);
        gridLayout.addView(dynamicViews.vaccine_name(getApplicationContext(),
                vaccine_scan.vName), counter + 1);
        gridLayout.addView(dynamicViews.dosage(getApplicationContext(),
                vaccine_scan.vDosage), counter + 2);
    }

    public void deleteView(View view) {
        this.view = view;
        dynamicViews = new DynamicViews(getApplicationContext());

        gridLayout.removeView(dynamicViews.vaccineTextView(getApplicationContext(), vaccine_scan.vID));
        gridLayout.removeView(dynamicViews.vaccine_name(getApplicationContext(), vaccine_scan.vName));
        gridLayout.removeView(dynamicViews.dosage(getApplicationContext(), vaccine_scan.vDosage));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:

                SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();

                Toast.makeText(this, "Logout option selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void displayAlert(String message) {
        final String state, state1;

        //------------------------------------------Getting Remained Vaccines to show in alert dialog----------------

        Cursor cursor = sqLite_db.vaccinesRemained(c_id, LVD);
        arr_length = cursor.getCount();

        if (arr_length != 0) {
            while (cursor.moveToNext()) {

                String vaccine_id = cursor.getString(0);

                new_vaccines = cursor.getString(1);
                vaccines_to_give.add(new_vaccines);
                StringBuilder stringBuilder = new StringBuilder();

                //-------------------------------Appending all remain vaccines to Array list and to string using StringBuilder-----------------

                for (int i = 0; i < vaccines_to_give.size(); i++) {

                    stringBuilder.append(vaccines_to_give.get(i) + ", ");
                }
                all_remaining_vaccines = stringBuilder.toString();

                //-----------------------------------If no Due Vaccine------------------------------------------------
            }
        } else {
            all_remaining_vaccines = null;
        }
        if (all_remaining_vaccines == null) {

            state = getString(R.string.ok);
            state1 = getString(R.string.back);
        } else {
            state = getString(R.string.next_vaccine);
            state1 = getString(R.string.back);
        }

        builder.setMessage(message);
        builder.setPositiveButton(state1, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                childName.setText(Child_name);
                idValue.setText(c_id);

                TransitionManager.beginDelayedTransition(linearLayout);
                Backlayout.setVisibility(ViewGroup.VISIBLE);
                Weightlayout.setVisibility(ViewGroup.GONE);
                Scanlayout.setVisibility(ViewGroup.GONE);
                vGivenlayout.setVisibility(ViewGroup.VISIBLE);
            }
        });
        builder.setNegativeButton(state, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (state.contains(getString(R.string.ok))) {
                    SharedPreferences sharedPreferences = getSharedPreferences("RI_preference", Context.MODE_PRIVATE);
                    sharedPreferences.edit().clear().apply();
                    startActivity(new Intent(Vaccine_details.this, Dashboard.class));
                }

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    //-----------------------------------------------Next Child Vaccine Alert--------------------------------------------

    public void VaccineAlert(View view) {

        Bundle bundle1 = getIntent().getExtras();
        c_id = bundle1.getString("child_id");
        LVD = bundle1.getString("lvd");

        //------------------------------------------Getting Remained Vaccines Again to show in alert dialog----------------

        dayValue = sqLite_db.DateValue();

        sqLite_db.open();
        Cursor cursor = sqLite_db.vaccinesRemained(c_id, LVD);

        arr_length2 = cursor.getCount();

        //-----------------------------------------Checking if there are due vaccines or not-------------------

        if (arr_length2 != 0) {
            if (DateDifference(c_id) <= dayValue) {
                while (cursor.moveToNext()) {

                    String vaccine_id = cursor.getString(0);

                    new_vaccines2 = cursor.getString(1);
                    vaccines_to_give2.add(new_vaccines2);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < vaccines_to_give2.size(); i++) {
                        stringBuilder.append(vaccines_to_give2.get(i) + ", ");
                    }
                    all_remaining_vaccines2 = stringBuilder.toString();
                }

                //-----------------------------------Initializing and setting click lister for Alert Dialog----------------

                builder.setTitle(getString(R.string.status));
                builder.setMessage(getString(R.string.remained) + ":\n" + all_remaining_vaccines2 + " \n" +
                        getString(R.string.continue_vac));
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        TransitionManager.beginDelayedTransition(linearLayout);
                        Backlayout.setVisibility(ViewGroup.GONE);
                        Weightlayout.setVisibility(ViewGroup.VISIBLE);
                        Scanlayout.setVisibility(ViewGroup.VISIBLE);
                        vGivenlayout.setVisibility(ViewGroup.GONE);

                        all_remaining_vaccines2 = null;
                        vaccines_to_give2.clear();

                    }
                });

                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences sharedPreferences = getSharedPreferences("RI_preference", Context.MODE_PRIVATE);
                        sharedPreferences.edit().clear().apply();
                        Intent intent = new Intent(Vaccine_details.this, Dashboard.class);
                        startActivity(intent);

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
        if (DateDifference(c_id) > dayValue) {
            // Toast.makeText(getApplicationContext(), "else mein chala gaya", Toast.LENGTH_LONG).show();
            builder.setTitle(getString(R.string.status));
            builder.setMessage(getString(R.string.all_given));
            builder.setPositiveButton(getString(R.string.next_proceed), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedPreferences = getSharedPreferences("RI_preference", Context.MODE_PRIVATE);
                    sharedPreferences.edit().clear().apply();
                    Intent intent = new Intent(Vaccine_details.this, Dashboard.class);
                    startActivity(intent);
                }
            });
            AlertDialog alertDialog1 = builder.create();
            alertDialog1.show();

        }

        //-----------------------------------If no Due Vaccine------------------------------------------------

        sqLite_db.close();

        //--------------------------------Setting list array of remained vaccines to null----------------------

        all_remaining_vaccines2 = null;
        vaccines_to_give2.clear();
    }

    public long DateDifference(String child_id) {
        String dueDate = null;
        //--------------------------------Checking if child comes under Due list or not---------------------
        SQLite_db sqLite_db;
        sqLite_db = SQLite_db.getInstance(Vaccine_details.this);

        Cursor cursor4 = sqLite_db.DueOnDate(child_id);
        if (cursor4 == null) {

        } else {
            while (cursor4.moveToNext()) {
                dueDate = cursor4.getString(0);
            }
        }
        //------------------------------Getting the Difference between Due date and Current date------------

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String Present_date;
        Date Current_date, DueDate;
        long difference = 0;
        Present_date = dateFormat.format(calendar.getTime());
        try {
            Current_date = dateFormat.parse(Present_date);
            DueDate = dateFormat.parse(dueDate);
            difference = (DueDate.getTime() - Current_date.getTime()) / (1000 * 60 * 60 * 24);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return difference;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void connectToScale(View view) {

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            Log.i(TAG, Integer.toString(pairedDevices.size()));
            ArrayList<String> deviceList = new ArrayList<>(pairedDevices.size());

            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                BluetoothSensorDevice sensorDevice = new BluetoothSensorDevice(device);
                deviceList.add(sensorDevice.getLabel());
            }
            final String[] deviceArray = deviceList.toArray(new String[0]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Scale:");
            builder.setItems(deviceArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, final int item) {
                    BluetoothSensorDevice sensorDevice = BluetoothSensorDevice.fromLabel(deviceArray[item]);
                    connectToDevice(sensorDevice);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }
    }
}



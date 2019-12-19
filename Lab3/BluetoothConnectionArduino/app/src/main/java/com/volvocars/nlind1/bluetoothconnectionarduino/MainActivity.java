package com.volvocars.nlind1.bluetoothconnectionarduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> devices;
    private BluetoothDevice device = null;
    private ArrayList<BluetoothDevice> myBluetoothDevices;
    MyBluetoothService mBtService;
    ArrayAdapter<BluetoothDevice> pairedDevicesArrayAdapter;

    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Button btn_scanDevices;
    Button btn_turnOnLed;
    Button btn_flashLed;
    Button btn_flashTwoLed;
    ToggleButton btn_buzzerOnOff;
    ListView devicesList;
    LinearLayout deviceListLayout;
    TextView buzzerStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init all widgets
        btn_scanDevices = findViewById(R.id.btn_ShowPairedDevice);
        btn_turnOnLed = findViewById(R.id.btn_turnOnLed);
        btn_flashLed = findViewById(R.id.btn_flashLed);
        btn_flashTwoLed = findViewById(R.id.btn_flashTwoLeds);
        btn_buzzerOnOff = findViewById(R.id.btn_TurnOnNOffBuzzer);
        buzzerStatus = findViewById(R.id.txt_buzzerStatus);

        devicesList = findViewById(R.id.deviceList);
        deviceListLayout = findViewById(R.id.linearLayout2);
        deviceListLayout.setVisibility(View.INVISIBLE);          // in startup, don't make it visible
        myBluetoothDevices = new ArrayList<>();
        pairedDevicesArrayAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, myBluetoothDevices);
        mBtService = new MyBluetoothService(MainActivity.this);

        // set buttons disable
        setButtonsDisable();

        // turn on bluetooth
        turnOnBluetooth();

        // onclick listener for button scanDevices
        btn_scanDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pairedDevicesArrayAdapter.isEmpty()) {
                    pairedDevicesArrayAdapter.clear();
                }
                findDevices();
                if (deviceListLayout.getVisibility() == View.INVISIBLE) {
                    deviceListLayout.setVisibility(View.VISIBLE);
                } else {
                    deviceListLayout.setVisibility(View.INVISIBLE);
                }

            }
        });

        // this on click listener can you find below
        devicesList.setOnItemClickListener(MainActivity.this);

        // this sends 1 to arduino
        btn_turnOnLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String output = "1";

                mBtService.write(output.getBytes());
            }
        });

        // this send 2 to arduino
        btn_flashLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String output = "2";

                mBtService.write(output.getBytes());
            }
        });

        // this sends 3 to arduino
        btn_flashTwoLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String output = "3";

                mBtService.write(output.getBytes());
            }
        });

        // this is a togglebutton and sends either 4 or 5, this depends is toggle is on or off
        // and its set the text of the current status of buzzer
        btn_buzzerOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    String output = "4";
                    mBtService.write(output.getBytes());
                } else {
                    String output = "5";
                    mBtService.write(output.getBytes());
                }
            }
        });

        handlerTask.run();
    }

    // Handler checks if status of buzzer changes, updates every 100ms
    Handler handler = new Handler();
    Runnable handlerTask = new Runnable() {
        @Override
        public void run() {
            switch (mBtService.getBuzzerStatus()) {
                case "4":
                    buzzerStatus.setText("BuzzerStatus: buzzer on");
                    break;
                case "5":
                    buzzerStatus.setText("BuzzerStatus: buzzer off");
                    break;
                default:
                    break;
            }
            handler.postDelayed(handlerTask, 100);
        }
    };

    // Disable buttons
    public void setButtonsDisable() {
        btn_turnOnLed.setEnabled(false);
        btn_flashTwoLed.setEnabled(false);
        btn_flashLed.setEnabled(false);
        btn_buzzerOnOff.setEnabled(false);
    }

    // Enables buttons
    public void setButtonsEnabled() {
        btn_turnOnLed.setEnabled(true);
        btn_flashTwoLed.setEnabled(true);
        btn_flashLed.setEnabled(true);
        btn_buzzerOnOff.setEnabled(true);
    }

    /**
     * Description: when you click on a bluetooth item in list view
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mBluetoothAdapter.cancelDiscovery();

        Log.d("Main", "onItemClick, you clicked a device");
        String deviceName = myBluetoothDevices.get(i).getName();
        String deviceAddress = myBluetoothDevices.get(i).getAddress();

        Log.d("OnItemClick", "deviceName: " + deviceName);
        Log.d("OnItemClick", "deviceAddress" + deviceAddress);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d("TRY TO PAIR", "try to pair with device " + deviceName);
            myBluetoothDevices.get(i).createBond();

            device = myBluetoothDevices.get(i);
            mBtService.startClient(device, myUUID);
            deviceListLayout.setVisibility(View.INVISIBLE);
            setButtonsEnabled();
        }
    }

    /**
     * Description: If devices have any paired devices,
     * print them out in a list view
     */
    private void findDevices() {
        devices = mBluetoothAdapter.getBondedDevices();

        Log.d("CheckDisc", String.valueOf(mBluetoothAdapter.isDiscovering()));
        if (devices.size() > 0) {
            for (BluetoothDevice device : devices) {
                myBluetoothDevices.add(device);
                devicesList.setAdapter(pairedDevicesArrayAdapter);
            }
        }
    }

    /**
     * Description: Turns on bluetooth, if the devices have any
     * Otherwise applications turns off
     */
    private void turnOnBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Yor devices doesn't support bluetooth", Toast.LENGTH_LONG);

            finish();
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, 1);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // do nothing, just override
    }

    /**
     * Description: onDestroy-function
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBtService.cancel();

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

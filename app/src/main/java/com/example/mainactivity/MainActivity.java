package com.example.mainactivity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

//blessed for android imports



public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private static final String TAG = "MyActivity";

    TextView viewX, viewY, viewZ, deviceText,microbitX,microbitY,microbitZ;
    Button startButton, stopButton, buttonBluetooth, buttonServer;

    //sensors from sensor manager
    private SensorManager sensorManager;
    private Sensor accelerometer;


    // dont know the mac of my device hostname is VR
    // private String deviceaddress = ("4A:21:25:DD:B3:2D");

    //  hostname is Mindo
    //  private String deviceaddress = ("DF:4A:07:7E:13:92");
    //  public UUID[] serviceUuidArray = {UUID.fromString("0000A012-0000-1000-8000-00805F9B34FB"),UUID.fromString("0000fff3-0000-1000-8000-00805F9B34FB")};


    //private String deviceaddress = ("FC:57:1A:B7:22:AD");
    //public final static String ACTION_DATA_AVAILABLE = "de.example.BluetoothLETest.ACTION_DATA_AVAILABLE";
/*
    public static final UUID Accel_SERVICE_UUID = UUID.fromString("A012");
    public static final UUID Accel_X_CHAR_UUID = UUID.fromString("A013");
    public static final UUID Accel_Y_CHAR_UUID = UUID.fromString("A014");
    public static final UUID Accel_Z_CHAR_UUID = UUID.fromString("A015");
    public static final UUID Joystick_SERVICE_UUID = UUID.fromString("fff3");
    public static final UUID Joystick_X_CHAR_UUID = UUID.fromString("0001");
    public static final UUID Joystick_Y_CHAR_UUID = UUID.fromString("0002");
    public static final UUID Joystick_Z_CHAR_UUID = UUID.fromString("0003");
    public String[] serviceUuidArray = {"A012","fff3"};
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    String bluetoothDeviceAddress;
    BluetoothGatt bluetoothGatt;
    int connectionState;
    final  int STATE_DISCONNECTED = 0;
    final  int STATE_CONNECTING = 1;
    final  int STATE_CONNECTED = 2;
    final String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    final String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    final  String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    final  String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    final  String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
*/
    public static final UUID Accel_service_UUID = UUID.fromString("0000A012-0000-1000-8000-00805F9B34FB");
    public static final UUID Accel_X_CHAR_UUID = UUID.fromString("0000A013-0000-1000-8000-00805F9B34FB");
    public static final UUID Accel_Y_CHAR_UUID = UUID.fromString("0000A014-0000-1000-8000-00805F9B34FB");
    public static final UUID Accel_Z_CHAR_UUID = UUID.fromString("0000A015-0000-1000-8000-00805F9B34FB");

    public static final UUID Joystick_service_UUID = UUID.fromString("0000fff3-0000-1000-8000-00805F9B34FB");
    public static final UUID Joystick_X_CHAR_UUID = UUID.fromString("00000001-0000-1000-8000-00805F9B34FB");
    public static final UUID Joystick_Y_CHAR_UUID = UUID.fromString("00000002-0000-1000-8000-00805F9B34FB");
    public static final UUID Joystick_Z_CHAR_UUID = UUID.fromString("00000003-0000-1000-8000-00805F9B34FB");

    public boolean connected = false;
    public String global_PONG_IP = "";
    public String globalMobileAccelData = "";
    public String globalMicrobitAccelData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.buttonStart);
        startButton.setOnClickListener(this);


        stopButton = findViewById(R.id.buttonStop);
        stopButton.setOnClickListener(this);

        buttonBluetooth = findViewById(R.id.buttonBluetooth);
        buttonBluetooth.setOnClickListener(this);

        buttonServer = findViewById(R.id.buttonServer);
        buttonServer.setOnClickListener(this);

        viewX = findViewById(R.id.viewX);
        viewX.setText("0.0");
        viewY = findViewById(R.id.viewY);
        viewY.setText("0.0");
        viewZ = findViewById(R.id.viewZ);
        viewZ.setText("0.0");
        deviceText = findViewById(R.id.deviceText);
        deviceText.setText("0.0");

        microbitX = findViewById(R.id.microbitX);
        microbitX.setText("0.0");

        microbitY = findViewById(R.id.microbitY);
        microbitY.setText("0.0");

        microbitZ = findViewById(R.id.microbitZ);
        microbitZ.setText("0.0");



        //instantiate object of sensor manager and accelerometer to be used by the application
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //get accelerometer sensor from sensor manager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


    }

    public void setupBluetooth() {

        // readChars(device);
        // readCharsAll(device);

        int REQUEST_ENABLE_BT = 1;
        Log.d("scan", "request BT access");

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            Log.d("scan", " BT null");
                return;
        }

        if (bluetoothAdapter.isEnabled()) {
            Log.d("scan", " BT Enabled");

            blessAndroidBLE();
        }

        if(!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }







    }

    public void blessAndroidBLE(){
        Log.d("scan", "inBlessedForAndroid");
        BluetoothHandler.getInstance(getApplicationContext());
        registerReceiver(Accel_X_Receiver, new IntentFilter( "Accel_X_CHAR_UUID_MEASUREMENT" ));
        registerReceiver(Accel_Y_Receiver, new IntentFilter( "Accel_Y_CHAR_UUID_MEASUREMENT" ));
        registerReceiver(Accel_Z_Receiver, new IntentFilter( "Accel_Z_CHAR_UUID_MEASUREMENT" ));

        registerReceiver(Joystick_X_Receiver, new IntentFilter( "Joystick_X_CHAR_UUID_MEASUREMENT" ));
        registerReceiver(Joystick_Y_Receiver, new IntentFilter( "Joystick_Y_CHAR_UUID_MEASUREMENT" ));
        registerReceiver(Joystick_Z_Receiver, new IntentFilter( "Joystick_Z_CHAR_UUID_MEASUREMENT" ));
        //registerReceiver(Joystick_BUTTON_Receiver, new IntentFilter( "Joystick_BUTTON_CHAR_UUID_MEASUREMENT" ));


    }
    private final BroadcastReceiver Accel_X_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Serializable measurement = intent.getSerializableExtra("Accel_X_CHAR_UUID_MEASUREMENT");
            microbitX.setText(String.format(measurement.toString()));
        }
    };
    private final BroadcastReceiver Accel_Y_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Serializable measurement = intent.getSerializableExtra("Accel_Y_CHAR_UUID_MEASUREMENT");
            microbitY.setText(String.format(measurement.toString()));
        }
    };
    private final BroadcastReceiver Accel_Z_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Serializable measurement = intent.getSerializableExtra("Accel_Z_CHAR_UUID_MEASUREMENT");
            microbitZ.setText(String.format(measurement.toString()));
        }
    };

    private final BroadcastReceiver Joystick_X_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Serializable measurement = intent.getSerializableExtra("Joystick_X_CHAR_UUID_MEASUREMENT");
           //microbitX.setText(String.format(measurement.toString()));
        }
    };
    private final BroadcastReceiver Joystick_Y_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Serializable measurement = intent.getSerializableExtra("Joystick_Y_CHAR_UUID_MEASUREMENT");
           // microbitX.setText(String.format(measurement.toString()));
        }
    };
    private final BroadcastReceiver Joystick_Z_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Serializable measurement = intent.getSerializableExtra("Joystick_Z_CHAR_UUID_MEASUREMENT");
            //microbitX.setText(String.format(measurement.toString()));
        }
    };



    /*
    private final BroadcastReceiver bloodPressureDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BloodPressureMeasurement measurement = (BloodPressureMeasurement) intent.getSerializableExtra("BloodPressure");
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
            String formattedTimestamp = df.format(measurement.timestamp);
            measurementValue.setText(String.format(Locale.ENGLISH, "%.0f/%.0f %s, %.0f bpm\n%s", measurement.systolic, measurement.diastolic, measurement.isMMHG ? "mmHg" : "kpa", measurement.pulseRate, formattedTimestamp));
        }
    };*/





/*
    public void readChars(RxBleDevice device) {
        device.establishConnection(false)
                .flatMapSingle(rxBleConnection -> rxBleConnection.readCharacteristic(Accel_X_CHAR_UUID))
                .subscribe(
                        characteristicValue -> {
                            // Read characteristic value.

                            Log.d("scan", "accel X value " + characteristicValue.toString());
                        },
                        throwable -> {
                            Log.d("scan", "subscribe error");
                            // Handle an error here.
                        }
                );

    }

    @SuppressLint("CheckResult")
    public void readCharsAll(RxBleDevice device) {

        device.establishConnection(false)
                .flatMap(rxBleConnection -> Observable.combineLatest(
                        rxBleConnection.readCharacteristic(Accel_X_CHAR_UUID),
                        rxBleConnection.readCharacteristic(Accel_Y_CHAR_UUID),
                        rxBleConnection.readCharacteristic(Accel_Z_CHAR_UUID),
                        ReadResult::new
                ))
                .subscribe(
                        readResult -> Log.d("Characteristics", readResult.toString()),
                        throwable -> Log.e("Error", throwable.getMessage())
                );

    }

*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStart: {
                //register the accelerometer with the sensor manager
                if (sensorManager != null) {
                    sensorManager.registerListener(this, accelerometer, sensorManager.SENSOR_DELAY_NORMAL);
                }

                break;
            }
            case R.id.buttonStop: {
                sensorManager.unregisterListener(this);
            }
            case R.id.buttonBluetooth: {
                setupBluetooth();
                //test
            }
            case R.id.buttonServer: {
                bindServer();
            }

        }
    }


    public void bindServer() {

        int port = 5555;

        try (DatagramSocket server = new DatagramSocket(port)) {
            server.setBroadcast(true);
            server.setSoTimeout(1000);
            sendPing(server);

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            Log.d("send", "START ACCEL DATA TRANSFER");
                            sendAccelData(server);

                        }
                    },
                    3000);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public void sendPing(DatagramSocket server) {
        int port = 5555;
        String message = "Ping";
        String BROADCAST_ADDR = "255.255.255.255";
        byte[] sendBuffer = message.getBytes();
        try {
            InetAddress serverAddr = InetAddress.getByName(BROADCAST_ADDR);
            DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddr, port);
            server.send(packet);
            Log.d("sent", "Sent '" + message + "' to " + BROADCAST_ADDR);
            listenForPong(server);
        } catch (SocketException socketEx) {
            Log.e("UDP:", "Socket Error", socketEx);
        } catch (IOException ioEx) {
            Log.e("UDP Send:", "Input Output Error", ioEx);
        }

    }


    public void listenForPong(DatagramSocket server) {
        try {
            byte[] messageReceived = new byte[8000];
            DatagramPacket packetReceived = new DatagramPacket(messageReceived, messageReceived.length);
            Log.d("rece", "waiting to receive message");
            String text = new String(messageReceived, 0, packetReceived.getLength());
            Log.d("MSG", "MSG " + text);
            while (connected = false) {
                server.receive(packetReceived);

                Log.d("rece", "Message Received " + text);
                if (text == "Pong") {
                    global_PONG_IP = String.valueOf(server.getInetAddress());
                    Log.d("global ip", "global pong ip" + global_PONG_IP);
                    connected = true;

                }
            }
        } catch (IOException ioEx) {
            Log.e("UDP LISTEN:", "Input Output Error", ioEx);
        }

    }

    public void SendMessage(String message, DatagramSocket udpSocket) {

        int port = 5555;
        String BROADCAST_ADDR = "255.255.255.255";

        try {
            byte[] sendBuffer = message.getBytes();
            InetAddress serverAddr = InetAddress.getByName(BROADCAST_ADDR);
            DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddr, port);
            udpSocket.send(packet);
            udpSocket.setSoTimeout(3000);
            Log.d("SENT", "Sending Packet " + packet.getData());
        } catch (SocketException socketEx) {
            Log.e("UDP:", "Socket Error", socketEx);
        } catch (IOException ioEx) {
            Log.e("UDP Send:", "Input Output Error", ioEx);
        }
    }

    public void sendAccelData(DatagramSocket server) {
        if (connected) {
            try {
                int port = 5555;
                //String BROADCAST_ADDR = "255.255.255.255";

                InetAddress serverAddr = InetAddress.getByName(global_PONG_IP);
                //String message = "accel data >>> ";
                byte[] sendBuffer = globalMobileAccelData.getBytes();
                DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddr, port);

                server.send(packet);
                Log.d("send", "Sent " + globalMobileAccelData + "'");
            } catch (UnknownHostException eHost) {
                Log.d("sendAccelData", ">>> " + eHost.getMessage());
            } catch (IOException eIO) {
                Log.d("IO Execption ", ">>> " + eIO.getMessage());
            }
        }


    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // get values for each axes X,Y,Z
            Log.d("NEIL", "Collecting sensor values");
            float X = event.values[0];
            float Y = event.values[1];
            float Z = event.values[2];
            Log.d("NEIL", "Logging sensor values" + "\n" + X + "\n" + Y + "\n" + Z);

            float timeStamp = event.timestamp;
            Log.d("NEIL", "Timestamp" + timeStamp);

            // set value on the screen
            String textX = Float.toString(X);
            String textY = Float.toString(Y);
            String textZ = Float.toString(Z);

            viewX.setText(textX);
            viewY.setText(textY);
            viewZ.setText(textZ);

            StringBuilder sb = new StringBuilder();
            sb.append(textX + "/").append(textY + "/").append(textZ + "/");

            String convertedMessage = sb.toString();
            System.out.println("CONVERTED MESSAGE" + convertedMessage);
            globalMobileAccelData = convertedMessage;
            Log.d("CONVERTED MESSAGE", convertedMessage);

            int port = 5555;

            try {
                DatagramSocket udpSocket = new DatagramSocket(port);
                SendMessage(convertedMessage, udpSocket);
            } catch (SocketException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    //this is an abstract method in the SensorEventListener , it must be implemented
    // but its not used in this application
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
package com.example.mainactivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.welie.blessed.BluetoothBytesParser;
import com.welie.blessed.BluetoothCentral;
import com.welie.blessed.BluetoothCentralCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import java.util.UUID;

import static android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_HIGH;
import static com.welie.blessed.BluetoothBytesParser.bytes2String;
import static com.welie.blessed.BluetoothPeripheral.GATT_SUCCESS;

public class BluetoothHandler {

    public static final UUID Accel_service_UUID = UUID.fromString("0000A012-0000-1000-8000-00805F9B34FB");
    public static final UUID Accel_X_CHAR_UUID = UUID.fromString("0000A013-0000-1000-8000-00805F9B34FB");
    public static final UUID Accel_Y_CHAR_UUID = UUID.fromString("0000A014-0000-1000-8000-00805F9B34FB");
    public static final UUID Accel_Z_CHAR_UUID = UUID.fromString("0000A015-0000-1000-8000-00805F9B34FB");

    public static final UUID Joystick_service_UUID = UUID.fromString("0000fff3-0000-1000-8000-00805F9B34FB");
    public static final UUID Joystick_X_CHAR_UUID = UUID.fromString("00000001-0000-1000-8000-00805F9B34FB");
    public static final UUID Joystick_Y_CHAR_UUID = UUID.fromString("00000002-0000-1000-8000-00805F9B34FB");
    public static final UUID Joystick_Z_CHAR_UUID = UUID.fromString("00000003-0000-1000-8000-00805F9B34FB");



    // Local variables
    private BluetoothCentral central;
    private static BluetoothHandler instance = null;
    private Context context;
    private Handler handler = new Handler();
    private int currentTimeCounter = 0;

    // Callback for peripherals
    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered(BluetoothPeripheral peripheral) {
            Log.d("BLE","discovered services");

            // Request a new connection priority
            peripheral.requestConnectionPriority(CONNECTION_PRIORITY_HIGH);

            // Read manufacturer and model number from the Device Information Service
            if(peripheral.getService(Accel_service_UUID) != null) {
                peripheral.readCharacteristic(peripheral.getCharacteristic(Accel_service_UUID, Accel_X_CHAR_UUID));
                peripheral.readCharacteristic(peripheral.getCharacteristic(Accel_service_UUID, Accel_Y_CHAR_UUID));
                peripheral.readCharacteristic(peripheral.getCharacteristic(Accel_service_UUID, Accel_Z_CHAR_UUID));
            }
            if(peripheral.getService(Joystick_service_UUID) != null) {
                peripheral.readCharacteristic(peripheral.getCharacteristic(Joystick_service_UUID, Joystick_X_CHAR_UUID));
                peripheral.readCharacteristic(peripheral.getCharacteristic(Joystick_service_UUID, Joystick_Y_CHAR_UUID));
                peripheral.readCharacteristic(peripheral.getCharacteristic(Joystick_service_UUID, Joystick_Z_CHAR_UUID));
            }

            // Turn on notifications for Current Time Service


                // If it has the write property we write the current time
                if (peripheral.getService(Accel_service_UUID) != null) {
                    peripheral.readCharacteristic(peripheral.getCharacteristic(Accel_service_UUID, Accel_X_CHAR_UUID));
                    BluetoothGattCharacteristic accel_X = peripheral.getCharacteristic(Accel_service_UUID, Accel_X_CHAR_UUID);
                    peripheral.setNotify(accel_X, true);
                    peripheral.readCharacteristic(peripheral.getCharacteristic(Accel_service_UUID, Accel_Y_CHAR_UUID));
                    BluetoothGattCharacteristic accel_Y = peripheral.getCharacteristic(Accel_service_UUID, Accel_Y_CHAR_UUID);
                    peripheral.setNotify(accel_Y, true);
                    peripheral.readCharacteristic(peripheral.getCharacteristic(Accel_service_UUID, Accel_Z_CHAR_UUID));
                    BluetoothGattCharacteristic accel_Z = peripheral.getCharacteristic(Accel_service_UUID, Accel_Z_CHAR_UUID);
                    peripheral.setNotify(accel_Z, true);
            }
                // If it has the write property we write the current time
                if (peripheral.getService(Joystick_service_UUID) != null) {
                    peripheral.readCharacteristic(peripheral.getCharacteristic(Joystick_service_UUID, Joystick_X_CHAR_UUID));
                    BluetoothGattCharacteristic joystick_X = peripheral.getCharacteristic(Joystick_service_UUID, Joystick_X_CHAR_UUID);
                    peripheral.setNotify(joystick_X, true);
                    peripheral.readCharacteristic(peripheral.getCharacteristic(Joystick_service_UUID, Joystick_Y_CHAR_UUID));
                    BluetoothGattCharacteristic joystick_Y = peripheral.getCharacteristic(Joystick_service_UUID, Joystick_Y_CHAR_UUID);
                    peripheral.setNotify(joystick_Y, true);
                    peripheral.readCharacteristic(peripheral.getCharacteristic(Joystick_service_UUID, Joystick_Z_CHAR_UUID));
                    BluetoothGattCharacteristic joystick_Z = peripheral.getCharacteristic(Joystick_service_UUID, Joystick_Z_CHAR_UUID);
                    peripheral.setNotify(joystick_Z, true);
                }
        }

        @Override
        public void onNotificationStateUpdate(BluetoothPeripheral peripheral, BluetoothGattCharacteristic characteristic, int status) {
            if( status == GATT_SUCCESS) {
                if(peripheral.isNotifying(characteristic)) {
                   Log.d("BLE","SUCCESS: Notify set to 'on' for %s"+ characteristic.getUuid());
                } else {
                    Log.d("BLE","SUCCESS: Notify set to 'off' for %s"+ characteristic.getUuid());
                }
            } else {
                Log.d("BLE","ERROR: Changing notification state failed for %s"+ characteristic.getUuid());
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothPeripheral peripheral, byte[] value, BluetoothGattCharacteristic characteristic, int status) {
            if( status == GATT_SUCCESS) {
               Log.d("BLE","SUCCESS: Writing <%s> to <%s>"+ bytes2String(value) + characteristic.getUuid().toString());
            } else {
                Log.d("BLE","ERROR: Failed writing <%s> to <%s>"+ bytes2String(value) + characteristic.getUuid().toString());
            }
        }

        @Override
        public void onCharacteristicUpdate(BluetoothPeripheral peripheral, byte[] value, BluetoothGattCharacteristic characteristic, int status) {
            if(status != GATT_SUCCESS) return;
            UUID characteristicUUID = characteristic.getUuid();
            BluetoothBytesParser parser = new BluetoothBytesParser(value);

            if (characteristicUUID.equals(Accel_X_CHAR_UUID)) {
                 byte[] measurement = value;
                Intent intent = new Intent("Accel_X_CHAR_UUID_MEASUREMENT");
                intent.putExtra("Accel_X_CHAR_UUID_MEASUREMENT", measurement);
                context.sendBroadcast(intent);
                Log.d("CHARS","Accel_X_CHAR"+ measurement);
            }
            else if(characteristicUUID.equals(Accel_Y_CHAR_UUID)) {
                byte[] measurement = value;
                Intent intent = new Intent("Accel_Y_CHAR_UUID_MEASUREMENT");
                intent.putExtra("Accel_Y_CHAR_UUID_MEASUREMENT", measurement);
                context.sendBroadcast(intent);
                Log.d("CHARS","Accel_Y_CHAR"+ measurement);
            }
            else if(characteristicUUID.equals(Accel_Z_CHAR_UUID)) {
                byte[] measurement = value;
                Intent intent = new Intent("Accel_Z_CHAR_UUID_MEASUREMENT");
                intent.putExtra("Accel_Z_CHAR_UUID_MEASUREMENT", measurement);
                context.sendBroadcast(intent);
                Log.d("CHARS","Accel_Z_CHAR"+ measurement);
            }
            else if(characteristicUUID.equals(Joystick_X_CHAR_UUID)) {
                byte[] measurement = value;
                Intent intent = new Intent("Joystick_X_CHAR_UUID_MEASUREMENT");
                intent.putExtra("Joystick_X_CHAR_UUID_MEASUREMENT", measurement);
                context.sendBroadcast(intent);
                Log.d("CHARS","Joystick_X_CHAR"+ measurement);
            }
            else if(characteristicUUID.equals(Joystick_Y_CHAR_UUID)) {
                byte[] measurement = value;
                Intent intent = new Intent("Joystick_Y_CHAR_UUID_MEASUREMENT");
                intent.putExtra("Joystick_Y_CHAR_UUID_MEASUREMENT", measurement);
                context.sendBroadcast(intent);
                Log.d("CHARS","Joystick_Y_CHAR"+ measurement);
            }
            else if(characteristicUUID.equals(Joystick_Z_CHAR_UUID)) {
                byte[] measurement = value;
                Intent intent = new Intent("Joystick_Z_CHAR_UUID_MEASUREMENT");
                intent.putExtra("Joystick_Z=_CHAR_UUID_MEASUREMENT", measurement);
                context.sendBroadcast(intent);
                Log.d("CHARS","Joystick_Z_CHAR"+ measurement);
            }
            else{
                Log.d("CHARS","End of else if ladder didnt read chars");
            }

            // joystick BUTTON
            /*
            else if(characteristicUUID.equals(Accel_X_CHAR_UUID)) {
                byte[] measurement = value;
                Intent intent = new Intent("Accel_X_CHAR_UUID_MEASUREMENT");
                intent.putExtra("Accel_X_CHAR_UUID_MEASUREMENT", measurement);
                context.sendBroadcast(intent);
                Log.d("CHARS",""+ measurement);
            }*/
        }
    };

    // Callback for central
    private final BluetoothCentralCallback bluetoothCentralCallback = new BluetoothCentralCallback() {
        @Override
        public void onConnectedPeripheral(BluetoothPeripheral peripheral) {
            Log.d("BLE","connected to '%s'"+ peripheral.getName());
        }

        @Override
        public void onConnectionFailed(BluetoothPeripheral peripheral, final int status) {
            Log.d("BLE","connection '%s' failed with status %d"+ peripheral.getName() + status);
        }

        @Override
        public void onDisconnectedPeripheral(final BluetoothPeripheral peripheral, final int status) {
            Log.d("BLE","disconnected '%s' with status %d"+ peripheral.getName() + status);
            // Reconnect to this device when it becomes available again
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    central.autoConnectPeripheral(peripheral, peripheralCallback);
                }
            }, 5000);
        }

        @Override
        public void onDiscoveredPeripheral(BluetoothPeripheral peripheral, ScanResult scanResult) {
            Log.d("BLE","Found peripheral '%s'"+peripheral.getName());
            central.stopScan();
            central.connectPeripheral(peripheral, peripheralCallback);
        }

        @Override
        public void onBluetoothAdapterStateChanged(int state) {
            Log.d("BLE","bluetooth adapter changed state to %d"+ state);
            if(state == BluetoothAdapter.STATE_ON) {
                // Bluetooth is on now, start scanning again
                // Scan for peripherals with a certain service UUIDs
                central.startPairingPopupHack();
                central.scanForPeripheralsWithServices(new UUID[]{Accel_service_UUID,Joystick_service_UUID,});
            }
        }
    };

    public static synchronized BluetoothHandler getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothHandler(context.getApplicationContext());
        }
        return instance;
    }

    private BluetoothHandler(Context context) {
        this.context = context;

        // Create BluetoothCentral
        central = new BluetoothCentral(context, bluetoothCentralCallback, new Handler());

        // Scan for peripherals with a certain service UUIDs
        central.startPairingPopupHack();
        central.scanForPeripheralsWithServices(new UUID[]{Accel_service_UUID,Joystick_service_UUID});
    }
}
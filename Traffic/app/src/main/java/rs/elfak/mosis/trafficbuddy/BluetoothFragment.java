package rs.elfak.mosis.trafficbuddy;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;


public class BluetoothFragment extends Fragment {

    private Button listenButton;
    private Button listDevicesButton;
    private Button sendButton;
    private Button acceptButton;


    private Button bluetoothButton;
    private Button bck;
    private ImageView icon;
    private ListView listView;
    private Boolean isConfirmed = false;
    private Integer count = 0;

    private BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice[] btArray;
    SendReceive sendReceive;


    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    private static final String APP_NAME = "TrafficBuddy";
    private static final UUID MY_UUID = UUID.fromString("7b1ba677-ef2e-48b9-89e9-3ab8f8dfd1be");

    FirebaseUser loggedInUser;
    String uid;
    String connectingWithUser;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVER_BT = 2;
    private ArrayList<String> mDeviceList;
    private TextView status, msg_box;
    EditText writeMsg;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);


        listenButton = view.findViewById(R.id.listenButton);
        listenButton.setVisibility(View.VISIBLE);

        listDevicesButton = view.findViewById(R.id.showDevicesButton);
        sendButton = view.findViewById(R.id.sendButton);
        sendButton.setVisibility(View.INVISIBLE);


        acceptButton = view.findViewById(R.id.accept);
        acceptButton.setVisibility(View.INVISIBLE);


        bluetoothButton = view.findViewById(R.id.bluetoothButton);

        status = view.findViewById(R.id.status);

        msg_box = view.findViewById(R.id.msg_box);
        writeMsg = view.findViewById(R.id.writeMsg);

        bck = view.findViewById(R.id.bck);
        icon = view.findViewById(R.id.icon);

        bck.setVisibility(View.INVISIBLE);
        icon.setVisibility(View.INVISIBLE);
        msg_box.setVisibility(View.INVISIBLE);
        status.setVisibility(View.INVISIBLE);


        listView = (ListView) view.findViewById(R.id.listView);
        mDeviceList = new ArrayList<String>();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        loggedInUser = Firebase.getFirebaseAuth().getCurrentUser();
        uid = loggedInUser.getUid();


        bluetoothButton.setOnClickListener(l -> {

            if(!mBluetoothAdapter.isEnabled() || !mBluetoothAdapter.isDiscovering()) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(discoverableIntent, REQUEST_DISCOVER_BT);
            }
            else
                Toast.makeText(getContext(), "Bluetooth je vec aktivan", Toast.LENGTH_SHORT).show();
            implementListeners();
        });

        return view;
    }

    private void implementListeners() {

        listDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendButton.setVisibility(View.VISIBLE);
                listenButton.setVisibility(View.INVISIBLE);

                bck.setVisibility(View.VISIBLE);
                icon.setVisibility(View.VISIBLE);
                msg_box.setVisibility(View.VISIBLE);
                status.setVisibility(View.VISIBLE);

                Set<BluetoothDevice> bt = mBluetoothAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                btArray = new BluetoothDevice[bt.size()];
                int index = 0;

                if (bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        btArray[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String string = loggedInUser.getUid();
                sendReceive.write(string.getBytes());
            }

        });

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptButton.setVisibility(View.VISIBLE);

                bck.setVisibility(View.VISIBLE);
                icon.setVisibility(View.VISIBLE);
                msg_box.setVisibility(View.VISIBLE);
                status.setVisibility(View.VISIBLE);

                BluetoothFragment.ServerClass serverClass = new BluetoothFragment.ServerClass();
                serverClass.start();
            }

        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothFragment.ClientClass clientClass = new BluetoothFragment.ClientClass(btArray[i]);
                clientClass.start();

                status.setText("Connecting");
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String string = String.valueOf(writeMsg.getText());
                String string = loggedInUser.getUid();
                sendReceive.write(string.getBytes());
            }
        });
    }

    public void makeFriends(String tempMsg) {
        connectingWithUser = tempMsg;

        Firebase.getDbRef().child(Firebase.DB_USERS).child(uid).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                User user = task.getResult().getValue(User.class);
                if (user != null) {
                    user.setNewFriend(connectingWithUser);
                    Firebase.getDbRef().child(Firebase.DB_USERS).child(uid).setValue(user).addOnSuccessListener(unused -> {
                        Toast.makeText(getActivity(), "Uspesno dodat prijatelj",
                                Toast.LENGTH_SHORT).show();
                    });
                }
                }

        });
    }

    //KOD KOJI PRIKAZUJE UREDJAJE KOJI SU TRENUTNO VIDLJIVI A NE UPARENE


//        mBluetoothAdapter.startDiscovery();
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        getActivity().registerReceiver(mReceiver, filter);
//
//    }
//
//    @Override
//    public void onDestroy() {
//        getActivity().unregisterReceiver(mReceiver);
//        super.onDestroy();
//    }
//
//
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent
//                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                mDeviceList.add(device.getName() + "\n" + device.getAddress());
//
//
//                listView.setAdapter(new ArrayAdapter<String>(context,
//                        android.R.layout.simple_list_item_1, mDeviceList));
//            }
//        }
//    };


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    Toast.makeText(getContext(), "stigla poruka", Toast.LENGTH_SHORT).show();
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    msg_box.setText(tempMsg);
                    makeFriends(tempMsg);

                    break;
            }
            return true;
        }
    });

    private class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket socket = null;

            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1) {
            device = device1;

            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new BluetoothFragment.SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
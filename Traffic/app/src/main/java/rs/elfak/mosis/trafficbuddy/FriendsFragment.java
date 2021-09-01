package rs.elfak.mosis.trafficbuddy;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import rs.elfak.mosis.trafficbuddy.adapters.FriendsAdapter;
import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;


public class FriendsFragment extends Fragment {

    private FirebaseUser loggedInUser;
    private RecyclerView recyclerView;
    private FriendsAdapter adapter;
    private User user;
    private ArrayList<String> myFriends;
    private ArrayList<User> myFriendsUseri;

    private String uid;
    //private FloatingActionButton bluetoothSettings;
    private Button connectBluetooth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        //bluetoothSettings = view.findViewById(R.id.blutut);
        connectBluetooth = view.findViewById(R.id.connectBluetooth);
        connectBluetooth.setOnClickListener(l -> {
            BluetoothFragment nextFrag = new BluetoothFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map_host_fragment, nextFrag, "bluetooth")
                    .addToBackStack(null)
                    .commit();
        });


        //priprema za recyclerView
        adapter = new FriendsAdapter(getContext());
        recyclerView = view.findViewById(R.id.rv_friends);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        FirebaseUser loggedInUser = Firebase.getFirebaseAuth().getCurrentUser();
        if (loggedInUser != null) {
            String uid = loggedInUser.getUid();
            myFriendsUseri = new ArrayList<User>();
            Firebase.getDbRef().child(Firebase.DB_USERS).child(uid).get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            user = task.getResult().getValue(User.class);
                            if (user != null) {
                                this.myFriends = user.getFriends(); //lista stringova

                                for (String friendID : myFriends) {
                                    Firebase.getDbRef().child(Firebase.DB_USERS).child(friendID).get().addOnCompleteListener(t -> {

                                        User user = t.getResult().getValue(User.class);
                                        myFriendsUseri.add(user);
                                        adapter.setMyFriendsList(myFriendsUseri);

                                    });
                                }

                            }
                        }
                    }
            );
        }

        return view;
    }

}
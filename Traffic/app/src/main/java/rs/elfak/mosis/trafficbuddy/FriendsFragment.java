package rs.elfak.mosis.trafficbuddy;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import rs.elfak.mosis.trafficbuddy.adapters.FriendsAdapter;
import rs.elfak.mosis.trafficbuddy.data.User;


public class FriendsFragment extends Fragment {

    private FirebaseUser loggedInUser;
    private RecyclerView recyclerView;
    private FriendsAdapter adapter;
    private User user;
    private ArrayList<String> myFriends;
    private ArrayList<User> myFriendsUser;

    private String uid;
    private FloatingActionButton bluetoothSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        bluetoothSettings = view.findViewById(R.id.blutut);

        bluetoothSettings.setOnClickListener(l -> {
            BluetoothFragment nextFrag = new BluetoothFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map_host_fragment, nextFrag, "bluetooth")
                    .addToBackStack(null)
                    .commit();
        });


        //priprema za recyclerView
//        FirebaseUser loggedInUser = Firebase.getFirebaseAuth().getCurrentUser();
//        if (loggedInUser != null) {
//            String uid = loggedInUser.getUid();
//
//            Firebase.getDbRef().child(Firebase.DB_USERS).child(uid).get().addOnCompleteListener(task -> {
//                        if (!task.isSuccessful()) {
//                            Log.e("firebase", "Error getting data", task.getException());
//                        } else {
//                            user = task.getResult().getValue(User.class);
//                            if (user != null) {
//                                this.myFriends = user.getFriends();
//                                myFriendsUser = new ArrayList<User>(1);
//                                Integer currentNum;
//                                if (myFriends.size() == 0)
//                                    currentNum = 0;
//                                else
//                                    currentNum = myFriends.size();
//
//                                for (String friend : myFriends) {
//                                    Firebase.getDbRef().child(Firebase.DB_USERS).child(friend).get().addOnCompleteListener(t -> {
////                                            this.friends.add(currentNum, newFriend);
//                                        User user = t.getResult().getValue(User.class);
//                                        myFriendsUser.add(currentNum, user);
//                                    });
//                                }
//
//                                setupRV(myFriendsUser);
//
//                            }
//                        }
//                    }
//            );
//        }
//
//        recyclerView = view.findViewById(R.id.rv_friends);

        return view;
    }

    public void setupRV(ArrayList<User> myFriendsUser) {
        adapter = new FriendsAdapter(getContext(), this.myFriendsUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

    }
}
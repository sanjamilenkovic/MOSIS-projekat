package rs.elfak.mosis.trafficbuddy;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;


public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private User user;
    Button dugme;
    CircleImageView userPhoto;
    EditText userName;
    EditText userLastName;
    EditText userMail;
    EditText userRankPoints;
    EditText userPhone;
    private EditText userUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        dugme = view.findViewById(R.id.dugme);
        dugme.setOnClickListener(l -> {
            firebaseAuth.signOut();
            Toast.makeText(getContext(), "Izlogovan user", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), MainActivity.class));

        });
        userPhoto = view.findViewById(R.id.image_edit);
        userName = view.findViewById(R.id.edit_name);
        userLastName = view.findViewById(R.id.edit_last_name);
        userMail = view.findViewById(R.id.edit_mail);
        userPhone = view.findViewById(R.id.edit_phone);
        userRankPoints = view.findViewById(R.id.edit_rank);
        userUsername = view.findViewById(R.id.edit_username);

        // Inflate the layout for this fragment
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser loggedInUser = Firebase.getFirebaseAuth().getCurrentUser();
        if (loggedInUser != null) {
            String uid = loggedInUser.getUid();

            Firebase.getDbRef().child(Firebase.DB_USERS).child(uid).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    user = task.getResult().getValue(User.class);
                    if (user != null) {
                        Picasso.get().load(user.getImageUrl()).into(userPhoto);
                        userName.setText(user.getName());
                        userLastName.setText(user.getLastName());
                        userMail.setText(user.getEmail());
                        userRankPoints.setText(String.valueOf(user.getRankPoints()));
                        userPhone.setText(user.getPhoneNumber());
                        userUsername.setText(user.getUsername());

                    }
                }
            });

        }
        return view;
    }
}

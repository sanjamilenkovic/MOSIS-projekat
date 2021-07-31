package rs.elfak.mosis.trafficbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = Firebase.getFirebaseAuth();
        firebaseAuth.signInWithEmailAndPassword("petar@gmail.com", "petar123")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String uid = user.getUid();

                            User dbUser = new User();
                            dbUser.setUid(uid);
                            dbUser.setEmail("sanja@gmail.com");
                            dbUser.setName("SANJA");
                            dbUser.setUsername("Petar123");
                            dbUser.setPassword("petar123");

                            String key = Firebase.getDbRef().push().getKey();
                            dbUser.setUid(key);
                           Firebase.getDbRef().child(Firebase.DB_DISCOS).child(key).setValue(dbUser);

                            byte[] b =new byte[23];
                            Firebase.getStorageRef().child(Firebase.STORAGE_USER_PHOTOS).child("asd"+ "_" + new Date() + ".jpg").putBytes(b)
                                    .addOnFailureListener(e ->  {
                                        Toast.makeText(getApplicationContext(), "Couldn't upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                      //  signUpButton.setEnabled(true);
                                    })
                                    .addOnSuccessListener(ts -> {
                                        ts.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(dUrl -> {
                                            dbUser.setImageUrl(dUrl.toString());
                                            Firebase.getDbRef().child(Firebase.DB_USERS).child(uid).setValue(dbUser).addOnSuccessListener(unused -> {
                                            //    updateUI(user);
                                            });
                                        });
                                    });



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                           // signUpButton.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }

}
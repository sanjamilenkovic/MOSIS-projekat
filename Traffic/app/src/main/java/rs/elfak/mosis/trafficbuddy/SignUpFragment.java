package rs.elfak.mosis.trafficbuddy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;

import static android.content.ContentValues.TAG;


public class SignUpFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private Button signUpButton;
    private EditText passwordEditText;
    private EditText emailEditText;
    private EditText nameEditText;
    private EditText lastNameEditText;
    private EditText phoneEditText;
    private ImageView imageView;
    private EditText usernameEditText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = Firebase.getFirebaseAuth();


    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inflate the layout for this fragment
        passwordEditText = getView().findViewById(R.id.edit_password);
        emailEditText = getView().findViewById(R.id.edit_mail);
        nameEditText = getView().findViewById(R.id.edit_name);
        usernameEditText = getView().findViewById(R.id.edit_username);

        lastNameEditText = getView().findViewById(R.id.edit_last_name);
        phoneEditText = getView().findViewById(R.id.edit_phone);
        signUpButton = getView().findViewById(R.id.button_sign_up);
        ImageButton imageButton = getView().findViewById(R.id.image_button);
        imageView = getView().findViewById(R.id.image_edit);

        phoneEditText.addTextChangedListener(signUpTextWatcher);
        passwordEditText.addTextChangedListener(signUpTextWatcher);
        emailEditText.addTextChangedListener(signUpTextWatcher);
        nameEditText.addTextChangedListener(signUpTextWatcher);
        lastNameEditText.addTextChangedListener(signUpTextWatcher);

        signUpButton.setOnClickListener(signupButtonOnClickListener);
        imageButton.setOnClickListener(imageButtonOnClickListener);



    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }
    private final TextWatcher signUpTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String lastNameInput = lastNameEditText.getText().toString().trim();
            String passwordInput = passwordEditText.getText().toString().trim();
            String emailInput = emailEditText.getText().toString().trim();
            String usernameInput = usernameEditText.getText().toString().trim();
            String nameInput = nameEditText.getText().toString().trim();
            String phoneNumberInput = phoneEditText.getText().toString().trim();


            signUpButton.setEnabled(!lastNameInput.isEmpty() && !phoneNumberInput.isEmpty()
                    && !usernameInput.isEmpty()
                    && !passwordInput.isEmpty()
                    && !emailInput.isEmpty()
                    && !nameInput.isEmpty()
                    && passwordInput.length() > 6);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }
    final ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Object d = extras.get("data");
                        if (d != null) {
                            Bitmap img = (Bitmap)d;
                            imageView.setImageBitmap(img);
                        }
                    }
                }
            });

    private final View.OnClickListener imageButtonOnClickListener = v -> {
        //dodati permisiju
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        mGetContent.launch(intent);
    };


    private final View.OnClickListener signupButtonOnClickListener = v -> {
        signUpButton.setEnabled(false);

        String lastName = lastNameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String username = usernameEditText.getText().toString();

        Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String uid = user.getUid();

                            User dbUser = new User();
                            dbUser.setUid(uid);
                            dbUser.setEmail(email);
                            dbUser.setName(name);
                            dbUser.setLastName(lastName);
                            dbUser.setPhoneNumber(phone);
                            dbUser.setPassword(password);
                            dbUser.setUsername(username);

                           Firebase.getStorageRef().child(Firebase.STORAGE_USER_PHOTOS).child(email + "_" + new Date() + ".jpg").putBytes(imageBytes)
                                    .addOnFailureListener(e ->  {
                                        Toast.makeText(getActivity(), "Couldn't upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        signUpButton.setEnabled(true);
                                    })
                                    .addOnSuccessListener(ts -> {
                                        ts.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(dUrl -> {
                                            dbUser.setImageUrl(dUrl.toString());
                                            Firebase.getDbRef().child(Firebase.DB_USERS).child(uid).setValue(dbUser).addOnSuccessListener(unused -> {
                                                Toast.makeText(getActivity(), "Uspesno ste se registrovali",
                                                        Toast.LENGTH_SHORT).show();
                                                SignInFragment nextFrag = new SignInFragment();

                                                getActivity().getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.nav_host_fragment, nextFrag, "bluetooth")
                                                        .addToBackStack(null)
                                                        .commit();
                                                FirebaseAuth current = FirebaseAuth.getInstance();
                                                current.signOut();
                                                });


                                        });
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            signUpButton.setEnabled(true);
                            Toast.makeText(getActivity(), "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    };

}
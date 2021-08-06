package rs.elfak.mosis.trafficbuddy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import rs.elfak.mosis.trafficbuddy.utils.Firebase;

import static android.content.ContentValues.TAG;


public class StartFragment extends Fragment implements View.OnClickListener {

    Button singUpBtn;
    private FirebaseAuth firebaseAuth;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = Firebase.getFirebaseAuth();
    }
    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_start, container, false);
        singUpBtn = view.findViewById(R.id.button_sign_up);
        singUpBtn.setOnClickListener(this);

        return view;
    }
    @Override
    public void onClick(View view) {
        Navigation.findNavController(view).navigate(R.id.action_start_dest_to_signUp_dest);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailEditText = getView().findViewById(R.id.edit_mail);
        passwordEditText = getView().findViewById(R.id.edit_password);
        signInButton = getView().findViewById(R.id.button_sign_in);

        // listeners
        emailEditText.addTextChangedListener(signUpTextWatcher);
        passwordEditText.addTextChangedListener(signUpTextWatcher);

        emailEditText.setText("sanja@mail.com");
        passwordEditText.setText("sanja123");
        signInButton.setOnClickListener(signInButtonOnClickListener);
    }

    private final TextWatcher signUpTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailInput = emailEditText.getText().toString().trim();
            String passwordInput = passwordEditText.getText().toString().trim();

            signInButton.setEnabled(!emailInput.isEmpty()
                    && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private final View.OnClickListener signInButtonOnClickListener = v -> {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String uid = user.getUid();
                            Toast.makeText(getActivity(), "Uspesno ste se ulogovali "+uid ,
                                    Toast.LENGTH_SHORT).show();
                            //dodati shared prefferences i otvaranje novog activitija
                            Intent intent = new Intent(getActivity(), MapActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    };

}
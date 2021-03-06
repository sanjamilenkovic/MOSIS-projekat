package rs.elfak.mosis.trafficbuddy.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import rs.elfak.mosis.trafficbuddy.R;
import rs.elfak.mosis.trafficbuddy.adapters.AddReportItemAdapter;
import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;

public class AddReportDialog extends Dialog implements AddReportItemAdapter.IconClickListener {

    public final Activity c;
    public Dialog d;

    private final LatLng latLng;
    private Report newReport;



    public AddReportDialog(Activity a, Bundle b) {
        super(a);
        this.c = a;
        latLng = b.getParcelable("currentLocation");
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_report);

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_report_image_holder);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);

        newReport = new Report();

        AddReportItemAdapter adapter = new AddReportItemAdapter(getContext());
        adapter.setIconClickListener(this);
        recyclerView.setAdapter(adapter);


        EditText editTitle = findViewById(R.id.edit_text_title);
        EditText editDescription = findViewById(R.id.edit_text_description);
        EditText editLon = findViewById(R.id.edit_text_lon);
        EditText editLat = findViewById(R.id.edit_text_lat);
        Button buttonAddReport = findViewById(R.id.btn_add_report);

        editLon.setText(latLng.longitude + "");
        editLon.setEnabled(false);
        editLat.setText(latLng.latitude + "");
        editLat.setEnabled(false);


        buttonAddReport.setOnClickListener(btn -> {
            FirebaseUser loggedInUser = Firebase.getFirebaseAuth().getCurrentUser();
            if (loggedInUser != null) {
                String currentUserId = loggedInUser.getUid();

                Firebase.getDbRef().child(Firebase.DB_USERS).child(currentUserId).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(c, "Error", Toast.LENGTH_SHORT).show();
                        btn.setEnabled(true);
                    } else {

//                        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
//
//                        String now = ISO_8601_FORMAT.format(new Date());

                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                        newReport.setDate(currentDate);
                        newReport.setTime(currentTime);
                        newReport.setDescription(String.valueOf(editDescription.getText()));
                        newReport.setTitle(String.valueOf(editTitle.getText()));
                        newReport.setLat(latLng.latitude);
                        newReport.setLon(latLng.longitude);
                        newReport.setReportedById(currentUserId);


                        String key = Firebase.getDbRef().push().getKey();
                        newReport.setId(key);
                        Firebase.getDbRef().child(Firebase.DB_REPORTS).child(key).setValue(newReport);
                        Toast.makeText(c, "Dodat report", Toast.LENGTH_SHORT).show();
                        dismiss();
                        //update ui ovde!!!
                        }

                });
            }
        });

    }

    @Override
    public void onIconClick(String naziv) {
        newReport.setIconTitle(naziv);
    }

}

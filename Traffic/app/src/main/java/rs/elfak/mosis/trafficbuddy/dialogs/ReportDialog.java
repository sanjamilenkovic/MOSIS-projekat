package rs.elfak.mosis.trafficbuddy.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import rs.elfak.mosis.trafficbuddy.R;
import rs.elfak.mosis.trafficbuddy.data.Rating;
import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;

public class ReportDialog extends Dialog {
    public final Activity c;
    private final Report currentReport;
    public Dialog d;
    private User currentUser;
    private Boolean liked = false;
    private Rating currentRating = null;
    TextView userReported;

    public ReportDialog(Activity a, Report r) {
        super(a);
        this.c = a;
        this.currentReport = r;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_current_report);

        getCurrentUser();

        TextView title = findViewById(R.id.reportDetailsTitle);
        title.setText(currentReport.getTitle());

        TextView description = findViewById(R.id.edit_description);
        description.setText(currentReport.getDescription());

        //slika
        ImageView image = findViewById(R.id.reportDetailsImage);
        Resources resources = c.getResources();
        final int resourceId = resources.getIdentifier(currentReport.getIconTitle(), "drawable",
                c.getPackageName());

        Drawable d = resources.getDrawable(resourceId);
        image.setImageDrawable(d);


        FloatingActionButton deleteMyReport = findViewById(R.id.deleteReport);
        if(currentReport.getReportedById().equals(Firebase.getFirebaseAuth().getCurrentUser().getUid()))
            deleteMyReport.show();

        deleteMyReport.setOnClickListener(l -> {
            Firebase.getDbRef().child(Firebase.DB_REPORTS).child(currentReport.getId()).removeValue();
            dismiss();
        });

        TextView lat = findViewById(R.id.edit_text_lat);
        lat.setText(currentReport.getLat() + "");

        TextView lon = findViewById(R.id.edit_text_lon);
        lon.setText(currentReport.getLon() + "");

        TextView date_time = findViewById(R.id.edit_added_date_time);
        if(currentReport.getDate() != null)
            date_time.setText(currentReport.getDate());
        else
            date_time.setText("24-04-99");

        if(currentReport.getTime() != null)
            date_time.append(" " + currentReport.getTime());
        else
            date_time.append(" 15:30");


        userReported = findViewById(R.id.edit_reportedBy);


        FirebaseUser loggedInUser = Firebase.getFirebaseAuth().getCurrentUser();
        String currentUserId = loggedInUser.getUid();
        ImageView thumbUp = findViewById(R.id.thumb_up);

        Firebase.getDbRef().child("ratings").get().addOnSuccessListener(it -> {
            for (DataSnapshot rat : it.getChildren()) {
                Rating rating = rat.getValue(Rating.class);
                if (rating.getRatedById().equals(currentUserId) && rating.getRatedReportId().equals(currentReport.getId())) {
                    currentRating = rating;
                    Toast.makeText(getContext(), rating.toString(), Toast.LENGTH_SHORT).show();
                    liked = true;
                    thumbUp.setColorFilter(c.getResources().getColor(R.color.light_green));
                }
            }
            ;
        });


        thumbUp.setOnClickListener(l -> {
            getCurrentUser();
            liked = !liked;
            if (liked) {
                thumbUp.setColorFilter(c.getResources().getColor(R.color.light_green));


                Rating newRating = new Rating();
                newRating.addLike();
                newRating.setRatedReportId(currentReport.getId());


                newRating.setRatedById(currentUserId);
                String key = Firebase.getDbRef().push().getKey();
                newRating.setId(key);
                currentRating = newRating;

                Firebase.getDbRef().child("ratings").child(key).setValue(newRating).addOnSuccessListener(it -> {

                    Firebase.getDbRef().child("users").child(currentReport.getReportedById()).child("rankPoints").setValue(currentUser.getRankPoints() + 1);
                });

            } else {
                thumbUp.setColorFilter(c.getResources().getColor(R.color.white));
                if (currentRating != null) {
                    Firebase.getDbRef().child("ratings").child(currentRating.getId()).removeValue().addOnSuccessListener(it -> {

                        Firebase.getDbRef().child("users").child(currentReport.getReportedById()).child("rankPoints").setValue(currentUser.getRankPoints() - 1);

                    });
                }
            }
        });
    }

    private void getCurrentUser() {
        Firebase.getDbRef().child(Firebase.DB_USERS).child(currentReport.getReportedById()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                currentUser = task.getResult().getValue(User.class);

                if (currentUser != null) {
                    userReported.setText(currentUser.getName());
                }
            }


        });
    }
}


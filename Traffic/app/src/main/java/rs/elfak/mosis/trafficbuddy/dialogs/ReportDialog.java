package rs.elfak.mosis.trafficbuddy.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import rs.elfak.mosis.trafficbuddy.R;
import rs.elfak.mosis.trafficbuddy.data.Rating;
import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;

public class ReportDialog extends Dialog {
    public final Activity c;
    private Report currentReport;
    public Dialog d;
    private Boolean liked = false;

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


        TextView lat = findViewById(R.id.edit_text_lat);
        lat.setText(currentReport.getLat() + "");

        TextView lon = findViewById(R.id.edit_text_lon);
        lon.setText(currentReport.getLon() + "");

        TextView userReported = findViewById(R.id.edit_reportedBy);

        String reportedById = currentReport.getReportedById();
        Firebase.getDbRef().child(Firebase.DB_USERS).child(reportedById).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                User user = task.getResult().getValue(User.class);
                if (user != null) {
                    userReported.setText(user.getName());
                }
            }

        });

        ImageView thumbUp = findViewById(R.id.thumb_up);
        thumbUp.setOnClickListener(l -> {
            liked = !liked;
            if (liked) {
                thumbUp.setColorFilter(c.getResources().getColor(R.color.light_green));


                Rating newRating = new Rating();
                newRating.addLike();
                newRating.setRatedReportId(currentReport.getId());

                FirebaseUser loggedInUser = Firebase.getFirebaseAuth().getCurrentUser();
                String currentUserId = loggedInUser.getUid();

                newRating.setRatedById(currentUserId);
                String key = Firebase.getDbRef().push().getKey();
                newRating.setId(key);


                Firebase.getDbRef().child(Firebase.DB_REPORTS).child(key).setValue(newRating);

            } else
                thumbUp.setColorFilter(c.getResources().getColor(R.color.white));

        });
    }
}


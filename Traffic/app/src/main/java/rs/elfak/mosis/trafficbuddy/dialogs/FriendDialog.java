package rs.elfak.mosis.trafficbuddy.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import rs.elfak.mosis.trafficbuddy.R;
import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.data.User;

public class FriendDialog extends Dialog {
    public final Activity c;
    private User currentFriend;
    CircleImageView userPhoto;
    EditText userMail;
    EditText userRankPoints;
    EditText userPhone;
    private TextView nameLastname;
    private TextView usernameNew;
    private FloatingActionButton fabShowReports;


    public FriendDialog(Activity a, User f) {
        super(a);
        this.c = a;
        this.currentFriend = f;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_friend);


        nameLastname = findViewById(R.id.logo);
        usernameNew = findViewById(R.id.username);
        userPhoto = findViewById(R.id.image_edit);
        userMail = findViewById(R.id.edit_mail);
        userPhone = findViewById(R.id.edit_phone);
        userRankPoints = findViewById(R.id.edit_rank);

        Picasso.get().load(currentFriend.getImageUrl()).into(userPhoto);
        nameLastname.setText(currentFriend.getName() + " " + currentFriend.getLastName());
        usernameNew.setText("@" + currentFriend.getUsername());
        userMail.setText(currentFriend.getEmail());
        userRankPoints.setText(String.valueOf(currentFriend.getRankPoints()));
        userPhone.setText(currentFriend.getPhoneNumber());

    }
}
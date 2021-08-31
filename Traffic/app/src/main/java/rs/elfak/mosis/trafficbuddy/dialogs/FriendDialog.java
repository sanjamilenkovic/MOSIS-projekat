package rs.elfak.mosis.trafficbuddy.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import rs.elfak.mosis.trafficbuddy.R;
import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.data.User;

public class FriendDialog extends Dialog {
    public final Activity c;
    private User currentFriend;
    CircleImageView userPhoto;
    EditText userName;
    EditText userLastName;
    EditText userMail;
    EditText userRankPoints;
    EditText userPhone;
    private EditText userUsername;


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


        userPhoto = findViewById(R.id.image_edit);
        userName = findViewById(R.id.edit_name);
        userLastName = findViewById(R.id.edit_last_name);
        userMail = findViewById(R.id.edit_mail);
        userPhone = findViewById(R.id.edit_phone);
        userRankPoints = findViewById(R.id.edit_rank);
        userUsername = findViewById(R.id.edit_username);

        Picasso.get().load(currentFriend.getImageUrl()).into(userPhoto);
        userName.setText(currentFriend.getName());
        userLastName.setText(currentFriend.getLastName());
        userMail.setText(currentFriend.getEmail());
        userRankPoints.setText(String.valueOf(currentFriend.getRankPoints()));
        userPhone.setText(currentFriend.getPhoneNumber());
        userUsername.setText(currentFriend.getUsername());

    }
}
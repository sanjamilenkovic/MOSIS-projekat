package rs.elfak.mosis.trafficbuddy.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import rs.elfak.mosis.trafficbuddy.FriendsFragment;
import rs.elfak.mosis.trafficbuddy.R;
import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.dialogs.FriendDialog;
import rs.elfak.mosis.trafficbuddy.dialogs.ReportDialog;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.Holder> {

    private ArrayList<User> myFriends = new ArrayList<>(0);
    private final LayoutInflater mInflater;


    public FriendsAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        User u = new User();
        u.setName("Sanja");
        u.setLastName("Milenkovic");
        u.setUsername("sanja");
        myFriends.add(u);

        u.setName("Petar");
        u.setLastName("Djordjevic");
        u.setUsername("petar");
        myFriends.add(u);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_item_friend, parent, false);
        return new FriendsAdapter.Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.imePrezime.setText(myFriends.get(position).getName() + " " + myFriends.get(position).getLastName());
        holder.username.setText("@" + myFriends.get(position).getUsername());
        Picasso.get().load(myFriends.get(position).getImageUrl()).into(holder.profilePicture);
        holder.imePrezime.setTag(position);

    }

    @Override
    public int getItemCount() {
        return myFriends.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView profilePicture;
        final TextView imePrezime;
        final TextView username;
        private Integer currentFriendPosition;


        public Holder(@NonNull View itemView) {
            super(itemView);

            profilePicture = itemView.findViewById(R.id.profilePicture);
            imePrezime = itemView.findViewById(R.id.imePrezime);
            username = itemView.findViewById(R.id.username);

            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            currentFriendPosition = (Integer) imePrezime.getTag();
            User currentFriend = myFriends.get(currentFriendPosition);

            FriendDialog fd = new FriendDialog((Activity) v.getContext(), currentFriend);
            fd.show();
        }
    }

    public void setMyFriendsList(ArrayList<User> f)
    {
        myFriends.clear();
        myFriends.addAll(f);
        notifyDataSetChanged();
    }
}

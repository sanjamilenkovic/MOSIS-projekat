package rs.elfak.mosis.trafficbuddy.adapters;

import android.annotation.SuppressLint;
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
import rs.elfak.mosis.trafficbuddy.data.User;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.Holder> {

    private ArrayList<User> myFriends = new ArrayList<>(0);
    private final LayoutInflater mInflater;


    public FriendsAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_item_my_reports, parent, false);
        return new FriendsAdapter.Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.imePrezime.setText(myFriends.get(position).getName() + " " + myFriends.get(position).getLastName());
        Picasso.get().load(myFriends.get(position).getImageUrl()).into(holder.profilePicture);

    }

    @Override
    public int getItemCount() {
        return myFriends.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        final ImageView profilePicture;
        final TextView imePrezime;


        public Holder(@NonNull View itemView) {
            super(itemView);

            profilePicture = itemView.findViewById(R.id.profilePicture);
            imePrezime = itemView.findViewById(R.id.imePrezime);
        }
    }
}

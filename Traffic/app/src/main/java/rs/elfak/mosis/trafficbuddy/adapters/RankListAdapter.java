package rs.elfak.mosis.trafficbuddy.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import rs.elfak.mosis.trafficbuddy.FriendsFragment;
import rs.elfak.mosis.trafficbuddy.R;
import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;

public class RankListAdapter extends RecyclerView.Adapter<RankListAdapter.Holder> {

    private ArrayList<User> rankList;
    private final LayoutInflater mInflater;


    public RankListAdapter(Context context, ArrayList<User> my) {
        this.rankList = my;
        this.mInflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_item_rank_list, parent, false);
        return new RankListAdapter.Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.user_name.setText(rankList.get(position).getName() + " " + rankList.get(position).getLastName());
        Picasso.get().load(rankList.get(position).getImageUrl()).into(holder.image_user);
        holder.rank_points.setText(String.valueOf(rankList.get(position).getRankPoints()));
//        if(rankList.get(position).getUid().equals(Firebase.getFirebaseAuth().getCurrentUser().getUid()))
//        {
//            holder.back.setBackgroundColor(Color.parseColor("#000000"));
//        }

    }

    @Override
    public int getItemCount() {
        return rankList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        final ImageView image_user;
        final TextView user_name;
        final TextView rank_points;
        final ConstraintLayout back;



        public Holder(@NonNull View itemView) {
            super(itemView);

            image_user = itemView.findViewById(R.id.image_user);
            user_name = itemView.findViewById(R.id.user_name);
            back = itemView.findViewById(R.id.back);
            rank_points = itemView.findViewById(R.id.rank_points);

        }
    }
}

package rs.elfak.mosis.trafficbuddy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.core.Repo;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import rs.elfak.mosis.trafficbuddy.R;
import rs.elfak.mosis.trafficbuddy.data.Report;

public class MyReportsAdapter extends RecyclerView.Adapter<MyReportsAdapter.ImageHolder> {

    private LayoutInflater mInflater;
    private List<Report> list;

    // data is passed into the constructor
    public MyReportsAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        list = new ArrayList<Report>(3);

        Report r = new Report();
        r.setIcon(R.drawable.sudar);
        r.setTitle("Sudar");
        list.add(r);


    }
    // stores and recycles views as they are scrolled off screen
    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView myReportImageView;
        TextView myReportTextView;

        ImageHolder(View itemView) {
            super(itemView);
            myReportImageView = itemView.findViewById(R.id.image_report);
            myReportTextView = itemView.findViewById(R.id.report_name);

//            itemView.setOnClickListener(im ->{
//                //ovde ide open reporta
//            });
        }

        @Override
        public void onClick(View v) {

        }
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_item_my_reports, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        holder.myReportImageView.setImageResource(list.get(position).getIcon());
        holder.myReportTextView.setText(list.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
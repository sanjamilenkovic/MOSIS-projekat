package rs.elfak.mosis.trafficbuddy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rs.elfak.mosis.trafficbuddy.R;
import rs.elfak.mosis.trafficbuddy.data.Report;

public class MyReportsAdapter extends RecyclerView.Adapter<MyReportsAdapter.ImageHolder> {

    private final LayoutInflater mInflater;
    private final List<Report> list  = new ArrayList<>();
    private List<Report> myReports;
    private final List<Report> allReports = new ArrayList<>();

    // data is passed into the constructor
    public MyReportsAdapter(Context context,List<Report> rs,List<Report> my) {
        this.mInflater = LayoutInflater.from(context);
        myReports = my;
        list.addAll(rs);
        allReports.addAll(rs);
    }
    // stores and recycles views as they are scrolled off screen
    public static class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView myReportImageView;
        final TextView myReportTextView;

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
       // holder.myReportImageView.setImageResource(list.get(position).getIcon());
       holder.myReportTextView.setText(list.get(position).getTitle());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateReportsList(Boolean flag){
        if (flag) {
            list.clear();
            list.addAll(myReports);
        }
        else{
            list.clear();
            list.addAll(allReports);
        }
        notifyDataSetChanged();
    }

}
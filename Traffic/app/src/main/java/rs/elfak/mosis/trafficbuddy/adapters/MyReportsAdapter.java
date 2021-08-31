package rs.elfak.mosis.trafficbuddy.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import rs.elfak.mosis.trafficbuddy.R;
import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.dialogs.ReportDialog;

public class MyReportsAdapter extends RecyclerView.Adapter<MyReportsAdapter.ImageHolder> {

    private final LayoutInflater mInflater;
    private final Context c;
    private Integer currentPlace;
    private final List<Report> list = new ArrayList<>();
    private List<Report> myReports;
    private final List<Report> allReports = new ArrayList<>();
//    private MyReportsAdapter.ReportClickListener reportClickListener;

    // data is passed into the constructor
    public MyReportsAdapter(Context context, List<Report> rs, List<Report> my) {
        this.c = context;
        this.mInflater = LayoutInflater.from(context);
        myReports = my;
        list.addAll(rs);
        allReports.addAll(rs);
    }

//    public void setReportClickListener(ReportClickListener i) {
//        reportClickListener = i;
//    }


    // stores and recycles views as they are scrolled off screen
    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView myReportImageView;
        final TextView myReportTextView;

        ImageHolder(View itemView) {
            super(itemView);
            myReportImageView = itemView.findViewById(R.id.image_report);
            myReportTextView = itemView.findViewById(R.id.report_name);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
//            AppCompatActivity activity = (AppCompatActivity) v.getContext();
//            ReportDetailsFragment reportDetailsFragment = new ReportDetailsFragment();
//            activity.getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.map_host_fragment, reportDetailsFragment)
//                    .addToBackStack(null).commit();

            //reportClickListener.onReportClick();

            currentPlace = (Integer) myReportTextView.getTag();

            Report currentReport = list.get(currentPlace);

            ReportDialog rd = new ReportDialog((Activity) v.getContext(), currentReport);
            rd.show();


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

        if (list.get(position).getIconTitle() != null) {
            Resources resources = c.getResources();
            final int resourceId = resources.getIdentifier(list.get(position).getIconTitle(), "drawable",
                    c.getPackageName());

            Drawable d = resources.getDrawable(resourceId);
            holder.myReportImageView.setImageDrawable(d);
        } else
            holder.myReportImageView.setImageResource(R.drawable.oprez);


        holder.myReportTextView.setText(list.get(position).getTitle());
        holder.myReportTextView.setTag(position);

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateReportsList(Boolean flag) {
        if (flag) {
            list.clear();
            list.addAll(myReports);
        } else {
            list.clear();
            list.addAll(allReports);
        }
        notifyDataSetChanged();
    }

    // parent activity will implement this method to respond to click events
//    public interface ReportClickListener {
//        void onReportClick();
//    }
}
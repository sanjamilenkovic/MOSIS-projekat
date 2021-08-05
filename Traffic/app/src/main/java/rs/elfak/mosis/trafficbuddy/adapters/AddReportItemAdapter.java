package rs.elfak.mosis.trafficbuddy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rs.elfak.mosis.trafficbuddy.R;

public class AddReportItemAdapter extends RecyclerView.Adapter<AddReportItemAdapter.ImageHolder> {

    private Integer selectedPosition = -1;
    private LayoutInflater mInflater;
    private List<Integer> list;
    private IconClickListener iconClickListener;

    // data is passed into the constructor
    public AddReportItemAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        list = new ArrayList<Integer>(2);
        list.add(R.drawable.guzva);
        list.add(R.drawable.sudar);
        list.add(R.drawable.radovi);
        list.add(R.drawable.kamera);
        list.add(R.drawable.zatvoren);
        list.add(R.drawable.oprez);
    }
    // stores and recycles views as they are scrolled off screen
    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView myImageView;

        ImageHolder(View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.image1);

            myImageView.setOnClickListener(im ->{

                Integer position = (Integer) myImageView.getTag();
                notifyDataSetChanged();
                selectedPosition = (Integer) position;//dobro selektuje poziciju samo treba da se prosledi i upise u bazu
                iconClickListener.onIconClick(selectedPosition);
            });
        }

        @Override
        public void onClick(View v) {

        }
    }
    public void setIconClickListener(IconClickListener i)
    {
        iconClickListener = i;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_item_report_image, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        if (position == selectedPosition) {
            holder.myImageView.setAlpha((float) 0.35);
        } else
            holder.myImageView.setAlpha((float) 1.0);
        holder.myImageView.setImageResource(list.get(position));
        holder.myImageView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    // parent activity will implement this method to respond to click events
    public interface IconClickListener {
        void onIconClick(int position);
    }
}
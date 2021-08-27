package rs.elfak.mosis.trafficbuddy.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rs.elfak.mosis.trafficbuddy.R;

public class AddReportItemAdapter extends RecyclerView.Adapter<AddReportItemAdapter.ImageHolder> {

    private Integer selectedPosition = -1;
    private final LayoutInflater mInflater;
    private final Context c;
    private final List<String> listaImena;
    private IconClickListener iconClickListener;

    // data is passed into the constructor
    public AddReportItemAdapter(Context context) {

        this.c = context;
        this.mInflater = LayoutInflater.from(context);
        listaImena = new ArrayList<String>(2);

        listaImena.add("guzva");
        listaImena.add("sudar");
        listaImena.add("radovi");
        listaImena.add("kamera");
        listaImena.add("zatvoren");
        listaImena.add("oprez");
    }

    // stores and recycles views as they are scrolled off screen
    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView myImageView;

        @RequiresApi(api = Build.VERSION_CODES.Q)
        ImageHolder(View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.image1);

            myImageView.setOnClickListener(im -> {
                String nazivDrawable = (String) myImageView.getTag();
                for (int i=0; i<5; i++)
                {
                    if (nazivDrawable.equals(listaImena.get(i)))
                    {
                        selectedPosition = i;
                    }
                }
                notifyDataSetChanged();

                iconClickListener.onIconClick(nazivDrawable);
            });
        }

        @Override
        public void onClick(View v) {

        }
    }

    public void setIconClickListener(IconClickListener i) {
        iconClickListener = i;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
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



        Resources resources = c.getResources();
        final int resourceId = resources.getIdentifier(listaImena.get(position), "drawable",
                c.getPackageName());
        Drawable d = resources.getDrawable(resourceId);
        holder.myImageView.setImageDrawable(d);


        holder.myImageView.setTag(listaImena.get(position));
    }

    @Override
    public int getItemCount() {
        return listaImena.size();
    }


    // parent activity will implement this method to respond to click events
    public interface IconClickListener {
        void onIconClick(String naziv);
    }


}
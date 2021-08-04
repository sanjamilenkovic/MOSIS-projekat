package rs.elfak.mosis.trafficbuddy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rs.elfak.mosis.trafficbuddy.adapters.AddReportItemAdapter;
import rs.elfak.mosis.trafficbuddy.adapters.MyReportsAdapter;
import rs.elfak.mosis.trafficbuddy.data.Report;


public class ReportsFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyReportsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports, container, false);


        // set up the RecyclerView
        recyclerView = view.findViewById(R.id.rv_report_image_holder);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);

        //set adapter for RecyclerView
        adapter = new MyReportsAdapter(getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }
}
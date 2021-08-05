package rs.elfak.mosis.trafficbuddy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import rs.elfak.mosis.trafficbuddy.adapters.AddReportItemAdapter;
import rs.elfak.mosis.trafficbuddy.adapters.MyReportsAdapter;
import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;
import rs.elfak.mosis.trafficbuddy.viewmodel.ReportsViewModel;


public class ReportsFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyReportsAdapter adapter;
    private ReportsViewModel viewModel;
    private Boolean flag = false;

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
        Button butt = view.findViewById(R.id.myRep);
        butt.setOnClickListener(e -> {
            flag = !flag;
            adapter.updateReportsList(flag);
        });
        recyclerView = view.findViewById(R.id.rv_report_image_holder);
        viewModel = new ViewModelProvider(this).get(ReportsViewModel.class);
        viewModel.getReports().observe(getViewLifecycleOwner(), listReports);
        return view;
    }

    Observer<List<Report>> listReports = new Observer<List<Report>>() {
        @Override
        public void onChanged(List<Report> userArrayList) {
            List<Report> myReports = new ArrayList<Report>();
            for (int i = 0; i < userArrayList.size(); i++) {
                if (userArrayList.get(i).getReportedById().equals(FirebaseAuth.getInstance().getUid())) {
                    myReports.add(userArrayList.get(i));
                }
            }
            adapter = new MyReportsAdapter(getContext(), userArrayList, myReports);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
    };

}
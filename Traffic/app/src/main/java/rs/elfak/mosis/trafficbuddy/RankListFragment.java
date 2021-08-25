package rs.elfak.mosis.trafficbuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rs.elfak.mosis.trafficbuddy.adapters.MyReportsAdapter;
import rs.elfak.mosis.trafficbuddy.adapters.RankListAdapter;
import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.viewmodel.ReportsViewModel;

public class RankListFragment extends Fragment {

    private RecyclerView recyclerView;
    private RankListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rank_list, container, false);
        recyclerView = view.findViewById(R.id.rv_rank_list);
        ReportsViewModel viewModel = new ViewModelProvider(this).get(ReportsViewModel.class);
        viewModel.getAllUsers().observe(getViewLifecycleOwner(), listUsers);
        return view;
    }

    final Observer<List<User>> listUsers = new Observer<List<User>>() {
        @Override
        public void onChanged(List<User> userArrayList) {
            ArrayList<User> myReports = new ArrayList<>();
            for (int i = 0; i < userArrayList.size(); i++) {
                 {

                    myReports.add(userArrayList.get(i));
                }
            }
            Collections.sort(myReports);
            adapter = new RankListAdapter(getContext(), myReports);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
    };
}


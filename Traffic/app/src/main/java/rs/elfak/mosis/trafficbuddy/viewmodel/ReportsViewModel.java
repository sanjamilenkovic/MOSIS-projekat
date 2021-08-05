package rs.elfak.mosis.trafficbuddy.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;

public class ReportsViewModel extends ViewModel {

    MutableLiveData<List<Report>> reports = new MutableLiveData<List<Report>>();
    private DatabaseReference reportDbRef;

    public ReportsViewModel() {
        reportDbRef = Firebase.getDbRef();
    }

    public LiveData<List<Report>> getReports() {
        if (reports.getValue() == null) {
            reportDbRef.child(Firebase.DB_REPORTS).addValueEventListener(reportValueListener);
        }
        return reports;
    }

    ValueEventListener reportValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                reports.postValue(toReports(snapshot));
            }
        }

        private List<Report> toReports(DataSnapshot snapshot) {
            List<Report> reps = new ArrayList<>();
            for (DataSnapshot discoSnapshot : snapshot.getChildren()) {
                Report rep = discoSnapshot.getValue(Report.class);
                reps.add(rep);
            }

            return reps;
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


}

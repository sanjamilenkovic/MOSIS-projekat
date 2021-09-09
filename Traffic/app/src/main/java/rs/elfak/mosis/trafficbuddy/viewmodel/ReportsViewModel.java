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
import java.util.List;
import rs.elfak.mosis.trafficbuddy.data.Report;
import rs.elfak.mosis.trafficbuddy.data.User;
import rs.elfak.mosis.trafficbuddy.utils.Firebase;

public class ReportsViewModel extends ViewModel {

    final MutableLiveData<List<Report>> reports = new MutableLiveData<List<Report>>();
    final MutableLiveData<List<User>> allUsers = new MutableLiveData<>();
    private final DatabaseReference reportDbRef;

    public ReportsViewModel() {
        reportDbRef = Firebase.getDbRef();
    }

    public LiveData<List<Report>> getReports() {
        if (reports.getValue() == null) {
            reportDbRef.child(Firebase.DB_REPORTS).addValueEventListener(reportValueListener);
        }
        return reports;
    }


    public LiveData<List<User>> getAllUsers() {

        if (allUsers.getValue() == null) {
            reportDbRef.child(Firebase.DB_USERS).addValueEventListener(userEventListener);
            }
        return allUsers;
        }


    final ValueEventListener userEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

            if (snapshot.exists()) {
                allUsers.postValue(toUsers(snapshot));
            }
        }
        private List<User> toUsers(DataSnapshot snapshot) {
            List<User> uss = new ArrayList<>();
            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                User rep = userSnapshot.getValue(User.class);
                uss.add(rep);
            }

            return uss;
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    };

    final ValueEventListener reportValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                reports.postValue(toReports(snapshot));
            }
        }

        private List<Report> toReports(DataSnapshot snapshot) {
            List<Report> reps = new ArrayList<>();
            for (DataSnapshot reportSnapshot : snapshot.getChildren()) {
                Report rep = reportSnapshot.getValue(Report.class);
                reps.add(rep);
            }

            return reps;
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


}

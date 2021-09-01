package rs.elfak.mosis.trafficbuddy.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import java.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.TimeZone;

import rs.elfak.mosis.trafficbuddy.R;
import rs.elfak.mosis.trafficbuddy.adapters.AddReportItemAdapter;

public class FilterDialog extends Dialog implements AddReportItemAdapter.IconClickListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {

    public final Activity c;
    private FilterClickListener filterClickListener;

    EditText _editText;
    EditText radius;
    private int _day;
    private int _month;
    private int _birthYear;

    //    private final LatLng latLng;
    private String reportType;
    private Calendar myCalendar;
    private String newDate;


    public FilterDialog(Activity a) {
        super(a);
        this.c = a;

//        latLng = b.getParcelable("currentLocation");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_filter);

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_report_image_holder);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);


        AddReportItemAdapter adapter = new AddReportItemAdapter(getContext());
        adapter.setIconClickListener(this);
        recyclerView.setAdapter(adapter);

        Button buttonFilterReports = findViewById(R.id.btn_filter_reports);
        _editText = findViewById(R.id.date);
        _editText.setOnClickListener(this);

        //radius = findViewById(R.id.radius);

        buttonFilterReports.setOnClickListener(l ->
        {

            filterClickListener.onIconClick(reportType, newDate);
            dismiss();

        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _birthYear = year;
        _month = monthOfYear;
        _day = dayOfMonth;
        updateDisplay();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(c, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();

    }

    // updates the date in the birth date EditText
    private void updateDisplay() {

        _editText.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(_day).append("-").append("0" + (_month + 1)).append("-").append(_birthYear).append(" "));

        newDate = _editText.getText().toString();
    }

    public void setFilterClickListener(FilterClickListener i) {
        filterClickListener = i;
    }


    @Override
    public void onIconClick(String naziv) {
        reportType = naziv;
    }

    public interface FilterClickListener {
        void onIconClick(String title, String date);
    }
}




package finternet.fitCard.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.dialogplus.DialogPlus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import finternet.fitCard.R;
import finternet.fitCard.adapter.AdapterClass;
import finternet.fitCard.doc.Constants;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;


@SuppressWarnings("ALL")
public class ScheduleClassActivity extends Activity implements View.OnClickListener, ILoadService {
    private MaterialDialog mDialog;
    private ImageView imgBack;


    private ListView mLstClasses;
    private AdapterClass adapterClass;
    private int mode = 0;
    private DialogPlus mDlgPost;
    private View mDialogPostView;

    private final TextView[] txtDay = new TextView[7];
    private final TextView[] txtDate = new TextView[7];

    private String currentDate;
    private String searchDate;
    private LinearLayout layoutUpcomingSchedule;
    private TextView txtUpcoming;

    private TextView txtBack;
    private TextView txtNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        initView();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        currentDate = curFormater.format(c.getTime());
        searchDate = currentDate;
        updateCalendar();
        loadClass();
    }

    public void loadClass() {
        showDialog();
        String request = searchDate + "/" + Globals.currentGym.mId + "/-1/-1/-1";
        //String request = searchDate + "/" + "-1" + "/-1/-1/-1";
        ServiceManager.serviceSearchClass(request, this);
    }

    public void updateCalendar() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        try {
            calendar.setTime(sdf.parse(currentDate));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        for (int i = currentDay; i < currentDay + 7; i++) {
            txtDay[i - currentDay].setText(Constants.DAYS[i % 7]);
        }
        for (int i = 0; i < 7; i++) {
            Calendar calendar1 = Calendar.getInstance();
            try {
                calendar1.setTime(sdf.parse(currentDate));// all done
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar1.add(Calendar.DATE, i);
            txtDate[i].setText(String.valueOf(calendar1.get(Calendar.DAY_OF_MONTH)));
            final int p = i;
            txtDate[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 0; j < 7; j++) {
                        txtDate[j].setBackgroundDrawable(null);
                    }
                    txtDate[p].setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_circle_calendar));
                    SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(curFormater.parse(currentDate));// all done
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c.add(Calendar.DATE, p);
                    searchDate = curFormater.format(c.getTime());
                    //updateCalendar();
                    loadClass();
                }
            });
        }
    }

    public void initView() {
        imgBack = (ImageView) this.findViewById(R.id.btnScheduleBack);
        imgBack.setOnClickListener(this);

        layoutUpcomingSchedule = (LinearLayout) this.findViewById(R.id.layoutScheduleUpcoming);
        txtUpcoming = (TextView) this.findViewById(R.id.txtScheduleUpcoming);

        mLstClasses = (ListView) this.findViewById(R.id.lstScheduleClasses);
        txtDay[0] = (TextView) this.findViewById(R.id.txtScheduleDay1);
        txtDay[1] = (TextView) this.findViewById(R.id.txtScheduleDay2);
        txtDay[2] = (TextView) this.findViewById(R.id.txtScheduleDay3);
        txtDay[3] = (TextView) this.findViewById(R.id.txtScheduleDay4);
        txtDay[4] = (TextView) this.findViewById(R.id.txtScheduleDay5);
        txtDay[5] = (TextView) this.findViewById(R.id.txtScheduleDay6);
        txtDay[6] = (TextView) this.findViewById(R.id.txtScheduleDay7);

        txtDate[0] = (TextView) this.findViewById(R.id.txtScheduleDate1);
        txtDate[1] = (TextView) this.findViewById(R.id.txtScheduleDate2);
        txtDate[2] = (TextView) this.findViewById(R.id.txtScheduleDate3);
        txtDate[3] = (TextView) this.findViewById(R.id.txtScheduleDate4);
        txtDate[4] = (TextView) this.findViewById(R.id.txtScheduleDate5);
        txtDate[5] = (TextView) this.findViewById(R.id.txtScheduleDate6);
        txtDate[6] = (TextView) this.findViewById(R.id.txtScheduleDate7);

        txtBack = (TextView) this.findViewById(R.id.txtScheduleBack);
        txtNext = (TextView) this.findViewById(R.id.txtScheduleNext);
        layoutUpcomingSchedule.setVisibility(View.GONE);

        txtBack.setOnClickListener(this);
        txtNext.setOnClickListener(this);

        mDialog = new MaterialDialog.Builder(this)
                .title("Please wait")
                .content("Loading Class Infos")
                .progress(true, 0)
                .build();


        adapterClass = new AdapterClass(this);
        mLstClasses.setAdapter(adapterClass);
        adapterClass.update(Globals.lstClasses);
        mLstClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Globals.currentClass = Globals.lstClasses.get(i);
                Intent m = new Intent(ScheduleClassActivity.this, DetailClassActivity.class);
                ScheduleClassActivity.this.startActivity(m);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccessLoad(int type) {
        if (Globals.lstClasses.size() > 0) {
            mLstClasses.setVisibility(View.VISIBLE);
            layoutUpcomingSchedule.setVisibility(View.GONE);
            adapterClass.update(Globals.lstClasses);
        } else {
            layoutUpcomingSchedule.setVisibility(View.VISIBLE);
            mLstClasses.setVisibility(View.GONE);
            if (Globals.mUpcomingDate.equals("")) {
                txtUpcoming.setTextColor(0xff000000);
                txtUpcoming.setText("Nothing Upcoming Classes");
                txtUpcoming.setOnClickListener(null);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM",Locale.US);
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(sdf.parse(Globals.mUpcomingDate));// all done
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                txtUpcoming.setText(getResources().getString(R.string.string_upcoming_class) + sdf1.format(calendar.getTime()));
                txtUpcoming.setOnClickListener(this);
                txtUpcoming.setTextColor(0xff2798C6);
            }

        }
    }

    @Override
    public void showDialog() {
        mDialog.show();
    }

    @Override
    public void hideDialog() {
        mDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnScheduleBack:
                finish();
                break;
            case R.id.txtScheduleBack:
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                try {
                    calendar.setTime(sdf.parse(currentDate));// all done
                    calendar.add(Calendar.DATE, -7);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentDate = sdf.format(calendar.getTime());
                searchDate = currentDate;
                updateCalendar();
                for (int j = 0; j < 7; j++) {
                    txtDate[j].setBackgroundDrawable(null);
                }
                txtDate[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_circle_calendar));
                loadClass();
                break;
            case R.id.txtScheduleUpcoming:
                currentDate = Globals.mUpcomingDate;
                searchDate = currentDate;
                updateCalendar();
                for (int j = 0; j < 7; j++) {
                    txtDate[j].setBackgroundDrawable(null);
                }
                txtDate[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_circle_calendar));
                loadClass();
                break;
            case R.id.txtScheduleNext:
                calendar = Calendar.getInstance();
                sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                try {
                    calendar.setTime(sdf.parse(currentDate));// all done
                    calendar.add(Calendar.DATE, +7);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentDate = sdf.format(calendar.getTime());
                searchDate = currentDate;
                updateCalendar();
                for (int j = 0; j < 7; j++) {
                    txtDate[j].setBackgroundDrawable(null);
                }
                txtDate[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_circle_calendar));
                loadClass();
                break;
        }
    }
}

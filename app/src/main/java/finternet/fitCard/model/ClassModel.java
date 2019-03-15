package finternet.fitCard.model;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("unused")
public class ClassModel extends Model implements Comparable<ClassModel> {
    public String mImage;
    public String mRecurring;
    public String mEndDate;
    public String mDate;
    public String mStartHour;
    public String mEndHour;
    public String mGymName;
    public String mCategory;
    public String mAddress;
    public String mDuration;
    public String mAvailable;
    public String mDescription;
    public String mCity;
    public String mId;
    public String mBookDate;
    public String mBookId;
    public String mGymId;
    public String mIsBook;

    @Override
    public int compareTo(ClassModel another) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date strDate = null;
        Date strDate1 = null;
        try {
            strDate = sdf.parse(mDate + " " + mStartHour);
            strDate1 = sdf.parse(another.mDate + " " + another.mStartHour);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (strDate == null || strDate1 == null)
            return 0;
        if (strDate.after(strDate1)) {
            return 1;
        }
        else if (strDate.before(strDate1))
            return -1;
        return 0;
    }
}

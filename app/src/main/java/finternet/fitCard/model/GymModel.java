package finternet.fitCard.model;

import java.util.ArrayList;
import java.util.List;

import finternet.fitCard.doc.Globals;


@SuppressWarnings({"CanBeFinal", "unused"})
public class GymModel extends Model implements Comparable<GymModel> {
    public String mId;
    public String mDescription;
    public String mRating;
    public String mReview;
    public String mImage;
    public String mCountry;
    public String mCity;
    public String mAddress;
    public String mLat;
    public String mLon;
    public String mBookDate;
    public String mLogo;
    public List<String> mStartHours = new ArrayList<>();
    public List<String> mEndHours = new ArrayList<>();
    public List<String> mClose = new ArrayList<>();
    public List<String> mAmenity = new ArrayList<>();
    public List<String> mStudio = new ArrayList<>();
    public List<String> mLocation = new ArrayList<>();
    public List<String> mActivity = new ArrayList<>();
    public String mBookId;
    public int mUsability;
    public int mEnableCode;
    public double mDistance;

    @Override
    public int compareTo(GymModel gymModel) {
        if (Globals.sortMode == 1) {
            if (mDistance < gymModel.mDistance) {
                return -1;
            } else if (mDistance > gymModel.mDistance) {
                return 1;
            } else {
                return 0;
            }
        }
        else
        {
            boolean t1 = Globals.isCloseGym(this);
            boolean t2 = Globals.isCloseGym(gymModel);
            if (t1 == true && t2 == false)
                return -1;
            else if (t1 == false && t2 == true)
                return 1;
            return 0;
        }
    }
}

package finternet.fitCard.doc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.CallbackManager;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import finternet.fitCard.model.ClassModel;
import finternet.fitCard.model.GymModel;
import finternet.fitCard.model.Model;
import finternet.fitCard.model.PlanModel;
import finternet.fitCard.model.ReviewModel;
import finternet.fitCard.model.UserModel;


@SuppressWarnings("ALL")
public class Globals {
    public static Enums.MODE e_mode = Enums.MODE.HOME;
    public static List<GymModel> lstGyms = new ArrayList<>();

    public static final List<GymModel> lstCloseGyms = new ArrayList<>();
    public static final List<GymModel> lstOpenGyms = new ArrayList<>();

    public static final List<GymModel> lstFeaturedGym = new ArrayList<>();
    public static final List<ClassModel> lstClasses = new ArrayList<>();
    public static final List<Model> lstCitys = new ArrayList<>();
    public static final List<Model> lstCategory = new ArrayList<>();
    public static final List<Model> lstLocations = new ArrayList<>();
    public static final List<Model> lstActivity = new ArrayList<>();
    public static final List<Model> lstStudio = new ArrayList<>();
    public static final List<Model> lstAmenity = new ArrayList<>();
    public static final List<ReviewModel> lstReviews = new ArrayList<>();
    public static final List<PlanModel> lstPlans = new ArrayList<>();

    public static final List<GymModel> lstReviewGyms = new ArrayList<>();
    public static final List<ClassModel> lstReviewClasses = new ArrayList<>();

    public static GymModel currentGym;
    public static ClassModel currentClass;
    public static UserModel mAccount = new UserModel();
    public static int currentPlan = -1;
    public static String mLastVisitGym = "";
    public static int mVisitAmount = 0;
    public static int mClassBook = 0;
    public static CallbackManager g_callbackManager;
    public static String mUpcomingDate = "";
    public static int contentWidth;
    public static int contentHeight;

    public static int visitCode;

    public static Location currentLocation;


    public static int isLogin = 0;
    public static int sortMode  = 0;

    public static boolean isCloseGym(GymModel model)
    {
        Date currentDate = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        String currentDateString = sdf1.format(currentDate);
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String startDateString = currentDateString + " " + model.mStartHours.get(currentDay).substring(0, 5);
        String endDateString = currentDateString + " " + model.mEndHours.get(currentDay).substring(0, 5);
        try {
            long startTimeStamp = format1.parse(startDateString).getTime();
            long endTimeStamp = format1.parse(endDateString).getTime();

            if (currentDate.getTime() > startTimeStamp && currentDate.getTime() < endTimeStamp)
            {
                return true;
            }
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static String getRealPathFromURI(Activity act, Uri contentURI) {
        @SuppressLint("Recycle") Cursor cursor = act.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private static Bitmap decodeFile(File f) {
        Bitmap b = null;
        int IMAGE_MAX_WIDTH = 800;
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        try {
            //fis = new FileInputStream(f);
            try {
                BitmapFactory.decodeFile(f.getAbsolutePath(), o);

                //fis.close();
                //fis = null;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            int scale = 1;

            if (o.outHeight > IMAGE_MAX_WIDTH || o.outWidth > IMAGE_MAX_WIDTH) {
                int maxwh = Math.max(o.outWidth, o.outHeight);
                while (maxwh / scale > IMAGE_MAX_WIDTH)
                    scale *= 2;
            }
            //Decode with inSampleSize

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.inJustDecodeBounds = false;
            o2.inPurgeable = true;

            //fis = new FileInputStream(f);

            try {
                b = BitmapFactory.decodeFile(f.getAbsolutePath(), o2);

                //fis.close();
                //fis = null;
            } catch (Exception e) {
                //	TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }
    public static boolean isUserOverInvoice()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = null;
        if (Globals.mAccount.mInvoiceEnd != null) {
            try {
                date = sdf.parse(Globals.mAccount.mInvoiceEnd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        if (date == null) return true;
        long l1 = c.getTimeInMillis();
        long l2 = date.getTime();
        if (l1 < l2 )
        {
            return false;
        }
        else {
            return true;
        }
    }
    public static float getDistance(Location l1, Location l2) {
        if (l1 ==null || l2 == null) return 0;
        return Math.abs(l1.distanceTo(l2));
    }
}

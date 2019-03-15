package finternet.fitCard.service;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import finternet.fitCard.activity.HomeActivity;
import finternet.fitCard.doc.Constants;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.fragment.ChargeFragment;
import finternet.fitCard.fragment.ProfileFragment;
import finternet.fitCard.model.ActivityModel;
import finternet.fitCard.model.AmenityModel;
import finternet.fitCard.model.CategoryModel;
import finternet.fitCard.model.CityModel;
import finternet.fitCard.model.ClassModel;
import finternet.fitCard.model.GymModel;
import finternet.fitCard.model.LocationModel;
import finternet.fitCard.model.PlanModel;
import finternet.fitCard.model.ReviewModel;
import finternet.fitCard.model.StudioModel;
import finternet.fitCard.model.UserModel;
import finternet.fitCard.util.HttpUtil;


@SuppressWarnings("UnnecessaryLocalVariable")
public class ServiceManager {

    public static void serviceLoginFacebook(String token,final HomeActivity activity)
    {
        String url = Constants.LOGINFACEBOOK + token;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String ss = "";
                int i = 1;
            }

            public void onFinish() {

            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject userObject = new JSONObject(paramString);
                    if (!userObject.has("error")) {
                        Globals.mAccount = new UserModel();
                        UserModel uModel = new UserModel();
                        uModel.mId = userObject.getString("id");
                        uModel.mName = userObject.getString("name");
                        if (userObject.getString("plan_id") == "null")
                            uModel.mPlan = "4";
                        else
                            uModel.mPlan = userObject.getString("plan_id");
                        uModel.mCredit = userObject.getString("credit");
                        uModel.mImage = userObject.getString("image");
                        uModel.mPhone = userObject.getString("phone");
                        uModel.mCity = userObject.getString("city");
                        uModel.mAddress = userObject.getString("address");
                        uModel.mInvoiceEnd = userObject.getString("invoice_end");
                        uModel.mInvoiceStart = userObject.getString("invoice_start");
                        uModel.mToken = userObject.getString("api_token");
                        uModel.mHasCard = userObject.getString("has_card_token");

                        Globals.mAccount = uModel;
                        //activity.onSuccessLoad(0);
                        //activity.hideDialog();
                    }
                    else
                    {
                        //activity.hideDialog();
                        //activity.onSuccessLoad(1);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    //activity.hideDialog();
                    //activity.onSuccessLoad(1);
                }
            }
        });
    }
    public static void serviceReviewGym(String mId, String mTitle, String mContent, float rate, String bookId) {
        RequestParams param = new RequestParams();
        param.put("id", Globals.mAccount.mId);
        param.put("gym", mId);
        param.put("title", mTitle);
        param.put("class", "-1");
        param.put("content", mContent);
        param.put("rate", String.valueOf(rate));
        param.put("book", bookId);
        HttpUtil.post(Constants.REVIEWCLASS, param, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
            }

            public void onFinish() {

            }

            public void onSuccess(String paramString) {  //that is return when success..

            }
        });
    }

    public static void serviceReviewClass(String mGym, String mId, String mTitle, String mContent, float rate, String bookId) {
        RequestParams param = new RequestParams();
        param.put("id", Globals.mAccount.mId);
        param.put("gym", mGym);
        param.put("class", mId);
        param.put("title", mTitle);
        param.put("content", mContent);
        param.put("rate", String.valueOf(rate));
        param.put("book", bookId);
        HttpUtil.post(Constants.REVIEWCLASS, param, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {

            }

            public void onFinish() {

            }

            public void onSuccess(String paramString) {  //that is return when success..

            }
        });
    }

    public static void serviceCancelGym(String mId) {
        String url = Constants.CANCELBOOKGYM + "/" + mId + "/" + Globals.mAccount.mId;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {

            }

            public void onFinish() {

            }

            public void onSuccess(String paramString) {  //that is return when success..

            }
        });
    }

    public static void serviceCancelClass(String mId) {
        String url = Constants.CANCELBOOKCLASS + "/" + mId + "/" + Globals.mAccount.mId;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {

            }

            public void onFinish() {

            }

            public void onSuccess(String paramString) {  //that is return when success..

            }
        });
    }

    public static void serviceLastVisitClass(final ILoadService activity) {
        String url = Constants.LASTVISITCLASS + "/" + Globals.mAccount.mId + "/" + Globals.currentClass.mId;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {

            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    Globals.mClassBook = 0;
                    if (localJSONObject1.has("book")) {
                        Globals.mClassBook = localJSONObject1.getInt("book");
                        activity.onSuccessLoad(3);
                    }
                    activity.hideDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                }
            }
        });
    }

    public static void serviceLastVisitGym(final ILoadService activity) {
        String url = Constants.LASTVISITGYM + "/" + Globals.mAccount.mId + "/" + Globals.currentGym.mId;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    Globals.mLastVisitGym = "";
                    if (localJSONObject1.has("booktime")) {
                        String bookTime = localJSONObject1.getString("booktime");
                        Globals.mVisitAmount = localJSONObject1.getInt("count");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM HH:mm",Locale.US);
                        if (!bookTime.equals("'")) {
                            Date date = sdf.parse(bookTime);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.HOUR, 3);
                            Date currentDate = new Date();
                            long l1 = currentDate.getTime();
                            long l2 = calendar.getTimeInMillis();
                            if (l1 > l2) {
                                activity.onSuccessLoad(3);
                            } else
                                Globals.mLastVisitGym = sdf1.format(calendar.getTime());
                        }
                        activity.onSuccessLoad(3);
                    }
                    activity.hideDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                }
            }
        });
    }
    public static void serviceCoupon(String strCode,final ILoadService activity)
    {
        String url = Constants.COUPONCODE + "/" + strCode + "/" + Globals.mAccount.mId;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
                activity.onSuccessLoad(2);
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    Globals.mLastVisitGym = "";
                    if (localJSONObject1.has("result")) {
                        String result = localJSONObject1.getString("result");
                        if (result.equals("1"))
                        {
                            int credits = localJSONObject1.getInt("credit");
                            Globals.mAccount.mCredit = String.valueOf(credits);
                            activity.onSuccessLoad(1);
                            return;
                        }

                    }
                    activity.hideDialog();
                    activity.onSuccessLoad(2);

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                    activity.onSuccessLoad(2);
                }
            }
        });
    }
    public static void serviceLoadOverReview(final Context context) {
        String url = Constants.LOADOVERREVIEW + "/" + Globals.mAccount.mId;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("gyms")) {
                        Globals.lstReviewGyms.clear();
                        Globals.lstReviewClasses.clear();
                        JSONArray gymArray = localJSONObject1.getJSONArray("gyms");
                        for (int i = 0; i < gymArray.length(); i++) {
                            GymModel mModel = new GymModel();
                            JSONObject bookObject = gymArray.getJSONObject(i);
                            JSONObject gymObject = bookObject.getJSONObject("gym");
                            mModel.mName = gymObject.getString("name");
                            mModel.mBookDate = bookObject.getString("date");
                            mModel.mBookId = bookObject.getString("id");
                            mModel.mRating = gymObject.getString("rating");
                            mModel.mImage = gymObject.getString("image");
                            mModel.mReview = gymObject.getString("reviews");
                            mModel.mCountry = gymObject.getString("country");
                            mModel.mCity = gymObject.getString("city");
                            mModel.mAddress = gymObject.getString("address");
                            mModel.mLat = gymObject.getString("lat");
                            mModel.mLon = gymObject.getString("lon");
                            mModel.mDescription = gymObject.getString("description");
                            mModel.mId = gymObject.getString("id");
                            mModel.mStartHours.clear();
                            mModel.mEndHours.clear();
                            /*Location l1 = new Location("");
                            l1.setLatitude(Double.parseDouble(mModel.mLat));
                            l1.setLongitude(Double.parseDouble(mModel.mLon));
                            mModel.mDistance = Globals.getDistance(Globals.currentLocation,l1);*/

                            for (int j = 0; j < Constants.FIELD_STARTHOUR.length; j++) {
                                String startHour = gymObject.getString(Constants.FIELD_STARTHOUR[j]);
                                String endHour = gymObject.getString(Constants.FIELD_ENDHOUR[j]);
                                mModel.mStartHours.add(startHour);
                                mModel.mEndHours.add(endHour);
                            }
                            Globals.lstReviewGyms.add(mModel);
                        }
                        JSONArray classArray = localJSONObject1.getJSONArray("classes");
                        for (int i = 0; i < classArray.length(); i++) {
                            ClassModel mModel = new ClassModel();
                            JSONObject bookObject = classArray.getJSONObject(i);
                            JSONObject classObject = bookObject.getJSONObject("class_model");
                            mModel.mBookDate = bookObject.getString("date");
                            mModel.mBookId = bookObject.getString("id");
                            mModel.mName = classObject.getString("name");
                            mModel.mDate = classObject.getString("date");
                            mModel.mEndDate = classObject.getString("enddate");
                            mModel.mEndHour = classObject.getString("endhour");
                            mModel.mStartHour = classObject.getString("starthour");
                            JSONObject gymObject = classObject.getJSONObject("gym_info");
                            mModel.mImage = gymObject.getString("image");
                            mModel.mGymName = gymObject.getString("name");
                            mModel.mGymId = gymObject.getString("id");
                            mModel.mDescription = gymObject.getString("description");
                            mModel.mCity = gymObject.getString("city");
                            mModel.mId = classObject.getString("id");

                            Globals.lstReviewClasses.add(mModel);
                        }
                        Intent intent = new Intent();
                        intent.setAction("finternet.fitcard.CUSTOM_INTENT");
                        context.sendBroadcast(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void serviceLoadBooks(final ILoadService activity) {
        String url = Constants.LOADBOOKS + "/" + Globals.mAccount.mId;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("gyms")) {
                        Globals.lstGyms.clear();
                        JSONArray gymArray = localJSONObject1.getJSONArray("gyms");
                        for (int i = 0; i < gymArray.length(); i++) {
                            GymModel mModel = new GymModel();
                            JSONObject bookObject = gymArray.getJSONObject(i);
                            JSONObject gymObject = bookObject.getJSONObject("gym");
                            mModel.mName = gymObject.getString("name");
                            mModel.mBookDate = bookObject.getString("date");
                            mModel.mBookId = bookObject.getString("id");
                            mModel.mRating = gymObject.getString("rating");
                            mModel.mImage = gymObject.getString("image");
                            mModel.mReview = gymObject.getString("reviews");
                            mModel.mCountry = gymObject.getString("country");
                            mModel.mCity = gymObject.getString("city");
                            mModel.mAddress = gymObject.getString("address");
                            mModel.mLat = gymObject.getString("lat");
                            mModel.mLon = gymObject.getString("lon");
                            mModel.mDescription = gymObject.getString("description");
                            mModel.mId = gymObject.getString("id");
                            try {
                                mModel.mUsability = gymObject.getInt("usability");
                            }
                            catch(Exception e)
                            {
                                mModel.mUsability = 0;
                            }
                            mModel.mStartHours.clear();
                            mModel.mEndHours.clear();
                            for (int j = 0; j < Constants.FIELD_STARTHOUR.length; j++) {
                                String startHour = gymObject.getString(Constants.FIELD_STARTHOUR[j]);
                                String endHour = gymObject.getString(Constants.FIELD_ENDHOUR[j]);
                                mModel.mStartHours.add(startHour);
                                mModel.mEndHours.add(endHour);
                            }
                            Globals.lstGyms.add(mModel);
                        }
                        JSONArray classArray = localJSONObject1.getJSONArray("classes");
                        JSONArray durationArray = localJSONObject1.getJSONArray("duration");
                        JSONArray availableArray = localJSONObject1.getJSONArray("available");
                        for (int i = 0; i < classArray.length(); i++) {
                            ClassModel mModel = new ClassModel();
                            JSONObject bookObject = classArray.getJSONObject(i);
                            JSONObject classObject = bookObject.getJSONObject("class_model");
                            mModel.mBookDate = bookObject.getString("date");
                            mModel.mBookId = bookObject.getString("id");
                            mModel.mName = classObject.getString("name");
                            mModel.mDate = classObject.getString("date");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            if (!mModel.mDate.equals("")) {
                                Date date = sdf.parse(mModel.mDate);
                                Date currentDate = new Date();
                                long l1 = date.getTime();
                                long l2 = currentDate.getTime();
                                if (l1 <= l2)
                                    continue;
                            }
                            mModel.mEndDate = classObject.getString("enddate");
                            mModel.mEndHour = classObject.getString("endhour");
                            mModel.mStartHour = classObject.getString("starthour");
                            JSONObject gymObject = classObject.getJSONObject("gym_info");
                            mModel.mImage = gymObject.getString("image");
                            mModel.mGymName = gymObject.getString("name");
                            mModel.mDuration = durationArray.getString(i);
                            mModel.mAvailable = availableArray.getString(i);
                            mModel.mDescription = gymObject.getString("description");
                            mModel.mCity = gymObject.getString("city");
                            mModel.mId = classObject.getString("id");

                            Globals.lstClasses.add(mModel);
                        }
                        activity.onSuccessLoad(0);
                    }
                    activity.hideDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                }
            }
        });
    }

    public static void serviceFeaturedGym(final ILoadService activity) {
        HttpUtil.get(Constants.FEATURED_URL, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("gyms")) {
                        Globals.lstFeaturedGym.clear();
                        JSONArray gymArray = localJSONObject1.getJSONArray("gyms");
                        for (int i = 0; i < gymArray.length(); i++) {
                            GymModel mModel = new GymModel();
                            JSONObject gymObject = gymArray.getJSONObject(i);
                            mModel.mName = gymObject.getString("name");
                            mModel.mRating = gymObject.getString("rating");
                            mModel.mImage = gymObject.getString("image");
                            mModel.mReview = gymObject.getString("reviews");
                            mModel.mCountry = gymObject.getString("country");
                            mModel.mCity = gymObject.getString("city");
                            mModel.mAddress = gymObject.getString("address");
                            mModel.mLat = gymObject.getString("lat");
                            mModel.mLon = gymObject.getString("lon");
                            mModel.mDescription = gymObject.getString("description");
                            mModel.mId = gymObject.getString("id");
                            try {
                                mModel.mUsability = gymObject.getInt("usability");
                            }
                            catch(Exception e)
                            {
                                mModel.mUsability = 0;
                            }
                            mModel.mStartHours.clear();
                            mModel.mEndHours.clear();
                            for (int j = 0; j < Constants.FIELD_STARTHOUR.length; j++) {
                                String startHour = gymObject.getString(Constants.FIELD_STARTHOUR[j]);
                                String endHour = gymObject.getString(Constants.FIELD_ENDHOUR[j]);
                                mModel.mStartHours.add(startHour);
                                mModel.mEndHours.add(endHour);
                            }
                            Location l1 = new Location("");
                            try {
                                l1.setLatitude(Double.parseDouble(mModel.mLat));
                                l1.setLongitude(Double.parseDouble(mModel.mLon));
                            }
                            catch(Exception e)
                            {
                                l1.setLatitude(0);
                                l1.setLongitude(0);
                            }
                            mModel.mDistance = Globals.getDistance(Globals.currentLocation,l1);
                            Globals.lstFeaturedGym.add(mModel);
                        }
                        activity.onSuccessLoad(0);
                    }
                    activity.hideDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                }
            }
        });

    }

    public static void serviceLoadGyms(final ILoadService activity) {
        HttpUtil.get(Constants.GYMLIST_URL, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("gyms")) {
                        Globals.lstGyms.clear();
                        Globals.lstOpenGyms.clear();;
                        Globals.lstCloseGyms.clear();
                        JSONArray gymArray = localJSONObject1.getJSONArray("gyms");
                        for (int i = 0; i < gymArray.length(); i++) {
                            GymModel mModel = new GymModel();
                            JSONObject gymObject = gymArray.getJSONObject(i);
                            mModel.mName = gymObject.getString("name");
                            mModel.mRating = gymObject.getString("rating");
                            mModel.mImage = gymObject.getString("image");
                            mModel.mReview = gymObject.getString("reviews");
                            mModel.mCountry = gymObject.getString("country");
                            mModel.mCity = gymObject.getString("city");
                            mModel.mAddress = gymObject.getString("address");
                            mModel.mLat = gymObject.getString("lat");
                            mModel.mLon = gymObject.getString("lon");
                            mModel.mDescription = gymObject.getString("description");
                            mModel.mId = gymObject.getString("id");
                            mModel.mEnableCode = gymObject.getInt("visitcode");
                            try {
                                mModel.mUsability = gymObject.getInt("usability");
                            }
                            catch(Exception e)
                            {
                                mModel.mUsability = 0;
                            }
                            mModel.mStartHours.clear();
                            mModel.mEndHours.clear();
                            mModel.mClose.clear();
                            for (int j = 0; j < Constants.FIELD_STARTHOUR.length; j++) {
                                String startHour = gymObject.getString(Constants.FIELD_STARTHOUR[j]);
                                String endHour = gymObject.getString(Constants.FIELD_ENDHOUR[j]);
                                String close = gymObject.getString(Constants.FIELD_CLOSE[j]);
                                mModel.mStartHours.add(startHour);
                                mModel.mEndHours.add(endHour);
                                mModel.mClose.add(close);
                            }
                            if (Globals.isCloseGym(mModel))
                            {
                                Globals.lstOpenGyms.add(mModel);
                            }
                            else Globals.lstCloseGyms.add(mModel);
                            Location l1 = new Location("");
                            try {
                                l1.setLatitude(Double.parseDouble(mModel.mLat));
                                l1.setLongitude(Double.parseDouble(mModel.mLon));
                            }
                            catch(Exception e)
                            {
                                l1.setLatitude(0);
                                l1.setLongitude(0);
                            }
                            mModel.mDistance = Globals.getDistance(Globals.currentLocation,l1);
                        }

                        for (int kk = 0; kk < Globals.lstOpenGyms.size();kk++)
                        {
                            Globals.lstGyms.add(Globals.lstOpenGyms.get(kk));
                        }
                        for (int kk = 0; kk < Globals.lstCloseGyms.size();kk++)
                        {
                            Globals.lstGyms.add(Globals.lstCloseGyms.get(kk));
                        }

                        Globals.lstCitys.clear();
                        JSONArray cityArray = localJSONObject1.getJSONArray("cityInfos");
                        for (int i = 0; i < cityArray.length(); i++) {
                            CityModel cModel = new CityModel();
                            JSONObject cityObject = cityArray.getJSONObject(i);
                            cModel.mId = cityObject.getString("id");
                            cModel.mName = cityObject.getString("city_name");
                            cModel.mLat = cityObject.getString("lat");
                            cModel.mLat = cityObject.getString("lon");

                            Globals.lstCitys.add(cModel);
                        }

                        Globals.lstLocations.clear();
                        JSONArray locationArray = localJSONObject1.getJSONArray("locationInfos");
                        for (int i = 0; i < locationArray.length(); i++) {
                            LocationModel lModel = new LocationModel();
                            JSONObject locationObject = locationArray.getJSONObject(i);
                            lModel.mId = locationObject.getString("id");
                            lModel.mName = locationObject.getString("name");

                            Globals.lstLocations.add(lModel);
                        }

                        Globals.lstAmenity.clear();
                        JSONArray amenityArray = localJSONObject1.getJSONArray("amenityInfos");
                        for (int i = 0; i < amenityArray.length(); i++) {
                            AmenityModel lModel = new AmenityModel();
                            JSONObject amenityObject = amenityArray.getJSONObject(i);
                            lModel.mId = amenityObject.getString("id");
                            lModel.mName = amenityObject.getString("name");

                            Globals.lstAmenity.add(lModel);
                        }

                        Globals.lstActivity.clear();
                        JSONArray activityArray = localJSONObject1.getJSONArray("activityInfos");
                        for (int i = 0; i < activityArray.length(); i++) {
                            ActivityModel lModel = new ActivityModel();
                            JSONObject activityObject = activityArray.getJSONObject(i);
                            lModel.mId = activityObject.getString("id");
                            lModel.mName = activityObject.getString("name");

                            Globals.lstActivity.add(lModel);
                        }

                        Globals.lstStudio.clear();
                        JSONArray studioArray = localJSONObject1.getJSONArray("studioInfos");
                        for (int i = 0; i < studioArray.length(); i++) {
                            StudioModel lModel = new StudioModel();
                            JSONObject studioObject = studioArray.getJSONObject(i);
                            lModel.mId = studioObject.getString("id");
                            lModel.mName = studioObject.getString("name");

                            Globals.lstStudio.add(lModel);
                        }

                        activity.onSuccessLoad(0);
                    }
                    activity.hideDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                }
            }
        });
    }
    public static void serviceLoadLocation(final ILoadService activity)
    {
        HttpUtil.get(Constants.LOCATION, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("lat")) {
                        String lat = localJSONObject1.getString("lat");
                        String lon = localJSONObject1.getString("lon");
                        Globals.currentLocation = new Location("reverseGeocoded");
                        Globals.currentLocation.setLatitude(Double.parseDouble(lat));
                        Globals.currentLocation.setLongitude(Double.parseDouble(lon));
                        ServiceManager.serviceFeaturedGym(activity);
                    }
                    activity.hideDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                }
            }
        });
    }
    public static void serviceLoadPlan(final ILoadService activity) {
        HttpUtil.get(Constants.LOADPLAN, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("plans")) {
                        JSONArray planArray = localJSONObject1.getJSONArray("plans");
                        for (int i = 0; i < planArray.length(); i++) {
                            PlanModel mModel = new PlanModel();
                            JSONObject planObject = planArray.getJSONObject(i);
                            mModel.mId = planObject.getString("id");
                            mModel.mName = planObject.getString("plan");
                            mModel.mPrice = planObject.getString("price");
                            mModel.mCredit = planObject.getString("credit");

                            Globals.lstPlans.add(mModel);
                        }
                        activity.onSuccessLoad(0);
                    }
                    activity.hideDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                }
            }
        });
    }

    public static void serviceChargeFund(final String card, final String cvv, final String month, final String year, final String amount, final ILoadService activity) {
        String url = Constants.CHARGEFUND +  String.valueOf(Globals.lstPlans.get(Globals.currentPlan).mId) + "?api_token="+Globals.mAccount.mToken ;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
                ((ChargeFragment) activity).mDialogError.show();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("token")) {
                        String token = localJSONObject1.getString("token");
                        String url = localJSONObject1.getString("payment_url");
                        String currency = localJSONObject1.getString("currency");
                        ServiceManager.servicePaymentService(url, token, card, amount, currency, month, year, cvv, activity);
                    } else {
                        activity.hideDialog();
                        ((ChargeFragment) activity).mDialogError.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                    ((ChargeFragment) activity).mDialogError.show();
                }
            }
        });
    }

    private static void servicePaymentService(String url, final String token, final String card, final String amount, final String currency, final String month, final String year, final String code, final ILoadService activity) {
        RequestParams reqParam = new RequestParams();
        reqParam.put("token", token);
        reqParam.put("card", card);
        reqParam.put("amount", amount);
        reqParam.put("currency", currency);
        reqParam.put("exp_month", month);
        reqParam.put("exp_year", year);
        reqParam.put("security_code", code);

        HttpUtil.post(url, reqParam, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
                ((ChargeFragment) activity).mDialogError.show();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    serviceFundCheck(token, activity);
                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                    ((ChargeFragment) activity).mDialogError.show();
                }
            }
        });
    }

    public static void serviceBookGym(final ILoadService activity) {
        String url = Constants.BOOKGYM + "/" + Globals.mAccount.mId + "/" + Globals.currentGym.mId;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {

            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("result")) {
                        int result = localJSONObject1.getInt("result");
                        Globals.visitCode = localJSONObject1.getInt("code");
                        if (result == 1) {
                            activity.hideDialog();
                            activity.onSuccessLoad(2);
                        } else {
                            activity.hideDialog();
                        }
                    } else {
                        activity.hideDialog();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                    ((ChargeFragment) activity).mDialogError.show();
                }
            }
        });
    }

    public static void serviceBookClass(final ILoadService activity) {
        String url = Constants.BOOKCLASS + "/" + Globals.mAccount.mId + "/" + Globals.currentClass.mId;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("result")) {
                        int result = localJSONObject1.getInt("result");
                        if (result == 1) {
                            activity.hideDialog();
                            activity.onSuccessLoad(2);
                        } else {
                            activity.hideDialog();
                        }
                    } else {
                        activity.hideDialog();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                    ((ChargeFragment) activity).mDialogError.show();
                }
            }
        });
    }
    private static void serviceFundCheck(final String token, final ILoadService activity) {
        String url = Constants.FUNDCHECK + "?api_token=" + Globals.mAccount.mToken;
        RequestParams p = new RequestParams();
        p.put("payment_token",token);
        HttpUtil. post(url,p, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
                ((ChargeFragment) activity).mDialogError.show();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("success")) {
                        activity.onSuccessLoad(0);
                    } else {
                        activity.hideDialog();
                        ((ChargeFragment) activity).mDialogError.show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                    ((ChargeFragment) activity).mDialogError.show();
                }
            }
        });
    }

    public static void serviceUpdateProfile(final UserModel mModel, final ILoadService activity) {
        String url = Constants.UPDATEPROFILE + "/" + mModel.mId + "/-1/-1/-1/-1";
        RequestParams p = new RequestParams();
        p.put("image", mModel.mImage);
        p.put("phone", mModel.mPhone);
        p.put("city", mModel.mCity);
        p.put("address", mModel.mAddress);
        p.put("name", mModel.mName);
        HttpUtil.post(url, p, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("result")) {
                        activity.hideDialog();
                        Globals.mAccount.mName = mModel.mName;
                        Globals.mAccount.mCity = mModel.mCity;
                        Globals.mAccount.mPhone = mModel.mPhone;
                        Globals.mAccount.mAddress = mModel.mAddress;
                        Globals.mAccount.mImage = mModel.mImage;
                        activity.onSuccessLoad(0);
                    } else {
                        activity.hideDialog();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                }
            }
        });
    }

    private static void serviceUpdateCredit(final String token, final String membership, final ILoadService activity) {
        String url = Constants.UPDATECREDIT + "/" + Globals.mAccount.mId + "/" + membership + "/" + token;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
                ((ChargeFragment) activity).mDialogError.show();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("result")) {
                        activity.hideDialog();
                        activity.onSuccessLoad(0);
                    } else {
                        activity.hideDialog();
                        ((ChargeFragment) activity).mDialogError.show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                    ((ChargeFragment) activity).mDialogError.show();
                }
            }
        });
    }
    public static void serviceCleanCode(final ILoadService activity)
    {
        String url = Constants.CLEANCARD + "/" + Globals.mAccount.mId;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString)
            {  //that is return when success..

                activity.onSuccessLoad(1);
            }
        });
    }
    public static void serviceChargeMemberWithToken(String mId,String mPid,final ILoadService activity)
    {
        String url = Constants.CHARGETOKEN + "/" +  mPid + "?api_token=" + Globals.mAccount.mToken;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                activity.onSuccessLoad(4);
            }
        });
    }
    public static void serviceRegisterUser(final String user, final String password, final String email, final ILoadService activity) {
        String url = Constants.REGISTERUSER;
        RequestParams param = new RequestParams();
        param.put("name",user);
        param.put("password",password);
        param.put("email",email);
        HttpUtil.post(url,param, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject userObject = new JSONObject(paramString);
                    if (!userObject.has("error")) {
                        Globals.mAccount = new UserModel();
                        UserModel uModel = new UserModel();
                        uModel.mId = userObject.getString("id");
                        uModel.mName = userObject.getString("name");
                        uModel.mEmail = email;
                        if (userObject.getString("plan_id") == "null")
                            uModel.mPlan = "4";
                        else
                            uModel.mPlan = userObject.getString("plan_id");
                        uModel.mCredit = userObject.getString("credit");
                        uModel.mImage = userObject.getString("image");
                        uModel.mPhone = userObject.getString("phone");
                        uModel.mCity = userObject.getString("city");
                        uModel.mAddress = userObject.getString("address");
                        uModel.mInvoiceEnd = userObject.getString("invoice_end");
                        uModel.mInvoiceStart = userObject.getString("invoice_start");
                        uModel.mToken = userObject.getString("api_token");
                        uModel.mHasCard = userObject.getString("has_card_token");

                        Globals.mAccount = uModel;
                        activity.onSuccessLoad(0);
                    }
                    activity.hideDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                }
            }
        });
    }

    public static void serviceProfileImageSave(UserModel profile, final ProfileFragment activity1) {
        if (profile.mImage == null || profile.mImage.equals("")) {
            profile.mImage = Globals.mAccount.mImage;
            ServiceManager.serviceUpdateProfile(profile, activity1);
        } else {
            PostImage m = new PostImage(profile, activity1);
            m.postHttp();
        }
    }
    public static void serviceProfile(final ProfileFragment fragment)
    {
        String url = Constants.PROFILEUSER + "?api_token=" + Globals.mAccount.mToken;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                fragment.hideDialog();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject userObject = new JSONObject(paramString);
                    if (!userObject.has("error")) {

                        UserModel uModel = new UserModel();
                        uModel.mId = userObject.getString("id");
                        uModel.mName = userObject.getString("name");
                        uModel.mEmail = Globals.mAccount.mEmail;
                        uModel.mPlan = userObject.getString("plan_id");
                        uModel.mCredit = userObject.getString("credit");
                        uModel.mImage = userObject.getString("image");
                        uModel.mPhone = userObject.getString("phone");
                        uModel.mCity = userObject.getString("city");
                        uModel.mAddress = userObject.getString("address");
                        uModel.mInvoiceEnd = userObject.getString("invoice_end");
                        uModel.mInvoiceStart = userObject.getString("invoice_start");
                        uModel.mHasCard = userObject.getString("has_card_token");
                        uModel.mToken = userObject.getString("api_token");

                        Globals.mAccount = uModel;
                        fragment.onSuccessLoad(3);
                    }
                    fragment.hideDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                    fragment.hideDialog();
                }
            }
        });
    }
    public static void serviceLoginUser(final String user, String password, final ILoadService activity) {
        String url = Constants.LOGINUSER;

        RequestParams p = new RequestParams();
        p.put("email",user);
        p.put("password",password);


        HttpUtil.post(url,p,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject userObject = new JSONObject(paramString);
                    if (!userObject.has("error")) {
                        Globals.mAccount = new UserModel();
                        UserModel uModel = new UserModel();
                        uModel.mId = userObject.getString("id");
                        uModel.mName = userObject.getString("name");
                        uModel.mEmail = user;
                        if (userObject.getString("plan_id") == "null")
                            uModel.mPlan = "4";
                        else
                            uModel.mPlan = userObject.getString("plan_id");
                        uModel.mCredit = userObject.getString("credit");
                        uModel.mImage = userObject.getString("image");
                        uModel.mPhone = userObject.getString("phone");
                        uModel.mCity = userObject.getString("city");
                        uModel.mAddress = userObject.getString("address");
                        uModel.mInvoiceEnd = userObject.getString("invoice_end");
                        uModel.mInvoiceStart = userObject.getString("invoice_start");
                        uModel.mToken = userObject.getString("api_token");
                        uModel.mHasCard = userObject.getString("has_card_token");

                        Globals.mAccount = uModel;
                        activity.onSuccessLoad(0);
                        activity.hideDialog();
                    }
                    else
                    {
                        activity.hideDialog();
                        activity.onSuccessLoad(1);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                    activity.onSuccessLoad(1);
                }
            }
        });
    }

    public static void serviceLoadReviewForClass(String cid, final ILoadService activity) {
        String url = Constants.LOADCLASSREVIEWS + "/" + cid;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("reviews")) {
                        Globals.lstReviews.clear();
                        JSONArray reviewArray = localJSONObject1.getJSONArray("reviews");
                        for (int i = 0; i < reviewArray.length(); i++) {
                            ReviewModel mModel = new ReviewModel();
                            JSONObject reviewObject = reviewArray.getJSONObject(i);
                            JSONObject consumerObject = reviewObject.getJSONObject("consumer");
                            mModel.mName = consumerObject.getString("name");
                            mModel.mRating = reviewObject.getString("star");
                            mModel.mImage = consumerObject.getString("image");
                            mModel.mDate = reviewObject.getString("date");
                            mModel.mContent = reviewObject.getString("description");
                            Globals.lstReviews.add(mModel);
                        }
                        activity.onSuccessLoad(1);
                    }
                    activity.hideDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                }
            }
        });
    }
    public static void serviceSendContact(String message,final ILoadService activity)
    {
        String url = Constants.CONTACTUS;
        RequestParams req = new RequestParams();
        req.put("message",message);
        HttpUtil.post(url, req,new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {

            }

            public void onSuccess(String paramString) {  //that is return when success..

            }
        });
    }
    public static void serviceLoadReviewForGym(String gid, final ILoadService activity) {
        String url = Constants.LOADGYMREVIEWS + "/" + gid;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                activity.hideDialog();
            }

            public void onFinish() {

            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("reviews")) {
                        Globals.lstReviews.clear();
                        JSONArray reviewArray = localJSONObject1.getJSONArray("reviews");
                        for (int i = 0; i < reviewArray.length(); i++) {
                            ReviewModel mModel = new ReviewModel();
                            JSONObject reviewObject = reviewArray.getJSONObject(i);
                            JSONObject consumerObject = reviewObject.getJSONObject("consumer");
                            mModel.mName = consumerObject.getString("name");
                            mModel.mRating = reviewObject.getString("star");
                            mModel.mImage = consumerObject.getString("image");
                            mModel.mDate = reviewObject.getString("date");
                            mModel.mContent = reviewObject.getString("description");
                            Globals.lstReviews.add(mModel);
                        }
                        activity.onSuccessLoad(1);
                    }
                    activity.hideDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                }
            }
        });
    }

    public static void serviceSearchGym(String request, final ILoadService activity) {
        String url = Constants.SEARCHGYM + "/" + request;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
            }

            public void onFinish() {
            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("gyms")) {
                        Globals.lstGyms.clear();
                        Globals.lstCloseGyms.clear();;
                        Globals.lstOpenGyms.clear();
                        JSONArray gymArray = localJSONObject1.getJSONArray("gyms");
                        for (int i = 0; i < gymArray.length(); i++) {
                            GymModel mModel = new GymModel();
                            JSONObject gymObject = gymArray.getJSONObject(i);
                            mModel.mName = gymObject.getString("name");
                            mModel.mRating = gymObject.getString("rating");
                            mModel.mImage = gymObject.getString("image");
                            mModel.mReview = gymObject.getString("reviews");
                            mModel.mCountry = gymObject.getString("country");
                            mModel.mCity = gymObject.getString("city");
                            mModel.mAddress = gymObject.getString("address");
                            mModel.mLat = gymObject.getString("lat");
                            mModel.mLon = gymObject.getString("lon");
                            mModel.mDescription = gymObject.getString("description");
                            mModel.mEnableCode = gymObject.getInt("visitcode");
                            mModel.mId = gymObject.getString("id");
                            try {
                                mModel.mUsability = gymObject.getInt("usability");
                            }
                            catch(Exception e)
                            {
                                mModel.mUsability = 0;
                            }
                            mModel.mAmenity.clear();
                            mModel.mStartHours.clear();
                            mModel.mEndHours.clear();
                            /*Location l1 = new Location("");
                            l1.setLatitude(Double.parseDouble(mModel.mLat));
                            l1.setLongitude(Double.parseDouble(mModel.mLon));
                            mModel.mDistance = Globals.getDistance(Globals.currentLocation,l1);*/

                            JSONArray amenityArray = gymObject.getJSONArray("gym_amenity");
                            JSONArray activityArray = gymObject.getJSONArray("gym_activity");
                            JSONArray studioArray = gymObject.getJSONArray("gym_studio");
                            JSONArray locationArray = gymObject.getJSONArray("gym_location");

                            for (int k = 0;k < amenityArray.length();k++)
                            {
                                JSONObject amenityObject = amenityArray.getJSONObject(k);
                                mModel.mAmenity.add(amenityObject.getString("amenity_id"));
                            }
                            for (int k = 0;k < activityArray.length();k++)
                            {
                                JSONObject amenityObject = activityArray.getJSONObject(k);
                                mModel.mActivity.add(amenityObject.getString("activity_id"));
                            }
                            for (int k = 0;k < studioArray.length();k++)
                            {
                                JSONObject amenityObject = studioArray.getJSONObject(k);
                                mModel.mStudio.add(amenityObject.getString("studio_id"));
                            }
                            for (int k = 0;k < locationArray.length();k++)
                            {
                                JSONObject amenityObject = locationArray.getJSONObject(k);
                                mModel.mLocation.add(amenityObject.getString("location_id"));
                            }


                            for (int j = 0; j < Constants.FIELD_STARTHOUR.length; j++) {
                                String startHour = gymObject.getString(Constants.FIELD_STARTHOUR[j]);
                                String endHour = gymObject.getString(Constants.FIELD_ENDHOUR[j]);
                                mModel.mStartHours.add(startHour);
                                mModel.mEndHours.add(endHour);
                            }

                            Location l1 = new Location("");
                            try {
                                l1.setLatitude(Double.parseDouble(mModel.mLat));
                                l1.setLongitude(Double.parseDouble(mModel.mLon));
                            }
                            catch(Exception e)
                            {
                                l1.setLatitude(0);
                                l1.setLongitude(0);
                            }
                            mModel.mDistance = Globals.getDistance(Globals.currentLocation,l1);


                            if (Globals.isCloseGym(mModel))
                            {
                                Globals.lstOpenGyms.add(mModel);
                            }
                            else Globals.lstCloseGyms.add(mModel);


                        }

                        for (int kk = 0; kk < Globals.lstOpenGyms.size();kk++)
                        {
                            Globals.lstGyms.add(Globals.lstOpenGyms.get(kk));
                        }
                        for (int kk = 0; kk < Globals.lstCloseGyms.size();kk++)
                        {
                            Globals.lstGyms.add(Globals.lstCloseGyms.get(kk));
                        }
                        activity.onSuccessLoad(1);
                    }
                    activity.hideDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                }
            }
        });
    }

    public static void serviceSearchClass(String request, final ILoadService activity) {
        String url = Constants.SEARCHCLASS + "/" + request;
        if (Globals.isLogin == 1)
            url = url + "/" + Globals.mAccount.mId;
        else
            url = url + "/-1";
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {

                activity.hideDialog();
            }

            public void onFinish() {

            }

            public void onSuccess(String paramString) {  //that is return when success..
                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("classes")) {
                        Globals.lstClasses.clear();
                        JSONArray classArray = localJSONObject1.getJSONArray("classes");
                        JSONArray durationArray = localJSONObject1.getJSONArray("duration");
                        JSONArray availableArray = localJSONObject1.getJSONArray("available");
                        JSONArray bookArray = localJSONObject1.getJSONArray("bookArray");
                        String upcomingDate = localJSONObject1.getString("upcoming");
                        Globals.mUpcomingDate = upcomingDate;
                        for (int i = 0; i < classArray.length(); i++) {
                            ClassModel mModel = new ClassModel();
                            JSONObject classObject = classArray.getJSONObject(i);
                            JSONObject categoryObject = null;
                            if (classObject.get("category") != null) {
                                try {
                                    categoryObject = classObject.getJSONObject("category");
                                } catch (Exception e) {
                                    categoryObject = null;
                                }

                            }
                            mModel.mName = classObject.getString("name");
                            mModel.mDate = classObject.getString("date");
                            mModel.mEndDate = classObject.getString("enddate");
                            mModel.mEndHour = classObject.getString("endhour");
                            mModel.mStartHour = classObject.getString("starthour");
                            mModel.mDescription = classObject.getString("description");
                            JSONObject gymObject = classObject.getJSONObject("gym_info");
                            mModel.mImage = gymObject.getString("image");
                            mModel.mGymName = gymObject.getString("name");
                            mModel.mDuration = durationArray.getString(i);
                            mModel.mAvailable = availableArray.getString(i);
                            //mModel.mDescription = gymObject.getString("description");
                            mModel.mIsBook = bookArray.getString(i);
                            mModel.mCity = gymObject.getString("city");
                            mModel.mAddress = gymObject.getString("address");
                            mModel.mCategory = classObject.getString("category");
                            mModel.mId = classObject.getString("id");
                            if (categoryObject != null)
                                mModel.mCategory = categoryObject.getString("category");
                            else
                                mModel.mCategory = "";
                            Globals.lstClasses.add(mModel);
                        }
                        Globals.lstCitys.clear();
                        JSONArray cityArray = localJSONObject1.getJSONArray("cityList");
                        for (int i = 0; i < cityArray.length(); i++) {
                            CityModel cModel = new CityModel();
                            JSONObject cityObject = cityArray.getJSONObject(i);
                            cModel.mId = cityObject.getString("id");
                            cModel.mName = cityObject.getString("city_name");
                            cModel.mLat = cityObject.getString("lat");
                            cModel.mLat = cityObject.getString("lon");

                            Globals.lstCitys.add(cModel);
                        }

                        Globals.lstCategory.clear();
                        JSONArray categoryArray = localJSONObject1.getJSONArray("category");
                        for (int i = 0; i < categoryArray.length(); i++) {
                            CategoryModel lModel = new CategoryModel();
                            JSONObject locationObject = categoryArray.getJSONObject(i);
                            lModel.mId = locationObject.getString("id");
                            lModel.mName = locationObject.getString("category");

                            Globals.lstCategory.add(lModel);
                        }

                        activity.onSuccessLoad(1);
                    }
                    activity.hideDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.hideDialog();
                }
            }
        });
    }


}

package finternet.fitCard.doc;

@SuppressWarnings("unused")
public class Constants {
    //public static String BASE_URL = "http://192.168.1.101:8088/service/";
    // public static String IMAGEBASEURL = "http://192.168.1.101:8088/";

    //public static String BASE_URL = "http://178.62.243.177/service/";
    //public static String IMAGEBASEURL = "http://178.62.243.177/";

    public static String BASE_URL = "https://fitcard.fi/service/";
    public static String BASE_URL1 = "https://fitcard.fi/";
    public static String IMAGEBASEURL = "https://fitcard.fi/";



    //private static final String BASE_URL = "http://s150717x97.imwork.net:8088/service/";
    //public static final String IMAGEBASEURL = "http://s150717x97.imwork.net:8088/";

    public static final String FEATURED_URL = BASE_URL + "serviceFeaturedGym";
    public static final String GYMLIST_URL = BASE_URL + "serviceLoadGym";
    public static final String SEARCHGYM = BASE_URL + "serviceSearchGym";
    public static final String SEARCHCLASS = BASE_URL + "searchClass";
    public static final String LOADGYMREVIEWS = BASE_URL + "serviceLoadReviewForGym";
    public static final String LOADCLASSREVIEWS = BASE_URL + "serviceLoadReviewForClass";
    public static final String LOGINUSER = BASE_URL1 + "api/v1/consumer/api-login";
    public static final String PROFILEUSER = BASE_URL1 + "/api/v1/consumer/get-data";
    public static final String REGISTERUSER = BASE_URL1 + "api/v1/consumer/register";
    public static final String LOGINFACEBOOK = BASE_URL1 + "api/v1/consumer/facebook-callback?code=";
    public static final String LOADPLAN = BASE_URL + "serviceLoadPlan";
    public static final String LOCATION = "http://ip-api.com/json";
    public static final String CHARGEFUND = BASE_URL1 + "api/v1/payment/create_charge/";
    public static final String FUNDCHECK = BASE_URL1 + "api/v1/payment/check_successful";
    public static final String UPDATECREDIT = BASE_URL + "serviceAfterPayment";
    public static final String UPLOADLOGO = BASE_URL + "serviceUploadLogo";
    public static final String UPDATEPROFILE = BASE_URL + "serviceUpdateProfile";
    public static final String BOOKGYM = BASE_URL + "serviceBookGym";
    public static final String BOOKCLASS = BASE_URL + "serviceBookClass";
    public static final String LOADBOOKS = BASE_URL + "serviceLoadBooks";
    public static final String LASTVISITGYM = BASE_URL + "serviceLastVisitGym";
    public static final String LASTVISITCLASS = BASE_URL + "serviceLastVisitClass";
    public static final String CANCELBOOKGYM = BASE_URL + "serviceDeleteGym";
    public static final String CANCELBOOKCLASS = BASE_URL + "serviceDeleteClass";
    public static final String LOADOVERREVIEW = BASE_URL + "serviceLoadOverBooks";
    public static final String REVIEWGYM = BASE_URL + "serviceReviewGym";
    public static final String REVIEWCLASS = BASE_URL + "serviceReviewClass";
    public static final String COUPONCODE = BASE_URL + "serviceCouponCode";
    public static final String CONTACTUS = BASE_URL + "serviceContactUs";
    public static final String CHARGETOKEN = BASE_URL1 + "api/v1/payment/pay_with_token";
    public static final String CLEANCARD = BASE_URL + "serviceCleanCard";


    public static final String[] FIELD_STARTHOUR = {"starthour_sun", "starthour_mon", "starthour_tue", "starthour_wed", "starthour_thu", "starthour_fri", "starthour_sat"};
    public static final String[] FIELD_ENDHOUR = {"endhour_sun", "endhour_mon", "endhour_tue", "endhour_wed", "endhour_thu", "endhour_fri", "endhour_sat"};
    public static final String[] FIELD_CLOSE = {"close_sun", "close_mon", "close_tue", "close_wed", "close_thu", "close_fri", "close_sat"};
    public static final String[] DAYS = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};


}

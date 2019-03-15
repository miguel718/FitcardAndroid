package finternet.fitCard.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.BuildConfig;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import finternet.fitCard.R;
import finternet.fitCard.doc.Constants;
import finternet.fitCard.doc.Enums;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.fragment.BookFragment;
import finternet.fitCard.fragment.ChargeFragment;
import finternet.fitCard.fragment.ClassFragment;
import finternet.fitCard.fragment.ContactUsFragment;
import finternet.fitCard.fragment.GymFragment;
import finternet.fitCard.fragment.HomeFragment;
import finternet.fitCard.fragment.LoginFragment;
import finternet.fitCard.fragment.MembershipFragment;
import finternet.fitCard.fragment.ProfileFragment;
import finternet.fitCard.fragment.RegisterFragment;
import finternet.fitCard.receiver.ReviewReceiver;
import finternet.fitCard.service.ServiceManager;
import finternet.fitCard.view.ActionBarDrawerToggle;
import finternet.fitCard.view.DrawerArrowDrawable;


@SuppressWarnings("ALL")
public class HomeActivity extends FragmentActivity implements View.OnClickListener,LocationListener {
    //public ViewPager viewPager;
    /*public LinearLayout layoutBtnGyms;
    public LinearLayout layoutBtnClasses;
    public LinearLayout layoutBtnAccount;
    public LinearLayout layoutBtnContactUs;*/


    private DrawerLayout mDrawerLayout;
    private RelativeLayout mNavView;
    private Fragment currentFragment;
    public ImageView imgSearch;

    private TextView txtMenuHome;
    private TextView txtMenuGym;
    private TextView txtMenuClass;
    private TextView txtMenuProfile;
    private TextView txtMenuPricing;
    private TextView txtMenuBooks;
    private TextView txtMenuContacts;
    private TextView txtMenuInvite;
    private TextView txtMenuLogout;

    private LinearLayout layoutMenuAccount;
    private ReviewReceiver broadcastReceiver = new ReviewReceiver();

    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    String latitude; // latitude
    String longitude; // longitude

    protected LocationManager locationManager;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public LoginManager loginManager;

    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;
    private AccessToken mAccessToken;
    private Profile pendingUpdateForUser;

    private Profile mProfile = null;
    public Collection<String> permissions = Arrays.asList("public_profile", "user_friends","user_birthday","public_profile", "user_friends", "email", "user_likes");


    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            mAccessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            setProfile(profile,mAccessToken.getToken());
        }

        @Override
        public void onCancel() {
            String ss = "";
            int i = 1;
        }

        @Override
        public void onError(FacebookException error) {
            String ss = "";
            int i = 1;
        }
    };

    private void setProfile(final Profile profile,String token) {
        /*UserModel uModel = new UserModel();
        uModel.mUser = profile.getFirstName() + profile.getLastName();
        uModel.mPassword = "";
        uModel.mAddress1 = "";
        uModel.mAddress2 = "";
        uModel.mCountry = "";
        uModel.mProvince = "";
        uModel.mCity = "";
        uModel.mZipCode = "";
        ServiceManager.onRegisterFacebook(uModel,null,this);*/
        ServiceManager.serviceLoginFacebook(token,this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
        }
        Globals.g_callbackManager = CallbackManager.Factory.create();
        loginManager    =   LoginManager.getInstance();
        setContentView(R.layout.activity_home);

        Constants.DAYS[0] = getResources().getString(R.string.string_sun);
        Constants.DAYS[1] = getResources().getString(R.string.string_mon);
        Constants.DAYS[2] = getResources().getString(R.string.string_tue);
        Constants.DAYS[3] = getResources().getString(R.string.string_wed);
        Constants.DAYS[4] = getResources().getString(R.string.string_thu);
        Constants.DAYS[5] = getResources().getString(R.string.string_fri);
        Constants.DAYS[6] = getResources().getString(R.string.string_sat);
        getSizeFragment();
        initView();
        initActionBar();
        setFragment();


        loginManager.registerCallback(Globals.g_callbackManager,mCallBack);
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldtoken, AccessToken newtoken) {
                mAccessToken = newtoken;
                Profile.fetchProfileForCurrentAccessToken();
            }
        };
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                //setProfile(currentProfile);
                //setProfile(currentProfile);
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finternet.fitcard.CUSTOM_INTENT"));


        if(Build.VERSION.SDK_INT >18 ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }


        Globals.currentLocation = getLocation();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Globals.currentLocation = getLocation();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read GPS", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void getSizeFragment() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Globals.contentWidth = size.x;
        Globals.contentHeight = size.y;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mNavView)) {
                mDrawerLayout.closeDrawer(mNavView);
            } else {
                mDrawerLayout.openDrawer(mNavView);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initActionBar() {
        try {
            ActionBar ab = getActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.topbar)));
            ab.setCustomView(R.layout.layout_actionbar);
            ab.setDisplayShowCustomEnabled(true);
            imgSearch = (ImageView) ab.getCustomView().findViewById(R.id.imgSearchTop);
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this res items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void initView() {
        Globals.e_mode = Enums.MODE.HOME;

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavView = (RelativeLayout) findViewById(R.id.navdrawer);
        DrawerArrowDrawable drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                drawerArrow, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        txtMenuHome = (TextView) this.findViewById(R.id.txtMenuHome);
        txtMenuGym = (TextView) this.findViewById(R.id.txtMenuGyms);
        txtMenuClass = (TextView) this.findViewById(R.id.txtMenuClasses);
        txtMenuProfile = (TextView) this.findViewById(R.id.txtMenuProfile);
        txtMenuPricing = (TextView) this.findViewById(R.id.txtMenuMembership);
        txtMenuBooks = (TextView) this.findViewById(R.id.txtMenuBooks);
        txtMenuContacts = (TextView) this.findViewById(R.id.txtMenuContactUs);
        txtMenuLogout = (TextView) this.findViewById(R.id.txtMenuLogout);
        layoutMenuAccount = (LinearLayout) this.findViewById(R.id.layoutMenuAccount);
        txtMenuInvite = (TextView) this.findViewById(R.id.txtMenuInvite);

        txtMenuHome.setOnClickListener(this);
        txtMenuGym.setOnClickListener(this);
        txtMenuProfile.setOnClickListener(this);
        txtMenuPricing.setOnClickListener(this);
        txtMenuBooks.setOnClickListener(this);
        txtMenuContacts.setOnClickListener(this);
        txtMenuClass.setOnClickListener(this);
        txtMenuLogout.setOnClickListener(this);
        txtMenuInvite.setOnClickListener(this);


        loginMode();

    }

    @SuppressLint("SetTextI18n")
    public void loginMode() {
        if (Globals.isLogin == 0) {
            layoutMenuAccount.setVisibility(View.GONE);
            txtMenuLogout.setText(this.getResources().getString(R.string.string_loginregister));
            txtMenuInvite.setVisibility(View.GONE);
        } else {
            layoutMenuAccount.setVisibility(View.VISIBLE);
            txtMenuLogout.setText(this.getResources().getString(R.string.string_logout));
            txtMenuInvite.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtMenuHome:
                Globals.e_mode = Enums.MODE.HOME;
                setFragment();
                break;
            case R.id.txtMenuGyms:
                Globals.e_mode = Enums.MODE.GYM;
                setFragment();
                break;
            case R.id.txtMenuClasses:
                Globals.e_mode = Enums.MODE.CLASSES;
                setFragment();
                break;
            case R.id.txtMenuBooks:
                Globals.e_mode = Enums.MODE.BOOKS;
                setFragment();
                break;
            case R.id.txtMenuProfile:
                Globals.e_mode = Enums.MODE.PROFILE;
                setFragment();
                break;
            case R.id.txtMenuMembership:
                if (!Globals.isUserOverInvoice())
                {
                    new MaterialDialog.Builder(this)
                            .title("Notice")
                            .content("You can't access during Invoice Period")
                            .show();
                }
                else {
                    Globals.e_mode = Enums.MODE.PRICE;
                    setFragment();
                }
                break;
            case R.id.txtMenuContactUs:
                Globals.e_mode = Enums.MODE.CONTACT;
                setFragment();
                break;
            case R.id.txtMenuLogout:
                if (Globals.isLogin == 1) {
                    Globals.isLogin = 0;
                } else {
                    saveUser();
                }
                loginMode();
                Globals.e_mode = Enums.MODE.LOGIN;
                setFragment();
                break;
            case R.id.txtMenuInvite:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_SUBJECT, "Title");
                i.putExtra(Intent.EXTRA_TEXT   , "Content");
                try {
                    startActivity(Intent.createChooser(i, "Share via..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    //Toast.makeText(this.getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        mDrawerLayout.closeDrawer(mNavView);

    }

    private void saveUser() {
        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        sp.edit().putString("id", "").apply();
        sp.edit().putString("name", "").apply();
        sp.edit().putString("image", "").apply();
        sp.edit().putString("address", "").apply();
        sp.edit().putString("phone", "").apply();
        sp.edit().putString("city", "").apply();
        sp.edit().putString("credit", "").apply();
        sp.edit().putString("plan", "").apply();
        sp.edit().putString("email", "").apply();
        sp.edit().putString("token", "").apply();
        sp.edit().putString("hasCard", "").apply();

    }
    public Location getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {

                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void setFragment() {
        switch (Globals.e_mode) {
            case HOME:
                imgSearch.setVisibility(View.GONE);
                currentFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, currentFragment).commit();
                break;
            case GYM:
                imgSearch.setVisibility(View.VISIBLE);
                currentFragment = new GymFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, currentFragment).commit();
                break;
            case CLASSES:
                imgSearch.setVisibility(View.VISIBLE);
                currentFragment = new ClassFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, currentFragment).commit();
                break;
            case PRICE:
                imgSearch.setVisibility(View.GONE);
                currentFragment = new MembershipFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, currentFragment).commit();
                break;
            case PROFILE:
                imgSearch.setVisibility(View.GONE);
                currentFragment = new ProfileFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, currentFragment).commit();
                break;
            case CONTACT:
                imgSearch.setVisibility(View.GONE);
                currentFragment = new ContactUsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, currentFragment).commit();
                break;
            case LOGIN:
                imgSearch.setVisibility(View.GONE);
                currentFragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, currentFragment).commit();
                break;
            case REGISTER:
                imgSearch.setVisibility(View.GONE);
                currentFragment = new RegisterFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, currentFragment).commit();
                break;
            case BOOKS:
                imgSearch.setVisibility(View.GONE);
                currentFragment = new BookFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, currentFragment).commit();
                break;
            case CHARGE:
                imgSearch.setVisibility(View.GONE);
                currentFragment = new ChargeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, currentFragment).commit();
                break;
        }
    }

    private void setBackFragment() {
        switch (Globals.e_mode) {
            case HOME:
                finish();
                break;
            case GYM:
            case CLASSES:
            case CONTACT:
            case LOGIN:
            case REGISTER:
            case PRICE:
            case BOOKS:
            case PROFILE:
                Globals.e_mode = Enums.MODE.HOME;
                break;
            case CHARGE:
                Globals.e_mode = Enums.MODE.PRICE;
                break;
        }
        setFragment();
    }
    public void startFacebookLogin()
    {
        loginManager.logInWithReadPermissions(this, permissions);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setBackFragment();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Globals.g_callbackManager != null) {
            Globals.g_callbackManager.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (Globals.e_mode == Enums.MODE.CLASSES) {
            ((ClassFragment) currentFragment).loadClass();
        } else if (Globals.e_mode == Enums.MODE.PRICE) {
            setFragment();
        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

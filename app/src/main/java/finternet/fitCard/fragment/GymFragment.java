package finternet.fitCard.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import finternet.fitCard.R;
import finternet.fitCard.activity.DetailGymActivity;
import finternet.fitCard.activity.HomeActivity;
import finternet.fitCard.adapter.AdapterGym;
import finternet.fitCard.adapter.AdapterSearchCity;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.model.ActivityModel;
import finternet.fitCard.model.AmenityModel;
import finternet.fitCard.model.GymModel;
import finternet.fitCard.model.LocationModel;
import finternet.fitCard.model.Model;
import finternet.fitCard.model.StudioModel;
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;


@SuppressWarnings("ALL")
public class GymFragment extends Fragment implements View.OnClickListener, ILoadService,GoogleMap.OnInfoWindowClickListener {

    private static View mRootView;
    private ListView mLstGyms;
    private AdapterGym adapterGym;
    private GoogleMap googleMap;
    private LinearLayout layoutGymMap;
    private LinearLayout layoutSort;
    private TextView txtTab1;
    private TextView txtTab2;

    private TextView txtSort1;
    private TextView txtSort2;
    private RangeSeekBar distanceRangeBar;


    private int mode = 0;
    private int sort = 0;
    private DialogPlus mDlgPost;
    private View mDialogPostView;
    private MaterialDialog mDialog;

    //SearchLayout
    private LinearLayout layoutActivity;
    private LinearLayout layoutCateogry;
    private LinearLayout layoutLocation;
    private LinearLayout layoutStudio;
    private LinearLayout layoutAmenity;

    private LinearLayout layoutSearch;
    private Spinner spCity;
    private AdapterSearchCity adapterSearch;
    private Button btnSearchRun;
    private EditText editKeyword;

    private List<SearchViewHolder> lstLocationHolders = new ArrayList<>();
    private List<SearchViewHolder> lstStudioHolders = new ArrayList<>();
    private List<SearchViewHolder> lstActivityHolders = new ArrayList<>();
    private List<SearchViewHolder> lstAmenityHolders = new ArrayList<>();

    private List<String> mFilterAmenity = new ArrayList<>();
    private List<String> mFilterActivity = new ArrayList<>();
    private List<String> mFilterStudio = new ArrayList<>();
    private List<String> mFilterLocation = new ArrayList<>();


    private int mDistanceMin = -1;
    private int mDistanceMax = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        }
        try {
            mRootView = inflater.inflate(R.layout.fragment_gym, container, false);

        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        initView();
        initSearchDialog();
        showDialog();
        ServiceManager.serviceLoadGyms(this);
        return mRootView;
    }

    private void initSearchDialog() {
        mDlgPost = DialogPlus.newDialog(this.getContext())
                .setContentHolder(new ViewHolder(R.layout.layout_dialog_search))
                .setGravity(Gravity.BOTTOM)
                .setExpanded(true, Globals.contentHeight / 2)
                .setContentHeight(Globals.contentHeight / 2)
                .setBackgroundColorResourceId(R.color.color_white)
                .create();
        mDialogPostView = mDlgPost.getHolderView();

        inflateSearchDialog();
    }

    private void initView() {
        mLstGyms = (ListView) mRootView.findViewById(R.id.lstGyms);
        layoutGymMap = (LinearLayout) mRootView.findViewById(R.id.layoutGymMap);
        txtTab1 = (TextView) mRootView.findViewById(R.id.txtGymTabList);
        txtTab2 = (TextView) mRootView.findViewById(R.id.txtGymTabMap);

        txtSort1 = (TextView) mRootView.findViewById(R.id.txtSortOpen);
        txtSort2 = (TextView) mRootView.findViewById(R.id.txtSortDistance);

        txtSort1.setOnClickListener(this);
        txtSort2.setOnClickListener(this);

        layoutSort = (LinearLayout) mRootView.findViewById(R.id.layoutSort);


        txtTab1.setOnClickListener(this);
        txtTab2.setOnClickListener(this);
        adapterGym = new AdapterGym(this.getActivity());
        mLstGyms.setAdapter(adapterGym);
        ((HomeActivity) getActivity()).imgSearch.setOnClickListener(this);

        googleMap = ((MapFragment) this.getActivity().getFragmentManager().findFragmentById(
                R.id.map1)).getMap();


        setMode();
        sort = 0;
        sort();
        sortByDistance();
        adapterGym.update(Globals.lstGyms);

        mLstGyms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Globals.currentGym = Globals.lstGyms.get(i);
                Intent m = new Intent(GymFragment.this.getContext(), DetailGymActivity.class);
                GymFragment.this.startActivityForResult(m, 1);
            }
        });
        mDialog = new MaterialDialog.Builder(this.getContext())
                .title("Pieni hetki...")
                .content("Ladataan kuntosalin tietoja")
                .progress(true, 0)
                .build();
    }

    private void setMode() {
        if (mode == 0) {
            layoutGymMap.setVisibility(View.GONE);
            mLstGyms.setVisibility(View.VISIBLE);
            layoutSort.setVisibility(View.VISIBLE);
            txtTab1.setTextColor(0xff2798C6);
            txtTab2.setTextColor(0xff000000);
            txtTab1.setBackgroundColor(0xffFAF9F5);
            txtTab2.setBackgroundColor(0xffEFEFEF);
        } else {
            layoutGymMap.setVisibility(View.VISIBLE);
            mLstGyms.setVisibility(View.GONE);
            txtTab1.setBackgroundColor(0xffEFEFEF);
            txtTab2.setBackgroundColor(0xffFAF9F5);
            txtTab2.setTextColor(0xff2798C6);
            txtTab1.setTextColor(0xff000000);
            layoutSort.setVisibility(View.GONE);

        }
    }
    private void sort()
    {
        if (sort == 0) {
            txtSort1.setTextColor(0xff2798C6);
            txtSort2.setTextColor(0xff000000);
        }
        else
        {
            txtSort2.setTextColor(0xff2798C6);
            txtSort1.setTextColor(0xff000000);
        }
    }
    public void sortByDistance()
    {
        if (sort == 0) {
            Globals.sortMode = 1;
            sort = 1;
            Collections.sort(Globals.lstGyms);
            Globals.sortMode = 0;
            sort = 0;
            Collections.sort(Globals.lstGyms);
        }
        else
            Collections.sort(Globals.lstGyms);
        adapterGym.update(Globals.lstGyms);
        addMarker();
        addCurrentPosition();
    }
    public void addCurrentPosition()
    {
        if (googleMap != null) {
            if (Globals.currentLocation != null) {
                LatLng latLng = new LatLng(Globals.currentLocation.getLatitude(), Globals.currentLocation.getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Current Position")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                googleMap.setOnInfoWindowClickListener(this);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layoutScroll:
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.txtGymTabList:
                mode = 0;
                setMode();
                break;
            case R.id.txtGymTabMap:
                mode = 1;
                setMode();
                break;
            case R.id.imgSearchTop:
                mDlgPost.show();
                break;
            case R.id.btnSearchRun:
                mDlgPost.dismiss();
                mDialog.setContent("Etsi kuntosaleja...");
                showDialog();
                searchGym();
                break;
            case R.id.txtSortDistance:
                this.sort = 1;
                Globals.sortMode = 1;
                sort();
                sortByDistance();
                break;
            case R.id.txtSortOpen:
                this.sort = 0;
                Globals.sortMode = 0;
                sort();
                sortByDistance();
                break;
        }
    }

    private void searchGym() {
        String request;
        String keyword = editKeyword.getText().toString();
        if (spCity.getSelectedItemPosition() == 0)
            request = String.valueOf(-1);
        else
            request = Globals.lstCitys.get(spCity.getSelectedItemPosition() - 1).mName;

        if (keyword.equals(""))
            request = request + "/-1";
        else
            request = request + "/" + keyword;
        request = request + "/-1/-1/-1/-1";

        mFilterActivity.clear();
        mFilterAmenity.clear();
        mFilterStudio.clear();
        mFilterLocation.clear();
        for (int i = 0; i < lstActivityHolders.size();i++)
        {
            if (lstActivityHolders.get(i).chkItem.isChecked())
            {
                mFilterActivity.add(((ActivityModel)Globals.lstActivity.get(i)).mId);
            }
        }
        for (int i = 0; i < lstAmenityHolders.size();i++)
        {
            if (lstAmenityHolders.get(i).chkItem.isChecked())
            {
                mFilterAmenity.add(((AmenityModel)Globals.lstAmenity.get(i)).mId);
            }
        }
        for (int i = 0; i < lstStudioHolders.size();i++)
        {
            if (lstStudioHolders.get(i).chkItem.isChecked())
            {
                mFilterStudio.add(((StudioModel)Globals.lstStudio.get(i)).mId);
            }
        }
        for (int i = 0; i < lstLocationHolders.size();i++)
        {
            if (lstLocationHolders.get(i).chkItem.isChecked())
            {
                mFilterLocation.add(((LocationModel)Globals.lstLocations.get(i)).mId);
            }
        }

        /*String locationRequest = "";
        for (int i = 0;i < lstLocationHolders.size();i++)
        {
            if (lstLocationHolders.get(i).chkItem.isChecked())
                locationRequest = locationRequest + "," + ((LocationModel)Globals.lstLocations.get(i)).mId;
        }
        if (locationRequest.equals(""))
            locationRequest = String.valueOf(-1);
        else locationRequest = locationRequest.substring(1);
        request = request + "/" + locationRequest;*/
        mDistanceMin = (int)distanceRangeBar.getSelectedMinValue();
        mDistanceMax = (int)distanceRangeBar.getSelectedMaxValue();

        ServiceManager.serviceSearchGym(request, this);
    }

    private void inflateSearchDialog() {
        layoutSearch = (LinearLayout) mDialogPostView.findViewById(R.id.layoutScroll);
        layoutActivity = (LinearLayout) mDialogPostView.findViewById(R.id.layoutSearchActivity);
        layoutAmenity = (LinearLayout) mDialogPostView.findViewById(R.id.layoutSearchAmenity);
        layoutStudio = (LinearLayout) mDialogPostView.findViewById(R.id.layoutSearchStudio);
        layoutCateogry = (LinearLayout) mDialogPostView.findViewById(R.id.layoutSearchCategory);
        layoutLocation = (LinearLayout) mDialogPostView.findViewById(R.id.layoutSearchLocation);
        spCity = (Spinner) mDialogPostView.findViewById(R.id.spSearchCity);
        btnSearchRun = (Button) mDialogPostView.findViewById(R.id.btnSearchRun);
        editKeyword = (EditText) mDialogPostView.findViewById(R.id.editSearchKeyword);

        distanceRangeBar = (RangeSeekBar) mDialogPostView.findViewById(R.id.rangeDistanceBar);

        btnSearchRun.setOnClickListener(this);
        layoutSearch.setOnClickListener(this);
        adapterSearch = new AdapterSearchCity(this.getActivity());
        spCity.setAdapter(adapterSearch);
        adapterSearch.update(Globals.lstCitys);
        addSearchItems();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


    }
    public boolean isFilter(GymModel gModel)
    {
        for (int i = 0 ;i < gModel.mAmenity.size();i++)
        {
            if (mFilterAmenity.contains(gModel.mAmenity.get(i))) {
                return true;
            }
        }
        for (int i = 0 ;i < gModel.mActivity.size();i++)
        {
            if (mFilterActivity.contains(gModel.mActivity.get(i))) {
                return true;
            }
        }
        for (int i = 0 ;i < gModel.mStudio.size();i++)
        {
            if (mFilterStudio.contains(gModel.mStudio.get(i))) {
                return true;
            }
        }
        for (int i = 0 ;i < gModel.mLocation.size();i++)
        {
            if (mFilterLocation.contains(gModel.mLocation.get(i))) {
                return true;
            }
        }
        if (mFilterActivity.size() == 0 && mFilterAmenity.size() == 0 && mFilterStudio.size() == 0 && mFilterLocation.size() == 0)
            return true;
        return false;
    }
    @Override
    public void onSuccessLoad(int type) {
        if (type == 1)
        {
            List<GymModel> lstFilterGym = new ArrayList<>();
            List<GymModel> lstDistanceFilterGym = new ArrayList<>();
            for (int i = 0;i < Globals.lstGyms.size();i++)
            {
                if (isFilter(Globals.lstGyms.get(i)))
                {
                    lstFilterGym.add(Globals.lstGyms.get(i));
                }
            }
            Globals.lstGyms = lstFilterGym;

            for (int i = 0;i < Globals.lstGyms.size();i++)
            {
                if (Globals.lstGyms.get(i).mDistance > mDistanceMin * 1000 && Globals.lstGyms.get(i).mDistance < mDistanceMax * 1000)
                {
                    lstDistanceFilterGym.add(Globals.lstGyms.get(i));
                }
            }
            Globals.lstGyms = lstDistanceFilterGym;
            sort = 1;
            Collections.sort(Globals.lstGyms);
            sort = 0;
            Collections.sort(Globals.lstGyms);

            adapterGym.update(Globals.lstGyms);
            return;
        }
        sort = 0;
        sort();
        sortByDistance();
        adapterGym.update(Globals.lstGyms);
        inflateSearchDialog();
        addMarker();
        addCurrentPosition();
    }

    private void addMarker() {
        if (googleMap != null) {
            googleMap.clear();
            googleMap.setOnInfoWindowClickListener(this);
            for (int i = 0; i < Globals.lstGyms.size(); i++) {
                LatLng latLng;
                if (Globals.lstGyms.get(i).mLat == null || Globals.lstGyms.get(i).mLat.equals(""))
                    latLng = new LatLng(43, 125);
                else {
                    try {
                        latLng = new LatLng(Double.parseDouble(Globals.lstGyms.get(i).mLat), Double.parseDouble(Globals.lstGyms.get(i).mLon));
                    }catch(Exception e)
                    {
                        latLng = new LatLng(43, 125);
                    }
                }
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker);
                if (Globals.isCloseGym(Globals.lstGyms.get(i)))
                {
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.marker);
                }
                else
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_close);
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(Globals.lstGyms.get(i).mName)
                        .icon(icon));
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        }
    }

    private void addSearchItems() {
        layoutLocation.removeAllViews();
        layoutCateogry.removeAllViews();
        layoutStudio.removeAllViews();
        layoutActivity.removeAllViews();
        layoutAmenity.removeAllViews();

        lstActivityHolders.clear();
        lstAmenityHolders.clear();
        lstLocationHolders.clear();
        lstStudioHolders.clear();

        for (int i = 0; i < Globals.lstLocations.size(); i++) {
            lstLocationHolders.add(addSearchItem(Globals.lstLocations.get(i), layoutLocation));
        }
        for (int i = 0; i < Globals.lstActivity.size(); i++) {
            lstActivityHolders.add(addSearchItem(Globals.lstActivity.get(i), layoutActivity));
        }
        for (int i = 0; i < Globals.lstAmenity.size(); i++) {
            lstAmenityHolders.add(addSearchItem(Globals.lstAmenity.get(i), layoutAmenity));
        }
        for (int i = 0; i < Globals.lstStudio.size(); i++) {
            lstStudioHolders.add(addSearchItem(Globals.lstStudio.get(i), layoutStudio));
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

    @SuppressLint("InflateParams")
    private SearchViewHolder addSearchItem(Model model, LinearLayout layoutParent) {
        View localView;
        SearchViewHolder localViewHolder = null;
        localView = LayoutInflater.from(this.getContext()).inflate(R.layout.item_search, null);

        localViewHolder = new SearchViewHolder();
        localViewHolder.chkItem = (CheckBox) localView.findViewById(R.id.chkSearchItem);
        localView.setTag(localViewHolder);

        localViewHolder.chkItem.setText(model.mName);
        localView.setTag(localViewHolder);
        layoutParent.addView(localView);
        return localViewHolder;
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        for (int i =0;i  <  Globals.lstGyms.size();i++)
        {
            if (marker.getTitle().equals(Globals.lstGyms.get(i).mName))
            {
                Globals.currentGym = Globals.lstGyms.get(i);
                Intent m = new Intent(GymFragment.this.getContext(), DetailGymActivity.class);
                GymFragment.this.startActivityForResult(m, 1);
            }
        }
    }

    public class SearchViewHolder {
        public CheckBox chkItem;
    }
}
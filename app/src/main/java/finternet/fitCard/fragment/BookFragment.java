package finternet.fitCard.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Collections;

import finternet.fitCard.R;
import finternet.fitCard.adapter.AdapterBookClass;
import finternet.fitCard.adapter.AdapterBookGym;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;

public class BookFragment extends Fragment implements View.OnClickListener, ILoadService {

    private View mRootView;
    private TextView txtBookGym;
    private TextView txtBookClass;
    private ListView lstBooks;
    private MaterialDialog mDialog;
    private AdapterBookGym adapterGym;
    private AdapterBookClass adapterClass;
    private int mode = 0;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_books, null);
        }
        initView();
        return mRootView;
    }

    private void initView() {
        mDialog = new MaterialDialog.Builder(this.getContext())
                .title("Pieni hetki...")
                .content("Varauksien avaus, pieni hetki")
                .progress(true, 0)
                .build();
        txtBookClass = (TextView) mRootView.findViewById(R.id.txtBooksClass);
        txtBookGym = (TextView) mRootView.findViewById(R.id.txtBooksGym);
        lstBooks = (ListView) mRootView.findViewById(R.id.lstBooks);

        txtBookClass.setOnClickListener(this);
        txtBookGym.setOnClickListener(this);

        adapterGym = new AdapterBookGym(this.getActivity());
        adapterClass = new AdapterBookClass(this.getActivity());
        showDialog();
        Globals.lstClasses.clear();
        Globals.lstGyms.clear();
        ServiceManager.serviceLoadBooks(this);
        setMode();
    }

    private void setMode() {
        if (mode == 0) {
            txtBookGym.setBackgroundColor(0xffFAF9F5);
            txtBookClass.setBackgroundColor(0xffEFEFEF);
            lstBooks.setAdapter(adapterGym);
        } else if (mode == 1) {
            txtBookClass.setBackgroundColor(0xffFAF9F5);
            txtBookGym.setBackgroundColor(0xffEFEFEF);
            lstBooks.setAdapter(adapterClass);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtBooksClass:
                mode = 1;
                setMode();
                break;
            case R.id.txtBooksGym:
                mode = 0;
                setMode();
                break;
        }
    }

    @Override
    public void onSuccessLoad(int type) {
        adapterGym.update(Globals.lstGyms);
        Collections.sort(Globals.lstClasses);
        adapterClass.update(Globals.lstClasses);
    }

    @Override
    public void showDialog() {
        mDialog.show();
    }

    @Override
    public void hideDialog() {
        mDialog.dismiss();
    }
}

package finternet.fitCard;

import android.app.Application;
import android.provider.SyncStateContract;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import finternet.fitCard.doc.Constants;
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;

public class MainApplication extends Application implements ILoadService{

    @Override
    public void onCreate()
    {
        super.onCreate();
        //ACRA.init(this);

    }

    @Override
    public void onSuccessLoad(int type) {

    }

    @Override
    public void showDialog() {

    }

    @Override
    public void hideDialog() {

    }
}

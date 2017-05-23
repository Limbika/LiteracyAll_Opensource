package com.literacyall.app.Dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.literacyall.app.R;
import com.literacyall.app.activities.MainActivity;
import com.literacyall.app.adapter.ApplicationAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RAFI on 5/17/2016.
 */
public class ListInstalledAppsDialog extends Dialog {

    Context context;
    ListView lstApps;
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter listadaptor = null;

    public ListInstalledAppsDialog(Context context) {
        super(context, R.style.CustomAlertDialog);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        int screenHeight = (int) (metrics.heightPixels * 0.80);
        getWindow().setLayout(screenWidth, screenHeight);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.dialog_list_apps);
        setCancelable(true);

        lstApps = (ListView) findViewById(R.id.lstApps);

        lstApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApplicationInfo app = applist.get(position);

                Toast.makeText(context, app.packageName.toString(), Toast.LENGTH_LONG).show();


                MainActivity mainActivity = (MainActivity) context;
                mainActivity.openApp(app.packageName.toString());
                dismiss();
                /*try {
                    Intent intent = packageManager.getLaunchIntentForPackage(app.packageName);
                    if (null != intent) {
                        context.startActivity(intent);
                    }
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }*/
            }
        });

        packageManager = context.getPackageManager();
        new LoadApplications().execute();

    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listadaptor = new ApplicationAdapter(context, R.layout.snippet_list_row, applist);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            lstApps.setAdapter(listadaptor);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(context, null, context.getString(R.string.loadinApplicationInfo));
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}

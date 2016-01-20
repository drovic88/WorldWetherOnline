package wether.seven.com.wether;

import android.app.DownloadManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    JSONObject jsonObj;
    GridView gridView;
    GridViewAdapter gridAdapter;
    ArrayList<String> outres;
    String[] out;
    String[] out1;
    String[] out2;
    DBS dbs = new DBS(this, "MYDB", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            dbs.deleteDBRows();
            File dir = new File(Environment.getExternalStorageDirectory() + "/seven_c");
            deleteNon_EmptyDir(dir);
            AsyncTask mtask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    String url = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=bangalore&format=json&num_of_days=5&key=329c87ezzdxyx73v8wahx9cm";
                    try {
                        jsonObj = getJsonFromServer(url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    dbs.insertDB(jsonObj);
                    outres = dbs.selectDB("imgurl");
                    out2 = outres.toArray(new String[outres.size()]);
                    for (int i = 0; i < out2.length; i++) {
                        file_download(out2[0], i + 1);
                    }

                    outres = dbs.selectDate();
                    out = outres.toArray(new String[outres.size()]);
                    outres = dbs.selectDB("temperature");
                    out1 = outres.toArray(new String[outres.size()]);

                    try {
                        Thread.sleep(5000);
                        gridView = (GridView) findViewById(R.id.gridView);
                        gridAdapter = new GridViewAdapter(MainActivity.this, R.layout.grid_item_layout, getData(out, out1));
                        gridView.setAdapter(gridAdapter);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            };
            mtask.execute();
        } else {
            outres = dbs.selectDate();
            out = outres.toArray(new String[outres.size()]);
            outres = dbs.selectDB("temperature");
            out1 = outres.toArray(new String[outres.size()]);
            gridView = (GridView) findViewById(R.id.gridView);
            gridAdapter = new GridViewAdapter(MainActivity.this, R.layout.grid_item_layout, getData(out, out1));
            gridView.setAdapter(gridAdapter);
        }
    }

    private ArrayList<ImageItem> getData(String[] o, String[] o1) {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        Bitmap bitmap;
        for (int i = 0; i < imgs.length(); i++) {
            File imgFile = new File("/sdcard/seven_c/image" + (i + 1) + ".png");
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageItems.add(new ImageItem(o1[i] + "\u00b0", bitmap, o[i]));
        }
        return imageItems;
    }

    public static JSONObject getJsonFromServer(String url) throws IOException, JSONException {
        BufferedReader inputStream = null;
        URL jsonUrl = new URL(url);
        URLConnection dc = jsonUrl.openConnection();
        inputStream = new BufferedReader(new InputStreamReader(
                dc.getInputStream()));
        String jsonResult = inputStream.readLine();
        return new JSONObject(jsonResult);
    }

    public void file_download(String uRl, int i) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/seven_c");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Demo")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir("/seven_c", "image" + i + ".png");

        mgr.enqueue(request);
    }

    public static boolean deleteNon_EmptyDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteNon_EmptyDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}

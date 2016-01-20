package wether.seven.com.wether;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class DBS extends SQLiteOpenHelper {
    public static SQLiteDatabase sqLiteDatabase;
    ArrayList<String> wtr;

    public DBS(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table wtr(date VARCHAR,temperature VARCHAR,imgurl VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertDB(JSONObject jsonOBJT) {
        sqLiteDatabase = this.getWritableDatabase();
        String imgurl;
        try {
            JSONArray a = jsonOBJT.getJSONObject("data").getJSONArray("weather");
            for (int i = 0; i < a.length(); i++) {
                ContentValues values = new ContentValues();
                values.put("date", a.getJSONObject(i).getString("date"));
                values.put("temperature", a.getJSONObject(i).getString("tempMaxC"));

                JSONObject j1 = new JSONObject(a.getJSONObject(i).getString("weatherIconUrl").replace("[", "").replace("]", ""));
                imgurl = j1.getString("value");
                values.put("imgurl", imgurl);
                sqLiteDatabase.insert("wtr", null, values);
            }
//            MainActivity.textView.setText(a.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> selectDate() {
        wtr = new ArrayList<String>();
        sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("select date from wtr", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                wtr.add(c.getString(0).substring(8));
            } while (c.moveToNext());
        }
        return wtr;
    }

    public ArrayList<String> selectDB(String type) {
        wtr = new ArrayList<String>();
        sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("select " + type + " from wtr", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                wtr.add(c.getString(0));
            } while (c.moveToNext());
        }
        return wtr;
    }

    public void deleteDBRows() {
        sqLiteDatabase = this.getReadableDatabase();
        sqLiteDatabase.execSQL("delete from wtr");
    }
}


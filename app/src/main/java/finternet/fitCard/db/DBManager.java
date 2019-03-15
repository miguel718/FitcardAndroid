package finternet.fitCard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;
import java.util.List;


public class DBManager {
    SQLiteDatabase database;
    String createTable = "create table playlist (id integer primary key AUTOINCREMENT ,title string);";

    String createMusic = "create table songlist (id integer primary key AUTOINCREMENT ,pid integer,sid string,title string,surl string,durl string,avatar string);";

    String createFavourite = "create table favourite (id integer primary key AUTOINCREMENT ,sid string,title string,surl string,durl string,avatar string);";

    String createRecentPlay = "create table recentplay (id integer primary key AUTOINCREMENT ,sid string,title string,surl string,durl string,avatar string);";


    String dbname = "rhydb";
    UsageSQLLite helper;
    SQLiteDatabase db;

    public DBManager() {

    }

    public DBManager(Context con) {
        helper = new UsageSQLLite(con, dbname, null, 1);
    }

    /*public void insertPlaylistData(PlaylistModel model) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", model.mTitle);
        db.insert("playlist", null, values);
    }

    public void updatePlaylistData(PlaylistModel model) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", model.mTitle);
        String[] query = new String[1];
        query[0] = String.valueOf(model.mPid);
        db.update("playlist", values, "id=?", query);
    }

    public void deletePlaylistData(int id) {
        db = helper.getWritableDatabase();
        String[] values = new String[1];
        values[0] = String.valueOf(id);
        db.delete("playlist", "id=?", values);
    }

    public boolean insertSonglistData(TrackModel model) {
        if (isAddedPlaylist(model.mId,model.mPid))
            return false;
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", model.mTitle);
        values.put("pid", model.mPid);
        values.put("sid", model.mId);
        values.put("surl", model.mStreamUrl);
        values.put("durl", model.mDownloadUrl);
        values.put("avatar", model.mImage);

        db.insert("songlist", null, values);
        return true;
    }

    public void updateSonglistData(TrackModel model) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", model.mTitle);
        values.put("pid", model.mPid);
        values.put("sid", model.mId);
        values.put("surl", model.mStreamUrl);
        values.put("durl", model.mDownloadUrl);
        values.put("avatar", model.mImage);


        String[] query = new String[1];
        query[0] = String.valueOf(model.mSid);
        db.update("songlist", values, "id=?", query);
    }

    public void deleteSonglistData(int id) {
        db = helper.getWritableDatabase();
        String[] values = new String[1];
        values[0] = String.valueOf(id);
        db.delete("songlist", "id=?", values);
    }


    public boolean insertFavouriteData(TrackModel model) {
        if (isFavourite(model.mId))
            return false;
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", model.mTitle);
        values.put("sid", model.mId);
        values.put("surl", model.mStreamUrl);
        values.put("durl", model.mDownloadUrl);
        values.put("avatar", model.mImage);

        db.insert("favourite", null, values);
        return true;
    }

    public void updateFavouriteData(TrackModel model) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", model.mTitle);
        values.put("pid", model.mPid);
        values.put("sid", model.mId);
        values.put("surl", model.mStreamUrl);
        values.put("durl", model.mDownloadUrl);
        values.put("avatar", model.mImage);


        String[] query = new String[1];
        query[0] = String.valueOf(model.mFid);
        db.update("favourite", values, "id=?", query);
    }

    public void deleteFavouriteData(int id) {
        db = helper.getWritableDatabase();
        String[] values = new String[1];
        values[0] = String.valueOf(id);
        db.delete("favourite", "id=?", values);
    }

    public void insertRecentPlayData(TrackModel model) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", model.mTitle);
        values.put("pid", model.mPid);
        values.put("sid", model.mId);
        values.put("surl", model.mStreamUrl);
        values.put("durl", model.mDownloadUrl);
        values.put("avatar", model.mImage);

        db.insert("recentplay", null, values);
    }

    public void updateRecentPlayData(TrackModel model) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", model.mTitle);
        values.put("pid", model.mPid);
        values.put("sid", model.mId);
        values.put("surl", model.mStreamUrl);
        values.put("durl", model.mDownloadUrl);
        values.put("avatar", model.mImage);


        String[] query = new String[1];
        query[0] = String.valueOf(model.mRid);
        db.update("recentplay", values, "id=?", query);
    }

    public void deleteRecentPlayData(int id) {
        db = helper.getWritableDatabase();
        String[] values = new String[1];
        values[0] = String.valueOf(id);
        db.delete("recentplay", "id=?", values);
    }


    public boolean isAddedPlaylist(String sid, int pid)
    {
        db = helper.getReadableDatabase();
        String[] query = new String[2];
        query[0] = String.valueOf(pid);
        query[1] = sid;
        Cursor c = db.rawQuery("SELECT * FROM `songlist` where pid=? and sid=? order by id desc",query);

        c.moveToFirst();
        if (c.getCount() > 0) {
            return true;
        }
        else return false;
    }

    public boolean isFavourite(String sid)
    {
        db = helper.getReadableDatabase();
        String[] query = new String[1];
        query[0] = sid;
        Cursor c = db.rawQuery("SELECT * FROM `favourite` where sid=? order by id desc",query);
        c.moveToFirst();
        if (c.getCount() > 0) {
            return true;
        }
        else return false;
    }




    public List<PlaylistModel> getAllPlaylist()
    {
        db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM `playlist` order by id desc",null);
        List<PlaylistModel> list = new ArrayList<PlaylistModel>();
        c.moveToFirst();
        if (c.getCount() > 0) {
            do {
                PlaylistModel temp = new PlaylistModel();
                temp.mPid = c.getInt(c.getColumnIndex("id"));
                temp.mTitle = c.getString(c.getColumnIndex("title"));
                list.add(temp);
            }while(c.moveToNext());
            c.close();
            return list;
        }
        else return list;
    }

    public List<String> getAllPlaylistString()
    {
        db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM `playlist` order by id desc",null);
        List<String> list = new ArrayList<String>();
        c.moveToFirst();
        if (c.getCount() > 0) {
            do {
                String temp = new String();
                temp = c.getString(c.getColumnIndex("title"));
                list.add(temp);
            }while(c.moveToNext());
            c.close();
            return list;
        }
        else return list;
    }


    public List<TrackModel> getAllFavourite()
    {
        db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM `favourite` order by id desc",null);
        List<TrackModel> list = new ArrayList<TrackModel>();
        c.moveToFirst();
        if (c.getCount() > 0) {
            do {
                TrackModel temp = new TrackModel();
                temp.mId = c.getString(c.getColumnIndex("sid"));
                temp.mTitle = c.getString(c.getColumnIndex("title"));
                temp.mStreamUrl = c.getString(c.getColumnIndex("surl"));
                temp.mDownloadUrl = c.getString(c.getColumnIndex("durl"));
                temp.mImage = c.getString(c.getColumnIndex("avatar"));
                temp.mFid = c.getInt(c.getColumnIndex("id"));
                list.add(temp);
            }while(c.moveToNext());
            c.close();
            return list;
        }
        else return list;
    }
    public List<TrackModel> getAllRecentPlay() {
        db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM `recentplay` order by id desc", null);
        List<TrackModel> list = new ArrayList<TrackModel>();
        c.moveToFirst();
        if (c.getCount() > 0) {
            do {
                TrackModel temp = new TrackModel();
                temp.mId = c.getString(c.getColumnIndex("sid"));
                temp.mTitle = c.getString(c.getColumnIndex("title"));
                temp.mStreamUrl = c.getString(c.getColumnIndex("surl"));
                temp.mDownloadUrl = c.getString(c.getColumnIndex("durl"));
                temp.mImage = c.getString(c.getColumnIndex("avatar"));
                temp.mRid = c.getInt(c.getColumnIndex("id"));
                list.add(temp);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }
    public List<TrackModel> getPlaylistMusic(int mPid)
    {
        db = helper.getReadableDatabase();
        String[] values = new String[1];
        values[0] = String.valueOf(mPid);
        Cursor c = db.rawQuery("SELECT * FROM `songlist` order by id desc where pid=?", values);
        List<TrackModel> list = new ArrayList<TrackModel>();
        c.moveToFirst();
        if (c.getCount() > 0) {
            do {
                TrackModel temp = new TrackModel();
                temp.mId = c.getString(c.getColumnIndex("sid"));
                temp.mTitle = c.getString(c.getColumnIndex("title"));
                temp.mStreamUrl = c.getString(c.getColumnIndex("surl"));
                temp.mDownloadUrl = c.getString(c.getColumnIndex("durl"));
                temp.mImage = c.getString(c.getColumnIndex("avatar"));
                temp.mPid = c.getInt(c.getColumnIndex("pid"));
                temp.mSid = c.getInt(c.getColumnIndex("id"));
                list.add(temp);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }*/
    public class UsageSQLLite extends SQLiteOpenHelper
    {
        public UsageSQLLite(Context con,String name,CursorFactory factory,int version)
        {
            super(con,name,factory,version);
        }

        @Override
        public void onCreate(SQLiteDatabase arg0) {
            // TODO Auto-generated method stub
            arg0.execSQL(createTable);
            arg0.execSQL(createMusic);
            arg0.execSQL(createFavourite);
            arg0.execSQL(createRecentPlay);

        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub

        }
    }
}

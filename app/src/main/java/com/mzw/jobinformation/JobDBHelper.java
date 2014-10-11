package com.mzw.jobinformation;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JobDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME ="JobInfo";
    private static final int DATABASE_VERSION = 1;

    public JobDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE job_collection (id text not null, " +
                "date text no null, " +
                "title text no null, " +
                "comp text no null, " +
                "type text no null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS job_collection");
        onCreate(sqLiteDatabase);
    }
}

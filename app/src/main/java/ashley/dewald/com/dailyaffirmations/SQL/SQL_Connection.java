package ashley.dewald.com.dailyaffirmations.SQL;

import android.content.Context;
import android.database.sqlite.*;
import android.util.Log;

/*
 * Helper class, creates connection to SQLite database.
 *
 * @version 1.0
 * @author Ashley Dewald
 */
public class SQL_Connection extends SQLiteOpenHelper {
    private static final String TAG = SQL_Connection.class.getCanonicalName();

    public static final String DB_NAME = "affirmations.db";
    private static final int VERSION = 1;

    public SQL_Connection(Context context){
        super(context, DB_NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
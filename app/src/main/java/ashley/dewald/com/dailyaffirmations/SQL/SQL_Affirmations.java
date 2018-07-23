package ashley.dewald.com.dailyaffirmations.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ashley.dewald.com.dailyaffirmations.Affrimation.Affirmation;
import ashley.dewald.com.dailyaffirmations.Affrimation.AffirmationContract;
/*
 * This class does the bulk of the work when it comes to working with our SQLite database.
 *
 * @version 1.0
 * @author Ashley Dewald
 */
public class SQL_Affirmations extends SQL_Engine {
    public SQL_Affirmations(Context context) {
        super(context);
    }

    /*
     * Creates a new table based on the names that is passed.
     */
    public void createNewTable(String AffirmationSet) {
        String command = AffirmationContract.tableScheme(AffirmationSet);
        database.execSQL(command);
    }

    /*
     * Inserts data into the Affirmation table.
     * @returns a boolean to indicate whether the operation was successful or not.
     */
    public boolean insert(String table, Affirmation affirmation) {
        try {
            ContentValues values =
                    AffirmationContract.insertValues(
                                                        affirmation.getAffirmation(),
                                                        affirmation.getIsSelected()
                                                    );
            // NOTE: The brackets ('[' & ']') allows the user to use table names with spaces
            // with-out processing the spaces out of the char array.
            database.insert("[" + table + "]", null , values);

            return true;
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
            return false;
        }
    }

    /*
     * Reads data from SQLite table.
     * @param table Name of table for look up.
     * @return Returns a list of affirmations found in the table.
     */
    public List<Affirmation> readTable(String table) {
        try {
            List<Affirmation> affirmations = new ArrayList<>();

            // We start off getting reading the constant 'readScheme' from 'AffirmationContract',
            // than initializing a rawQuery and drop that values into our cursor value.
            //
            // NOTE: The reason why we are using rawQuery instead of query is because of the control
            // it allows us to have over the syntax we pass. The command string that is passed is
            // '[' + table + ']'. SQLite.query freaks outs when because of the brackets, and the
            // brackets allow us to simply read a table value with spaces, avoiding have to convert
            // the string into a new SQLite friendly string value.
            String command = AffirmationContract.readScheme(table);
            Cursor c = database.rawQuery(command, null,null);

            // We move the cursor to begging.
            c.moveToFirst();

            // We use the constants in 'AffirmationContract' to set the index values of the
            // 'AffirmationIndex', and 'AffirmationSelectedIndex'.
            final int affirmationIndex = c.getColumnIndex(AffirmationContract.Col.Affirmation);
            final int affirmationSelectedIndex = c.getColumnIndex(AffirmationContract.Col.Selected);

            // We loop though the cursor, after each loop we check to see if we have reached the
            // final value in the cursor.
            while(!c.isAfterLast()){
                String affirmation = c.getString(affirmationIndex);

                // SQLite does not support boolean value, so we read it as an 'int' and do a quick
                // check from there.
                boolean selected = c.getInt(affirmationSelectedIndex) == 1;

                // We save the affirmation values, and add it to our ArrayList.
                Affirmation affirmationItem = new Affirmation(affirmation, selected);
                affirmations.add(affirmationItem);

                // We move to cursor to the next index.
                c.moveToNext();
            }

            return affirmations;

        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
    }
}

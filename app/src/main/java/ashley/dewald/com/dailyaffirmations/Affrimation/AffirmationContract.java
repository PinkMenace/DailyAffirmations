package ashley.dewald.com.dailyaffirmations.Affrimation;

import android.content.ContentValues;

/*
 * Helper functions, and constants to be used when interacting with our SQL database so mistakes are
 * not made as often. As a side effect, this helps make debugging easier, since we can have the
 * expectation of how thing are going to work.
 *
 * @version 1.0
 * @author Ashley Dewald
 */
public class AffirmationContract {
    public static String tableScheme(String name){
        // NOTE: The brackets ('[' & ']') allows the user to use table names with spaces
        // with-out processing the spaces out of the char array.
        String createTableCommand = "Create TABLE IF NOT EXISTS [" + name + "] (" +
                Col.Affirmation + " VARCHAR NOT NULL, " +
                Col.Selected + " INTEGER);";

        return createTableCommand;
    }

    public static ContentValues insertValues(String Affirmation, boolean Selected){
        ContentValues values = new ContentValues();
        values.put(AffirmationContract.Col.Affirmation, String.valueOf(Affirmation));
        values.put(AffirmationContract.Col.Selected, String.valueOf(Selected ? 1 : 0));

        return values;
    }

    public static String readScheme(String table){
        return "SELECT * FROM [" + table + "]";
    }

    public static final class Col{
        public static final String Affirmation = "Affirmation";
        public static final String Selected = "Selected";
    }
}

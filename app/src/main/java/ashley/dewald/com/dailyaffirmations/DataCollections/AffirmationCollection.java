package ashley.dewald.com.dailyaffirmations.DataCollections;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.nio.channels.NotYetBoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ashley.dewald.com.dailyaffirmations.Affrimation.Affirmation;
import ashley.dewald.com.dailyaffirmations.SQL.Default_SQL_Database;
import ashley.dewald.com.dailyaffirmations.SQL.SQL_Affirmations;

public class AffirmationCollection {
    public String TAG = getClass().getCanonicalName();
    public static final int NOT_FOUND = -1;

    private SQL_Affirmations database;
    private List<AffirmationData> dAffirmationSet;  // Since 'AffirmationData' is nothing more than a
                                                    // glorified ArrayList the best way to look at
                                                    // is a List of Lists, or
                                                    // List<ArrayList<Affirmation>>

    public AffirmationCollection(Context context) {
        database = new SQL_Affirmations(context);

        readDatabase();
    }

    /*
     * Reads the contents of the database into the affirmation data sets
     *
     */
    public void readDatabase() {
        try {
            // We start off by getting probing the database and getting all the table names.
            List<String> tableNames = database.getAllTableNames();
            dAffirmationSet = new ArrayList<>();

           if(tableNames.size()==0) {
               Log.d(TAG, "No table data from database found.");
               return;
           }

            for (String name : tableNames) {
                // We use each of the strings we got from tableNames to probe the database and get the
                // affirmation contents.
                List<Affirmation> affirmationsList = database.readTable(name);

                // We than add the data to an AffirmationData set, than we add the AffirmationData set
                // to our list of AffirmationData sets.
                AffirmationData data = new AffirmationData(name, affirmationsList);

                dAffirmationSet.add(data);
            }

        } catch (Exception e) {
            return;
        }
    }

    /*
     * Allows the user to save the data from their collection of affirmation data to the internal
     * database.
     *
     * @param list Allows the user to pass in new list of collection of affirmation data to be saved.
     * if null is passed we save data from this affirmation collection class.
     */
    public void saveData(@Nullable List<AffirmationData> list){
        try{
            // We start off by checking if the list parameter is null.
            if(list == null) {
                if (dAffirmationSet.size() == 0) {
                    Log.d(TAG, "Something went wrong. The internal affirmation set was null, " +
                            "and nothing was passed to this function.");
                    return;
                }

                // If list is null, we copy the data from the affirmationSet to override the null
                // reference of the 'list' parameter variable.
                list = dAffirmationSet;
            }

            // We start off by dropping all the tables. Our AffirmationData set list holds
            // all the references to everything we are working with, so their is not reason, at least
            // with this application, to spend the time going through each and every item to update
            // it. Instead all we do is drop all the tables, and set a ( hopefully updated) copy the data
            // back into the database.
            database.dropAllTables();

            for(AffirmationData data: list){
                // We start off by creating a new table with the header of the value we are working
                // with.
                String dataSetName = data.getHeaderName();
                database.createNewTable(dataSetName);

                // When than insert all the affirmation values into the selected table.
                for(Affirmation affirmation: data){
                    database.insert(dataSetName, affirmation);
                }
            }
        }catch (Exception e){
            return;
        }
    }

    /*
     * Allows the user to save the data from this Affirmation data class to the database.
     *
     */
    public void saveData(){
        saveData(null);
    }

    /*
     * Rebuilds the database to it's default state.
     *
     */
    public void rebuildDatabase(){
        try {
            // We start off by dropping all the tables in the database.
            database.dropAllTables();

            // We get the default data from "Default_SQL_Database" class.
            List<AffirmationData> dataSet = new Default_SQL_Database().getData();

            Random r = new Random();
            dAffirmationSet = new ArrayList<>();

            // When we rebuild the data-set, we randomly select affirmations and enter everything
            // into memory.
            for(AffirmationData data: dataSet){

                createNewSet(data.getHeaderName());

                for(Affirmation a: data){

                    addToSet(data.getHeaderName(), a);

                    if(26 <= r.nextInt() % 100){
                        a.setIsSelected(true);
                    }
                }
            }

            // We finish up by saving the data back-into to the database.
            saveData(dataSet);
        }catch(Exception e){
            return;
        }
    }

    public int size(){return dAffirmationSet.size(); }

    public SQL_Affirmations getDatabase() {
        return database;
    }

    /*
     * Gets the list of data set name that are in use.
     *
     * @return List<String> Returns a list of strings.
     */
    public List<String> getDataSetNames(){
        // All this functions does is create a list of availableSets and loops through the affirmation
        // data list and add the names to list.
        List<String> availableSets = new ArrayList<>();
        for(AffirmationData set: dAffirmationSet){
            availableSets.add(set.getHeaderName());
        }
        return availableSets;
    }

    public List<Affirmation> getSelectedData() throws Exception {
        if (dAffirmationSet != null) {
            if (database == null) {
                throw new Exception();
            }
            readDatabase();
        }

        List<Affirmation> selectedAffirmations = new ArrayList<>();

        for (AffirmationData dataSet : dAffirmationSet) {

            for (Affirmation affirmation : dataSet) {

                if (affirmation.getIsSelected()) {
                    selectedAffirmations.add(affirmation);
                }
            }
        }

        return selectedAffirmations;
    }

    /*
     * Retrieves the set at the index we pass. Returns a null if the index we are looking for is
     * out of bounds.
     *
     * @param index The index value of the set we are looking for.
     * @return Returns a AffirmationSet list
     */
    public AffirmationData getSet(int index)
    {
        try {
            if (index < 0 || size() < index)
                throw new Exception();

            return dAffirmationSet.get(index);
        }catch (Exception e){
            // I went with a bit of a 'excessive' detailed error handling here. Since all transactions
            // with this function is bound through getSet(String setName), which uses 'findSetIndex()'.
            // We should never reach this code, so if something does go wrong somehow this should help
            // at least give us an idea of where to start looking for the issue.
            String line = "Index is out of bounds, ";

            if(index < 0){
                line += "index value needs to be a positive number.";
            }else{
                line += "index value needs to less than the size() of the collection.\n";
            }

            line += "Index Value: " + index + "\nList size: " + size();

            Log.e(TAG, line);

            return new AffirmationData("NULL");
        }
    }

    /*
     * Retrieves the set with the name we pass.
     *
     * @param setName Gets the name of the set we are looking for.
     * @return Returns a AffirmationSet list
     */
    public AffirmationData getSet(String setName){
        int index = findSetIndex(setName);
        if(index != NOT_FOUND){
            return getSet(index);
        }

        return null;
    }


    /*
     * Allows to the user to create create a new data set with a specified name.
     *
     * @param setName Name of the new data set.
     * @return Returns whether the operation was successful
     *
     */
    public boolean createNewSet(String setName){
        // We start off by searching through the list to see if the setName already exists in the
        // list. If not, we create a new table with the name, otherwise we exit the function.
        if(!setExists(setName))
        {
            AffirmationData newSet = new AffirmationData(setName);
            dAffirmationSet.add(newSet);
            return true;
        }

        return false;
    }


    /*
     * Allows the user to pass the set name we are looking to remove. Function utilises
     * 'findSetIndex()' to find the index value that is need to remove.
     *
     * @param setName Searches for the index value of the set name we want to remove.
     * @return Returns whether the operation was successful
     *
     */
    public boolean removeSet(String setName){
        int index = findSetIndex(setName);
        if(index != NOT_FOUND)
        {
            return removeSet(index);
        }

        return false;
    }

    /*
     * Removes the affirmation set by index.
     *
     * @param index The index where the set we which to remove is stored.
     * @return Return whether the operation was successful
     *
     */
    public boolean removeSet(int index){
        if(index < 0 || size() < index)
            throw new IndexOutOfBoundsException();

        dAffirmationSet.remove(index);
        return true;
    }


    /*
     * Allow the user to add values to the set.
     *
     * @param setName Name of the Affirmation collection.
     * @param newAffirmation Allows the user to pass a new affirmation to be added to the set..
     * @return Return whether the operation was successful
     */
    public boolean addToSet(String setName, Affirmation newAffirmation){
        int index = findSetIndex(setName);
        if(index != NOT_FOUND){
            dAffirmationSet.get(index).add(newAffirmation);
            return true;
        }

        return false;
    }

    /*
     * Allow the user to add values to the set.
     *
     * @param setName Name of the Affirmation collection.
     * @param AffirmationString The string for the affirmation we are adding.
     * @return Return whether the operation was successful
     */
    public boolean addToSet(String setName, String AffirmationString){
        // Converts our affirmation string in an affirmation object.
        Affirmation newAffirmation = new Affirmation(AffirmationString);
        return addToSet(setName, newAffirmation);
    }

    /*****************************************************/
    /******************Helper Functions*******************/
    /*****************************************************/

    /*
     *
     * @return Returns true/false whether or not the set exists in the list.
     */
    public boolean setExists(String setName){
        return findSetIndex(setName) != NOT_FOUND;
    }

    /*
     * Searches through the AffirmationCollection's looking the specified affirmation list index.
     *
     * @return Returns the index value of where the set we are looking for is located.
     */
    public int findSetIndex(String setName){
        for(int x = 0; x < dAffirmationSet.size(); x++){
            String testSet = dAffirmationSet.get(x).getHeaderName();
            if(setName.equalsIgnoreCase(testSet))
                return x;
        }

        return -1;
    }
}
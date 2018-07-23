package ashley.dewald.com.dailyaffirmations;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.drm.DrmStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import ashley.dewald.com.dailyaffirmations.Affrimation.Affirmation;
import ashley.dewald.com.dailyaffirmations.Affrimation.RecyclerAdapter;
import ashley.dewald.com.dailyaffirmations.BackGroundService.AffirmationService;
import ashley.dewald.com.dailyaffirmations.DataCollections.AffirmationCollection;
import ashley.dewald.com.dailyaffirmations.DataCollections.AffirmationData;
import ashley.dewald.com.dailyaffirmations.msic.SimpleItemTouchHelperCallBack;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getCanonicalName();

    /****************************************************************************/
    /************************Android UI Components*******************************/
    /****************************************************************************/

    /**
     * Buttons Components
     **/
    private Button mAddNewAffirmationButton;
    private Button mAddNewAffirmationGroupButton;

    /**
     * Spinner Components
     **/
    private Spinner mAffirmationSpinner; // AffirmationSpinner;
    private ArrayAdapter<String> mAffirmationSpinnerAdapter;
    private List<String> mAffirmationSpinnerData;

    /**
     * Recycler View Components
     **/
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAffirmationAdapter;

    /****************************************************************************/
    /************************ Variables *****************************************/
    /****************************************************************************/

    private AffirmationCollection dataCollection;
    private boolean startBackGroundService;

    /****************************************************************************/
    /************************Android Activity Functions**************************/
    /****************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataCollection = new AffirmationCollection(this);
        startBackGroundService = true;

        initializeComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);

        MenuItem toggleItem = menu.findItem(R.id.toggleNotifications);
        if (AffirmationService.isServiceAlarmOn(this)) {
            toggleItem.setTitle(R.string.turnOffNotifications);
            startBackGroundService = true;
        }
        else {
            toggleItem.setTitle(R.string.turnOnNotifications);
            startBackGroundService = false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.toggleNotifications:
                invalidateOptionsMenu();
                return true;
            case R.id.rebuildDatabase:
                dataCollection.rebuildDatabase();
                initializeSpinner();
                return true;
            case R.id.saveDatabase:
                dataCollection.saveData(null);
                return true;
            case R.id.dropTable:
                String spinnerItem = mAffirmationSpinner.getItemAtPosition(mAffirmationSpinner.getSelectedItemPosition()).toString();
                dataCollection.removeSet(spinnerItem);
                initializeSpinner();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();

        dataCollection.saveData(null);

        AffirmationService service = new AffirmationService();
        AffirmationService.setServiceAlarm(this, startBackGroundService);
    }

    /****************************************************************************/
    /************************Components Initialization***************************/
    /****************************************************************************/

    private void initializeComponents() {
        initializeButtons();
        initializeSpinner();
        initializeRecyclerView();
    }

    private void initializeButtons() {
        mAddNewAffirmationButton = (Button) findViewById(R.id.addNewAffirmation);
        mAddNewAffirmationGroupButton = (Button) findViewById(R.id.addAffirmationGroup);

        mAddNewAffirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBox(ActionCode.AddToDataCollection).show();
            }
        });

        mAddNewAffirmationGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBox(ActionCode.AddToSpinner).show();
            }
        });
    }

    private void initializeSpinner() {
        mAffirmationSpinner = findViewById(R.id.affirmationSpinner);
        mAffirmationSpinnerData = new ArrayList<>();

        for (int x = 0; x < dataCollection.getDataSetNames().size(); x++) {
            mAffirmationSpinnerData.add(dataCollection.getDataSetNames().get(x));
        }

        mAffirmationSpinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, mAffirmationSpinnerData);
        mAffirmationSpinner.setAdapter(mAffirmationSpinnerAdapter);

        mAffirmationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void initializeRecyclerView() {
        PopupMenu menu = new PopupMenu(this, mRecyclerView);
        menu.getMenuInflater().inflate(R.menu.affirmation_recycler_view_menu, menu.getMenu());

        mRecyclerView = findViewById(R.id.affirmationsRecyclerView);
        mAffirmationAdapter = new RecyclerAdapter(this);

        mRecyclerView.setAdapter(mAffirmationAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateRecyclerView();

        ItemTouchHelper.Callback callBack = new SimpleItemTouchHelperCallBack(mAffirmationAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callBack);
        touchHelper.attachToRecyclerView(mRecyclerView);
    }

    /****************************************************************************/
    /************************Component Updates***********************************/
    /****************************************************************************/

    public void updateRecyclerView() {
        if (mAffirmationAdapter == null || dataCollection.size() == 0)
            return;

        int spinnerPosition = mAffirmationSpinner.getSelectedItemPosition();
        String spinnerItemName = mAffirmationSpinner.getItemAtPosition(spinnerPosition).toString();
        AffirmationData list = dataCollection.getSet(spinnerItemName);

        mAffirmationAdapter.setData(list);
    }

    /*
     * Adds an item to the affirmation group.
     */
    private void addItemToSpinner(String newAffirmationGroup) {
        mAffirmationSpinnerData.add(newAffirmationGroup);
        dataCollection.createNewSet(newAffirmationGroup);

        // We set the spinner index to '0', than we notify that we have updated the data set, otherwise
        // our spinner item will not display any items.
        mAffirmationSpinner.setSelection(0);
        mAffirmationSpinnerAdapter.notifyDataSetChanged();
    }

    /*
     * Removes an item from the affirmation group.
     */
    private void removeItemFromSpinner(String AffirmationGroupToRemove) {
        mAffirmationSpinnerData.remove(AffirmationGroupToRemove);

        // We set the spinner index to '0', than we notify that we have updated the data set, otherwise
        // our spinner item will not display any items.
        mAffirmationSpinner.setSelection(0);
        mAffirmationSpinnerAdapter.notifyDataSetChanged();
    }

    private void addItemToDataCollection(String newAffirmation) {
        int spinnerPosition = mAffirmationSpinner.getSelectedItemPosition();
        String spinnerItem = mAffirmationSpinner.getItemAtPosition(spinnerPosition).toString();
        dataCollection.addToSet(spinnerItem, newAffirmation);
        mAffirmationAdapter.notifyDataSetChanged();
    }

    /****************************************************************************/
    /***************************Alert DialogBox**********************************/
    /****************************************************************************/
    /* Alert dialog box that open when one of the two buttons is pressed, allowing for user input.
     *
     *@param action
     *
     */
    // NOTE: Was not sure how to get pass the bad design here of having to pass a 'action' code to
    // the function. It was either pass an action code or make a copy of the function with the only
    // change being that one opens up the 'addItemToSpinner()' and the other one
    // 'addItemToDataCollection(). All attempts to separate and make 'DialogInterface.OnClickListener()'
    // a parameter failed.
    private AlertDialog dialogBox(final ActionCode code) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialolg_box_prompt, null);

        final EditText input = view.findViewById(R.id.input);

        TextView textView = view.findViewById(R.id.dialogTextView);


        if(ActionCode.AddToSpinner == code)
            textView.setText(getResources().getText(R.string.newAffirmationGroupDialogBoxMessage));
        else
            textView.setText(getResources().getText(R.string.newAffirmationDialogBoxMessage));

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setView(view);

        dialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString();

                        if (text.length() == 0)
                            return;

                        switch (code.getValue()) {
                            case 0:
                                addItemToSpinner(text);
                                return;
                            case 1:
                                addItemToDataCollection(text);
                                return;
                            default:
                                throw new InvalidParameterException();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
        .setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                input.clearFocus();
            }
        })
        .setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        AlertDialog dialogBox = dialogBuilder.create();

        dialogBox.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialogBox;
    }

    private enum ActionCode {
        AddToSpinner(0),
        AddToDataCollection(1);

        private int code;

        private ActionCode(int actionCode) {
            code = actionCode;
        }

        public int getValue() {
            return code;
        }
    }
}

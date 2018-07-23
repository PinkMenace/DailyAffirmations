package ashley.dewald.com.dailyaffirmations.Affrimation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Collections;

import ashley.dewald.com.dailyaffirmations.DataCollections.AffirmationData;
import ashley.dewald.com.dailyaffirmations.R;
import ashley.dewald.com.dailyaffirmations.msic.ItemTouchHelperAdapter;

/*
 * RecyclerView adapter, with basic touch input.
 *
 * @version 2.0
 * @author Ashley Dewald
 */
public class RecyclerAdapter extends Adapter<RecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private final String TAG = getClass().getCanonicalName();

    private AffirmationData data;
    private LayoutInflater inflater;

    public RecyclerAdapter(Context context) {
        inflater = LayoutInflater.from(context);

        // When setting the data
        // data = new AffirmationData("NULL");
        notifyDataSetChanged();
    }

    /*
     * Sets the data the we are working with. If the newData is null we exit
     * the function, otherwise we override, the AffirmationData reference, and call
     * 'notifyDataSetChanged()' to update what is being displayed.
     *
     * @param newData The new data we want to display.
     */
    public void setData(AffirmationData newData){
        if(newData == null)
            return;

        data = newData;
        notifyDataSetChanged();
    }

    /*
     * @return Returns a copy to the AffirmationData we are working with.
     */
    public AffirmationData getData(){
        return data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = inflater.inflate(R.layout.recyclerview_affirmation_layout, parent, false);
        return new ViewHolder(item, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // We first start off by getting
        final Affirmation current = data.get(position);

        // We start off setting up the ViewHolder with our affirmation.
        holder.selected.setChecked(current.getIsSelected());
        holder.affirmation.setText(current.getAffirmation());

        // Finally we finish setting up the ViewHolder by attaching a 'OnClickListener()' that waits
        // for the notification there was input on out checkbox.
        holder.selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (0 < position || position < data.size())
                    data.get(position).setIsSelected(holder.selected.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return (data != null) ? data.size() : 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition)
    {
        // Allows the user switch positions of how our shown.
        Collections.swap(data, fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        // Allows the user to 'delete' and item by swiping if off screen.
        data.remove(position);
        notifyItemRemoved(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final CheckBox selected;
        public final TextView affirmation;
        private final RecyclerAdapter adapter;

        public ViewHolder(View itemView, RecyclerAdapter Adapter) {
            super(itemView);

            adapter = Adapter;

            selected = (CheckBox) itemView.findViewById(R.id.affirmation_selected);
            affirmation = (TextView) itemView.findViewById(R.id.affirmation_item);
        }

        public boolean isSelected(){
            return selected.isSelected();
        }

        @Override
        public void onClick(View v) {

        }
    }
}
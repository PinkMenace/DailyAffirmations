package ashley.dewald.com.dailyaffirmations.DataCollections;

import java.util.ArrayList;
import java.util.List;

import ashley.dewald.com.dailyaffirmations.Affrimation.Affirmation;

/*
 * Extends the basic ArrayList class to give the user the ability to 'name' the list, and add
 * data to it during creation List<Affirmation>.
 *
 * @version 1.0
 * @author Ashley Dewald
 */
public class AffirmationData extends ArrayList<Affirmation>{
    private String headerName;

    public AffirmationData(String HeaderName) {
        super();
        headerName = HeaderName;
    }

    public AffirmationData(String HeaderName, List<Affirmation> list) {
        super();
        headerName = HeaderName;

        for(Affirmation a: list){
            add(a);
        }
    }

    public String getHeaderName() { return headerName; }
}

package ashley.dewald.com.dailyaffirmations.Affrimation;

/*
 * Basic container to hold the affirmation object.
 * Hold the 'affirmation string' and whether or not it has been selected.
 *
 * @version 1.0
 * @author Ashley Dewald
 */
public class Affirmation {
    private boolean isSelected;
    private String affirmation;

    public Affirmation(String Affirmation) {
        this(Affirmation, false);
    }

    public Affirmation(String Affirmation, boolean Selected) {
        affirmation = Affirmation;
        isSelected = Selected;
    }

    public boolean getIsSelected(){return isSelected; }
    public String getAffirmation() { return affirmation; }

    public void setIsSelected(boolean setIsSelected){isSelected = setIsSelected; }
}

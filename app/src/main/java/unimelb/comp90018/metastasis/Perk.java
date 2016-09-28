package unimelb.comp90018.metastasis;

/**
 * Created by David on 9/20/2015.
 */
public class Perk {
    private String name;
    private int duration;
    private boolean isActive;
    private boolean inUse;

    public Perk(String name, int duration){
        this.name = name;
        this.duration = duration;
        this.isActive = false;
        this.inUse = false;
    }

    public String getName(){
        return name;
    }

    public int getDuration(){
        return duration;
    }

    public void setActive(boolean state){
        this.isActive = state;
    }

    public void setInUse(boolean state){this.inUse = state;}

    public boolean getIsActive(){
        return isActive;
    }

    public boolean getIsInUse(){
        return inUse;
    }
}

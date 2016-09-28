package unimelb.comp90018.metastasis.gamedata;

/**
 * Created by Purathani on 26/08/15.
 */
public class GameState {

    //private variables
    int id;
    String game_state_name;
    String description;
    String status;


    // Empty constructor
    public GameState() {

    }

    // constructor
    public GameState(int _id, String _game_state_name, String _description, String _status ) {
        this.id = _id;
        this.game_state_name = _game_state_name;
        this.description = _description;
        this.status = _status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGame_state_name() {
        return game_state_name;
    }

    public void setGame_state_name(String game_state_name) {
        this.game_state_name = game_state_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

package unimelb.comp90018.metastasis.gamedata;

import java.util.Date;

/**
 * Created by Purathani on 18/08/15.
 */
public class GameScore {

    //private variables
    int id;
    int game_id;
    String player_name;
    String player_type;
    double score;
    double disks_eaten;
    float played_time;
    Date played_date;
    String game_state;


    // Empty constructor
    public GameScore() {

    }

    // constructor
    public GameScore(int _id, int _game_id, String _player, String _player_type, double _score, double _disks_eaten, float _timePlay, Date _datePlay, String _game_state) {
        this.id = _id;
        this.game_id = _game_id;
        this.player_name = _player;
        this.player_type = _player_type;
        this.score = _score;
        this.disks_eaten = _disks_eaten;
        this.played_time = _timePlay;
        this.played_date = _datePlay;
        this.game_state = _game_state;
    }


    public int getID()
    {
        return this.id;
    }


    public void setID(int _id)
    {
        this.id = _id;
    }


    public String getPlayer_name()
    {
        return this.player_name;
    }


    public void setPlayer_name(String _player)
    {
        this.player_name = _player;
    }

    public String getPlayer_type() {
        return player_type;
    }

    public void setPlayer_type(String player_type) {
        this.player_type = player_type;
    }

    public double getScore()
    {
        return this.score;
    }


    public void setScore(double _score)
    {
        this.score = _score;
    }

    public double getDisks_eaten() {
        return disks_eaten;
    }

    public void setDisks_eaten(double disks_eaten) {
        this.disks_eaten = disks_eaten;
    }

    public float getPlayed_time()
    {
        return played_time;
    }

    public void setPlayed_time(float played_time)
    {
        this.played_time = played_time;
    }

    public Date getPlayed_date()

    {
        return played_date;
    }

    public void setPlayed_date(Date played_date)
    {
        this.played_date = played_date;
    }

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public String getGame_state() {
        return game_state;
    }

    public void setGame_state(String game_state) {
        this.game_state = game_state;
    }
}

package unimelb.comp90018.metastasis.gamedata;

import java.util.Date;

/**
 * Created by Purathani on 21/08/15.
 */
public class UserSetting {

    //private variables
    int id;
    String username;
    String firstname;
    String lastname;
    String email;
    Date created_date;

    // Empty constructor
    public UserSetting() {

    }

    // constructor
    public UserSetting(int _id, String _username, String _firstname, String _lastname, String _email, Date _created_date ) {
        this.id = _id;
        this.username = _username;
        this.firstname = _firstname;
        this.lastname = _lastname;
        this.email = _email;
        this.created_date = _created_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }



}

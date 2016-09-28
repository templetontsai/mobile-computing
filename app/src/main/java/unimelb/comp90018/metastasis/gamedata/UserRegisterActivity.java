package unimelb.comp90018.metastasis.gamedata;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import unimelb.comp90018.metastasis.ui.R;

public class UserRegisterActivity extends FragmentActivity {

    EditText editTextEmail;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration_layout);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("Enter Email Address:");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void onClickUserRegister(View view) {
        DatabaseHandlerUser dbHandler = new DatabaseHandlerUser(this);

        Date created_date = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        try {
            created_date = formater.parse(created_date.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String userEmail = editTextEmail.getText().toString();

        int email_sign_char = userEmail.indexOf("@");
        String username = userEmail.substring(0,email_sign_char);

        UserSetting userSetting = new UserSetting(1, username, username, username, userEmail, created_date);

        dbHandler.addUser(userSetting);
        textView.setText("User Successfully Registered");
        editTextEmail.setText("");


    }
}

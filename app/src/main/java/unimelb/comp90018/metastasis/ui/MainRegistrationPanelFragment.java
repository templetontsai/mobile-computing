package unimelb.comp90018.metastasis.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;

import android.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unimelb.comp90018.metastasis.gamedata.DatabaseHandlerUser;
import unimelb.comp90018.metastasis.gamedata.SessionManager;
import unimelb.comp90018.metastasis.gamedata.UserSetting;


/**
 * This is the fragment for user registration in MainActivity. When there is no data in database.
 * MainActivity should show this fragment otherwise MainUserFragment should be shown
 */
public class MainRegistrationPanelFragment extends Fragment {


    EditText editTextEmail;
    TextView textView;
    SessionManager session;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

         View view = inflater.inflate(R.layout.user_registration_layout, container, false);
         editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
         textView = (TextView) view.findViewById(R.id.textView);

        Button button = (Button) view.findViewById(R.id.btnSaveEmail);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatabaseHandlerUser dbHandler = new DatabaseHandlerUser(getActivity());

                Date created_date = new Date();
                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    created_date = formater.parse(created_date.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String userEmail = editTextEmail.getText().toString();

                if (!isValidEmail(userEmail)) {
                    editTextEmail.setError("Invalid Email");
                }

                else {

                    int email_sign_char = userEmail.indexOf("@");
                    String username = userEmail.substring(0, email_sign_char);

                    // Add user in sqlite database table - User
                    UserSetting userSetting = new UserSetting(1, username, username, username, userEmail, created_date);
                    long userId = dbHandler.addUser(userSetting);

                    textView.setText("User Successfully Registered");
                    editTextEmail.setText("");

                    // Session Manager
                    session = new SessionManager(getActivity());
                    boolean userInDatabase = true;
                    String loginUserName = userSetting.getUsername();
                    String loginUserEmail = userSetting.getEmail();
                    int loginUserId = (int)userId;

                    // Create session
                    session.createUserSession(userInDatabase, loginUserId, loginUserName, loginUserEmail);

                    showDialog("User Registration", "User successfully registerred", getActivity());
                }

            }
        });
        return view;

    }

    // show alert dialog box
    public Dialog showDialog(String title, String msg, final Activity activity) {

        final AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        alertDialog.show();
        return alertDialog;

    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}

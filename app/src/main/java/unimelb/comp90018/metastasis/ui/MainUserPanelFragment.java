package unimelb.comp90018.metastasis.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import unimelb.comp90018.metastasis.gamedata.SessionManager;


/**
 * This is the fragment for user panel in MainActivity. When there exists user data in database.
 * MainActivity should show this fragment otherwise MainRegistrationFragment should be shown
 */
public class MainUserPanelFragment extends Fragment {


    TextView dispaly_name_textView;
    SessionManager session;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.main_page_layout, container, false);

        dispaly_name_textView = (TextView) view.findViewById(R.id.dispaly_name_textView);
        session = new SessionManager(getActivity());

        String username =  "";
        username = session.getLoginUsername();
        dispaly_name_textView.setText("Welcome " + username + "!");
        return view;


    }
}

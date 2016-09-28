package unimelb.comp90018.metastasis.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * This is CreditActivity it has immersive mode and it is the rewarding page for the team
 */
public class CreditActivity extends ImmersiveActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
    }


    public void onClickBack(View view) {
        Intent settingActivityIntent = new Intent(this, SettingActivity.class);
        startActivity(settingActivityIntent);
        finish();
    }

}

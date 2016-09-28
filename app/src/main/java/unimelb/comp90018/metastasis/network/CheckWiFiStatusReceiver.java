package unimelb.comp90018.metastasis.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;



/**
 * This class is to check the capability of the device whether it has wifi direct supported or not as well as on/off status, changed states
 */
public class CheckWiFiStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "CheckWiFiStatusReceiver";
    private WifiP2pManager mManager = null;
    private WifiP2pManager.Channel mChannel = null;
    private Context mContext = null;
    private GameLobby mGameLobby = null;




    public CheckWiFiStatusReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, Context mContext, GameLobby mGameLobby) {
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mContext = mContext;
        this.mGameLobby = mGameLobby;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            CharSequence text;
            int duration;
            Toast toast;

            switch(state) {
                case WifiP2pManager.WIFI_P2P_STATE_ENABLED:
                    text = "WiFi P2P is on";
                    duration = Toast.LENGTH_SHORT;
                    toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                case WifiP2pManager.WIFI_P2P_STATE_DISABLED:
                    text = "WiFi P2P is off";
                    duration = Toast.LENGTH_SHORT;
                    toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;

            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (mManager == null) {
                return;
            }


            NetworkInfo networkInfo = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                // we are connected with the other device, request connection
                // info to find group owner IP
                Log.d(TAG, "Connected to p2p network. Requesting network details");
                mManager.requestConnectionInfo(mChannel, mGameLobby);
            } else {
                // It's a disconnect
                Log.d(TAG, "disconnected to p2p network. Requesting network details");
                mGameLobby.disconnect();
                mGameLobby.disconnectSocket();
            }





        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
                .equals(action)) {
            WifiP2pDevice device = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            switch(device.status) {
                case WifiP2pDevice.AVAILABLE:
                    Log.d(TAG, "Device status - AVAILABLE" );
                    break;
                case WifiP2pDevice.CONNECTED:
                    Log.d(TAG, "Device status - CONNECTED");
                    break;
                case WifiP2pDevice.INVITED:
                    Log.d(TAG, "Device status - INVITED" );
                    break;
                case WifiP2pDevice.UNAVAILABLE:
                    Log.d(TAG, "Device status - UNAVAILABLE" );
                    break;
                case WifiP2pDevice.FAILED:
                    Log.d(TAG, "Device status - FAILED" );
                    break;


            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION");
            mGameLobby.peerRequest();
            //mGameLobby.discoverLobby();
        }
    }
}

package unimelb.comp90018.metastasis.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import unimelb.comp90018.metastasis.gamedata.SessionManager;
import unimelb.comp90018.metastasis.network.CheckWiFiStatusReceiver;
import unimelb.comp90018.metastasis.network.GameDataTransferClient;
import unimelb.comp90018.metastasis.network.GameDataTransferServer;
import unimelb.comp90018.metastasis.network.GameLobby;

/**
 * This is the multiplayer activity and inherits from ImmersiveActivity for immersive mode
 */
public class MultiplayerActivity extends ImmersiveActivity {
    private static final String TAG = "MultiplayerActivity";

    private IntentFilter mIntentFilter = null;
    private WifiP2pManager mManager = null;
    private WifiP2pManager.Channel mChannel = null;
    private CheckWiFiStatusReceiver mReceiver = null;
    private GameLobby mGameLobby = null;
    private TextView playerList = null;
    private Button joinLobbyButton = null;
    private ListView lobbyListView = null;
    private ArrayAdapter<WifiP2pDevice> lobbyListAdapter = null;
    private WifiP2pDevice selectedLobby = null;

    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);



        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mGameLobby = GameLobby.getInstance();
        mGameLobby.init(mManager, mChannel);
        mReceiver = new CheckWiFiStatusReceiver(mManager, mChannel, this, mGameLobby);

        playerList = (TextView) findViewById(R.id.playerListText);
        joinLobbyButton = (Button) findViewById(R.id.joinLobby);
        joinLobbyButton.setClickable(false);
        joinLobbyButton.setEnabled(false);
        lobbyListView = (ListView) findViewById(R.id.lobbylistView);
        lobbyListAdapter = new ArrayAdapter<>(this, R.layout.peer_list);
        lobbyListView.setAdapter(lobbyListAdapter);
        lobbyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // selected item
                // String selected = ((TextView) view.findViewById(R.id.your_textView_item_id)).getText().toString();
                selectedLobby = (WifiP2pDevice) lobbyListView.getItemAtPosition(position);
                if (selectedLobby != null) {
                    joinLobbyButton.setEnabled(true);
                    joinLobbyButton.setClickable(true);
                }


            }
        });




        mGameLobby.setTextView(playerList);
        mGameLobby.setAdapter(lobbyListAdapter);

        session = new SessionManager(getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mReceiver);
    }

    @TargetApi(16)
    public void onClickCreate(View view) {

        if(mGameLobby != null) {
            mGameLobby.createLobby();
            //mGameLobby.peerDiscovery();
        }

    }

    @TargetApi(16)
    public void onClickJoin(View view) {

        if(mGameLobby != null) {
            mGameLobby.connect(selectedLobby);
            //mGameLobby.connectPeer();
        }

    }


    public void onClickHome(View view){
        mGameLobby.disconnect();
        mGameLobby.disconnectSocket();
        Intent mainGameActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainGameActivityIntent);
        finish();
    }

    public void onClickSearch(View view){
        if(mGameLobby != null) {
            mGameLobby.discoverLobby();
        }

    }

    public void onClickPlayMultiplayer(View view){
        if(mGameLobby != null){

            if(mGameLobby.playerType.equals("Server")){
                String gameMode = "MultiplayerServer";
                session.createGameMode(gameMode);
                Log.d(TAG, "Server game starting ");
                Intent mainGameActivityIntent = new Intent(this, GameActivity.class);

                //connection_id, 2 is server, 1 is client
                Bundle bundle = new Bundle();
                bundle.putInt("connection_id", 2);
                GameDataTransferServer mGameDataTransferServer = mGameLobby.getServerInstance();
                bundle.putSerializable("socket_instance", mGameDataTransferServer);
                mainGameActivityIntent.putExtras(bundle);
                startActivity(mainGameActivityIntent);
                finish();
            }else if(mGameLobby.playerType.equals("Client")){
                String gameMode = "MultiplayerClient";
                session.createGameMode(gameMode);
                session.setServerAddress(mGameLobby.serverAddress);
                Log.d(TAG, "Client game starting");
                Intent mainGameActivityIntent = new Intent(this, GameActivity.class);

                //connection_id, 2 is server, 1 is client
                Bundle bundle = new Bundle();
                bundle.putInt("connection_id", 1);
                GameDataTransferClient mGameDataTransferClient = mGameLobby.getClientInstance();
                bundle.putSerializable("socket_instance", mGameDataTransferClient);
                mainGameActivityIntent.putExtras(bundle);
                startActivity(mainGameActivityIntent);
                finish();
            }
        }
    }
}

package unimelb.comp90018.metastasis.network;

import android.annotation.TargetApi;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unimelb.comp90018.metastasis.gamedata.SessionManager;

/**
 * This class is to discover game service through WiFiDirect
 */
public class GameLobby implements WifiP2pManager.ConnectionInfoListener{

    private static final String TAG = "GameLobby";
    private static GameLobby instance = null;

    private WifiP2pManager mManager = null;
    private WifiP2pManager.Channel mChannel = null;
    private HostTextRecordListener mHostTextRecordListener= null;
    private HostServiceRecordListener mHostServiceRecordListener= null;
    private GameDataTransferServer mGameDataTransferServer = null;
    private GameDataTransferClient mGameDataTransferClient = null;
    private WifiP2pDnsSdServiceRequest serviceRequest = null;
    private TextView mTextView = null;
    private ArrayAdapter<WifiP2pDevice> mLobbyListAdapter = null;
    private static final String SERVER_PORT = "4545";
    public static final int SET_GAMEDATA_MANAGER = 1;
    public String playerType = "";
    private List peers = new ArrayList();
    public String serverAddress;
    private WifiP2pManager.PeerListListener myPeerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            // Out with the old, in with the new.
            peers.clear();
            peers.addAll(peerList.getDeviceList());

            //mLobbyListAdapter.addAll(peers);
            //mLobbyListAdapter.notifyDataSetChanged();
            if (peers.size() == 0) {
                Log.d(TAG, "No devices found");
                return;
            }
        }
    };



    protected GameLobby() {}

    public static GameLobby getInstance() {
        if(instance == null) {
            instance = new GameLobby();
        }
        return instance;
    }

    public GameDataTransferServer getServerInstance() {
        return mGameDataTransferServer;
    }

    public GameDataTransferClient getClientInstance() {
        return mGameDataTransferClient;
    }

    public void init(WifiP2pManager mManager, WifiP2pManager.Channel mChannel) {


        if(mManager != null && mChannel != null) {
            this.mManager = mManager;
            this.mChannel = mChannel;

            //peerDiscovery();
        }

    }

    public void setTextView(TextView mTextView) {
        this.mTextView = mTextView;
    }

    public void setAdapter(ArrayAdapter<WifiP2pDevice> mLobbyListAdapter) {
        this.mLobbyListAdapter = mLobbyListAdapter;
    }

    public void peerRequest() {
        if (mManager != null) {
            mManager.requestPeers(mChannel, myPeerListListener);
        }
    }
    public void connectPeer(WifiP2pDevice mDevice) {


        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = mDevice.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int result) {
                Log.d(TAG, "connectPeer failed");
            }
        });
    }


    public void peerDiscovery() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
                Log.d(TAG, "Peer discovery Success");
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
            }
        });
    }


    @TargetApi(16)
    public void createLobby() {

        if(mManager != null && mChannel != null) {

            //  Create a string map containing information about your service.
            Map lobbyInfo = new HashMap();
            lobbyInfo.put("port", String.valueOf(SERVER_PORT));
            lobbyInfo.put("service_name", "Metastasis");
            lobbyInfo.put("available", "visible");

            // Service information.  Pass it an instance name, service type
            // _protocol._transportlayer , and the map containing
            // information other devices will want once they connect to this one.
            WifiP2pDnsSdServiceInfo serviceInfo =
                    WifiP2pDnsSdServiceInfo.newInstance("Metastasis", "_presence._tcp", lobbyInfo);

            // Add the local service, sending the service info, network channel,
            // and listener that will be used to indicate success or failure of
            // the request.
            mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "addLocalService success");
                }

                @Override
                public void onFailure(int result) {

                    switch (result) {
                        case WifiP2pManager.P2P_UNSUPPORTED:
                            Log.d(TAG, "P2P is Unsupported");
                            break;
                        case WifiP2pManager.BUSY:
                            Log.d(TAG, "P2P Busy");
                            break;
                        case WifiP2pManager.ERROR:
                            Log.d(TAG, "P2P Error");
                            break;

                    }
                }
            });

            discoverLobby();
            //peerDiscovery();

        }
    }

    @TargetApi(16)
    public void discoverLobby() {

        if(mManager != null && mChannel != null) {
            mHostServiceRecordListener = new HostServiceRecordListener();
            mHostTextRecordListener = new HostTextRecordListener();
            mManager.setDnsSdResponseListeners(mChannel, mHostServiceRecordListener, mHostTextRecordListener);

            serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();

            mManager.addServiceRequest(mChannel,
                    serviceRequest,
                    new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "addServiceRequest success");
                        }

                        @Override
                        public void onFailure(int result) {
                            switch (result) {
                                case WifiP2pManager.P2P_UNSUPPORTED:
                                    Log.d(TAG, "P2P is Unsupported");
                                    break;
                                case WifiP2pManager.BUSY:
                                    Log.d(TAG, "P2P Busy");
                                    break;
                                case WifiP2pManager.ERROR:
                                    Log.d(TAG, "P2P Error");
                                    break;

                            }
                        }
                    });

            mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {


                @Override
                public void onSuccess() {

                    Log.d(TAG, "discoverServices success");
                }

                @Override
                public void onFailure(int result) {
                    switch (result) {
                        case WifiP2pManager.P2P_UNSUPPORTED:
                            Log.d(TAG, "P2P is Unsupported");
                            break;
                        case WifiP2pManager.BUSY:
                            Log.d(TAG, "P2P Busy");
                            break;
                        case WifiP2pManager.ERROR:
                            Log.d(TAG, "P2P Error");
                            break;

                    }

                }

            });

        }
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {

        Log.d(TAG, "onConnectionInfoAvailable");

        if (info.groupFormed && info.isGroupOwner) {

            if(mGameDataTransferServer == null) {
                Log.d(TAG, "group owner starting server");
                mGameDataTransferServer = new GameDataTransferServer(Integer.parseInt(SERVER_PORT));
                playerType = "Server";
            }


        } else if(info.groupFormed){

            if(mGameDataTransferClient == null) {
                Log.d(TAG, "client starting data transfer");
                serverAddress = info.groupOwnerAddress.getHostAddress();
                mGameDataTransferClient = new GameDataTransferClient(info.groupOwnerAddress,Integer.parseInt(SERVER_PORT));
                playerType = "Client";
            }

        }
    }

    public void disconnectSocket() {
        if(mGameDataTransferClient != null) {
            mGameDataTransferClient.disconnect();
            mGameDataTransferClient = null;
        } else if(mGameDataTransferServer != null) {
            mGameDataTransferServer.disconnect();
            mGameDataTransferServer = null;
        }
        Log.d(TAG, "Disconnect Socket Successful");
    }

    @TargetApi(16)
    public void disconnect() {
        if (mManager != null && mChannel != null) {
            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onFailure(int result) {
                    Log.d(TAG, "Disconnect failed. Reason :" + result);
                }
                @Override
                public void onSuccess() {

                }
            });
        }

    }

    @TargetApi(16)
    public void connect(WifiP2pDevice device) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.removeServiceRequest(mChannel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "removeServiceRequest success");
                    }
                    @Override
                    public void onFailure(int result) {
                        Log.d(TAG, "removeServiceRequest fail = " + result);
                    }
                });

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                Log.d(TAG, "connect successful");

            }

            @Override
            public void onFailure(int result) {
                switch (result) {
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        Log.d(TAG, "P2P is Unsupported");
                        break;
                    case WifiP2pManager.BUSY:
                        Log.d(TAG, "P2P Busy");
                        break;
                    case WifiP2pManager.ERROR:
                        Log.d(TAG, "P2P Error");
                        break;

                }
            }
        });

    }


    @TargetApi(16)
    class HostTextRecordListener implements WifiP2pManager.DnsSdTxtRecordListener {

        final HashMap<String, String> lobbyInfo = new HashMap<String, String>();
        @Override
        /* Callback includes:
         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
         * record: TXT record dta as a map of key/value pairs.
         * device: The device running the advertised service.
         */

        public void onDnsSdTxtRecordAvailable(
                String fullDomain, Map record, WifiP2pDevice device) {
            Log.d(TAG, "DnsSdTxtRecord available -" + record.toString());

        }

    }

    @TargetApi(16)
    class HostServiceRecordListener implements WifiP2pManager.DnsSdServiceResponseListener {

        final HashMap<String, String> buddies = new HashMap<String, String>();
        @Override
        public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                            WifiP2pDevice resourceType) {

            // Update the device name with the human-friendly version from
            // the DnsTxtRecord, assuming one arrived.
            resourceType.deviceName = buddies
                    .containsKey(resourceType.deviceAddress) ? buddies
                    .get(resourceType.deviceAddress) : resourceType.deviceName;


            mLobbyListAdapter.clear();
            mLobbyListAdapter.add(resourceType);
            mLobbyListAdapter.notifyDataSetChanged();


            Log.d(TAG, "Metastasis Lobby: " + instanceName + "DeviceAddress = " + resourceType.deviceAddress);
        }
    }

    public List getPeers(){
        return mGameDataTransferServer.getPeerAddresses();
    }
}

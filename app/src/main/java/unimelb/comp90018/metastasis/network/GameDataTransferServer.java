package unimelb.comp90018.metastasis.network;



import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.os.Handler;

import unimelb.comp90018.metastasis.Entity;
import unimelb.comp90018.metastasis.Food;
import unimelb.comp90018.metastasis.Vector;
import unimelb.comp90018.metastasis.gamedata.SessionManager;

/**
 * This class is where the device acts as server and the owner of the group, its main task is to synchronize and broadcast all the data
 * to the connected clients
 */
public class GameDataTransferServer extends Thread implements Serializable{

    private static final String TAG = "GameDataTransferServer";
    private int mPort;
    private ServerSocket mServerSocket = null;
    private boolean isRunning = false;
    private List<String> peers = new ArrayList<>();

    private List<ServerDataManager> clientThreadsList;
    private int totalThreadCount;

    private List<Food> entityList;
    private Vector oldServerPosition;
    private Vector serverPosition;

    private ServerDataManager clientThread;
    private LockObject mLock = null;


    public GameDataTransferServer(int mPort) {

        this.mPort = mPort;
        this.clientThreadsList = new CopyOnWriteArrayList<>();
        this.entityList = new CopyOnWriteArrayList<>();
        this.serverPosition = new Vector();
        this.oldServerPosition = new Vector();
        this.totalThreadCount = 0;
        this.mLock = new LockObject();

    }

    @Override
    public void run() {

        try {

            Log.d(TAG, "data transfer server is up");
            mServerSocket = new ServerSocket(mPort);
            isRunning = true;
            while(isRunning) {
                Socket server = mServerSocket.accept();

                clientThread = new ServerDataManager(server, Integer.toString(totalThreadCount));
                clientThreadsList.add(clientThread);
                clientThread.start();
                totalThreadCount++;

            }





        } catch (IOException e){
            try {
                if (mServerSocket != null && !mServerSocket.isClosed())
                    mServerSocket.close();
            } catch (IOException ioe) {
            }

            e.printStackTrace();

        } finally {
            try {
                if (mServerSocket != null && !mServerSocket.isClosed())
                    mServerSocket.close();
            } catch (IOException ioe) {
            }

        }
    }

    public void disconnect() {
        try {
            if(mServerSocket != null && !mServerSocket.isClosed()) {
                isRunning = false;
                mServerSocket.close();
                Log.d(TAG, "Server disconnect");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void setFoodList(List<Food> foodList){
        this.entityList = foodList;
    }

    public void setVectorList(Vector playerPosition){
        if(oldServerPosition==null) {
            this.oldServerPosition = playerPosition;
           // this.serverPosition = playerPosition;
        }else{
            this.serverPosition = playerPosition;
        }
    }

    public Vector getClientVector() {
        Vector v = null;

      //  synchronized (mLock) {
            if(clientThread != null && clientThread.isAlive()) {
                v = clientThread.clientVector;
                if(v != null) {
                    Log.d(TAG, "X " + v.x);
                    return v;

                }
            }
      //  }

        return v;
    }

    class ServerDataManager extends Thread {
        private Socket mSocket = null;
        public Vector clientVector = null;
        private ObjectInputStream vectorCommInput;
        private ObjectOutputStream vectorCommOutput;
        private boolean isClientConnectedRunning;




        public ServerDataManager(Socket mSocket, String id){
            super(id);
            this.mSocket = mSocket;
            this.clientVector = new Vector();
        }

        @Override
        public void run() {

            if(mSocket != null) {

                isClientConnectedRunning = true;
                sendFoodItems(entityList);
                while(isClientConnectedRunning) {
                    Log.d(TAG, "The transfer server is sending data");
                    if(!oldServerPosition.equals(serverPosition)) {
                        Log.d(TAG, "Current State of player: "+serverPosition);
                        sendVectors(serverPosition);
                        oldServerPosition = serverPosition;
                    }
                    receiveClientVectors();
                }

                try {
                    vectorCommInput.close();
                    vectorCommOutput.close();
                    mSocket.close();

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }

        }

        private void receiveClientVectors() {
            try {

                if(!mSocket.isClosed()) {

                   // synchronized (mLock) {
                        if(vectorCommInput==null)
                            vectorCommInput = new ObjectInputStream(mSocket.getInputStream());
                        Vector newVector = (Vector) this.vectorCommInput.readObject();
                        if(!newVector.equals(clientVector))
                            this.clientVector = (Vector) this.vectorCommInput.readObject();
                        if (clientVector != null) {
                            Log.d(TAG, "clientVector = " + this.clientVector.x);
                        }

                  //  }
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
                try {
                    this.vectorCommInput.close();
                    this.vectorCommOutput.close();
                    mSocket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (ClassNotFoundException ce) {
                ce.printStackTrace();

            }

        }

        public void sendFoodItems(List<Food> entities){
            try
            {
                if(!mSocket.isClosed()) {
                    if(vectorCommOutput==null)
                        this.vectorCommOutput = new ObjectOutputStream(mSocket.getOutputStream());

                    this.vectorCommOutput.writeObject(entities);
                }

            }
            catch(IOException e)
            {
                try {
                    this.vectorCommInput.close();
                    this.vectorCommOutput.close();
                    mSocket.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();

                }
                e.printStackTrace();
            }
        }

        public void sendVectors(Vector playerPosition){
            try
            {
                if(!mSocket.isClosed() && playerPosition!=null) {
                    if(vectorCommOutput==null)
                       this.vectorCommOutput = new ObjectOutputStream(mSocket.getOutputStream());
                    Log.d(TAG, "Vector List: "+playerPosition);
                    this.vectorCommOutput.writeObject(playerPosition);

                }
            }
            catch(IOException e)
            {
                try {
                    this.vectorCommInput.close();
                    this.vectorCommOutput.close();
                    mSocket.close();

                } catch (IOException ioe) {
                    ioe.printStackTrace();

                }
                e.printStackTrace();
            }finally {
                try {
                    this.vectorCommOutput.flush();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }


    }

    public List getPeerAddresses(){
        return peers;
    }



}

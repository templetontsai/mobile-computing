package unimelb.comp90018.metastasis.network;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.os.Handler;

import unimelb.comp90018.metastasis.Entity;
import unimelb.comp90018.metastasis.Food;
import unimelb.comp90018.metastasis.Vector;

/**
 * This class is client class to transmit game data such as coordinate to server
 */
public class GameDataTransferClient extends Thread implements Serializable{

    private static final String TAG = "GameDataTransferClient";
    private InetAddress mGroupOwnerAddress = null;
    private int mPort;
    private  Socket client;
    private ObjectOutputStream vectorCommOutput;
    private ObjectInputStream vectorCommInput;
    private List<Food> foodList;
    private Vector serverVector;
    private Vector clientVector;
    private Vector oldClientVector;

    public GameDataTransferClient(InetAddress mGroupOwnerAddress, int mPort) {
        this.mGroupOwnerAddress = mGroupOwnerAddress;
        this.mPort = mPort;
        this.serverVector = new Vector();
        this.clientVector = new Vector();
        this.oldClientVector = new Vector();
    }

    @Override
    public void run() {

        try {

            client = new Socket();

            Log.d(TAG, "data transfer client is up, " + mGroupOwnerAddress.getHostAddress());

            client.connect(new InetSocketAddress(mGroupOwnerAddress.getHostAddress(),
                    mPort), 5000);
            vectorCommInput = new ObjectInputStream(client.getInputStream());
            vectorCommOutput = new ObjectOutputStream(client.getOutputStream());
            foodList = new CopyOnWriteArrayList<>();
            Object oldVector = new Vector(0, 0);

            while(true){


                Log.d(TAG, "0This is frustrating" );
                Log.d(TAG, "1This is frustrating" );
                if(vectorCommInput==null)
                    vectorCommInput = new ObjectInputStream(client.getInputStream());


                Object streamObject = vectorCommInput.readObject();


                    if(streamObject instanceof List) {
                        List<Object> tempList = (List<Object>)streamObject;
                        Log.d(TAG, "Total Food: " + ((List) tempList).size());
                        for(int i=0; i<tempList.size(); i++) {
                            foodList.add((Food) tempList.get(i));
                        }
                    }else if(streamObject instanceof Vector){
                        Vector newVector = (Vector)streamObject;
                        Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!! ");
                        if(!streamObject.equals(oldVector)) {
                            Log.d(TAG, "1Server player is now at: x: "+newVector.x+" y:"+newVector.y);
                            if(newVector!=null) {
                                Log.d(TAG, "2Server player is now at: x: "+newVector.x+" y:"+newVector.y);
                                if (this.serverVector == null) {
                                    this.serverVector = newVector;
                                } else {
                                    this.serverVector.reset(newVector);
                                    oldVector = newVector;
                                }
                            }
                        }

                    }

                //Send updates only if the client has moved
                if(!oldClientVector.equals(clientVector)) {
                    Log.d(TAG, "Current State of player: "+clientVector);
                    sendVector(clientVector);
                    oldClientVector = clientVector;
                }

                Log.d(TAG, "2This is frustrating");

              /**  try {
                    Thread.sleep(10);
                } catch(Exception e0) {

                }**/

            }
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            try {
                client.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        } finally {
            try {
                if(client != null && !client.isClosed()) {
                    client.close();
                    Log.d(TAG, "Client disconnect");
                }

            } catch(IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void disconnect() {
        try {
            if(client != null && !client.isClosed()) {
                client.close();
                Log.d(TAG, "Client disconnect");
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public List<Food> getInitialFood(){

        return foodList;
    }

    public Vector getServerVectors(){
        return this.serverVector;
    }

    public void sendVector(Vector vector){
        try
        {
            if(vectorCommOutput==null)
                vectorCommOutput = new ObjectOutputStream(client.getOutputStream());

            vectorCommOutput.writeObject(vector);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setVector(Vector clientVector){
        if(oldClientVector==null){
            this.oldClientVector = clientVector;
        }else {
            this.clientVector = clientVector;
        }
    }


}

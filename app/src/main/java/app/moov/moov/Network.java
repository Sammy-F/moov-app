package app.moov.moov;

/**
 * Created by EFrost on 2/14/2018.
 */
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.widget.Toast;

import java.net.*;
import java.io.*;

//public class Network {
   // public static void main(String args[]) throws IOException {

    //}
     //       System.out.println();
     //       InetAddress localaddr = InetAddress.getLocalHost();

            //System.out.println ("Local IP Address : " + localaddr );
            //System.out.println ("Local hostname   : " + localaddr.getHostName());
     //       Socket user = new Socket(servername, portnum);
     //       System.out.println("User connected to " + user.getLocalSocketAddress());
     //       OutputStream Out2Server = user.getOutputStream();
     //       DataOutputStream out = new DataOutputStream(Out2Server);

     //       out.writeUTF("Server writes: " + user.getLocalSocketAddress());
     //       InputStream In2Server = user.getInputStream();
     //       DataInputStream in = new DataInputStream(In2Server);

     //       System.out.println("Server is actually writing: " + in.readUTF());
     //       user.close();
     //   }
     //   catch (UnknownHostException error)
     //   {
     //       System.err.println ("Can't detect localhost : " + error);
     //   }
     //   catch (IOException error){
     //       error.printStackTrace();
     //   }

    //}

  //  public String internetConnector(){
     //   Context context = this;

     //   ConnectivityManager check = (ConnectivityManager)
     //           this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
     //   NetworkInfo[] info = check.getAllNetworkInfo();
     //   for (int i = 0; i<info.length; i++){
     //       if (info[i].getState() == NetworkInfo.State.CONNECTED){
     //           Toast.makeText(context, "Internet is connected",
     //                   Toast.LENGTH_SHORT).show();
      //      }
     //   }
     //   return "done";
   // }

//    public String serverResponse() throws IOException{
//        String servername = "MoovConnect";
//        int portnum = Integer.parseInt("74");
//        ServerSocket listener = new ServerSocket(9000); //additional listener serversocket added
//        try {
//            while (true) { //new stuff added on this current test
//                Socket socket = listener.accept(); //new listener added.
//                try {
//                    PrintWriter out =
//                            new PrintWriter(socket.getOutputStream(), true);
//                    out.println("This thing is actually working!");
//                } finally {
//                    socket.close();
//                }
//            }
//        } finally {
//            listener.close();
////        }
////    }
//    /** Converts a byte_array of octets into a string */
//    public static String byteToStr( byte[] byte_arr )
//    {
//        StringBuffer internal_buffer = new StringBuffer();
//
//        // Keep looping, and adding octets to the IP Address
//        for (int index = 0; index < byte_arr.length -1; index++)
//        {
//            internal_buffer.append ( String.valueOf(byte_arr[index]) + ".");
//        }
//
//        // Add the final octet, but no trailing '.'
//        internal_buffer.append ( String.valueOf (byte_arr.length) );
//
//        return internal_buffer.toString();
//    }
//}

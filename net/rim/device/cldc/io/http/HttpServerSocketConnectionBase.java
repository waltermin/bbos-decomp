package net.rim.device.cldc.io.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.util.Arrays;
import net.rim.device.cldc.io.utility.URL;
import net.rim.vm.WeakReference;

public class HttpServerSocketConnectionBase implements ServerSocketConnection {
   private WeakReference[] _sockets;
   private WeakReference[] _socketIns;
   private WeakReference[] _socketOuts;
   protected ServerSocketConnection _serverConnection;
   protected URL _url;

   @Override
   public void close() {
      this._serverConnection.close();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public StreamConnection acceptAndOpen() throws IOException {
      boolean reused = false;
      SocketConnection socket = null;
      DataInputStream in = null;
      DataOutputStream out = null;

      while (true) {
         synchronized (this) {
            if (this._sockets.length <= 0) {
               break;
            }

            socket = (SocketConnection)this._sockets[0].get();
            in = (DataInputStream)this._socketIns[0].get();
            out = (DataOutputStream)this._socketOuts[0].get();
            Arrays.removeAt(this._sockets, 0);
            Arrays.removeAt(this._socketIns, 0);
            Arrays.removeAt(this._socketOuts, 0);
            if (socket == null) {
               continue;
            }
         }

         reused = true;
         boolean var81 = false /* VF: Semaphore variable */;

         try {
            var81 = true;
            socket.getLocalPort();
            var81 = false;
         } finally {
            if (var81) {
               label607:
               try {
                  if (in != null) {
                     in.close();
                  }
               } finally {
                  break label607;
               }

               label604:
               try {
                  if (out != null) {
                     out.close();
                  }
               } finally {
                  break label604;
               }

               label601:
               try {
                  socket.close();
               } finally {
                  break label601;
               }

               socket = null;
               in = null;
               out = null;
               reused = false;
               continue;
            }
         }
      }

      if (!reused) {
         socket = (SocketConnection)this._serverConnection.acceptAndOpen();
         if (socket == null) {
            throw new IOException();
         }

         in = socket.openDataInputStream();
         out = socket.openDataOutputStream();
      }

      if (socket == null) {
         throw new IOException();
      }

      HttpServerProtocolBase serverBase = new HttpServerProtocolBase(this._url, this, socket, in, out);
      if (reused) {
         boolean var44 = false /* VF: Semaphore variable */;

         try {
            var44 = true;
            serverBase.openInputStream();
            var44 = false;
         } finally {
            if (var44) {
               label587:
               try {
                  if (in != null) {
                     in.close();
                  }
               } finally {
                  break label587;
               }

               label584:
               try {
                  if (out != null) {
                     out.close();
                  }
               } finally {
                  break label584;
               }

               label581:
               try {
                  if (socket != null) {
                     socket.close();
                  }
               } finally {
                  break label581;
               }

               SocketConnection var91 = null;
               DataInputStream var93 = null;
               DataOutputStream var95 = null;
               HttpServerProtocolBase var97 = null;
               var91 = (SocketConnection)this._serverConnection.acceptAndOpen();
               if (var91 == null) {
                  throw new IOException();
               }

               var93 = var91.openDataInputStream();
               var95 = var91.openDataOutputStream();
               var97 = new HttpServerProtocolBase(this._url, this, var91, var93, var95);
               return var97;
            }
         }
      }

      return serverBase;
   }

   void addAcceptCandidate(StreamConnection socket, DataInputStream in, DataOutputStream out) {
      synchronized (this) {
         Arrays.add(this._sockets, new WeakReference(socket));
         Arrays.add(this._socketIns, new WeakReference(in));
         Arrays.add(this._socketOuts, new WeakReference(out));
      }
   }

   @Override
   public int getLocalPort() {
      return this._serverConnection.getLocalPort();
   }

   @Override
   public String getLocalAddress() {
      return this._serverConnection.getLocalAddress();
   }

   public HttpServerSocketConnectionBase(URL url, ServerSocketConnection serverConnection) {
      this._serverConnection = serverConnection;
      this._url = url;
      this._sockets = new WeakReference[0];
      this._socketIns = new WeakReference[0];
      this._socketOuts = new WeakReference[0];
   }
}

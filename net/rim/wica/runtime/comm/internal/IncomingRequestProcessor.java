package net.rim.wica.runtime.comm.internal;

import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.http.HttpServerConnection;
import net.rim.wica.runtime.comm.IncomingRequest;
import net.rim.wica.runtime.comm.IncomingRequestListener;

public final class IncomingRequestProcessor implements Runnable {
   private CommunicationServiceImpl _commService;
   private IncomingRequestListener _incomingRequestListener;
   private String _listeningPort;
   private StreamConnectionNotifier _streamConnectionNotifier;

   final void stop() {
      CommUtilities.closeAndIgnoreException(this._streamConnectionNotifier);
   }

   final void setIncomingRequestListener(IncomingRequestListener listener) {
      this._incomingRequestListener = listener;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      label170:
      while (this._commService.isRunning()) {
         boolean var23 = false /* VF: Semaphore variable */;

         try {
            var23 = true;
            this._streamConnectionNotifier = (StreamConnectionNotifier)Connector.open(this._listeningPort);
            var23 = false;
         } finally {
            if (var23) {
               CommUtilities.closeAndIgnoreException(this._streamConnectionNotifier);
               return;
            }
         }

         while (true) {
            HttpServerConnection conn;
            InputStream in;
            while (true) {
               if (!this._commService.isRunning()) {
                  continue label170;
               }

               conn = null;
               in = null;

               try {
                  conn = (HttpServerConnection)this._streamConnectionNotifier.acceptAndOpen();
                  in = conn.openInputStream();
               } catch (Throwable var25) {
                  if (this._commService.isRunning()) {
                     this._commService.logException("Could not accept push connection and openInputStream", e);
                  }

                  CommUtilities.closeAndIgnoreException(in);
                  CommUtilities.closeAndIgnoreException(this._streamConnectionNotifier);
                  CommUtilities.closeAndIgnoreException(conn);
                  continue label170;
               }

               IncomingRequest request = null;
               boolean var10 = false /* VF: Semaphore variable */;

               try {
                  label162:
                  try {
                     var10 = true;
                     request = new IncomingRequestImpl(IOUtilities.streamToBytes(in));
                     this.sendAck(conn);
                     this._incomingRequestListener.processIncomingRequest(request);
                     var10 = false;
                     break;
                  } catch (Throwable var26) {
                     this._commService.logException("Exception while processing incoming push request", e);
                     var10 = false;
                     break label162;
                  }
               } finally {
                  if (var10) {
                     CommUtilities.closeAndIgnoreException(in);
                     CommUtilities.closeAndIgnoreException(conn);
                  }
               }

               CommUtilities.closeAndIgnoreException(in);
               CommUtilities.closeAndIgnoreException(conn);
            }

            CommUtilities.closeAndIgnoreException(in);
            CommUtilities.closeAndIgnoreException(conn);
         }
      }
   }

   public IncomingRequestProcessor(CommunicationServiceImpl commService, String listeningPort) {
      this._commService = commService;
      this._listeningPort = listeningPort;
   }

   private final void sendAck(HttpServerConnection conn) {
      conn.setResponseProperty("X-Wap-Push-Status", Integer.toString(400));
      conn.setResponseCode(204);
   }
}

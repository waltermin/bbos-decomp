package net.rim.device.internal.io.streamdatagram;

import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.system.EventLogger;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.tunnel.Tunnel;

public class StreamDatagramServerSocketConnectionBase implements ServerSocketConnection, StreamDatagramConnectionConstants {
   protected StreamDatagramAddressBase _myAddress;
   protected boolean _isClosed;
   protected Object _backlogWaitObj = new Object();
   protected Vector _tcpConnectionBacklog = (Vector)(new Object());
   protected StreamDatagramTransportBase _transport;
   protected Tunnel _tunnel;
   public Object _pendingConnectionWaitObj = new Object();
   public Vector _pendingConnectionList = (Vector)(new Object());
   protected int _mode;
   protected boolean _timeouts;
   private static String STR_CLOSED = "connection is closed";
   public static final int STREAM_DEBUG_LEVEL;

   @Override
   public void close() {
      throw null;
   }

   @Override
   public StreamConnection acceptAndOpen() {
      throw null;
   }

   public boolean getTimeouts() {
      return this._timeouts;
   }

   public Connection openPrim(StreamDatagramAddressBase myAddr, int mode, boolean timeouts) {
      if (this._transport != null && this._transport.isConnectionTableFull()) {
         EventLogger.logEvent(447071754022829032L, 1413696867, 3);
         this._transport.logConnections();
         throw new Object("Max connections opened.");
      }

      this._myAddress = myAddr;
      this._mode = mode;
      this._timeouts = timeouts;
      if (this._transport == null) {
         throw new Object();
      }

      this.openPrimChores();
      this._transport.addServerConnection(this);
      return this;
   }

   protected void openPrimChores() {
      throw null;
   }

   public int getMode() {
      return this._mode;
   }

   protected void throwIOExceptionIfThisConnIsClosed() {
      if (this._isClosed) {
         throw new Object(STR_CLOSED);
      }
   }

   protected void closeAssociatedConnections() {
      try {
         ProtocolDaemon.getInstance().submitRunnable(new StreamDatagramServerSocketConnectionBase$StreamDatagramConnectionBaseReaper(this));
      } finally {
         return;
      }
   }

   protected StreamConnection getNextBackloggedConnection(int _1) {
      throw null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected boolean isConnectionWaiting(int portNumToSearchFor) {
      int sizeOfBacklog = this._tcpConnectionBacklog.size();

      for (int i = 0; i < sizeOfBacklog; i++) {
         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            Object ioe = this._tcpConnectionBacklog.elementAt(i);
            if (ioe != null) {
               if (((StreamDatagramConnectionBase)ioe).getLocalPort() == portNumToSearchFor) {
                  return true;
               }

               var6 = false;
            } else {
               var6 = false;
            }
         } finally {
            if (var6) {
               this._tcpConnectionBacklog.removeElementAt(i);
               sizeOfBacklog--;
               i--;
               continue;
            }
         }
      }

      return false;
   }

   @Override
   public int getLocalPort() {
      throw null;
   }

   @Override
   public String getLocalAddress() {
      this.throwIOExceptionIfThisConnIsClosed();
      return StreamDatagramAddressBase.getLocalAddressInternal(this._myAddress.getApnName());
   }
}

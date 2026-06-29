package net.rim.device.cldc.io.simultcp;

import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.internal.firewall.Firewall;
import net.rim.device.internal.io.streamdatagram.StreamDatagramConnectionBase;
import net.rim.device.internal.io.streamdatagram.StreamDatagramServerSocketConnectionBase;
import net.rim.device.internal.io.streamdatagram.StreamDatagramTransportBase;
import net.rim.device.internal.io.tcp.TcpConnectionIdentifier;
import net.rim.device.internal.io.tcp.TcpUtils;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.TraceBack;
import net.rim.vm.WeakReference;

final class SimulTcpServerSocketConnection extends StreamDatagramServerSocketConnectionBase implements SimulTcpConstants {
   private int _listenSocketID = -1;

   SimulTcpServerSocketConnection(StreamDatagramTransportBase transport, Tunnel tunnel) {
      super._transport = transport;
      super._tunnel = tunnel;
      super._transport.initForServerSocketConnections();
   }

   @Override
   public final StreamConnection acceptAndOpen() {
      this.throwIOExceptionIfThisConnIsClosed();
      synchronized (super._transport._connectionTableWaitObj) {
         if (super._transport.isConnectionTableFull()) {
            label178:
            try {
               super._transport._connectionTableWaitObj.wait();
            } finally {
               break label178;
            }
         }
      }

      int myPort;
      while ((myPort = this.getLocalPort()) != 0) {
         synchronized (super._backlogWaitObj) {
            if (this.isConnectionWaiting(myPort)) {
               int moduleHandle = TraceBack.getCallingModule(0);
               if (!ControlledAccess.verifyCodeModuleSignature(moduleHandle, 51) && !Firewall.getInstance().allowConnection("simultcp", "", false)) {
                  throw new Object("Permission denied");
               }

               Object var10000;
               try {
                  Object temp;
                  if ((temp = this.getNextBackloggedConnection(myPort)) == null) {
                     continue;
                  }

                  var10000 = temp;
               } finally {
                  continue;
               }

               return (StreamConnection)var10000;
            } else {
               try {
                  super._backlogWaitObj.wait();
                  this.throwIOExceptionIfThisConnIsClosed();
               } finally {
                  continue;
               }
            }
         }
      }

      throw new Object();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final void openPrimChores() {
      try {
         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            int e = TcpUtils.addToTcpConnectionDatabase(this, (TcpConnectionIdentifier)super._myAddress);
            if (super._myAddress.getLocalPort() != e) {
               super._myAddress.setLocalPort(e);
               var6 = false;
            } else {
               var6 = false;
            }
         } finally {
            if (var6) {
               EventLogger.logEvent(447071754022829032L, 1413696867, 0);
               TcpUtils.logConnectionDatabase();
               throw new Object("Max connections opened.");
            }
         }

         if ((
               this._listenSocketID = RadioInternal.simulTCPCommand(
                  4, this._listenSocketID, 0, super._myAddress.getLocalPort(), RadioInfo.getAccessPointNumber(super._myAddress.getApnName())
               )
            )
            == -1) {
            throw new Object();
         }

         if (this._listenSocketID == 11) {
            throw new Object();
         }
      } finally {
         throw new Object();
      }
   }

   public final Connection openPrim(String name, int mode, boolean timeouts) {
      return this.openPrim(new SimulTcpAddress(name), mode, timeouts);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final StreamConnection getNextBackloggedConnection(int portToLookFor) {
      Protocol tcpConnection = null;
      int sizeOfBacklog = super._tcpConnectionBacklog.size();

      int i;
      for (i = 0; i < sizeOfBacklog; i++) {
         tcpConnection = (Protocol)super._tcpConnectionBacklog.elementAt(i);
         if (tcpConnection != null && tcpConnection.getLocalPortInternal() == portToLookFor) {
            break;
         }
      }

      if (i == sizeOfBacklog) {
         return null;
      }

      synchronized (super._backlogWaitObj) {
         super._tcpConnectionBacklog.removeElementAt(i);
         super._backlogWaitObj.notifyAll();
      }

      int newConnSocketID = -1;
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         newConnSocketID = RadioInternal.simulTCPCommand(
            12, this._listenSocketID, 0, super._myAddress.getLocalPort(), RadioInfo.getAccessPointNumber(super._myAddress.getApnName())
         );
         if (newConnSocketID == -1) {
            throw new Object();
         }

         var9 = false;
      } finally {
         if (var9) {
            throw new Object();
         }
      }

      tcpConnection.postAccept(super._mode, super._timeouts, newConnSocketID);
      super._transport.addConnection((WeakReference)(new Object(tcpConnection)));
      return tcpConnection;
   }

   @Override
   protected final boolean isConnectionWaiting(int portNumToSearchFor) {
      int sizeOfBacklog = super._tcpConnectionBacklog.size();

      for (int i = 0; i < sizeOfBacklog; i++) {
         Object o = super._tcpConnectionBacklog.elementAt(i);
         if (o != null && ((StreamDatagramConnectionBase)o).getLocalPortInternal() == portNumToSearchFor) {
            return true;
         }
      }

      return false;
   }

   public final void addConnectionToBacklog(StreamConnection streamConnection) {
      this.throwIOExceptionIfThisConnIsClosed();
      synchronized (super._backlogWaitObj) {
         super._tcpConnectionBacklog.addElement(streamConnection);
         super._backlogWaitObj.notifyAll();
      }
   }

   @Override
   public final void close() {
      if (!super._isClosed) {
         super._isClosed = true;
         if (super._myAddress != null) {
            TcpUtils.removeFromTcpConnectionDatabase(this, (TcpConnectionIdentifier)super._myAddress);
         }

         this.closeAssociatedConnections();
         super._tunnel.close();
         if (super._transport != null) {
            super._transport.close(this);
         }

         synchronized (super._backlogWaitObj) {
            super._backlogWaitObj.notifyAll();
         }

         if (this._listenSocketID != -1 && RadioInternal.simulTCPCommand(1, this._listenSocketID, 0, 0, 0) < 0) {
            throw new Object();
         }
      }
   }

   @Override
   public final int getLocalPort() {
      this.throwIOExceptionIfThisConnIsClosed();
      return super._myAddress.getLocalPort();
   }
}

package net.rim.device.cldc.io.tcp;

import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.system.EventLogger;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.internal.io.streamdatagram.StreamDatagramTransportBase;
import net.rim.device.internal.io.tcp.TcpAddress;
import net.rim.device.internal.io.tcp.TcpConstants;
import net.rim.device.internal.io.tcp.TcpDatagramBase;
import net.rim.device.internal.io.tcp.TcpDatagramProperties;
import net.rim.device.internal.system.TCPPacketHeader;
import net.rim.vm.WeakReference;

public final class Transport extends StreamDatagramTransportBase implements TcpConstants {
   private int _maxPacketSize;
   private TcpTimerThread _timerThread;
   Vector _tcpConnectionDatabase = (Vector)(new Object());
   static final int TCP_PENDING_WORTHY_FLAGS;
   static final int TCP_FLAGS_RESET_OR_ACK;

   public Transport() {
      this._maxPacketSize = TCPPacketHeader.getMaxPacketSize();
   }

   @Override
   public final void init() {
      super.init(this.openNativeConnection());
      this._timerThread = new TcpTimerThread();
      ProtocolDaemon.getInstance().startThread(this._timerThread);
      EventLogger.register(447071754022829032L, "net.rim.tcp", 2);
      EventLogger.logEvent(447071754022829032L, 1413695860, 0);
   }

   private final DatagramConnection openNativeConnection() {
      DatagramConnection newConnection = (DatagramConnection)Connector.open("tcpdatagram://");
      return newConnection;
   }

   protected final int getMaximumLength() {
      return this._maxPacketSize;
   }

   @Override
   protected final void sendDatagram(Datagram dgram) {
      this.addSendRequest(super._subConnection, dgram);
   }

   @Override
   protected final boolean isEssential(Datagram dgram) {
      if (dgram instanceof TcpDatagramBase && dgram.getLength() == 0) {
         TcpDatagramBase datagram = (TcpDatagramBase)dgram;
         TcpDatagramProperties props = datagram._tcpProps;
         if (props._isEssential) {
            return true;
         }

         int flags = props._flags;
         return (flags & 7) != 0 || (flags & 16) == 0;
      } else {
         return true;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final boolean passToListeners(Datagram datagram) {
      TcpDatagramBase tcpDatagram = (TcpDatagramBase)datagram;
      int flags = tcpDatagram._tcpProps._flags;
      if ((flags & 23) == 0) {
         return false;
      }

      int datagramListenPort = ((TcpAddress)tcpDatagram.getAddressBase()).getLocalPort();
      TcpServerSocketConnection tcpServerSocketConnPtr = null;
      if (super._wServerCache != null) {
         tcpServerSocketConnPtr = (TcpServerSocketConnection)super._wServerCache.get();
         if (tcpServerSocketConnPtr == null) {
            super._wServerCache = null;
         } else {
            boolean var14 = false /* VF: Semaphore variable */;

            label101:
            try {
               var14 = true;
               if (this.passDatagramToTcpServerSocketConn(tcpServerSocketConnPtr, tcpDatagram, datagramListenPort, flags)) {
                  return true;
               }

               var14 = false;
            } finally {
               if (var14) {
                  super._wServerCache = null;
                  break label101;
               }
            }
         }
      }

      int _superServerConnectionsSize = super._superServerConnections.size();

      for (int i = 0; i < _superServerConnectionsSize; i++) {
         WeakReference w = (WeakReference)super._superServerConnections.elementAt(i);
         if (w != null) {
            tcpServerSocketConnPtr = (TcpServerSocketConnection)w.get();
         }

         if (w != null && tcpServerSocketConnPtr != null) {
            try {
               if (this.passDatagramToTcpServerSocketConn(tcpServerSocketConnPtr, tcpDatagram, datagramListenPort, flags)) {
                  super._wServerCache = (WeakReference)(new Object(tcpServerSocketConnPtr));
                  return true;
               }
            } finally {
               ;
            }
         } else {
            super._superServerConnections.removeElementAt(i--);
            _superServerConnectionsSize--;
         }
      }

      return false;
   }

   private final boolean passDatagramToTcpServerSocketConn(
      TcpServerSocketConnection tcpServerSocketConnPtr, TcpDatagramBase tcpDatagram, int datagramListenPort, int flags
   ) {
      if (super._wPendingCache != null) {
         Protocol tcpConn = (Protocol)super._wPendingCache.get();
         if (tcpConn != null && tcpConn.isAddressed(tcpDatagram.getAddressBase())) {
            int tcpState = tcpConn.pendingConnectionDatagramReceived(tcpDatagram);
            switch (tcpState) {
               case -1:
               case 2:
                  break;
               case 1:
                  if (flags == 2) {
                     return true;
                  }
                  break;
               case 3:
               case 5:
               case 6:
               case 7:
               case 8:
               case 9:
               case 10:
                  return true;
               case 4:
               default:
                  tcpServerSocketConnPtr.addConnectionToBacklog(tcpConn);
               case 0:
                  synchronized (tcpServerSocketConnPtr._pendingConnectionWaitObj) {
                     tcpServerSocketConnPtr._pendingConnectionList.removeElement(tcpConn);
                     return true;
                  }
            }
         }
      }

      if (tcpServerSocketConnPtr.getLocalPort() != datagramListenPort) {
         return false;
      }

      synchronized (tcpServerSocketConnPtr._pendingConnectionWaitObj) {
         Vector pendingConnectionList = tcpServerSocketConnPtr._pendingConnectionList;
         int pendingConnectionListSize = pendingConnectionList.size();

         for (int i = 0; i < pendingConnectionListSize; i++) {
            Protocol tcpConn = (Protocol)pendingConnectionList.elementAt(i);
            if (tcpConn == null) {
               pendingConnectionList.removeElementAt(i--);
               pendingConnectionListSize--;
            } else if (tcpConn.isAddressed(tcpDatagram.getAddressBase())) {
               super._wPendingCache = (WeakReference)(new Object(tcpConn));
               int tcpState = tcpConn.pendingConnectionDatagramReceived(tcpDatagram);
               switch (tcpState) {
                  case -1:
                  case 2:
                     break;
                  case 1:
                     if (flags == 2) {
                        return true;
                     }
                     break;
                  case 4:
                  default:
                     tcpServerSocketConnPtr.addConnectionToBacklog(tcpConn);
                  case 0:
                     pendingConnectionList.removeElementAt(i--);
                  case 3:
                  case 5:
                  case 6:
                  case 7:
                  case 8:
                  case 9:
                  case 10:
                     return true;
               }
            }
         }
      }

      if (flags == 2) {
         return tcpServerSocketConnPtr.isBacklogFull() ? true : this.openNewTcpConnectionForServerSocket(tcpServerSocketConnPtr, tcpDatagram);
      } else {
         return false;
      }
   }

   private final boolean openNewTcpConnectionForServerSocket(TcpServerSocketConnection param1, Datagram param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramTransportBase._connectionBaseFactory Lnet/rim/device/internal/io/streamdatagram/StreamDatagramConnectionBaseFactory;
      // 04: invokevirtual net/rim/device/internal/io/streamdatagram/StreamDatagramConnectionBaseFactory.createInstance ()Lnet/rim/device/internal/io/streamdatagram/StreamDatagramConnectionBase;
      // 07: checkcast net/rim/device/cldc/io/tcp/Protocol
      // 0a: astore 3
      // 0b: aload 3
      // 0c: aload 2
      // 0d: checkcast net/rim/device/internal/io/tcp/TcpDatagramBase
      // 10: aload 1
      // 11: invokevirtual net/rim/device/internal/io/streamdatagram/StreamDatagramServerSocketConnectionBase.getMode ()I
      // 14: aload 1
      // 15: invokevirtual net/rim/device/internal/io/streamdatagram/StreamDatagramServerSocketConnectionBase.getTimeouts ()Z
      // 18: invokevirtual net/rim/device/cldc/io/tcp/Protocol.spawnNewConnectionForServerSocket (Lnet/rim/device/internal/io/tcp/TcpDatagramBase;IZ)Ljavax/microedition/io/Connection;
      // 1b: pop
      // 1c: aload 0
      // 1d: new java/lang/Object
      // 20: dup
      // 21: aload 3
      // 22: invokespecial net/rim/vm/WeakReference.<init> (Ljava/lang/Object;)V
      // 25: putfield net/rim/device/internal/io/streamdatagram/StreamDatagramTransportBase._wPendingCache Lnet/rim/vm/WeakReference;
      // 28: aload 1
      // 29: aload 3
      // 2a: invokevirtual net/rim/device/cldc/io/tcp/TcpServerSocketConnection.addConnectionToPendingConnectionList (Ljavax/microedition/io/StreamConnection;)V
      // 2d: bipush 1
      // 2e: ireturn
      // 2f: astore 4
      // 31: ldc2_w 447071754022829032
      // 34: ldc_w 1413695843
      // 37: bipush 1
      // 38: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 3b: pop
      // 3c: bipush 0
      // 3d: ireturn
      // 3e: astore 4
      // 40: bipush 0
      // 41: ireturn
      // try (5 -> 24): 25 null
      // try (5 -> 24): 33 null
   }

   @Override
   protected final void processReceivedDatagram(Datagram datagram) {
      if (!this.passUpDatagram(datagram)) {
      }
   }

   public final WeakReference[] getConnections() {
      return super._superConnections;
   }

   final TcpTimerThread getTcpTimers() {
      return this._timerThread;
   }

   final DatagramConnectionBase getSubConnection() {
      return super._subConnection;
   }
}

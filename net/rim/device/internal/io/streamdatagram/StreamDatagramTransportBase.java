package net.rim.device.internal.io.streamdatagram;

import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.io.IOPortAlreadyBoundException;
import net.rim.device.api.io.TransportBase;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.internal.io.TrafficLogger;
import net.rim.vm.WeakReference;

public class StreamDatagramTransportBase extends TransportBase implements StreamDatagramConnectionConstants, RadioStatusListener {
   protected DatagramConnectionBase _subConnection;
   private StreamReceiveThread _receiveThread;
   protected StreamSendThread _sendThread;
   protected StreamDatagramConnectionBaseFactory _connectionBaseFactory;
   protected WeakReference[] _superConnections = new WeakReference[0];
   protected Vector _superServerConnections = new Vector();
   public Object _connectionTableWaitObj = new Object();
   protected WeakReference _wCache;
   protected WeakReference _wPendingCache;
   protected WeakReference _wServerCache;

   protected void processReceivedDatagram(Datagram _1) {
      throw null;
   }

   protected void sendDatagram(Datagram _1) {
      throw null;
   }

   protected boolean isEssential(Datagram _1) {
      throw null;
   }

   protected boolean passToListeners(Datagram _1) {
      throw null;
   }

   public void init(DatagramConnection subConnection) {
      if (subConnection != null) {
         if (!(subConnection instanceof DatagramConnectionBase)) {
            throw new RuntimeException();
         }

         this._subConnection = (DatagramConnectionBase)subConnection;
         this._receiveThread = new StreamReceiveThread(this, this._subConnection);
         ProtocolDaemon.getInstance().startThread(this._receiveThread);
      } else {
         this._subConnection = null;
      }

      this._sendThread = new StreamSendThread();
      this._sendThread.start();
      ProtocolDaemon.getInstance().addRadioListener(this);
   }

   public void initForServerSocketConnections() {
      if (this._connectionBaseFactory == null) {
         this._connectionBaseFactory = new StreamDatagramConnectionBaseFactory(this);
      }
   }

   public void addConnection(StreamConnection connection) {
      this.addConnection(new WeakReference(connection));
   }

   public void addConnection(WeakReference w) {
      this._wCache = w;
      if (w == this._wPendingCache) {
         this._wPendingCache = null;
      }

      this.stopSelfDestructTimer();
      synchronized (this._superConnections) {
         if (w.get() instanceof StreamDatagramConnectionBase) {
            ((StreamDatagramConnectionBase)w.get()).setTrafficLogger(super._tLogger);
         }

         Arrays.add(this._superConnections, w);
      }
   }

   public void addServerConnection(ServerSocketConnection serverConnection) {
      int port = serverConnection.getLocalPort();
      synchronized (this._superServerConnections) {
         for (int i = this._superServerConnections.size() - 1; i >= 0; i--) {
            WeakReference w = (WeakReference)this._superServerConnections.elementAt(i);
            ServerSocketConnection tcpServerSocketConnPtr;
            if (w != null && (tcpServerSocketConnPtr = (ServerSocketConnection)w.get()) != null) {
               if (tcpServerSocketConnPtr.getLocalPort() == port) {
                  throw new IOPortAlreadyBoundException();
               }
            } else {
               this._superServerConnections.removeElementAt(i);
            }
         }

         this._superServerConnections.addElement(new WeakReference(serverConnection));
      }
   }

   public StreamDatagramConnectionBase getNewStreamDatagramConnectionBase() {
      return this._connectionBaseFactory.createInstance();
   }

   public Vector getTcpConnectionDatabase() {
      return null;
   }

   public void close(Connection connectionToClose) {
      int i = 0;
      if (connectionToClose instanceof StreamDatagramConnectionBase) {
         synchronized (this._superConnections) {
            StreamConnection connection = null;
            int size = this._superConnections.length;

            for (int var13 = 0; var13 < size; var13++) {
               connection = null;
               WeakReference w = this._superConnections[var13];
               if (w != null) {
                  connection = (StreamConnection)w.get();
               }

               if (w == null || connection == null || connection == connectionToClose) {
                  Arrays.removeAt(this._superConnections, var13--);
                  size--;
               }
            }
         }
      } else if (connectionToClose instanceof ServerSocketConnection) {
         synchronized (this._superServerConnections) {
            int superServerConnectionSize = this._superServerConnections.size();

            for (int var14 = 0; var14 < superServerConnectionSize; var14++) {
               ServerSocketConnection serverSockConnection = null;
               WeakReference w = (WeakReference)this._superServerConnections.elementAt(var14);
               if (w != null) {
                  serverSockConnection = (ServerSocketConnection)w.get();
               }

               if (w == null || serverSockConnection == null || serverSockConnection == connectionToClose) {
                  this._superServerConnections.removeElementAt(var14--);
                  superServerConnectionSize--;
               }
            }
         }
      }

      synchronized (this._connectionTableWaitObj) {
         this._connectionTableWaitObj.notifyAll();
      }

      if (this._superServerConnections.size() == 0 && this._superConnections.length == 0) {
         this.startSelfDestructTimer();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected boolean passUpDatagram(Datagram datagram) {
      StreamDatagramConnectionBase connection = null;
      WeakReference w = null;
      DatagramAddressBase addressBase = null;
      if (!(datagram instanceof DatagramBase)) {
         return false;
      }

      addressBase = ((DatagramBase)datagram).getAddressBase();
      if (addressBase == null) {
         return false;
      }

      if (this._wCache != null) {
         connection = (StreamDatagramConnectionBase)this._wCache.get();
         if (connection != null) {
            boolean var15 = false /* VF: Semaphore variable */;

            label131:
            try {
               var15 = true;
               if (connection.isAddressed(addressBase)) {
                  connection.processReceivedDatagram(datagram);
                  return true;
               }

               var15 = false;
            } finally {
               if (var15) {
                  this._wCache = null;
                  break label131;
               }
            }
         }
      }

      synchronized (this._superConnections) {
         for (int i = this._superConnections.length - 1; i >= 0; i--) {
            w = this._superConnections[i];
            if (w != null) {
               connection = (StreamDatagramConnectionBase)w.get();
            }

            if (w != null && connection != null) {
               boolean ret = connection.isAddressed(addressBase);
               if (ret) {
                  connection.processReceivedDatagram(datagram);
                  this._wCache = new WeakReference(connection);
                  return true;
               }
            } else {
               Arrays.removeAt(this._superConnections, i);
            }
         }
      }

      try {
         return this.passToListeners(datagram);
      } finally {
         ;
      }
   }

   protected void addSendRequest(Object sendObj, Datagram datagram) {
      if (this._sendThread == null) {
         this._sendThread = new StreamSendThread();
         this._sendThread.start();
      }

      this._sendThread.addRequest(sendObj, datagram);
   }

   protected byte[] setup(int callType, Object context) {
      return null;
   }

   public boolean isConnectionTableFull() {
      int size = 0;
      Object temp = null;
      synchronized (this._superConnections) {
         synchronized (this._superConnections) {
            for (int i = this._superConnections.length - 1; i >= 0; i--) {
               temp = this._superConnections[i].get();
               if (temp == null) {
                  System.out.println("Connections #" + (i + 1) + " purged");
                  Arrays.removeAt(this._superConnections, i);
               }
            }
         }

         size += this._superConnections.length;
      }

      synchronized (this._superServerConnections) {
         size += this._superServerConnections.size();
      }

      return size >= 30;
   }

   public void logConnections() {
      if (EventLogger.getMinimumLevel() == 5) {
         StringBuffer strbuf = new StringBuffer();
         strbuf.append("TCP Connections:\n");
         synchronized (this._superConnections) {
            for (int i = this._superConnections.length - 1; i >= 0; i--) {
               appendConnection(strbuf, this._superConnections[i]);
            }
         }

         strbuf.append("TCP Server Connections:\n");
         synchronized (this._superServerConnections) {
            for (int i = this._superServerConnections.size() - 1; i >= 0; i--) {
               appendConnection(strbuf, (WeakReference)this._superServerConnections.elementAt(i));
            }
         }

         EventLogger.logEvent(447071754022829032L, strbuf.toString().getBytes(), 5);
      }
   }

   public void mobilityManagementEvent(int eventCode, int cause) {
   }

   @Override
   public void pdpStateChange(int apn, int state, int cause) {
      for (int i = this._superConnections.length - 1; i >= 0; i--) {
         WeakReference w = this._superConnections[i];
         if (w != null) {
            StreamDatagramConnectionBase connection = (StreamDatagramConnectionBase)w.get();
            if (connection instanceof RadioStatusListener) {
               connection.pdpStateChange(apn, state, cause);
            }
         }
      }
   }

   @Override
   public void signalLevel(int level) {
   }

   @Override
   public void networkStarted(int networkId, int service) {
   }

   @Override
   public void baseStationChange() {
   }

   @Override
   public void networkStateChange(int state) {
   }

   @Override
   public void networkScanComplete(boolean success) {
   }

   @Override
   public void radioTurnedOff() {
   }

   @Override
   public void networkServiceChange(int networkId, int service) {
   }

   private static void appendConnection(StringBuffer param0, WeakReference param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 2
      // 02: aload 1
      // 03: invokevirtual net/rim/vm/WeakReference.get ()Ljava/lang/Object;
      // 06: astore 3
      // 07: aload 3
      // 08: dup
      // 09: instanceof net/rim/device/internal/io/streamdatagram/StreamDatagramConnectionBase
      // 0c: ifne 13
      // 0f: pop
      // 10: goto 3b
      // 13: checkcast net/rim/device/internal/io/streamdatagram/StreamDatagramConnectionBase
      // 16: astore 4
      // 18: aload 4
      // 1a: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramConnectionBase._myAddress Lnet/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase;
      // 1d: astore 2
      // 1e: aload 0
      // 1f: ldc_w "client("
      // 22: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 25: pop
      // 26: aload 0
      // 27: aload 4
      // 29: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramConnectionBase._currentTcpState I
      // 2c: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 2f: pop
      // 30: aload 0
      // 31: ldc_w "(:"
      // 34: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 37: pop
      // 38: goto 56
      // 3b: aload 3
      // 3c: dup
      // 3d: instanceof net/rim/device/internal/io/streamdatagram/StreamDatagramServerSocketConnectionBase
      // 40: ifne 47
      // 43: pop
      // 44: goto 56
      // 47: checkcast net/rim/device/internal/io/streamdatagram/StreamDatagramServerSocketConnectionBase
      // 4a: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramServerSocketConnectionBase._myAddress Lnet/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase;
      // 4d: astore 2
      // 4e: aload 0
      // 4f: ldc_w "server:"
      // 52: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 55: pop
      // 56: aload 0
      // 57: ldc_w "//"
      // 5a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 5d: pop
      // 5e: aload 0
      // 5f: aload 2
      // 60: invokevirtual net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase.getIpAddress ()I
      // 63: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 66: pop
      // 67: aload 0
      // 68: bipush 58
      // 6a: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 6d: pop
      // 6e: aload 0
      // 6f: aload 2
      // 70: invokevirtual net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase.getDestPort ()I
      // 73: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 76: pop
      // 77: aload 0
      // 78: bipush 59
      // 7a: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 7d: pop
      // 7e: aload 0
      // 7f: aload 2
      // 80: invokevirtual net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase.getLocalPort ()I
      // 83: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 86: pop
      // 87: aload 0
      // 88: bipush 47
      // 8a: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 8d: pop
      // 8e: aload 0
      // 8f: aload 2
      // 90: invokevirtual net/rim/device/internal/io/streamdatagram/StreamDatagramAddressBase.getApnName ()Ljava/lang/String;
      // 93: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 96: pop
      // 97: aload 0
      // 98: bipush 10
      // 9a: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 9d: pop
      // 9e: return
      // 9f: astore 2
      // a0: aload 0
      // a1: ldc_w "ClassCastException"
      // a4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // a7: pop
      // a8: return
      // a9: astore 2
      // aa: aload 0
      // ab: ldc_w "NullPointerException"
      // ae: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // b1: pop
      // b2: return
      // try (0 -> 83): 84 null
      // try (0 -> 83): 90 null
   }

   @Override
   public void setTrafficLogger(TrafficLogger logger) {
      super.setTrafficLogger(logger);
      synchronized (this._superConnections) {
         for (int i = this._superConnections.length - 1; i >= 0; i--) {
            StreamDatagramConnectionBase connection = null;
            WeakReference w = this._superConnections[i];
            if (w != null) {
               connection = (StreamDatagramConnectionBase)w.get();
            }

            if (w != null && connection != null) {
               connection.setTrafficLogger(logger);
            } else {
               Arrays.removeAt(this._superConnections, i);
            }
         }
      }
   }

   private void startSelfDestructTimer() {
   }

   @Override
   public void init() {
      throw null;
   }

   private void stopSelfDestructTimer() {
   }
}

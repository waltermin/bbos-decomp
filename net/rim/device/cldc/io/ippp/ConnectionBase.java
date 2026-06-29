package net.rim.device.cldc.io.ippp;

import javax.microedition.io.Datagram;
import net.rim.device.api.io.ConnectionClosedException;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.EventLogger;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.device.internal.firewall.Firewall;
import net.rim.vm.TraceBack;

public class ConnectionBase extends DatagramConnectionBase implements DatagramStatusListener, DatagramListener {
   protected byte _protocol;
   protected byte _protocolVersion;
   protected String _specificUID;
   protected String _groupUID;
   protected String _detereminedGroupUID;
   private boolean _encryptedChannel;
   private int _datagramSequenceNumber;
   protected URL _url;
   private Queue _queue;
   protected int _connectionID;
   protected boolean _errorOccured;
   protected boolean _disconnected;
   protected int _errorCode;
   protected String _errorMessage;
   protected String _connectionHandlerName = "";
   protected boolean _connectRequestSent;
   private int _nextSequence;
   private short _flowControlTimeout;
   private boolean _receiveActive = true;
   private int _sysCheckTimeout;
   private static final String STRING_ConnectionHandler = "ConnectionHandler";
   private static final String STRING_ConnectionTimeout = "ConnectionTimeout";
   private static final String STRING_SpecificUID = "SpecificUID";
   private static final String STRING_FlowControlTimeout = "FlowControlTimeout";
   private static final String MSG_Connection_is_closed = "Connection is closed";
   private static final int EVENT_LOG_TX = 1417167682;
   private static final int EVENT_LOG_RX = 1383613250;
   private static final byte IPPP_VERSION_BYTE = 16;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void initialize(String name, boolean useTimeouts) {
      this._protocolVersion = 16;
      this._url = (URL)(new Object("ippp", name));
      URLParameters parameters = this._url.getRIMParameters();
      super.openPrim(name, 0, useTimeouts);
      this._detereminedGroupUID = SocketTransportBase.findAcceptableConnectionUid(parameters);
      long timeout = this.getTransport().getTimeout(null);
      super.setTimeout(timeout);
      if (parameters != null) {
         String parameterValue = parameters.getValue("ConnectionHandler");
         if (parameterValue != null) {
            this._connectionHandlerName = parameterValue;
         }

         parameterValue = parameters.getValue("ConnectionTimeout");
         if (parameterValue != null) {
            boolean var14 = false /* VF: Semaphore variable */;

            try {
               var14 = true;
               long e = Long.parseLong(parameterValue);
               if (e > 0) {
                  timeout = e;
               }

               super.setTimeout(timeout);
               var14 = false;
            } finally {
               if (var14) {
                  StringBuffer sb = (StringBuffer)(new Object());
                  sb.append("ConnectionTimeout").append(" must have a valid long value");
                  throw new Object(sb.toString());
               }
            }
         }

         parameterValue = parameters.getValue("FlowControlTimeout");
         if (parameterValue != null) {
            boolean var11 = false /* VF: Semaphore variable */;

            try {
               var11 = true;
               short var19 = Short.parseShort(parameterValue);
               this._flowControlTimeout = var19;
               parameters.remove("FlowControlTimeout");
               var11 = false;
            } finally {
               if (var11) {
                  StringBuffer sb = (StringBuffer)(new Object());
                  sb.append("FlowControlTimeout").append(" must have a valid short value");
                  throw new Object(sb.toString());
               }
            }
         }

         if (parameters.containParameter("SpecificUID")) {
            this._specificUID = parameters.getValue("SpecificUID");
         }
      }
   }

   public URL getURL() {
      return this._url;
   }

   protected void initializeDatagram(SocketDatagram socketDatagram) {
      socketDatagram.setAddress(super._addressBase.getAddress());
      socketDatagram.setConnectionID(this.getConnectionID());
      socketDatagram.setProtocol(this._protocol);
      socketDatagram.setProtocolVersion(this._protocolVersion);
      socketDatagram.setDomainName(this._url.getHost());
      socketDatagram.setPort((short)this._url.getPort());
   }

   void superSend(Datagram datagram) {
      EventLogger.logEvent(6406224406390975741L, 1417167682, 5);
      super.send(datagram);
   }

   public int getConnectionID() {
      return this._connectionID;
   }

   public byte getProtocol() {
      return this._protocol;
   }

   public byte getProtocolVersion() {
      return this._protocolVersion;
   }

   public short getPort() {
      return (short)this._url.getPort();
   }

   public String getDomainName() {
      return this._url.getHost();
   }

   public URLParameters getRIMParameters() {
      return this._url.getRIMParameters();
   }

   public String getSpecificUID() {
      return this._specificUID;
   }

   public String getGroupUID() {
      return this._groupUID;
   }

   public String getConnectionHandlerName() {
      return this._connectionHandlerName;
   }

   public boolean isChannelEncrypted() {
      return this._encryptedChannel;
   }

   public int getDataSize() {
      return this.getTransport().getDataSize(this._groupUID == null ? this._detereminedGroupUID : this._groupUID);
   }

   void cancelReceiving() {
      synchronized (this._queue) {
         this._receiveActive = false;
         this._queue.notifyAll();
      }
   }

   public void errorOccured(int param1, String param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: iload 1
      // 02: putfield net/rim/device/cldc/io/ippp/ConnectionBase._errorCode I
      // 05: aload 0
      // 06: aload 2
      // 07: putfield net/rim/device/cldc/io/ippp/ConnectionBase._errorMessage Ljava/lang/String;
      // 0a: aload 0
      // 0b: getfield net/rim/device/cldc/io/ippp/ConnectionBase._disconnected Z
      // 0e: ifne 68
      // 11: aload 0
      // 12: invokevirtual net/rim/device/cldc/io/ippp/ConnectionBase.newDatagram ()Ljavax/microedition/io/Datagram;
      // 15: checkcast net/rim/device/cldc/io/ippp/SocketDatagram
      // 18: astore 3
      // 19: aload 3
      // 1a: bipush -128
      // 1c: invokevirtual net/rim/device/cldc/io/ippp/IPPPDatagramBase.addIPPPFlags (B)V
      // 1f: aload 3
      // 20: aload 0
      // 21: getfield net/rim/device/cldc/io/ippp/ConnectionBase._errorCode I
      // 24: invokevirtual net/rim/device/cldc/io/ippp/IPPPDatagramBase.setErrorCode (I)V
      // 27: aload 3
      // 28: aload 0
      // 29: getfield net/rim/device/cldc/io/ippp/ConnectionBase._errorMessage Ljava/lang/String;
      // 2c: invokevirtual net/rim/device/cldc/io/ippp/IPPPDatagramBase.setErrorMessage (Ljava/lang/String;)V
      // 2f: aload 0
      // 30: aload 3
      // 31: invokevirtual net/rim/device/cldc/io/ippp/ConnectionBase.send (Ljavax/microedition/io/Datagram;)V
      // 34: aload 0
      // 35: invokevirtual net/rim/device/cldc/io/ippp/ConnectionBase.close ()V
      // 38: aload 0
      // 39: bipush 1
      // 3a: putfield net/rim/device/cldc/io/ippp/ConnectionBase._disconnected Z
      // 3d: goto 68
      // 40: astore 3
      // 41: aload 0
      // 42: invokevirtual net/rim/device/cldc/io/ippp/ConnectionBase.close ()V
      // 45: aload 0
      // 46: bipush 1
      // 47: putfield net/rim/device/cldc/io/ippp/ConnectionBase._disconnected Z
      // 4a: goto 68
      // 4d: astore 3
      // 4e: aload 0
      // 4f: invokevirtual net/rim/device/cldc/io/ippp/ConnectionBase.close ()V
      // 52: aload 0
      // 53: bipush 1
      // 54: putfield net/rim/device/cldc/io/ippp/ConnectionBase._disconnected Z
      // 57: goto 68
      // 5a: astore 4
      // 5c: aload 0
      // 5d: invokevirtual net/rim/device/cldc/io/ippp/ConnectionBase.close ()V
      // 60: aload 0
      // 61: bipush 1
      // 62: putfield net/rim/device/cldc/io/ippp/ConnectionBase._disconnected Z
      // 65: aload 4
      // 67: athrow
      // 68: aload 0
      // 69: bipush 1
      // 6a: putfield net/rim/device/cldc/io/ippp/ConnectionBase._errorOccured Z
      // 6d: return
      // try (9 -> 27): 33 null
      // try (9 -> 27): 40 null
      // try (9 -> 27): 47 null
      // try (33 -> 34): 47 null
      // try (40 -> 41): 47 null
      // try (47 -> 48): 47 null
   }

   long getTimeout() {
      return super._timeout;
   }

   @Override
   public void dataReceived(SocketDatagram datagram) {
      synchronized (this._queue) {
         if (!this._queue.put(datagram.getSequence(), datagram)) {
            this.errorOccured(128, "Receive queue was overrun");
         }

         this._queue.notify();
      }
   }

   @Override
   public void updateDatagramStatus(int _1, int _2, Object _3) {
      throw null;
   }

   @Override
   public void errorReceived(SocketDatagram _1) {
      throw null;
   }

   @Override
   public void disconnectOrderReceived(SocketDatagram _1) {
      throw null;
   }

   @Override
   public void connectRequestReceived(SocketDatagram _1) {
      throw null;
   }

   @Override
   protected boolean isAddressed(DatagramAddressBase addressBase) {
      return this.isAddressed(addressBase.getAddress());
   }

   @Override
   public Datagram newDatagram() {
      return this.newDatagram(null, 0, 0, null);
   }

   protected ConnectionBase(Queue queue, String name, boolean useTimeouts) {
      this.initialize(name, useTimeouts);
      this._queue = queue;
      this._connectionID = this._queue.getConnectionID();
      this.getTransport().registerEstablishedConnection(this._connectionID, this);
      this._connectRequestSent = true;
   }

   protected ConnectionBase(String name, boolean useTimeouts) {
      this.initialize(name, useTimeouts);
      if (this._detereminedGroupUID == null) {
         throw new Object("Could not find a service book entry for IPPP");
      }

      SocketTransportBase transport = this.getTransport();
      this._sysCheckTimeout = transport.getInteractivePingPacketTimeout();
      this._connectionID = transport.getNextConnectionId();
      this._queue = new Queue(this._connectionID, transport.getQueueSize(this._detereminedGroupUID));
      transport.registerEstablishedConnection(this._connectionID, this);
   }

   @Override
   public void send(Datagram datagram) throws ConnectionClosedException {
      if (!super._isActive) {
         throw new ConnectionClosedException();
      }

      if (this._errorOccured) {
         throw new Object(this._errorMessage);
      }

      if (this._groupUID == null) {
         this._groupUID = this._detereminedGroupUID;
      }

      SocketDatagram socketDatagram = (SocketDatagram)datagram;
      socketDatagram.setConnectionHandlerName(this.getConnectionHandlerName());
      socketDatagram.setFlowControlTimeout(this._flowControlTimeout);
      socketDatagram.setSpecificUID(this._specificUID);
      socketDatagram.setGroupUID(this._groupUID);
      synchronized (this) {
         if (!this._connectRequestSent) {
            this._connectRequestSent = true;
            socketDatagram.addIPPPFlags((byte)1);
         }

         socketDatagram.setSequence(this._nextSequence);
         this._nextSequence = this._nextSequence == 255 ? 0 : ++this._nextSequence;
      }

      this.allocateDatagramId(datagram);
      socketDatagram.setDatagramStatusListener(this);
      this.superSend(datagram);
   }

   @Override
   public Datagram newDatagram(byte[] buf, int offset, int length, String addr) {
      SocketDatagram datagramBase = (SocketDatagram)super._transport.newDatagram(buf, offset, length, addr);
      this.initializeDatagram(datagramBase);
      return datagramBase;
   }

   @Override
   public void receive(Datagram datagram) throws ConnectionClosedException, SocketBaseIOException {
      if (super._isActive && this._receiveActive) {
         DatagramBase receivedDatagram = null;
         synchronized (this._queue) {
            while (receivedDatagram == null) {
               receivedDatagram = this._queue.get(this._datagramSequenceNumber);
               if (receivedDatagram == null) {
                  if (this._errorOccured) {
                     throw new Object(this._errorMessage);
                  }

                  if (!super._isActive || !this._receiveActive) {
                     throw new Object();
                  }

                  boolean sysCheckSent = false;

                  try {
                     long tempTimeOut = super._timeout;

                     while (true) {
                        long st = System.currentTimeMillis();
                        if (this._sysCheckTimeout != 0 && tempTimeOut > this._sysCheckTimeout) {
                           this._queue.wait(this._sysCheckTimeout);
                        } else {
                           this._queue.wait(tempTimeOut);
                        }

                        long et = System.currentTimeMillis();
                        receivedDatagram = this._queue.get(this._datagramSequenceNumber);
                        if (receivedDatagram == null) {
                           tempTimeOut -= et - st;
                           if (tempTimeOut <= 0) {
                              throw new Object(((StringBuffer)(new Object("Local connection timed out after ~ "))).append(super._timeout).toString());
                           }

                           if (this._errorOccured) {
                              throw new Object(this._errorMessage);
                           }

                           if (!super._isActive || !this._receiveActive) {
                              throw new Object();
                           }

                           if (this._sysCheckTimeout != 0) {
                              this.getTransport().sendSysCheck(this._groupUID, this._specificUID);
                              this._sysCheckTimeout = 0;
                              sysCheckSent = true;
                           }
                        } else {
                           if (sysCheckSent) {
                              if (et - st <= 10000) {
                                 this.getTransport().sysCheckWorked();
                              }

                              sysCheckSent = false;
                           }

                           this._sysCheckTimeout = 0;
                           tempTimeOut = 0;
                        }

                        if (tempTimeOut <= 0) {
                           break;
                        }
                     }
                  } finally {
                     continue;
                  }
               }
            }

            if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)
               && !Firewall.getInstance().allowConnection(this._url.getScheme(), "", this.getProperties(this._url.toString()))) {
               throw new Object("Permission denied");
            }

            ((DatagramBase)datagram).copy(receivedDatagram);
            this._datagramSequenceNumber = this._datagramSequenceNumber == 255 ? 0 : ++this._datagramSequenceNumber;
            EventLogger.logEvent(6406224406390975741L, 1383613250, 5);
         }

         SocketDatagram socketDatagram = (SocketDatagram)datagram;
         if (socketDatagram.testIPPPFlags((byte)-128)) {
            int errorCode = socketDatagram.getErrorCode();
            String errorMessage = socketDatagram.getErrorMessage();
            this._disconnected = true;
            this.errorOccured(errorCode, errorMessage);
            SocketBaseIOException sbioe = new SocketBaseIOException(this._errorMessage);
            sbioe.setExceptionCode(errorCode);
            throw sbioe;
         }

         if (this._groupUID == null) {
            this._groupUID = socketDatagram.getGroupUID();
         }

         if (this._specificUID == null) {
            this._specificUID = socketDatagram.getSpecificUID();
         }

         this._encryptedChannel = socketDatagram.wasEncrypted();
         if (socketDatagram.testIPPPFlags((byte)4)) {
            this.close();
         }

         if (super._tLogger != null) {
            super._tLogger.bytesReceived(this, 1, receivedDatagram.getAddress(), receivedDatagram.getLength(), receivedDatagram.getData());
         }
      } else {
         throw new ConnectionClosedException();
      }
   }

   @Override
   public synchronized boolean isAddressed(String addr) {
      return Integer.parseInt(addr) == this.getConnectionID();
   }

   private SocketTransportBase getTransport() {
      return (SocketTransportBase)super._transport;
   }

   @Override
   public void close() {
      ((SocketTransportBase)super._transport).removeQueue(this._connectionID);
      this.getTransport().deregisterEstablishedConnection(this._connectionID);
      synchronized (this._queue) {
         super._isActive = false;
         this._queue.notifyAll();
      }
   }
}

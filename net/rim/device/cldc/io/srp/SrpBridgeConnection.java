package net.rim.device.cldc.io.srp;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.SocketConnection;
import net.rim.device.api.io.ConnectionCloseListener;
import net.rim.device.api.io.ConnectionCloseProvider;
import net.rim.device.api.io.ConnectionClosedException;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.ssl.SSLConnection;
import net.rim.device.cldc.io.ssl.SSLConnectionOptions;
import net.rim.device.cldc.io.ssl.TLSIOException;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.cldc.io.utility.URLParameters;
import net.rim.device.internal.io.TrafficLogger;
import net.rim.vm.WeakReference;

final class SrpBridgeConnection implements DatagramConnection, ConnectionBaseInterface, ConnectionCloseListener {
   private SocketConnection _socket;
   private OutputStream _out;
   private InputStream _in;
   private int _open;
   private String _socketAddress;
   private int _mode;
   private boolean _timeouts;
   private WeakReference _connListener;
   private String[] _params;
   private DatagramStatusListener _listener;
   private SrpAddress _addressBase;
   private SrpAddress _receiveFilter;
   static final int STATE_INACTIVE;
   static final int STATE_CONNECTING;
   static final int STATE_ACTIVE;

   @Override
   public final void close() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokevirtual net/rim/device/cldc/io/srp/SrpBridgeConnection.getConnectionState ()I
      // 04: ifeq 53
      // 07: aload 0
      // 08: aconst_null
      // 09: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._socketAddress Ljava/lang/String;
      // 0c: aload 0
      // 0d: aload 0
      // 0e: astore 1
      // 0f: monitorenter
      // 10: aload 0
      // 11: bipush 0
      // 12: invokespecial net/rim/device/cldc/io/srp/SrpBridgeConnection.setConnectionState (I)V
      // 15: aload 0
      // 16: invokevirtual java/lang/Object.notifyAll ()V
      // 19: aload 1
      // 1a: monitorexit
      // 1b: goto 23
      // 1e: astore 2
      // 1f: aload 1
      // 20: monitorexit
      // 21: aload 2
      // 22: athrow
      // 23: aload 0
      // 24: getfield net/rim/device/cldc/io/srp/SrpBridgeConnection._in Ljava/io/InputStream;
      // 27: ifnull 31
      // 2a: aload 0
      // 2b: getfield net/rim/device/cldc/io/srp/SrpBridgeConnection._in Ljava/io/InputStream;
      // 2e: invokevirtual java/io/InputStream.close ()V
      // 31: aload 0
      // 32: getfield net/rim/device/cldc/io/srp/SrpBridgeConnection._out Ljava/io/OutputStream;
      // 35: ifnull 3f
      // 38: aload 0
      // 39: getfield net/rim/device/cldc/io/srp/SrpBridgeConnection._out Ljava/io/OutputStream;
      // 3c: invokevirtual java/io/OutputStream.close ()V
      // 3f: aload 0
      // 40: getfield net/rim/device/cldc/io/srp/SrpBridgeConnection._socket Ljavax/microedition/io/SocketConnection;
      // 43: ifnull 53
      // 46: aload 0
      // 47: getfield net/rim/device/cldc/io/srp/SrpBridgeConnection._socket Ljavax/microedition/io/SocketConnection;
      // 4a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 4f: goto 53
      // 52: astore 1
      // 53: aload 0
      // 54: aconst_null
      // 55: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._in Ljava/io/InputStream;
      // 58: aload 0
      // 59: aconst_null
      // 5a: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._out Ljava/io/OutputStream;
      // 5d: aload 0
      // 5e: aconst_null
      // 5f: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._socket Ljavax/microedition/io/SocketConnection;
      // 62: aload 0
      // 63: aload 0
      // 64: aconst_null
      // 65: dup_x1
      // 66: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._addressBase Lnet/rim/device/cldc/io/srp/SrpAddress;
      // 69: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._receiveFilter Lnet/rim/device/cldc/io/srp/SrpAddress;
      // 6c: aload 0
      // 6d: aconst_null
      // 6e: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._socketAddress Ljava/lang/String;
      // 71: aload 0
      // 72: aconst_null
      // 73: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._params [Ljava/lang/String;
      // 76: aload 0
      // 77: bipush 0
      // 78: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._mode I
      // 7b: aload 0
      // 7c: bipush 0
      // 7d: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._timeouts Z
      // 80: aload 0
      // 81: aconst_null
      // 82: invokevirtual net/rim/device/cldc/io/srp/SrpBridgeConnection.setSrpConnectionStatusListener (Lnet/rim/device/cldc/io/srp/SrpConnectionStatusListener;)V
      // 85: aload 0
      // 86: aconst_null
      // 87: invokevirtual net/rim/device/cldc/io/srp/SrpBridgeConnection.setDatagramStatusListener (Lnet/rim/device/api/io/DatagramStatusListener;)V
      // 8a: return
      // 8b: astore 3
      // 8c: aload 0
      // 8d: aconst_null
      // 8e: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._in Ljava/io/InputStream;
      // 91: aload 0
      // 92: aconst_null
      // 93: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._out Ljava/io/OutputStream;
      // 96: aload 0
      // 97: aconst_null
      // 98: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._socket Ljavax/microedition/io/SocketConnection;
      // 9b: aload 0
      // 9c: aload 0
      // 9d: aconst_null
      // 9e: dup_x1
      // 9f: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._addressBase Lnet/rim/device/cldc/io/srp/SrpAddress;
      // a2: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._receiveFilter Lnet/rim/device/cldc/io/srp/SrpAddress;
      // a5: aload 0
      // a6: aconst_null
      // a7: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._socketAddress Ljava/lang/String;
      // aa: aload 0
      // ab: aconst_null
      // ac: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._params [Ljava/lang/String;
      // af: aload 0
      // b0: bipush 0
      // b1: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._mode I
      // b4: aload 0
      // b5: bipush 0
      // b6: putfield net/rim/device/cldc/io/srp/SrpBridgeConnection._timeouts Z
      // b9: aload 0
      // ba: aconst_null
      // bb: invokevirtual net/rim/device/cldc/io/srp/SrpBridgeConnection.setSrpConnectionStatusListener (Lnet/rim/device/cldc/io/srp/SrpConnectionStatusListener;)V
      // be: aload 0
      // bf: aconst_null
      // c0: invokevirtual net/rim/device/cldc/io/srp/SrpBridgeConnection.setDatagramStatusListener (Lnet/rim/device/api/io/DatagramStatusListener;)V
      // c3: aload 3
      // c4: athrow
      // try (10 -> 17): 18 null
      // try (18 -> 21): 18 null
      // try (38 -> 41): 42 null
      // try (0 -> 43): 77 null
      // try (77 -> 78): 77 null
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      return this.reopen(name, mode, timeouts, null);
   }

   @Override
   public final int getProperties(String name) {
      return 1;
   }

   final synchronized int getConnectionState() {
      return this._open;
   }

   final synchronized void setSrpConnectionStatusListener(SrpConnectionStatusListener listener) {
      this._connListener = (WeakReference)(new Object(listener));
   }

   public final void setDatagramStatusListener(DatagramStatusListener listener) {
      this._listener = listener;
   }

   public final void handleDatagramStatus(int datagramId, int event, Object context) {
      if (this._listener != null) {
         try {
            this._listener.updateDatagramStatus(datagramId, event, context);
         } finally {
            return;
         }
      }
   }

   final String getAddress(boolean send) {
      if (send) {
         return this._addressBase != null ? this._addressBase.getAddress() : null;
      } else {
         return this._receiveFilter != null ? this._receiveFilter.getAddress() : null;
      }
   }

   protected final boolean isAddressed(DatagramAddressBase addressBase) {
      return addressBase != null && this._receiveFilter != null ? this._receiveFilter.equals(addressBase) : false;
   }

   final Datagram newDatagram(int length, boolean send) {
      if (this.getConnectionState() == 2) {
         return new SrpDatagramInternal(null, 0, length, send ? this._addressBase : this._receiveFilter);
      } else {
         throw new Object();
      }
   }

   public final void setTrafficLogger(TrafficLogger logger) {
   }

   public final void datagramProcessed(int dgId) {
   }

   public final int allocateDatagramId(Datagram datagram) {
      return 0;
   }

   public final byte[] setup(int callType, Object context) {
      return null;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final Connection reopen(String name, int mode, boolean timeouts, String[] params) {
      URL url = null;
      int linkType = -1;
      int connectionType = -1;
      int lPort = -1;
      String connectionScheme = "socket";

      try {
         url = (URL)(new Object("socket", name));
         URLParameters urlParams = url.getRIMParameters();
         if (urlParams == null) {
            urlParams = (URLParameters)(new Object());
         }

         urlParams.remove("deviceside");
         urlParams.setParameter("deviceside", "true");
         url.setRIMParameters(urlParams);
         if (urlParams.containParameter("connectiontype")) {
            if (StringUtilities.strEqualIgnoreCase("router", urlParams.getValue("connectiontype"), 1701707776)) {
               connectionType = 0;
            } else if (StringUtilities.strEqualIgnoreCase("relay", urlParams.getValue("connectiontype"), 1701707776)) {
               connectionType = 1;
            }
         }

         urlParams.remove("connectiontype");
         if (!urlParams.containParameter("retrynocontext")) {
            urlParams.setParameter("retrynocontext", "true");
         }

         if (urlParams.containParameter("interface")) {
            if (StringUtilities.strEqualIgnoreCase("wifi", urlParams.getValue("interface"), 1701707776)) {
               linkType = 0;
            } else if (StringUtilities.strEqualIgnoreCase("cellular", urlParams.getValue("interface"), 1701707776)) {
               linkType = 1;
            }
         }

         if (urlParams.containParameter("connectionhandler")) {
            if (StringUtilities.strEqualIgnoreCase("ssl", urlParams.getValue("connectionhandler"), 1701707776)) {
               connectionScheme = "ssl";
               if (params != null && params.length > 0) {
                  urlParams.setParameter("ConnectionSetup", "delayed");
               }
            } else if (StringUtilities.strEqualIgnoreCase("socket", urlParams.getValue("connectionhandler"), 1701707776)) {
               connectionScheme = "socket";
            }
         }

         urlParams.remove("connectionhandler");
         if (urlParams.containParameter(";localport=")) {
            String value = urlParams.getValue(";localport=");
            if (value != null) {
               label159:
               try {
                  int ret = DatagramAddressBase.parseInt(value, 0, value.length(), 10);
                  if (ret >= 0 && ret <= 65535) {
                     lPort = ret;
                  }
               } finally {
                  break label159;
               }
            }
         }

         if (urlParams.containParameter("ConnectionSetup")) {
            if (!StringUtilities.strEqualIgnoreCase("delayed", urlParams.getValue("ConnectionSetup"), 1701707776)) {
               params = null;
            }
         } else {
            params = null;
         }

         url.setScheme(connectionScheme);
      } catch (Throwable var18) {
         EventLogger.logEvent(5159979649545707334L, 1380541810, 2);
         throw new Object(e.getMessage());
      }

      if (linkType != -1 && connectionType != -1) {
         name = SrpUtils.createSrpAddress(url.getHost(), url.getPort(), lPort, null);
         this._addressBase = new SrpAddress(name, linkType, connectionType);
         this._receiveFilter = new SrpAddress(name, linkType, connectionType);
         this._receiveFilter.swap();
         this._socketAddress = url.toString();
         this._params = params;
         this._mode = mode;
         this._timeouts = timeouts;
         this.initializeConnection(params);
         return this;
      } else {
         throw new Object("Unspecified connection params");
      }
   }

   @Override
   public final int getNominalLength() {
      return 0;
   }

   @Override
   public final int getMaximumLength() {
      return 0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void send(Datagram dgram) {
      int event = 128;
      boolean var7 = false /* VF: Semaphore variable */;

      label129: {
         try {
            try {
               var7 = true;
               if (this.getConnectionState() == 2) {
                  if (dgram == null) {
                     throw new Object();
                  }

                  this._out.write(dgram.getData());
                  if (dgram instanceof SrpDatagramInternal && ((SrpDatagramInternal)dgram).getPayload() != null) {
                     this._out
                        .write(
                           ((SrpDatagramInternal)dgram).getPayload(),
                           ((SrpDatagramInternal)dgram).getPayloadOffset(),
                           ((SrpDatagramInternal)dgram).getPayloadLength()
                        );
                  }

                  this._out.flush();
                  event = 2;
                  EventLogger.logEvent(5159979649545707334L, 1415082867, 4);
                  var7 = false;
               } else {
                  event = 13185;
                  var7 = false;
               }
               break label129;
            } catch (Throwable var10) {
               label124: {
                  EventLogger.logEvent(5159979649545707334L, 1415082850, 2);
                  if (e instanceof Object) {
                     event = 13185;
                  }

                  if (this.getConnectionState() == 2) {
                     this.subClose(3);
                     var7 = false;
                  } else {
                     var7 = false;
                  }
                  break label124;
               }
            }
         } finally {
            if (var7) {
               if (dgram instanceof Object) {
                  this.handleDatagramStatus(((DatagramBase)dgram).getDatagramId(), event, null);
               }
            }
         }

         if (dgram instanceof Object) {
            this.handleDatagramStatus(((DatagramBase)dgram).getDatagramId(), event, null);
            return;
         }

         return;
      }

      if (dgram instanceof Object) {
         this.handleDatagramStatus(((DatagramBase)dgram).getDatagramId(), event, null);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void receive(Datagram dgram) {
      int logEvent = 4;
      IOException ioe = null;
      boolean closeInvoked = false;
      boolean var16 = false /* VF: Semaphore variable */;

      label396: {
         try {
            try {
               var16 = true;
               if (this.getConnectionState() == 0) {
                  EventLogger.logEvent(5159979649545707334L, 1381528183, 0);
                  synchronized (this) {
                     label346:
                     try {
                        this.wait();
                     } finally {
                        break label346;
                     }

                     if (this._socketAddress != null) {
                        this.initializeConnection(this._params);
                     }

                     if (this.getConnectionState() == 0) {
                        EventLogger.logEvent(5159979649545707334L, 1381528183, 2);
                        ioe = (IOException)(new Object());
                        throw ioe;
                     }
                  }
               }

               byte[] header = new byte[6];
               int len = 6;
               int i = 0;

               label357:
               while (len > 0) {
                  int numRead = this._in.read(header, i, len);
                  if (numRead < 0) {
                     logEvent = 3;
                     switch (this.getConnectionState()) {
                        case 0:
                           ioe = new ConnectionClosedException();
                           throw ioe;
                        case 2:
                           this.subClose(3);
                           closeInvoked = true;
                           ioe = (IOException)(new Object());
                           throw ioe;
                        default:
                           break label357;
                     }
                  }

                  i += numRead;
                  len -= numRead;
               }

               if (this.getConnectionState() != 2) {
                  var16 = false;
               } else {
                  label390: {
                     len = DatagramAddressBase.readInt(header, 2);
                     if (len >= 0 && len <= SrpUtils.getMaximumLength() && SrpUtils.verifySrpHeader(header)) {
                        boolean srpInternalDatagram = dgram instanceof SrpDatagramInternal;
                        byte[] data = null;
                        if (srpInternalDatagram) {
                           data = new byte[len];
                           i = 0;
                        } else {
                           data = new byte[6 + len];
                           System.arraycopy(header, 0, data, 0, 6);
                           i = 6;
                        }

                        while (true) {
                           if (len > 0) {
                              int numRead = this._in.read(data, i, len);
                              if (numRead >= 0) {
                                 i += numRead;
                                 len -= numRead;
                                 continue;
                              }

                              logEvent = 3;
                              switch (this.getConnectionState()) {
                                 case 0:
                                    ioe = new ConnectionClosedException();
                                    throw ioe;
                                 case 2:
                                    this.subClose(3);
                                    closeInvoked = true;
                                    ioe = (IOException)(new Object());
                                    throw ioe;
                              }
                           }

                           if (this.getConnectionState() == 2) {
                              if (srpInternalDatagram) {
                                 ((SrpDatagramInternal)dgram).setPayload(data, 0, data.length);
                                 data = header;
                              }

                              dgram.setData(data, 0, data.length);
                              if (!(dgram instanceof Object)) {
                                 dgram.setAddress(this._receiveFilter.getAddress());
                              } else {
                                 ((DatagramBase)dgram).setAddressBase(this._receiveFilter);
                              }
                           }

                           data = null;
                           Object var29 = null;
                           var16 = false;
                           break label390;
                        }
                     }

                     throw new SrpBridgeConnection$SrpFormatException(null);
                  }
               }
            } catch (Throwable var27) {
               logEvent = 2;
               int eventCode = 0;
               dgram.reset();
               if (e instanceof SrpBridgeConnection$SrpFormatException) {
                  eventCode = 1213359474;
                  ioe = (IOException)e;
               } else if (e instanceof ConnectionClosedException) {
                  eventCode = 1381524850;
                  logEvent = 3;
               } else {
                  if (e instanceof TLSIOException) {
                     eventCode = 1397967973;
                  } else if (!(e instanceof Object)) {
                     eventCode = 1431201138;
                  } else {
                     eventCode = 1381524850;
                  }

                  if (!closeInvoked && this.getConnectionState() == 2) {
                     this.subClose(3);
                  }
               }

               EventLogger.logEvent(5159979649545707334L, eventCode, logEvent);
               ioe = (IOException)(new Object());
               var16 = false;
               break label396;
            }
         } finally {
            if (var16) {
               EventLogger.logEvent(5159979649545707334L, 1381528434, logEvent);
               if (ioe != null) {
                  throw ioe;
               }
            }
         }

         EventLogger.logEvent(5159979649545707334L, 1381528434, logEvent);
         if (ioe != null) {
            throw ioe;
         }

         return;
      }

      EventLogger.logEvent(5159979649545707334L, 1381528434, logEvent);
      if (ioe != null) {
         throw ioe;
      }
   }

   @Override
   public final Datagram newDatagram(byte[] buf, int size, String addr) {
      return null;
   }

   @Override
   public final Datagram newDatagram(byte[] buf, int size) {
      return null;
   }

   @Override
   public final Datagram newDatagram(int size, String addr) {
      return null;
   }

   @Override
   public final Datagram newDatagram(int length) {
      return this.newDatagram(length, false);
   }

   @Override
   public final void connectionClosed(ConnectionCloseProvider connection) {
   }

   private final synchronized void setConnectionState(int state) {
      switch (state) {
         case -1:
            throw new Object();
         case 0:
         case 1:
         case 2:
         default:
            this._open = state;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void initializeConnection(String[] params) {
      if (this.getConnectionState() == 0) {
         this._socket = null;
         int event = 0;
         boolean var13 = false /* VF: Semaphore variable */;

         try {
            try {
               var13 = true;
               this.setConnectionState(1);
               EventLogger.logEvent(5159979649545707334L, 1229874030, 0);
               this._socket = (SocketConnection)Connector.open(this._socketAddress, this._mode, this._timeouts);
               if (this._socket == null) {
                  var13 = false;
               } else {
                  if (this._socket instanceof ConnectionCloseProvider) {
                     ((ConnectionCloseProvider)this._socket).setConnectionCloseListener(this);
                  }

                  if (params == null || params.length <= 0) {
                     params = null;
                  } else if (this._socket instanceof SSLConnection) {
                     SSLConnectionOptions connectionOptions = new SSLConnectionOptions();
                     connectionOptions.setAcceptableDomainNames(params);
                     SSLConnection sslConnection = (SSLConnection)this._socket;
                     sslConnection.setOverrideConnectionOptions(connectionOptions);
                  } else {
                     params = null;
                  }

                  this._out = this._socket.openOutputStream();
                  this._in = this._socket.openInputStream();
                  if (params != null && this._out != null) {
                     this._out.flush();
                  }

                  event = 1;

                  label232:
                  try {
                     if (!this._receiveFilter.resolved() || this._socket.getLocalPort() != this._receiveFilter.getDestPort()) {
                        this._receiveFilter
                           .setAddress(SrpUtils.createSrpAddress(this._socket.getAddress(), this._socket.getLocalPort(), this._socket.getPort(), null));
                     }

                     if (!this._addressBase.resolved() || this._socket.getLocalPort() != this._addressBase.getLocalPort()) {
                        this._addressBase
                           .setAddress(SrpUtils.createSrpAddress(this._socket.getAddress(), this._socket.getPort(), this._socket.getLocalPort(), null));
                     }
                  } finally {
                     break label232;
                  }

                  if (this.getConnectionState() != 1) {
                     throw new SrpConnectionOpenException();
                  }

                  this.setConnectionState(2);
                  synchronized (this) {
                     this.notifyAll();
                     var13 = false;
                  }
               }
            } catch (Throwable var24) {
               this.setConnectionState(0);
               EventLogger.logEvent(5159979649545707334L, 1229874030, 2);
               event = 4;
               throw new SrpConnectionOpenException(e.getMessage());
            }
         } finally {
            if (var13) {
               WeakReference connListener = this.getSrpConnectionStatusListener();
               if (connListener != null) {
                  SrpConnectionStatusListener listener = (SrpConnectionStatusListener)connListener.get();
                  if (listener != null) {
                     listener.connectionStatusChanged(this, event);
                  }
               }
            }
         }

         WeakReference connListener = this.getSrpConnectionStatusListener();
         if (connListener != null) {
            SrpConnectionStatusListener listener = (SrpConnectionStatusListener)connListener.get();
            if (listener != null) {
               listener.connectionStatusChanged(this, event);
               return;
            }
         }
      }
   }

   private final void subClose(int reason) {
      WeakReference connListener = this.getSrpConnectionStatusListener();
      this.close();
      if (connListener != null) {
         SrpConnectionStatusListener listener = (SrpConnectionStatusListener)connListener.get();
         if (listener != null) {
            listener.connectionStatusChanged(this, reason);
         }
      }
   }

   private final synchronized WeakReference getSrpConnectionStatusListener() {
      return this._connListener;
   }
}

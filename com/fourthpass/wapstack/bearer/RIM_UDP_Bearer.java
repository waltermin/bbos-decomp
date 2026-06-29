package com.fourthpass.wapstack.bearer;

import com.fourthpass.wapstack.util.InetAddress;
import com.fourthpass.wapstack.wdp.WDPPacket;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.cldc.io.utility.PacketLogger;
import net.rim.device.cldc.io.utility.SessionStats;
import net.rim.device.internal.proxy.Proxy;

public final class RIM_UDP_Bearer extends Thread implements IBearer {
   private boolean _closed;
   private DatagramConnection _datagramConnection;
   private InetAddress _gatewayDest;
   private int _gwPortDest;
   private SessionStats _stats;
   private boolean _needSource;
   private PacketLogger _packetLoggerInstance = PacketLogger.getInstance();
   private Vector _sendQueue = new Vector();
   private boolean _shutdown;
   private boolean _started;
   private long _lastPacketTime;
   private static final int MAX_SEND_QUEUE_SIZE = 7;

   public final long getLastPacketTime() {
      return this._lastPacketTime;
   }

   public final int sendInternal(WDPPacket packet) {
      int sentCount = -1;

      try {
         Datagram datagramSendPacket = this._datagramConnection.newDatagram(packet.getPacketData(), packet.getDataLength());
         if (this._packetLoggerInstance._lowLoggingEnabled) {
            this._packetLoggerInstance
               .logPacket(packet.getPacketData(), 0, packet.getDataLength(), this._gatewayDest.getDisplayableString() + ':' + this._gwPortDest, true);
         }

         this._datagramConnection.send(datagramSendPacket);
         sentCount = datagramSendPacket.getLength();
         if (this._stats != null) {
            this._stats.addToSent(sentCount);
         }

         this._lastPacketTime = System.currentTimeMillis();
         return sentCount;
      } finally {
         return 0;
      }
   }

   public final byte getBearerType() {
      return 0;
   }

   @Override
   public final void closeConnection() {
      synchronized (this._sendQueue) {
         this._shutdown = true;
         this._sendQueue.notify();
      }

      label33:
      try {
         if (this._datagramConnection != null) {
            this._datagramConnection.close();
         }
      } finally {
         break label33;
      }

      this._closed = true;
   }

   @Override
   public final void setReceivingTimeout(int timeout) {
      if (this._datagramConnection != null && this._datagramConnection instanceof DatagramConnectionBase) {
         ((DatagramConnectionBase)this._datagramConnection).setTimeout(timeout);
      }
   }

   @Override
   public final int getTransmissionTimeout() {
      return 0;
   }

   @Override
   public final void setTransmissionTimeout(int timeout) {
   }

   @Override
   public final boolean isClosed() {
      return this._closed;
   }

   @Override
   public final int receive(WDPPacket param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush -1
      // 002: istore 2
      // 003: aload 0
      // 004: getfield com/fourthpass/wapstack/bearer/RIM_UDP_Bearer._closed Z
      // 007: ifeq 00d
      // 00a: bipush -1
      // 00c: ireturn
      // 00d: aload 0
      // 00e: getfield com/fourthpass/wapstack/bearer/RIM_UDP_Bearer._datagramConnection Ljavax/microedition/io/DatagramConnection;
      // 011: bipush 0
      // 012: invokeinterface javax/microedition/io/DatagramConnection.newDatagram (I)Ljavax/microedition/io/Datagram; 2
      // 017: astore 3
      // 018: aload 0
      // 019: getfield com/fourthpass/wapstack/bearer/RIM_UDP_Bearer._datagramConnection Ljavax/microedition/io/DatagramConnection;
      // 01c: aload 3
      // 01d: invokeinterface javax/microedition/io/DatagramConnection.receive (Ljavax/microedition/io/Datagram;)V 2
      // 022: aload 0
      // 023: getfield com/fourthpass/wapstack/bearer/RIM_UDP_Bearer._packetLoggerInstance Lnet/rim/device/cldc/io/utility/PacketLogger;
      // 026: getfield net/rim/device/cldc/io/utility/PacketLogger._lowLoggingEnabled Z
      // 029: ifeq 073
      // 02c: aload 0
      // 02d: getfield com/fourthpass/wapstack/bearer/RIM_UDP_Bearer._packetLoggerInstance Lnet/rim/device/cldc/io/utility/PacketLogger;
      // 030: aload 3
      // 031: invokeinterface javax/microedition/io/Datagram.getData ()[B 1
      // 036: aload 3
      // 037: invokeinterface javax/microedition/io/Datagram.getOffset ()I 1
      // 03c: aload 3
      // 03d: invokeinterface javax/microedition/io/Datagram.getLength ()I 1
      // 042: aload 0
      // 043: getfield com/fourthpass/wapstack/bearer/RIM_UDP_Bearer._gatewayDest Lcom/fourthpass/wapstack/util/InetAddress;
      // 046: ifnonnull 04f
      // 049: ldc_w ""
      // 04c: goto 06f
      // 04f: new java/lang/StringBuffer
      // 052: dup
      // 053: invokespecial java/lang/StringBuffer.<init> ()V
      // 056: aload 0
      // 057: getfield com/fourthpass/wapstack/bearer/RIM_UDP_Bearer._gatewayDest Lcom/fourthpass/wapstack/util/InetAddress;
      // 05a: invokevirtual com/fourthpass/wapstack/util/InetAddress.getDisplayableString ()Ljava/lang/String;
      // 05d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 060: bipush 58
      // 062: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 065: aload 0
      // 066: getfield com/fourthpass/wapstack/bearer/RIM_UDP_Bearer._gwPortDest I
      // 069: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 06c: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 06f: bipush 0
      // 070: invokevirtual net/rim/device/cldc/io/utility/PacketLogger.logPacket ([BIILjava/lang/String;Z)V
      // 073: aload 0
      // 074: getfield com/fourthpass/wapstack/bearer/RIM_UDP_Bearer._needSource Z
      // 077: ifeq 10c
      // 07a: aload 3
      // 07b: dup
      // 07c: instanceof net/rim/device/api/io/DatagramBase
      // 07f: ifne 086
      // 082: pop
      // 083: goto 10c
      // 086: checkcast net/rim/device/api/io/DatagramBase
      // 089: invokevirtual net/rim/device/api/io/DatagramBase.getAddressBase ()Lnet/rim/device/api/io/DatagramAddressBase;
      // 08c: astore 4
      // 08e: aload 4
      // 090: dup
      // 091: instanceof net/rim/device/api/io/UdpAddress
      // 094: ifne 09b
      // 097: pop
      // 098: goto 10c
      // 09b: checkcast net/rim/device/api/io/UdpAddress
      // 09e: invokevirtual net/rim/device/api/io/UdpAddress.getIpAddress ()[B
      // 0a1: astore 5
      // 0a3: aload 5
      // 0a5: ifnull 10c
      // 0a8: new java/lang/StringBuffer
      // 0ab: dup
      // 0ac: invokespecial java/lang/StringBuffer.<init> ()V
      // 0af: astore 6
      // 0b1: aload 6
      // 0b3: aload 5
      // 0b5: bipush 0
      // 0b6: baload
      // 0b7: sipush 255
      // 0ba: iand
      // 0bb: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 0be: pop
      // 0bf: aload 6
      // 0c1: bipush 46
      // 0c3: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 0c6: pop
      // 0c7: aload 6
      // 0c9: aload 5
      // 0cb: bipush 1
      // 0cc: baload
      // 0cd: sipush 255
      // 0d0: iand
      // 0d1: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 0d4: pop
      // 0d5: aload 6
      // 0d7: bipush 46
      // 0d9: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 0dc: pop
      // 0dd: aload 6
      // 0df: aload 5
      // 0e1: bipush 2
      // 0e3: baload
      // 0e4: sipush 255
      // 0e7: iand
      // 0e8: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 0eb: pop
      // 0ec: aload 6
      // 0ee: bipush 46
      // 0f0: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 0f3: pop
      // 0f4: aload 6
      // 0f6: aload 5
      // 0f8: bipush 3
      // 0fa: baload
      // 0fb: sipush 255
      // 0fe: iand
      // 0ff: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 102: pop
      // 103: aload 1
      // 104: aload 6
      // 106: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 109: invokevirtual com/fourthpass/wapstack/wdp/WDPPacket.setSource (Ljava/lang/String;)V
      // 10c: aload 1
      // 10d: aload 3
      // 10e: invokeinterface javax/microedition/io/Datagram.getData ()[B 1
      // 113: aload 3
      // 114: invokeinterface javax/microedition/io/Datagram.getOffset ()I 1
      // 119: aload 3
      // 11a: invokeinterface javax/microedition/io/Datagram.getLength ()I 1
      // 11f: invokevirtual com/fourthpass/wapstack/wdp/WDPPacket.setPacketData ([BII)V
      // 122: aload 3
      // 123: invokeinterface javax/microedition/io/Datagram.getLength ()I 1
      // 128: istore 2
      // 129: aload 0
      // 12a: getfield com/fourthpass/wapstack/bearer/RIM_UDP_Bearer._stats Lnet/rim/device/cldc/io/utility/SessionStats;
      // 12d: ifnull 138
      // 130: aload 0
      // 131: getfield com/fourthpass/wapstack/bearer/RIM_UDP_Bearer._stats Lnet/rim/device/cldc/io/utility/SessionStats;
      // 134: iload 2
      // 135: invokevirtual net/rim/device/cldc/io/utility/SessionStats.addToReceived (I)V
      // 138: aload 0
      // 139: invokestatic java/lang/System.currentTimeMillis ()J
      // 13c: putfield com/fourthpass/wapstack/bearer/RIM_UDP_Bearer._lastPacketTime J
      // 13f: iload 2
      // 140: ireturn
      // 141: astore 3
      // 142: iload 2
      // 143: ireturn
      // 144: astore 3
      // 145: bipush 0
      // 146: istore 2
      // 147: iload 2
      // 148: ireturn
      // try (7 -> 144): 146 null
      // try (7 -> 144): 149 null
   }

   @Override
   public final int send(WDPPacket packet) {
      synchronized (this._sendQueue) {
         if (!this._started) {
            this._started = true;
            Proxy.getInstance().startThread(this);
         }

         if (this._sendQueue.size() >= 7) {
            this._sendQueue.removeElementAt(0);
         }

         this._sendQueue.addElement(packet);
         this._sendQueue.notify();
      }

      return packet.getDataLength();
   }

   @Override
   public final SessionStats getSessionStats() {
      return this._stats;
   }

   @Override
   public final void run() {
      WDPPacket packet = null;

      while (true) {
         synchronized (this._sendQueue) {
            while (true) {
               if (this._sendQueue.size() == 0 && !this._shutdown) {
                  try {
                     this._sendQueue.wait();
                  } finally {
                     continue;
                  }
               } else {
                  if (this._shutdown) {
                     return;
                  }

                  packet = (WDPPacket)this._sendQueue.elementAt(0);
                  this._sendQueue.removeElementAt(0);
                  break;
               }
            }
         }

         this.sendInternal(packet);
         packet = null;
      }
   }

   private final String schemaFor(int localPort) {
      StringBuffer tempBuffer = new StringBuffer("udp://");
      tempBuffer.append(':').append(localPort);
      tempBuffer.append(";openTunnel=false");

      try {
         ControlledAccess.assertRRISignatures(true);
         tempBuffer.append(";retrynocontext=true");
      } finally {
         return tempBuffer.toString();
      }

      return tempBuffer.toString();
   }

   private final String schemaFor(InetAddress netAdd, int port, int sourcePort, String apn) {
      StringBuffer tempBuffer = new StringBuffer("udp://");
      tempBuffer.append(netAdd.toString()).append(':').append(port).append(';').append(sourcePort).append('/').append(apn);

      try {
         ControlledAccess.assertRRISignatures(true);
         tempBuffer.append(";retrynocontext=true");
      } finally {
         return tempBuffer.toString();
      }

      return tempBuffer.toString();
   }

   public RIM_UDP_Bearer(InetAddress gatewayDest, int gwPort, String gatewayAPN, int sourcePort) {
      this._gatewayDest = gatewayDest;
      this._gwPortDest = gwPort;
      this._stats = new SessionStats();
      this._stats.setConnectedHost(gatewayDest.toString(), gwPort);
      this._datagramConnection = (DatagramConnection)Connector.open(this.schemaFor(gatewayDest, gwPort, sourcePort, gatewayAPN), 3, false);
   }

   public RIM_UDP_Bearer(int localPort) {
      this._needSource = true;
      this._datagramConnection = (DatagramConnection)Connector.open(this.schemaFor(localPort), 3, false);
   }
}

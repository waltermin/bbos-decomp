package com.fourthpass.wapstack.bearer;

import com.fourthpass.wapstack.wdp.WDPPacket;
import javax.microedition.io.Connector;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.MessageConnection;
import net.rim.device.api.io.SmsAddress;
import net.rim.device.cldc.io.utility.PacketLogger;
import net.rim.device.cldc.io.utility.SessionStats;

public final class RIM_SMS_Bearer implements IBearer {
   private boolean _closed;
   private MessageConnection _messagingConnection;
   private String _destAddress;
   private int _sourcePort;
   private PacketLogger _packetLoggerInstance = PacketLogger.getInstance();

   public RIM_SMS_Bearer(int sourcePort, int receivePort) {
      int[] ports = new int[]{receivePort};
      this._destAddress = SmsAddress.makeAddress(true, null, ports);
      this._sourcePort = sourcePort;
      this._closed = false;
      this._messagingConnection = (MessageConnection)Connector.open(this._destAddress, 3, false);
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
      // 00: bipush -1
      // 02: istore 2
      // 03: aload 0
      // 04: getfield com/fourthpass/wapstack/bearer/RIM_SMS_Bearer._closed Z
      // 07: ifeq 0d
      // 0a: bipush -1
      // 0c: ireturn
      // 0d: aload 0
      // 0e: getfield com/fourthpass/wapstack/bearer/RIM_SMS_Bearer._messagingConnection Ljavax/wireless/messaging/MessageConnection;
      // 11: invokeinterface javax/wireless/messaging/MessageConnection.receive ()Ljavax/wireless/messaging/Message; 1
      // 16: astore 3
      // 17: aload 3
      // 18: dup
      // 19: instanceof net/rim/device/cldc/io/sms/MessageImpl
      // 1c: ifne 23
      // 1f: pop
      // 20: goto 97
      // 23: checkcast net/rim/device/cldc/io/sms/MessageImpl
      // 26: astore 4
      // 28: aload 4
      // 2a: invokevirtual net/rim/device/cldc/io/sms/MessageImpl.getBuffer ()[B
      // 2d: astore 5
      // 2f: aload 3
      // 30: instanceof javax/wireless/messaging/TextMessage
      // 33: ifeq 41
      // 36: aload 5
      // 38: bipush 0
      // 39: aload 5
      // 3b: arraylength
      // 3c: invokestatic net/rim/device/cldc/io/sms/SmsUtil.decodeWDPData ([BII)[B
      // 3f: astore 5
      // 41: aload 4
      // 43: invokevirtual net/rim/device/cldc/io/sms/MessageImpl.getSCAddress ()Ljava/lang/String;
      // 46: astore 6
      // 48: aload 0
      // 49: getfield com/fourthpass/wapstack/bearer/RIM_SMS_Bearer._packetLoggerInstance Lnet/rim/device/cldc/io/utility/PacketLogger;
      // 4c: getfield net/rim/device/cldc/io/utility/PacketLogger._lowLoggingEnabled Z
      // 4f: ifeq 7d
      // 52: aload 0
      // 53: getfield com/fourthpass/wapstack/bearer/RIM_SMS_Bearer._packetLoggerInstance Lnet/rim/device/cldc/io/utility/PacketLogger;
      // 56: aload 5
      // 58: bipush 0
      // 59: aload 5
      // 5b: arraylength
      // 5c: new java/lang/StringBuffer
      // 5f: dup
      // 60: invokespecial java/lang/StringBuffer.<init> ()V
      // 63: aload 0
      // 64: getfield com/fourthpass/wapstack/bearer/RIM_SMS_Bearer._destAddress Ljava/lang/String;
      // 67: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 6a: bipush 58
      // 6c: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 6f: aload 0
      // 70: getfield com/fourthpass/wapstack/bearer/RIM_SMS_Bearer._sourcePort I
      // 73: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 76: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 79: bipush 0
      // 7a: invokevirtual net/rim/device/cldc/io/utility/PacketLogger.logPacket ([BIILjava/lang/String;Z)V
      // 7d: aload 1
      // 7e: aload 6
      // 80: invokevirtual com/fourthpass/wapstack/wdp/WDPPacket.setSource (Ljava/lang/String;)V
      // 83: aload 1
      // 84: aload 5
      // 86: bipush 0
      // 87: aload 5
      // 89: arraylength
      // 8a: invokevirtual com/fourthpass/wapstack/wdp/WDPPacket.setPacketData ([BII)V
      // 8d: aload 5
      // 8f: arraylength
      // 90: ireturn
      // 91: astore 3
      // 92: iload 2
      // 93: ireturn
      // 94: astore 3
      // 95: bipush 0
      // 96: istore 2
      // 97: iload 2
      // 98: ireturn
      // try (7 -> 69): 70 null
      // try (7 -> 69): 73 null
   }

   @Override
   public final void closeConnection() {
      label20:
      try {
         if (this._messagingConnection != null) {
            this._messagingConnection.close();
         }
      } finally {
         break label20;
      }

      this._closed = true;
   }

   @Override
   public final int getTransmissionTimeout() {
      return 0;
   }

   @Override
   public final void setTransmissionTimeout(int timeout) {
   }

   @Override
   public final void setReceivingTimeout(int timeout) {
   }

   @Override
   public final int send(WDPPacket packet) {
      int sentCount = -1;

      try {
         int len = packet.getDataLength();
         byte[] data = packet.getPacketData();
         BinaryMessage bMessage = (BinaryMessage)this._messagingConnection.newMessage("binary");
         if (this._packetLoggerInstance._lowLoggingEnabled) {
            this._packetLoggerInstance.logPacket(data, 0, len, this._destAddress + ':' + this._sourcePort, true);
         }

         bMessage.setPayloadData(data);
         this._messagingConnection.send(bMessage);
         return len;
      } finally {
         return 0;
      }
   }

   @Override
   public final boolean isClosed() {
      return this._closed;
   }

   @Override
   public final SessionStats getSessionStats() {
      return null;
   }
}

package com.fourthpass.wapstack.wtls;

import com.fourthpass.wapstack.IPacketTransiver;
import com.fourthpass.wapstack.IWapStackLayer;
import com.fourthpass.wapstack.util.UserDefinedEvent;
import com.fourthpass.wapstack.wdp.WDPPacket;
import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.SecurityInfo;
import net.rim.device.api.crypto.tls.wtls20.WTLSDataTransport;
import net.rim.device.api.crypto.tls.wtls20.WTLSLayerConnection;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.cldc.io.ssl.TLSException;

public class RIMWTLSLayer implements IWapStackLayer, IPacketTransiver, WTLSDataTransport {
   private IWapStackLayer _submissionLayer;
   private IPacketTransiver _transiver;
   private IWapStackLayer _userLayer;
   private WDPPacket _sendPacket;
   private WDPPacket _receivePacket;
   private WTLSLayerConnection _protocol;
   private static final int MAX_BUFFER_SIZE = 1500;

   public void setSubmissionLayer(IWapStackLayer submissionLayer) {
      this._submissionLayer = submissionLayer;
   }

   public IWapStackLayer getSubmissionLayer() {
      return this._submissionLayer;
   }

   public IWapStackLayer getUserLayer() {
      return this._userLayer;
   }

   public SecurityInfo getRIMSecurityInfo() {
      return this._protocol != null ? this._protocol.getRIMSecurityInfo() : null;
   }

   @Override
   public void eventOccured(Object event) {
      if (event instanceof UserDefinedEvent && this._userLayer != null) {
         this._userLayer.eventOccured(event);
      }
   }

   @Override
   public void close() {
      this._userLayer = null;
      this._submissionLayer.close();
   }

   @Override
   public int send(WDPPacket param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield com/fourthpass/wapstack/wtls/RIMWTLSLayer._protocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSLayerConnection;
      // 04: aload 1
      // 05: invokevirtual com/fourthpass/wapstack/wdp/WDPPacket.getPacketData ()[B
      // 08: bipush 0
      // 09: aload 1
      // 0a: invokevirtual com/fourthpass/wapstack/wdp/WDPPacket.getDataLength ()I
      // 0d: invokeinterface net/rim/device/api/crypto/tls/wtls20/WTLSLayerConnection.write ([BII)V 4
      // 12: aload 1
      // 13: invokevirtual com/fourthpass/wapstack/wdp/WDPPacket.getDataLength ()I
      // 16: ireturn
      // 17: astore 2
      // 18: aload 0
      // 19: new com/fourthpass/wapstack/util/UserDefinedEvent
      // 1c: dup
      // 1d: bipush 4
      // 1f: aconst_null
      // 20: invokespecial com/fourthpass/wapstack/util/UserDefinedEvent.<init> (SLjava/lang/Object;)V
      // 23: invokevirtual com/fourthpass/wapstack/wtls/RIMWTLSLayer.eventOccured (Ljava/lang/Object;)V
      // 26: bipush 0
      // 27: ireturn
      // 28: astore 2
      // 29: bipush 0
      // 2a: ireturn
      // try (0 -> 10): 11 null
      // try (0 -> 10): 21 null
   }

   @Override
   public int receive(WDPPacket param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: sipush 1500
      // 03: newarray 8
      // 05: astore 2
      // 06: bipush 0
      // 07: istore 3
      // 08: aload 0
      // 09: getfield com/fourthpass/wapstack/wtls/RIMWTLSLayer._protocol Lnet/rim/device/api/crypto/tls/wtls20/WTLSLayerConnection;
      // 0c: aload 2
      // 0d: sipush 1500
      // 10: invokeinterface net/rim/device/api/crypto/tls/wtls20/WTLSLayerConnection.read ([BI)I 3
      // 15: istore 3
      // 16: aload 1
      // 17: aload 2
      // 18: iload 3
      // 19: invokevirtual com/fourthpass/wapstack/wdp/WDPPacket.setPacketData ([BI)V
      // 1c: iload 3
      // 1d: ireturn
      // 1e: astore 4
      // 20: aload 0
      // 21: new com/fourthpass/wapstack/util/UserDefinedEvent
      // 24: dup
      // 25: bipush 4
      // 27: aconst_null
      // 28: invokespecial com/fourthpass/wapstack/util/UserDefinedEvent.<init> (SLjava/lang/Object;)V
      // 2b: invokevirtual com/fourthpass/wapstack/wtls/RIMWTLSLayer.eventOccured (Ljava/lang/Object;)V
      // 2e: bipush 0
      // 2f: ireturn
      // 30: astore 4
      // 32: bipush 0
      // 33: ireturn
      // try (5 -> 15): 17 null
      // try (5 -> 15): 27 null
   }

   @Override
   public void setReceivingTimeout(int milliseconds) {
      this._transiver.setReceivingTimeout(milliseconds);
   }

   @Override
   public boolean isSecure() {
      return true;
   }

   @Override
   public byte[] read(int timeout) throws IOException, InterruptedIOException {
      this._transiver.setReceivingTimeout(timeout);
      int recvCount = this._transiver.receive(this._receivePacket);
      if (recvCount == -1 && timeout != 0) {
         throw new InterruptedIOException("Timeout occurred.");
      }

      if (recvCount == -1) {
         if (this._transiver.isClosed()) {
            throw new IOException("connection closed");
         } else {
            return new byte[0];
         }
      } else {
         byte[] result = new byte[recvCount];
         System.arraycopy(this._receivePacket.getPacketData(), 0, result, 0, recvCount);
         return result;
      }
   }

   @Override
   public void write(byte[] bytes, int offset, int count) {
      this._sendPacket.setPacketData(bytes, count);
      this._transiver.send(this._sendPacket);
   }

   @Override
   public void status(int stat) {
      RIMGlobalMessagePoster.postGlobalEvent(-1602902615298266273L, stat, 0);
   }

   @Override
   public void setUserLayer(IWapStackLayer userLayer) {
      this._userLayer = userLayer;
   }

   @Override
   public boolean isClosed() {
      return this._transiver.isClosed();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public RIMWTLSLayer(
      IWapStackLayer submissionLayer,
      String apn,
      String address,
      boolean openwaveMode,
      int clientIdMode,
      String clientIdValue,
      int ipAddress,
      int ipPort,
      boolean wap20Conformance
   ) throws ConnectionNotFoundException, TLSException {
      this._submissionLayer = submissionLayer;
      this._transiver = (IPacketTransiver)submissionLayer;
      this._sendPacket = new WDPPacket();
      this._receivePacket = new WDPPacket();

      label42:
      try {
         Class wtls20 = Class.forName("net.rim.device.api.crypto.tls.wtls20.WTLS20");
         this._protocol = (WTLSLayerConnection)wtls20.newInstance();
      } finally {
         break label42;
      }

      if (this._protocol == null) {
         throw new ConnectionNotFoundException();
      }

      try {
         this._protocol.makeConnection(this, apn, address, openwaveMode, clientIdMode, clientIdValue, ipAddress, ipPort, wap20Conformance);
      } catch (Throwable var15) {
         throw new TLSException(e);
      }
   }

   public static byte getErrorDescription(TLSException exception) {
      try {
         Class wtls20 = Class.forName("net.rim.device.api.crypto.tls.wtls20.WTLS20");
         WTLSLayerConnection protocol = (WTLSLayerConnection)wtls20.newInstance();
         return protocol.getErrorDescription(exception);
      } finally {
         ;
      }
   }
}

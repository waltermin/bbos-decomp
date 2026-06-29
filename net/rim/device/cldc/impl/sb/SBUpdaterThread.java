package net.rim.device.cldc.impl.sb;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.gme.GMEDatagram;
import net.rim.device.internal.crypto.CryptoBlock;

final class SBUpdaterThread extends Thread {
   private ServiceBook _mySB;
   private DatagramConnection _myConn;
   private DatagramConnection _replyConn;
   private Datagram _dg;
   private ServiceRecord[] _serviceRecordOverride = null;
   private ServiceRecord _defaultServiceRecord = null;
   private static final String SERVICE_BOOK_CID = "SERVICE_BOOK";
   private static final int DUP_SECURE_SERVICE = 1146311525;
   private static final int ERROR_UNABLE_OPEN_REPLY_CONNECTION = 1163220050;
   private static final int RECEIVED_SECURE_SERVICE_BOOK_PACKET = 1381192528;

   public SBUpdaterThread() {
      this._defaultServiceRecord = new ServiceRecord(3);
      this._defaultServiceRecord.setCid("SERVICE_BOOK");
      this._defaultServiceRecord.setType(0);
      this._defaultServiceRecord.setEncryptionMode(1);
      this._defaultServiceRecord.setName("SERVICE_BOOK");
      this._serviceRecordOverride = new ServiceRecord[1];
      this._serviceRecordOverride[0] = new ServiceRecord(3);
      this._mySB = ServiceBook.getSB();
      EventLogger.register(-863050508581563378L, "net.rim.sb", 2);
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial net/rim/device/cldc/impl/sb/SBUpdaterThread.initConnection ()V
      // 04: goto 09
      // 07: astore 1
      // 08: return
      // 09: aconst_null
      // 0a: astore 1
      // 0b: aload 0
      // 0c: invokespecial net/rim/device/cldc/impl/sb/SBUpdaterThread.getNextPacketData ()Lnet/rim/device/api/util/DataBuffer;
      // 0f: astore 1
      // 10: aload 0
      // 11: aload 1
      // 12: invokespecial net/rim/device/cldc/impl/sb/SBUpdaterThread.processNextPacketData (Lnet/rim/device/api/util/DataBuffer;)V
      // 15: goto 09
      // 18: astore 1
      // 19: ldc2_w -863050508581563378
      // 1c: ldc_w 1146311525
      // 1f: bipush 3
      // 21: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 24: pop
      // 25: goto 09
      // 28: astore 1
      // 29: goto 09
      // try (0 -> 2): 3 null
      // try (5 -> 13): 14 null
      // try (5 -> 13): 21 null
   }

   private final void processNextPacketData(DataBuffer packetDataBuffer) {
      int source = 2;
      String uid = null;
      String keyId = null;
      boolean wasEncrypted = false;
      if (packetDataBuffer instanceof GMEDatagram) {
         GMEDatagram gmeDatagram = (GMEDatagram)packetDataBuffer;
         uid = gmeDatagram.getGMEAddress().getSrc().address;
         uid = StringUtilities.toLowerCase(uid, 1701707776);
         if (gmeDatagram != null && gmeDatagram.wasEncrypted()) {
            wasEncrypted = true;
            keyId = gmeDatagram.getKeyId();
            if (gmeDatagram.wasDatagramSecure()) {
               EventLogger.logEvent(-863050508581563378L, 1381192528, 4);
               source = 5;
            }
         }
      }

      byte[] result = this._mySB.processServiceBookCommandData(packetDataBuffer, source, uid, keyId);
      String uidAlias = uid;
      String senderKeyId = null;
      if (result != null && result.length > 0) {
         if (wasEncrypted) {
            senderKeyId = CryptoBlock.getKeyIDForUID(uid);
            if (senderKeyId == null) {
               uidAlias = CryptoBlock.getUIDForKeyId(keyId);
            }
         }

         this.ackResult(result, uid, keyId, uidAlias, source);
      }
   }

   private final void initConnection() {
      this._myConn = (DatagramConnection)Connector.open("gme:SERVICE_BOOK");
   }

   private final void ackResult(byte[] result, String address, String keyId, String uidAlias, int source) {
      if (this._replyConn != null) {
         this._replyConn.close();
      }

      ServiceRecord[] services = this._mySB.findRecordsByUid(address);
      if (services != null && services.length > 0) {
         address = services[0].getUid();
      }

      this._replyConn = (DatagramConnection)Connector.open("gme:SERVICE_BOOK/" + address);
      if (this._replyConn == null) {
         EventLogger.logEvent(-863050508581563378L, 1163220050, 2);
      } else {
         ServiceRecord sr = this._serviceRecordOverride[0];
         if (services != null && services.length > 0) {
            services[0].copyInto(sr);
         } else {
            this._defaultServiceRecord.copyInto(sr);
         }

         sr.setCid("SERVICE_BOOK");
         sr.setType(0);
         sr.setSource(3);
         if (keyId != null) {
            if (CryptoBlock.isEnterpriseClassKey(keyId, null)) {
               sr.setEncryptionMode(2);
            } else {
               sr.setEncryptionMode(4);
            }
         } else {
            sr.setEncryptionMode(1);
         }

         sr.setUid(uidAlias);
         GMEDatagram replyDatagram = (GMEDatagram)this._replyConn.newDatagram(result, result.length);
         replyDatagram.setServiceRecordOverride(this._serviceRecordOverride);
         if (this._replyConn instanceof DatagramConnectionBase) {
            ((DatagramConnectionBase)this._replyConn).allocateDatagramId(replyDatagram);
         }

         EventLogger.logEvent(-863050508581563378L, 1415082868, replyDatagram.getDatagramId(), 10, 0);
         this._replyConn.send(replyDatagram);
      }
   }

   private final DataBuffer getNextPacketData() {
      if (this._dg == null) {
         this._dg = this._myConn.newDatagram(1);
      }

      this._myConn.receive(this._dg);
      EventLogger.logEvent(-863050508581563378L, 1381516132, !(this._dg instanceof GMEDatagram) ? 0 : ((GMEDatagram)this._dg).getTransactionId(), 10, 0);
      return (DataBuffer)this._dg;
   }
}

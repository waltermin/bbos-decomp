package net.rim.device.apps.internal.applicationdelivery;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.AbstractTransmissionService;
import net.rim.device.apps.api.transmission.DatagramConnectionUser;
import net.rim.device.apps.api.transmission.DefaultReceiveThread;
import net.rim.device.apps.api.transmission.Packet;
import net.rim.device.apps.api.transmission.PacketReceiver;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.cldc.io.gme.GMEDatagram;
import net.rim.device.cldc.io.gme.GMETarget;
import net.rim.device.internal.crypto.CryptoBlock;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.DebugUtilities;
import net.rim.device.internal.system.ForcedResetManager;
import net.rim.device.internal.util.ByteArray;
import net.rim.vm.Memory;

class ApplicationDeliveryTransmissionService extends AbstractTransmissionService implements PacketReceiver, RealtimeClockListener {
   private PersistentObject _persistentData;
   private long _lastCleanTime;
   long CLEAN_CHECK_TIME = 14400000;
   ApplicationDeliveryTransmissionService$ApplicationDeliveryPersistedData _applicationDeliveryPersistedData;
   private static final int COMMAND_APPLICATION_DATA = 1;
   private static final int COMMAND_ACKNOWLEDGEMENT = 2;
   static final int ACK_RESERVED = 0;
   static final int ACK_SUCCESS = 1;
   static final int ACK_TIMEOUT = 2;
   static final int ACK_INSUFFICIENT_MEMORY = 3;
   static final int ACK_INSUFFICIENT_PRIVILEGES = 4;
   static final int ACK_INVALID_VERSION = 5;
   static final int ACK_DATA_FORMAT_ERROR = 6;
   static final int ACK_INVALID_COMMAND = 7;
   static final int ACK_INSUFFICIENT_BODY_DATA = 8;
   static final int ACK_INVALID_MODULE_HASH = 9;
   static final int ACK_INVALID_APP_DATA_LENGTH = 10;
   static final int ACK_INSUFFICIENT_APP_DATA = 11;
   static final int ACK_INCOMPLETE_MODULE = 12;
   static final int ACK_MODULE_SAVE_FAILED = 13;
   static final int ACK_SECURITY_VIOLATION = 14;
   static final int ACK_FAILURE = -1;
   private static final byte TAG_TRANSACTION_ID = 1;
   private static final byte TAG_MODULE_COUNT = 2;
   private static final byte TAG_MODULE_ID = 3;
   private static final byte TAG_SEQUENCE_NUMBER = 4;
   private static final byte TAG_MODULE_SIZE = 5;
   private static final byte TAG_MODULE_DATA_OFFSET = 6;
   private static final byte TAG_MODULE_DATA = 7;
   private static final byte TAG_APPLICATION_GROUP_DATA = 8;
   private static final byte TAG_VERSION_SUPPORTED = 9;
   private static final byte TAG_GENERAL_ERROR = 10;
   private static final byte TAG_TRANSACTION_STATUS = -1;
   private static final int VERSION = 1;
   private static final long APPLICATION_DELIVERY_PERSISTED_GUID = -5859617397995461741L;
   static boolean _debugMode;

   void clearApplications() {
      synchronized (this._applicationDeliveryPersistedData._pendingApplications) {
         this._applicationDeliveryPersistedData._pendingApplications = (LongHashtable)(new Object());
         this._persistentData.commit();
         this.isCleanerNecessary();
      }
   }

   protected boolean verifySourceUid(String sourceKeyId) {
      boolean securityViolation = true;
      String besUIDs = ITPolicy.getString(24, 51);
      if (besUIDs != null) {
         StringTokenizer tokenizer = (StringTokenizer)(new Object(besUIDs, ','));
         String uid = null;

         while (tokenizer.hasMoreElements()) {
            uid = (String)tokenizer.nextElement();
            if (CryptoBlock.validateSenderByUid(sourceKeyId, uid)) {
               securityViolation = false;
               break;
            }
         }
      }

      if (securityViolation) {
         ApplicationDeliveryEventLogger.logEvent(1163092822, 2);
      }

      return !securityViolation;
   }

   void processDataBuffer(DataBuffer data) {
      if (_debugMode) {
         int flashFree = Memory.getFlashFree();
         System.out.println(((StringBuffer)(new Object("APPD - Available Flash before processing databuffer: "))).append(flashFree).toString());
      }

      synchronized (this._applicationDeliveryPersistedData._pendingApplications) {
         this.processDataBuffer2(data);
         this._persistentData.commit();
      }

      if (_debugMode) {
         int flashFree = Memory.getFlashFree();
         System.out.println(((StringBuffer)(new Object("APPD - Available Flash after processing databuffer: "))).append(flashFree).toString());
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   void processDataBuffer2(DataBuffer data) {
      data.setBigEndian(true);
      ApplicationInfo applicationInfo = null;
      ModuleInfo moduleInfo = null;
      int moduleOffset = 0;
      byte[] sequenceData = null;
      int sequenceNumber = 0;
      int sequenceOffset = 0;
      int sequenceLength = 0;
      boolean var25 = false /* VF: Semaphore variable */;

      try {
         var25 = true;
         if (data.readByte() != 1) {
            ApplicationDeliveryEventLogger.logEvent(1162895693, 2);
            this.sendAcknowledgement(null, 5);
            return;
         }

         if (data.readByte() != 1) {
            ApplicationDeliveryEventLogger.logEvent(1162895693, 2);
            this.sendAcknowledgement(null, 7);
            return;
         }

         int bodyLength = data.readCompressedInt();
         if (bodyLength != data.available()) {
            ApplicationDeliveryEventLogger.logEvent(1162895693, 2);
            this.sendAcknowledgement(null, 8);
            return;
         }

         int tleStartPosition = data.getPosition();
         if (!this.moveToTagLocation(data, (byte)1)) {
            ApplicationDeliveryEventLogger.logEvent(1162895693, 2);
            this.sendAcknowledgement(null, 6);
            return;
         }

         data.readCompressedInt();
         long transactionId = data.readLong();
         data.setPosition(tleStartPosition);
         int moduleCount = 1;
         if (this.moveToTagLocation(data, (byte)2)) {
            moduleCount = TLEUtilities.readIntegerField(data);
         }

         data.setPosition(tleStartPosition);
         applicationInfo = (ApplicationInfo)this._applicationDeliveryPersistedData._pendingApplications.get(transactionId);
         if (applicationInfo == null) {
            applicationInfo = new ApplicationInfo(moduleCount, transactionId);
            Proxy.getInstance().addRealtimeClockListener(this);
            this._applicationDeliveryPersistedData._pendingApplications.put(transactionId, applicationInfo);
         }

         applicationInfo.setLastReceivedTimeStamp();
         if (!this.moveToTagLocation(data, (byte)3)) {
            applicationInfo.error(this, 1162895693, 6);
            return;
         }

         byte[] moduleHash = data.readByteArray();
         if (moduleHash == null || moduleHash.length != 20) {
            applicationInfo.error(this, 1162895693, 9);
            return;
         }

         data.setPosition(tleStartPosition);
         if (!this.moveToTagLocation(data, (byte)5)) {
            applicationInfo.error(this, 1162895693, 6);
            return;
         }

         int moduleSize = TLEUtilities.readIntegerField(data);
         data.setPosition(tleStartPosition);
         Hashtable modules = applicationInfo.getModules();
         ByteArray moduleHashByteArray = (ByteArray)(new Object(moduleHash));
         moduleInfo = (ModuleInfo)modules.get(moduleHashByteArray);
         if (moduleInfo == null) {
            moduleInfo = new ModuleInfo(moduleSize, moduleHash);
            modules.put(moduleHashByteArray, moduleInfo);
         }

         sequenceNumber = 0;
         if (this.moveToTagLocation(data, (byte)4)) {
            sequenceNumber = TLEUtilities.readIntegerField(data);
         }

         data.setPosition(tleStartPosition);
         moduleOffset = 0;
         if (this.moveToTagLocation(data, (byte)6)) {
            moduleOffset = TLEUtilities.readIntegerField(data);
         }

         data.setPosition(tleStartPosition);
         if (!this.moveToTagLocation(data, (byte)7)) {
            applicationInfo.error(this, 1162895693, 6);
            return;
         }

         sequenceLength = data.readCompressedInt();
         if (sequenceLength <= 0) {
            applicationInfo.error(this, 1162895693, 10);
            return;
         }

         sequenceData = data.getArray();
         sequenceOffset = data.getPosition();
         if (data.available() < sequenceLength) {
            applicationInfo.error(this, 1162895693, 11);
            return;
         }

         data.setPosition(tleStartPosition);
         if (this.moveToTagLocation(data, (byte)8)) {
            byte[] applicationGroupInfo = data.readByteArray();
            applicationInfo.setApplicationGroupData(applicationGroupInfo);
         }

         if (_debugMode) {
            System.out.println("----------------------------------------");
            System.out.println("APPD - Module Hash: ");
            DebugUtilities.printArrayContents(moduleHash);
            System.out.println(((StringBuffer)(new Object("APPD - Number of Modules:"))).append(moduleCount).toString());
            System.out.println(((StringBuffer)(new Object("APPD - Sequence Number:"))).append(sequenceNumber).toString());
            System.out.println(((StringBuffer)(new Object("APPD - Transaction Id:"))).append(transactionId).toString());
            System.out.println(((StringBuffer)(new Object("APPD - Module Offset:"))).append(moduleOffset).toString());
            System.out.println(((StringBuffer)(new Object("APPD - Sequence Length: "))).append(sequenceLength).toString());
            System.out.println(((StringBuffer)(new Object("APPD - Module Size: "))).append(moduleSize).toString());
            System.out.println("----------------------------------------");
         }

         moduleInfo.writeSequence(sequenceNumber, moduleOffset, sequenceData, sequenceOffset, sequenceLength);
         if (applicationInfo.hasErrorOccurred()) {
            applicationInfo.error(this, 1162695267, -1);
            return;
         }

         var25 = false;
      } finally {
         if (var25) {
            if (applicationInfo != null) {
               applicationInfo.error(this, 1162895693, 6);
               return;
            }

            ApplicationDeliveryEventLogger.logEvent(1162895693, 2);
            return;
         }
      }

      boolean var22 = false /* VF: Semaphore variable */;

      try {
         var22 = true;
         if (applicationInfo.haveAllModulesBeenReceived()) {
            applicationInfo.saveAllModules(this);
            this.isCleanerNecessary();
            return;
         }

         var22 = false;
      } finally {
         if (var22) {
            applicationInfo.error(this, 1163359297, -1);
            return;
         }
      }
   }

   boolean moveToTagLocation(DataBuffer data, byte tagToFind) {
      while (true) {
         try {
            if (data.readByte() == tagToFind) {
               return true;
            }

            int length = data.readCompressedInt();
            data.skipBytes(length);
         } finally {
            ;
         }
      }
   }

   boolean sendAcknowledgement(ApplicationInfo applicationInfo, int transactionStatus) {
      return this.sendAcknowledgement(applicationInfo, transactionStatus, null);
   }

   void isCleanerNecessary() {
      if (this._applicationDeliveryPersistedData._pendingApplications.size() <= 0) {
         Proxy.getInstance().removeRealtimeClockListener(this);
      }
   }

   public void scheduleDeviceReset() {
      ForcedResetManager _resetManager = ForcedResetManager.getInstance();
      _resetManager.scheduleDeviceReset(CommonResources.getString(9129));
   }

   void cleanUpOldApplications() {
      synchronized (this._applicationDeliveryPersistedData._pendingApplications) {
         this._lastCleanTime = System.currentTimeMillis();
         ApplicationInfo[] recordsToRemove = new ApplicationInfo[0];
         Enumeration enumeration = this._applicationDeliveryPersistedData._pendingApplications.elements();

         while (enumeration.hasMoreElements()) {
            ApplicationInfo applicationInfo = (ApplicationInfo)enumeration.nextElement();
            if (System.currentTimeMillis() - applicationInfo.getLastReceivedTimeStamp() > this.CLEAN_CHECK_TIME) {
               applicationInfo.error(this, 1161916500, 2, true);
               Arrays.add(recordsToRemove, applicationInfo);
            }
         }

         for (int i = 0; i < recordsToRemove.length; i++) {
            this._applicationDeliveryPersistedData._pendingApplications.remove(recordsToRemove[i].getTransactionId());
         }

         if (recordsToRemove.length > 0) {
            this._persistentData.commit();
         }

         this.isCleanerNecessary();
      }
   }

   @Override
   public void clockUpdated() {
      if (System.currentTimeMillis() - this._lastCleanTime > this.CLEAN_CHECK_TIME) {
         this.cleanUpOldApplications();
      }
   }

   @Override
   public synchronized void receivePacket(DataBuffer packetDataBuffer, Object contextObject) {
      GMEDatagram gmeDatagram = this.verifyGMEDatagram(packetDataBuffer, true);
      if (gmeDatagram != null) {
         GMETarget source = gmeDatagram.getGMEAddress().getSrc();
         ApplicationDeliveryEventLogger.logEvent(1382240363, 0);
         String keyId = gmeDatagram.getKeyId();
         if (!this.verifySourceUid(keyId)) {
            ApplicationDeliveryEventLogger.logEvent(1163092822, 2);
            this.sendAcknowledgement(null, 14, source.address);
         } else {
            this._applicationDeliveryPersistedData._sourceUid = source.address.getBytes();
            this._applicationDeliveryPersistedData._keyId = keyId;
            this.processDataBuffer(packetDataBuffer);
         }
      }
   }

   @Override
   protected DatagramConnection createConnection(ServiceRecord serviceRecord) {
      return null;
   }

   @Override
   protected DatagramConnection createSendingConnection(ServiceRecord serviceRecord) {
      String gcfURL;
      if (serviceRecord == null) {
         gcfURL = "gme:APPD";
      } else {
         gcfURL = ((StringBuffer)(new Object("gme:APPD/"))).append(serviceRecord.getUid()).toString();
      }

      return (DatagramConnection)Connector.open(gcfURL);
   }

   private DatagramConnection createSendingConnection(String uid) {
      String gcfURL;
      if (uid == null) {
         gcfURL = "gme:APPD";
      } else {
         gcfURL = ((StringBuffer)(new Object("gme:APPD/"))).append(uid).toString();
      }

      return (DatagramConnection)Connector.open(gcfURL);
   }

   private boolean sendAcknowledgement(ApplicationInfo applicationInfo, int transactionStatus, String uid) {
      String targetUID = uid;
      if (targetUID == null) {
         if (!this.verifySourceUid(this._applicationDeliveryPersistedData._keyId)) {
            return false;
         }

         targetUID = (String)(new Object(this._applicationDeliveryPersistedData._sourceUid));
      }

      DataBuffer data = (DataBuffer)(new Object(true));
      data.writeByte(1);
      data.writeByte(2);
      DataBuffer tleData = (DataBuffer)(new Object(true));
      if (transactionStatus == 5) {
         tleData.writeByte(9);
         tleData.writeCompressedInt(1);
         tleData.writeByte(1);
      } else if (applicationInfo != null) {
         tleData.writeByte(1);
         tleData.writeCompressedInt(8);
         tleData.writeLong(applicationInfo.getTransactionId());
      } else if (applicationInfo == null) {
         tleData.writeByte(10);
         tleData.writeCompressedInt(DataBuffer.getCompressedIntSize(transactionStatus));
         tleData.writeCompressedInt(transactionStatus);
      }

      TLEUtilities.writeIntegerField(tleData, -1, transactionStatus, false);
      data.writeCompressedInt(tleData.getLength());
      data.write(tleData.getArray());
      ServiceRecord[] srs = ServiceBook.getSB().findRecordsByUid(targetUID);
      if (srs.length <= 0) {
         ApplicationDeliveryEventLogger.logEvent(1163084115, 2);
         return false;
      }

      ContextObject context = (ContextObject)(new Object());
      ContextObject.put(context, -7050660451800027507L, srs);
      super._serviceRecord = srs[0];
      if (super._serviceRecord != null) {
         label49:
         try {
            this.setSendingConnection(this.createSendingConnection(targetUID));
         } finally {
            break label49;
         }
      }

      Packet packet = (Packet)(new Object(data, null, 0, context));
      this.transmitPacket(packet, null);
      return true;
   }

   @Override
   public int getStatus() {
      if (!this.verifySourceUid(this._applicationDeliveryPersistedData._keyId)) {
         return 2;
      }

      ServiceRecord[] srs = ServiceBook.getSB().findRecordsByUid((String)(new Object(this._applicationDeliveryPersistedData._sourceUid)));
      return srs != null && srs.length > 0 ? 3 : 2;
   }

   @Override
   protected DatagramConnection createReceivingConnection(ServiceRecord serviceRecord) {
      return (DatagramConnection)Connector.open("gme:APPD");
   }

   static ApplicationDeliveryTransmissionService getInstance() {
      TransmissionService transmissionService = TransmissionServiceManager.get(-4198074063353182686L);
      return !(transmissionService instanceof ApplicationDeliveryTransmissionService) ? null : (ApplicationDeliveryTransmissionService)transmissionService;
   }

   @Override
   protected ServiceRecord initServiceRecord() {
      return super._serviceRecord;
   }

   @Override
   protected DatagramConnectionUser createReceiverThread(DatagramConnection connection) {
      DefaultReceiveThread receiveThread = (DefaultReceiveThread)(new Object(this, 8238338396594524829L));

      try {
         receiveThread.setDatagramConnection(this.createReceivingConnection(null));
         return receiveThread;
      } finally {
         ;
      }
   }

   ApplicationDeliveryTransmissionService() {
      super(-4198074063353182686L, 8238338396594524829L, false);
      this._persistentData = RIMPersistentStore.getPersistentObject(-5859617397995461741L);
      synchronized (this._persistentData) {
         if (this._persistentData.getContents() == null) {
            this._persistentData.setContents(new ApplicationDeliveryTransmissionService$ApplicationDeliveryPersistedData((LongHashtable)(new Object())), 51);
            this._persistentData.commit();
         }
      }

      this._applicationDeliveryPersistedData = (ApplicationDeliveryTransmissionService$ApplicationDeliveryPersistedData)this._persistentData.getContents();
      if (this._applicationDeliveryPersistedData._pendingApplications.size() > 0) {
         this._lastCleanTime = System.currentTimeMillis();
         Proxy.getInstance().addRealtimeClockListener(this);
      }
   }
}

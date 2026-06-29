package net.rim.device.api.servicebook;

import java.util.Hashtable;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.crypto.CryptoBlock;
import net.rim.device.internal.crypto.OTAKeyGenCrypto;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.TraceBack;

public final class ServiceBook implements Persistable, GlobalEventListener {
   private ServiceRecord[] _records;
   private int _nextRecordId;
   private boolean _brInProgress;
   private int[] _recordCounts;
   private ServiceBook$SBRestriction[] _restrictions;
   private boolean _serialInjectionInProgress;
   private Hashtable _serviceStatuses;
   private IntIntHashtable _CIDAttributes;
   Hashtable _serverPINmappings = new Hashtable();
   public static final long SB_GUID = -863050508581563378L;
   public static final int DUP_NONE = 0;
   public static final int DUP_REMOVED = 1;
   public static final int DUP_REPLACED = 2;
   public static final long GUID_SB_ADDED = -4220058463650496006L;
   public static final long GUID_SB_REMOVED = 2522898683889177438L;
   public static final long GUID_SB_CHANGED = 8288627527798139133L;
   public static final long GUID_SB_BR_START = 1348796660760556312L;
   public static final long GUID_SB_BR_END = -583230596614878690L;
   public static final long GUID_SB_OTA_UPDATE = 6213587377148297993L;
   public static final long GUID_SB_OTA_SWITCH = -5256071285987383000L;
   public static final long GUID_SB_PIN_CHANGE = -1426098722237447363L;
   public static final long GUID_SB_POLICY_CHANGED = 1077267820605375385L;
   public static final int SERVICEBOOK_PAYLOAD_VERSION_3 = 48;
   public static final int SEND_ACK_FLAG = 64;
   public static final int ACK_SUCCESSFUL = 192;
   public static final int ACK_FAILURE = 128;
   public static final int SOURCE_SERVER_UID = 1;
   public static final int NEW_SERVER_UID = 2;
   public static final int TO_BE_DELETED_SERVER_UID = 1;
   public static final int SERVICE_STATUS_FIELD = 3;
   public static final int STATUS_LAST_PIN = 1;
   public static final int COMMAND_INJECT_SERVICE = 0;
   public static final int COMMAND_SWITCH_SERVICE = 1;
   public static final int COMMAND_DELETE_SERVICE = 2;
   public static final int COMMAND_SERVICE_STATUS = 3;
   public static final int RESULT_SUCCESS = 0;
   public static final int RESULT_GENERAL_ERROR = 1;
   public static final int RESULT_UNKNOWN_COMMAND = 2;
   public static final int RESULT_SECURITY_VIOLATION_ERROR = 3;
   public static final int RESULT_KEY_ERROR = 4;
   public static final int RESULT_KEY_MISMATCH = 5;
   public static final int RESULT_UNSUPPORTED_CMD_VERSION = 6;
   public static final int RESULT_SWITCH_PENDING_ERROR = 7;
   public static final int RESULT_DUPLICATE_SERVICE_ERROR = 8;
   public static final int RESULT_PACKET_PARSE_ERROR = 9;
   public static final int RESULT_CUSTOM_ERROR = 255;
   public static final byte SERVICE_RECORD_SUMMARY_RECORD = 1;
   private static DataBuffer _commandPayload = new DataBuffer();
   private static String _pendingSwitchSourceUID = null;
   private static String _pendingSwitchDestinationUID = null;
   private static long _pendingTimestamp = 0;
   private static final int CID_ATTRIBUTE_SINGLETON_NOT_REGISTERED = 0;
   private static final int CID_ATTRIBUTE_SINGLETON_SECURE = 1;
   private static final int CID_ATTRIBUTE_SINGLETON_INSECURE = 2;
   private static final int CID_ATTRIBUTE_SINGLETON = 3;
   private static ServiceBook _instance;
   public static final int VIEW_MODE_UNKNOWN = -1;
   public static final int VIEW_MODE_RW = 0;
   public static final int VIEW_MODE_R = 1;
   public static final int VIEW_MODE_RO = 2;

   public final synchronized ServiceRecord addRecord(ServiceRecord sr) {
      return this.securityCheckIncomingServiceRecord(sr, 3, null, null, false) == 0 ? this.addRecordInternal(sr) : null;
   }

   public final synchronized boolean removeRecord(ServiceRecord sr) {
      assertRRISignature();
      int index = Arrays.getIndex(this._records, sr);
      if (index == -1) {
         return false;
      }

      this.expungeRecord(index);
      int serviceCount = 0;

      for (int i = this._records.length - 1; i >= 0; i--) {
         if (StringUtilities.compareToIgnoreCase(sr.getUid(), this._records[i].getUid(), 1701707776) == 0) {
            serviceCount++;
         }
      }

      if (serviceCount == 0) {
         this._serviceStatuses.remove(StringUtilities.toLowerCase(sr.getUid(), 1701707776));
         this.doCommit();
      }

      return true;
   }

   public final synchronized void removeAllRecords() {
      this.removeAllRecords(true);
   }

   public final synchronized void removeAllRecords(boolean removeSecureRecords) {
      assertRRISignature();

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (removeSecureRecords || !sr.isSecureService()) {
            this.expungeRecord(i);
         }
      }

      this._serviceStatuses.clear();
      this.doCommit();
   }

   public final synchronized ServiceBook$ServiceStatus getStatusForUid(String uid) {
      assertPermission(TraceBack.getCallingModule(0));
      ServiceBook$ServiceStatus result = null;
      if (uid != null) {
         result = (ServiceBook$ServiceStatus)this._serviceStatuses.get(StringUtilities.toLowerCase(uid, 1701707776));
      }

      return result;
   }

   public final synchronized void updateStatusForUid(String uid, ServiceBook$ServiceStatus serviceStatus) {
      assertPermission(TraceBack.getCallingModule(0));
      if (uid != null && serviceStatus != null) {
         this._serviceStatuses.put(StringUtilities.toLowerCase(uid, 1701707776), serviceStatus);
         this.doCommit();
      }
   }

   public final int getNumRecords(int type) {
      assertPermission(TraceBack.getCallingModule(0));

      try {
         return this._recordCounts[type];
      } finally {
         ;
      }
   }

   public final synchronized ServiceRecord getRecordById(int id) {
      assertPermission(TraceBack.getCallingModule(0));

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (sr.getId() == id) {
            return sr;
         }
      }

      return null;
   }

   public final synchronized ServiceRecord getRecordByUidAndCid(String uid, String cid) {
      assertPermission(TraceBack.getCallingModule(0));

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (sr.getType() == 0
            && StringUtilities.strEqualIgnoreCase(uid, sr.getUid(), 1701707776)
            && StringUtilities.strEqualIgnoreCase(cid, sr.getCid(), 1701707776)) {
            return sr;
         }
      }

      return null;
   }

   public final synchronized boolean isRecordDisallowed(String uid, String cid) {
      assertPermission(TraceBack.getCallingModule(0));

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (sr.getType() == 6
            && StringUtilities.strEqualIgnoreCase(uid, sr.getUid(), 1701707776)
            && StringUtilities.strEqualIgnoreCase(cid, sr.getCid(), 1701707776)) {
            return true;
         }
      }

      return false;
   }

   public final synchronized ServiceRecord getRecordByCidAndHash(String cid, int nameHash, int uidHash) {
      assertPermission(TraceBack.getCallingModule(0));

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (sr.getType() == 0
            && StringUtilities.strEqualIgnoreCase(cid, sr.getCid(), 1701707776)
            && (nameHash == sr.getNameHash() || uidHash == sr.getUidHash())) {
            return sr;
         }
      }

      return null;
   }

   public final synchronized ServiceRecord getRecordByCidAndDataSourceId(String cid, String aDataSourceId) {
      assertPermission(TraceBack.getCallingModule(0));

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (sr.getType() == 0 && StringUtilities.strEqualIgnoreCase(cid, sr.getCid(), 1701707776) && aDataSourceId.equals(sr.getDataSourceId())) {
            return sr;
         }
      }

      return null;
   }

   public final synchronized ServiceRecord getRecordByCidAndUserId(String cid, int userId, int nameHash, int uidHash) {
      ServiceRecord sr = null;
      if (userId != -1) {
         sr = this.getRecordByCidAndUserId(cid, userId);
      } else {
         sr = this.getRecordByCidAndHash(cid, nameHash, uidHash);
      }

      return sr;
   }

   public final synchronized ServiceRecord getRecordByCidAndUserId(String cid, int userId) {
      assertPermission(TraceBack.getCallingModule(0));

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (sr.getType() == 0 && StringUtilities.strEqualIgnoreCase(cid, sr.getCid(), 1701707776) && userId == sr.getUserId()) {
            return sr;
         }
      }

      return null;
   }

   public final synchronized ServiceRecord getRecordByCidAndSid(String cid, long sid) {
      assertPermission(TraceBack.getCallingModule(0));

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (sr.getType() == 0 && StringUtilities.strEqualIgnoreCase(cid, sr.getCid(), 1701707776) && ServiceIdentifier.createSid(sr) == sid) {
            return sr;
         }
      }

      return null;
   }

   public final ServiceRecord getCIDAssociatedWithService(String targetCID, ServiceRecord sr) {
      return this.getCIDAssociatedWithService(targetCID, sr.getUserId(), sr.getDataSourceId(), sr.getUid());
   }

   public final ServiceRecord getCIDAssociatedWithService(String targetCID, int userID, String dsID, String uid) {
      ServiceRecord targetServiceRecord = null;

      label47:
      try {
         if (dsID != null) {
            targetServiceRecord = this.getRecordByCidAndDataSourceId(targetCID, dsID);
            if (targetServiceRecord == null && userID != -1) {
               targetServiceRecord = this.getRecordByCidAndUserId(targetCID, userID);
            }
         }

         if (targetServiceRecord == null && userID != -1) {
            targetServiceRecord = this.getRecordByCidAndUserId(targetCID, userID);
         }
      } finally {
         break label47;
      }

      if (dsID == null && userID == -1) {
         targetServiceRecord = this.getRecordByUidAndCid(uid, targetCID);
      }

      return targetServiceRecord;
   }

   public final synchronized ServiceRecord[] findRecordsByType(int type) {
      assertPermission(TraceBack.getCallingModule(0));
      ServiceRecord[] srs = new ServiceRecord[0];

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (sr.getType() == type) {
            Arrays.add(srs, sr);
         }
      }

      return srs;
   }

   public final synchronized ServiceRecord[] findRecordsByUid(String uid) {
      assertPermission(TraceBack.getCallingModule(0));
      ServiceRecord[] srs = new ServiceRecord[0];

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (sr.getType() == 0 && StringUtilities.strEqualIgnoreCase(uid, sr.getUid(), 1701707776)) {
            Arrays.add(srs, sr);
         }
      }

      return srs;
   }

   public final synchronized ServiceRecord[] findRecordsByCid(String cid) {
      assertPermission(TraceBack.getCallingModule(0));
      ServiceRecord[] srs = new ServiceRecord[0];

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (sr.getType() == 0 && StringUtilities.strEqualIgnoreCase(cid, sr.getCid(), 1701707776)) {
            Arrays.add(srs, sr);
         }
      }

      return srs;
   }

   public final synchronized ServiceRecord[] findRecordsByName(String name) {
      assertPermission(TraceBack.getCallingModule(0));
      ServiceRecord[] srs = new ServiceRecord[0];

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (name.equals(sr.getName()) && sr.getType() == 0) {
            Arrays.add(srs, sr);
         }
      }

      return srs;
   }

   public final synchronized ServiceRecord[] findDuplicateRecords(
      ServiceRecord rec, int newType, String newName, String newUid, String newCid, String dataSourceId, int userId
   ) {
      assertPermission(TraceBack.getCallingModule(0));
      ServiceRecord[] srs = new ServiceRecord[0];

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (sr.isDuplicate(rec, newType, newName, newUid, newCid, dataSourceId, userId)) {
            Arrays.add(srs, sr);
         }
      }

      return srs;
   }

   public final synchronized ServiceRecord[] getRecords() {
      assertPermission(TraceBack.getCallingModule(0));
      return this._records;
   }

   public final boolean isAllowedRecord(ServiceRecord sr, int newType, String newName, String newUid, String newCid) {
      boolean implicitAccept = true;
      int size = this._restrictions.length;

      for (int i = 0; i < size; i++) {
         switch (this._restrictions[i].isAllowed(sr, newType, newName, newUid, newCid)) {
            case -2147483647:
               implicitAccept = true;
               break;
            case -2147483646:
               implicitAccept = false;
               break;
            case 1:
               return true;
            case 2:
               return false;
         }
      }

      return implicitAccept;
   }

   public final synchronized void commit() {
      assertPermission(TraceBack.getCallingModule(0));

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         if (sr.isDirty()) {
            switch (sr.getType()) {
               case 5:
                  sr.setType(0);
               case 0:
                  if (!this.isAllowedRecord(sr, -1, null, null, null)) {
                     sr.setType(6);
                  }
                  break;
               case 6:
                  if (this.isAllowedRecord(sr, 0, null, null, null)) {
                     sr.setType(0);
                  }
            }

            this.countRecord(sr);
         }
      }

      int i = 0;

      while (i < this._records.length) {
         ServiceRecord sr = this._records[i];
         if (!sr.isDirty() || this.handleDuplicates(sr) == sr) {
            i++;
         }
      }

      this.doCommit();
   }

   public final void displayEditor(int viewMode) {
      assertRRISignature();
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      SBThunks thunks = (SBThunks)reg.waitFor(5050782853726896085L);
      thunks.displayEditor(this, viewMode);
   }

   final void setBrInProgress(boolean flag) {
      if (this._brInProgress && !flag) {
         this._brInProgress = false;
         this.commit();
         this.xmitSBEvent(-583230596614878690L, null, null);
      } else {
         if (!this._brInProgress && flag) {
            this._brInProgress = true;
            this.xmitSBEvent(1348796660760556312L, null, null);
         }
      }
   }

   public final void setSerialInjectionInProgress(boolean flag) {
      assertPermission(TraceBack.getCallingModule(0));
      if (this._serialInjectionInProgress && !flag) {
         this._serialInjectionInProgress = false;
      } else {
         if (!this._serialInjectionInProgress && flag) {
            this._serialInjectionInProgress = true;
         }
      }
   }

   public final boolean getSerialInjectionInProgress() {
      assertPermission(TraceBack.getCallingModule(0));
      return this._serialInjectionInProgress;
   }

   public final synchronized byte[] getServiceList() {
      ServiceRecord[] records = this.getRecords();
      DataBuffer buffer = new DataBuffer(true);
      DataBuffer tempbuffer = new DataBuffer(true);
      buffer.setPosition(0);

      for (int i = 0; i < records.length; i++) {
         ServiceRecord sr = records[i];
         String uid = sr.getUid();
         String cid = sr.getCid();
         String name = sr.getName();
         String datasourceId = sr.getDataSourceId();
         tempbuffer.reset();
         tempbuffer.writeByte(6);
         tempbuffer.writeShort(uid.length());
         tempbuffer.write(uid.getBytes());
         tempbuffer.writeByte(8);
         tempbuffer.writeShort(cid.length());
         tempbuffer.write(cid.getBytes());
         tempbuffer.writeByte(1);
         tempbuffer.writeShort(name.length());
         tempbuffer.write(name.getBytes());
         if (datasourceId != null) {
            tempbuffer.writeByte(161);
            tempbuffer.writeShort(datasourceId.length());
            tempbuffer.write(datasourceId.getBytes());
         }

         if (sr.getUserId() != -1) {
            tempbuffer.writeByte(163);
            tempbuffer.writeShort(4);
            tempbuffer.writeInt(sr.getUserId());
         }

         tempbuffer.writeByte(11);
         tempbuffer.writeShort(4);
         tempbuffer.writeInt(sr.getEncryptionMode());
         tempbuffer.trim();
         byte[] bytes = tempbuffer.toArray();
         buffer.write(1);
         buffer.writeCompressedInt(bytes.length);
         buffer.write(bytes);
      }

      buffer.trim();
      return buffer.toArray();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized void itPolicyChanged(byte[] sbInfo) {
      assertRRISignature();
      ServiceBook$ITPolicyParser parser = new ServiceBook$ITPolicyParser();
      if (sbInfo != null) {
         int version = sbInfo[0] & 255;
         switch (version & 240) {
            case 16:
            case 32:
               parser.setVersion(version);
               boolean var7 = false /* VF: Semaphore variable */;

               try {
                  var7 = true;
                  TLEUtilities.parseField(new DataBuffer(sbInfo, 1, sbInfo.length, true), parser, sbInfo.length);
                  var7 = false;
                  break;
               } finally {
                  if (var7) {
                     return;
                  }
               }
            default:
               return;
         }
      }

      this._restrictions = parser.getRestrictions();

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr = this._records[i];
         int type = sr.getType();
         if (type == 6) {
            if (this.isAllowedRecord(sr, 0, null, null, null)) {
               sr.setType(0);
            }
         } else if (type == 0 && !this.isAllowedRecord(sr, -1, null, null, null)) {
            sr.setType(6);
         }
      }

      this.commit();
      this.xmitSBEvent(1077267820605375385L, null, null);
   }

   public final synchronized byte[] processServiceBookCommandData(DataBuffer param1, int param2, String param3, String param4) {
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
      // 000: invokestatic net/rim/device/api/servicebook/ServiceBook.assertRRISignature ()V
      // 003: new net/rim/device/api/util/DataBuffer
      // 006: dup
      // 007: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 00a: astore 5
      // 00c: iload 2
      // 00d: bipush 1
      // 00e: if_icmpeq 01c
      // 011: aload 0
      // 012: iload 2
      // 013: aload 4
      // 015: aload 3
      // 016: invokespecial net/rim/device/api/servicebook/ServiceBook.authenticateSender (ILjava/lang/String;Ljava/lang/String;)Z
      // 019: ifeq 020
      // 01c: bipush 1
      // 01d: goto 021
      // 020: bipush 0
      // 021: istore 6
      // 023: aload 1
      // 024: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 027: istore 7
      // 029: aload 1
      // 02a: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 02d: istore 8
      // 02f: iload 8
      // 031: sipush 240
      // 034: iand
      // 035: lookupswitch 214 1 48 19
      // 048: aload 1
      // 049: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 04c: istore 9
      // 04e: getstatic net/rim/device/api/servicebook/ServiceBook._commandPayload Lnet/rim/device/api/util/DataBuffer;
      // 051: aload 1
      // 052: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 055: aload 1
      // 056: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // 059: iload 9
      // 05b: aload 1
      // 05c: invokevirtual net/rim/device/api/util/DataBuffer.isBigEndian ()Z
      // 05f: invokevirtual net/rim/device/api/util/DataBuffer.setData ([BIIZ)V
      // 062: aload 1
      // 063: iload 9
      // 065: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 068: pop
      // 069: getstatic net/rim/device/api/servicebook/ServiceBook._commandPayload Lnet/rim/device/api/util/DataBuffer;
      // 06c: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 06f: ifne 08d
      // 072: aload 5
      // 074: bipush -1
      // 076: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 079: aload 5
      // 07b: bipush -128
      // 07d: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 080: aload 5
      // 082: bipush 1
      // 083: invokevirtual net/rim/device/api/util/DataBuffer.writeCompressedInt (I)V
      // 086: aload 5
      // 088: bipush 9
      // 08a: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 08d: getstatic net/rim/device/api/servicebook/ServiceBook._commandPayload Lnet/rim/device/api/util/DataBuffer;
      // 090: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 093: ifle 0e4
      // 096: iload 2
      // 097: bipush 1
      // 098: if_icmpeq 0a6
      // 09b: aload 0
      // 09c: iload 2
      // 09d: aload 4
      // 09f: aload 3
      // 0a0: invokespecial net/rim/device/api/servicebook/ServiceBook.authenticateSender (ILjava/lang/String;Ljava/lang/String;)Z
      // 0a3: ifeq 0aa
      // 0a6: bipush 1
      // 0a7: goto 0ab
      // 0aa: bipush 0
      // 0ab: istore 6
      // 0ad: getstatic net/rim/device/api/servicebook/ServiceBook._commandPayload Lnet/rim/device/api/util/DataBuffer;
      // 0b0: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 0b3: istore 10
      // 0b5: getstatic net/rim/device/api/servicebook/ServiceBook._commandPayload Lnet/rim/device/api/util/DataBuffer;
      // 0b8: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 0bb: istore 11
      // 0bd: getstatic net/rim/device/api/servicebook/ServiceBook._commandPayload Lnet/rim/device/api/util/DataBuffer;
      // 0c0: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 0c3: pop
      // 0c4: getstatic net/rim/device/api/servicebook/ServiceBook._commandPayload Lnet/rim/device/api/util/DataBuffer;
      // 0c7: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 0ca: istore 12
      // 0cc: aload 0
      // 0cd: iload 10
      // 0cf: iload 12
      // 0d1: iload 11
      // 0d3: getstatic net/rim/device/api/servicebook/ServiceBook._commandPayload Lnet/rim/device/api/util/DataBuffer;
      // 0d6: iload 2
      // 0d7: aload 3
      // 0d8: aload 4
      // 0da: iload 6
      // 0dc: aload 5
      // 0de: invokespecial net/rim/device/api/servicebook/ServiceBook.processCommand (BBBLnet/rim/device/api/util/DataBuffer;ILjava/lang/String;Ljava/lang/String;ZLnet/rim/device/api/util/DataBuffer;)Z
      // 0e1: ifne 08d
      // 0e4: aload 5
      // 0e6: invokevirtual net/rim/device/api/util/DataBuffer.trim ()V
      // 0e9: aload 5
      // 0eb: invokevirtual net/rim/device/api/util/DataBuffer.toArray ()[B
      // 0ee: astore 10
      // 0f0: aload 5
      // 0f2: invokevirtual net/rim/device/api/util/DataBuffer.reset ()V
      // 0f5: aload 5
      // 0f7: bipush 48
      // 0f9: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 0fc: aload 5
      // 0fe: aload 10
      // 100: invokevirtual net/rim/device/api/util/DataBuffer.writeByteArray ([B)V
      // 103: aload 5
      // 105: invokevirtual net/rim/device/api/util/DataBuffer.trim ()V
      // 108: goto 125
      // 10b: iload 6
      // 10d: ifne 113
      // 110: goto 125
      // 113: aload 1
      // 114: iload 7
      // 116: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 119: aload 0
      // 11a: aload 1
      // 11b: iload 2
      // 11c: aload 3
      // 11d: aload 4
      // 11f: iload 6
      // 121: invokespecial net/rim/device/api/servicebook/ServiceBook.processServiceBookData (Lnet/rim/device/api/util/DataBuffer;ILjava/lang/String;Ljava/lang/String;Z)I
      // 124: pop
      // 125: getstatic net/rim/device/api/servicebook/ServiceBook._commandPayload Lnet/rim/device/api/util/DataBuffer;
      // 128: invokevirtual net/rim/device/api/util/DataBuffer.reset ()V
      // 12b: goto 144
      // 12e: astore 6
      // 130: getstatic net/rim/device/api/servicebook/ServiceBook._commandPayload Lnet/rim/device/api/util/DataBuffer;
      // 133: invokevirtual net/rim/device/api/util/DataBuffer.reset ()V
      // 136: goto 144
      // 139: astore 13
      // 13b: getstatic net/rim/device/api/servicebook/ServiceBook._commandPayload Lnet/rim/device/api/util/DataBuffer;
      // 13e: invokevirtual net/rim/device/api/util/DataBuffer.reset ()V
      // 141: aload 13
      // 143: athrow
      // 144: aload 0
      // 145: getfield net/rim/device/api/servicebook/ServiceBook._serverPINmappings Ljava/util/Hashtable;
      // 148: invokevirtual java/util/Hashtable.keys ()Ljava/util/Enumeration;
      // 14b: astore 6
      // 14d: aload 6
      // 14f: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 154: ifeq 196
      // 157: aload 6
      // 159: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 15e: checkcast java/lang/String
      // 161: astore 7
      // 163: aload 7
      // 165: ifnull 14d
      // 168: aload 0
      // 169: getfield net/rim/device/api/servicebook/ServiceBook._serverPINmappings Ljava/util/Hashtable;
      // 16c: aload 7
      // 16e: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 171: checkcast java/lang/Integer
      // 174: astore 8
      // 176: aload 8
      // 178: ifnull 14d
      // 17b: aload 8
      // 17d: invokevirtual java/lang/Integer.intValue ()I
      // 180: istore 9
      // 182: ldc2_w -1426098722237447363
      // 185: invokestatic net/rim/device/api/system/DeviceInfo.getDeviceId ()I
      // 188: iload 9
      // 18a: aload 7
      // 18c: aconst_null
      // 18d: invokestatic net/rim/device/api/system/RIMGlobalMessagePoster.postGlobalEvent (JIILjava/lang/Object;Ljava/lang/Object;)Z
      // 190: pop
      // 191: goto 14d
      // 194: astore 6
      // 196: aload 0
      // 197: getfield net/rim/device/api/servicebook/ServiceBook._serverPINmappings Ljava/util/Hashtable;
      // 19a: invokevirtual java/util/Hashtable.clear ()V
      // 19d: aload 5
      // 19f: invokevirtual net/rim/device/api/util/DataBuffer.trim ()V
      // 1a2: aload 5
      // 1a4: invokevirtual net/rim/device/api/util/DataBuffer.getLength ()I
      // 1a7: ifle 1b0
      // 1aa: aload 5
      // 1ac: invokevirtual net/rim/device/api/util/DataBuffer.toArray ()[B
      // 1af: areturn
      // 1b0: aconst_null
      // 1b1: areturn
      // try (5 -> 129): 132 null
      // try (5 -> 129): 136 null
      // try (132 -> 133): 136 null
      // try (136 -> 137): 136 null
      // try (141 -> 173): 173 null
   }

   public final void deRegisterCIDAsSingleton(String CID) {
      this.deRegisterCIDAsSingleton(CID, true, true);
   }

   public final void registerCIDAsSingleton(String CID) {
      this.registerCIDAsSingleton(CID, true, true);
   }

   public final void registerCIDAsSingleton(String CID, boolean secureServices, boolean insecureServices) {
      if (CID != null) {
         CID = StringUtilities.toLowerCase(CID, 1701707776);
         int attributes = 0;
         int code = CRC32.update(0, CID.getBytes());
         if (this._CIDAttributes.containsKey(code)) {
            attributes = this._CIDAttributes.get(code);
         }

         if (secureServices) {
            attributes |= 1;
         }

         if (insecureServices) {
            attributes |= 2;
         }

         this._CIDAttributes.put(code, attributes);
      }
   }

   public final void deRegisterCIDAsSingleton(String CID, boolean secureServices, boolean insecureServices) {
      if (CID != null) {
         CID = StringUtilities.toLowerCase(CID, 1701707776);
         int attributes = 0;
         int code = CRC32.update(0, CID.getBytes());
         if (this._CIDAttributes.containsKey(code)) {
            attributes = this._CIDAttributes.get(code);
         }

         if (secureServices) {
            attributes--;
         }

         if (insecureServices) {
            attributes -= 2;
         }

         if (attributes < 0) {
            attributes = 0;
         }

         this._CIDAttributes.put(code, attributes);
      }
   }

   final boolean isCIDRegisteredAsSingleton(String CID, boolean secure, boolean insecure) {
      if (CID == null) {
         return false;
      }

      CID = StringUtilities.toLowerCase(CID, 1701707776);
      boolean result = false;
      int attributes = 0;
      int code = CRC32.update(0, CID.getBytes());
      if (this._CIDAttributes.containsKey(code)) {
         attributes = this._CIDAttributes.get(code);
      }

      if (attributes == 3) {
         return true;
      }

      if (secure) {
         result = (attributes & 1) > 0;
      }

      if (insecure) {
         if (secure) {
            result &= (attributes & 2) > 0;
         } else {
            result = (attributes & 2) > 0;
         }
      }

      return result;
   }

   final int getCIDSingletonAttributes(String CID) {
      if (CID == null) {
         return 0;
      }

      CID = StringUtilities.toLowerCase(CID, 1701707776);
      int code = CRC32.update(0, CID.getBytes());
      return this._CIDAttributes.containsKey(code) ? this._CIDAttributes.get(code) : 0;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      assertRRISignature();
      if (guid != 8508406279413621091L && guid != -594020114676189989L) {
         if (guid == -254931370837867202L) {
            for (int i = this._records.length - 1; i >= 0; i--) {
               this._records[i].eventOccurred(guid, data0, data1, object0, object1);
            }
         }
      } else {
         byte[] serviceBook = ITPolicy.getByteArray(4);
         if (serviceBook == null) {
            serviceBook = ITPolicy.getByteArray(3);
            if (serviceBook != null) {
               byte[] temp = new byte[serviceBook.length + 1];
               temp[0] = 16;
               System.arraycopy(serviceBook, 0, temp, 1, serviceBook.length);
               serviceBook = temp;
            }
         }

         this.itPolicyChanged(serviceBook);
      }
   }

   private final synchronized void doCommit() {
      if (!this._brInProgress) {
         PersistentObject.commit(this);
      }
   }

   private final synchronized void countRecord(ServiceRecord sr) {
      int type = sr.getType();
      int lastType = sr.getLastCountedType();
      if (type != lastType) {
         this._recordCounts[type]++;
         if (lastType != 4) {
            this._recordCounts[lastType]--;
         }

         sr.setLastCountedType(type);
      }
   }

   private final synchronized ServiceRecord handleDuplicates(ServiceRecord sr) {
      ServiceRecord replaced = null;

      for (int i = this._records.length - 1; i >= 0; i--) {
         ServiceRecord sr2 = this._records[i];
         if (sr2 != sr && sr2.isDuplicate(sr, -1, null, null, null, null, -1)) {
            if (replaced == null && sr.getType() == sr2.getType()) {
               ServiceRecord originalSR = null;
               if (sr2.getLastUpdated() >= sr.getLastUpdated()) {
                  int index = Arrays.getIndex(this._records, sr2);
                  if (index < i) {
                     i--;
                  }

                  this.expungeRecord(index);
                  sr.setId(sr2.getId());
                  originalSR = new ServiceRecord();
                  sr.copyInto(originalSR);
                  originalSR.setId(sr.getId());
                  sr2.copyInto(sr);
                  sr.setDirty(false);
                  sr.setLastUpdated(sr2.getLastUpdated());
                  replaced = sr;
               } else {
                  int index = Arrays.getIndex(this._records, sr);
                  if (index < i) {
                     i--;
                  }

                  this.expungeRecord(index);
                  originalSR = new ServiceRecord();
                  sr2.copyInto(originalSR);
                  originalSR.setId(sr2.getId());
                  sr.copyInto(sr2);
                  sr2.setDirty(false);
                  sr2.setLastUpdated(sr.getLastUpdated());
                  replaced = sr2;
               }

               this.doCommit();
               this.xmitSBEvent(8288627527798139133L, replaced, originalSR);
            } else {
               this.expungeRecord(i);
            }
         }
      }

      if (replaced == null) {
         this.doCommit();
         if (sr.getId() == -1) {
            sr.setId(++this._nextRecordId);
            this.xmitSBEvent(-4220058463650496006L, sr, null);
         } else {
            this.xmitSBEvent(8288627527798139133L, sr, null);
         }

         sr.setDirty(false);
      }

      return replaced == null ? sr : replaced;
   }

   private final synchronized ServiceRecord addRecordInternal(ServiceRecord sr) {
      assertRRISignature();
      int type = sr.getType();
      ServiceRecord returnMe = sr;
      if (sr.getId() != -1) {
         return null;
      }

      if (!sr.isValid()) {
         return null;
      }

      if (type == 4) {
         return null;
      }

      if (type == 5) {
         sr.setType(0);
      } else if (type == 6) {
         sr.setType(0);
      }

      if (!this.isAllowedRecord(sr, -1, null, null, null)) {
         sr.setType(6);
      }

      if (!Arrays.contains(this._records, sr)) {
         sr.clearCryptoKey();
         sr.setDirty(true);
         this.countRecord(sr);
         Arrays.add(this._records, sr);
         returnMe = this.handleDuplicates(sr);
         this.doCommit();
      }

      return returnMe;
   }

   private final synchronized int processServiceBookData(DataBuffer dataBuffer, int source, String callerUID, String keyId, boolean autheticated) throws ServiceBook$DuplicateSecureServiceException, ServiceBook$SecurityViolationException {
      assertRRISignature();
      ServiceRecord[] serviceRecords = parse(dataBuffer, source);
      int size = serviceRecords != null ? serviceRecords.length : 0;

      for (int index = 0; index < size; index++) {
         ServiceRecord sr = serviceRecords[index];
         if (sr != null) {
            int resultCode = this.securityCheckIncomingServiceRecord(sr, source, callerUID, keyId, autheticated);
            switch (resultCode) {
               case 0:
                  this.addRecordInternal(sr);
                  break;
               case 3:
                  throw new ServiceBook$SecurityViolationException();
               case 8:
                  throw new ServiceBook$DuplicateSecureServiceException();
            }
         }
      }

      return size;
   }

   private final int securityCheckIncomingServiceRecord(ServiceRecord sr, int source, String callerUID, String keyId, boolean autheticated) {
      if (sr == null) {
         return 255;
      }

      if (!sr.isSecureService()) {
         ServiceRecord[] dups = this.findDuplicateRecords(sr, 0, null, null, null, null, -1);
         if (dups.length > 0) {
            for (int i = dups.length - 1; i >= 0; i--) {
               if (dups[i].isSecureService()) {
                  return 8;
               }

               if (dups[i].isWeakSecureService() && !sr.isWeakSecureService()) {
                  return 8;
               }
            }
         }
      }

      if (source != 3) {
         if (sr.isSecureService()) {
            if (!autheticated || source != 5 && source != 1) {
               return 3;
            }

            String targetUID = sr.getUid();
            if (source != 1 && StringUtilities.compareToIgnoreCase(targetUID, callerUID, 1701707776) != 0 && CryptoBlock.validateSenderByUid(keyId, targetUID)) {
               return 3;
            }
         }

         sr.setType(0);
      }

      if (sr.getCryptoKey() != null) {
         try {
            if (sr.getEncryptionMode() == 4) {
               CryptoBlock.addNonEnterpriseClassSymmetricKey(sr.getUid(), sr.getCryptoKey());
            } else if (source == 1) {
               CryptoBlock.addSymmetricKey(sr.getUid(), sr.getCryptoKey());
               return 0;
            }
         } finally {
            ;
         }
      }

      return 0;
   }

   private static final void assertRRISignature() {
      ControlledAccess.assertRRISignatures(false);
   }

   private final boolean authenticateSender(int source, String keyId, String callerUID) {
      if (source == 5) {
         String uidByKeyId = CryptoBlock.getUIDForKeyId(keyId);
         if (StringUtilities.compareToIgnoreCase(uidByKeyId, callerUID, 1701707776) != 0 && !CryptoBlock.validateSenderByUid(keyId, callerUID)) {
            return false;
         }
      }

      return true;
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final synchronized boolean processCommand(
      byte command,
      byte commandVersion,
      byte commandFlag,
      DataBuffer comandData,
      int source,
      String callerUID,
      String keyId,
      boolean authenticatedFlag,
      DataBuffer result
   ) {
      boolean ackRequested = false;
      int ack = 192;
      byte resultCode = 0;
      ackRequested = (commandFlag & 64) == 64;
      if (ackRequested) {
         result.writeByte(command);
      }

      try {
         if (_pendingTimestamp != 0 && System.currentTimeMillis() - _pendingTimestamp > 3600000) {
            _pendingTimestamp = 0;
            _pendingSwitchSourceUID = null;
            _pendingSwitchDestinationUID = null;
         }

         switch (command) {
            case -1:
               ack = 128;
               resultCode = 2;
               break;
            case 0:
            default:
               boolean var34 = false /* VF: Semaphore variable */;

               label446: {
                  label467: {
                     try {
                        var34 = true;
                        if (this.processServiceBookData(comandData, source, callerUID, keyId, authenticatedFlag) < 1) {
                           ack = 128;
                           resultCode = 1;
                           var34 = false;
                           break;
                        }

                        var34 = false;
                     } catch (ServiceBook$DuplicateSecureServiceException var35) {
                        var34 = false;
                        break label446;
                     } catch (ServiceBook$SecurityViolationException var36) {
                        var34 = false;
                        break label467;
                     } finally {
                        if (var34) {
                           ack = 128;
                           resultCode = 1;
                           break;
                        }
                     }

                     if (_pendingSwitchDestinationUID != null && StringUtilities.strEqual(_pendingSwitchDestinationUID, callerUID)) {
                        this.deleteService(_pendingSwitchSourceUID, (byte)1);
                        RIMGlobalMessagePoster.postGlobalEvent(-5256071285987383000L, 0, 0, _pendingSwitchDestinationUID, _pendingSwitchSourceUID);
                        _pendingSwitchDestinationUID = null;
                        _pendingSwitchSourceUID = null;
                        break;
                     }

                     RIMGlobalMessagePoster.postGlobalEvent(6213587377148297993L, 0, 0, callerUID, null);
                     break;
                  }

                  ack = 128;
                  resultCode = 3;
                  break;
               }

               ack = 128;
               resultCode = 8;
               break;
            case 1:
               if (source != 5 && source != 1) {
                  ack = 128;
                  resultCode = 3;
               } else {
                  String sourceUID = null;
                  String newUID = null;

                  for (int i = 0; i < 2; i++) {
                     byte stringId = comandData.readByte();
                     if (stringId == 1) {
                        sourceUID = new String(comandData.readByteArray());
                        sourceUID = StringUtilities.toLowerCase(sourceUID, 1701707776);
                     } else if (stringId == 2) {
                        newUID = new String(comandData.readByteArray());
                        newUID = StringUtilities.toLowerCase(newUID, 1701707776);
                     }
                  }

                  if (StringUtilities.compareToIgnoreCase(callerUID, newUID, 1701707776) != 0) {
                     ack = 128;
                     resultCode = 3;
                  } else if (source == 5 && !authenticatedFlag && !CryptoBlock.validateSenderByUid(keyId, sourceUID)) {
                     ack = 128;
                     resultCode = 3;
                  } else if ((_pendingSwitchSourceUID == null || StringUtilities.compareToIgnoreCase(_pendingSwitchSourceUID, sourceUID, 1701707776) == 0)
                     && (_pendingSwitchDestinationUID == null || StringUtilities.compareToIgnoreCase(_pendingSwitchDestinationUID, newUID, 1701707776) == 0)) {
                     if (sourceUID != null && newUID != null && StringUtilities.compareToIgnoreCase(sourceUID, newUID, 1701707776) != 0) {
                        ServiceRecord[] sourceSRS = this.findRecordsByUid(sourceUID);
                        if (sourceSRS != null && sourceSRS.length == 0) {
                        }

                        ServiceRecord[] destinationSRS = this.findRecordsByUid(newUID);
                        if (destinationSRS == null && destinationSRS.length > 0) {
                        }

                        if (!this.switchService(sourceUID, newUID)) {
                           ack = 128;
                           resultCode = 1;
                        } else {
                           _pendingSwitchSourceUID = sourceUID;
                           _pendingSwitchDestinationUID = newUID;
                           _pendingTimestamp = System.currentTimeMillis();
                        }
                     } else {
                        ack = 128;
                        resultCode = 7;
                     }
                  } else {
                     ack = 128;
                     resultCode = 7;
                  }
               }
               break;
            case 2:
               ack = 128;
               if (comandData.readByte() == 1) {
                  String serviceToDeleteUID = new String(comandData.readByteArray());
                  ServiceRecord[] records = this.findRecordsByUid(serviceToDeleteUID);
                  boolean hasSecureServices = false;

                  for (int i = 0; i < records.length; i++) {
                     if (records[i].getEncryptionMode() == 2) {
                        hasSecureServices = true;
                        break;
                     }
                  }

                  if (!hasSecureServices || authenticatedFlag && (source == 5 || source == 1)) {
                     if (this.deleteService(serviceToDeleteUID, (byte)3)) {
                        ack = 192;
                     }
                  } else {
                     resultCode = 3;
                  }
               }
               break;
            case 3:
               ack = 128;
               String serverUID = null;
               byte commandField = comandData.readByte();
               if (commandField == 1) {
                  ack = 192;
                  serverUID = new String(comandData.readByteArray());
                  serverUID = StringUtilities.toLowerCase(serverUID, 1701707776);
                  ServiceBook$ServiceStatus serviceStatus = null;
                  if (this._serviceStatuses.containsKey(serverUID)) {
                     serviceStatus = (ServiceBook$ServiceStatus)this._serviceStatuses.get(serverUID);
                  } else {
                     serviceStatus = new ServiceBook$ServiceStatus(serverUID);
                     this._serviceStatuses.put(serverUID, serviceStatus);
                  }

                  byte statusCommandField = comandData.readByte();
                  switch (statusCommandField) {
                     case 3:
                        int length = comandData.readCompressedInt();
                        DataBuffer statusFieldPayload = new DataBuffer();
                        statusFieldPayload.setData(comandData.getArray(), comandData.getArrayPosition(), length, comandData.isBigEndian());
                        comandData.skipBytes(length);

                        while (statusFieldPayload.available() > 0) {
                           byte statusField = statusFieldPayload.readByte();
                           int statusFieldLength = statusFieldPayload.readCompressedInt();
                           switch (statusField) {
                              case 1:
                                 int lastPIN = statusFieldPayload.readInt();
                                 if (statusFieldLength != 4) {
                                    statusFieldPayload.skipBytes(statusFieldLength);
                                 } else {
                                    if (DeviceInfo.getDeviceId() != lastPIN) {
                                       this._serverPINmappings.put(serverUID, new Integer(lastPIN));
                                    }

                                    serviceStatus.setLastPIN(lastPIN);
                                 }
                                 break;
                              default:
                                 statusFieldPayload.skipBytes(statusFieldPayload.readCompressedInt());
                           }
                        }
                  }

                  if (ack == 192) {
                     this.doCommit();
                  }
               }
         }

         if (ackRequested) {
            result.writeByte((byte)ack);
            result.writeCompressedInt(1);
            result.writeByte(resultCode);
            if (ack == 128) {
               return false;
            }
         }

         return true;
      } catch (Throwable var38) {
         if (ackRequested) {
            result.writeByte(128);
            String errorString = e.toString();
            result.writeCompressedInt(errorString.length() + 1);
            result.writeByte(255);
            result.writeChars(errorString);
         }

         return false;
      }
   }

   private final boolean deleteService(String serviceToDeleteUID, byte removeKeyFlag) {
      ServiceRecord[] records = this.findRecordsByUid(serviceToDeleteUID);

      for (int i = records.length - 1; i >= 0; i--) {
         this.removeRecord(records[i]);
      }

      if (removeKeyFlag != 255) {
         OTAKeyGenCrypto.removeSymmetricKey(serviceToDeleteUID, removeKeyFlag);
      }

      return true;
   }

   private final boolean switchService(String currentUID, String newUID) {
      return currentUID == null || newUID == null ? false : OTAKeyGenCrypto.moveKey(currentUID, newUID) || OTAKeyGenCrypto.getSymmetricKey(newUID) != null;
   }

   public static final ServiceRecord[] parse(DataBuffer buf, int source) {
      assertRRISignature();
      return ServiceRecord.parse(buf, source);
   }

   public static final ServiceBook getSB() {
      assertPermission(TraceBack.getCallingModule(0));
      if (_instance == null) {
         _instance = (ServiceBook)ApplicationRegistry.getApplicationRegistry().waitFor(-863050508581563378L);
      }

      return _instance;
   }

   private ServiceBook() {
      this._recordCounts = new int[7];
      this._brInProgress = false;
      this._nextRecordId = 0;
      this._records = new ServiceRecord[0];
      this._restrictions = new ServiceBook$SBRestriction[0];
      this._serviceStatuses = new Hashtable();
      this._CIDAttributes = new IntIntHashtable();
   }

   public static final ServiceBook init() {
      assertRRISignature();
      PersistentObject persist = RIMPersistentStore.getPersistentObject(-863050508581563378L);
      Object o = persist.getContents();
      ServiceBook sb;
      if (o == null) {
         sb = new ServiceBook();
      } else {
         sb = (ServiceBook)o;
      }

      if (o != sb) {
         persist.setContents(sb, 51);
         persist.commit();
      }

      Proxy p = Proxy.getInstance();
      p.addGlobalEventListener(sb);

      for (int i = sb._records.length - 1; i >= 0; i--) {
         HostRoutingTable hrt;
         if ((hrt = sb._records[i].getAttachedHrt()) != null) {
            hrt.init();
            hrt.setActiveIndex();
         }
      }

      return sb;
   }

   private final void expungeRecord(int index) {
      ServiceRecord sr = this._records[index];
      assertPermission(TraceBack.getCallingModule(0));
      Arrays.removeAt(this._records, index);
      this._recordCounts[sr.getLastCountedType()]--;
      this.doCommit();
      if (sr.getId() != -1) {
         this.xmitSBEvent(2522898683889177438L, sr, null);
      }
   }

   private final void xmitSBEvent(long guid, ServiceRecord rec, ServiceRecord oldRec) {
      if (!ApplicationControl.isXmitDisabled()) {
         RIMGlobalMessagePoster.postGlobalEvent(guid, rec != null ? rec.getId() : -1, 0, rec, oldRec);
      }
   }

   private static final void assertPermission(int moduleHandle) {
      if (!ControlledAccess.verifyCodeModuleSignature(moduleHandle, 51)
         && ApplicationControl.isExternalConnectionAllowed(null, true) == 1
         && ApplicationControl.isInternalConnectionAllowed(null, true) == 1) {
         throw new ControlledAccessException();
      }
   }
}

package net.rim.device.internal.deviceagent;

import java.util.Enumeration;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncCollectionStatusProvider;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.IDENInfo;
import net.rim.device.api.system.Memory;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC24;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.synchronization.AlwaysSyncCollection;
import net.rim.device.internal.synchronization.NoProtectedContentInCollection;
import net.rim.device.internal.synchronization.ota.adapters.OTASyncCollectionAdapter;
import net.rim.device.internal.synchronization.ota.api.SyncAgent;
import net.rim.device.internal.synchronization.ota.api.SyncAgentListener;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.LcdPowerSaving;
import net.rim.device.internal.system.Security;
import net.rim.vm.Array;
import net.rim.vm.PersistentInteger;

public class OutgoingDeviceAgentCollection
   extends DeviceAgentCollection
   implements DeviceAgent,
   DeviceAgentUid,
   SIMCardStatusListener,
   SyncAgentListener,
   AlwaysSyncCollection,
   NoProtectedContentInCollection,
   SyncCollectionStatusProvider {
   private OutgoingDeviceAgentCollection$CurrentStatus _currentStatus;
   private boolean _is413OrLaterBES;
   private static final long ID = -4762577470950986502L;
   private static final long CURRENT_STATUS_TIMER = 21600000L;
   private static final long DEVICE_AGENT_DB = -1904221448506136235L;

   private OutgoingDeviceAgentCollection() {
      super(-1904221448506136235L);
      int id = PersistentInteger.getId(-4762577470950986502L, 0);
      this.updateBESVersion();
      if (PersistentInteger.get(id) == 0) {
         this.collectModuleInformation();
         this.collectALXInformation();
         this.collectSBInformation();
         this.collectGeneralDeviceInformation();
         this.collectDeviceCapabilitiesInformation();
         this.collectCurrentDeviceInformation();
         PersistentInteger.set(id, 1);
      } else {
         this.remove(this.get(117440512));
      }

      this.launchStatusCollector(21600000);
      Proxy p = Proxy.getInstance();
      SIMCard.addListener(p, this);
      SyncAgent.getSingletonInstance().registerListener(this);
   }

   @Override
   public void addCollectionListener(Object listener) {
      super.addCollectionListener(listener);
      if (listener instanceof OTASyncCollectionAdapter) {
         this.updateSoftwareInfo();
      }
   }

   public static DeviceAgentCollection getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      DeviceAgentCollection deviceAgentCollection = (OutgoingDeviceAgentCollection)ar.getOrWaitFor(-1904221448506136235L);
      if (deviceAgentCollection == null) {
         deviceAgentCollection = new OutgoingDeviceAgentCollection();
         ar.put(-1904221448506136235L, deviceAgentCollection);
      }

      return deviceAgentCollection;
   }

   private void updateBESVersion() {
      String besVersion = ITPolicy.getString(21, 3);
      this._is413OrLaterBES = false;
      if (besVersion != null) {
         StringTokenizer tokenizer = (StringTokenizer)(new Object(besVersion, "."));
         String majorString = null;
         String minorString = null;
         String patchString = null;
         if (tokenizer.hasMoreElements()) {
            majorString = tokenizer.nextToken();
            if (tokenizer.hasMoreElements()) {
               minorString = tokenizer.nextToken();
               if (tokenizer.hasMoreElements()) {
                  patchString = tokenizer.nextToken();
               }
            }
         }

         try {
            if (majorString != null) {
               int value = Integer.valueOf(majorString);
               if (value > 4) {
                  this._is413OrLaterBES = true;
               } else if (value == 4 && minorString != null) {
                  value = Integer.valueOf(minorString);
                  if (value > 1) {
                     this._is413OrLaterBES = true;
                  } else if (value == 1 && patchString != null && Integer.valueOf(patchString) >= 3) {
                     this._is413OrLaterBES = true;
                     return;
                  }
               }
            }
         } finally {
            return;
         }
      }
   }

   private boolean isRIMModule(int handle) {
      String moduleName = CodeModuleManager.getModuleName(handle);
      return moduleName != null && StringUtilities.startsWithIgnoreCase(moduleName, "net_rim_");
   }

   private boolean isLibraryModule(int handle) {
      String moduleName = CodeModuleManager.getModuleName(handle);
      return this.isRIMModule(handle) && moduleName.indexOf("_resource__") != -1 ? false : CodeModuleManager.isLibrary(handle);
   }

   public String getOperatorName() {
      String operatorName = "";
      if (RadioInfo.getNetworkType() == 5) {
         int idenMCC = IDENInfo.getHomeMCC();
         int idenNDC = IDENInfo.getHomeNDC();
         if (idenMCC == 790 && idenNDC == 16) {
            operatorName = "NEXTEL";
         } else if (idenMCC == 770 && idenNDC == 864) {
            operatorName = "TELUS";
         }
      } else {
         int index = RadioInfo.getCurrentNetworkIndex();
         if (index > -1) {
            label42:
            try {
               operatorName = RadioInfo.getNetworkName(index);
            } finally {
               break label42;
            }
         }
      }

      if (operatorName == null) {
         operatorName = "";
      }

      return operatorName;
   }

   @Override
   public SyncConverter getSyncConverter() {
      return getInstance();
   }

   @Override
   public synchronized boolean addDeviceCapabilities(byte tag, byte[] info) {
      DeviceAgentModel model = (DeviceAgentModel)this.get(117440512);
      if (model == null) {
         model = this.createDeviceAgentModel((byte)7);
         ConverterUtilities.writeByteArray(model.getData(), tag, info);
         this.addSyncObject(model);
         return true;
      } else {
         DataBuffer newBuffer = this.removeTag(model.getData(), tag);
         ConverterUtilities.writeByteArray(newBuffer, tag, info);
         this.updateSyncObject(model, new DeviceAgentModel(117440512, newBuffer));
         return true;
      }
   }

   @Override
   public boolean setDeviceCapabilitiesFlag(byte tag, byte[] mask) {
      return this.modifyDeviceCapabilitiesFlag(tag, mask, true);
   }

   @Override
   public boolean clearDeviceCapabilitiesFlag(byte tag, byte[] mask) {
      return this.modifyDeviceCapabilitiesFlag(tag, mask, false);
   }

   private synchronized boolean modifyDeviceCapabilitiesFlag(byte tag, byte[] mask, boolean set) {
      byte[] capabilities = this.getDeviceCapabilities(tag);
      int length = mask.length;
      if (length > capabilities.length) {
         Array.resize(capabilities, length);
      }

      for (int i = 0; i < length; i++) {
         if (set) {
            capabilities[i] |= mask[i];
         } else {
            capabilities[i] = (byte)(capabilities[i] & ~mask[i]);
         }
      }

      return this.addDeviceCapabilities(tag, capabilities);
   }

   private String getBootRomVersionFromMetrics(byte[] metrics) {
      StringBuffer buffer = (StringBuffer)(new Object(7));
      String seperator = ".";
      buffer.append(metrics[11]);
      buffer.append(seperator);
      buffer.append(metrics[10]);
      buffer.append(seperator);
      buffer.append(metrics[9]);
      buffer.append(seperator);
      buffer.append(metrics[8]);
      return buffer.toString();
   }

   @Override
   public byte[] getDeviceCapabilities(byte param1) {
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
      // 00: bipush 1
      // 01: newarray 8
      // 03: dup
      // 04: bipush 0
      // 05: bipush 0
      // 06: bastore
      // 07: astore 2
      // 08: aload 0
      // 09: ldc_w 117440512
      // 0c: invokevirtual net/rim/device/internal/deviceagent/DeviceAgentCollection.get (I)Ljava/lang/Object;
      // 0f: checkcast net/rim/device/internal/deviceagent/DeviceAgentModel
      // 12: astore 3
      // 13: aload 3
      // 14: ifnull 6f
      // 17: aload 3
      // 18: invokevirtual net/rim/device/internal/deviceagent/DeviceAgentModel.getData ()Lnet/rim/device/api/util/DataBuffer;
      // 1b: astore 4
      // 1d: aload 4
      // 1f: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 22: istore 5
      // 24: aload 4
      // 26: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 29: aload 4
      // 2b: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 2e: ifne 4f
      // 31: aload 4
      // 33: invokestatic net/rim/device/api/synchronization/ConverterUtilities.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 36: istore 6
      // 38: iload 6
      // 3a: iload 1
      // 3b: if_icmpne 47
      // 3e: aload 4
      // 40: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 43: astore 2
      // 44: goto 4f
      // 47: aload 4
      // 49: invokestatic net/rim/device/api/synchronization/ConverterUtilities.skipField (Lnet/rim/device/api/util/DataBuffer;)V
      // 4c: goto 29
      // 4f: aload 4
      // 51: iload 5
      // 53: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 56: aload 2
      // 57: areturn
      // 58: astore 6
      // 5a: aload 4
      // 5c: iload 5
      // 5e: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 61: aload 2
      // 62: areturn
      // 63: astore 7
      // 65: aload 4
      // 67: iload 5
      // 69: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 6c: aload 7
      // 6e: athrow
      // 6f: aload 2
      // 70: areturn
      // try (22 -> 38): 43 null
      // try (22 -> 38): 49 null
      // try (43 -> 44): 49 null
      // try (49 -> 50): 49 null
   }

   @Override
   public synchronized boolean removeDeviceCapabilities(byte tag) {
      DeviceAgentModel model = (DeviceAgentModel)this.get(117440512);
      if (model != null) {
         DataBuffer newBuffer = this.removeTag(model.getData(), tag);
         this.updateSyncObject(model, new DeviceAgentModel(117440512, newBuffer));
      }

      return true;
   }

   private DataBuffer removeTag(DataBuffer buffer, int tag) {
      DataBuffer newBuffer = (DataBuffer)(new Object(false));
      buffer.rewind();

      try {
         while (!buffer.eof()) {
            int type = ConverterUtilities.getType(buffer);
            if (type == 100) {
               ConverterUtilities.writeByteArray(newBuffer, 100, ConverterUtilities.readByteArray(buffer));
            } else if (type == 1) {
               ConverterUtilities.writeInt(newBuffer, 1, ConverterUtilities.readInt(buffer));
            } else if (type != tag) {
               ConverterUtilities.writeByteArray(newBuffer, type, ConverterUtilities.readByteArray(buffer, true));
            } else {
               ConverterUtilities.skipField(buffer);
            }
         }
      } finally {
         return newBuffer;
      }

      return newBuffer;
   }

   @Override
   public synchronized DataBuffer getDeviceAgentInfo(byte param1) {
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
      // 00: iload 1
      // 01: bipush 7
      // 03: if_icmpeq 08
      // 06: aconst_null
      // 07: areturn
      // 08: new java/lang/Object
      // 0b: dup
      // 0c: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 0f: astore 2
      // 10: aload 0
      // 11: ldc_w 117440512
      // 14: invokevirtual net/rim/device/internal/deviceagent/DeviceAgentCollection.get (I)Ljava/lang/Object;
      // 17: checkcast net/rim/device/internal/deviceagent/DeviceAgentModel
      // 1a: astore 3
      // 1b: aload 3
      // 1c: ifnull 72
      // 1f: aload 3
      // 20: invokevirtual net/rim/device/internal/deviceagent/DeviceAgentModel.getData ()Lnet/rim/device/api/util/DataBuffer;
      // 23: astore 4
      // 25: aload 4
      // 27: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 2a: istore 5
      // 2c: aload 4
      // 2e: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 31: aload 4
      // 33: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 36: ifne 52
      // 39: aload 4
      // 3b: invokestatic net/rim/device/api/synchronization/ConverterUtilities.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 3e: istore 6
      // 40: aload 4
      // 42: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 45: astore 7
      // 47: aload 2
      // 48: iload 6
      // 4a: aload 7
      // 4c: invokestatic net/rim/device/api/util/TLEUtilities.writeDataField (Lnet/rim/device/api/util/DataBuffer;I[B)V
      // 4f: goto 31
      // 52: aload 4
      // 54: iload 5
      // 56: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 59: aload 2
      // 5a: areturn
      // 5b: astore 6
      // 5d: aload 4
      // 5f: iload 5
      // 61: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 64: aload 2
      // 65: areturn
      // 66: astore 8
      // 68: aload 4
      // 6a: iload 5
      // 6c: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 6f: aload 8
      // 71: athrow
      // 72: aload 2
      // 73: areturn
      // try (24 -> 38): 43 null
      // try (24 -> 38): 49 null
      // try (43 -> 44): 49 null
      // try (49 -> 50): 49 null
   }

   @Override
   public String getDatabaseName() {
      return "Handheld Agent";
   }

   @Override
   public String getSyncName() {
      return "Handheld Agent";
   }

   @Override
   public void cardReady() {
      this.updateSoftwareInfo();
   }

   @Override
   public void cardUpdated() {
      this.updateSoftwareInfo();
   }

   @Override
   public void cardInvalid(int reason, int subReason) {
   }

   @Override
   public void cardInserted() {
   }

   @Override
   public void cardFault(int reason) {
   }

   @Override
   public void smsEFFull() {
   }

   @Override
   public void responseDeleteSMS(int status, int packetId) {
   }

   @Override
   public void responseMarkSMSAsRead(int status, int packetId) {
   }

   private void updateSoftwareInfo() {
      if (!super._listeners.isEmpty()) {
         DeviceAgentModel currModel = (DeviceAgentModel)this.get(67108864);
         if (currModel == null) {
            this.addSyncObject(this.createDeviceAgentModel((byte)4));
            return;
         }

         this.updateSyncObject(currModel, this.createDeviceAgentModel((byte)4));
      }
   }

   private void launchStatusCollector(long time) {
      if (this._currentStatus != null) {
         this._currentStatus.stop();
      }

      this._currentStatus = new OutgoingDeviceAgentCollection$CurrentStatus(this);
      this._currentStatus.start(time);
   }

   @Override
   public void onSyncAgentEvent(int eventId, Object anObject) {
      if (eventId == 28) {
         this.checkAddedModuleInformation();
         this.checkDeletedModuleInformation();
         this.checkAddedALXInformation();
         this.checkDeletedALXInformation();
         this.updateSoftwareInfo();
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 978519096100388739L) {
         SyncManager.getInstance().syncImmediately(this);
      } else if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         boolean oldBESFlag = this._is413OrLaterBES;
         this.updateBESVersion();
         this.updateSoftwareInfo();
         if (oldBESFlag != this._is413OrLaterBES) {
            this.checkAddedModuleInformation();
            this.checkAddedALXInformation();
            this.checkDeletedModuleInformation();
            this.checkDeletedALXInformation();
         }
      } else if (guid == 256826950193107649L) {
         this.checkAddedModuleInformation();
         this.checkAddedALXInformation();
      } else if (guid == -4232371946002803201L) {
         this.checkDeletedModuleInformation();
         this.checkDeletedALXInformation();
      } else if (guid == -4220058463650496006L) {
         ServiceRecord sr = ServiceBook.getSB().getRecordById(data0);
         if (sr != null) {
            this.addSyncObject(this.createServiceBookInformation(sr));
         }
      } else if (guid == 2522898683889177438L) {
         ServiceRecord sr = ServiceBook.getSB().getRecordById(data0);
         if (sr != null) {
            this.removeSyncObject(this.createServiceBookInformation(sr));
         } else if (object0 instanceof ServiceRecord) {
            this.removeSyncObject(this.createServiceBookInformation((ServiceRecord)object0));
         }
      } else if (guid == 8288627527798139133L) {
         this.handleSBChangedEvent(object0, object1);
      } else if (guid == -1789715216180579536L) {
         this.updateSoftwareInfo();
      } else if (guid == -5907912590164645220L && data0 == 101) {
         DeviceAgentModel model = (DeviceAgentModel)IncomingDeviceAgentCollection.getInstance().get(data1);
         if (model != null) {
            DataBuffer data = model.getData();
            data.rewind();

            try {
               while (data.available() > 0) {
                  int field = ConverterUtilities.getType(data);
                  switch (field) {
                     case 3:
                        this.launchStatusCollector(ConverterUtilities.readInt(data) * 1000);
                        break;
                     default:
                        ConverterUtilities.skipField(data);
                  }
               }
            } finally {
               return;
            }
         } else {
            this.launchStatusCollector(21600000);
         }
      }
   }

   @Override
   public SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      return dataBuffer.available() > 0 ? new DeviceAgentModel(uid, dataBuffer) : null;
   }

   private void collectGeneralDeviceInformation() {
      if (this.get(50331648) == null) {
         this.addSyncObject(this.createDeviceAgentModel((byte)3));
      }

      if (this.get(67108864) == null) {
         this.addSyncObject(this.createDeviceAgentModel((byte)4));
      }
   }

   private void collectDeviceCapabilitiesInformation() {
      this.addSyncObject(this.createDeviceAgentModel((byte)7));
   }

   private void collectCurrentDeviceInformation() {
      DeviceAgentModel oldModel = (DeviceAgentModel)this.get(83886080);
      DeviceAgentModel newModel = this.createDeviceAgentModel((byte)5);
      if (oldModel == null) {
         this.addSyncObject(newModel);
      } else {
         this.updateSyncObject(oldModel, newModel);
      }
   }

   private void collectSBInformation() {
      ServiceRecord[] srs = ServiceBook.getSB().getRecords();

      for (int i = srs.length - 1; i >= 0; i--) {
         this.addSyncObject(this.createServiceBookInformation(srs[i]));
      }
   }

   private DeviceAgentModel createServiceBookInformation(ServiceRecord sr) {
      byte[] tableID = new byte[]{6};
      int time = (int)(System.currentTimeMillis() / 1000);
      String recordId = ((StringBuffer)(new Object())).append(sr.getUid()).append(sr.getCid()).append(sr.getDataSourceId()).toString();
      byte[] appData = sr.getApplicationData();
      DeviceAgentModel model = new DeviceAgentModel(100663296 | CRC24.update(13501623, recordId.getBytes()));
      DataBuffer data = model.getData();
      ConverterUtilities.writeByteArray(data, 100, tableID);
      ConverterUtilities.writeInt(data, 1, time);
      ConverterUtilities.writeStringSmart(data, 2, sr.getName());
      ConverterUtilities.writeStringSmart(data, 3, sr.getUid());
      ConverterUtilities.writeStringSmart(data, 4, sr.getCid());
      if (appData != null) {
         ConverterUtilities.writeByteArray(data, 5, appData);
      }

      if (sr.getDataSourceId() != null) {
         ConverterUtilities.writeStringSmart(data, 8, sr.getDataSourceId());
      }

      if (sr.getUserId() != -1) {
         ConverterUtilities.writeInt(data, 7, sr.getUserId());
      }

      ConverterUtilities.writeInt(data, 6, sr.getEncryptionMode());
      ConverterUtilities.writeInt(data, 9, sr.getServiceIdentifierValue());
      return model;
   }

   private void collectModuleInformation() {
      int[] handle = CodeModuleManager.getModuleHandles();

      for (int i = handle.length - 1; i >= 0; i--) {
         try {
            int uid = this.getModuleUID(handle[i]);
            DeviceAgentModel object = this.createModuleInformation(handle[i], uid);
            if (object != null) {
               this.addSyncObject(object);
            }
         } finally {
            continue;
         }
      }
   }

   private int getModuleUID(int handle) {
      return 16777216 | CRC24.update(13501623, CodeModuleManager.getModuleHash(handle));
   }

   private IntVector getSnapshotOfCurrentModules() {
      IntVector uids = (IntVector)(new Object());
      int[] handles = CodeModuleManager.getModuleHandles();

      for (int i = handles.length - 1; i >= 0; i--) {
         try {
            uids.addElement(this.getModuleUID(handles[i]));
         } finally {
            continue;
         }
      }

      uids.trimToSize();
      return uids;
   }

   private void checkDeletedModuleInformation() {
      SyncObject[] objs = this.getSyncObjects();
      IntVector uids = this.getSnapshotOfCurrentModules();

      for (int i = objs.length - 1; i >= 0; i--) {
         int uid = objs[i].getUID();
         if (uid >> 24 == 1 && !uids.contains(uid)) {
            this.removeSyncObject(objs[i]);
         }
      }
   }

   private void checkAddedModuleInformation() {
      int[] handles = CodeModuleManager.getModuleHandles();

      for (int i = handles.length - 1; i >= 0; i--) {
         try {
            int uid = this.getModuleUID(handles[i]);
            if (this.get(uid) == null) {
               DeviceAgentModel object = this.createModuleInformation(handles[i], uid);
               if (object != null) {
                  this.addSyncObject(object);
               }
            }
         } finally {
            continue;
         }
      }
   }

   private DeviceAgentModel createModuleInformation(int handle, int uid) {
      if ((CodeModuleManager.getModuleFlags(handle) & 2) == 0 && !this._is413OrLaterBES && this.isRIMModule(handle) && !this.isLibraryModule(handle)) {
         return null;
      }

      byte[] tableID = new byte[]{1};
      int time = (int)(System.currentTimeMillis() / 1000);
      DeviceAgentModel model = new DeviceAgentModel(uid);
      DataBuffer data = model.getData();
      ConverterUtilities.writeByteArray(data, 100, tableID);
      ConverterUtilities.writeInt(data, 1, time);
      ConverterUtilities.writeStringSmart(data, 2, CodeModuleManager.getModuleName(handle));
      ConverterUtilities.writeStringSmart(data, 3, CodeModuleManager.getModuleVersion(handle));
      ConverterUtilities.writeInt(data, 4, CodeModuleManager.getModuleCodeSize(handle));
      ConverterUtilities.writeInt(data, 5, CodeModuleManager.getModuleFlags(handle));
      ConverterUtilities.writeInt(data, 7, this.isModuleSignedByRIM(handle));
      return model;
   }

   private void collectALXInformation() {
      CodeModuleGroup[] group = CodeModuleGroupManager.loadAll();
      if (group != null) {
         for (int i = 0; i < group.length; i++) {
            this.addSyncObject(this.createALXInformation(group[i]));
         }
      }
   }

   private IntVector getSnapshotOfCurrentALX() {
      IntVector uids = (IntVector)(new Object());
      CodeModuleGroup[] group = CodeModuleGroupManager.loadAll();
      if (group == null) {
         return null;
      }

      for (int i = group.length - 1; i >= 0; i--) {
         uids.addElement(33554432 | CRC24.update(13501623, CodeModuleGroupManager.getGroupData(group[i].getHandle())));
      }

      uids.trimToSize();
      return uids;
   }

   private void checkDeletedALXInformation() {
      SyncObject[] objs = this.getSyncObjects();
      IntVector uids = this.getSnapshotOfCurrentALX();
      if (uids != null) {
         for (int i = objs.length - 1; i >= 0; i--) {
            int uid = objs[i].getUID();
            if (uid >> 24 == 2 && !uids.contains(uid)) {
               this.removeSyncObject(objs[i]);
            }
         }
      }
   }

   private void checkAddedALXInformation() {
      CodeModuleGroup[] group = CodeModuleGroupManager.loadAll();
      if (group != null) {
         for (int i = 0; i < group.length; i++) {
            int uid = 33554432 | CRC24.update(13501623, CodeModuleGroupManager.getGroupData(group[i].getHandle()));
            if (this.get(uid) == null) {
               this.addSyncObject(this.createALXInformation(group[i]));
            }
         }
      }
   }

   private DeviceAgentModel createALXInformation(CodeModuleGroup alx) {
      byte[] tableID = new byte[]{2};
      int time = (int)(System.currentTimeMillis() / 1000);
      DeviceAgentModel model = new DeviceAgentModel(33554432 | CRC24.update(13501623, CodeModuleGroupManager.getGroupData(alx.getHandle())));
      DataBuffer data = model.getData();
      ConverterUtilities.writeByteArray(data, 100, tableID);
      ConverterUtilities.writeInt(data, 1, time);
      ConverterUtilities.writeStringSmart(data, 2, alx.getName());
      ConverterUtilities.writeInt(data, 3, alx.getFlags());
      ConverterUtilities.writeStringSmart(data, 4, alx.getFriendlyName());
      ConverterUtilities.writeStringSmart(data, 5, alx.getDescription());
      ConverterUtilities.writeStringSmart(data, 6, alx.getVersion());
      ConverterUtilities.writeStringSmart(data, 7, alx.getVendor());
      ConverterUtilities.writeStringSmart(data, 8, alx.getCopyright());
      ConverterUtilities.writeStringSmart(data, 9, this.getEnumerationList(alx, (byte)9));
      ConverterUtilities.writeStringSmart(data, 10, this.getEnumerationList(alx, (byte)10));
      return model;
   }

   private String getEnumerationList(CodeModuleGroup alx, byte tag) {
      String list = "";
      Enumeration enumeration = null;
      switch (tag) {
         case 8:
            break;
         case 9:
            enumeration = alx.getDependencies();
            break;
         case 10:
         default:
            enumeration = alx.getModules();
      }

      if (enumeration != null) {
         while (enumeration.hasMoreElements()) {
            String name = (String)enumeration.nextElement();
            list = ((StringBuffer)(new Object())).append(list).append(name).append(';').toString();
         }
      }

      return list;
   }

   private int isModuleSignedByRIM(int moduleHandle) {
      CodeSigningKey key = CodeSigningKey.getBuiltInKey(51);
      return CodeModuleManager.verifySignature(moduleHandle, key.getSignerIdAsInt(), key.getPublicKey()) ? 1 : 0;
   }

   private DeviceAgentModel createDeviceAgentModel(byte uid) {
      byte[] tableID = new byte[]{uid};
      int time = (int)(System.currentTimeMillis() / 1000);
      DeviceAgentModel model = new DeviceAgentModel(uid << 24);
      DataBuffer data = model.getData();
      ConverterUtilities.writeByteArray(data, 100, tableID);
      ConverterUtilities.writeInt(data, 1, time);
      switch (uid) {
         case 2:
            break;
         case 3:
         default:
            ConverterUtilities.writeInt(data, 2, InternalServices.getHardwareID());
            ConverterUtilities.writeStringSmart(data, 3, DeviceInfo.getDeviceName());
            ConverterUtilities.writeInt(data, 4, Branding.getVendorId());
            ConverterUtilities.writeInt(data, 5, this.getTotalFlash());
            ConverterUtilities.writeInt(data, 6, LcdPowerSaving.getPartialColourDepth());
            ConverterUtilities.writeStringSmart(data, 7, this.getSupportedBands());
            ConverterUtilities.writeStringSmart(data, 8, this.getBootRomVersionFromMetrics(InternalServices.getBootRomMetrics()));
            ConverterUtilities.writeInt(data, 9, InternalServices.isDeviceSecure() ? 1 : 0);
            ConverterUtilities.writeInt(data, 11, Display.getHorizontalResolution());
            ConverterUtilities.writeInt(data, 12, Display.getVerticalResolution());
            ConverterUtilities.writeInt(data, 13, Display.getHeight());
            ConverterUtilities.writeInt(data, 14, Display.getWidth());
            ConverterUtilities.writeStringSmart(data, 15, this.getNetworkType());
            ConverterUtilities.writeStringSmart(data, 16, getPIN());
            ConverterUtilities.writeStringSmart(data, 17, this.getSerialNumber());
            break;
         case 4:
            String policy = ITPolicy.getString(5);
            ConverterUtilities.writeStringSmart(data, 2, DeviceInfo.getPlatformVersion());
            String applicationVersion = ApplicationDescriptor.currentApplicationDescriptor().getVersion();
            ConverterUtilities.writeStringSmart(data, 3, applicationVersion);
            String operatorName = this.getOperatorName();
            ConverterUtilities.writeStringSmart(data, 5, operatorName);
            if (Phone.isSupported()) {
               label67:
               try {
                  ConverterUtilities.writeStringSmart(data, 6, Phone.getInstance().getNumber(0));
               } finally {
                  break label67;
               }
            }

            ConverterUtilities.writeInt(data, 7, Security.getInstance().isPasswordEnabled() ? 1 : 0);
            if (policy != null) {
               ConverterUtilities.writeStringSmart(data, 8, policy);
            }

            ConverterUtilities.writeInt(data, 9, (int)(ITPolicyInternal.getProcessedTimeStamp() / 1000));
            if (DirectConnect.isSupported()) {
               ConverterUtilities.writeStringSmart(data, 10, DirectConnect.getUFMI());
            }

            int cldcHandle = CodeModuleManager.getModuleHandle("net_rim_cldc");
            if (cldcHandle != 0) {
               String cldcVersion = CodeModuleManager.getModuleVersion(cldcHandle);
               ConverterUtilities.writeStringSmart(data, 11, cldcVersion == null ? "" : cldcVersion);
            }

            ConverterUtilities.writeStringSmart(data, 12, Locale.getDefaultForSystem().toString());
            break;
         case 5:
            ConverterUtilities.writeInt(data, 2, RadioInfo.getSignalLevel());
            ConverterUtilities.writeInt(data, 3, Memory.getFlashFree());
            ConverterUtilities.writeInt(data, 4, DeviceInfo.getBatteryLevel());
            ConverterUtilities.writeInt(data, 5, (int)(InternalServices.getUptime() / 1000));
      }

      return model;
   }

   private int getTotalFlash() {
      int nRoundingGranularityInBytes = 0;
      int nEstimatedOSSizeInSectors = 0;
      int hwId = InternalServices.getHardwareID();
      switch (RadioInfo.getNetworkType()) {
         case 2:
            nRoundingGranularityInBytes = 1048576;
            break;
         case 3:
         default:
            nEstimatedOSSizeInSectors = 32;
            nRoundingGranularityInBytes = 8388608;
            break;
         case 4:
         case 7:
            nEstimatedOSSizeInSectors = 32;
            if (hwId == 469763332) {
               nRoundingGranularityInBytes = 16777216;
            } else {
               nRoundingGranularityInBytes = 2097152;
            }
            break;
         case 5:
            nEstimatedOSSizeInSectors = 16;
            nRoundingGranularityInBytes = 8388608;
            break;
         case 6:
            nEstimatedOSSizeInSectors = 32;
            nRoundingGranularityInBytes = 16777216;
      }

      int nRawEstimateInBytes = nEstimatedOSSizeInSectors * 64 * 1024 + Memory.getFlashTotal();
      return nRoundingGranularityInBytes * ((nRawEstimateInBytes + nRoundingGranularityInBytes - 1) / nRoundingGranularityInBytes);
   }

   private void handleSBChangedEvent(Object object0, Object object1) {
      if (object0 != null && object1 != null && object0 instanceof ServiceRecord && object1 instanceof ServiceRecord) {
         ServiceRecord newSR = (ServiceRecord)object0;
         ServiceRecord oldSR = (ServiceRecord)object1;
         if (this.serviceBooksDiffer(newSR, oldSR)) {
            this.removeSyncObject(this.createServiceBookInformation(oldSR));
            this.addSyncObject(this.createServiceBookInformation(newSR));
         }
      }
   }

   private boolean serviceBooksDiffer(ServiceRecord sr1, ServiceRecord sr2) {
      return !StringUtilities.strEqual(sr1.getCid(), sr2.getCid())
         || !StringUtilities.strEqual(sr1.getUid(), sr2.getUid())
         || !StringUtilities.strEqual(sr1.getName(), sr2.getName())
         || sr1.getUserId() != sr2.getUserId()
         || !StringUtilities.strEqual(sr1.getDataSourceId(), sr2.getDataSourceId())
         || sr1.getEncryptionMode() != sr2.getEncryptionMode()
         || sr1.getServiceIdentifierValue() != sr2.getServiceIdentifierValue()
         || sr1.getApplicationData() != null ^ sr2.getApplicationData() != null
         || !Arrays.equals(sr1.getApplicationData(), sr2.getApplicationData());
   }

   private void addBand(StringBuffer strBuf, String s) {
      if (strBuf.length() > 0) {
         strBuf.append(", ");
      }

      strBuf.append(s);
   }

   private String getSupportedBands() {
      int bands = RadioInfo.getSupportedBands();
      StringBuffer strBuf = (StringBuffer)(new Object());
      if ((bands & 256) != 0) {
         this.addBand(strBuf, "GSM 850");
      }

      if ((bands & 4) != 0) {
         this.addBand(strBuf, "GSM 900");
      }

      if ((bands & 8) != 0) {
         this.addBand(strBuf, "GSM 1800");
      }

      if ((bands & 16) != 0) {
         this.addBand(strBuf, "GSM 1900");
      }

      if ((bands & 32) != 0) {
         this.addBand(strBuf, "CDMA 800");
      }

      if ((bands & 64) != 0) {
         this.addBand(strBuf, "CDMA 1900");
      }

      if ((bands & 128) != 0) {
         this.addBand(strBuf, "iDEN 800");
      }

      return strBuf.toString();
   }

   private String getNetworkType() {
      switch (RadioInfo.getNetworkType()) {
         case 2:
            return "UNKNOWN";
         case 3:
         default:
            return "GPRS";
         case 4:
            return "CDMA";
         case 5:
            return "IDEN";
         case 6:
            return "802.11";
         case 7:
            return "3G";
      }
   }

   private String getSerialNumber() {
      switch (RadioInfo.getNetworkType()) {
         case 2:
            return "UNKNOWN";
         case 3:
         case 6:
         case 7:
            return GPRSInfo.imeiToString(GPRSInfo.getIMEI());
         case 4:
         default:
            return Integer.toHexString(CDMAInfo.getESN());
         case 5:
            return IDENInfo.imeiToString(IDENInfo.getIMEI());
      }
   }

   private static String getPIN() {
      int pin = DeviceInfo.getDeviceId();
      return Integer.toHexString(pin);
   }

   @Override
   public boolean isWritableForSerialSync() {
      return false;
   }

   @Override
   public boolean isReadableForSerialSync() {
      return true;
   }

   @Override
   public boolean isWritableForOTASL() {
      return true;
   }

   @Override
   public int getOTASLControlMask() {
      return 0;
   }
}

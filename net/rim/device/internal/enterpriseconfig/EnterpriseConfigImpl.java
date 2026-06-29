package net.rim.device.internal.enterpriseconfig;

import net.rim.device.api.enterpriseconfig.EnterpriseConfig;
import net.rim.device.api.enterpriseconfig.EnterpriseConfigRecord;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncPriorityAndDependencyProvider;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionSchemaProvider;
import net.rim.device.api.synchronization.SyncCollectionStatusProvider;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.utility.framework.SimplePersistentEncryptedSyncCollection;
import net.rim.device.apps.api.utility.framework.SimplePersistentSyncCollection$SimpleData;

public final class EnterpriseConfigImpl
   extends SimplePersistentEncryptedSyncCollection
   implements EnterpriseConfig,
   OTASyncCapable,
   SyncConverter,
   SyncCollectionSchemaProvider,
   OTASyncPriorityAndDependencyProvider,
   SyncCollectionStatusProvider {
   private SimplePersistentSyncCollection$SimpleData _data = (SimplePersistentSyncCollection$SimpleData)this._persistentObject.getContents();
   private static final long PERSISTED_ENTERPRISE_CONFIGURATION = 1348834229778161606L;
   private static ResourceBundleFamily _resourceBundle = ResourceBundle.getBundle(457834568927961098L, "net.rim.device.apps.internal.resource.EnterpriseConfig");
   private static final int ENTERPRISE_CONFIGURATION_INITIAL_SIZE = 16;
   private static EnterpriseConfigImpl _instance;

   public static final void libMain(String[] args) {
      EnterpriseConfig enterpriseConfig = getInstance();
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(enterpriseConfig);
      }
   }

   private EnterpriseConfigImpl() {
      super(new EnterpriseConfigImpl$EnterpriseConfigComparator(), 1348834229778161606L);
      this.initialize();
      this.commonCtorEpilogue();
   }

   public static final EnterpriseConfig getInstance() {
      if (_instance == null) {
         ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
         _instance = (EnterpriseConfigImpl)registry.getOrWaitFor(5083252457608156518L);
         if (_instance == null) {
            _instance = new EnterpriseConfigImpl();
            registry.put(5083252457608156518L, _instance);
            SyncManager var1 = SyncManager.getInstance();
         }
      }

      return _instance;
   }

   private final synchronized void initialize() {
      if (this._data == null) {
         this._data = new SimplePersistentSyncCollection$SimpleData(16);
         super._persistentObject.setContents(this._data, 51);
         this.commit();
      }

      this.initList(this._data._items, 1);
   }

   @Override
   public final EnterpriseConfigRecord createEnterpriseConfigRecord(int uid, byte[] data) {
      return new EnterpriseConfigRecordImpl(uid, data);
   }

   @Override
   public final EnterpriseConfigRecord[] getRecordsByTableId(byte tableId) {
      EnterpriseConfigRecord[] result = new EnterpriseConfigRecord[0];

      for (int i = 0; i < super._elements.size(); i++) {
         EnterpriseConfigRecord record = (EnterpriseConfigRecord)super._elements.elementAt(i);
         if (record.getTableId() == tableId) {
            Arrays.add(result, record);
         }
      }

      return result;
   }

   @Override
   protected final String getContentProtectionEnabledMessage() {
      return _resourceBundle.getString(0);
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final String getSyncName() {
      return "Enterprise Configuration";
   }

   @Override
   public final String getSyncName(Locale locale) {
      ResourceBundle bundle = _resourceBundle.getBundle(locale);
      return bundle != null ? bundle.getString(0) : null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   protected final void clearPersistentData() {
      this._data = null;
      this.initialize();
   }

   @Override
   protected final void syncTransactionStopped() {
      if (super._syncRemoveAllDone) {
         int size = this.size();
         if (size > 0) {
            for (int i = 0; i < size; i++) {
               super._listenerManager.fireElementAdded(this, this.getAt(i));
            }
         }
      }

      super.syncTransactionStopped();
   }

   @Override
   public final boolean convert(SyncObject param1, DataBuffer param2, int param3) {
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
      // 00: aload 1
      // 01: dup
      // 02: instanceof net/rim/device/api/enterpriseconfig/EnterpriseConfigRecord
      // 05: ifne 0c
      // 08: pop
      // 09: goto 5d
      // 0c: checkcast net/rim/device/api/enterpriseconfig/EnterpriseConfigRecord
      // 0f: astore 4
      // 11: aload 4
      // 13: invokeinterface net/rim/device/api/enterpriseconfig/EnterpriseConfigRecord.getData ()Lnet/rim/device/api/util/DataBuffer; 1
      // 18: astore 5
      // 1a: aload 5
      // 1c: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 1f: istore 6
      // 21: aload 5
      // 23: bipush 0
      // 24: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 27: aload 5
      // 29: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 2c: newarray 8
      // 2e: astore 7
      // 30: aload 5
      // 32: aload 7
      // 34: invokevirtual net/rim/device/api/util/DataBuffer.readFully ([B)V
      // 37: aload 2
      // 38: aload 7
      // 3a: invokevirtual net/rim/device/api/util/DataBuffer.write ([B)V
      // 3d: aload 5
      // 3f: iload 6
      // 41: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 44: bipush 1
      // 45: ireturn
      // 46: astore 7
      // 48: aload 5
      // 4a: iload 6
      // 4c: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 4f: bipush 1
      // 50: ireturn
      // 51: astore 8
      // 53: aload 5
      // 55: iload 6
      // 57: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 5a: aload 8
      // 5c: athrow
      // 5d: bipush 0
      // 5e: ireturn
      // try (14 -> 27): 32 null
      // try (14 -> 27): 38 null
      // try (32 -> 33): 38 null
      // try (38 -> 39): 38 null
   }

   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      return dataBuffer.available() > 0 ? new EnterpriseConfigRecordImpl(uid, dataBuffer) : null;
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public final int getSyncPriority() {
      return 1;
   }

   @Override
   public final int getDependencyLevel() {
      return 1;
   }

   @Override
   public final boolean isWritableForSerialSync() {
      return false;
   }

   @Override
   public final boolean isReadableForSerialSync() {
      return false;
   }

   @Override
   public final boolean isWritableForOTASL() {
      return true;
   }

   @Override
   public final int getOTASLControlMask() {
      return 0;
   }
}

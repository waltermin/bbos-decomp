package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionWithVersion;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncParametersProvider;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.utility.framework.SimplePersistentEncryptedSyncCollection;
import net.rim.device.apps.api.utility.framework.SimplePersistentSyncCollection$SimpleData;
import net.rim.device.internal.synchronization.ota.util.Hash;

public final class EmailSettingCollectionImpl
   extends SimplePersistentEncryptedSyncCollection
   implements OTASyncCapable,
   OTASyncParametersProvider,
   CollectionEventSource,
   ReadableList,
   WritableSet,
   CollectionWithVersion,
   SyncConverter {
   private SimplePersistentSyncCollection$SimpleData _data = (SimplePersistentSyncCollection$SimpleData)this._persistentObject.getContents();
   private String _id;
   private static ContextObject _encodeContext = new ContextObject(50, 50, 19);
   private static ContextObject _decodeContext = _encodeContext.clone();
   private static final int EMAIL_SETTING_INITIAL_SIZE = 16;
   private static final String EMAIL_SETTING_DATA_NAME_STR = "net.rim.device.apps.internal.blackberryemail.emailsetting.EMAIL_SETTING_DATA_NAME.";
   private static long EMAIL_SETTING_DATA_NAME;
   private static final String EMAIL_SETTING_COLLECTION_ID_STR = "net.rim.device.apps.internal.blackberryemail.emailsetting.EMAIL_SETTING_COLLECTION_ID.";
   private static long EMAIL_SETTING_COLLECTION_ID;

   private EmailSettingCollectionImpl(String id) {
      super(new EmailSettingComparator(), EMAIL_SETTING_DATA_NAME);
      this._id = id;
      this.initialize();
      this.commonCtorEpilogue();
   }

   private final synchronized void initialize() {
      if (this._data == null) {
         this._data = new SimplePersistentSyncCollection$SimpleData(16);
         super._persistentObject.setContents(this._data, 51);
         this.commit();
      }

      this.initList(this._data._items, 1);
   }

   public static final EmailSettingCollectionImpl getInstance(String userId) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      EmailSettingCollectionImpl collection = (EmailSettingCollectionImpl)ar.getOrWaitFor(
         Hash.bytesToLong(("net.rim.device.apps.internal.blackberryemail.emailsetting.EMAIL_SETTING_COLLECTION_ID." + userId).getBytes())
      );
      if (collection == null) {
         EMAIL_SETTING_COLLECTION_ID = Hash.bytesToLong(
            ("net.rim.device.apps.internal.blackberryemail.emailsetting.EMAIL_SETTING_COLLECTION_ID." + userId).getBytes()
         );
         EMAIL_SETTING_DATA_NAME = Hash.bytesToLong(("net.rim.device.apps.internal.blackberryemail.emailsetting.EMAIL_SETTING_DATA_NAME." + userId).getBytes());
         collection = new EmailSettingCollectionImpl(userId);
         if (collection.size() == 0) {
            collection.add(new EmailSettingModelImpl(userId));
         }

         ar.put(EMAIL_SETTING_COLLECTION_ID, collection);
      }

      return collection;
   }

   @Override
   public final void add(Object o) {
      if (o instanceof EmailSettingModelImpl) {
         EmailSettingModelImpl m = (EmailSettingModelImpl)o;
         if (m._id == null) {
            m._id = this._id;
         }
      }

      super.add(o);
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "Email Settings - " + this._id;
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
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
   protected final String getContentProtectionEnabledMessage() {
      return "CONTENT_PROTECTION_OUT_OF_RESET";
   }

   @Override
   public final String getUserSystemId() {
      return null;
   }

   @Override
   public final String getDataSourceName() {
      return null;
   }

   @Override
   public final String getDatabaseName() {
      return "Email Settings";
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (object instanceof RIMModel) {
         SyncBuffer syncBuffer = new SyncBuffer(buffer, version, 0);
         RIMModel model = (RIMModel)object;
         return syncBuffer.addModel(model, _encodeContext);
      } else {
         return false;
      }
   }

   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      SyncBuffer syncBuffer = new SyncBuffer(dataBuffer, version, uid);
      synchronized (_decodeContext) {
         _decodeContext.put(255, syncBuffer);
         SyncObject result = (SyncObject)FactoryUtil.createInstance(1683565110580900081L, _decodeContext);
         _decodeContext.remove(255);
         return result;
      }
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }
}

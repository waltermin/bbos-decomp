package net.rim.device.apps.internal.blackberryemail.email.filters;

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
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.utility.framework.SimplePersistentEncryptedSyncCollection;
import net.rim.device.apps.api.utility.framework.SimplePersistentSyncCollection$SimpleData;
import net.rim.device.internal.synchronization.ota.util.Hash;

public final class EmailFilterCollectionImpl
   extends SimplePersistentEncryptedSyncCollection
   implements OTASyncCapable,
   OTASyncParametersProvider,
   CollectionEventSource,
   ReadableList,
   WritableSet,
   CollectionWithVersion,
   SyncConverter {
   private SimplePersistentSyncCollection$SimpleData _data = (SimplePersistentSyncCollection$SimpleData)this._persistentObject.getContents();
   private String _userId;
   private static ContextObjectWR _encodeContextWR = new ContextObjectWR(33, 33, 19);
   private static ContextObjectWR _decodeContextWR = new ContextObjectWR(33, 33, 19);
   private static final int FILTER_INITIAL_SIZE = 16;
   private static final String FILTER_DATA_NAME_STR = "net.rim.device.apps.internal.blackberryemail.email.filters.FILTER_DATA_NAME.";
   private static long FILTER_DATA_NAME;
   private static final String EMAIL_FILTER_COLLECTION_ID_STR = "net.rim.device.apps.internal.blackberryemail.email.filters.EMAIL_FILTER_COLLECTION_ID.";
   private static long EMAIL_FILTER_COLLECTION_ID;

   private EmailFilterCollectionImpl(String userId) {
      super(new EmailFilterComparator(), FILTER_DATA_NAME);
      this._userId = userId;
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

   public static final EmailFilterCollectionImpl getInstance(String userId) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      EmailFilterCollectionImpl collection = (EmailFilterCollectionImpl)ar.getOrWaitFor(
         Hash.bytesToLong(("net.rim.device.apps.internal.blackberryemail.email.filters.EMAIL_FILTER_COLLECTION_ID." + userId).getBytes())
      );
      if (collection == null) {
         EMAIL_FILTER_COLLECTION_ID = Hash.bytesToLong(
            ("net.rim.device.apps.internal.blackberryemail.email.filters.EMAIL_FILTER_COLLECTION_ID." + userId).getBytes()
         );
         FILTER_DATA_NAME = Hash.bytesToLong(("net.rim.device.apps.internal.blackberryemail.email.filters.FILTER_DATA_NAME." + userId).getBytes());
         collection = new EmailFilterCollectionImpl(userId);
         ar.put(EMAIL_FILTER_COLLECTION_ID, collection);
      }

      return collection;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "Email Filters - " + this._userId;
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
      return "Checking Content Protection of Email Filters";
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
      return "Email Filters";
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (object instanceof RIMModel) {
         SyncBuffer syncBuffer = new SyncBuffer(buffer, version, 0);
         RIMModel model = (RIMModel)object;
         return syncBuffer.addModel(model, _encodeContextWR.getContextObject());
      } else {
         return false;
      }
   }

   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      SyncBuffer syncBuffer = new SyncBuffer(dataBuffer, version, uid);
      ContextObject decodeContext = _decodeContextWR.getContextObject();
      synchronized (decodeContext) {
         decodeContext.put(255, syncBuffer);
         SyncObject result = (SyncObject)FactoryUtil.createInstance(-1388842558271364146L, decodeContext);
         decodeContext.remove(255);
         return result;
      }
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }
}

package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.wica.common.metadata.component.DataComponentDef;
import net.rim.wica.runtime.access.data.collections.StdCmpCollection;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.component.DataCollectionImpl;
import net.rim.wica.runtime.metadata.util.ValueResolver;
import net.rim.wica.runtime.util.LongVector;

class StdCmpCollectionImpl extends DataCollectionImpl implements KeyDataCollection, StdCmpCollection, BuiltinCollection {
   protected IntVector _handles;
   protected IntVector _deletedItems;
   protected IntVector _createdItems;
   protected IntVector _modifiedItems;
   protected IntVector _loadedItems;
   protected IntHashtable _intFieldHandlers;
   protected IntHashtable _booleanFieldHandlers;
   protected IntHashtable _longFieldHandlers;
   protected IntHashtable _objectFieldHandlers;
   protected LoadItemHelper _loadHelper;
   private EventService _eventService;
   protected boolean _ignoreCAPExternalAccess = false;
   protected boolean _overrideReadOnly;
   static Class class$net$rim$wica$runtime$event$EventService;

   protected void initCache() {
      this._modifiedItems = new IntVector(10, 5);
      this._createdItems = new IntVector(10, 5);
      this._deletedItems = new IntVector(10, 5);
      this._loadedItems = new IntVector(10, 5);
   }

   protected boolean isFieldWriteable(long dataHandle, int field) {
      return this._overrideReadOnly || this.getDef().getAccessType(field) == 268435456;
   }

   protected int getUID() {
      return UIDGenerator.getUID();
   }

   @Override
   public IntHashtable getIntFieldHandlers() {
      return this._intFieldHandlers;
   }

   @Override
   public IntHashtable getLongFieldHandlers() {
      return this._longFieldHandlers;
   }

   @Override
   public IntHashtable getObjectFieldHandlers() {
      return this._objectFieldHandlers;
   }

   @Override
   public IntHashtable getBooleanFieldHandlers() {
      return this._booleanFieldHandlers;
   }

   @Override
   public long create(Object pkey) {
      long handle = this.find(pkey);
      if (handle == -1) {
         handle = this.create();
      }

      return handle;
   }

   @Override
   public long find(Object pkey) {
      if (this.verifyAccessGranted() && pkey != null) {
         this.initMappings();
         int keyValue = (Integer)pkey;
         return this._handles.contains(keyValue) ? (long)super._defs.getId() << 32 | 4294967295L & keyValue : -1;
      } else {
         return -1;
      }
   }

   @Override
   public long[] findWhere(String expression, ValueResolver resolver) {
      if (!this.verifyAccessGranted()) {
         return new long[0];
      }

      LongVector findWhereResults = new LongVector();
      super._wiclet.findWhere(findWhereResults, this, this.retrieveAll(true), expression, resolver);
      findWhereResults.trimToSize();
      return findWhereResults.getArray();
   }

   @Override
   public int getCount() {
      this.initMappings();
      return this._handles.size();
   }

   @Override
   public void restoreKey(long handle, Object key) {
      int intHandle = this.getHandle(handle);
      this._handles.addElement(intHandle);
   }

   @Override
   public Object getPKey(long dataHandle) {
      this.initMappings();
      int handle = this.getHandle(dataHandle);
      if (this._handles.contains(handle)) {
         int[] keyFields = super._defs.getKeyFields();
         return this.getFieldValueAsObject(dataHandle, keyFields[0]);
      } else {
         return null;
      }
   }

   @Override
   public long[] retrieveAll(boolean bSort) {
      if (!this.verifyAccessGranted()) {
         return new long[0];
      }

      this.initMappings();
      long[] allHandles = null;
      int numItems = this._handles.size();
      if (numItems > 0) {
         allHandles = new long[numItems];
         int[] handles = this._handles.getArray();
         long defID = (long)super._defs.getId() << 32;

         for (int i = numItems - 1; i >= 0; i--) {
            allHandles[i] = defID | 4294967295L & handles[i];
         }
      } else {
         allHandles = new long[0];
      }

      if (bSort && allHandles != null && allHandles.length != 0) {
         Arrays.sort(allHandles, 0, allHandles.length - 1);
      }

      return allHandles;
   }

   @Override
   public void initHandles() {
      this._handles = this.uidsInExternalDB();
      if (this._handles == null) {
         this._handles = new IntVector(0);
      }
   }

   @Override
   public boolean isItemLoaded(long dataHandle) {
      int handle = this.getHandle(dataHandle);
      return this._loadedItems.contains(handle);
   }

   @Override
   public boolean loadItemFromDB(long dataHandle) {
      Object obj = this.getDBItemFromHandle(dataHandle);
      if (obj == null) {
         return false;
      }

      if (this._loadedItems.contains(this.getHandle(dataHandle))) {
         return true;
      }

      this._loadedItems.addElement(this.getHandle(dataHandle));
      this.onInitData(dataHandle);
      boolean ignoreTransactions = false;
      if (super._transactions != null) {
         ignoreTransactions = super._transactions.getIgnoreTransactions();
         super._transactions.setIgnoreTransactions(true);
      }

      this._overrideReadOnly = true;
      this.loadItem(dataHandle, obj);
      this._overrideReadOnly = false;
      if (super._transactions != null) {
         super._transactions.setIgnoreTransactions(ignoreTransactions);
      }

      return true;
   }

   @Override
   public void initMappings() {
      if (this._handles == null) {
         this.initHandles();
         this.initFieldHandlers();
      }
   }

   @Override
   public void resetCache() {
      this.resetInternalCache();
      this._modifiedItems = new IntVector(10, 5);
      this._createdItems = new IntVector(10, 5);
      this._deletedItems = new IntVector(10, 5);
      this._loadedItems = new IntVector(10, 5);
   }

   @Override
   public DataComponentDef getKeyDef() {
      return this.getDef();
   }

   @Override
   public void saveCreatedItems() {
      throw null;
   }

   @Override
   public void saveModifiedItems() {
      throw null;
   }

   @Override
   public void saveDeletedItems() {
      throw null;
   }

   @Override
   public IntVector uidsInExternalDB() {
      throw null;
   }

   @Override
   public void loadItem(long _1, Object _3) {
      throw null;
   }

   @Override
   public void initFieldHandlers() {
      throw null;
   }

   @Override
   public Object getDBItemFromHandle(long _1) {
      throw null;
   }

   private boolean verifyAccessGranted() {
      if (!this._ignoreCAPExternalAccess && super._wiclet.getContext().getExternalAccessType() == 0) {
         this._eventService.dispatchEvent(this, 605, 106);
         return false;
      } else {
         return true;
      }
   }

   private boolean verifyReadWriteAccessGranted() {
      if (!this._ignoreCAPExternalAccess && super._wiclet.getContext().getExternalAccessType() != 2) {
         this._eventService.dispatchEvent(this, 605, 106);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public boolean contains(long dataHandle) {
      this.initMappings();
      return super._defs.getId() == (int)(dataHandle >>> 32) && this._handles.contains(this.getHandle(dataHandle));
   }

   @Override
   public long create() {
      if (!this.verifyReadWriteAccessGranted()) {
         return -1;
      }

      this.initMappings();
      int uid = this.getUID();
      if (uid == -1) {
         return uid;
      }

      long handle = (long)super._defs.getId() << 32 | 4294967295L & uid;
      this.onInitData(handle);
      int[] keys = super._defs.getKeyFields();
      this._overrideReadOnly = true;
      this.setIntFieldValue(handle, keys[0], uid);
      this._overrideReadOnly = false;
      if (super._transactions != null) {
         super._transactions.created(handle);
      }

      this._loadedItems.addElement(uid);
      this._createdItems.addElement(uid);
      this._handles.addElement(uid);
      return handle;
   }

   public StdCmpCollectionImpl(WicletEx owner, DataComponentDef defs) {
      super(owner, defs, false);
      this.initCache();
      this._loadHelper = new LoadItemHelper(this);
      this._eventService = (EventService)owner.getRuntime()
         .getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         );
   }

   @Override
   public void setBooleanFieldValue(long dataHandle, int field, boolean value) {
      if (this.verifyReadWriteAccessGranted()) {
         if (this.isFieldWriteable(dataHandle, field)) {
            super.setBooleanFieldValue(dataHandle, field, value);
         }
      }
   }

   @Override
   public void setDoubleFieldValue(long dataHandle, int field, double value) {
      if (this.verifyReadWriteAccessGranted()) {
         if (this.isFieldWriteable(dataHandle, field)) {
            super.setDoubleFieldValue(dataHandle, field, value);
         }
      }
   }

   @Override
   public void setFieldValueFromObject(long dataHandle, int field, Object value) {
      if (this.verifyReadWriteAccessGranted()) {
         if (this.isFieldWriteable(dataHandle, field)) {
            super.setFieldValueFromObject(dataHandle, field, value);
         }
      }
   }

   @Override
   public void setIntFieldValue(long dataHandle, int field, int value) {
      if (this.verifyReadWriteAccessGranted()) {
         if (this.isFieldWriteable(dataHandle, field)) {
            super.setIntFieldValue(dataHandle, field, value);
         }
      }
   }

   @Override
   public void setLongFieldValue(long dataHandle, int field, long value) {
      if (this.verifyReadWriteAccessGranted()) {
         if (this.isFieldWriteable(dataHandle, field)) {
            super.setLongFieldValue(dataHandle, field, value);
         }
      }
   }

   @Override
   public void setObjectFieldValue(long dataHandle, int field, Object value) {
      if (this.verifyReadWriteAccessGranted()) {
         if (this.isFieldWriteable(dataHandle, field)) {
            super.setObjectFieldValue(dataHandle, field, value);
         }
      }
   }

   @Override
   public void setReferenceField(long dataHandle, int field, long reference) {
      if (this.verifyReadWriteAccessGranted()) {
         if (this.isFieldWriteable(dataHandle, field)) {
            super.setReferenceField(dataHandle, field, reference);
         }
      }
   }

   @Override
   public boolean getBooleanFieldValue(long dataHandle, int field) {
      if (!this.verifyAccessGranted()) {
         return false;
      } else {
         return this.isItemLoaded(dataHandle) ? super.getBooleanFieldValue(dataHandle, field) : this._loadHelper.getBooleanFieldValue(dataHandle, field);
      }
   }

   @Override
   public int getIntFieldValue(long dataHandle, int field) {
      if (!this.verifyAccessGranted()) {
         return -1;
      }

      this.initMappings();
      return this.isItemLoaded(dataHandle) ? super.getIntFieldValue(dataHandle, field) : this._loadHelper.getIntFieldValue(dataHandle, field);
   }

   @Override
   public long getLongFieldValue(long dataHandle, int field) {
      if (!this.verifyAccessGranted()) {
         return -1;
      }

      this.initMappings();
      return this.isItemLoaded(dataHandle) ? super.getLongFieldValue(dataHandle, field) : this._loadHelper.getLongFieldValue(dataHandle, field);
   }

   @Override
   public Object getObjectFieldValue(long dataHandle, int field) {
      if (!this.verifyAccessGranted()) {
         return null;
      }

      this.initMappings();
      Object retValue;
      if (this.isItemLoaded(dataHandle)) {
         boolean initialize = false;
         if (this.getDef().getFieldType(field) == 32774) {
            initialize = !this.isInnerVectorInitialized(dataHandle, field);
         }

         retValue = super.getObjectFieldValue(dataHandle, field);
         if (initialize) {
            this._loadHelper.initVector(retValue);
         }
      } else {
         retValue = this._loadHelper.getObjectFieldValue(dataHandle, field);
      }

      return retValue;
   }

   @Override
   public void save() {
      if (super._wiclet.getContext().getExternalAccessType() == 2) {
         for (int i = this._deletedItems.size() - 1; i >= 0; i--) {
            this._createdItems.removeElement(this._deletedItems.elementAt(i));
            this._modifiedItems.removeElement(this._deletedItems.elementAt(i));
         }

         if (this._createdItems.size() > 0) {
            this.saveCreatedItems();
            this._createdItems.removeAllElements();
         }

         if (this._modifiedItems.size() > 0) {
            this.saveModifiedItems();
            this._modifiedItems.removeAllElements();
         }

         if (this._deletedItems.size() > 0) {
            this.saveDeletedItems();
            this._deletedItems.removeAllElements();
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }

   @Override
   public boolean equals(long data1, long data2) {
      this.initMappings();
      int key1 = this.getHandle(data1);
      int key2 = this.getHandle(data2);
      return !this._handles.contains(key1) || !this._handles.contains(key2) ? false : key1 == key2;
   }

   @Override
   public void removeAll() {
      if (this.verifyReadWriteAccessGranted()) {
         if (super._transactions != null) {
            long[] handles = this.retrieveAll(false);

            for (int i = handles.length - 1; i >= 0; i--) {
               this.removeEx(handles[i]);
            }
         }

         this._handles.removeAllElements();
         this.resetInternalCache();
      }
   }

   @Override
   public void remove(long dataHandle) {
      if (this.verifyReadWriteAccessGranted()) {
         this.initMappings();
         if (this.removeEx(dataHandle)) {
            this._handles.removeElement(this.getHandle(dataHandle));
         }
      }
   }

   private boolean removeEx(long dataHandle) {
      int handle = this.getHandle(dataHandle);
      if (!this._handles.contains(handle)) {
         return false;
      }

      if (!this._createdItems.contains(handle) && !this._modifiedItems.contains(handle) && !this._loadedItems.contains(handle)) {
         if (super._transactions != null) {
            super._transactions.markDeleted(dataHandle);
         }

         this.newVersion(dataHandle);
      } else {
         super.remove(dataHandle);
      }

      this._deletedItems.addElement(handle);
      return true;
   }

   @Override
   public void restoreHandle(long handle) {
      int intHandle = this.getHandle(handle);
      this._deletedItems.removeElement(intHandle);
      if (this._createdItems.contains(intHandle) || this._modifiedItems.contains(intHandle) || this._loadedItems.contains(intHandle)) {
         super.restoreHandle(handle);
      }
   }

   @Override
   public long getReferenceField(long dataHandle, int field) {
      if (!this.verifyAccessGranted()) {
         return -1;
      }

      this.initMappings();
      long ref = this.getLongFieldValue(dataHandle, field);
      DataCollection dc = super._wiclet.getDataCollection(super._defs.getFieldReferenceType(field));
      if (ref == -1 || !dc.contains(ref)) {
         if (!(dc instanceof AccessInnerDataCollection)) {
            if (dc instanceof KeyDataCollection && ref != -1) {
               ref = -1;
               this.setLongFieldValue(dataHandle, field, ref);
            }
         } else {
            AccessInnerDataCollection aidc = (AccessInnerDataCollection)dc;
            boolean ignoreTransactions = false;
            if (super._transactions != null) {
               ignoreTransactions = super._transactions.getIgnoreTransactions();
               super._transactions.setIgnoreTransactions(true);
            }

            ref = aidc.create();
            aidc.addReference(ref, false);
            this._ignoreCAPExternalAccess = true;
            this.setReferenceField(dataHandle, field, ref);
            this._ignoreCAPExternalAccess = false;
            aidc.setOwnerComponent(ref, dataHandle, field);
            if (super._transactions != null) {
               super._transactions.setIgnoreTransactions(ignoreTransactions);
            }
         }
      }

      return ref;
   }

   @Override
   public void modified(long dataHandle, int field) {
      this.initMappings();
      int handle = this.getHandle(dataHandle);
      if (this._handles.contains(handle)) {
         super.modified(dataHandle, field);
         if (!this._createdItems.contains(handle)) {
            boolean itemLoaded = this.isItemLoaded(dataHandle);
            if (!itemLoaded) {
               itemLoaded = this.loadItemFromDB(dataHandle);
            }

            if (itemLoaded && !this._modifiedItems.contains(handle)) {
               this._modifiedItems.addElement(handle);
            }
         }
      }
   }
}

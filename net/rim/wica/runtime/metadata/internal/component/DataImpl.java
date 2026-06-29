package net.rim.wica.runtime.metadata.internal.component;

import net.rim.wica.runtime.metadata.component.Data;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.def.DataDefAccess;
import net.rim.wica.runtime.metadata.internal.transaction.TransactionManager;
import net.rim.wica.runtime.metadata.internal.util.PersistenceListener;
import net.rim.wica.runtime.persistence.PersStandaloneDataStruct;
import net.rim.wica.runtime.persistence.WicletStore;

public class DataImpl extends ComponentImpl implements Data, DataOwner {
   private boolean _save;
   private TransactionManager _transactions;
   private PersistenceListener _persistenceListener;
   static Class class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager;
   static Class class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener;

   protected boolean load() {
      if (this.isPersistable() && this._persistenceListener.isPersistenceReadable()) {
         WicletStore store = ((WicletEx)super._wiclet).getContext().getWicletStore();
         PersStandaloneDataStruct gData = (PersStandaloneDataStruct)store.loadData(super._defId);
         if (gData != null) {
            super._dataFields = gData.getDataFields();
            super._longData = gData.getLongData();
            Object[] tempRefObjects = gData.getRefObjects();
            if (tempRefObjects != null) {
               super._refObjects = new Object[tempRefObjects.length];
            }

            int numFields = this.getNumFields();

            for (int field = 0; field < numFields; field++) {
               if (this.isPersistable(field)) {
                  int fieldType = this.getFieldType(field);
                  if ((fieldType & 32768) != 0) {
                     int i = super._dataFields[field];
                     if (tempRefObjects[i] != null) {
                        Object o = ComponentImpl.createArray(super._wiclet, fieldType, this, super._handle, field, this, null, this.isPersistable());
                        ((BaseInnerVector)o).setArrayRef(tempRefObjects[i]);
                        super._refObjects[i] = o;
                     }
                  } else if (fieldType == 3) {
                     int i = super._dataFields[field];
                     super._refObjects[i] = tempRefObjects[i];
                  }
               } else {
                  this.setDefault(field);
               }
            }

            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isModified() {
      return this._save;
   }

   @Override
   public void save() {
      if (this._save && this.isPersistable() && this._persistenceListener.isPersistenceReadable()) {
         PersStandaloneDataStruct gData = new PersStandaloneDataStruct();
         gData.setDataFields(super._dataFields);
         gData.setLongData(super._longData);
         if (super._refObjects != null) {
            gData.initRefObjects(super._refObjects.length);
            int numFields = this.getNumFields();

            for (int field = 0; field < numFields; field++) {
               if (this.isPersistable(field)) {
                  int fieldType = this.getFieldType(field);
                  if ((fieldType & 32768) != 0) {
                     int i = super._dataFields[field];
                     if (super._refObjects[i] != null
                        && (fieldType != 32774 || super._wiclet.getDataCollection(this.getFieldReferenceType(field)).getDef().isPersistable())) {
                        ((BaseInnerVector)super._refObjects[i]).trimToSize();
                        gData.setRefObjectAt(((BaseInnerVector)super._refObjects[i]).getArrayRef(), i);
                     }
                  } else if (fieldType == 3) {
                     int i = super._dataFields[field];
                     gData.setRefObjectAt(super._refObjects[i], i);
                  }
               }
            }
         }

         WicletStore store = ((WicletEx)super._wiclet).getContext().getWicletStore();
         store.storeData(super._defId, gData);
         this._save = false;
      }
   }

   @Override
   public boolean isPersistable(int fieldId) {
      return ((DataDefAccess)super._defs).isPersistable(super._defId, fieldId);
   }

   @Override
   public void modified(long dataHandle, int field) {
      super.modified(dataHandle, field);
      if (this._transactions != null) {
         this._transactions.modified(dataHandle, field);
      }

      this._persistenceListener.modified(dataHandle, field);
      this._save = true;
   }

   @Override
   public boolean isPersistable() {
      return ((DataDefAccess)super._defs).isPersistable(super._defId);
   }

   public DataImpl(int def, WicletEx wiclet, DataDefAccess defs) {
      super(def, wiclet, defs);
      this._transactions = (TransactionManager)wiclet.getRuntime()
         .getService(
            class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager == null
               ? (
                  class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager = class$(
                     "net.rim.wica.runtime.metadata.internal.transaction.TransactionManager"
                  )
               )
               : class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager
         );
      this._persistenceListener = (PersistenceListener)wiclet.getRuntime()
         .getService(
            class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
               ? (
                  class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = class$(
                     "net.rim.wica.runtime.metadata.internal.util.PersistenceListener"
                  )
               )
               : class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener
         );
      if (!this.load()) {
         this.createDefaults();
      }

      this._persistenceListener.created(this);
   }

   @Override
   public void setReferenceField(int field, long reference) {
      long oldRef = this.getLongFieldValue(field);
      if (oldRef != reference) {
         DataCollection dc = super._wiclet.getDataCollection(this.getFieldReferenceType(field));
         boolean persistable = this.isPersistable() && this.isPersistable(field);
         dc.addReference(reference, persistable);
         dc.removeReference(oldRef, persistable);
      }

      this.setLongFieldValue(field, reference);
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
}

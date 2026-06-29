package net.rim.wica.runtime.metadata.internal.component;

import net.rim.device.api.util.LongIntHashtable;
import net.rim.wica.common.metadata.component.DataComponentDef;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.handler.KeylessHandler;
import net.rim.wica.runtime.metadata.internal.transaction.TransactionManager;
import net.rim.wica.runtime.metadata.internal.util.PersistenceListener;
import net.rim.wica.runtime.persistence.PersKeylessDataCollectionStruct;

public class KeylessDataCollection implements DataCollection {
   private DataCollectionImpl _transientCollection;
   private KeylessDataCollection$PersRefDataCollectionImpl _persistentCollection;
   protected TransactionManager _transactions;
   private LongIntHashtable _transientRefCounts;
   private LongIntHashtable _persistentRefCounts;
   protected WicletEx _wiclet;
   private KeylessHandler _keylessHandler;
   private PersistenceListener _persistenceListener;
   static Class class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager;
   static Class class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler;
   static Class class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener;

   public boolean hasReferences(long handle) {
      return (this._transientRefCounts.containsKey(handle) || this._persistentRefCounts.containsKey(handle)) && this.getCollection(handle).contains(handle);
   }

   public long create(boolean persistable) {
      if (!persistable) {
         return this.create();
      }

      long handle = this._persistentCollection.create();
      this._transientCollection._idSource = this._persistentCollection._idSource;
      return handle;
   }

   public void startFromHandle(long nextHandle) {
      int id = (int)(nextHandle & -1);
      if (this._persistentCollection._idSource < id) {
         this._persistentCollection._idSource = id;
         this._transientCollection._idSource = id;
      }
   }

   public long nextHandle() {
      return (long)this._persistentCollection.getDef().getId() << 32 | 4294967295L & this._persistentCollection._idSource;
   }

   @Override
   public DataComponentDef getDef() {
      return this._transientCollection.getDef();
   }

   @Override
   public long create() {
      long handle = this._transientCollection.create();
      this._persistentCollection._idSource = this._transientCollection._idSource;
      if (this._keylessHandler != null && this._keylessHandler.isInScript()) {
         this._keylessHandler.addZeroReference(handle);
      }

      return handle;
   }

   @Override
   public boolean contains(long dataHandle) {
      return this.getCollection(dataHandle).contains(dataHandle);
   }

   @Override
   public void save() {
      this._transientCollection.defragment();
      PersKeylessDataCollectionStruct dcData = new PersKeylessDataCollectionStruct();
      dcData._persistentRefCounts = this._persistentRefCounts;
      this._persistentCollection.save(dcData);
   }

   @Override
   public boolean isModified() {
      return this._persistentCollection.isModified();
   }

   @Override
   public void copyFields(long copyTo, long copyFrom, boolean deep) {
      this.getCollection(copyTo).copyFields(copyTo, copyFrom, this.getCollection(copyFrom), deep);
   }

   @Override
   public boolean equals(long data1, long data2) {
      return this.getCollection(data1).equals(data1, data2, this.getCollection(data2));
   }

   @Override
   public boolean equalsField(long data, int field, Object o) {
      return this.getCollection(data).equalsField(data, field, o);
   }

   @Override
   public void setObjectFieldValue(long dataHandle, int field, Object value) {
      this.getCollection(dataHandle).setObjectFieldValue(dataHandle, field, value);
   }

   @Override
   public void setIntFieldValue(long dataHandle, int field, int value) {
      this.getCollection(dataHandle).setIntFieldValue(dataHandle, field, value);
   }

   @Override
   public void setBooleanFieldValue(long dataHandle, int field, boolean value) {
      this.getCollection(dataHandle).setBooleanFieldValue(dataHandle, field, value);
   }

   @Override
   public void setDoubleFieldValue(long dataHandle, int field, double value) {
      this.getCollection(dataHandle).setDoubleFieldValue(dataHandle, field, value);
   }

   @Override
   public void setLongFieldValue(long dataHandle, int field, long value) {
      this.getCollection(dataHandle).setLongFieldValue(dataHandle, field, value);
   }

   @Override
   public void setReferenceField(long dataHandle, int field, long reference) {
      this.getCollection(dataHandle).setReferenceField(dataHandle, field, reference);
   }

   @Override
   public long getReferenceField(long dataHandle, int field) {
      return this.getCollection(dataHandle).getReferenceField(dataHandle, field);
   }

   @Override
   public long getExistingReferenceField(long dataHandle, int field) {
      return this.getCollection(dataHandle).getExistingReferenceField(dataHandle, field);
   }

   @Override
   public long getReferenceFieldAsIs(long dataHandle, int field) {
      return this.getCollection(dataHandle).getReferenceFieldAsIs(dataHandle, field);
   }

   @Override
   public Object getObjectFieldValue(long dataHandle, int field) {
      return this.getCollection(dataHandle).getObjectFieldValue(dataHandle, field);
   }

   @Override
   public int getIntFieldValue(long dataHandle, int field) {
      return this.getCollection(dataHandle).getIntFieldValue(dataHandle, field);
   }

   @Override
   public boolean getBooleanFieldValue(long dataHandle, int field) {
      return this.getCollection(dataHandle).getBooleanFieldValue(dataHandle, field);
   }

   @Override
   public double getDoubleFieldValue(long dataHandle, int field) {
      return this.getCollection(dataHandle).getDoubleFieldValue(dataHandle, field);
   }

   @Override
   public long getLongFieldValue(long dataHandle, int field) {
      return this.getCollection(dataHandle).getLongFieldValue(dataHandle, field);
   }

   @Override
   public Object getFieldValueAsObject(long dataHandle, int field) {
      return this.getCollection(dataHandle).getFieldValueAsObject(dataHandle, field);
   }

   @Override
   public void setFieldValueFromObject(long dataHandle, int field, Object value) {
      this.getCollection(dataHandle).setFieldValueFromObject(dataHandle, field, value);
   }

   @Override
   public boolean isSupported(String feature) {
      return this._transientCollection.isSupported(feature);
   }

   @Override
   public void restoreHandle(long handle) {
      this.getCollection(handle).restoreHandle(handle);
   }

   @Override
   public boolean isPersistable(long dataHandle) {
      if (this._persistentRefCounts.containsKey(dataHandle)) {
         return true;
      } else {
         return this._transientRefCounts.containsKey(dataHandle) ? false : this._persistentCollection.contains(dataHandle);
      }
   }

   @Override
   public boolean isInnerVectorInitialized(long dataHandle, int field) {
      return this.getCollection(dataHandle).isInnerVectorInitialized(dataHandle, field);
   }

   @Override
   public void remove(long dataHandle) {
      if (this._transactions == null || !this._transactions.markDeleted(dataHandle)) {
         this.getCollection(dataHandle).remove(dataHandle);
         this._persistentRefCounts.remove(dataHandle);
         this._transientRefCounts.remove(dataHandle);
      }
   }

   @Override
   public void addReference(long handle, boolean persistableContainer) {
      if (handle != -1) {
         if (!persistableContainer) {
            int counter = this._transientRefCounts.get(handle);
            counter = counter == -1 ? 1 : counter + 1;
            this._transientRefCounts.put(handle, counter);
         } else {
            int counter = this._persistentRefCounts.get(handle);
            if (counter != -1 && counter != 0) {
               counter++;
            } else {
               counter = 1;
               if (!this._persistentCollection.contains(handle)) {
                  this.makePersistable(handle);
               }
            }

            this._persistentRefCounts.put(handle, counter);
            this._persistentCollection._save = true;
         }
      }
   }

   @Override
   public void removeReference(long handle, boolean persistableContainer) {
      if (handle != -1) {
         if (persistableContainer) {
            int counter = this._persistentRefCounts.get(handle);
            if (counter > 0) {
               if (--counter == 0) {
                  if (!this._transientRefCounts.containsKey(handle)) {
                     if (this._keylessHandler != null && this._keylessHandler.isInScript()) {
                        this._keylessHandler.addTempReference(handle);
                        this._transientRefCounts.put(handle, 1);
                        this.makeNonPersistable(handle);
                     } else {
                        this.removeInternal(handle, this._persistentCollection);
                     }
                  } else {
                     this.makeNonPersistable(handle);
                  }

                  this._persistentRefCounts.remove(handle);
               } else {
                  this._persistentRefCounts.put(handle, counter);
               }

               this._persistentCollection._save = true;
            }
         } else {
            int counter = this._transientRefCounts.get(handle);
            if (counter > 0) {
               if (--counter == 0) {
                  if (!this._persistentRefCounts.containsKey(handle)) {
                     if (this._keylessHandler != null && this._keylessHandler.isInScript()) {
                        this._keylessHandler.addTempReference(handle);
                     } else {
                        this.removeInternal(handle, this._transientCollection);
                        this._transientRefCounts.remove(handle);
                     }
                  } else {
                     this._transientRefCounts.remove(handle);
                  }
               } else {
                  this._transientRefCounts.put(handle, counter);
               }
            }
         }
      }
   }

   public KeylessDataCollection(WicletEx wiclet, DataComponentDef defs) {
      this._wiclet = wiclet;
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
      this._keylessHandler = (KeylessHandler)wiclet.getRuntime()
         .getService(
            class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler == null
               ? (class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler = class$("net.rim.wica.runtime.metadata.internal.handler.KeylessHandler"))
               : class$net$rim$wica$runtime$metadata$internal$handler$KeylessHandler
         );
      this._persistentCollection = new KeylessDataCollection$PersRefDataCollectionImpl(wiclet, defs, true);
      this._transientCollection = new DataCollectionImpl(wiclet, defs, false);
      this._transientCollection._idSource = this._persistentCollection._idSource;
      this._transientRefCounts = (LongIntHashtable)(new Object());
      this._persistentRefCounts = this._persistentCollection.getPersistentRefCounts();
      if (this._persistentRefCounts == null) {
         this._persistentRefCounts = (LongIntHashtable)(new Object());
      }

      this._persistenceListener = (PersistenceListener)this._wiclet
         .getRuntime()
         .getService(
            class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
               ? (
                  class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = class$(
                     "net.rim.wica.runtime.metadata.internal.util.PersistenceListener"
                  )
               )
               : class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener
         );
      this._persistenceListener.created(this);
   }

   private DataCollectionImpl getCollection(long handle) {
      return this.isPersistable(handle) ? this._persistentCollection : this._transientCollection;
   }

   private void removeInternal(long dataHandle, DataCollection dc) {
      if (this._transactions == null || !this._transactions.markDeleted(dataHandle)) {
         dc.remove(dataHandle);
      }

      this._persistenceListener.deleted(dataHandle);
   }

   private void makePersistable(long handle) {
      boolean transactionState = false;
      if (this._transactions != null) {
         transactionState = this._transactions.getIgnoreTransactions();
         this._transactions.setIgnoreTransactions(true);
      }

      int oldIdSource = this._persistentCollection._idSource;
      this._persistentCollection._idSource = this._transientCollection.getHandle(handle);
      this._persistentCollection.create();
      this._persistentCollection._idSource = oldIdSource;
      this._persistentCollection.copyFields(handle, handle, this._transientCollection, false);
      this._transientCollection.remove(handle);
      if (this._transactions != null) {
         this._transactions.setIgnoreTransactions(transactionState);
      }
   }

   private void makeNonPersistable(long handle) {
      boolean transactionState = false;
      if (this._transactions != null) {
         transactionState = this._transactions.getIgnoreTransactions();
         this._transactions.setIgnoreTransactions(true);
      }

      int oldIdSource = this._transientCollection._idSource;
      this._transientCollection._idSource = this._persistentCollection.getHandle(handle);
      this._transientCollection.create();
      this._transientCollection._idSource = oldIdSource;
      this._transientCollection.copyFields(handle, handle, this._persistentCollection, false);
      this._persistentCollection.remove(handle);
      if (this._transactions != null) {
         this._transactions.setIgnoreTransactions(transactionState);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}

package net.rim.wica.runtime.metadata.internal.util;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.wica.runtime.access.data.collections.StdCmpCollection;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.Data;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.component.InnerDataVector;
import net.rim.wica.runtime.metadata.internal.component.KeylessDataCollection;
import net.rim.wica.runtime.metadata.internal.def.DataDefAccess;
import net.rim.wica.runtime.metadata.internal.transaction.TransactionManager;
import net.rim.wica.runtime.persistence.PersDataCollectionStruct;
import net.rim.wica.runtime.persistence.PersistentContentHelper;
import net.rim.wica.runtime.persistence.WicletStore;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.util.LongLongHashtable;
import net.rim.wica.runtime.util.LongVector;

public class PersistenceListener implements PersistentContentListener, Serviceable {
   private WicletEx _app;
   private DataDefAccess _defs;
   private boolean _mergeTransient;
   private LongHashtable _handles;
   private IntHashtable _cmpHandlers;
   private IntIntHashtable _defToMaxHandle;
   private LongHashtable _cmpLevelKeyless;
   private LongLongHashtable _transactionHandles;
   private LongHashtable _refIndex;
   private LongVector _refFields;
   private boolean _transactionStarted;
   private boolean _persistenceReadable;
   private Object _globalTicket;
   private Object _dummy = new Object();
   private static final long OP_CREATE = 4294967296L;
   private static final long OP_MODIFY = 8589934592L;
   private static final long OP_DELETE = 17179869184L;
   static Class class$net$rim$wica$runtime$metadata$WicletRuntime;
   static Class class$net$rim$wica$runtime$persistence$PersistentContentHelper;
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager;

   public void undoTransaction() {
      if (this._transactionStarted) {
         this._transactionStarted = false;
         LongEnumeration handles = this._transactionHandles.keys();
         LongEnumeration codes = this._transactionHandles.elements();

         while (handles.hasMoreElements()) {
            long handle = handles.nextElement();
            long code = codes.nextElement();
            if ((code & 4294967296L) != 0) {
               this.clean(handle);
            } else if ((code & 8589934592L) != 0) {
               int fieldIndex = (int)(code & 4294967295L);
               IntVector fields = this.getFields(handle);

               for (int i = fields.size() - 1; i >= fieldIndex; i--) {
                  this.removeRefIndex(handle, fields.elementAt(i));
                  fields.removeElementAt(i);
               }
            }
         }

         this._transactionHandles.clear();
      }
   }

   public boolean isPersistenceReadable() {
      return this._persistenceReadable;
   }

   public void activate() {
      this.immediateMerge(false);
   }

   public void deactivate() {
      this.immediateStart();
   }

   public void shutdown() {
      PersistentContentHelper persistentContent = (PersistentContentHelper)this._app
         .getRuntime()
         .getService(
            class$net$rim$wica$runtime$persistence$PersistentContentHelper == null
               ? (class$net$rim$wica$runtime$persistence$PersistentContentHelper = class$("net.rim.wica.runtime.persistence.PersistentContentHelper"))
               : class$net$rim$wica$runtime$persistence$PersistentContentHelper
         );
      persistentContent.removeListener(this);
      this.immediateMerge(true);
   }

   public void commitTransaction() {
      if (this._transactionStarted) {
         this._transactionStarted = false;
         LongEnumeration handles = this._transactionHandles.keys();
         LongEnumeration codes = this._transactionHandles.elements();

         while (handles.hasMoreElements()) {
            long handle = handles.nextElement();
            long code = codes.nextElement();
            if ((code & 17179869184L) != 0) {
               this.clean(handle);
            }
         }

         this._transactionHandles.clear();
      }
   }

   public void startTransaction() {
      if (!this._persistenceReadable && !this._transactionStarted) {
         this._transactionStarted = true;
      }
   }

   public void created(Object data) {
      if (!this._persistenceReadable) {
         if (data instanceof StdCmpCollection) {
            return;
         }

         if (data instanceof Data) {
            this._cmpHandlers.put(((Data)data).getDef().getId(), data);
            return;
         }

         if (data instanceof KeylessDataCollection) {
            KeylessDataCollection dc = (KeylessDataCollection)data;
            int defId = dc.getDef().getId();
            dc.startFromHandle((long)defId << 32 | this._defToMaxHandle.get(defId));
            this._cmpHandlers.put(defId, data);
            return;
         }

         if (data instanceof KeyDataCollection) {
            this._cmpHandlers.put(((DataCollection)data).getDef().getId(), data);
         }
      }
   }

   public void created(long handle) {
      if (this._transactionStarted && !this._handles.containsKey(handle)) {
         IntVector fields = (IntVector)(new Object());
         this._handles.put(handle, fields);
         this._transactionHandles.put(handle, 4294967296L);
      }
   }

   public void modified(long handle, int field) {
      if (this._transactionStarted) {
         IntVector fields = (IntVector)this._handles.get(handle);
         if (fields == null) {
            fields = (IntVector)(new Object());
            this._handles.put(handle, fields);
            this.addFieldEntry(handle, fields, field);
            return;
         }

         if (!this.isFieldRegistered(fields, field)) {
            this.addFieldEntry(handle, fields, field);
         }
      }
   }

   public void deleted(long handle) {
      if (this._transactionStarted) {
         long existing = this._transactionHandles.containsKey(handle) ? this._transactionHandles.get(handle) : 0;
         this._transactionHandles.put(handle, 17179869184L | existing);
      }
   }

   public void createdKeylessOnDecoding(long handle) {
      if (this._transactionStarted) {
         this._cmpLevelKeyless.put(handle, this._dummy);
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
   }

   @Override
   public void persistentContentStateChanged(int state) {
      int currentState = PersistentContent.getState();
      if (currentState == 2) {
         this._app.getRuntime().enqueuePriorityRunnable(new PersistenceListener$Start(this, null));
      } else {
         if (currentState == 1) {
            this._app.getRuntime().enqueuePriorityRunnable(new PersistenceListener$Merge(this, null));
         }
      }
   }

   @Override
   public void setServices(ServiceProvider provider) {
      WicletRuntime runtime = (WicletRuntime)provider.getService(
         class$net$rim$wica$runtime$metadata$WicletRuntime == null
            ? (class$net$rim$wica$runtime$metadata$WicletRuntime = class$("net.rim.wica.runtime.metadata.WicletRuntime"))
            : class$net$rim$wica$runtime$metadata$WicletRuntime
      );
      this._app = (WicletEx)runtime.getWiclet();
      this._mergeTransient = !this._app.isBackground();
      this._persistenceReadable = true;
      PersistentContentHelper persistentContent = (PersistentContentHelper)provider.getService(
         class$net$rim$wica$runtime$persistence$PersistentContentHelper == null
            ? (class$net$rim$wica$runtime$persistence$PersistentContentHelper = class$("net.rim.wica.runtime.persistence.PersistentContentHelper"))
            : class$net$rim$wica$runtime$persistence$PersistentContentHelper
      );
      persistentContent.addListener(this);
      this.checkPersistenceOnStartup(persistentContent);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void start() {
      try {
         this._handles = (LongHashtable)(new Object());
         this._cmpHandlers = (IntHashtable)(new Object());
         this._defToMaxHandle = (IntIntHashtable)(new Object());
         this._cmpLevelKeyless = (LongHashtable)(new Object());
         this._transactionHandles = new LongLongHashtable();
         this._refIndex = (LongHashtable)(new Object());
         this._refFields = new LongVector();
         this._defs = this._app.getDefinitions().getDataDefs();
         this.preventHandleConflict();
      } catch (Throwable var3) {
         Logger.log(this.toString(), ((StringBuffer)(new Object("Intent list start failed: "))).append(t.toString()).toString(), 2);
         return;
      }
   }

   private void preventHandleConflict() {
      IntEnumeration defIds = this._defs.getDataDefIds();
      WicletStore store = this._app.getContext().getWicletStore();
      int globalDefId = this._app.getDefinitions().getGlobalDefId();

      while (defIds.hasMoreElements()) {
         int id = defIds.nextElement();
         if (id != globalDefId && !this._defs.hasKey(id)) {
            PersDataCollectionStruct dcData = (PersDataCollectionStruct)store.loadData(id);
            this._defToMaxHandle.put(id, dcData != null ? dcData.getIdSource() : 0);
         }
      }

      PersDataCollectionStruct dcData = (PersDataCollectionStruct)store.loadData(1);
      this._defToMaxHandle.put(1, dcData != null ? dcData.getIdSource() : 0);
      dcData = (PersDataCollectionStruct)store.loadData(6);
      this._defToMaxHandle.put(6, dcData != null ? dcData.getIdSource() : 0);
      dcData = (PersDataCollectionStruct)store.loadData(8);
      this._defToMaxHandle.put(8, dcData != null ? dcData.getIdSource() : 0);
      dcData = (PersDataCollectionStruct)store.loadData(4);
      this._defToMaxHandle.put(4, dcData != null ? dcData.getIdSource() : 0);
   }

   private void immediateStart() {
      this._globalTicket = null;
      if (this.shouldStart()) {
         this._persistenceReadable = false;
         this.start();
      }
   }

   private void immediateMerge(boolean force) {
      Object ticket = PersistentContent.getTicket();
      if (ticket == null) {
         Logger.log(this.toString(), "PersistentContent locked on merge.", 3);
      } else {
         if (force ? this.canMerge() : this.shouldMerge()) {
            this._persistenceReadable = true;
            this.merge();
         }

         ticket = null;
      }
   }

   private boolean shouldStart() {
      return this._persistenceReadable && PersistentContent.getState() != 1;
   }

   private boolean shouldMerge() {
      return !this._persistenceReadable && PersistentContent.getState() == 1;
   }

   private boolean canMerge() {
      return !this._persistenceReadable;
   }

   private boolean isFieldRegistered(IntVector fields, int field) {
      return fields.indexOf(field) != -1;
   }

   private void addFieldEntry(long handle, IntVector fields, int field) {
      if (!this._transactionHandles.containsKey(handle)) {
         this._transactionHandles.put(handle, 8589934592L | fields.size());
      }

      fields.addElement(field);
      int parentDef = (int)(handle >>> 32);
      if (this._defs.hasDefinition(parentDef) && !this._cmpHandlers.containsKey(parentDef)) {
         int fieldType = this._defs.getFieldType(parentDef, field);
         if (fieldType == 6 || fieldType == 32774) {
            int nestedDef = this._defs.getFieldReferenceType(parentDef, field);
            if (this._cmpHandlers.containsKey(nestedDef)) {
               if (!this._defs.hasDefinition(nestedDef)) {
                  if (this._app.getDataCollection(nestedDef).getDef().hasKey()) {
                     return;
                  }
               } else if (this._defs.hasKey(nestedDef)) {
                  return;
               }

               Object data = this._app.getData(parentDef);
               IntVector indicies = (IntVector)this._refIndex.get(handle);
               if (indicies == null) {
                  indicies = (IntVector)(new Object());
                  this._refIndex.put(handle, indicies);
               }

               indicies.addElement(field);
               indicies.addElement(this._refFields.size());
               if (fieldType == 32774) {
                  InnerDataVector source = (InnerDataVector)(
                     !(data instanceof Data) ? ((DataCollection)data).getObjectFieldValue(handle, field) : ((Data)data).getObjectFieldValue(field)
                  );
                  this._refFields.addElement(source.size());

                  for (int i = source.size() - 1; i >= 0; i--) {
                     this._refFields.addElement(source.elementAtAsIs(i));
                  }
               } else if (fieldType == 6) {
                  this._refFields
                     .addElement(
                        !(data instanceof Data) ? ((DataCollection)data).getReferenceFieldAsIs(handle, field) : ((Data)data).getReferenceFieldAsIs(field)
                     );
               }
            }
         }
      }
   }

   private int getRefIndex(long handle, int field) {
      if (this._refIndex.containsKey(handle)) {
         IntVector indicies = (IntVector)this._refIndex.get(handle);

         for (int i = indicies.size() - 2; i >= 0; i -= 2) {
            if (indicies.elementAt(i) == field) {
               return indicies.elementAt(++i);
            }
         }
      }

      return -1;
   }

   private void removeRefIndex(long handle, int field) {
      if (this._refIndex.containsKey(handle)) {
         IntVector indicies = (IntVector)this._refIndex.get(handle);

         for (int i = indicies.size() - 2; i >= 0; i -= 2) {
            if (indicies.elementAt(i) == field) {
               indicies.removeElementAt(i);
               indicies.removeElementAt(++i);
               return;
            }
         }
      }
   }

   private IntVector getFields(long handle) {
      return (IntVector)this._handles.get(handle);
   }

   private synchronized void checkPersistenceOnStartup(PersistentContentHelper persistentContent) {
      int state = PersistentContent.getState();
      if (state != 1) {
         Runnable start = new PersistenceListener$Start(this, null);
         if (this._app.isBackground() || this._globalTicket != null) {
            this._app.getRuntime().enqueuePriorityRunnable(start);
            return;
         }

         this._app.getRuntime().enqueuePriorityRunnable(new PersistenceListener$1(this));
      }
   }

   private boolean waitForDeactivate() {
      return !this._app.isBackground() && (!this._app.getRuntime().isStarted() || this._app.getRuntime().isActive());
   }

   private void clean(long handle) {
      this._handles.remove(handle);
      this._cmpLevelKeyless.remove(handle);
      this._refIndex.remove(handle);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void merge() {
      TransactionManager transactions = null;
      int transactionId = -1;
      boolean var7 = false /* VF: Semaphore variable */;

      label174: {
         try {
            label158:
            try {
               var7 = true;
               if (!this._handles.isEmpty()) {
                  transactions = (TransactionManager)this._app
                     .getRuntime()
                     .getService(
                        class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager == null
                           ? (
                              class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager = class$(
                                 "net.rim.wica.runtime.metadata.internal.transaction.TransactionManager"
                              )
                           )
                           : class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager
                     );
                  if (transactions != null) {
                     transactionId = transactions.startTransaction();
                  }

                  new PersistenceListener$MergingAlgorithm(this, null).merge();
                  if (transactions != null) {
                     transactions.complete(false, transactionId, true, true);
                  }

                  this._app.save();
                  var7 = false;
               } else {
                  var7 = false;
               }
               break label174;
            } catch (Throwable var10) {
               Logger.log(this.toString(), ((StringBuffer)(new Object("Data merge failed: "))).append(t.toString()).toString(), 2);
               if (transactions != null) {
                  if (transactionId != -1) {
                     transactions.undo(false, transactionId, true);
                     var7 = false;
                  } else {
                     var7 = false;
                  }
               } else {
                  var7 = false;
               }
               break label158;
            }
         } finally {
            if (var7) {
               this._handles = null;
               this._cmpHandlers = null;
               this._defToMaxHandle = null;
               this._cmpLevelKeyless = null;
               this._transactionHandles = null;
               this._refIndex = null;
               this._refFields = null;
               this._defs = null;
               if (this._app.isBackground()) {
                  this._app.clear();
               }

               if (transactions != null && transactionId != -1) {
                  transactions.removeTrans(transactionId);
               }
            }
         }

         this._handles = null;
         this._cmpHandlers = null;
         this._defToMaxHandle = null;
         this._cmpLevelKeyless = null;
         this._transactionHandles = null;
         this._refIndex = null;
         this._refFields = null;
         this._defs = null;
         if (this._app.isBackground()) {
            this._app.clear();
         }

         if (transactions != null && transactionId != -1) {
            transactions.removeTrans(transactionId);
            return;
         }

         return;
      }

      this._handles = null;
      this._cmpHandlers = null;
      this._defToMaxHandle = null;
      this._cmpLevelKeyless = null;
      this._transactionHandles = null;
      this._refIndex = null;
      this._refFields = null;
      this._defs = null;
      if (this._app.isBackground()) {
         this._app.clear();
      }

      if (transactions != null && transactionId != -1) {
         transactions.removeTrans(transactionId);
      }
   }

   @Override
   public String toString() {
      return "MDS Runtime Metadata.PersistenceListener";
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

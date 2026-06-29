package net.rim.wica.runtime.metadata.internal.util;

import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.LongEnumeration;
import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.common.metadata.component.DataComponentDef;
import net.rim.wica.runtime.access.data.collections.StdCmpCollection;
import net.rim.wica.runtime.metadata.component.Data;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.metadata.internal.component.InnerDataVector;
import net.rim.wica.runtime.metadata.internal.component.KeylessDataCollection;
import net.rim.wica.runtime.persistence.PersDataCollectionStruct;
import net.rim.wica.runtime.persistence.WicletStore;
import net.rim.wica.runtime.util.LongLongHashtable;

final class PersistenceListener$MergingAlgorithm {
   private LongLongHashtable _srcToDstHandles;
   private int _globalDefId;
   private final PersistenceListener this$0;

   private PersistenceListener$MergingAlgorithm(PersistenceListener this$0) {
      this.this$0 = this$0;
   }

   protected final void merge() {
      this._srcToDstHandles = new LongLongHashtable();
      this._globalDefId = this.this$0._app.getDefinitions().getGlobalDefId();
      this.loadCollections();
      LongEnumeration e = this.this$0._handles.keys();

      while (e.hasMoreElements()) {
         long srcHandle = e.nextElement();
         int defId = (int)(srcHandle >>> 32);
         if (defId == this._globalDefId) {
            this.mergeGlobals();
         } else {
            DataCollection dc = this.getCollection(defId);
            if (dc.getDef().hasKey()) {
               this.merge((KeyDataCollection)this.getCollection(defId), srcHandle);
            }
         }
      }
   }

   private final void merge(KeyDataCollection srcKdc, long srcHandle) {
      if (srcHandle != -1 && !this._srcToDstHandles.containsKey(srcHandle)) {
         if (srcKdc instanceof StdCmpCollection) {
            this._srcToDstHandles.put(srcHandle, srcHandle);
         } else {
            int defId = (int)(srcHandle >>> 32);
            boolean transientHandle = !this.this$0._defs.isPersistable(defId);
            if (transientHandle && !this.this$0._mergeTransient) {
               this._srcToDstHandles.put(srcHandle, -1);
            } else if (!transientHandle && this.this$0._cmpHandlers.containsKey(defId)) {
               KeyDataCollection dstKdc = (KeyDataCollection)this.this$0._app.getDataCollection(defId);
               Object key = srcKdc.getPKey(srcHandle);
               long dstHandle = dstKdc.create(key);
               this._srcToDstHandles.put(srcHandle, dstHandle);
               this.mergeDeep(srcKdc, dstKdc, srcHandle, dstHandle, true);
            } else {
               this._srcToDstHandles.put(srcHandle, srcHandle);
               this.mergeShallow(srcKdc, srcHandle, !transientHandle);
            }
         }
      }
   }

   private final void merge(KeylessDataCollection srcKdc, long srcHandle, long dstHandle, boolean deepMergeParent, boolean persistableParent) {
      if (srcHandle != -1 && !this._srcToDstHandles.containsKey(srcHandle)) {
         int defId = (int)(srcHandle >>> 32);
         if (this.this$0._cmpHandlers.containsKey(defId)) {
            KeylessDataCollection dstKdc = (KeylessDataCollection)this.this$0._app.getDataCollection(defId);
            if (dstHandle == -1 || this.this$0._cmpLevelKeyless.containsKey(srcHandle) || !dstKdc.contains(dstHandle)) {
               dstHandle = dstKdc.create(persistableParent);
            }

            this._srcToDstHandles.put(srcHandle, dstHandle);
            this.mergeDeep(srcKdc, dstKdc, srcHandle, dstHandle, persistableParent);
         } else if (deepMergeParent && dstHandle != -1 && !this.this$0._cmpLevelKeyless.containsKey(srcHandle) && srcKdc.contains(dstHandle)) {
            this._srcToDstHandles.put(srcHandle, dstHandle);
            this.mergeDeep(srcKdc, srcKdc, srcHandle, dstHandle, persistableParent);
         } else {
            this._srcToDstHandles.put(srcHandle, srcHandle);
            this.mergeShallow(srcKdc, srcHandle, persistableParent);
         }
      }
   }

   private final void mergeGlobals() {
      Data srcGlobals = (Data)this.this$0._cmpHandlers.get(this._globalDefId);
      if (srcGlobals != null) {
         this.mergeDeep(srcGlobals, this.this$0._app.getGlobals());
      } else {
         this.mergeShallow(this.this$0._app.getGlobals());
      }
   }

   private final void mergeShallow(DataCollection srcDC, long srcHandle, boolean persistableParent) {
      IntVector fields = this.this$0.getFields(srcHandle);
      DataComponentDef def = srcDC.getDef();

      for (int i = fields.size() - 1; i >= 0; i--) {
         int field = fields.elementAt(i);
         switch (def.getFieldType(field)) {
            case 6:
               this.mergeDataField(srcDC, srcDC, srcHandle, srcHandle, field, persistableParent, false);
               break;
            case 32774:
               this.mergeDataArrayField(srcDC, srcDC, srcHandle, -1, field, persistableParent, false);
         }
      }
   }

   private final void mergeDeep(DataCollection srcDC, DataCollection dstDC, long srcHandle, long dstHandle, boolean persistableParent) {
      IntVector fields = this.this$0.getFields(srcHandle);
      DataComponentDef def = srcDC.getDef();

      for (int i = fields.size() - 1; i >= 0; i--) {
         int field = fields.elementAt(i);
         switch (def.getFieldType(field)) {
            case 0:
               dstDC.setBooleanFieldValue(dstHandle, field, srcDC.getBooleanFieldValue(srcHandle, field));
               break;
            case 1:
            case 5:
               dstDC.setIntFieldValue(dstHandle, field, srcDC.getIntFieldValue(srcHandle, field));
               break;
            case 2:
               dstDC.setDoubleFieldValue(dstHandle, field, srcDC.getDoubleFieldValue(srcHandle, field));
               break;
            case 3:
            case 32768:
            case 32769:
            case 32770:
            case 32771:
            case 32772:
            case 32773:
            case 32776:
               dstDC.setObjectFieldValue(dstHandle, field, srcDC.getObjectFieldValue(srcHandle, field));
               break;
            case 4:
            case 8:
               dstDC.setLongFieldValue(dstHandle, field, srcDC.getLongFieldValue(srcHandle, field));
               break;
            case 6:
               this.mergeDataField(srcDC, dstDC, srcHandle, dstHandle, field, persistableParent, true);
               break;
            case 32774:
               this.mergeDataArrayField(srcDC, dstDC, srcHandle, dstHandle, field, persistableParent, true);
               break;
            default:
               throw new RuntimeException("Not recognized type");
         }
      }
   }

   private final void mergeShallow(Data srcGlobals) {
      ComponentDef def = srcGlobals.getDef();
      long globalHandle = (long)this._globalDefId << 32;
      IntVector fields = this.this$0.getFields(globalHandle);

      for (int i = fields.size() - 1; i >= 0; i--) {
         int field = fields.elementAt(i);
         switch (def.getFieldType(field)) {
            case 6:
               if (this.mergeable(this._globalDefId, field)) {
                  this.mergeDataField(srcGlobals, srcGlobals, field, false);
               }
               break;
            case 32774:
               if (this.mergeable(this._globalDefId, field)) {
                  this.mergeDataArrayField(srcGlobals, srcGlobals, field, false);
               }
         }
      }
   }

   private final void mergeDeep(Data srcGlobals, Data dstGlobals) {
      ComponentDef def = dstGlobals.getDef();
      IntVector fields = this.this$0.getFields((long)this._globalDefId << 32);

      for (int i = fields.size() - 1; i >= 0; i--) {
         int field = fields.elementAt(i);
         if (this.mergeable(this._globalDefId, field)) {
            switch (def.getFieldType(field)) {
               case 0:
                  dstGlobals.setBooleanFieldValue(field, srcGlobals.getBooleanFieldValue(field));
                  break;
               case 1:
               case 5:
                  dstGlobals.setIntFieldValue(field, srcGlobals.getIntFieldValue(field));
                  break;
               case 2:
                  dstGlobals.setDoubleFieldValue(field, srcGlobals.getDoubleFieldValue(field));
                  break;
               case 3:
               case 32768:
               case 32769:
               case 32770:
               case 32771:
               case 32772:
               case 32773:
               case 32776:
                  dstGlobals.setObjectFieldValue(field, srcGlobals.getObjectFieldValue(field));
                  break;
               case 4:
               case 8:
                  dstGlobals.setLongFieldValue(field, srcGlobals.getLongFieldValue(field));
                  break;
               case 6:
                  this.mergeDataField(srcGlobals, dstGlobals, field, true);
                  break;
               case 32774:
                  this.mergeDataArrayField(srcGlobals, dstGlobals, field, true);
                  break;
               default:
                  throw new RuntimeException("Unrecognized field type");
            }
         }
      }
   }

   private final void mergeDataField(DataCollection srcDC, DataCollection dstDC, long srcHandle, long dstHandle, int field, boolean persistable, boolean deep) {
      int nestedDef = srcDC.getDef().getFieldReferenceType(field);
      long nestedHandle = srcDC.getReferenceFieldAsIs(srcHandle, field);
      long newHandle = -1;
      if (nestedHandle != -1) {
         DataCollection dc = this.getCollection(nestedDef);
         if (!dc.getDef().hasKey()) {
            this.merge(
               (KeylessDataCollection)dc,
               nestedHandle,
               deep ? dstDC.getReferenceFieldAsIs(dstHandle, field) : this.getInitialRef(srcHandle, field),
               deep,
               persistable
            );
         } else {
            this.merge((KeyDataCollection)dc, nestedHandle);
         }

         newHandle = this._srcToDstHandles.get(nestedHandle);
      }

      if (deep) {
         dstDC.setReferenceField(dstHandle, field, newHandle);
         this.getCollection(nestedDef).removeReference(nestedHandle, persistable);
      } else {
         srcDC.setReferenceField(srcHandle, field, newHandle);
         this.removeInitialRef(srcHandle, field, nestedDef);
      }
   }

   private final void mergeDataField(Data srcGlobals, Data dstGlobals, int field, boolean deep) {
      int nestedDef = srcGlobals.getDef().getFieldReferenceType(field);
      long nestedHandle = srcGlobals.getReferenceFieldAsIs(field);
      long newHandle = -1;
      if (nestedHandle != -1) {
         DataCollection dc = this.getCollection(nestedDef);
         if (!dc.getDef().hasKey()) {
            this.merge(
               (KeylessDataCollection)dc,
               nestedHandle,
               deep ? dstGlobals.getReferenceFieldAsIs(field) : this.getInitialRef((long)this._globalDefId << 32, field),
               deep,
               srcGlobals.isPersistable(field)
            );
         } else {
            this.merge((KeyDataCollection)dc, nestedHandle);
         }

         newHandle = this._srcToDstHandles.get(nestedHandle);
      }

      if (deep) {
         dstGlobals.setReferenceField(field, newHandle);
         this.getCollection(nestedDef).removeReference(nestedHandle, srcGlobals.isPersistable(field));
      } else {
         srcGlobals.setReferenceField(field, newHandle);
         this.removeInitialRef((long)this._globalDefId << 32, field, nestedDef);
      }
   }

   private final void mergeDataArrayField(
      DataCollection srcDC, DataCollection dstDC, long srcHandle, long dstHandle, int field, boolean persistable, boolean deepParent
   ) {
      int nestedDef = srcDC.getDef().getFieldReferenceType(field);
      boolean deepChild = this.this$0._cmpHandlers.containsKey(nestedDef);
      InnerDataVector srcArray = (InnerDataVector)srcDC.getObjectFieldValue(srcHandle, field);
      InnerDataVector dstArray = srcArray;
      if (deepParent) {
         dstArray = (InnerDataVector)dstDC.getObjectFieldValue(dstHandle, field);
      } else if (deepChild) {
         dstArray.setTypeHandler(this.this$0._app.getDataCollection(nestedDef));
      }

      this.mergeDataArray(srcArray, dstArray, nestedDef, persistable, deepParent);
      if (deepParent && !deepChild) {
         srcArray.removeAllElements();
      } else {
         if (!deepParent && deepChild) {
            this.removeInitialRefArray(srcHandle, field, nestedDef);
         }
      }
   }

   private final void mergeDataArrayField(Data srcGlobals, Data dstGlobals, int field, boolean deepParent) {
      int nestedDef = srcGlobals.getDef().getFieldReferenceType(field);
      boolean deepChild = this.this$0._cmpHandlers.containsKey(nestedDef);
      InnerDataVector srcArray = (InnerDataVector)srcGlobals.getObjectFieldValue(field);
      InnerDataVector dstArray = srcArray;
      if (deepParent) {
         dstArray = (InnerDataVector)dstGlobals.getObjectFieldValue(field);
      } else if (deepChild) {
         dstArray.setTypeHandler(this.this$0._app.getDataCollection(nestedDef));
      }

      this.mergeDataArray(srcArray, dstArray, nestedDef, srcGlobals.isPersistable(field), deepParent);
      if (deepParent && !deepChild) {
         srcArray.removeAllElements();
      } else {
         if (!deepParent && deepChild) {
            this.removeInitialRefArray((long)this._globalDefId << 32, field, nestedDef);
         }
      }
   }

   private final void mergeDataArray(InnerDataVector srcArray, InnerDataVector dstArray, int nestedDef, boolean persistable, boolean deep) {
      int size = srcArray.size();
      if (deep) {
         dstArray.removeAllElements();
         dstArray.ensureCapacity(size);
      }

      if (size != 0) {
         DataCollection dc = this.getCollection(nestedDef);
         if (!dc.getDef().hasKey()) {
            KeylessDataCollection kdc = (KeylessDataCollection)dc;

            for (int j = 0; j < size; j++) {
               long srcHandle = srcArray.elementAtAsIs(j);
               if (srcHandle != -1) {
                  this.merge(kdc, srcHandle, -1, deep, persistable);
                  if (deep) {
                     dstArray.insertElementAt(this._srcToDstHandles.get(srcHandle), j);
                  } else {
                     srcArray.setElementAt(this._srcToDstHandles.get(srcHandle), j);
                  }
               }
            }
         } else {
            KeyDataCollection kdc = (KeyDataCollection)dc;

            for (int j = 0; j < size; j++) {
               long srcHandle = srcArray.elementAtAsIs(j);
               if (srcHandle != -1) {
                  this.merge(kdc, srcHandle);
                  if (deep) {
                     dstArray.insertElementAt(this._srcToDstHandles.get(srcHandle), j);
                  } else {
                     srcArray.setElementAt(this._srcToDstHandles.get(srcHandle), j);
                  }
               }
            }
         }
      }
   }

   private final boolean mergeable(int defId, int field) {
      return this.this$0._mergeTransient || this.this$0._defs.isPersistable(defId) && this.this$0._defs.isPersistable(defId, field);
   }

   private final DataCollection getCollection(int defId) {
      DataCollection dc = (DataCollection)this.this$0._cmpHandlers.get(defId);
      return dc != null ? dc : this.this$0._app.getDataCollection(defId);
   }

   private final void loadCollections() {
      WicletStore store = this.this$0._app.getContext().getWicletStore();
      IntEnumeration e = this.this$0._cmpHandlers.keys();
      IntVector switchToShallow = new IntVector();
      boolean loadGlobals = false;

      while (e.hasMoreElements()) {
         int defId = e.nextElement();
         if (this.isPersistenceEmpty(store, defId)) {
            switchToShallow.addElement(defId);
         } else if (defId == this._globalDefId) {
            loadGlobals = true;
         } else {
            DataCollection dc = (DataCollection)this.this$0._cmpHandlers.get(defId);
            if (dc.getDef().hasKey()) {
               this.loadKeyCollection(defId);
            } else {
               this.loadKeylessCollection(defId);
            }
         }
      }

      if (loadGlobals) {
         this.loadGlobals();
      }

      if (!switchToShallow.isEmpty()) {
         for (int i = switchToShallow.size() - 1; i >= 0; i--) {
            this.this$0._cmpHandlers.remove(switchToShallow.elementAt(i));
         }
      }
   }

   private final boolean isPersistenceEmpty(WicletStore store, int defId) {
      Object data = store.loadData(defId);
      if (!(data instanceof PersDataCollectionStruct)) {
         return data == null;
      }

      PersDataCollectionStruct dcData = (PersDataCollectionStruct)data;
      return dcData.getDataFields() == null || dcData.getDataFields().isEmpty();
   }

   private final void loadKeyCollection(int defId) {
      if (this.this$0._defs.hasDefinition(defId) && this.this$0._defs.isPersistable(defId)) {
         this.this$0._app.clear(defId);
         this.this$0._app.getDataCollection(defId);
      }
   }

   private final void loadKeylessCollection(int defId) {
      this.this$0._app.clear(defId);
      KeylessDataCollection persistedDC = (KeylessDataCollection)this.this$0._app.getDataCollection(defId);
      KeylessDataCollection dc = (KeylessDataCollection)this.this$0._cmpHandlers.get(defId);
      persistedDC.startFromHandle(dc.nextHandle());
   }

   private final void loadGlobals() {
      this.this$0._app.clear(this._globalDefId);
      this.this$0._app.getGlobals();
   }

   private final long getInitialRef(long parentHandle, int field) {
      int refIndex = this.this$0.getRefIndex(parentHandle, field);
      return refIndex != -1 ? this.this$0._refFields.elementAt(refIndex) : -1;
   }

   private final void removeInitialRef(long handle, int field, int nestedDef) {
      int refIndex = this.this$0.getRefIndex(handle, field);
      if (refIndex != -1) {
         this.this$0._app.getDataCollection(nestedDef).removeReference(this.this$0._refFields.elementAt(refIndex), true);
      }
   }

   private final void removeInitialRefArray(long handle, int field, int nestedDef) {
      int refIndex = this.this$0.getRefIndex(handle, field);
      if (refIndex != -1) {
         int count = (int)this.this$0._refFields.elementAt(refIndex);
         if (count != 0) {
            DataCollection dc = this.this$0._app.getDataCollection(nestedDef);
            count = refIndex + count;

            while (count > refIndex) {
               dc.removeReference(this.this$0._refFields.elementAt(count--), true);
            }
         }
      }
   }

   PersistenceListener$MergingAlgorithm(PersistenceListener x0, PersistenceListener$1 x1) {
      this(x0);
   }
}

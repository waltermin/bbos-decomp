package net.rim.wica.runtime.metadata.internal.transaction;

import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.Data;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.metadata.internal.component.Cloneable;
import net.rim.wica.runtime.metadata.internal.component.InnerDataVector;
import net.rim.wica.runtime.util.LongVector;
import net.rim.wica.runtime.util.Poolable;

final class PooledTransaction implements Poolable {
   private final Wiclet _wiclet;
   private LongHashtable _operations = new LongHashtable();
   private Vector _objFields;
   private int _id;
   static final int OP_DUMMY = 0;
   static final int OP_CREATE = 1;
   static final int OP_MODIFY = 2;
   static final int OP_REMOVE = 4;
   static final int OP_MARK_REMOVE = 8;
   static final int MAX_ID = Integer.MAX_VALUE;
   static final int INVALID_ID = -1;
   static final int NO_PKEY = -1;
   private static int _idCount = 0;

   public final IntVector getEntry(long dh) {
      return (IntVector)this._operations.get(dh);
   }

   public final LongEnumeration getDataHandles() {
      return this._operations.keys();
   }

   public final void setNewTransID() {
      this._id = this.generateID();
   }

   public final int getTransID() {
      return this._id;
   }

   public final void complete(LongVector dataHandles) {
      if (dataHandles != null) {
         LongEnumeration dhs = this.getDataHandles();

         while (dhs.hasMoreElements()) {
            dataHandles.addElement(dhs.nextElement());
         }
      }

      LongHashtable operations = this._operations;
      LongEnumeration longEnum = operations.keys();

      while (longEnum.hasMoreElements()) {
         long dataH = longEnum.nextElement();
         IntVector entry = (IntVector)operations.get(dataH);
         if (this.getOpCode(entry) == 8) {
            DataCollection innerDC = this.getDataCollection(dataH);
            if (innerDC.contains(dataH)) {
               innerDC.remove(dataH);
            }
         }
      }

      this._operations.clear();
      this._objFields.removeAllElements();
   }

   public final void undo() {
      LongHashtable operations = this._operations;
      LongEnumeration longEnum = operations.keys();

      while (longEnum.hasMoreElements()) {
         long dataH = longEnum.nextElement();
         IntVector entry = (IntVector)operations.get(dataH);
         int opCode = this.getOpCode(entry);
         Object data = this._wiclet.getData((int)(dataH >>> 32));
         DataCollection dc;
         Data cmp;
         if (!(data instanceof Data)) {
            cmp = null;
            dc = (DataCollection)data;
            if ((dc == null || !dc.contains(dataH)) && opCode != 4) {
               continue;
            }
         } else {
            cmp = (Data)data;
            dc = null;
         }

         switch (opCode) {
            case 0:
            case 3:
               break;
            case 1:
            default:
               dc.remove(dataH);
               break;
            case 4:
               dc.restoreHandle(dataH);
               if (dc instanceof KeyDataCollection) {
                  KeyDataCollection kDC = (KeyDataCollection)dc;
                  kDC.restoreKey(dataH, this.getPKeyObject(entry));
               }
            case 2:
               if (dc != null) {
                  this.setFieldsBack(entry, dataH, dc);
               } else {
                  this.setFieldsBack(entry, dataH, cmp);
               }
         }
      }

      this._operations.clear();
      this._objFields.removeAllElements();
   }

   public final void created(long dataHandle) {
      if (this._operations.get(dataHandle) == null) {
         this._operations.put(dataHandle, this.initEntry(1));
      }
   }

   public final void markDeleted(long dataHandle) {
      IntVector entry = (IntVector)this._operations.get(dataHandle);
      if (entry != null) {
         int op = this.getOpCode(entry);
         switch (op) {
            case 0:
               break;
            case 1:
            case 2:
            default:
               this.setOpCode(entry, 8);
               return;
         }
      } else {
         this._operations.put(dataHandle, this.initEntry(8));
      }
   }

   public final void deleted(long dataHandle) {
      IntVector entry = (IntVector)this._operations.get(dataHandle);
      boolean bModifyToRemove = false;
      if (entry != null) {
         int op = this.getOpCode(entry);
         switch (op) {
            case 0:
            case 4:
            case 8:
               return;
            case 1:
               this.setOpCode(entry, 0);
               return;
            case 2:
               bModifyToRemove = true;
         }
      } else {
         entry = this.initEntry(4);
         this._operations.put(dataHandle, entry);
      }

      DataCollection dc = this.getDataCollection(dataHandle);
      if (dc != null) {
         int numFields = dc.getDef().getNumFields();
         if (dc instanceof KeyDataCollection) {
            this.setPKeyObject(entry, ((KeyDataCollection)dc).getPKey(dataHandle));
         }

         if (bModifyToRemove) {
            for (int i = 0; i < numFields; i++) {
               if (!this.isFieldRegistered(entry, i)) {
                  this.addFieldInEntry(entry, dataHandle, i);
               }
            }
         } else {
            for (int i = 0; i < numFields; i++) {
               this.addFieldInEntry(entry, dataHandle, i);
            }
         }
      }
   }

   public final void modified(long dataHandle, int field) {
      IntVector entry = (IntVector)this._operations.get(dataHandle);
      if (entry != null) {
         int op = this.getOpCode(entry);
         switch (op) {
            case 2:
               if (!this.isFieldRegistered(entry, field)) {
                  this.addFieldInEntry(entry, dataHandle, field);
                  return;
               }
         }
      } else {
         entry = this.initEntry(2);
         this._operations.put(dataHandle, entry);
         this.addFieldInEntry(entry, dataHandle, field);
      }
   }

   protected final void setOpCode(IntVector entry, int opCode) {
      entry.setElementAt(entry.firstElement() & -256 | opCode & 0xFF, 0);
   }

   public final boolean contains(long dh) {
      return this._operations.containsKey(dh);
   }

   @Override
   public final void clear() {
      this.setNewTransID();
   }

   private final Object getPKeyObject(IntVector entry) {
      return this._objFields.elementAt(entry.elementAt(1));
   }

   private final void setPKeyObject(IntVector entry, Object pKey) {
      this._objFields.addElement(pKey);
      entry.setElementAt(this._objFields.indexOf(pKey), 1);
   }

   private final IntVector initEntry(int op) {
      IntVector entry = new IntVector();
      entry.addElement(op);
      entry.addElement(-1);
      return entry;
   }

   private final void addFieldInEntry(IntVector entry, long dataHandle, int i) {
      long value = 0;
      Object objValue = null;
      Object data = this._wiclet.getData((int)(dataHandle >>> 32));
      boolean isObj = false;
      int fieldType;
      Data cmp;
      DataCollection dc;
      if (!(data instanceof Data)) {
         cmp = null;
         dc = (DataCollection)data;
         fieldType = dc.getDef().getFieldType(i);
      } else {
         cmp = (Data)data;
         dc = null;
         fieldType = cmp.getDef().getFieldType(i);
      }

      entry.addElement(i);
      this.setNumFields(entry, this.getNumFields(entry) + 1);
      switch (fieldType) {
         case -1:
         case 3:
         case 7:
            isObj = true;
            if (dc != null) {
               objValue = dc.getObjectFieldValue(dataHandle, i);
            } else {
               objValue = cmp.getObjectFieldValue(i);
            }
            break;
         case 0:
         default:
            if (dc != null) {
               value = dc.getBooleanFieldValue(dataHandle, i) ? 1 : 0;
            } else {
               value = cmp.getBooleanFieldValue(i) ? 1 : 0;
            }
            break;
         case 1:
         case 5:
            if (dc != null) {
               value = dc.getIntFieldValue(dataHandle, i);
            } else {
               value = cmp.getIntFieldValue(i);
            }
            break;
         case 2:
            if (dc != null) {
               value = Double.doubleToLongBits(dc.getDoubleFieldValue(dataHandle, i));
            } else {
               value = Double.doubleToLongBits(cmp.getDoubleFieldValue(i));
            }
            break;
         case 4:
         case 8:
            if (dc != null) {
               value = dc.getLongFieldValue(dataHandle, i);
            } else {
               value = cmp.getLongFieldValue(i);
            }
            break;
         case 6:
            if (dc != null) {
               value = dc.getReferenceFieldAsIs(dataHandle, i);
            } else {
               value = cmp.getReferenceFieldAsIs(i);
            }
      }

      if (isObj) {
         if (fieldType == 32774) {
            objValue = this.cloneDataVector((InnerDataVector)objValue);
         } else if (objValue instanceof Cloneable) {
            objValue = ((Cloneable)objValue).clone();
         }

         if (objValue != null) {
            this._objFields.addElement(objValue);
            value = this._objFields.indexOf(objValue);
         } else {
            value = -1;
         }
      }

      entry.addElement((int)(value >>> 32));
      entry.addElement((int)(value & -1));
   }

   private final void setFieldsBack(IntVector entry, long dataHandle, Data cmp) {
      int numFields = this.getNumFields(entry);

      for (int i = 0; i < numFields; i++) {
         int fieldType = this.getFieldType(i, dataHandle, entry);
         int fieldID = this.getFieldID(i, entry);
         switch (fieldType) {
            case -1:
            case 3:
            case 7:
               cmp.setObjectFieldValue(fieldID, this.getFieldObjectValue(this.getFieldRawValue(i, entry)));
               break;
            case 0:
            default:
               cmp.setBooleanFieldValue(fieldID, this.getFieldRawValue(i, entry) == 1);
               break;
            case 1:
            case 5:
               cmp.setIntFieldValue(fieldID, (int)this.getFieldRawValue(i, entry));
               break;
            case 2:
               cmp.setDoubleFieldValue(fieldID, Double.longBitsToDouble(this.getFieldRawValue(i, entry)));
               break;
            case 4:
            case 8:
               cmp.setLongFieldValue(fieldID, this.getFieldRawValue(i, entry));
               break;
            case 6:
               cmp.setReferenceField(fieldID, this.getFieldRawValue(i, entry));
         }
      }
   }

   private final void setFieldsBack(IntVector entry, long dataHandle, DataCollection dc) {
      int numFields = this.getNumFields(entry);

      for (int i = 0; i < numFields; i++) {
         int fieldType = this.getFieldType(i, dataHandle, entry);
         int fieldID = this.getFieldID(i, entry);
         switch (fieldType) {
            case -1:
            case 3:
            case 7:
               dc.setObjectFieldValue(dataHandle, fieldID, this.getFieldObjectValue(this.getFieldRawValue(i, entry)));
               break;
            case 0:
            default:
               dc.setBooleanFieldValue(dataHandle, fieldID, this.getFieldRawValue(i, entry) == 1);
               break;
            case 1:
            case 5:
               dc.setIntFieldValue(dataHandle, fieldID, (int)this.getFieldRawValue(i, entry));
               break;
            case 2:
               dc.setDoubleFieldValue(dataHandle, fieldID, Double.longBitsToDouble(this.getFieldRawValue(i, entry)));
               break;
            case 4:
            case 8:
               dc.setLongFieldValue(dataHandle, fieldID, this.getFieldRawValue(i, entry));
               break;
            case 6:
               dc.setReferenceField(dataHandle, fieldID, this.getFieldRawValue(i, entry));
         }
      }
   }

   private final boolean isFieldRegistered(IntVector entry, int field) {
      boolean ret = false;
      int numFields = this.getNumFields(entry);
      int i = 0;

      for (int j = 2; i < numFields; j += 3) {
         if (entry.elementAt(j) == field) {
            return true;
         }

         i++;
      }

      return ret;
   }

   private final int getOpCode(IntVector entry) {
      return entry.firstElement() & 0xFF;
   }

   public PooledTransaction(Wiclet wiclet) {
      this._wiclet = wiclet;
      this._objFields = new Vector();
      this.setNewTransID();
   }

   private final int getNumFields(IntVector entry) {
      return entry.firstElement() >>> 16;
   }

   private final void setNumFields(IntVector entry, int numField) {
      entry.setElementAt(entry.firstElement() & 0xFF | numField << 16, 0);
   }

   private final int getFieldID(int index, IntVector entry) {
      return entry.elementAt(this.fieldIndex2EntryIndex(index));
   }

   private final int fieldIndex2EntryIndex(int index) {
      return (index << 1) + index + 2;
   }

   private final long getFieldRawValue(int index, IntVector entry) {
      int hIntIndex = this.fieldIndex2EntryIndex(index) + 1;
      return (long)entry.elementAt(hIntIndex) << 32 | 4294967295L & entry.elementAt(hIntIndex + 1);
   }

   private final Object getFieldObjectValue(long rawValue) {
      return rawValue != -1 ? this._objFields.elementAt((int)rawValue) : null;
   }

   private final int getFieldType(int index, long handle, IntVector entry) {
      ComponentDef cmpDef = this._wiclet.getComponentDef((int)(handle >>> 32));
      return cmpDef.getFieldType(entry.elementAt(this.fieldIndex2EntryIndex(index)));
   }

   private final LongVector cloneDataVector(InnerDataVector from) {
      int size = from.size();
      LongVector to = new LongVector(size);

      for (int i = 0; i < size; i++) {
         to.addElement(from.elementAtAsIs(i));
      }

      return to;
   }

   private final DataCollection getDataCollection(long dataHandle) {
      return this._wiclet.getDataCollection((int)(dataHandle >>> 32));
   }

   private final int generateID() {
      if (_idCount == Integer.MAX_VALUE) {
         _idCount = 1;
      } else {
         _idCount++;
      }

      return _idCount;
   }
}

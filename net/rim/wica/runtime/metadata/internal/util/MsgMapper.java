package net.rim.wica.runtime.metadata.internal.util;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntLongHashtable;
import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.runtime.metadata.MetadataException;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.CompositeKey;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.metadata.component.Msg;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.util.LongVector;

public final class MsgMapper {
   private WicletEx _wiclet;

   public MsgMapper(WicletEx wiclet) {
      this._wiclet = wiclet;
   }

   public final boolean hasMapping(Msg msg) {
      for (int i = msg.getDef().getNumFields() - 1; i >= 0; i--) {
         int[] map = msg.getFieldMapping(i);
         if (map != null && map.length > 0) {
            return true;
         }
      }

      return false;
   }

   public final void applyInMapping(Msg msg) {
      ComponentDef def = msg.getDef();
      int numFields = def.getNumFields();
      IntLongHashtable regHandles = null;

      for (int i = 0; i < numFields; i++) {
         int[] map = msg.getFieldMapping(i);
         if (map != null && map.length > 1) {
            int maplength = map.length;
            Object data = this._wiclet.getData(map[0]);
            if (!(data instanceof Component)) {
               if (regHandles == null) {
                  regHandles = new IntLongHashtable();
               }

               KeyDataCollection dc = (KeyDataCollection)data;
               if (regHandles.containsKey(map[0])) {
                  long value = regHandles.get(map[0]);
                  if (dc.getDef().isKeyField(map[1])) {
                     int lastKeyIndex = (int)(value >>> 32);
                     if (i > lastKeyIndex) {
                        regHandles.put(map[0], this.mapKeyFields(msg, dc, i));
                     }
                  } else {
                     long handle = (long)map[0] << 32 | 4294967295L & value;
                     this.mapFields(msg, i, 1, maplength, map, dc, handle, true);
                  }
               } else {
                  long result = this.mapKeyFields(msg, dc, i);
                  regHandles.put(map[0], result);
                  if (!dc.getDef().isKeyField(map[1])) {
                     long handle = (long)map[0] << 32 | 4294967295L & result;
                     this.mapFields(msg, i, 1, maplength, map, dc, handle, true);
                  }
               }
            } else {
               Component cmp = (Component)data;
               if (maplength == 2) {
                  this.mapCmpField(msg, i, cmp, map[1]);
               } else {
                  this.mapFields(
                     msg, i, 2, maplength, map, this._wiclet.getDataCollection(cmp.getDef().getFieldReferenceType(map[1])), cmp.getReferenceField(map[1]), true
                  );
               }
            }
         }
      }
   }

   public final void applyOutMapping(Msg msg) {
      ComponentDef def = msg.getDef();
      int numFields = def.getNumFields();

      for (int i = 0; i < numFields; i++) {
         int[] map = msg.getFieldMapping(i);
         if (map != null && map.length > 1) {
            int maplength = map.length;
            Object data = this._wiclet.getData(map[0]);
            if (data instanceof Component) {
               Component cmp = (Component)data;
               if (maplength == 2) {
                  this.mapCmpField(cmp, map[1], msg, i);
               } else {
                  this.mapFields(
                     msg,
                     i,
                     2,
                     maplength,
                     map,
                     this._wiclet.getDataCollection(cmp.getDef().getFieldReferenceType(map[1])),
                     cmp.getReferenceField(map[1]),
                     false
                  );
               }
            }
         }
      }
   }

   private final void mapFields(
      Component cmp, int cmpField, int startMappingFrom, int stopMappingAt, int[] map, DataCollection dc, long handle, boolean cmpIsDataSource
   ) {
      int mapField;
      for (mapField = startMappingFrom; handle != -1 && mapField < stopMappingAt - 1; mapField++) {
         handle = dc.getReferenceField(handle, map[mapField]);
         dc = this._wiclet.getDataCollection(dc.getDef().getFieldReferenceType(map[mapField]));
      }

      if (cmpIsDataSource) {
         if (handle != -1) {
            this.mapField(cmp, cmpField, dc, handle, map[mapField]);
            return;
         }
      } else {
         if (handle != -1) {
            this.mapField(dc, handle, map[mapField], cmp, cmpField);
            return;
         }

         cmp.setFieldValueFromObject(cmpField, null);
      }
   }

   private final void mapCmpField(Component srcCmp, int scrField, Component dstCmp, int dstField) {
      switch (srcCmp.getDef().getFieldType(scrField)) {
         case 0:
            dstCmp.setBooleanFieldValue(dstField, srcCmp.getBooleanFieldValue(scrField));
            return;
         case 1:
         case 5:
            dstCmp.setIntFieldValue(dstField, srcCmp.getIntFieldValue(scrField));
            return;
         case 2:
            dstCmp.setDoubleFieldValue(dstField, srcCmp.getDoubleFieldValue(scrField));
            return;
         case 3:
            dstCmp.setObjectFieldValue(dstField, srcCmp.getObjectFieldValue(scrField));
            return;
         case 4:
         case 8:
            dstCmp.setLongFieldValue(dstField, srcCmp.getLongFieldValue(scrField));
            return;
         case 6:
            dstCmp.setReferenceField(dstField, srcCmp.getReferenceField(scrField));
            return;
         case 32768:
         case 32769:
         case 32770:
         case 32771:
         case 32772:
         case 32773:
         case 32776:
            dstCmp.setObjectFieldValue(dstField, srcCmp.getObjectFieldValue(scrField));
         default:
            return;
         case 32774:
            LongVector handles = (LongVector)srcCmp.getObjectFieldValue(scrField);
            dstCmp.setObjectFieldValue(dstField, handles);
      }
   }

   private final void mapField(Component srcCmp, int scrField, DataCollection dc, long handle, int fieldId) {
      if (dc.getDef().getAccessType(fieldId) != 268435456) {
         throw new MetadataException("Incorrect mapping. Can not map to a read-only field");
      }

      switch (dc.getDef().getFieldType(fieldId)) {
         case 0:
            dc.setBooleanFieldValue(handle, fieldId, srcCmp.getBooleanFieldValue(scrField));
            return;
         case 1:
         case 5:
            dc.setIntFieldValue(handle, fieldId, srcCmp.getIntFieldValue(scrField));
            return;
         case 2:
            dc.setDoubleFieldValue(handle, fieldId, srcCmp.getDoubleFieldValue(scrField));
            return;
         case 3:
            dc.setObjectFieldValue(handle, fieldId, srcCmp.getObjectFieldValue(scrField));
            return;
         case 4:
         case 8:
            dc.setLongFieldValue(handle, fieldId, srcCmp.getLongFieldValue(scrField));
            return;
         case 6:
            long ref = srcCmp.getReferenceField(scrField);
            dc.setReferenceField(handle, fieldId, ref);
            return;
         case 32774:
            LongVector handles = (LongVector)srcCmp.getObjectFieldValue(scrField);
            dc.setObjectFieldValue(handle, fieldId, handles);
            return;
         default:
            dc.setFieldValueFromObject(handle, fieldId, srcCmp.getFieldValueAsObject(scrField));
      }
   }

   private final void mapField(DataCollection dc, long handle, int fieldId, Component dstCmp, int dstField) {
      switch (dstCmp.getDef().getFieldType(dstField)) {
         case 0:
            dstCmp.setBooleanFieldValue(dstField, dc.getBooleanFieldValue(handle, fieldId));
            return;
         case 1:
         case 5:
            dstCmp.setIntFieldValue(dstField, dc.getIntFieldValue(handle, fieldId));
            return;
         case 2:
            dstCmp.setDoubleFieldValue(dstField, dc.getDoubleFieldValue(handle, fieldId));
            return;
         case 3:
            dstCmp.setObjectFieldValue(dstField, dc.getObjectFieldValue(handle, fieldId));
            return;
         case 4:
         case 8:
            dstCmp.setLongFieldValue(dstField, dc.getLongFieldValue(handle, fieldId));
            return;
         case 6:
            long ref = dc.getReferenceField(handle, fieldId);
            dstCmp.setReferenceField(dstField, ref);
            return;
         case 32774:
            LongVector handles = (LongVector)dc.getObjectFieldValue(handle, fieldId);
            dstCmp.setObjectFieldValue(dstField, handles);
            return;
         default:
            dstCmp.setFieldValueFromObject(dstField, dc.getFieldValueAsObject(handle, fieldId));
      }
   }

   private final long mapKeyFields(Msg msg, KeyDataCollection dc, int fieldIndex) {
      int defId = dc.getDef().getId();
      int numFields = msg.getDef().getNumFields();
      int[] pkeyFields = dc.getDef().getKeyFields();
      if (pkeyFields.length != 1) {
         int keyCount = 0;
         int numberOfKeys = pkeyFields.length;
         CompositeKey cKey = new CompositeKey(numberOfKeys);

         for (int i = 0; i < numberOfKeys; i++) {
            cKey.setPart(i, this);
         }

         while (fieldIndex < numFields && keyCount < numberOfKeys) {
            int[] map = msg.getFieldMapping(fieldIndex);
            if (map[0] == defId) {
               int keyIndex = Arrays.binarySearch(pkeyFields, map[1]);
               if (keyIndex >= 0 && cKey.getPart(keyIndex) == this) {
                  keyCount++;
                  cKey.setPart(keyIndex, msg.getFieldValueAsObject(fieldIndex));
               }
            }

            fieldIndex++;
         }

         if (keyCount == numberOfKeys) {
            long handle = dc.create(cKey);
            return (long)(fieldIndex - 1) << 32 | (int)(4294967295L & handle);
         }
      } else {
         while (fieldIndex < numFields) {
            int[] map = msg.getFieldMapping(fieldIndex);
            if (map[0] == defId && map[1] == pkeyFields[0]) {
               Object keyValue;
               if (msg.getDef().getFieldType(fieldIndex) == 6) {
                  long handle = msg.getReferenceField(fieldIndex);
                  KeyDataCollection keyDC = (KeyDataCollection)this._wiclet.getDataCollection(msg.getDef().getFieldReferenceType(fieldIndex));
                  keyValue = keyDC.getPKey(handle);
               } else {
                  keyValue = msg.getFieldValueAsObject(fieldIndex);
               }

               long handle = dc.create(keyValue);
               return (long)fieldIndex << 32 | (int)(4294967295L & handle);
            }

            fieldIndex++;
         }
      }

      throw new RuntimeException("Field level mapping must provide the primary key.");
   }
}

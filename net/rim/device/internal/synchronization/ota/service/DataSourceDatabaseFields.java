package net.rim.device.internal.synchronization.ota.service;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.synchronization.ota.util.Helper;
import net.rim.device.internal.synchronization.ota.util.LengthEncoding;
import net.rim.device.internal.synchronization.ota.util.TLESerializableObject;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;
import net.rim.vm.Array;

public final class DataSourceDatabaseFields implements Persistable, TLESerializableObject {
   private Object[] _fields = new Object[0];
   private IntVector _orderedFields = (IntVector)(new Object());
   private int[] _keyFields = new int[0];
   private int _flags;
   public static final byte FIELDTYPE_OPAQUE = 0;
   public static final byte FIELDTYPE_NULL_TERMINATED_STRING = 1;
   public static final byte FIELDTYPE_NULL_TERMINATED_STRING_UNICODE = 2;
   public static final byte FIELDTYPE_STRING = 3;
   public static final byte FIELDTYPE_STRING_UNICODE = 4;
   public static final byte FIELDTYPE_INT = 5;
   private static final int KEYFIELDBIT = 128;
   private static final int ALL_FIELDS_MAPPED = 1;

   public final void addField(int aUniqueTag, boolean keyField, int type, int toTag, byte[] xReplacmentTags) {
      this.addField(aUniqueTag, keyField, type, toTag, xReplacmentTags, 0);
   }

   public final boolean match(DataSourceDatabaseFields aDataSourceDatabaseFields) {
      for (int xTag = 0; xTag < this._fields.length; xTag++) {
         if (!aDataSourceDatabaseFields.contains(xTag)
            || this.getFieldType(xTag) != aDataSourceDatabaseFields.getFieldType(xTag)
            || this.isKeyField(xTag) != aDataSourceDatabaseFields.isKeyField(xTag)) {
            return false;
         }
      }

      return true;
   }

   public final boolean isKeyField(int aTag) {
      return Arrays.getIndex(this._keyFields, aTag) != -1;
   }

   public final boolean allFieldsMapped() {
      return Helper.getFlagValue(this._flags, 1);
   }

   public final void allFieldsMapped(boolean include) {
      this._flags = Helper.setFlagValue(this._flags, include, 1);
   }

   public final int getMappedTagFor(int aTag, byte[] aMappingCounters) {
      if (this.contains(aTag) && this._fields != null && aTag >= 0 && aTag < this._fields.length) {
         byte[] xTagReplacmentValues = (byte[])this._fields[aTag];
         if (xTagReplacmentValues != null) {
            int xTagCounter = aMappingCounters[aTag]++;
            if (xTagCounter < 0 || xTagCounter >= xTagReplacmentValues.length - 1) {
               throw new DataSourceDatabaseFields$UnmappedTagException(aTag, xTagCounter);
            }

            aTag = xTagReplacmentValues[xTagCounter + 1];
         }
      }

      return aTag;
   }

   public final int getMaxFieldTag() {
      return this.allFieldsMapped() ? 255 : this._fields.length - 1;
   }

   public final int getFieldType(int aTag) {
      if (this.contains(aTag) && aTag >= 0 && aTag < this._fields.length) {
         byte[] xTemp = (byte[])this._fields[aTag];
         if (xTemp != null) {
            return xTemp[0] & 127;
         }
      }

      return 0;
   }

   public final boolean contains(int aTag) {
      if (this.allFieldsMapped()) {
         return true;
      } else {
         return aTag > this._fields.length - 1 ? false : this._fields[aTag] != null;
      }
   }

   public final byte[] getUniqueTagsFor(int aTag) {
      return this.contains(aTag) && aTag >= 0 && aTag < this._fields.length ? (byte[])this._fields[aTag] : null;
   }

   public final IntVector getOrderedFields() {
      return this._orderedFields;
   }

   public final int[] getKeyFields() {
      return this._keyFields;
   }

   @Override
   public final void writeTo(DataBuffer dout) {
      int xNextAvailableTag = this._fields.length;

      for (int xTag = 0; xTag < xNextAvailableTag; xTag++) {
         byte[] xTemp = (byte[])this._fields[xTag];
         if (xTemp != null && xTemp.length > 0) {
            dout.write(xTag);
            LengthEncoding.write(dout, xTemp.length - 1);
            dout.write(xTemp[0]);
            if (xTemp.length > 2) {
               dout.write(xTemp, 2, xTemp.length - 2);
            }
         }
      }
   }

   @Override
   public final void readFrom(DataBuffer din) {
      try {
         while (din.available() != 0) {
            int xTag = TypeLengthEncoding.readTag(din);
            byte[] xValue = TypeLengthEncoding.readBytes(din);
            if (xValue.length > 0) {
               int xType = xValue[0];
               boolean xIsKeyField = (xType & 128) == 128;
               this.addField(xTag, xIsKeyField, xType, xTag, xValue, 1);

               for (int i = 1; i < xValue.length; i++) {
                  int xUniqueTag = xValue[i] & 255;
                  this.addField(xUniqueTag, xIsKeyField, xType, xTag, null);
               }
            }
         }
      } finally {
         return;
      }
   }

   private final void addField(int aUniqueTag, boolean keyField, int type, int toTag, byte[] xReplacmentTags, int offset) {
      int xReplacmentTagsLength = (xReplacmentTags != null ? xReplacmentTags.length : 0) - offset;
      byte[] xTemp = new byte[xReplacmentTagsLength + 2];
      type |= keyField ? 128 : 0;
      xTemp[0] = (byte)type;
      xTemp[1] = (byte)toTag;
      if (xReplacmentTags != null && offset < xReplacmentTags.length) {
         System.arraycopy(xReplacmentTags, offset, xTemp, 2, xReplacmentTagsLength);
      }

      if (keyField) {
         Arrays.add(this._keyFields, aUniqueTag);
      }

      this.adjustFieldsArray(aUniqueTag);
      this._fields[aUniqueTag] = xTemp;
      this._orderedFields.addElement(aUniqueTag);
   }

   private final void adjustFieldsArray(int aTag) {
      if (aTag > this._fields.length - 1) {
         Array.resize(this._fields, aTag + 1);
      }
   }

   public DataSourceDatabaseFields() {
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public DataSourceDatabaseFields(byte[] encodedBytes) {
      this();

      try {
         this.readFrom((DataBuffer)(new Object(encodedBytes, 0, encodedBytes.length, true)));
      } catch (Throwable var4) {
         throw new Object(e.toString());
      }
   }

   @Override
   public final String toString() {
      StringBuffer xSb = (StringBuffer)(new Object());
      int xSize = this._orderedFields.size();

      for (int xIndex = 0; xIndex < xSize; xIndex++) {
         int xField = this._orderedFields.elementAt(xIndex);
         if (this.isKeyField(xField)) {
            xSb.append("K");
         }

         xSb.append("Field= ").append(xField).append(" Type= ").append(this.getFieldType(xField)).append('\n');
      }

      return xSb.toString();
   }
}

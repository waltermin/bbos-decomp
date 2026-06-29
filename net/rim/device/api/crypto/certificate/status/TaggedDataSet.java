package net.rim.device.api.crypto.certificate.status;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;

class TaggedDataSet {
   private IntIntHashtable _fields;
   private IntHashtable _uncompressedFields;
   private ProviderCompressionTable _compressionTable;

   public TaggedDataSet(ProviderCompressionTable compressionTable) {
      if (compressionTable == null) {
         this._fields = null;
         this._compressionTable = null;
         this._uncompressedFields = (IntHashtable)(new Object());
      } else {
         this._compressionTable = compressionTable;
         this._fields = (IntIntHashtable)(new Object());
         this._uncompressedFields = null;
      }
   }

   public void addField(int tag, byte[] value) {
      if (value == null) {
         throw new Object();
      }

      if (this._compressionTable == null) {
         this._uncompressedFields.put(tag, value);
      } else {
         int compressionIndex = this._compressionTable.addTableEntry(value);
         this._fields.put(tag, compressionIndex);
      }
   }

   public byte[] getField(int tag) {
      if (this._compressionTable == null) {
         return (byte[])this._uncompressedFields.get(tag);
      }

      int compressionIndex = this._fields.get(tag);
      return compressionIndex == -1 ? null : this._compressionTable.getTableEntry(compressionIndex);
   }

   public void createFrom(TaggedDataSet dataSet) {
      IntEnumeration tags;
      if (dataSet._compressionTable != null) {
         tags = dataSet._fields.keys();
      } else {
         tags = dataSet._uncompressedFields.keys();
      }

      while (tags.hasMoreElements()) {
         int tag = tags.nextElement();
         this.addField(tag, dataSet.getField(tag));
      }
   }

   public void serialize(DataOutputStream out) {
      if (this._compressionTable == null) {
         throw new Object();
      }

      if (out == null) {
         throw new Object();
      }

      out.writeShort((short)this._fields.size());
      IntEnumeration tagsEnum = this._fields.keys();
      IntEnumeration valuesEnum = this._fields.elements();

      while (tagsEnum.hasMoreElements()) {
         out.writeByte((byte)tagsEnum.nextElement());
         out.writeShort((short)valuesEnum.nextElement());
      }
   }

   public void unSerialize(DataInputStream in) {
      if (this._compressionTable == null) {
         throw new Object();
      }

      if (in == null) {
         throw new Object();
      }

      int length = in.readShort() & '\uffff';

      for (int i = 0; i < length; i++) {
         int tag = in.readByte() & 255;
         int compressionIndex = in.readShort() & '\uffff';
         this._fields.put(tag, compressionIndex);
      }
   }
}

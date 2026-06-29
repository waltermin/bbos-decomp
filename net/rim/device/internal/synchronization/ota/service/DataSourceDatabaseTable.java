package net.rim.device.internal.synchronization.ota.service;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.synchronization.ota.util.Helper;
import net.rim.device.internal.synchronization.ota.util.TLESerializableObject;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;

public final class DataSourceDatabaseTable implements Persistable, TLESerializableObject {
   private int _id;
   private DataSourceDatabaseFields _schema;
   private int _flags;
   private static final byte DEFAULT_FLAG = 1;
   private static final byte ID = 1;
   private static final byte SCHEMA = 2;
   private static final byte DEFAULT = 3;

   public final void setDefault(boolean value) {
      this._flags = Helper.setFlagValue(this._flags, value, 1);
   }

   public final boolean isDefault() {
      return Helper.getFlagValue(this._flags, 1);
   }

   public final void setId(int id) {
      this._id = id;
   }

   public final int getId() {
      return this._id;
   }

   public final void setSchema(DataSourceDatabaseFields aSchema) {
      this._schema = aSchema;
   }

   public final DataSourceDatabaseFields getSchema() {
      return this._schema;
   }

   @Override
   public final void writeTo(DataBuffer dout) {
      TypeLengthEncoding.writeInt(dout, 1, this._id);
      if (this._schema != null) {
         TypeLengthEncoding.writeTLESerializableObject(dout, 2, this._schema);
      }

      if (this.isDefault()) {
         TypeLengthEncoding.writeBoolean(dout, 3, true);
      }
   }

   @Override
   public final void readFrom(DataBuffer din) {
      try {
         while (din.available() != 0) {
            int xTag = din.readUnsignedByte();
            switch (xTag) {
               case 0:
                  TypeLengthEncoding.skipValue(din);
                  break;
               case 1:
               default:
                  this.setId(TypeLengthEncoding.readUnsignedShort(din));
                  break;
               case 2:
                  this.setSchema(new DataSourceDatabaseFields(TypeLengthEncoding.readBytes(din)));
                  break;
               case 3:
                  this.setDefault(TypeLengthEncoding.readBoolean(din));
            }
         }
      } finally {
         return;
      }
   }

   public DataSourceDatabaseTable(DataBuffer din) {
      this();
      this.readFrom(din);
   }

   public DataSourceDatabaseTable() {
   }

   public static final DataSourceDatabaseTable create(DataBuffer din) {
      return new DataSourceDatabaseTable(din);
   }

   public static final DataSourceDatabaseTable create(byte[] bytes) {
      DataBuffer xDataBuffer = (DataBuffer)(new Object(bytes, 0, bytes.length, true));
      return create(xDataBuffer);
   }

   @Override
   public final String toString() {
      StringBuffer xSb = (StringBuffer)(new Object());
      xSb.append("[ Table ]").append('\n');
      xSb.append("ID= ").append(this._id).append('\n');
      xSb.append("Default=").append(this.isDefault()).append('\n');
      xSb.append("Schema=\n").append(this._schema).append('\n');
      return xSb.toString();
   }
}

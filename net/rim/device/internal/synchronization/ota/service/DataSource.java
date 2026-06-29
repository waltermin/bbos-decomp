package net.rim.device.internal.synchronization.ota.service;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.synchronization.ota.util.TLESerializableObject;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;

public final class DataSource implements Persistable, TLESerializableObject {
   private int _version;
   private int _id;
   private String _name;
   private int _hashCode;
   private boolean _default;
   private Hashtable _nameToDatabaseMap = new Hashtable();
   private IntHashtable _idToDatabaseMap = new IntHashtable();
   private boolean _couldHandleNonSync;
   private static final byte VERSION = 1;
   private static final byte NAME = 2;
   private static final byte ID = 3;
   private static final byte DATABASE = 4;
   private static final byte DEFAULT = 5;

   public final void addDataSourceDatabase(DataSourceDatabase aDataSourceDatabase) {
      String xName = aDataSourceDatabase.getName();
      int xDatabaseId = aDataSourceDatabase.getId();
      IntHashtable xVersionToDatabaseMap = (IntHashtable)this._nameToDatabaseMap.get(xName);
      if (xVersionToDatabaseMap == null) {
         xVersionToDatabaseMap = new IntHashtable(1);
         this._idToDatabaseMap.put(xDatabaseId, xVersionToDatabaseMap);
         this._nameToDatabaseMap.put(xName, xVersionToDatabaseMap);
      }

      xVersionToDatabaseMap.put(aDataSourceDatabase.getVersion(), aDataSourceDatabase);
   }

   public final String getDataSourceDatabaseNameFor(int aDataSourceDatabaseId) {
      IntHashtable xVersionToDatabaseMap = (IntHashtable)this._idToDatabaseMap.get(aDataSourceDatabaseId);
      if (xVersionToDatabaseMap != null) {
         Enumeration xDatabasesList = xVersionToDatabaseMap.elements();
         if (xDatabasesList.hasMoreElements()) {
            DataSourceDatabase xDataSourceDatabase = (DataSourceDatabase)xDatabasesList.nextElement();
            return xDataSourceDatabase.getName();
         }
      }

      return null;
   }

   public final boolean couldHandleNonSync() {
      return this._couldHandleNonSync;
   }

   public final boolean isDefault() {
      return this._default;
   }

   public final int getVersion() {
      return this._version;
   }

   public final int getId() {
      return this._id;
   }

   public final String getName() {
      return this._name;
   }

   public final Hashtable getDataSourceDatabases() {
      return this._nameToDatabaseMap;
   }

   public final boolean contains(int aDataSourceDatabaseId) {
      return this._idToDatabaseMap.containsKey(aDataSourceDatabaseId);
   }

   public final boolean contains(String aDataSourceDatabaseName) {
      return this._nameToDatabaseMap.containsKey(aDataSourceDatabaseName);
   }

   public final IntHashtable getDataSourceDatabaseBy(String aDataSourceDataDatabaseName) {
      return (IntHashtable)this._nameToDatabaseMap.get(aDataSourceDataDatabaseName);
   }

   public final DataSourceDatabase getDataSourceDatabaseBy(int aDataSourceDatabaseId, int aDataSourceDatabaseVersion) {
      IntHashtable xVersionToDatabaseMap = (IntHashtable)this._idToDatabaseMap.get(aDataSourceDatabaseId);
      return xVersionToDatabaseMap != null ? (DataSourceDatabase)xVersionToDatabaseMap.get(aDataSourceDatabaseVersion) : null;
   }

   public final DataSourceDatabase getDataSourceDatabaseBy(String aDataSourceDataDatabaseName, int aDataSourceDatabaseVersion) {
      if (aDataSourceDataDatabaseName != null) {
         IntHashtable xVersionToDatabaseMap = (IntHashtable)this._nameToDatabaseMap.get(aDataSourceDataDatabaseName);
         if (xVersionToDatabaseMap != null) {
            return (DataSourceDatabase)xVersionToDatabaseMap.get(aDataSourceDatabaseVersion);
         }
      }

      return null;
   }

   @Override
   public final void writeTo(DataBuffer dout) {
   }

   @Override
   public final void readFrom(DataBuffer din) {
      try {
         while (din.available() != 0) {
            int xTag = TypeLengthEncoding.readTag(din);
            switch (xTag) {
               case 0:
                  TypeLengthEncoding.skipValue(din);
                  break;
               case 1:
               default:
                  this._version = TypeLengthEncoding.readInt(din);
                  break;
               case 2:
                  this._name = TypeLengthEncoding.readString(din);
                  if (this._name != null && this._name.length() != 0) {
                     this._hashCode = CRC32.update(-1, this._name.getBytes());
                  }
                  break;
               case 3:
                  this._id = TypeLengthEncoding.readUnsignedShort(din);
                  break;
               case 4:
                  byte[] xValue = TypeLengthEncoding.readBytes(din);
                  if (xValue.length == 0) {
                     break;
                  }

                  DataSourceDatabase xDataSourceDatabase = DataSourceDatabase.create(xValue);
                  if (xDataSourceDatabase.isGeneric()) {
                     this._couldHandleNonSync = true;
                  }

                  this.addDataSourceDatabase(xDataSourceDatabase);
                  break;
               case 5:
                  this._default = TypeLengthEncoding.readBoolean(din);
            }
         }
      } finally {
         return;
      }
   }

   public DataSource(DataBuffer din) {
      this();
      this.readFrom(din);
   }

   public static final DataSource create(DataBuffer din) {
      return new DataSource(din);
   }

   public static final DataSource create(byte[] bytes) {
      return create(new DataBuffer(bytes, 0, bytes.length, true));
   }

   public DataSource() {
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   @Override
   public final boolean equals(Object anObject) {
      if (this == anObject) {
         return true;
      } else {
         return !(anObject instanceof DataSource) ? false : anObject.hashCode() == this.hashCode();
      }
   }

   @Override
   public final String toString() {
      StringBuffer xSb = new StringBuffer();
      xSb.append('[').append(this._name).append(']').append('\n');
      xSb.append("Version= ").append(this._version).append('\n');
      xSb.append("ID= ").append(this._id).append('\n');
      xSb.append("Default= ").append(this._default).append('\n');
      Enumeration xDatabasesList = this._nameToDatabaseMap.elements();
      xSb.append("[Databases List]").append('\n');

      while (xDatabasesList.hasMoreElements()) {
         IntHashtable xVersionToDatabaseMap = (IntHashtable)xDatabasesList.nextElement();
         Enumeration xDataSourceDatabasesList = xVersionToDatabaseMap.elements();

         while (xDataSourceDatabasesList.hasMoreElements()) {
            DataSourceDatabase xDataSourceDatabase = (DataSourceDatabase)xDataSourceDatabasesList.nextElement();
            xSb.append(xDataSourceDatabase).append('\n');
         }
      }

      return xSb.toString();
   }
}

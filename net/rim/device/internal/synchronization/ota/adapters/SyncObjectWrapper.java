package net.rim.device.internal.synchronization.ota.adapters;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.synchronization.ota.service.DataSourceDatabase;
import net.rim.device.internal.synchronization.ota.service.DataSourceDatabaseFields;
import net.rim.device.internal.synchronization.ota.service.DataSourceDatabaseFields$UnmappedTagException;
import net.rim.device.internal.synchronization.ota.service.DataSourceDatabaseTable;
import net.rim.device.internal.synchronization.ota.util.LengthEncoding;
import net.rim.device.internal.synchronization.ota.util.TLEField;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;
import net.rim.vm.Array;

final class SyncObjectWrapper {
   private SyncObject _syncObject;
   private DataBuffer _otaFormOfSyncObject;
   private DataBuffer _serialFormOfSyncObject;
   private SyncConverter _syncConverter;
   private DataSourceDatabase _dataSourceDatabase;
   private DataSourceDatabaseFields _dataSourceDatabaseFields;
   private byte[] _counters;
   private int _uid;
   private byte _databaseVersion;
   private boolean _databaseHasTables;
   private int _recordTypeTag;
   private int _defaultTableId;
   private byte _tableId = -1;
   private int _finalFieldsHash;
   private int _finalKeyFieldsHash;
   private TLEField _recordTypeField;
   private int[] _tempKeyFieldsSet;
   private int[] _allFieldsHashes;
   private int[] _keyFieldsHashes;
   public static final byte IgnoreUnMappedFields = 1;
   public static final byte IncludeUnMappedFieldsOnly = 2;
   public static final byte ForHashCalculation = 4;
   public static final byte IncludeEmptyFields = 8;
   public static final int OrderedHash = 16;
   public static final byte FIELD_ATTRIBUTES_TRANCATED = 1;

   SyncObjectWrapper() {
   }

   SyncObjectWrapper(SyncObject aSyncObject) {
      this();
      this.setSyncObject(aSyncObject);
   }

   public final synchronized void setDataSourceDatabase(DataSourceDatabase aDataSourceDatabase, int aDatabaseVersion) {
      this._dataSourceDatabase = aDataSourceDatabase;
      this._databaseVersion = (byte)aDatabaseVersion;
      this._databaseHasTables = this._dataSourceDatabase.containsTables();
      this._defaultTableId = this._dataSourceDatabase.getDefaultTableId();
      this._recordTypeTag = this._dataSourceDatabase.getRecordTypeTag();
   }

   public final synchronized void setSyncConverter(SyncConverter aSyncConverter) {
      this._syncConverter = aSyncConverter;
   }

   private final void resetKeyFieldHashes() {
      if (this._keyFieldsHashes != null) {
         for (int xIndex = 0; xIndex < this._keyFieldsHashes.length; xIndex++) {
            this._keyFieldsHashes[xIndex] = -1;
         }
      }
   }

   private final void resetAllFieldHashes() {
      if (this._allFieldsHashes != null) {
         for (int xIndex = 0; xIndex < this._allFieldsHashes.length; xIndex++) {
            this._allFieldsHashes[xIndex] = -1;
         }
      }
   }

   public final synchronized void reset(boolean completeReset) {
      this.setSyncObject(null);
      if (completeReset) {
         this.setOtaFormOfSyncObject(null);
         this.setSerialFormOfSyncObject(null);
      } else {
         this.resetOtaFormOfSyncObject();
         this.resetSerialFormOfSyncObject();
      }
   }

   private final void makeOtaFormOfSyncObjectBufferReady() {
      if (this._otaFormOfSyncObject == null) {
         this._otaFormOfSyncObject = new DataBuffer(true);
      }
   }

   private final void makeSerialFormOfSyncObjectBufferReady() {
      if (this._serialFormOfSyncObject == null) {
         this._serialFormOfSyncObject = new DataBuffer(false);
      }
   }

   public final synchronized boolean setSyncObject(SyncObject aSyncObject) {
      this._syncObject = aSyncObject;
      if (aSyncObject != null) {
         this.setUid(aSyncObject.getUID());
         return true;
      } else {
         this.setUid(0);
         return false;
      }
   }

   public final synchronized SyncObject getSyncObject() {
      return this._syncObject;
   }

   public final synchronized void setUid(int aUid) {
      this._uid = aUid;
   }

   public final synchronized int getUid() {
      return this._uid;
   }

   public final synchronized int getKeyFieldsHash() {
      return this._finalKeyFieldsHash;
   }

   public final synchronized int getFieldsHash() {
      return this._finalFieldsHash;
   }

   private final void initializeCounters() {
      if (this._counters == null) {
         this._counters = new byte[this._dataSourceDatabase.getMaxFieldTag() + 1];
      } else {
         Arrays.fill(this._counters, (byte)0);
      }
   }

   public final synchronized void setTableId(int aTableId) {
      if (this._tableId != aTableId) {
         if (aTableId == 0) {
            aTableId = this._defaultTableId;
         }

         DataSourceDatabaseTable xDataSourceDatabaseTable = this._dataSourceDatabase.getTable(aTableId);
         if (xDataSourceDatabaseTable != null) {
            this._dataSourceDatabaseFields = xDataSourceDatabaseTable != null ? xDataSourceDatabaseTable.getSchema() : null;
            this._tableId = (byte)aTableId;
            if (this._recordTypeField == null) {
               this._recordTypeField = new TLEField();
               this._recordTypeField.setTag(this._recordTypeTag);
            }

            this._recordTypeField.setValue(this._tableId);
            return;
         }

         this._tableId = 0;
         this._dataSourceDatabaseFields = null;
      }
   }

   public final synchronized int getTableId() {
      return this._tableId;
   }

   public final synchronized void setOtaFormOfSyncObject(DataBuffer aBuffer) {
      if (aBuffer != null) {
         aBuffer.setBigEndian(true);
      }

      this._otaFormOfSyncObject = aBuffer;
   }

   public final synchronized void setSerialFormOfSyncObject(DataBuffer aBuffer) {
      if (aBuffer != null) {
         aBuffer.setBigEndian(false);
      }

      this._serialFormOfSyncObject = aBuffer;
   }

   public final synchronized DataBuffer getOtaFormOfSyncObject() {
      this.makeOtaFormOfSyncObjectBufferReady();
      return this._otaFormOfSyncObject;
   }

   public final synchronized DataBuffer getSerialFormOfSyncObject() {
      this.makeSerialFormOfSyncObjectBufferReady();
      return this._serialFormOfSyncObject;
   }

   public final synchronized void resetOtaFormOfSyncObject() {
      if (this._otaFormOfSyncObject != null) {
         this._otaFormOfSyncObject.zero();
         this._otaFormOfSyncObject.rewind();
         this._otaFormOfSyncObject.setLength(0);
      }
   }

   public final synchronized void resetSerialFormOfSyncObject() {
      if (this._serialFormOfSyncObject != null) {
         this._serialFormOfSyncObject.zero();
         this._serialFormOfSyncObject.rewind();
         this._serialFormOfSyncObject.setLength(0);
      }
   }

   public final synchronized void rewindOtaFormOfSyncObject() {
      if (this._otaFormOfSyncObject != null) {
         this._otaFormOfSyncObject.rewind();
      }
   }

   public final synchronized void rewindSerialFormOfSyncObject() {
      if (this._serialFormOfSyncObject != null) {
         this._serialFormOfSyncObject.rewind();
      }
   }

   public final synchronized byte[] getCopyOfOtaBuffer() {
      this.makeOtaFormOfSyncObjectBufferReady();
      return Arrays.copy(this.getOtaBuffer());
   }

   public final synchronized byte[] getCopyOfSerialBuffer() {
      this.makeSerialFormOfSyncObjectBufferReady();
      return Arrays.copy(this.getSerialBuffer());
   }

   public final synchronized byte[] getOtaBuffer() {
      this.makeOtaFormOfSyncObjectBufferReady();
      this._otaFormOfSyncObject.trim();
      return this._otaFormOfSyncObject.getArray();
   }

   public final synchronized byte[] getSerialBuffer() {
      this.makeSerialFormOfSyncObjectBufferReady();
      this._serialFormOfSyncObject.trim();
      return this._serialFormOfSyncObject.getArray();
   }

   public final synchronized boolean convertToSerialForm(boolean findTable) {
      this.makeSerialFormOfSyncObjectBufferReady();
      boolean xResult = false;
      if (this._syncConverter != null && this._syncObject != null && this._dataSourceDatabase != null) {
         this.resetSerialFormOfSyncObject();
         xResult = this._syncConverter.convert(this._syncObject, this._serialFormOfSyncObject, this._databaseVersion);
         if (xResult && findTable && this._databaseHasTables) {
            this.findTableId();
         }
      }

      return xResult;
   }

   private final void findTableId() {
      this.rewindSerialFormOfSyncObject();
      int xTableId = 0;

      label44:
      try {
         if (this._dataSourceDatabase.definesRecordTypeTag()) {
            int xLength = this._serialFormOfSyncObject.readUnsignedShort();
            int xTag = this._serialFormOfSyncObject.readUnsignedByte();
            if (this._recordTypeTag == xTag && xLength != 0) {
               int xIndex = 0;

               do {
                  xTableId |= this._serialFormOfSyncObject.readByte() << 8 * xIndex++;
               } while (--xLength > 0);
            }
         }
      } finally {
         break label44;
      }

      if (xTableId == 0) {
         xTableId = this._defaultTableId;
      }

      this.setTableId(xTableId);
   }

   public final synchronized boolean decode() {
      DataSourceDatabaseFields$UnmappedTagException e;
      try {
         this.makeSerialFormOfSyncObjectBufferReady();
         if (!this._databaseHasTables) {
            this.rewindSerialFormOfSyncObject();
            return this.setSyncObject(this._syncConverter.convert(this._serialFormOfSyncObject, this._databaseVersion, this._uid));
         }

         if (this._dataSourceDatabaseFields == null) {
            return false;
         }

         this.makeOtaFormOfSyncObjectBufferReady();
         this.rewindOtaFormOfSyncObject();
         this.resetSerialFormOfSyncObject();
         this.initializeCounters();
         boolean xApplyMergeOperations = this._dataSourceDatabase.applyMergeOperations();
         if (this._tableId != 0 && this._tableId != this._defaultTableId) {
            ConverterUtilities.writeInt(this._serialFormOfSyncObject, this._recordTypeTag, this._tableId);
         }

         int[] xOriginalKeyFieldsList = this._dataSourceDatabaseFields.getKeyFields();
         if (this._tempKeyFieldsSet == null) {
            this._tempKeyFieldsSet = new int[xOriginalKeyFieldsList.length];
         } else {
            Array.resize(this._tempKeyFieldsSet, xOriginalKeyFieldsList.length);
         }

         System.arraycopy(xOriginalKeyFieldsList, 0, this._tempKeyFieldsSet, 0, xOriginalKeyFieldsList.length);
         int xKeyFieldIndex = 0;

         while (this._otaFormOfSyncObject.available() > 0) {
            boolean xIsCString = false;
            boolean xIsKeyField = false;
            int xUniqueTag = TypeLengthEncoding.readTag(this._otaFormOfSyncObject);
            boolean xIsEncoded = (xUniqueTag & 128) == 128;
            if (xIsEncoded) {
               xUniqueTag &= 127;
            }

            xKeyFieldIndex = Arrays.getIndex(this._tempKeyFieldsSet, xUniqueTag);
            if (xKeyFieldIndex != -1) {
               this._tempKeyFieldsSet[xKeyFieldIndex] = -1;
               xIsKeyField = true;
            }

            int xTag = this._dataSourceDatabaseFields.getMappedTagFor(xUniqueTag, this._counters);
            int xFieldType = this._dataSourceDatabaseFields.getFieldType(xTag);
            if (xIsEncoded) {
               xTag |= 128;
            }

            switch (xFieldType) {
               case 1:
                  xIsCString = true;
               default:
                  int xLength = LengthEncoding.read(this._otaFormOfSyncObject);
                  if (xApplyMergeOperations && !xIsKeyField && xLength == 0) {
                     continue;
                  }

                  this._serialFormOfSyncObject.writeShort(xLength + (!xIsEncoded && xIsCString ? 1 : 0));
                  this._serialFormOfSyncObject.writeByte(xTag);
                  this._serialFormOfSyncObject.write(this._otaFormOfSyncObject.getArray(), this._otaFormOfSyncObject.getArrayPosition(), xLength);
                  if (xIsCString && !xIsEncoded) {
                     this._serialFormOfSyncObject.writeByte(0);
                  }

                  this._otaFormOfSyncObject.skipBytes(xLength);
                  break;
               case 5:
                  int xIntValue = TypeLengthEncoding.readInt(this._otaFormOfSyncObject);
                  ConverterUtilities.writeInt(this._serialFormOfSyncObject, xTag, xIntValue);
            }

            Thread.yield();
         }

         this._serialFormOfSyncObject.trim();
         this._serialFormOfSyncObject.rewind();
         return this.setSyncObject(this._syncConverter.convert(this._serialFormOfSyncObject, this._databaseVersion, this._uid));
      } catch (DataSourceDatabaseFields$UnmappedTagException var13) {
         e = var13;
      } finally {
         ;
      }

      e.log(this._dataSourceDatabase.getName());
      return false;
   }

   public final synchronized boolean encode(int bitFlags) {
      DataSourceDatabaseFields$UnmappedTagException e;
      try {
         int xRecordFieldsHash = 0;
         int xRecordKeyFieldsHash = 0;
         boolean xIgnoreUnMappedFields = (bitFlags & 1) == 1;
         boolean xIncludeUnMappedFieldsOnly = (bitFlags & 2) == 2;
         boolean xForHashCalculation = (bitFlags & 4) == 4;
         boolean xIncludeEmptyFields = (bitFlags & 8) == 8;
         boolean xOrderedHash = (bitFlags & 16) == 16;
         if (xOrderedHash) {
            if (this._allFieldsHashes == null) {
               this._allFieldsHashes = new int[128];
            }

            this.resetAllFieldHashes();
            if (this._keyFieldsHashes == null) {
               this._keyFieldsHashes = new int[128];
            }

            this.resetKeyFieldHashes();
         }

         if (!this._databaseHasTables) {
            if (xForHashCalculation) {
               this.makeSerialFormOfSyncObjectBufferReady();
               int xFieldHash = CRC32.update(-1, 1);
               byte[] xBuffer = this.getSerialBuffer();
               xFieldHash = CRC32.update(xFieldHash, xBuffer);
               xRecordFieldsHash ^= xFieldHash;
            }
         } else {
            if (this._dataSourceDatabaseFields == null) {
               return false;
            }

            this.makeOtaFormOfSyncObjectBufferReady();
            this.makeSerialFormOfSyncObjectBufferReady();
            this.rewindSerialFormOfSyncObject();
            this.resetOtaFormOfSyncObject();
            this.initializeCounters();
            if (xForHashCalculation) {
               if (xOrderedHash) {
                  this._keyFieldsHashes[0] = this._recordTypeField.hashCode();
               } else {
                  xRecordKeyFieldsHash = this._recordTypeField.hashCode();
               }
            }

            byte[] xSerialBuffer = this._serialFormOfSyncObject.getArray();

            label357:
            while (true) {
               int xFieldHash;
               int xTag;
               label355:
               while (true) {
                  int xOffset;
                  int xLength;
                  boolean xIsMappedTag;
                  boolean xEncoded;
                  do {
                     if (this._serialFormOfSyncObject.available() <= 0) {
                        break label357;
                     }

                     xFieldHash = 0;
                     int xFieldType = 0;
                     xEncoded = false;
                     boolean xEncodingHasHeaderBlock = false;
                     xLength = this._serialFormOfSyncObject.readUnsignedShort();
                     xTag = this._serialFormOfSyncObject.readUnsignedByte();
                     xEncoded = (xTag & 128) == 128;
                     if (xEncoded) {
                        xTag &= 127;
                     }

                     xOffset = this._serialFormOfSyncObject.getPosition();
                     this._serialFormOfSyncObject.skipBytes(xLength);
                     if (this._dataSourceDatabaseFields.allFieldsMapped()) {
                        break;
                     }

                     xIsMappedTag = this._dataSourceDatabaseFields.contains(xTag);
                  } while (xTag == this._recordTypeTag || xIncludeUnMappedFieldsOnly && xIsMappedTag || xIgnoreUnMappedFields && !xIsMappedTag);

                  xTag = this._dataSourceDatabaseFields.getMappedTagFor(xTag, this._counters);
                  int var33 = this._dataSourceDatabaseFields.getFieldType(xTag);
                  switch (var33) {
                     case 1:
                        if (!xEncoded) {
                           xLength--;
                        }
                        break;
                     case 5:
                        xIsMappedTag = (boolean)this._serialFormOfSyncObject.getPosition();
                        this._serialFormOfSyncObject.setPosition(xOffset);
                        TypeLengthEncoding.writeInt(this._otaFormOfSyncObject, xTag, this._serialFormOfSyncObject.readInt());
                        this._serialFormOfSyncObject.setPosition(xIsMappedTag);
                        if (xForHashCalculation) {
                           byte[] xTemp = this.getOtaBuffer();
                           xFieldHash = CRC32.update(-1, xTag);
                           xFieldHash = CRC32.update(xFieldHash, xTemp, 2, xTemp.length - 2);
                           if (xOrderedHash) {
                              if (this._allFieldsHashes[xTag] == -1) {
                                 this._allFieldsHashes[xTag] = xFieldHash;
                              } else {
                                 this._allFieldsHashes[xTag] = CRC32.updateInt(this._allFieldsHashes[xTag], xFieldHash);
                              }
                           } else {
                              xRecordFieldsHash ^= xFieldHash;
                           }

                           this.resetOtaFormOfSyncObject();
                        }
                        break label355;
                  }

                  if (!xForHashCalculation) {
                     if (!xEncoded) {
                        if (xIncludeEmptyFields || xLength != 0) {
                           TypeLengthEncoding.writeBytes(this._otaFormOfSyncObject, xTag, xSerialBuffer, xOffset, xLength);
                        }
                        break;
                     }

                     boolean var36 = (xSerialBuffer[xOffset] & 128) == 128;
                     if (!var36) {
                        TypeLengthEncoding.writeBytes(this._otaFormOfSyncObject, xTag | 128, xSerialBuffer, xOffset, xLength);
                        break;
                     }

                     xIsMappedTag = (boolean)this._serialFormOfSyncObject.getPosition();
                     this._serialFormOfSyncObject.setPosition(xOffset + 1);
                     this._otaFormOfSyncObject.write(xTag | 128);
                     this._otaFormOfSyncObject.writeInt(0);
                     int xOtaStreamStartDataPosition = this._otaFormOfSyncObject.getPosition();
                     this._otaFormOfSyncObject.writeByte(xSerialBuffer[xOffset]);

                     while (this._serialFormOfSyncObject.available() > 0) {
                        int xHeaderFieldValueLength = this._serialFormOfSyncObject.readShort();
                        if (xHeaderFieldValueLength <= 0) {
                           break;
                        }

                        int xHeaderFieldTag = this._serialFormOfSyncObject.readByte();
                        int xHeaderFieldValueOffset = this._serialFormOfSyncObject.getPosition();
                        TypeLengthEncoding.writeBytes(
                           this._otaFormOfSyncObject, xHeaderFieldTag, xSerialBuffer, xHeaderFieldValueOffset, xHeaderFieldValueLength
                        );
                        this._serialFormOfSyncObject.skipBytes(xHeaderFieldValueLength);
                     }

                     this._otaFormOfSyncObject.writeByte(0);
                     int xDataOffset = this._serialFormOfSyncObject.getPosition();
                     int xDataLength = xIsMappedTag - xDataOffset;
                     this._otaFormOfSyncObject.writeByteArray(xSerialBuffer, xDataOffset, xDataLength);
                     this._serialFormOfSyncObject.setPosition(xIsMappedTag);
                     int xCurrentOtaStreamPosition = this._otaFormOfSyncObject.getPosition();
                     this._otaFormOfSyncObject.setPosition(xOtaStreamStartDataPosition - 4);
                     this._otaFormOfSyncObject.writeInt(LengthEncoding.getFixedEncodingLengthFor(xCurrentOtaStreamPosition - xOtaStreamStartDataPosition));
                     this._otaFormOfSyncObject.setPosition(xCurrentOtaStreamPosition);
                     break;
                  }

                  if (xLength != 0) {
                     xIsMappedTag = (boolean)xOffset;
                     if (xEncoded) {
                        boolean var35 = (xSerialBuffer[xOffset] & 128) == 128;
                        xSerialBuffer[xOffset] = (byte)(xSerialBuffer[xOffset] & 15);
                        xLength--;
                        if (!var35) {
                           xIsMappedTag++;
                        } else {
                           int xOriginalSerialStreamPosition = this._serialFormOfSyncObject.getPosition();
                           this._serialFormOfSyncObject.setPosition(xOffset + 1);

                           while (this._serialFormOfSyncObject.available() > 0) {
                              int xHeaderFieldLength = this._serialFormOfSyncObject.readShort();
                              if (xHeaderFieldLength == 0) {
                                 break;
                              }

                              this._serialFormOfSyncObject.skipBytes(xHeaderFieldLength + 1);
                           }

                           xIsMappedTag = (boolean)this._serialFormOfSyncObject.getPosition();
                           xLength -= xIsMappedTag - xOffset;
                           this._serialFormOfSyncObject.setPosition(xOriginalSerialStreamPosition);
                        }
                     }

                     xFieldHash = CRC32.update(-1, xTag);
                     if (xEncoded) {
                        xFieldHash = CRC32.update(xFieldHash, xSerialBuffer, xOffset, 1);
                     }

                     xFieldHash = CRC32.update(xFieldHash, xSerialBuffer, xIsMappedTag, xLength);
                     if (xOrderedHash) {
                        if (this._allFieldsHashes[xTag] == -1) {
                           this._allFieldsHashes[xTag] = xFieldHash;
                        } else {
                           this._allFieldsHashes[xTag] = CRC32.updateInt(this._allFieldsHashes[xTag], xFieldHash);
                        }
                     } else {
                        xRecordFieldsHash ^= xFieldHash;
                     }
                     break;
                  }
               }

               if (xForHashCalculation && this._dataSourceDatabaseFields.isKeyField(xTag)) {
                  if (xOrderedHash) {
                     if (this._keyFieldsHashes[xTag] == -1) {
                        this._keyFieldsHashes[xTag] = xFieldHash;
                     } else {
                        this._keyFieldsHashes[xTag] = CRC32.updateInt(this._keyFieldsHashes[xTag], xFieldHash);
                     }
                  } else {
                     xRecordKeyFieldsHash ^= xFieldHash;
                  }
               }

               Thread.yield();
            }
         }

         if (xForHashCalculation) {
            if (xOrderedHash && this._databaseHasTables) {
               this._finalFieldsHash = CRC32.update(-1, this._allFieldsHashes, 0, this._allFieldsHashes.length);
               this._finalKeyFieldsHash = CRC32.update(-1, this._keyFieldsHashes, 0, this._keyFieldsHashes.length);
            } else {
               this._finalFieldsHash = xRecordFieldsHash;
               this._finalKeyFieldsHash = xRecordKeyFieldsHash;
            }

            this.resetAllFieldHashes();
            this.resetKeyFieldHashes();
         }

         return true;
      } catch (DataSourceDatabaseFields$UnmappedTagException var24) {
         e = var24;
      } finally {
         ;
      }

      e.log(this._dataSourceDatabase.getName());
      return false;
   }
}

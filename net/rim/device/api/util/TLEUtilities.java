package net.rim.device.api.util;

import java.io.EOFException;
import java.io.UnsupportedEncodingException;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.internal.i18n.UnicodeServiceUtilities;

public final class TLEUtilities {
   private TLEUtilities() {
   }

   public static final void parseBuffer(DataBuffer db, TLEFieldController con) {
      int type;
      while (!db.eof() && (type = db.readUnsignedByte()) != 0) {
         int length = db.readCompressedInt();
         if (!con.processField(type, length, db)) {
            db.skipBytes(length);
         }
      }
   }

   public static final void parseField(DataBuffer db, TLEFieldController con, int length) {
      int oldSize = db.getLength();
      db.setLength(db.getPosition() + length);
      parseBuffer(db, con);
      db.setLength(oldSize);
   }

   public static final int readIntegerField(DataBuffer buf, int type) {
      if (buf.readUnsignedByte() == type) {
         return readIntegerField(buf);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static final int readIntegerField(DataBuffer buf) {
      return readIntegerFieldWithLength(buf, buf.readCompressedInt());
   }

   public static final int readIntegerFieldWithLength(DataBuffer buf, int length) {
      switch (length) {
         case 0:
         case 3:
            throw new IllegalArgumentException();
         case 1:
         default:
            return buf.readUnsignedByte();
         case 2:
            return buf.readUnsignedShort();
         case 4:
            return buf.readInt();
      }
   }

   public static final byte[] readDataField(DataBuffer buf, int type) {
      if (buf.readUnsignedByte() == type) {
         return readDataField(buf);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static final byte[] readDataField(DataBuffer buf) {
      byte[] b = new byte[buf.readCompressedInt()];
      buf.readFully(b);
      return b;
   }

   public static final String readStringField(DataBuffer buf, int type) {
      return readStringField(buf, type, true);
   }

   public static final String readStringField(DataBuffer buf, int type, boolean stripNull) {
      if (buf.readUnsignedByte() != type) {
         throw new IllegalArgumentException();
      } else {
         return readStringField(buf, stripNull);
      }
   }

   public static final String readStringField(DataBuffer buf, boolean stripNull) {
      int length = buf.readCompressedInt();
      byte[] data = buf.getArray();
      int offset = buf.getArrayPosition();
      String result = StringUtilities.decodeBOM(data, offset, length);
      buf.skipBytes(length);
      if (stripNull && length > 0 && result.charAt(result.length() - 1) == 0) {
         result = result.substring(0, result.length() - 1);
      }

      return result;
   }

   public static final String readStringFieldEncoded(DataBuffer buf) {
      int oldPosition = buf.getPosition();
      int type = buf.readUnsignedByte();
      int length = buf.readCompressedInt();
      byte[] data = buf.getArray();
      int offset = buf.getArrayPosition();
      String returnedString = null;
      boolean encoded = (type & 128) != 0 && (type & 240) != 240;

      try {
         if (encoded && length < 1) {
            throw new EOFException();
         }

         returnedString = UnicodeServiceUtilities.readString(data, offset, length, encoded);
         buf.skipBytes(length);
         return returnedString;
      } catch (UnsupportedEncodingException uee) {
         buf.setPosition(oldPosition);
         throw uee;
      } catch (EOFException e) {
         buf.setPosition(oldPosition);
         throw e;
      }
   }

   public static final void writeDataField(DataBuffer buf, int type, byte[] data) {
      int length = data != null ? data.length : 0;
      writeDataField(buf, type, data, 0, length);
   }

   public static final void writeDataField(DataBuffer buf, int type, byte[] data, int offset, int length) {
      buf.writeByte(type);
      buf.writeCompressedInt(length);
      if (data != null) {
         buf.write(data, offset, length);
      }
   }

   public static final void writeDataField(DataBuffer buf, int type, String value, int start, int len, boolean addNull) {
      buf.writeByte(type);
      buf.writeCompressedInt(len + (addNull ? 1 : 0));
      len += start;

      for (int i = start; i < len; i++) {
         buf.writeByte(value.charAt(i));
      }

      if (addNull) {
         buf.writeByte(0);
      }
   }

   public static final void writeField(DataBuffer db, int type, TLEFieldController con) {
      db.writeByte(type);
      int sizePos = db.getPosition();
      db.writeInt(0);
      db.writeByte(0);
      con.dumpField(type, db);
      int curPos = db.getPosition();
      int size = curPos - sizePos - 5;
      db.setPosition(sizePos);

      for (int s = 28; s > 0; s -= 7) {
         db.writeByte(size >>> s | 128);
      }

      db.writeByte(size & 127);
      db.setPosition(curPos);
   }

   public static final void writeIntegerField(DataBuffer buf, int type, int value, boolean fixed) {
      buf.writeByte(type);
      if ((value & -65536) != 0 || fixed) {
         buf.writeCompressedInt(4);
         buf.writeInt(value);
      } else if ((value & 0xFF00) != 0) {
         buf.writeCompressedInt(2);
         buf.writeShort(value);
      } else {
         buf.writeCompressedInt(1);
         buf.writeByte(value);
      }
   }

   public static final void writeStringField(DataBuffer buf, int type, String value) {
      int length = value != null ? value.length() : 0;
      writeStringField(buf, type, value, 0, length, false);
   }

   public static final void writeStringField(DataBuffer buf, int type, String value, boolean addNull) {
      int length = value != null ? value.length() : 0;
      writeStringField(buf, type, value, 0, length, addNull);
   }

   public static final void writeStringField(DataBuffer buf, int type, String value, int offset, int length) {
      writeStringField(buf, type, value, offset, length, false);
   }

   public static final void writeStringField(DataBuffer buf, int type, String value, int offset, int len, boolean addNull) {
      buf.ensureCapacity(2 * len + 2 + 5 + 1);
      buf.writeByte(type);
      byte[] bufferBytes = buf.getArray();
      int arrayPosition = buf.getArrayPosition() + 5;
      int length = StringUtilities.codeBOM(value, offset, offset + len, bufferBytes, arrayPosition);
      boolean isUTF16 = length > 2 && bufferBytes[offset] == 239 && bufferBytes[offset + 1] == 255;
      buf.writeCompressedInt(length + (addNull ? (isUTF16 ? 2 : 1) : 0));
      if (arrayPosition != buf.getArrayPosition()) {
         System.arraycopy(bufferBytes, arrayPosition, bufferBytes, buf.getArrayPosition(), length);
      }

      buf.skipBytes(length);
      if (addNull) {
         buf.writeByte(0);
         if (isUTF16) {
            buf.writeByte(0);
         }
      }

      buf.trim(false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void writeStringFieldEncoded(DataBuffer buf, int type, String value, String encodingName) {
      if (value != null && encodingName != null) {
         int length = value.length();
         int compressedFieldSize = getLengthStructureSize(length + 2);
         boolean encoded = true;
         byte encoding = 0;
         byte[] bytes = null;
         buf.ensureCapacity(length + compressedFieldSize + 2);
         byte[] bufferBytes = buf.getArray();
         int arrayPosition = buf.getArrayPosition();
         if (StringUtilities.getCharacterSize(value) == 1) {
            buf.setPosition(arrayPosition + compressedFieldSize + 1);
            encoded = !ConverterUtilities.writeStringDefault(buf, value);
         }

         if ((encoding = UnicodeServiceUtilities.getEncoding(encodingName)) == -1) {
            encodingName = "";
         }

         if (encoded) {
            type |= 128;
            encoded = true;
            boolean var13 = false /* VF: Semaphore variable */;

            try {
               var13 = true;
               bytes = value.getBytes(encodingName);
               if (bytes != null) {
                  length = bytes.length;
                  compressedFieldSize = getLengthStructureSize(length + (encoded ? 1 : 0) + compressedFieldSize + 1);
                  buf.ensureCapacity(length + (encoded ? 1 : 0) + compressedFieldSize + 1);
                  var13 = false;
               } else {
                  var13 = false;
               }
            } finally {
               if (var13) {
                  buf.setPosition(arrayPosition);
               }
            }

            buf.setPosition(arrayPosition);
         }

         buf.setPosition(arrayPosition);
         buf.writeByte(type);
         buf.writeCompressedInt(length + 1);
         if (encoded) {
            buf.writeByte(encoding);
         }

         if (bytes != null) {
            arrayPosition = buf.getArrayPosition();
            System.arraycopy(bytes, 0, bufferBytes, arrayPosition, length);
         }

         buf.skipBytes(length);
         if (!encoded) {
            buf.writeByte(0);
         }

         buf.trim(false);
      }
   }

   public static final boolean findType(DataBuffer buffer, int type) {
      try {
         while (true) {
            int thisType = getType(buffer);
            if (thisType == type) {
               return true;
            }

            skipField(buffer);
         }
      } catch (EOFException var3) {
         return false;
      }
   }

   public static final void skipField(DataBuffer buffer) {
      buffer.skipBytes(1);
      int length = buffer.readCompressedInt();
      buffer.skipBytes(length);
   }

   public static final int getType(DataBuffer buffer) {
      return getType(buffer, false);
   }

   public static final int getType(DataBuffer buffer, boolean convertTag) {
      if (buffer.available() < 1) {
         throw new EOFException();
      }

      int type = buffer.getArray()[buffer.getArrayPosition()] & 255;
      return convertTag && (type & 128) != 0 && (type & 240) != 240 ? type & -129 : type;
   }

   public static final int getIntegerFieldSize(int i) {
      if ((i & -65536) != 0) {
         return 6;
      } else {
         return (i & 0xFF00) != 0 ? 4 : 3;
      }
   }

   private static final int getLengthStructureSize(int length) {
      int num = 5;

      for (int s = 28; s > 0; s -= 7) {
         if ((length >>> s & 127) != 0) {
            return num;
         }

         num--;
      }

      return num;
   }

   public static final int getFieldSize(int dataSize) {
      return getLengthStructureSize(dataSize) + 1 + dataSize;
   }

   public static final String getStringFromBuffer(DataBuffer buf, int length) {
      String str = StringUtilities.cStr2String(buf.getArray(), buf.getArrayPosition(), length);
      buf.skipBytes(length);
      return str;
   }
}

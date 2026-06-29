package net.rim.wica.transport.internal.message.data;

import net.rim.wica.transport.util.CompressedBuffer;
import net.rim.wica.transport.util.DataException;

public class DataBuffer extends CompressedBuffer {
   public DataBuffer() {
      this(256);
   }

   public DataBuffer(int bufferLength) {
      super(bufferLength);
   }

   public DataBuffer(byte[] buffer) {
      this(buffer, 0, buffer.length);
   }

   public DataBuffer(byte[] buffer, int offset, int length) {
      super(buffer, offset, length);
   }

   public boolean readBoolean() {
      return this.readByte() != 0;
   }

   public float readFloat() {
      return Float.intBitsToFloat(this.readUncompressedInt());
   }

   public double readDouble() {
      return Double.longBitsToDouble(this.readUncompressedLong());
   }

   public String readString() {
      return this.readUTF();
   }

   protected String readUTF() {
      int utflen = this.readCompressedInt() - 1;
      if (utflen < 0) {
         return null;
      }

      int endPosition = super._cursor + utflen;
      this.ensureAvailable(endPosition);
      StringBuffer str = (StringBuffer)(new Object(utflen));

      while (super._cursor < endPosition) {
         int c = super._buffer[super._cursor] & 255;
         switch (c >> 4) {
            case -1:
            case 8:
            case 9:
            case 10:
            case 11:
               throw new DataException(1);
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            default:
               super._cursor++;
               str.append((char)c);
               break;
            case 12:
            case 13:
               super._cursor += 2;
               if (super._cursor > endPosition) {
                  throw new DataException(1);
               }

               int char2 = super._buffer[super._cursor - 1];
               if ((char2 & 192) != 128) {
                  throw new DataException(1);
               }

               str.append((char)((c & 31) << 6 | char2 & 63));
               break;
            case 14:
               super._cursor += 3;
               if (super._cursor > endPosition) {
                  throw new DataException(1);
               }

               int char2 = super._buffer[super._cursor - 2];
               int char3 = super._buffer[super._cursor - 1];
               if ((char2 & 192) != 128 || (char3 & 192) != 128) {
                  throw new DataException(1);
               }

               str.append((char)((c & 15) << 12 | (char2 & 63) << 6 | (char3 & 63) << 0));
         }
      }

      return (String)(new Object(str));
   }

   public void writeBoolean(boolean value) {
      this.writeByte((byte)(value ? 1 : 0));
   }

   public void writeFloat(float value) {
      this.writeUncompressedInt(Float.floatToIntBits(value));
   }

   public final void writeDouble(double value) {
      this.writeUncompressedLong(Double.doubleToLongBits(value));
   }

   public void writeString(String str) {
      this.writeUTF(str);
   }

   protected void writeUTF(String str) {
      if (str == null) {
         this.writeCompressedInt(0);
      } else {
         int strlen = str.length();
         int utflen = 0;
         char[] charr = new char[strlen];
         int c = 0;
         str.getChars(0, strlen, charr, 0);

         for (int i = 0; i < strlen; i++) {
            int var7 = charr[i];
            if (var7 >= 1 && var7 <= 127) {
               utflen++;
            } else if (var7 > 2047) {
               utflen += 3;
            } else {
               utflen += 2;
            }
         }

         this.writeCompressedInt(utflen + 1);
         this.ensureBuffer(super._cursor + utflen);

         for (int i = 0; i < strlen; i++) {
            int var8 = charr[i];
            if (var8 >= 1 && var8 <= 127) {
               this.nextByte((byte)var8);
            } else if (var8 > 2047) {
               this.nextByte((byte)(224 | var8 >> '\f' & 15));
               this.nextByte((byte)(128 | var8 >> 6 & 63));
               this.nextByte((byte)(128 | var8 >> 0 & 63));
            } else {
               this.nextByte((byte)(192 | var8 >> 6 & 31));
               this.nextByte((byte)(128 | var8 >> 0 & 63));
            }
         }
      }
   }
}

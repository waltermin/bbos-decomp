package net.rim.device.apps.internal.docview.gui;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.EOFException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import net.rim.device.apps.internal.docview.core.DocViewInputStream;

public final class DocViewInputStreamImpl extends DocViewInputStream {
   private InputStream in;

   public DocViewInputStreamImpl(InputStream input) {
      if (input == null) {
         throw new IllegalArgumentException("Null input stream.");
      }

      this.in = input;
   }

   @Override
   public final int read() {
      return this.in.read();
   }

   @Override
   public final int read(byte[] b) {
      return this.in.read(b, 0, b.length);
   }

   @Override
   public final int read(byte[] b, int off, int len) {
      return this.in.read(b, off, len);
   }

   @Override
   public final void readFully(byte[] b) {
      this.readFully(b, 0, b.length);
   }

   @Override
   public final void readFully(byte[] b, int off, int len) throws EOFException {
      if (len < 0) {
         throw new IndexOutOfBoundsException();
      }

      int n = 0;

      while (n < len) {
         int count = this.read(b, off + n, len - n);
         if (count < 0) {
            throw new EOFException();
         }

         n += count;
      }
   }

   @Override
   public final int skipBytes(int n) {
      int total = 0;
      int cur = 0;

      while (total < n && (cur = (int)this.skip(n - total)) > 0) {
         total += cur;
      }

      return total;
   }

   @Override
   public final boolean readBoolean() throws EOFException {
      int ch = this.read();
      if (ch < 0) {
         throw new EOFException();
      } else {
         return ch != 0;
      }
   }

   @Override
   public final byte readByte() throws EOFException {
      int ch = this.read();
      if (ch < 0) {
         throw new EOFException();
      } else {
         return (byte)ch;
      }
   }

   @Override
   public final int readUnsignedByte() throws EOFException {
      int ch = this.read();
      if (ch < 0) {
         throw new EOFException();
      } else {
         return ch;
      }
   }

   @Override
   public final short readShort() {
      return (short)this.readUnsignedShort();
   }

   @Override
   public final int readUnsignedShort() {
      synchronized (this.in) {
         int b1 = this.read();
         int b2 = this.read();
         if ((b1 | b2) < 0) {
            throw new EOFException();
         } else {
            return b2 << 8 | b1;
         }
      }
   }

   @Override
   public final char readChar() {
      return (char)this.readUnsignedShort();
   }

   @Override
   public final int readInt() {
      synchronized (this.in) {
         int b1 = this.read();
         int b2 = this.read();
         int b3 = this.read();
         int b4 = this.read();
         if ((b1 | b2 | b3 | b4) < 0) {
            throw new EOFException();
         } else {
            return b4 << 24 | b3 << 16 | b2 << 8 | b1;
         }
      }
   }

   @Override
   public final long readLong() {
      synchronized (this.in) {
         long b8 = this.read();
         long b7 = this.read();
         long b6 = this.read();
         long b5 = this.read();
         long b4 = this.read();
         long b3 = this.read();
         long b2 = this.read();
         long b1 = this.read();
         if ((b1 | b2 | b3 | b4 | b5 | b6 | b7 | b8) < 0) {
            throw new EOFException();
         } else {
            return b1 << 56 | b2 << 48 | b3 << 40 | b4 << 32 | b5 << 24 | b6 << 16 | b7 << 8 | b8;
         }
      }
   }

   @Override
   public final float readFloat() {
      return (float)-1082130432;
   }

   @Override
   public final double readDouble() {
      return (double)-4616189618054758400L;
   }

   @Override
   public final String readUTF() {
      return readUTF(this);
   }

   public static final String readUTF(DataInput in) throws UTFDataFormatException {
      int ch1 = in.readUnsignedByte();
      int ch2 = in.readUnsignedByte();
      int utflen = (ch1 << 8) + (ch2 << 0);
      StringBuffer str = new StringBuffer(utflen);
      byte[] bytearr = new byte[utflen];
      int count = 0;
      in.readFully(bytearr, 0, utflen);

      while (count < utflen) {
         int c = bytearr[count] & 255;
         switch (c >> 4) {
            case -1:
            case 8:
            case 9:
            case 10:
            case 11:
               throw new UTFDataFormatException();
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            default:
               count++;
               str.append((char)c);
               break;
            case 12:
            case 13:
               count += 2;
               if (count > utflen) {
                  throw new UTFDataFormatException();
               }

               int char2 = bytearr[count - 1];
               if ((char2 & 192) != 128) {
                  throw new UTFDataFormatException();
               }

               str.append((char)((c & 31) << 6 | char2 & 63));
               break;
            case 14:
               count += 3;
               if (count > utflen) {
                  throw new UTFDataFormatException();
               }

               int char2 = bytearr[count - 2];
               int char3 = bytearr[count - 1];
               if ((char2 & 192) != 128 || (char3 & 192) != 128) {
                  throw new UTFDataFormatException();
               }

               str.append((char)((c & 15) << 12 | (char2 & 63) << 6 | (char3 & 63) << 0));
         }
      }

      return new String(str);
   }

   @Override
   public final long skip(long n) {
      return this.in.skip(n);
   }

   @Override
   public final int available() {
      return this.in.available();
   }

   @Override
   public final void close() {
      this.in.close();
   }

   @Override
   public final synchronized void mark(int readlimit) {
      this.in.mark(readlimit);
   }

   @Override
   public final synchronized void reset() {
      this.in.reset();
   }

   @Override
   public final boolean markSupported() {
      return this.in.markSupported();
   }

   @Override
   public final boolean supportsTotalByteCount() {
      return this.in instanceof ByteArrayInputStream;
   }

   @Override
   public final Object getInputStreamSyncObject() {
      return this.in;
   }
}

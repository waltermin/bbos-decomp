package java.io;

public class DataInputStream extends InputStream implements DataInput {
   protected InputStream in;

   public DataInputStream(InputStream in) {
      this.in = in;
   }

   @Override
   public int read() {
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
   public final int readUnsignedShort() throws EOFException {
      int ch1 = this.read();
      int ch2 = this.read();
      if ((ch1 | ch2) < 0) {
         throw new EOFException();
      } else {
         return (ch1 << 8) + (ch2 << 0);
      }
   }

   @Override
   public final char readChar() {
      return (char)this.readUnsignedShort();
   }

   @Override
   public final int readInt() throws EOFException {
      int ch1 = this.read();
      int ch2 = this.read();
      int ch3 = this.read();
      int ch4 = this.read();
      if ((ch1 | ch2 | ch3 | ch4) < 0) {
         throw new EOFException();
      } else {
         return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
      }
   }

   @Override
   public final long readLong() {
      return ((long)this.readInt() << 32) + (this.readInt() & 4294967295L);
   }

   @Override
   public final float readFloat() {
      return Float.intBitsToFloat(this.readInt());
   }

   @Override
   public final double readDouble() {
      return Double.longBitsToDouble(this.readLong());
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
   public long skip(long n) {
      return this.in.skip(n);
   }

   @Override
   public int available() {
      return this.in.available();
   }

   @Override
   public void close() {
      this.in.close();
   }

   @Override
   public synchronized void mark(int readlimit) {
      this.in.mark(readlimit);
   }

   @Override
   public synchronized void reset() {
      this.in.reset();
   }

   @Override
   public boolean markSupported() {
      return this.in.markSupported();
   }
}

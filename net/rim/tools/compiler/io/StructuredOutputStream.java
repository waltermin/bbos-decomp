package net.rim.tools.compiler.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import net.rim.tools.compiler.vm.Constants;

public class StructuredOutputStream implements Constants {
   private boolean _writingCode;
   protected int _offset;
   private OutputStream _out;
   private byte[] _buffer;
   private int _used;
   private static final boolean _littleEndian = true;

   protected void init(OutputStream out, boolean littleEndian, PrintStream listing) {
      this._out = out;
      this._buffer = new byte[128];
   }

   protected StructuredOutputStream() {
   }

   public StructuredOutputStream(OutputStream out, boolean littleEndian) {
      this.init(out, littleEndian, null);
   }

   public void setWritingCode(boolean wc) {
      this._writingCode = wc;
   }

   public boolean writingCode() {
      return this._writingCode;
   }

   public Object getCookie() {
      return null;
   }

   public int getOffset() {
      return this._offset;
   }

   public void resetOffset() throws IOException {
      if ((this._offset & 3) != 0) {
         throw new IOException("output stream offset may only be reset on 4 byte aligned boundary");
      }

      this._offset = 0;
   }

   public void flush() {
      this._out.write(this._buffer, 0, this._used);
      this._used = 0;
   }

   public void write(int b) {
      this._buffer[this._used++] = (byte)b;
      if (this._used == this._buffer.length) {
         this.flush();
      }
   }

   public int writeSlack(int align) {
      int temp = align - 1;
      int slack = (this._offset + temp & ~temp) - this._offset;

      for (int i = slack; i > 0; i--) {
         this.writeByte(0);
      }

      return slack;
   }

   public void write(byte[] b, int start, int length) {
      int end = start + length;

      for (int i = start; i < end; i++) {
         this.writeByte(b[i]);
      }
   }

   public void write(byte[] b) {
      this.write(b, 0, b.length);
   }

   public void writeByte(int b) {
      this._offset++;
      this.write(b);
   }

   public void writeChar(int c) {
      this._offset += 2;
      this.write(c);
      this.write(c >> 8);
   }

   public void writeShort(int s) {
      this._offset += 2;
      this.write(s);
      this.write(s >> 8);
   }

   public void writeMultiByteShort(int s) {
      for (s &= 65535; (s & -128) != 0; s >>>= 7) {
         this.writeByte(s & 127 | 128);
      }

      this.writeByte(s & 127);
   }

   public void writeInt(int i) {
      this._offset += 4;
      this.write(i);
      this.write(i >> 8);
      this.write(i >> 16);
      this.write(i >> 24);
   }

   public void writeLong(long l) {
      this._offset += 8;
      this.write((byte)l);
      this.write((byte)(l >> 8));
      this.write((byte)(l >> 16));
      this.write((byte)(l >> 24));
      this.write((byte)(l >> 32));
      this.write((byte)(l >> 40));
      this.write((byte)(l >> 48));
      this.write((byte)(l >> 56));
   }

   public void close() {
      this.flush();
      this._out.close();
      this._out = null;
      this._buffer = null;
   }
}

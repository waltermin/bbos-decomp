package net.rim.tools.compiler.io;

public final class StructuredOutputStreamCounter extends StructuredOutputStream {
   private Object _cookie;

   public StructuredOutputStreamCounter(Object cookie) {
      this._cookie = cookie;
   }

   @Override
   public final Object getCookie() {
      return this._cookie;
   }

   @Override
   public final void resetOffset() {
      super.resetOffset();
   }

   @Override
   public final void write(byte[] b, int start, int length) {
      super._offset += length;
   }

   @Override
   public final void write(int value) {
      super._offset++;
   }

   @Override
   public final void writeByte(int b) {
      super._offset++;
   }

   @Override
   public final void writeChar(int c) {
      super._offset += 2;
   }

   @Override
   public final void writeShort(int s) {
      super._offset += 2;
   }

   @Override
   public final void writeMultiByteShort(int s) {
      s &= 65535;
      if ((s & 49152) != 0) {
         super._offset += 3;
      } else if ((s & 16256) != 0) {
         super._offset += 2;
      } else {
         super._offset++;
      }
   }

   @Override
   public final void writeInt(int i) {
      super._offset += 4;
   }

   @Override
   public final void writeLong(long l) {
      super._offset += 8;
   }

   @Override
   public final void close() {
   }
}

package net.rim.device.internal.browser.util;

public class PipePtr {
   private byte[] _bytes;
   private int _length;
   private int _offset;
   private boolean _ref;

   public PipePtr() {
   }

   public PipePtr(byte[] b, int offset, int length, boolean isRef) {
      this.setData(b, offset, length, isRef);
   }

   public void setData(byte[] b, int offset, int length, boolean isRef) {
      this._bytes = b;
      this._offset = offset;
      this._length = length;
      this._ref = isRef;
   }

   public void setLength(int length) {
      this._length = length;
   }

   public int getOffset() {
      return this._offset;
   }

   public int getLength() {
      return this._length;
   }

   public byte[] getData() {
      return this._bytes;
   }

   public boolean isRef() {
      return this._ref;
   }
}

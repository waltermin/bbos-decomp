package net.rim.device.apps.internal.browser.stack;

import java.io.InputStream;

public final class LZSSInputStream extends InputStream {
   private InputStream _in;
   private byte[] _window;
   private int _windowPosition;
   private int _numAccumulatedBits;
   private int _accumulatedBits;
   private static final int BITS_PER_CHARACTER;
   private static final int POSITION_BIT_COUNT;
   private static final int LENGTH_BIT_COUNT;

   public LZSSInputStream(InputStream in) {
      this._in = in;
      this._window = new byte[4096];
   }

   @Override
   public final int read() {
      byte[] b = new byte[1];
      return this.read(b, 0, 1) != 1 ? -1 : b[0] & 0xFF;
   }

   @Override
   public final int read(byte[] data, int offset, int length) {
      int originalOffset = offset;

      while (offset < length) {
         offset = this.decodeCharacter(data, offset);
      }

      this._numAccumulatedBits = 0;
      this._accumulatedBits = 0;
      return offset - originalOffset;
   }

   private final int decodeCharacter(byte[] data, int offset) {
      int flag = this.inputBit();
      if (flag == 0) {
         byte c = (byte)this.inputBits(8);
         this._window[this._windowPosition] = c;
         data[offset++] = c;
         this._windowPosition++;
         this._windowPosition = this._windowPosition % this._window.length;
      }

      if (flag == 1) {
         int matchOffset = this.inputBits(12);
         int matchLength = this.inputBits(4);

         for (int i = 0; i < matchLength; i++) {
            byte c = this._window[(matchOffset + i) % this._window.length];
            this._window[this._windowPosition] = c;
            data[offset++] = c;
            this._windowPosition++;
            this._windowPosition = this._windowPosition % this._window.length;
         }
      }

      return offset;
   }

   private final byte inputBit() {
      if (this._numAccumulatedBits < 8) {
         this._numAccumulatedBits += 8;
         int read = this._in.read();
         if (read == -1) {
            throw new Object();
         }

         this._accumulatedBits = this._accumulatedBits | read << 32 - this._numAccumulatedBits;
      }

      int return_bit = this._accumulatedBits >>> 31;
      this._accumulatedBits <<= 1;
      this._numAccumulatedBits--;
      return (byte)return_bit;
   }

   private final int inputBits(int num_bits) {
      while (this._numAccumulatedBits < num_bits) {
         this._numAccumulatedBits += 8;
         int read = this._in.read();
         if (read == -1) {
            throw new Object();
         }

         this._accumulatedBits = this._accumulatedBits | read << 32 - this._numAccumulatedBits;
      }

      int return_bits = this._accumulatedBits >>> 31 - num_bits >>> 1;
      this._accumulatedBits <<= num_bits;
      this._numAccumulatedBits -= num_bits;
      return return_bits;
   }
}

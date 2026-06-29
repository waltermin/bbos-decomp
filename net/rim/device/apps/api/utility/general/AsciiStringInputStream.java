package net.rim.device.apps.api.utility.general;

import java.io.InputStream;

public class AsciiStringInputStream extends InputStream {
   private byte[] _input;
   private int _markPosition;
   private int _currentPosition;
   private static String ASCII_ENCODING = "iso-8859-1";

   public AsciiStringInputStream(String asciiinput) {
      try {
         this._input = asciiinput.getBytes(ASCII_ENCODING);
      } finally {
         return;
      }
   }

   @Override
   public int available() {
      return this._input == null ? 0 : this._input.length - this._currentPosition;
   }

   @Override
   public void mark(int readlimit) {
      this._markPosition = this._currentPosition;
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   @Override
   public int read() {
      if (this._input == null) {
         return -1;
      } else {
         return this._currentPosition >= this._input.length ? -1 : this._input[this._currentPosition++] & 0xFF;
      }
   }

   @Override
   public void reset() {
      this._currentPosition = this._markPosition;
   }

   @Override
   public long skip(long n) {
      long newPosition = n + this._currentPosition;
      if (n < 0) {
         return 0;
      }

      if (newPosition > this._input.length) {
         newPosition = this._input.length;
      }

      long lengthSkipped = newPosition - this._currentPosition;
      this._currentPosition = (int)(newPosition & -1);
      return lengthSkipped;
   }
}

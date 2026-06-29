package net.rim.device.api.io;

import java.io.InputStream;

public class SharedInputStream extends InputStream {
   private SharedInputStreamSource _source;
   private int _currentPosition;
   private int _maxPosition;
   private int _startPosition;

   public SharedInputStream(byte[] input) {
      this(new SharedInputStreamSource(input), 0, input.length);
   }

   public SharedInputStream(SharedInputStream input) {
      this(input._source, input._currentPosition, Integer.MAX_VALUE - input._currentPosition);
   }

   public SharedInputStream(SharedInputStream input, int length) {
      this(input._source, input._currentPosition, length);
   }

   private SharedInputStream(SharedInputStreamSource source) {
      this(source, 0, Integer.MAX_VALUE);
   }

   private SharedInputStream(SharedInputStreamSource source, int currentPosition, int length) {
      this._source = source;
      this._currentPosition = currentPosition;
      this._startPosition = currentPosition;
      if (length + currentPosition >= 0 && length >= 0) {
         this._maxPosition = currentPosition + length;
      } else {
         this._maxPosition = Integer.MAX_VALUE;
      }
   }

   public static SharedInputStream getSharedInputStream(InputStream input) {
      return !(input instanceof SharedInputStream) ? new SharedInputStream(new SharedInputStreamSource(input)) : ((SharedInputStream)input).readInputStream();
   }

   public static SharedInputStream getSharedInputStream(byte[] input) {
      if (input == null) {
         throw new IllegalArgumentException();
      } else {
         return new SharedInputStream(new SharedInputStreamSource(input), 0, input.length);
      }
   }

   public static SharedInputStream getSharedInputStream(InputStream input, int length) {
      return !(input instanceof SharedInputStream)
         ? new SharedInputStream(new SharedInputStreamSource(input), 0, length)
         : ((SharedInputStream)input).readInputStream(length);
   }

   public void setLength(int length) {
      if (length + this._startPosition >= 0 && length >= 0) {
         this._maxPosition = this._startPosition + length;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int peek() {
      return this._currentPosition >= this._maxPosition ? -1 : this._source.read(this._currentPosition);
   }

   @Override
   public int read() {
      return this._currentPosition >= this._maxPosition ? -1 : this._source.read(this._currentPosition++);
   }

   @Override
   public int read(byte[] buffer, int offset, int length) {
      if (length < 0) {
         throw new IllegalArgumentException();
      }

      if (this._currentPosition >= this._maxPosition) {
         return -1;
      }

      length = this._source.read(this._currentPosition, buffer, offset, Math.min(length, this._maxPosition - this._currentPosition));
      if (length > 0) {
         this._currentPosition += length;
      }

      return length;
   }

   @Override
   public long skip(long n) {
      if (n < 0) {
         throw new IllegalArgumentException();
      }

      n = this._source.skip(this._currentPosition, n);
      if (this._currentPosition + n >= this._maxPosition) {
         n = this._maxPosition - this._currentPosition;
      }

      this._currentPosition = (int)(this._currentPosition + n);
      return n;
   }

   @Override
   public int available() {
      return Math.min(this._maxPosition - this._currentPosition, this._source.available(this._currentPosition));
   }

   public SharedInputStream readInputStream() {
      return new SharedInputStream(this._source, this._currentPosition, this._maxPosition - this._currentPosition);
   }

   public SharedInputStream readInputStream(int length) {
      SharedInputStream stream = new SharedInputStream(this._source, this._currentPosition, length);
      this.skip(length);
      return stream;
   }

   public int getCurrentPosition() {
      return this._currentPosition;
   }

   public void setCurrentPosition(int currentPosition) {
      if (currentPosition < 0) {
         throw new IllegalArgumentException();
      }

      this._currentPosition = currentPosition;
   }
}

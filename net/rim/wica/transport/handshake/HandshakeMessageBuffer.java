package net.rim.wica.transport.handshake;

public class HandshakeMessageBuffer {
   private int _cursor;
   private byte[] _buffer;
   private static final int FIELD_SIZE_LIMIT = 131072;

   public HandshakeMessageBuffer(int length) throws HandshakeMessageException {
      if (length < 0) {
         throw new HandshakeMessageException();
      }

      this._cursor = 0;
      this._buffer = new byte[length];
   }

   public HandshakeMessageBuffer(byte[] buffer) throws HandshakeMessageException {
      if (buffer == null) {
         throw new HandshakeMessageException();
      }

      this._cursor = 0;
      this._buffer = buffer;
   }

   public byte[] readBytes() throws HandshakeMessageException {
      int length = this.readInt();
      if (length > 131072) {
         throw new HandshakeMessageException();
      }

      byte[] bytes = length == -1 ? null : new byte[length];

      for (int i = 0; i < length; i++) {
         bytes[i] = this.readByte();
      }

      return bytes;
   }

   public void writeBytes(byte[] bytes) throws HandshakeMessageException {
      int length = bytes == null ? -1 : bytes.length;
      if (length > 131072) {
         throw new HandshakeMessageException();
      }

      this.writeInt(length);

      for (int i = 0; i < length; i++) {
         this.writeByte(bytes[i]);
      }
   }

   public byte readByte() throws HandshakeMessageException {
      if (this._cursor == this._buffer.length) {
         throw new HandshakeMessageException();
      }

      byte b = this._buffer[this._cursor];
      this._cursor++;
      return b;
   }

   public void writeByte(byte b) throws HandshakeMessageException {
      if (this._cursor == this._buffer.length) {
         throw new HandshakeMessageException();
      }

      this._buffer[this._cursor] = b;
      this._cursor++;
   }

   public byte[] getBytes() {
      byte[] bytes = new byte[this._cursor];

      for (int i = 0; i < this._cursor; i++) {
         bytes[i] = this._buffer[i];
      }

      return bytes;
   }

   public void writeLong(long n) {
      long number = n;

      for (int i = 0; i < 8; i++) {
         long b = number >>> i * 8 & 255;
         this.writeByte((byte)b);
      }
   }

   public long readLong() {
      long number = 0;

      for (int i = 0; i < 8; i++) {
         long b = 255 & this.readByte();
         number |= b << i * 8;
      }

      return number;
   }

   public void writeInt(int n) {
      int number = n;

      for (int i = 0; i < 4; i++) {
         int b = number >>> i * 8 & 0xFF;
         this.writeByte((byte)b);
      }
   }

   public int readInt() {
      int number = 0;

      for (int i = 0; i < 4; i++) {
         int b = 255 & this.readByte();
         number |= b << i * 8;
      }

      return number;
   }
}

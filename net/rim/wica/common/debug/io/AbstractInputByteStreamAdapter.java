package net.rim.wica.common.debug.io;

import java.io.DataInputStream;
import java.io.InputStream;

public class AbstractInputByteStreamAdapter implements IInputByteStreamAdapter {
   private DataInputStream _dataInputStream;

   protected void init(InputStream inStream) {
      this._dataInputStream = (DataInputStream)(new Object(inStream));
   }

   @Override
   public final int readBuffer(int numBytes, byte[] buffer) {
      if (numBytes > buffer.length) {
         throw new Object(
            ((StringBuffer)(new Object("Trying to read "))).append(numBytes).append(" bytes into buffer of size ").append(buffer.length).toString()
         );
      }

      int readSoFar = 0;

      while (readSoFar < numBytes) {
         readSoFar += this._dataInputStream.read(buffer, readSoFar, numBytes - readSoFar);
      }

      return readSoFar;
   }

   @Override
   public final boolean readBoolean() {
      return this._dataInputStream.readBoolean();
   }

   @Override
   public final int readInt() {
      return this._dataInputStream.readInt();
   }

   @Override
   public final long readLong() {
      return this._dataInputStream.readLong();
   }

   @Override
   public final String readString() {
      return this._dataInputStream.readUTF();
   }
}

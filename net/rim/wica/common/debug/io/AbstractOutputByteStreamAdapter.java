package net.rim.wica.common.debug.io;

import java.io.DataOutputStream;
import java.io.OutputStream;

public class AbstractOutputByteStreamAdapter implements IOutputByteStreamAdapter {
   private DataOutputStream _dataOutputStream;

   protected void init(OutputStream outStream) {
      this._dataOutputStream = (DataOutputStream)(new Object(outStream));
   }

   @Override
   public final void writeBuffer(int numBytes, byte[] buffer) {
      if (numBytes > buffer.length) {
         throw new Object(
            ((StringBuffer)(new Object("Trying to write "))).append(numBytes).append(" bytes from buffer of size ").append(buffer.length).toString()
         );
      }

      this._dataOutputStream.write(buffer, 0, numBytes);
   }

   @Override
   public final void writeBoolean(boolean value) {
      this._dataOutputStream.writeBoolean(value);
   }

   @Override
   public final void writeInt(int value) {
      this._dataOutputStream.writeInt(value);
   }

   @Override
   public final void writeLong(long value) {
      this._dataOutputStream.writeLong(value);
   }

   @Override
   public final void writeString(String value) {
      this._dataOutputStream.writeUTF(value);
   }

   @Override
   public final void flush() {
      this._dataOutputStream.flush();
   }
}

package net.rim.device.apps.internal.explorer.content;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import javax.microedition.io.InputConnection;

final class MediaContentHandlerApplication$ByteArrayInputConnection implements InputConnection {
   private ByteArrayInputStream _bytes;

   @Override
   public final void close() {
      this._bytes.close();
   }

   @Override
   public final InputStream openInputStream() {
      return this._bytes;
   }

   @Override
   public final DataInputStream openDataInputStream() {
      return new DataInputStream(this._bytes);
   }

   public MediaContentHandlerApplication$ByteArrayInputConnection(byte[] data) {
      this._bytes = new ByteArrayInputStream(data);
   }
}

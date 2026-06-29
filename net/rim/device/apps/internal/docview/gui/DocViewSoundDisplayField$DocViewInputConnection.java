package net.rim.device.apps.internal.docview.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.ContentConnection;
import net.rim.device.api.io.IOCancelledException;

final class DocViewSoundDisplayField$DocViewInputConnection implements ContentConnection {
   private boolean _closed;
   private DocViewSoundDisplayField$DocViewInputOutputByteStream _inputStream;
   private final int _totalLength;
   private final DocViewSoundDisplayField this$0;

   @Override
   public final void close() {
      this._closed = true;
   }

   final byte[] getAudioData() {
      return this._totalLength > 0 ? this._inputStream.getAudioData() : null;
   }

   final void addAudioData(byte[] audioData) {
      this._inputStream.appendData(audioData);
   }

   @Override
   public final String getType() {
      return this.this$0._contentType;
   }

   @Override
   public final String getEncoding() {
      return null;
   }

   @Override
   public final long getLength() {
      return this._totalLength;
   }

   @Override
   public final OutputStream openOutputStream() {
      return null;
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      return null;
   }

   @Override
   public final InputStream openInputStream() throws IOCancelledException {
      if (this._closed) {
         throw new IOCancelledException();
      } else {
         return this._inputStream;
      }
   }

   @Override
   public final DataInputStream openDataInputStream() {
      InputStream in = this.openInputStream();
      return !(in instanceof DataInputStream) ? new DataInputStream(in) : (DataInputStream)in;
   }

   DocViewSoundDisplayField$DocViewInputConnection(DocViewSoundDisplayField _1, byte[] initialData, int totalLength) {
      this.this$0 = _1;
      this._inputStream = new DocViewSoundDisplayField$DocViewInputOutputByteStream(_1, initialData, totalLength);
      this._totalLength = totalLength;
   }
}

package net.rim.device.apps.internal.browser.stack;

import java.io.DataInputStream;
import java.io.InputStream;
import javax.microedition.io.InputConnection;

public final class RTSPConnection implements InputConnection {
   private String _url;
   private String _userAgent;

   @Override
   public final void close() {
   }

   public final String getUserAgent() {
      return this._userAgent;
   }

   public final String getURL() {
      return this._url;
   }

   @Override
   public final InputStream openInputStream() {
      return (InputStream)(new Object(new byte[0]));
   }

   @Override
   public final DataInputStream openDataInputStream() {
      return (DataInputStream)(new Object(this.openInputStream()));
   }

   public RTSPConnection(String url, String userAgent) {
      this._url = url;
      this._userAgent = userAgent;
   }
}

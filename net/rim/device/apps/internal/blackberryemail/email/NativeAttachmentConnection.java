package net.rim.device.apps.internal.blackberryemail.email;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.ContentConnection;
import net.rim.device.apps.api.framework.model.URLProvider;

public class NativeAttachmentConnection implements ContentConnection, URLProvider {
   private byte[] _data;
   private String _contentType;
   private String _url;

   @Override
   public void close() {
   }

   @Override
   public int getURLType() {
      return 0;
   }

   @Override
   public String getType() {
      return this._contentType;
   }

   @Override
   public InputStream openInputStream() {
      return new ByteArrayInputStream(this._data);
   }

   @Override
   public DataInputStream openDataInputStream() {
      return new DataInputStream(this.openInputStream());
   }

   @Override
   public OutputStream openOutputStream() {
      return null;
   }

   @Override
   public DataOutputStream openDataOutputStream() {
      return null;
   }

   @Override
   public String getEncoding() {
      return null;
   }

   @Override
   public long getLength() {
      return this._data.length;
   }

   @Override
   public String getURL() {
      return this._url;
   }

   public NativeAttachmentConnection(byte[] data, String contentType, String url) {
      this._data = data;
      this._contentType = contentType;
      this._url = url;
   }
}

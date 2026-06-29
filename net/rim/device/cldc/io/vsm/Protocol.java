package net.rim.device.cldc.io.vsm;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.system.Branding;

public final class Protocol implements ConnectionBaseInterface, HttpConnection {
   private byte[] _data;
   private String _type;
   private String _name;
   private int _responseCode;

   @Override
   public final void close() {
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) {
      int semicolon = name.indexOf(59);
      if (semicolon != -1) {
         name = name.substring(0, semicolon);
      }

      this._name = name;
      if (name.equals("/help")) {
         this._data = Branding.getData(20480);
      }

      if (this._data != null) {
         this._responseCode = 200;
         this._type = "application/vnd.wap.wmlc";
         return this;
      } else {
         this._data = new byte[0];
         this._responseCode = 404;
         return this;
      }
   }

   @Override
   public final String getType() {
      return this._type;
   }

   @Override
   public final String getEncoding() {
      return null;
   }

   @Override
   public final long getLength() {
      return this._data.length;
   }

   @Override
   public final InputStream openInputStream() {
      return (InputStream)(new Object(this._data));
   }

   @Override
   public final DataInputStream openDataInputStream() {
      ByteArrayInputStream is = (ByteArrayInputStream)(new Object(this._data));
      return (DataInputStream)(new Object(is));
   }

   @Override
   public final OutputStream openOutputStream() {
      throw new Object("Read only");
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      throw new Object("Read only");
   }

   @Override
   public final int getProperties(String name) {
      return 2;
   }

   @Override
   public final String getURL() {
      return ((StringBuffer)(new Object("vsm:"))).append(this._name).toString();
   }

   @Override
   public final String getProtocol() {
      return "vsm";
   }

   @Override
   public final String getHost() {
      return "";
   }

   @Override
   public final String getFile() {
      return "";
   }

   @Override
   public final String getRef() {
      int fragmentIdentifier = this._name.indexOf(35);
      return fragmentIdentifier != -1 ? this._name.substring(fragmentIdentifier + 1) : null;
   }

   @Override
   public final String getQuery() {
      return null;
   }

   @Override
   public final int getPort() {
      return -1;
   }

   @Override
   public final String getRequestMethod() {
      return "GET";
   }

   @Override
   public final void setRequestMethod(String method) {
   }

   @Override
   public final String getRequestProperty(String key) {
      return null;
   }

   @Override
   public final void setRequestProperty(String key, String value) {
   }

   @Override
   public final int getResponseCode() {
      return this._responseCode;
   }

   @Override
   public final String getResponseMessage() {
      return this._responseCode == 200 ? "OK" : "Error";
   }

   @Override
   public final long getExpiration() {
      return 0;
   }

   @Override
   public final long getDate() {
      return 0;
   }

   @Override
   public final long getLastModified() {
      return 0;
   }

   @Override
   public final String getHeaderField(String name) {
      return null;
   }

   @Override
   public final int getHeaderFieldInt(String name, int def) {
      return def;
   }

   @Override
   public final long getHeaderFieldDate(String name, long def) {
      return def;
   }

   @Override
   public final String getHeaderField(int n) {
      return n == 0 ? this._type : null;
   }

   @Override
   public final String getHeaderFieldKey(int n) {
      return n == 0 && this._type != null ? "Content-Type" : null;
   }
}

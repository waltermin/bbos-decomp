package net.rim.device.api.crypto.cms;

import java.io.InputStream;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

public class CMSInputStream extends InputStream {
   protected InputStream _input;
   private byte[] _buffer;
   protected InputStream _data;
   protected OID _contentType;

   protected CMSInputStream(InputStream input) {
      if (input == null) {
         throw new Object();
      }

      this._input = input;
   }

   public int getContentType() {
      if (this._contentType == null) {
         return -1;
      } else if (this._contentType.equals(OIDs.getOID(541859388))) {
         return 10;
      } else if (this._contentType.equals(OIDs.getOID(542121532))) {
         return 11;
      } else if (this._contentType.equals(OIDs.getOID(542383676))) {
         return 12;
      } else if (this._contentType.equals(OIDs.getOID(-1721352904))) {
         return 13;
      } else if (this._contentType.equals(OIDs.getOID(-1721352925))) {
         return 14;
      } else if (this._contentType.equals(OIDs.getOID(-477712249))) {
         return 15;
      } else {
         throw new Object();
      }
   }

   public CMSInputStream getCMSInputStream() {
      return !(this._data instanceof CMSInputStream) ? null : (CMSInputStream)this._data;
   }

   public static CMSInputStream getCMSInputStream(InputStream inputStream, KeyStore keyStore) {
      return getCMSInputStream(inputStream, keyStore, null, true);
   }

   public static CMSInputStream getCMSInputStream(InputStream inputStream, KeyStore keyStore, SymmetricKey sessionKey) {
      return getCMSInputStream(getCMSContext(inputStream), keyStore, sessionKey, true);
   }

   public static CMSInputStream getCMSInputStream(InputStream inputStream, KeyStore keyStore, SymmetricKey sessionKey, boolean displayUI) {
      return getCMSInputStream(getCMSContext(inputStream), keyStore, sessionKey, displayUI);
   }

   public static CMSContext getCMSContext(InputStream inputStream) {
      return new CMSContext(inputStream);
   }

   public static CMSInputStream getCMSInputStream(CMSContext context, KeyStore keyStore) {
      return getCMSInputStream(context, keyStore, null, true);
   }

   public static CMSInputStream getCMSInputStream(CMSContext context, KeyStore keyStore, SymmetricKey sessionKey) {
      return context.getCMSInputStream(keyStore, sessionKey, true);
   }

   public static CMSInputStream getCMSInputStream(CMSContext context, KeyStore keyStore, SymmetricKey sessionKey, boolean displayUI) {
      return context.getCMSInputStream(keyStore, sessionKey, displayUI);
   }

   @Override
   public int read(byte[] _1, int _2, int _3) {
      throw null;
   }

   @Override
   public int read() {
      if (this._buffer == null) {
         this._buffer = new byte[1];
      }

      return this.read(this._buffer, 0, 1) < 0 ? -1 : this._buffer[0] & 0xFF;
   }

   @Override
   public int read(byte[] buffer) {
      return this.read(buffer, 0, buffer == null ? 0 : buffer.length);
   }

   @Override
   public long skip(long n) {
      return this._input.skip(n);
   }

   @Override
   public int available() {
      return this._input.available();
   }

   @Override
   public void close() {
      this._input.close();
   }

   public boolean isSigned() {
      throw null;
   }

   public boolean isEncrypted() {
      throw null;
   }

   public void setData(InputStream _1) {
      throw null;
   }

   public boolean isContentComplete() {
      throw null;
   }
}

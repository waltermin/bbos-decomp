package net.rim.device.cldc.io.data;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.utility.MalformedURLException;

public final class Protocol implements ConnectionBaseInterface, HttpConnection {
   private String _name;
   private String _type;
   private byte[] _data;
   private boolean _base64Encoded;
   private int _responseCode = 200;

   @Override
   public final void close() {
   }

   @Override
   public final Connection openPrim(String name, int mode, boolean timeouts) throws MalformedURLException {
      int endOfType = name.indexOf(44);
      if (endOfType == -1) {
         throw new MalformedURLException("Missing required comma");
      }

      int semicolon = name.indexOf(59, endOfType + 1);
      if (semicolon != -1) {
         name = name.substring(0, semicolon);
      }

      this._name = name;
      int endOfData = name.indexOf(35, endOfType + 1);
      if (endOfData == -1) {
         endOfData = name.length();
      }

      this._data = uriDecodeToBytes(name.substring(endOfType + 1, endOfData));
      this._base64Encoded = endOfType >= 7 && StringUtilities.regionMatches(name, true, endOfType - 7, ";base64", 0, 7, 1701707776);
      if (this._base64Encoded) {
         endOfType -= 7;
      }

      this._type = uriDecodeToString(name.substring(0, endOfType)).trim();
      if (this._type.length() == 0) {
         this._type = "text/plain;charset=US-ASCII";
         return this;
      }

      if (this._type.charAt(0) == ';') {
         this._type = "text/plain" + this._type;
      }

      return this;
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
      return this._base64Encoded ? -1 : this._data.length;
   }

   @Override
   public final InputStream openInputStream() {
      InputStream is = new ByteArrayInputStream(this._data);
      return this._base64Encoded ? new Base64InputStream(is) : is;
   }

   @Override
   public final DataInputStream openDataInputStream() {
      return new DataInputStream(this.openInputStream());
   }

   @Override
   public final OutputStream openOutputStream() throws IOException {
      throw new IOException("Read only");
   }

   @Override
   public final DataOutputStream openDataOutputStream() throws IOException {
      throw new IOException("Read only");
   }

   @Override
   public final int getProperties(String name) {
      return 2;
   }

   @Override
   public final String getURL() {
      return "data:" + this._name;
   }

   @Override
   public final String getProtocol() {
      return "data";
   }

   @Override
   public final String getHost() {
      return null;
   }

   @Override
   public final String getFile() {
      return null;
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
      return "OK";
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
      return StringUtilities.strEqualIgnoreCase(name, "Content-Type", 1701707776) ? this._type : null;
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

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final String uriDecodeToString(String str) {
      if (str.indexOf(37) == -1) {
         return str;
      }

      int length = str.length();
      StringBuffer buffer = new StringBuffer(length);

      for (int i = 0; i < length; i++) {
         char ch = str.charAt(i);
         if (ch == '%' && i + 2 < length) {
            char digit1 = str.charAt(++i);
            char digit2 = str.charAt(++i);
            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               int nfe = NumberUtilities.hexDigitToInt(digit1);
               int h0 = NumberUtilities.hexDigitToInt(digit2);
               buffer.append((char)(nfe << 4 | h0));
               var10 = false;
            } finally {
               if (var10) {
                  buffer.append(ch);
                  buffer.append(digit1);
                  buffer.append(digit2);
                  continue;
               }
            }
         } else {
            buffer.append(ch);
         }
      }

      return buffer.toString();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final byte[] uriDecodeToBytes(String str) {
      int length = str.length();
      ByteArrayOutputStream baos = new ByteArrayOutputStream(length);

      for (int i = 0; i < length; i++) {
         char ch = str.charAt(i);
         if (ch == '%' && i + 2 < length) {
            char digit1 = str.charAt(++i);
            char digit2 = str.charAt(++i);
            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               int nfe = NumberUtilities.hexDigitToInt(digit1);
               int h0 = NumberUtilities.hexDigitToInt(digit2);
               baos.write(nfe << 4 | h0);
               var10 = false;
            } finally {
               if (var10) {
                  baos.write(ch);
                  baos.write(digit1);
                  baos.write(digit2);
                  continue;
               }
            }
         } else {
            baos.write(ch);
         }
      }

      return baos.toByteArray();
   }
}

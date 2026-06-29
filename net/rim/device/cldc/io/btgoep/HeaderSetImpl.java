package net.rim.device.cldc.io.btgoep;

import java.io.IOException;
import java.util.Calendar;
import javax.obex.HeaderSet;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;

public class HeaderSetImpl implements HeaderSet {
   private int[] _headers;
   private Object[] _objects;
   private int _responseCode = -1;
   byte[] _nonce;
   public static final int BODY = 72;
   public static final int END_OF_BODY = 73;
   public static final int CONNECTION_ID = 203;
   public static final int AUTH_CHALLENGE = 77;
   public static final int AUTH_RESPONSE = 78;
   private static final String NONCE_KEY = "RIM-NONCE-KEY";

   public HeaderSetImpl() {
      this.reset();
   }

   public void reset() {
      this._headers = new int[0];
      this._objects = new Object[0];
   }

   public HeaderSetImpl(int responseCode) {
      this._responseCode = responseCode;
   }

   public HeaderSetImpl(int responseCode, DataBuffer data) {
      this(data);
      this._responseCode = responseCode;
   }

   public HeaderSetImpl(DataBuffer data) throws IOException {
      this();

      while (!data.eof()) {
         int header;
         Object o;
         header = data.readUnsignedByte();
         label49:
         switch (header & 192) {
            case 0:
               int length = data.readUnsignedShort() - 3;
               if (length == 0) {
                  o = "";
               } else {
                  int numChars = (length >> 1) - 1;
                  char[] array = new char[numChars];

                  for (int i = 0; i < numChars; i++) {
                     array[i] = data.readChar();
                  }

                  data.skipBytes(2);
                  o = new String(array);
               }
               break;
            case 64:
               int length = data.readUnsignedShort() - 3;
               byte[] array = new byte[length];
               data.read(array, 0, length);
               switch (header) {
                  case 66:
                     if (length > 0) {
                        length--;
                     }
                  case 68:
                  case 71:
                     o = new String(array, 0, length);
                     break label49;
                  default:
                     o = array;
                     break label49;
               }
            case 128:
               o = new Byte(data.readByte());
               break;
            case 192:
               o = new Long(data.readInt() & 4294967295L);
               break;
            default:
               throw new IOException("Unknown data type: " + Integer.toHexString(header));
         }

         int i = this._headers.length - 1;

         while (i >= 0 && this._headers[i] != header) {
            i--;
         }

         if (i >= 0) {
            this._objects[i] = o;
         } else {
            Arrays.add(this._headers, header);
            Arrays.add(this._objects, o);
         }
      }
   }

   void setResponseCode(int responseCode) {
      this._responseCode = responseCode;
   }

   void removeHeader(int header) {
      int index = -1;
      int length = this._headers.length;

      for (int i = 0; i < length; i++) {
         if (this._headers[i] == header) {
            index = i;
            break;
         }
      }

      if (index != -1) {
         int[] headers = new int[length - 1];
         Object[] objects = new Object[length - 1];
         int j = 0;

         for (int i = 0; i < length; i++) {
            if (i != index) {
               headers[j] = this._headers[i];
               objects[j] = this._objects[i];
               j++;
            }
         }

         this._headers = headers;
         this._objects = objects;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public synchronized DataBuffer getData() throws IOException {
      int length = this._headers.length;
      if (length == 0) {
         return null;
      }

      DataBuffer db = new DataBuffer();

      for (int x = 0; x < length; x++) {
         int header = this._headers[x];
         Object o = this._objects[x];
         db.writeByte(header);
         if (!(o instanceof String)) {
            if (!(o instanceof Byte)) {
               if (!(o instanceof Long)) {
                  byte[] b = (byte[])o;
                  db.writeShort(b.length + 3);
                  db.write(b, 0, b.length);
               } else {
                  Long l = (Long)o;
                  db.writeInt((int)l.longValue());
               }
            } else {
               Byte b = (Byte)o;
               db.writeByte(b);
            }
         } else {
            String s = (String)o;
            if ((header & 192) == 0) {
               db.writeShort(s.length() * 2 + 5);

               try {
                  db.write(s.getBytes("UnicodeBigUnmarked"));
               } catch (Throwable var9) {
                  throw new IOException(ex.getMessage());
               }

               db.writeShort(0);
            } else {
               db.writeShort(s.length() + 4);
               byte[] b = s.getBytes();
               db.write(b, 0, b.length);
               db.writeByte(0);
            }
         }
      }

      db.rewind();
      return db;
   }

   @Override
   public synchronized void setHeader(int headerID, Object headerValue) {
      switch (headerID) {
         case 1:
         case 5:
         case 66:
            if (headerValue != null && !(headerValue instanceof String)) {
               throw new IllegalArgumentException();
            }
            break;
         case 68:
         case 196:
            if (headerValue != null && !(headerValue instanceof Calendar)) {
               throw new IllegalArgumentException();
            }
            break;
         case 70:
         case 71:
         case 74:
         case 76:
         case 77:
         case 78:
         case 79:
            if (headerValue != null && !(headerValue instanceof byte[])) {
               throw new IllegalArgumentException();
            }
            break;
         case 192:
         case 195:
         case 203:
            if (headerValue != null && !(headerValue instanceof Long)) {
               throw new IllegalArgumentException();
            }
            break;
         default:
            if (headerID >= 48 && headerID <= 63
               ? headerValue != null && !(headerValue instanceof String)
               : (
                  headerID >= 112 && headerID <= 127
                     ? headerValue != null && !(headerValue instanceof byte[])
                     : (
                        headerID >= 176 && headerID <= 191
                           ? headerValue != null && !(headerValue instanceof Byte)
                           : headerID < 240 || headerID > 255 || headerValue != null && !(headerValue instanceof Long)
                     )
               )) {
               throw new IllegalArgumentException();
            }
      }

      if (headerValue == null) {
         for (int i = this._headers.length - 1; i >= 0; i--) {
            if (this._headers[i] == headerID) {
               Arrays.removeAt(this._headers, i);
               Arrays.removeAt(this._objects, i);
            }
         }
      } else {
         if (headerValue instanceof Long) {
            Long l = (Long)headerValue;
            long ll = l;
            if (ll < 0 || ll > 4294967295L) {
               throw new IllegalArgumentException();
            }
         }

         for (int i = this._headers.length - 1; i >= 0; i--) {
            if (this._headers[i] == headerID) {
               this._objects[i] = headerValue;
               return;
            }
         }

         Arrays.add(this._headers, headerID);
         Arrays.add(this._objects, headerValue);
      }
   }

   @Override
   public synchronized Object getHeader(int headerID) {
      switch (headerID) {
         case 1:
         case 5:
         case 66:
         case 68:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 76:
         case 77:
         case 78:
         case 79:
         case 192:
         case 195:
         case 196:
         case 203:
            break;
         default:
            if ((headerID < 48 || headerID > 63)
               && (headerID < 112 || headerID > 127)
               && (headerID < 176 || headerID > 191)
               && (headerID < 240 || headerID > 255)) {
               throw new IllegalArgumentException();
            }
      }

      int length = this._headers.length;

      for (int x = 0; x < length; x++) {
         if (this._headers[x] == headerID) {
            return this._objects[x];
         }
      }

      return null;
   }

   @Override
   public int[] getHeaderList() {
      return this._headers.length == 0 ? null : this._headers;
   }

   @Override
   public void createAuthenticationChallenge(String realm, boolean userID, boolean access) {
      DataBuffer buffer = new DataBuffer();
      byte[] digestData = null;

      label36:
      try {
         String nouce = System.currentTimeMillis() + 58 + "RIM-NONCE-KEY";
         Digest digest = DigestFactory.getInstance("MD5");
         digest.update(nouce.getBytes());
         digestData = digest.getDigest();
         buffer.writeByte(0);
         buffer.writeByte(digestData.length);
         buffer.write(digestData);
         int option = 0;
         if (userID) {
            option |= 1;
         }

         if (!access) {
            option |= 2;
         }

         buffer.writeByte(1);
         buffer.writeByte(1);
         buffer.writeByte(option);
         if (realm != null) {
            buffer.writeByte(2);
            buffer.writeByte(realm.getBytes().length + 1);
            buffer.writeByte(1);
            buffer.write(realm.getBytes());
         }
      } finally {
         break label36;
      }

      this._nonce = digestData;
      this.setHeader(77, buffer.getArray());
   }

   @Override
   public int getResponseCode() throws IOException {
      if (this._responseCode == -1) {
         throw new IOException();
      } else {
         return this._responseCode;
      }
   }

   @Override
   public synchronized String toString() {
      StringBuffer sb = new StringBuffer();
      int numHeaders = this._headers.length;

      for (int x = 0; x < numHeaders; x++) {
         int header = this._headers[x];
         Object o = this._objects[x];
         sb.append("Header: ");
         switch (header) {
            case 1:
               sb.append("Name");
               break;
            case 5:
               sb.append("Description");
               break;
            case 66:
               sb.append("Type");
               break;
            case 68:
               sb.append("Time (ISO 8601)");
               break;
            case 70:
               sb.append("Target");
               break;
            case 71:
               sb.append("HTTP");
               break;
            case 72:
               sb.append("Body");
               break;
            case 73:
               sb.append("End of Body");
               break;
            case 74:
               sb.append("Who");
               break;
            case 76:
               sb.append("App Parameters");
               break;
            case 77:
               sb.append("Auth Challenge");
               break;
            case 78:
               sb.append("Auth Response");
               break;
            case 79:
               sb.append("Object Class");
               break;
            case 192:
               sb.append("Count");
               break;
            case 195:
               sb.append("Length");
               break;
            case 196:
               sb.append("Time (4 byte)");
               break;
            case 203:
               sb.append("Connection Id");
               break;
            default:
               sb.append("User defined 0x" + Integer.toHexString(header));
         }

         sb.append(": ");
         if (!(o instanceof byte[])) {
            if (!(o instanceof Byte)) {
               if (!(o instanceof Long)) {
                  sb.append(o);
               } else {
                  Long l = (Long)o;
                  sb.append("0x");
                  sb.append(Integer.toHexString((int)l.longValue()));
               }
            } else {
               Byte b = (Byte)o;
               sb.append("0x");
               sb.append(Integer.toHexString(b & 255));
            }
         } else {
            byte[] b = (byte[])o;
            int length = b.length;

            for (int i = 0; i < length; i++) {
               String s = Integer.toHexString(b[i] & 255);
               if (s.length() == 1) {
                  sb.append('0');
               }

               sb.append(s);
               sb.append(' ');
            }
         }

         sb.append("\r\n");
      }

      return sb.toString();
   }
}

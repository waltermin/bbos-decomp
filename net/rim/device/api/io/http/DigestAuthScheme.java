package net.rim.device.api.io.http;

import java.util.Hashtable;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;

public final class DigestAuthScheme extends AuthScheme {
   private static int _nc = 0;

   DigestAuthScheme(Hashtable parms) {
      super(parms);
   }

   @Override
   public final String getAuthResponse() {
      String username = (String)super._parms.get("username");
      String password = (String)super._parms.get("password");
      String realm = (String)super._parms.get("realm");
      String nonce = (String)super._parms.get("nonce");
      String algorithm = (String)super._parms.get("algorithm");
      String method = (String)super._parms.get("method");
      String uri = (String)super._parms.get("uri");
      String qop = (String)super._parms.get("qop");
      String opaque = (String)super._parms.get("opaque");
      if (username != null && password != null && realm != null && nonce != null && method != null && uri != null) {
         if (algorithm == null) {
            algorithm = "MD5";
         }

         if (qop == null) {
            qop = "auth";
         }

         MD5Digest md5 = (MD5Digest)(new Object());
         md5.update(Long.toString(System.currentTimeMillis()).getBytes());
         String cnonce = this.toHex(md5.getDigest());
         String nc = NumberUtilities.toString(++_nc, 16, 8);
         md5.reset();
         md5.update(username.getBytes());
         md5.update(58);
         md5.update(realm.getBytes());
         md5.update(58);
         md5.update(password.getBytes());
         if (StringUtilities.strEqualIgnoreCase(algorithm, "md5-sess", 1701707776)) {
            byte[] h1 = md5.getDigest();
            md5.reset();
            md5.update(h1);
            md5.update(58);
            md5.update(nonce.getBytes());
            md5.update(58);
            md5.update(cnonce.getBytes());
         }

         String a1Hex = this.toHex(md5.getDigest());
         md5.reset();
         md5.update(method.getBytes());
         md5.update(58);
         md5.update(uri.getBytes());
         byte[] ha2 = md5.getDigest();
         md5.reset();
         md5.update(a1Hex.getBytes());
         md5.update(58);
         md5.update(nonce.getBytes());
         md5.update(58);
         if (qop != null) {
            md5.update(nc.getBytes());
            md5.update(58);
            md5.update(cnonce.getBytes());
            md5.update(58);
            md5.update(qop.getBytes());
            md5.update(58);
         }

         md5.update(this.toHex(ha2).getBytes());
         StringBuffer buffer = (StringBuffer)(new Object("Digest username=\""));
         buffer.append(username).append("\", realm=\"");
         buffer.append(realm).append("\", nonce=\"");
         buffer.append(nonce).append("\", uri=\"");
         buffer.append(uri).append('"');
         if (qop != null) {
            buffer.append(", qop=auth");
         }

         if (algorithm != null) {
            buffer.append(", algorithm=\"");
            buffer.append(algorithm).append('"');
         }

         if (qop != null) {
            buffer.append(", nc=");
            buffer.append(nc).append(", cnonce=\"");
            buffer.append(cnonce).append('"');
         }

         buffer.append(", response=\"");
         buffer.append(this.toHex(md5.getDigest())).append('"');
         if (opaque != null) {
            buffer.append(", opaque=\"");
            buffer.append(opaque).append('"');
         }

         return buffer.toString();
      } else {
         throw new Object();
      }
   }

   @Override
   public final void updateAuth(String authInfo) {
      int currentIndex = 0;

      int equalIndex;
      while ((equalIndex = authInfo.indexOf(61, currentIndex)) > 0) {
         String name = authInfo.substring(currentIndex, equalIndex).trim();
         if (StringUtilities.strEqualIgnoreCase(name, "nextnonce", 1701707776)) {
            currentIndex = AuthScheme.readValue("nonce", super._parms, authInfo, equalIndex);
         } else if (StringUtilities.strEqualIgnoreCase(name, "response-auth", 1701707776)) {
            currentIndex = AuthScheme.readValue("response-auth", super._parms, authInfo, equalIndex);
         }

         int commaIndex = authInfo.indexOf(44, currentIndex);
         int semicolonIndex = authInfo.indexOf(59, currentIndex);
         if (commaIndex == -1 || semicolonIndex != -1 && semicolonIndex < commaIndex) {
            commaIndex = semicolonIndex;
         }

         if (commaIndex < 0) {
            return;
         }

         currentIndex = commaIndex + 1;
      }
   }

   private final String toHex(byte[] data) {
      StringBuffer buffer = (StringBuffer)(new Object(data.length << 1));

      for (int i = 0; i < data.length; i++) {
         buffer.append(NumberUtilities.intToHexDigit(data[i] >> 4));
         buffer.append(NumberUtilities.intToHexDigit(data[i]));
      }

      return buffer.toString();
   }

   @Override
   public final int getType() {
      return 1;
   }
}

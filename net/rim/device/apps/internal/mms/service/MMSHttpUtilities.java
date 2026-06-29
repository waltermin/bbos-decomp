package net.rim.device.apps.internal.mms.service;

import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.util.UAProf;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Phone;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.core.AcceptValueProviderRegistry;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.apps.internal.mms.options.MMSTransportServiceBook;
import net.rim.vm.Array;

final class MMSHttpUtilities {
   private static final String PROFILE;
   private static final String[] _rejectedTypes = new String[]{
      "application/java-archive",
      "application/java",
      "application/pgp-keys",
      "application/vnd.rim.css;v=1",
      "application/vnd.rim.html",
      "application/vnd.rim.location",
      "application/vnd.rim.ucs",
      "application/vnd.wap.cert-response",
      "application/vnd.wap.signed-certificate",
      "application/vnd.wap.wtls-ca-certificate",
      "application/x-vnd.rim.pme",
      "application/x-vnd.rim.pme.b",
      "application/x-x509-ca-cert",
      "application/x-x509-email-cert",
      "application/x-x509-server-cert",
      "application/x-x509-user-cert",
      "image/pme",
      "image/vnd.rim.png",
      "image/x-rdi",
      "image/x-rgi",
      "image/x-rpi",
      "image/x-rwi",
      "text/vnd.rim.location",
      "text/vnd.sun.j2me.app-descriptor"
   };

   static final void readAll(InputStream inputstream, byte[] data) {
      byte[] buf = new byte[4000];

      while (true) {
         int count = inputstream.read(buf);
         if (count < 0) {
            System.out.println(((StringBuffer)(new Object("MMS readAll "))).append(data.length).append(" bytes.").toString());
            return;
         }

         if (count > 0) {
            int offset = data.length;
            Array.resize(data, offset + count);
            System.arraycopy(buf, 0, data, offset, count);
         }
      }
   }

   static final void setRequestProperties(HttpConnection connection, HttpHeaders headers) {
      if (headers != null) {
         int size = headers.size();

         for (int i = 0; i < size; i++) {
            String name = headers.getPropertyKey(i);
            String value = headers.getPropertyValue(i);
            connection.setRequestProperty(name, value);
         }
      }
   }

   static final HttpHeaders getStandardSendHeaders(int contentLength) {
      HttpHeaders headers = (HttpHeaders)(new Object());
      headers.setProperty("User-Agent", getUserAgentName());
      headers.setProperty("profile", getUAProfUrl());
      headers.setProperty("content-type", "application/vnd.wap.mms-message");
      headers.setProperty("content-length", Integer.toString(contentLength));
      headers.setProperty("Accept", "*/*");
      addAuthenticationHeaders(headers);
      return headers;
   }

   static final HttpHeaders getStandardRequestHeaders() {
      HttpHeaders headers = (HttpHeaders)(new Object());
      headers.setProperty("User-Agent", getUserAgentName());
      headers.setProperty("Accept", getAcceptTypes());
      headers.setProperty("Accept-Charset", AcceptValueProviderRegistry.getAcceptCharsetValues());
      headers.setProperty("Accept-Language", Locale.getDefault().getLanguage());
      headers.setProperty("profile", getUAProfUrl());
      addAuthenticationHeaders(headers);
      return headers;
   }

   private static final int getTypeLen(String allTypes, int pos) {
      int len = allTypes.indexOf(44, pos);
      if (len < 0) {
         len = allTypes.length();
      }

      return len - pos;
   }

   private static final void appendType(String allTypes, int pos, StringBuffer list) {
      if (list.length() != 0) {
         list.append(',');
      }

      int len = getTypeLen(allTypes, pos);
      StringUtilities.append(list, allTypes, pos, len);
   }

   private static final boolean isAcceptedType(String allTypes, int pos) {
      int typeLen = getTypeLen(allTypes, pos);

      for (int i = 0; i < _rejectedTypes.length; i++) {
         int rejectLen = _rejectedTypes[i].length();
         if (rejectLen == typeLen && allTypes.regionMatches(true, pos, _rejectedTypes[i], 0, typeLen)) {
            return false;
         }
      }

      return true;
   }

   private static final String buildAcceptList(String allTypes) {
      if (allTypes == null) {
         return null;
      }

      StringBuffer acceptList = (StringBuffer)(new Object(1000));
      int pos = 0;

      for (int next = allTypes.indexOf(44, pos); next >= 0; next = allTypes.indexOf(44, pos)) {
         if (isAcceptedType(allTypes, pos)) {
            appendType(allTypes, pos, acceptList);
         }

         pos = next + 1;
      }

      if (pos < allTypes.length() && isAcceptedType(allTypes, pos)) {
         appendType(allTypes, pos, acceptList);
      }

      return acceptList.toString();
   }

   private static final String getAcceptTypes() {
      StringBuffer acceptTypes = (StringBuffer)(new Object());
      String acceptList = buildAcceptList(RenderingSession.getNewInstance().getAcceptTypes());
      acceptTypes.append(acceptList);
      acceptTypes.append(",application/vnd.wap.coc");
      acceptTypes.append(",application/vnd.wap.slc");
      acceptTypes.append(",application/vnd.wap.sic");
      if (MMSTransportServiceBook.isWAPServiceRecord()) {
         acceptTypes.append(",application/vnd.wap.sia");
      }

      return acceptTypes.toString();
   }

   private static final String getUAProfUrl() {
      String url = MMSClientServiceBook.getUAProfUrl();
      if (url == null || url.length() == 0) {
         url = UAProf.getDefaultUAProfURI(true);
      }

      return url;
   }

   private static final String getUserAgentName() {
      String name = MMSClientServiceBook.getUserAgentName();
      if (name == null || name.length() == 0) {
         name = MMSTransportServiceBook.getUserAgentName();
      }

      return name;
   }

   private static final void addAuthenticationHeaders(HttpHeaders headers) {
      addAuthenticationHeaders(headers, MMSTransportServiceBook.getAuthenticationHeader());
   }

   public static final void addAuthenticationHeaders(HttpHeaders headers, String authHeader) {
      if (authHeader != null) {
         if (authHeader.indexOf(61) < 0) {
            String msisdn = getMSISDN();
            if (msisdn != null) {
               headers.setProperty(authHeader, msisdn);
            }
         } else {
            authHeader = strReplace(authHeader, "[MSISDN]", getMSISDN());
            int startIndex = 0;

            while (startIndex < authHeader.length()) {
               int endIndex = authHeader.indexOf(59, startIndex);
               if (endIndex <= startIndex) {
                  endIndex = authHeader.length();
               }

               int separatorIndex = authHeader.indexOf(61, startIndex);
               if (separatorIndex > startIndex && separatorIndex < endIndex) {
                  String name = authHeader.substring(startIndex, separatorIndex);
                  String value = authHeader.substring(separatorIndex + 1, endIndex);
                  headers.setProperty(name, value);
               } else {
                  String name = authHeader.substring(startIndex, endIndex);
                  headers.setProperty(name, "");
               }

               startIndex = endIndex + 1;
            }
         }
      }
   }

   private static final String strReplace(String str, String find, String replace) {
      while (true) {
         int index = str.indexOf(find);
         if (index < 0) {
            return str;
         }

         str = ((StringBuffer)(new Object())).append(str.substring(0, index)).append(replace).append(str.substring(index + find.length())).toString();
      }
   }

   private static final String getMSISDN() {
      String msisdn = null;

      try {
         return Phone.getInstance().getNumber(0);
      } finally {
         ;
      }
   }
}

package net.rim.device.cldc.io.utility;

import net.rim.device.api.util.StringUtilities;

public class URL {
   private String _urlWithoutRIMParams;
   private String _scheme;
   private String _host;
   private int _port;
   private String _path;
   private String _query;
   private String _fragment;
   private URLParameters _rimParameters;
   private static final String[] RIM_PARAMETERS = new String[]{
      ";wapgatewayip=",
      ";deviceside=",
      ";apn=",
      ";connectiontimeout=",
      ";connectionhandler=",
      ";rdhttps",
      ";trustall",
      ";endtoendrequired",
      ";endtoenddesired",
      ";wapgatewayport=",
      ";wapgatewayapn=",
      ";wapsourceip=",
      ";wapsourceport=",
      ";wapsegmentationmode=",
      ";wapenablewtls=",
      ";wapbearer=",
      ";tunnelauthusername=",
      ";tunnelauthpassword=",
      ";usepipe=",
      ";connectionuid=",
      ";tlsversion=",
      ";connectiontype=",
      ";usefilter=",
      ";encryptrequired=",
      ";usecompression=",
      ";connectionsetup=",
      ";flowcontroltimeout=",
      ";specificuid=",
      ";interface=",
      ";retrynocontext=",
      ";localport=",
      ";sessiontimeout="
   };
   private static final String STRING_NetPart = "//";

   public URL(String url) {
      this.initialize(url);
   }

   public URL(String scheme, String restOfUrl) {
      if (scheme != null && restOfUrl != null) {
         StringBuffer temp = new StringBuffer();
         temp.append(scheme).append(':');
         if (!restOfUrl.startsWith("//")) {
            temp.append("//");
         }

         temp.append(restOfUrl);
         this.initialize(temp.toString());
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void initialize(String url) {
      if (url == null) {
         throw new IllegalArgumentException();
      }

      this.parseUrlString(url);
   }

   @Override
   public String toString() {
      return this._rimParameters != null ? this._urlWithoutRIMParams + this._rimParameters.toString() : this._urlWithoutRIMParams;
   }

   public String getScheme() {
      return this._scheme;
   }

   public void setScheme(String scheme) {
      String schemeless = this._urlWithoutRIMParams.substring(this._scheme.length());
      scheme = StringUtilities.toLowerCase(scheme, 1701707776);
      this._urlWithoutRIMParams = scheme + schemeless;
      this._scheme = scheme;
   }

   public String getHost() {
      return this._host;
   }

   public int getPort() {
      return this._port;
   }

   public String getPath() {
      return this._path;
   }

   public String getQuery() {
      return this._query;
   }

   public String getFragment() {
      return this._fragment;
   }

   public URLParameters getRIMParameters() {
      return this._rimParameters;
   }

   public String toStringWithoutRIMParams() {
      return this._urlWithoutRIMParams;
   }

   private void parseUrlString(String url) {
      int positionOfColon = url.indexOf(58);
      if (positionOfColon == -1) {
         throw new MalformedURLException();
      }

      this._scheme = StringUtilities.toLowerCase(url.substring(0, positionOfColon), 1701707776);
      if (!this.validateScheme()) {
         throw new MalformedURLException("Scheme contains invalid characters");
      }

      String lowercaseUrl = StringUtilities.toLowerCase(url, 1701707776);
      String[] params = RIM_PARAMETERS;

      for (int i = 0; i < params.length; i++) {
         int start = lowercaseUrl.indexOf(params[i]);
         if (start != -1) {
            int end = lowercaseUrl.indexOf(59, start + 1);
            String newUrl;
            if (end == -1) {
               end = lowercaseUrl.length();
               newUrl = url.substring(0, start);
               lowercaseUrl = StringUtilities.toLowerCase(newUrl, 1701707776);
            } else {
               newUrl = url.substring(0, start) + url.substring(end);
               lowercaseUrl = StringUtilities.toLowerCase(newUrl, 1701707776);
            }

            if (this._rimParameters == null) {
               this._rimParameters = new URLParameters();
            }

            int equalsIndex = url.indexOf(61, start + 1);
            if (equalsIndex != -1 && equalsIndex <= end) {
               this._rimParameters.setParameter(url.substring(start + 1, equalsIndex), url.substring(equalsIndex + 1, end));
            } else {
               this._rimParameters.setParameter(url.substring(start + 1, end), "");
            }

            url = newUrl;
         }
      }

      this._urlWithoutRIMParams = url;
      this.parseSchemeSpecificPart(url);
      if (this._port == 0) {
         if (this._scheme.equals("http")) {
            this._port = 80;
            return;
         }

         if (this._scheme.equals("https") || this._scheme.equals("tls")) {
            this._port = 443;
            return;
         }

         if (this._host != null && (this._scheme.equals("ssl") || this._scheme.equals("socket"))) {
            throw new IllegalArgumentException("no port specified");
         }
      }
   }

   private boolean validateScheme() {
      if (this._scheme != null && this._scheme.length() != 0) {
         char c = this._scheme.charAt(0);
         if (c >= 'a' && c <= 'z') {
            for (int i = 1; i < this._scheme.length(); i++) {
               c = this._scheme.charAt(i);
               if (!Character.isDigit(c) && (c < 'a' || c > 'z') && c != '+' && c != '-' && c != '.') {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void parseSchemeSpecificPart(String url) {
      int position = url.indexOf(58) + 1;
      boolean isLDAPUrl = this._scheme.equalsIgnoreCase("ldap");
      int urlLength = url.length();
      if (!url.regionMatches(false, position, "//", 0, 2)) {
         throw new MalformedURLException("Missing '//' to signify the use of common Internet syntax");
      }

      position += 2;
      if (position >= urlLength) {
         throw new MalformedURLException("Missing everything past initial '//'");
      }

      int positionOfColon = url.indexOf(58, position);
      int positionOfSlash = url.indexOf(47, position);
      int positionOfQuestionMark = url.indexOf(63, position);
      int positionOfHash = url.indexOf(35, position);
      int positionOfSemiColon = url.indexOf(59, position);
      int hostStringEndsAt = urlLength;
      if (positionOfColon != -1) {
         hostStringEndsAt = Math.min(hostStringEndsAt, positionOfColon);
      }

      if (positionOfSlash != -1) {
         hostStringEndsAt = Math.min(hostStringEndsAt, positionOfSlash);
      }

      if (positionOfSemiColon != -1) {
         hostStringEndsAt = Math.min(hostStringEndsAt, positionOfSemiColon);
      }

      if (positionOfQuestionMark != -1) {
         hostStringEndsAt = Math.min(hostStringEndsAt, positionOfQuestionMark);
      }

      if (positionOfHash != -1) {
         hostStringEndsAt = Math.min(hostStringEndsAt, positionOfHash);
      }

      if (position != hostStringEndsAt) {
         this._host = url.substring(position, hostStringEndsAt);
         position = hostStringEndsAt;
      } else if (position == positionOfSlash && !isLDAPUrl) {
         throw new IllegalArgumentException("illegal host string: starts with '/'");
      }

      if (position < urlLength && url.charAt(position) == ':') {
         position++;
         String portString = null;
         positionOfSlash = url.indexOf(47, position);
         positionOfQuestionMark = url.indexOf(63, position);
         positionOfSemiColon = url.indexOf(59, position);
         positionOfHash = url.indexOf(35, position);
         int portStringEndsAt = urlLength;
         if (positionOfSlash != -1) {
            portStringEndsAt = Math.min(portStringEndsAt, positionOfSlash);
         }

         if (positionOfQuestionMark != -1) {
            portStringEndsAt = Math.min(portStringEndsAt, positionOfQuestionMark);
         }

         if (positionOfSemiColon != -1) {
            portStringEndsAt = Math.min(portStringEndsAt, positionOfSemiColon);
         }

         if (positionOfHash != -1) {
            portStringEndsAt = Math.min(portStringEndsAt, positionOfHash);
         }

         try {
            portString = url.substring(position, portStringEndsAt);
            if (portString.trim().length() != 0) {
               this._port = Integer.parseInt(portString);
            }

            position = portStringEndsAt;
         } catch (NumberFormatException e) {
            throw new MalformedURLException(portString + " does not represent a valid port number");
         }
      }

      if (position < urlLength) {
         positionOfHash = url.indexOf(35, position);
         positionOfQuestionMark = url.indexOf(63, position);
         int pathStringEndsAt = urlLength;
         if (positionOfHash != -1) {
            pathStringEndsAt = Math.min(pathStringEndsAt, positionOfHash);
         }

         if (positionOfQuestionMark != -1) {
            pathStringEndsAt = Math.min(pathStringEndsAt, positionOfQuestionMark);
         }

         if (position != pathStringEndsAt) {
            this._path = url.substring(position, pathStringEndsAt);
            if (this._path.equals("/") && (this._host == null || this._host.length() == 0) && !isLDAPUrl) {
               throw new IllegalArgumentException("illegal host string: /");
            }

            position = pathStringEndsAt;
         }
      }

      if (position < urlLength && url.charAt(position) == '?') {
         position++;
         int queryStringEndsAt = urlLength;
         positionOfHash = url.indexOf(35, position);
         if (positionOfHash != -1) {
            queryStringEndsAt = Math.min(queryStringEndsAt, positionOfHash);
         }

         if (position != queryStringEndsAt) {
            this._query = url.substring(position, queryStringEndsAt);
            position = queryStringEndsAt;
         }
      }

      if (position < urlLength) {
         if (url.charAt(position) == '#') {
            this._fragment = url.substring(++position);
         } else {
            throw new MalformedURLException("URL was not parsed completely, left: " + url.substring(position));
         }
      }
   }

   public void setRIMParameters(URLParameters params) {
      this._rimParameters = params;
   }
}

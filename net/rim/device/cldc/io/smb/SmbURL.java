package net.rim.device.cldc.io.smb;

import net.rim.device.cldc.io.utility.MalformedURLException;

final class SmbURL {
   private String _filepath;
   private String _domain;
   private String _user;
   private String _password;
   private String _server;
   static final byte PARAM_DOMAIN = 1;
   static final byte PARAM_USERNAME = 2;
   static final byte PARAM_PASSWORD = 3;
   static final byte PARAM_SERVER = 4;
   static final byte PARAM_FILE_PATH = 5;
   static final byte PARAM_PARAM = 6;

   SmbURL(String url) {
      this.parseSmbURL(url);
   }

   SmbURL(SmbURL url) throws MalformedURLException {
      if (url == null) {
         throw new MalformedURLException();
      }

      this._filepath = url.getFilepath();
      this._domain = url.getDomain();
      this._user = url.getUser();
      this._password = url.getPassword();
      this._server = url.getServer();
   }

   private final void parseSmbURL(String url) throws MalformedURLException {
      url = url.replace('\\', '/');
      if (url.startsWith("//")) {
         url = url.substring(2);
      }

      int amp = url.indexOf(64);
      int sl = url.indexOf(47);
      if (sl == -1) {
         throw new MalformedURLException("Invalid SMB URL, / not found");
      }

      if (amp == -1) {
         this._server = "//" + url.substring(0, sl);
         this._filepath = url.substring(sl);
      } else {
         int start = url.indexOf(59, 0);
         if (start != -1) {
            this._domain = url.substring(0, start);
            int end = url.indexOf(58, start + 1);
            if (end != -1) {
               this._user = url.substring(start + 1, end);
               this._password = url.substring(end + 1, amp);
            }
         }

         if (this._domain == null || this._user == null || this._password == null) {
            throw new MalformedURLException("Credentials not specified");
         }

         this._server = "//" + url.substring(amp + 1, sl);
         this._filepath = url.substring(sl);
      }

      if (this._filepath == null) {
         throw new MalformedURLException("File PATH not specified");
      }
   }

   public final String getDomain() {
      return this._domain;
   }

   public final String getFilepath() {
      return this._filepath;
   }

   public final String getPassword() {
      return this._password;
   }

   public final String getUser() {
      return this._user;
   }

   public final String getServer() {
      return this._server;
   }

   @Override
   public final String toString() {
      StringBuffer buff = new StringBuffer(1024);
      if (this._domain != null && this._user != null && this._password != null) {
         buff.append(this._domain).append(';').append(this._user).append(':').append(this._password).append('&');
      }

      if (this._server != null) {
         buff.append(this._server).append('/');
      }

      if (this._filepath != null) {
         buff.append(this._filepath);
      }

      return buff.toString();
   }
}

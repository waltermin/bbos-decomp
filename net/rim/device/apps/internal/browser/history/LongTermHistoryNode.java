package net.rim.device.apps.internal.browser.history;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;

public class LongTermHistoryNode implements Persistable {
   private Object _domain;
   private Object _url;
   private Object _title;
   private long _timestamp;
   private String _configUID;
   private int _configType = BrowserConfigRecord.INVALID_VALUE;
   private String _transportCID;

   public LongTermHistoryNode(String url, String title, BrowserConfigRecord browserConfig) {
      this._timestamp = System.currentTimeMillis();
      URI uri = (URI)(new Object(url));
      this._domain = encode(uri.getAuthority());
      this._url = encode(url);
      this._title = encode(title);
      if (browserConfig != null) {
         this._configUID = browserConfig.getUid();
         this._configType = browserConfig.getPropertyAsInt(12);
         this._transportCID = browserConfig.getPropertyAsString(3);
      }
   }

   public String getTitle() {
      return decode(this._title);
   }

   public String getDomain() {
      return decode(this._domain);
   }

   public long getTimestamp() {
      return this._timestamp;
   }

   public String getUrl() {
      return decode(this._url);
   }

   public String getConfigUID() {
      return this._configUID;
   }

   public int getConfigType() {
      return this._configType;
   }

   public String getTransportCID() {
      return this._transportCID;
   }

   private static Object encode(String string) {
      return PersistentContent.encode(string);
   }

   private static String decode(Object encoding) {
      return PersistentContent.decodeString(encoding);
   }
}

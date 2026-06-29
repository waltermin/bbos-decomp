package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;

public class DownloadContentVerb extends Verb {
   private BrowserConfigRecord _browserConfigRecord;
   private boolean _provisioningSR;
   private long _type;
   private String _url;

   public DownloadContentVerb(BrowserConfigRecord browserConfigRecord, String url, long type, boolean provisioningSR) {
      super(1180240);
      this._browserConfigRecord = browserConfigRecord;
      this._type = type;
      this._provisioningSR = provisioningSR;
      this._url = url;
   }

   @Override
   public Object invoke(Object context) {
      BrowserConfigRecord configRecord = this._browserConfigRecord;
      if (configRecord == null) {
         String configUid = GeneralProperty.getOrDetermineDefaultBrowserConfigServiceUID();
         if (configUid != null && configUid.length() != 0) {
            configRecord = BrowserConfigRecord.getDecodedConfig(configUid, BrowserConfigRecord.INVALID_VALUE, null);
         }
      }

      String url = this._url;
      if (url == null && !this._provisioningSR) {
         if (configRecord == null) {
            return null;
         }

         if (this._type == -2145255983166432762L) {
            url = configRecord.getPropertyAsString(46);
         } else if (this._type == -4064892972611285119L) {
            url = configRecord.getPropertyAsString(47);
         } else if (this._type == -201905085362485851L) {
            url = configRecord.getPropertyAsString(48);
         }
      }

      if (url == null) {
         return null;
      }

      ModelResult modelResult = new ModelResult(url, 8193, null);
      FetchRequest fetchRequest = new FetchRequest(modelResult, configRecord);
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      browser.initiateFetchRequest(fetchRequest);
      return null;
   }

   @Override
   public String toString() {
      if (this._type == -2145255983166432762L) {
         return BrowserResources.getString(728);
      } else if (this._type == -4064892972611285119L) {
         return BrowserResources.getString(730);
      } else {
         return this._type == -201905085362485851L ? BrowserResources.getString(729) : null;
      }
   }

   public BrowserConfigRecord getBrowserConfigRecord() {
      return this._browserConfigRecord;
   }

   public long getType() {
      return this._type;
   }

   public boolean isProvisioningSR() {
      return this._provisioningSR;
   }
}

package net.rim.blackberry.api.browser;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.cldc.io.ippp.SocketTransportBase;
import net.rim.device.internal.firewall.Firewall;

public class BrowserSession {
   protected String _connectionUid;

   protected BrowserSession() {
   }

   public void showBrowser() {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      browser.activateBrowser(this._connectionUid);
   }

   public void displayPage(String url, String referrer, HttpHeaders requestHeaders, PostData postData) {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      if (requestHeaders == null) {
         requestHeaders = new HttpHeaders();
      }

      RenderingUtilities.setReferrer(requestHeaders, referrer);
      if (postData != null) {
         String contentType = postData.getContentType();
         if (contentType != null) {
            requestHeaders.setProperty("Content-Type", contentType);
         }
      }

      ModelResult mr = new ModelResult(url, 8195, requestHeaders);
      if (postData != null) {
         mr.setPostData(postData.getBytes());
      }

      BrowserConfigRecord bcr = BrowserConfigRecord.getDecodedConfig(this._connectionUid, BrowserConfigRecord.INVALID_VALUE, null);
      this.firewallCheck(bcr, url);
      FetchRequest internalRequest = new FetchRequest(mr, bcr);
      browser.initiateFetchRequest(internalRequest);
   }

   private void firewallCheck(BrowserConfigRecord bcr, String url) {
      String cid = bcr.getPropertyAsString(3);
      int colon = url.indexOf(58);
      String protocol = "http";
      if (colon != -1) {
         protocol = url.substring(0, colon);
      }

      if (!ControlledAccess.verifyRRISignatures(true)) {
         boolean internal = this.isInternalConnection(cid, bcr);
         if (!Firewall.getInstance().allowConnection(protocol, url, internal)) {
            throw new SecurityException("Permission Denied: The Firewall has forbidden this operation as it indicates a possible security violation");
         }
      }
   }

   private boolean isInternalConnection(String cid, BrowserConfigRecord bcr) {
      if (StringUtilities.strEqualIgnoreCase(cid, "wap", 1701707776)) {
         return false;
      } else if (StringUtilities.strEqualIgnoreCase(cid, "wptcp", 1701707776)) {
         return false;
      } else if (StringUtilities.strEqualIgnoreCase(cid, "ippp", 1701707776)) {
         String uid = bcr.getPropertyAsString(4);
         return 0 == SocketTransportBase.getTypeByUid(uid);
      } else {
         return false;
      }
   }

   public void displayPage(String url) {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      BrowserConfigRecord bcr = BrowserConfigRecord.getDecodedConfig(this._connectionUid, BrowserConfigRecord.INVALID_VALUE, null);
      FetchRequest internalRequest = new FetchRequest(new ModelResult(url, 8195, null), bcr);
      browser.initiateFetchRequest(internalRequest);
      browser.activateBrowser(this._connectionUid);
   }
}

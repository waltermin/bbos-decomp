package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.ui.DialogEnterString;
import net.rim.device.cldc.io.utility.URIEncoder;

public final class FollowLinkVerb extends BrowserVerb {
   private String _url;
   private boolean _isHomePage;
   private String _queryTitle;
   private static int DESCRIPTION = 100;

   public FollowLinkVerb(String url, boolean isHomePage, String queryTitle) {
      super(341248, BrowserResources.getResourceBundle(), DESCRIPTION);
      this._url = url;
      this._isHomePage = isHomePage;
      this._queryTitle = queryTitle;
   }

   public FollowLinkVerb(String url) {
      this(url, false, null);
   }

   @Override
   public final Object invoke(Object context) {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      if (browser == null) {
         return null;
      }

      String url = this._url;
      if (this._queryTitle != null && url != null) {
         int stringSubstitutionIndex = url.indexOf("%s");
         if (stringSubstitutionIndex >= 0) {
            DialogEnterString queryDialog = (DialogEnterString)(new Object(
               MessageFormat.format(BrowserResources.getString(658), new Object[]{this._queryTitle}), null, CommonResources.getString(117)
            ));
            if (queryDialog.doModal() == -1) {
               return null;
            }

            StringBuffer urlBuffer = (StringBuffer)(new Object(url));
            urlBuffer.setLength(stringSubstitutionIndex);
            String query = queryDialog.getResult();
            boolean useLatin1 = true;

            for (int i = query.length() - 1; i >= 0; i--) {
               if (query.charAt(i) > 255) {
                  useLatin1 = false;
                  break;
               }
            }

            URIEncoder.encode(urlBuffer, queryDialog.getResult(), useLatin1 ? "iso-8859-1" : "utf-8", false);
            urlBuffer.append(url.substring(stringSubstitutionIndex + 2));
            url = urlBuffer.toString();
         }
      }

      boolean programmatic = false;
      if (context instanceof Object) {
         programmatic = context;
      }

      ModelResult modelResult = new ModelResult(url, 8449, null);
      modelResult.setHomePage(this._isHomePage);
      FetchRequest fetchRequest = new FetchRequest(modelResult, programmatic ? 32 : 0);
      browser.initiateFetchRequest(fetchRequest);
      return fetchRequest;
   }
}

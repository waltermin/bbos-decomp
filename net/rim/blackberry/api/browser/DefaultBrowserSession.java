package net.rim.blackberry.api.browser;

import net.rim.device.apps.internal.browser.options.GeneralProperty;

class DefaultBrowserSession extends BrowserSession {
   DefaultBrowserSession() {
      super._connectionUid = GeneralProperty.getDefaultBrowserConfigServiceUID();
      if (super._connectionUid == null || super._connectionUid.length() == 0) {
         super._connectionUid = GeneralProperty.determineDefaultBrowserConfigServiceUID();
      }
   }
}

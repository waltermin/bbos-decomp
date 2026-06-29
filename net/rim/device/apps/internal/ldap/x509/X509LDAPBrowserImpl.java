package net.rim.device.apps.internal.ldap.x509;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.ldap.LDAPBrowser;
import net.rim.device.apps.internal.ldap.LDAPBrowserContextFactory;

public final class X509LDAPBrowserImpl extends UiApplication implements Runnable {
   private LDAPBrowser _app = (LDAPBrowser)(new Object("X509"));
   private static final String CONTEXT_NAME = "X509";

   public static final void main(String[] args) {
      if (args != null && args.length == 1 && args[0].equals("init")) {
         LDAPBrowserContextFactory.register("X509", new X509LDAPBrowserContext());
      } else {
         X509LDAPBrowserImpl app = new X509LDAPBrowserImpl();
         app.invokeLater(app);
         app.enterEventDispatcher();
      }
   }

   private X509LDAPBrowserImpl() {
   }

   @Override
   public final void run() {
      this._app.open(false);
      System.exit(0);
   }
}

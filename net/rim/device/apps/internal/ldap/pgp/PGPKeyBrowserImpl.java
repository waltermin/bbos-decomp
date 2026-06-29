package net.rim.device.apps.internal.ldap.pgp;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.ldap.LDAPBrowser;
import net.rim.device.apps.internal.ldap.LDAPBrowserContextFactory;

public final class PGPKeyBrowserImpl extends UiApplication implements Runnable {
   private LDAPBrowser _app = (LDAPBrowser)(new Object("PGP"));
   private static final String CONTEXT_NAME = "PGP";

   public static final void main(String[] args) {
      if (args != null && args.length == 1 && args[0].equals("init")) {
         LDAPBrowserContextFactory.register("PGP", new PGPLDAPBrowserContext());
      } else {
         PGPKeyBrowserImpl app = new PGPKeyBrowserImpl();
         app.invokeLater(app);
         app.enterEventDispatcher();
      }
   }

   private PGPKeyBrowserImpl() {
   }

   @Override
   public final void run() {
      this._app.open(false);
      System.exit(0);
   }
}

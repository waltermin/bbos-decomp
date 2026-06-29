package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.apps.api.framework.verb.Verb;

public final class LaunchKeyStoreBrowserVerb extends Verb {
   private String _description;
   private String _browserContext;
   private Object _displayContext;

   public LaunchKeyStoreBrowserVerb(String browserContext, Object displayContext) {
      super(1200272);
      KeyStoreBrowserContext context = KeyStoreBrowser.getInstance().getContext(browserContext);
      this._description = context.getLaunchKeyStoreBrowserVerbDescription();
      this._browserContext = browserContext;
      this._displayContext = displayContext;
   }

   @Override
   public final String toString() {
      return this._description;
   }

   @Override
   public final Object invoke(Object context) {
      KeyStoreBrowser.getInstance().show(this._browserContext, this._displayContext);
      return null;
   }
}

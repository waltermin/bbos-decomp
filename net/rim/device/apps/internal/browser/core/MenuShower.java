package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.verbs.BrowserVerb;

final class MenuShower implements Runnable {
   private BrowserImpl _browser;
   private int _menuInstance;
   private Verb _hotkeyVerb;
   private boolean _performDefaultAction;
   private Object _ticket;

   MenuShower(BrowserImpl browser, int menuInstance, Verb hotkeyVerb, boolean performDefaultAction) {
      this._browser = browser;
      this._menuInstance = menuInstance;
      this._hotkeyVerb = hotkeyVerb;
      this._performDefaultAction = performDefaultAction;
      this._ticket = PersistentContent.getTicket();
   }

   MenuShower(BrowserImpl browser, int menuInstance, Verb hotkeyVerb) {
      this(browser, menuInstance, hotkeyVerb, false);
   }

   @Override
   public final void run() {
      if (this._ticket == null) {
         this._browser.releaseBrowserLock();
      } else if (this._hotkeyVerb == null) {
         this._browser.showMenuSynchronously(this._performDefaultAction, this._menuInstance);
      } else {
         this._hotkeyVerb.invoke(null);
         if (!(this._hotkeyVerb instanceof BrowserVerb) || !((BrowserVerb)this._hotkeyVerb).isModal()) {
            this._browser.releaseBrowserLock();
         }
      }
   }
}

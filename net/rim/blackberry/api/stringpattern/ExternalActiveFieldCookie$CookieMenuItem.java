package net.rim.blackberry.api.stringpattern;

import net.rim.blackberry.api.menuitem.ApplicationMenuItem;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.vm.Message;

final class ExternalActiveFieldCookie$CookieMenuItem extends MenuItem {
   private ApplicationMenuItem _ami;
   private ApplicationDescriptor _application;
   private String _matchedString;

   ExternalActiveFieldCookie$CookieMenuItem(ApplicationMenuItem menuItem, ApplicationDescriptor application) {
      super(menuItem.toString(), 0, 0);
      this._ami = menuItem;
      this._application = application;
   }

   final void initialize(String matchedString) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void run() {
      int pid = -1;
      ApplicationManager am = ApplicationManager.getApplicationManager();

      try {
         label54:
         try {
            pid = am.runApplication(this._application, false);
         } finally {
            break label54;
         }
      } finally {
         if (pid == -1) {
            return;
         }
      }

      Message invokeLaterMessage = (Message)(new Object(0, 2, new ExternalActiveFieldCookie$CookieMenuItem$1(this), null));
      ((ApplicationManagerInternal)am).postMessage(pid, invokeLaterMessage);
   }
}

package net.rim.device.apps.api.quickcontact;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.apps.api.ui.ClickAndHoldKey;

final class ClickAndHoldKeyImpl implements ClickAndHoldKey {
   @Override
   public final boolean validClickAndHold(char key) {
      QuickContactList.getInstance();
      return QuickContactList.isValidQuickContactKey(key);
   }

   @Override
   public final boolean doClickAndHold(int keycode) {
      if (!QuickContactList.isValidQuickContactKey(keycode)) {
         return false;
      }

      if (!ITPolicy.getBoolean(1, true)) {
         return true;
      }

      QuickContactItem qci = QuickContactList.getInstance().getQuickContactItem(keycode);
      if (qci != null) {
         qci.invoke();
         return true;
      }

      if (!ApplicationManager.getApplicationManager().isSystemLocked()) {
         QuickContactUtil.promptUserForNewQuickContactItem(keycode);
      }

      return true;
   }
}

package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class SaveRequestToMessageListVerb extends BrowserVerb {
   private static final int DESCRIPTION;

   public SaveRequestToMessageListVerb() {
      super(16987189);
   }

   @Override
   public final String toString() {
      return BrowserResources.getString(112);
   }

   @Override
   public final Object invoke(Object context) {
      int result = Dialog.ask(3, BrowserResources.getString(525), 4);
      if (result == 4) {
         BrowserDaemonRegistry.getInstance().saveRequestToMessageList();
      }

      return null;
   }

   @Override
   public final boolean isEnabled() {
      int state = BrowserDaemonRegistry.getInstance().getBrowserExecutionState();
      return state == 1;
   }
}

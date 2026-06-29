package net.rim.device.apps.internal.browser.common;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class BrowserPhoneConfirmation implements Confirmation {
   @Override
   public final boolean confirm(Verb verb, Object context) {
      return Dialog.ask(3, BrowserResources.getString(465)) == 4;
   }
}

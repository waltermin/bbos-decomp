package net.rim.device.apps.internal.browser.ui;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class FindNextVerb extends Verb {
   TFMFindManager _findManager;

   FindNextVerb(TFMFindManager findManager) {
      super(196640, CommonResources.getResourceBundle(), 9024);
      this._findManager = findManager;
   }

   @Override
   public final Object invoke(Object context) {
      this._findManager.findMatch(true, true);
      return null;
   }
}

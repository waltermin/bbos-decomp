package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.util.StringMatch;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.ui.component.SimpleInputDialog;

final class FindVerb extends Verb {
   TFMFindManager _findManager;
   private static final int MAX_CHARS = 256;

   FindVerb(TFMFindManager findManager) {
      super(196624, CommonResources.getResourceBundle(), 9023);
      this._findManager = findManager;
   }

   @Override
   public final Object invoke(Object context) {
      boolean foundSomething = false;
      if (context instanceof StringMatch) {
         this._findManager._stringMatch = (StringMatch)context;
         foundSomething = this._findManager.findMatch(false, false);
      } else {
         SimpleInputDialog dialog = new SimpleInputDialog(0, CommonResources.getString(9025), 0, 256, 0);
         dialog.show();
         String searchString = dialog.getText();
         if (searchString != null && searchString.length() > 0) {
            this._findManager._stringMatch = new StringMatch(searchString, false, false);
            foundSomething = this._findManager.findMatch(false, true);
         }
      }

      return foundSomething ? Boolean.TRUE : Boolean.FALSE;
   }
}

package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;

final class ViewIdleModeTextVerb extends RibbonBarVerb {
   private String _text;
   private static ViewIdleModeTextVerb _theVerb;

   private ViewIdleModeTextVerb(RibbonLauncherImpl ribbonApp) {
      super(ribbonApp, 619264);
   }

   @Override
   public final String toString() {
      return RibbonResources.getString(140);
   }

   static final Verb getInstance(RibbonLauncherImpl ribbonApp, String text) {
      if (_theVerb == null) {
         _theVerb = new ViewIdleModeTextVerb(ribbonApp);
      }

      _theVerb._text = text;
      return _theVerb;
   }

   @Override
   public final Object invoke(Object parameter) {
      Dialog.inform(this._text);
      return null;
   }
}

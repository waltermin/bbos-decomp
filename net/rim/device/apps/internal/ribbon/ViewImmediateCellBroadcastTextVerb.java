package net.rim.device.apps.internal.ribbon;

import net.rim.device.apps.api.framework.verb.Verb;

final class ViewImmediateCellBroadcastTextVerb extends RibbonBarVerb {
   private String _text;
   private static ViewImmediateCellBroadcastTextVerb _theVerb;

   private ViewImmediateCellBroadcastTextVerb(RibbonLauncherImpl ribbonApp) {
      super(ribbonApp, 619008);
   }

   @Override
   public final String toString() {
      return RibbonResources.getString(157);
   }

   static final Verb getInstance(RibbonLauncherImpl ribbonApp, String text) {
      if (_theVerb == null) {
         _theVerb = new ViewImmediateCellBroadcastTextVerb(ribbonApp);
      }

      _theVerb._text = text;
      return _theVerb;
   }

   @Override
   public final Object invoke(Object parameter) {
      new CellBroadcastDialog(this._text).show();
      return null;
   }
}

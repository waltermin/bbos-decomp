package net.rim.device.apps.internal.docview.gui;

import net.rim.device.apps.api.ui.ExitVerb;

final class DocViewExitVerb extends ExitVerb {
   private ExitScreen _screen;

   DocViewExitVerb(ExitScreen screen) {
      super(0, null);
      this._screen = screen;
   }

   @Override
   public final Object invoke(Object parameter) {
      return this._screen.canExitScreen() ? super.invoke(parameter) : null;
   }
}

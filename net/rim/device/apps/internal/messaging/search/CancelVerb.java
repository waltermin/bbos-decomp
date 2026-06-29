package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

public final class CancelVerb extends Verb {
   private boolean _popPrevious;
   private boolean _fromRibbon;
   private Screen _screen;
   private Verb _saveVerb;
   private String _confirmationMessage;

   @Override
   public final String toString() {
      return CommonResource.getString(9);
   }

   public CancelVerb(boolean popPrevious) {
      super(268500992);
      this._popPrevious = popPrevious;
   }

   public CancelVerb(boolean popPrevious, boolean fromRibbon, Screen screen, Verb saveVerb, String confirmationMessage) {
      super(268501008);
      this._popPrevious = popPrevious;
      this._screen = screen;
      this._saveVerb = saveVerb;
      this._confirmationMessage = confirmationMessage;
      this._fromRibbon = fromRibbon;
   }

   @Override
   public final Object invoke(Object context) {
      boolean performPop = true;
      if (this._screen != null && this._screen.isDirty()) {
         int retVal = Dialog.ask(1, this._confirmationMessage);
         switch (retVal) {
            case -2:
            case 0:
               break;
            case -1:
               performPop = false;
               break;
            case 1:
               if (this._saveVerb != null) {
                  this._saveVerb.invoke(context);
                  performPop = false;
               }
               break;
            case 2:
            default:
               performPop = true;
         }
      }

      if (this._popPrevious && performPop) {
         UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
         if (this._fromRibbon) {
            ApplicationManager.getApplicationManager().requestForegroundForConsole();
         }
      }

      return null;
   }
}

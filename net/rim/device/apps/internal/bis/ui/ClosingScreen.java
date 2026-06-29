package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.bis.BISClient;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;

public final class ClosingScreen extends BasicScreen {
   protected boolean _exitOnDisplay;

   public ClosingScreen() {
      this(false);
   }

   public ClosingScreen(boolean exitOnDisplay) {
      this._exitOnDisplay = exitOnDisplay;
   }

   @Override
   public final void refresh(Hashtable screenParams) {
   }

   @Override
   protected final void onDisplay() {
      super.onDisplay();
      if (this._exitOnDisplay) {
         BISClient bisClient = (BISClient)UiApplication.getUiApplication();
         bisClient.shutdown();
         System.exit(0);
      }
   }

   @Override
   protected final void showErrorMessage() {
      if (!this._exitOnDisplay) {
         Dialog.alert(super._errorMessage);
         BISClient bisClient = (BISClient)UiApplication.getUiApplication();
         bisClient.shutdown();
         System.exit(0);
      }
   }
}

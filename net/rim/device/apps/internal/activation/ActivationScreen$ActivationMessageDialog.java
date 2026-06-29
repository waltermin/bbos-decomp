package net.rim.device.apps.internal.activation;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.ribbon.RibbonLauncher;

final class ActivationScreen$ActivationMessageDialog implements Runnable {
   private String _message;
   private boolean _exitApp;
   private boolean _deregisterRibbonIcon;
   private boolean _globalStatus;
   private final ActivationScreen this$0;

   ActivationScreen$ActivationMessageDialog(ActivationScreen _1, String message, boolean exitApp, boolean deregisterRibbonIcon, boolean globalStatus) {
      this.this$0 = _1;
      this._message = message;
      this._exitApp = exitApp;
      this._globalStatus = globalStatus;
      this._deregisterRibbonIcon = deregisterRibbonIcon;
   }

   @Override
   public final void run() {
      Bitmap icon = Bitmap.getPredefinedBitmap(0);
      if (icon == null) {
         throw new RuntimeException("Activation:ICON");
      }

      if (this._globalStatus) {
         Dialog dialog = new ActivationScreen$ActivationMessageDialog$1(this, 0, this._message, 0, icon, 0);
         Dialog currentGlobalScreenScreen = this.this$0._app.getCurrentGlobalScreenDialog();
         if (currentGlobalScreenScreen != null) {
            UiApplication.getUiApplication().popScreen(currentGlobalScreenScreen);
         }

         dialog.setDialogClosedListener(this.this$0._app);
         this.this$0._app.setCurrentGlobalScreenDialog(dialog);
         UiApplication.getUiApplication().pushGlobalScreen(dialog, -2147483645, 2);
      } else {
         Dialog dialog = new Dialog(0, this._message, 0, icon, 0);
         dialog.doModal();
      }

      Bitmap var4 = null;
      Dialog var6 = null;
      if (this._deregisterRibbonIcon) {
         RibbonLauncher.getInstance().unregisterAction("net.rim.ActivationHomeScreenApp");
      } else {
         ((ActivationServiceImpl)this.this$0._activationService).iconRefresh();
      }

      if (this._exitApp && !this._globalStatus) {
         this.this$0._app.setCurrentState(0);
         this.this$0.exitApp();
      }
   }
}

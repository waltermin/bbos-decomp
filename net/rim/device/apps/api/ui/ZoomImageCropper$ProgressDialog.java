package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.internal.i18n.CommonResource;

class ZoomImageCropper$ProgressDialog extends PopupScreen implements Runnable {
   public ZoomImageCropper$ProgressDialog() {
      super((Manager)(new Object()), 0);
      RichTextField label = (RichTextField)(new Object(CommonResource.getBundle().getString(10185), 36028797018963968L));
      ((DialogFieldManager)this.getDelegate()).setMessage(label);
   }

   public void show() {
      UiApplication.getUiApplication().pushScreen(this);
   }

   @Override
   public void run() {
      this.close();
   }
}

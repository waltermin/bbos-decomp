package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.vm.TraceBack;

public final class TraceBackDialog extends Dialog {
   private Object _backTrace;
   private UiApplication _app;

   private TraceBackDialog(UiApplication app, String message, Object backTrace) {
      super(message, CommonResource.getStringArray(10004), null, 0, Bitmap.getPredefinedBitmap(2), 0);
      this._app = app;
      this._backTrace = backTrace;
   }

   @Override
   protected final boolean openProductionBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1145197894:
            return super.openProductionBackdoor(backdoorCode);
         case 1145197895:
         default:
            StringBuffer message = new StringBuffer();
            TraceBackDialog$TraceBackScreen fs = new TraceBackDialog$TraceBackScreen(this);
            int i = 0;

            while (true) {
               String extra = TraceBack.getMessage(this._backTrace, i);
               if (extra == null) {
                  RichTextField text = new RichTextField(message.toString());
                  fs.add(text);
                  this._app.pushGlobalScreen(fs, 10, 2);
                  return true;
               }

               message.append(extra);
               message.append('\n');
               i++;
            }
      }
   }

   public static final void show(UiApplication app, String message, Object backTrace) {
      Dialog d = new TraceBackDialog(app, message, backTrace);
      app.pushGlobalScreen(d, 11, 2);
   }
}

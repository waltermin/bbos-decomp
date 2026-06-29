package net.rim.device.apps.api.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.ui.VerticalFieldManager3;

public class PopupStatus extends PopupScreen {
   private PopupStatus$PopScreenRunnable _popScreenRunnable = new PopupStatus$PopScreenRunnable(this);
   private long _shown;
   protected LabelField _label;
   protected BitmapField _icon;
   protected VerticalFieldManager _vfm = (VerticalFieldManager)this.getDelegate();
   private static final int MIN_DISMISS_TIME = 1000;

   public PopupStatus(String message, Bitmap bitmap) {
      super(new VerticalFieldManager3());
      this._label = new LabelField(message, 51539607552L);
      if (bitmap != null) {
         this._label.setImage(bitmap);
      }

      this._vfm.add(this._label);
   }

   public static void show(String message, int time) {
      PopupStatus status = new PopupStatus(message, Bitmap.getPredefinedBitmap(0));
      status.show(time);
   }

   public void show(int time) {
      UiApplication app = UiApplication.getUiApplication();
      this._popScreenRunnable.init();
      if (app.invokeLater(this._popScreenRunnable, time, false) == -1) {
         throw new RuntimeException();
      }

      this._shown = System.currentTimeMillis();
      app.pushModalScreen(this);
   }

   private void dismiss() {
      if (System.currentTimeMillis() - this._shown > 1000) {
         this._popScreenRunnable.run();
      }
   }

   @Override
   protected boolean keyChar(char c, int status, int time) {
      if (super.keyChar(c, status, time) || c != ' ' && c != 27) {
         return false;
      }

      this.dismiss();
      return true;
   }

   @Override
   public boolean trackwheelClick(int status, int time) {
      if (!super.trackwheelClick(status, time)) {
         this.dismiss();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean stylusTap(int x, int y, int status, int time) {
      if (!super.stylusTap(x, y, status, time)) {
         this.dismiss();
         return true;
      } else {
         return false;
      }
   }
}

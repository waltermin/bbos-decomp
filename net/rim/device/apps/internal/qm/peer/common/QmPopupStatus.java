package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class QmPopupStatus extends QmThemedPopupScreen {
   private QmPopupStatus$PopScreenRunnable _popScreenRunnable = new QmPopupStatus$PopScreenRunnable(this);
   private long _shown;
   protected LabelField _label;
   protected BitmapField _icon;
   protected VerticalFieldManager _vfm = (VerticalFieldManager)this.getDelegate();
   private static final int MIN_DISMISS_TIME = 1000;

   public QmPopupStatus(String message, Bitmap bitmap) {
      super(new QmPopupStatus$1());
      this._label = new LabelField(message, 51539607552L);
      if (bitmap != null) {
         this._label.setImage(bitmap);
      }

      this._vfm.add(this._label);
   }

   public static final void show(String message, int time) {
      QmPopupStatus status = new QmPopupStatus(message, Bitmap.getPredefinedBitmap(0));
      status.show(time);
   }

   public final void show(int time) {
      UiApplication app = UiApplication.getUiApplication();
      this._popScreenRunnable.init();
      if (app.invokeLater(this._popScreenRunnable, time, false) == -1) {
         throw new RuntimeException();
      }

      this._shown = System.currentTimeMillis();
      app.pushModalScreen(this);
   }

   private final void dismiss() {
      if (System.currentTimeMillis() - this._shown > 1000) {
         this._popScreenRunnable.run();
      }
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (super.keyChar(c, status, time) || c != ' ' && c != 27) {
         return false;
      }

      this.dismiss();
      return true;
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      if (!super.trackwheelClick(status, time)) {
         this.dismiss();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean stylusTap(int x, int y, int status, int time) {
      if (!super.stylusTap(x, y, status, time)) {
         this.dismiss();
         return true;
      } else {
         return false;
      }
   }
}

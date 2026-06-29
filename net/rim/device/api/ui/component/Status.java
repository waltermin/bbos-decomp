package net.rim.device.api.ui.component;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.ImageBitmap;
import net.rim.device.internal.ui.component.ImageField;

public final class Status extends PopupScreen {
   private Status$PopScreenRunnable _popScreenRunnable = new Status$PopScreenRunnable(this);
   private int _id = -1;
   private long _shown;
   private boolean _allowDismiss;
   private boolean _block;
   private int _priority;
   private RichTextField _label;
   private DialogFieldManager _dfm;
   static final int DEFAULT_TIME = 2000;
   public static final long GLOBAL_STATUS = 33554432L;
   private static final int MIN_DISMISS_TIME = 500;

   private Status(String message, Bitmap bitmap, long style, boolean allowDismiss, boolean block, int priority) {
      this(message, ImageBitmap.create(bitmap), style, allowDismiss, block, priority);
   }

   private Status(String message, Image image, long style, boolean allowDismiss, boolean block, int priority) {
      super(new DialogFieldManager(), style);
      this.setAcceptsInput(allowDismiss);
      this._dfm = (DialogFieldManager)this.getDelegate();
      this._label = new RichTextField(message, 36028797018963968L);
      this._dfm.setMessage(this._label);
      this._allowDismiss = allowDismiss;
      this._block = block;
      this._priority = priority;
      if (image != null) {
         ImageField ifield = new ImageField();
         ifield.setImage(image);
         ifield.setPreferredSize(Display.getWidth() >> 2, Display.getHeight() >> 2);
         this._dfm.setIcon(ifield);
      }
   }

   private final void show0(int time) {
      this._popScreenRunnable.init();
      this._id = Application.getApplication().invokeLater(this._popScreenRunnable, time, false);
      if (this._id == -1) {
         throw new RuntimeException("No timer available for status popup.");
      }

      this._shown = System.currentTimeMillis();
      UiEngine uiEngine = Ui.getUiEngine();
      if (this.isStyle(33554432)) {
         uiEngine.pushGlobalScreen(this, this._priority, 2);
      } else if (this._block) {
         uiEngine.pushModalScreen(this);
      } else {
         uiEngine.pushScreen(this);
      }
   }

   private final void dismiss() {
      if (this._allowDismiss && System.currentTimeMillis() - this._shown > 500) {
         if (this._id != -1) {
            Application.getApplication().cancelInvokeLater(this._id);
         }

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
   protected final boolean keyDown(int keycode, int time) {
      return Keypad.key(keycode) == 261 ? true : super.keyDown(keycode, time);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (!super.trackwheelClick(status, time)) {
         this.dismiss();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean stylusTap(int x, int y, int status, int time) {
      return super.stylusTap(x, y, status, time) ? true : this.trackwheelClick(status, time);
   }

   public static final void show(String message) {
      show(message, Bitmap.getPredefinedBitmap(0), 2000);
   }

   public static final void show(String message, int time) {
      show(message, Bitmap.getPredefinedBitmap(0), time);
   }

   public static final void show(String message, Bitmap bitmap, int time) {
      show(message, bitmap, time, 0, true, true, 50);
   }

   public static final void show(String message, Bitmap bitmap, int time, long style, boolean allowDismiss, boolean block, int priority) {
      Status status = new Status(message, bitmap, style, allowDismiss, block, priority);
      status.show0(time);
   }

   public static final void show(String message, Image image, int time, long style, boolean allowDismiss, boolean block, int priority) {
      Status status = new Status(message, image, style, allowDismiss, block, priority);
      status.show0(time);
   }
}

package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UserInputEventListener;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.theme.Tag;

public final class HintPopup extends Screen implements Runnable, UserInputEventListener {
   private HintPopup$PopScreenRunnable _popScreenRunnable = new HintPopup$PopScreenRunnable(this);
   private Screen _screen;
   private int _duration;
   private HintPollingThread$HintProvider _hintProvider;
   private static Tag TAG = Tag.create("inplace");
   private static final int DEFAULT_HINT_DURATION;

   final Screen getScreenBeneath() {
      return this._screen;
   }

   @Override
   public final void onUserInput(int device, int flags) {
      if (this._screen != null && this._screen.getUiEngine() != null) {
         this._screen.getUiEngine().removeUserInputEventListener(this);
         this.dismiss();
      }
   }

   @Override
   public final void run() {
      UiApplication app = UiApplication.getUiApplication();
      this._popScreenRunnable.init();
      if (app.invokeLater(this._popScreenRunnable, this._duration, false) == -1) {
         throw new Object();
      }

      this._screen = app.getActiveScreen();
      this._screen.getUiEngine().addUserInputEventListener(this);
      app.pushScreen(this);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int size = Ui.convertSize(FontRegistry.DEFAULT_SIZE, 3, 0);
      this.setFont(null);
      Font font = this.getFont();
      if (font.getHeight() > size) {
         this.setFont(font.derive(font.getStyle(), size));
      }
   }

   private HintPopup(HintPollingThread$HintProvider hintProvider, Object hint, int duration) {
      super((Manager)(new Object()));
      this.setTag(TAG);
      this.setAcceptsInput(false);
      HorizontalFieldManager hfm = (HorizontalFieldManager)this.getDelegate();
      if (hint instanceof Object) {
         hfm.add((Field)hint);
      } else {
         hfm.add((Field)(new Object(hint.toString(), 51539607616L)));
      }

      this._hintProvider = hintProvider;
      this._duration = duration;
   }

   private final void dismiss() {
      this._popScreenRunnable.run();
   }

   static final void show(HintPollingThread$HintProvider hintProvider) {
      show(hintProvider, 2000);
   }

   static final void show(HintPollingThread$HintProvider hintProvider, int duration) {
      Object hint = hintProvider.getHint();
      if (hint != null) {
         UiApplication.getUiApplication().invokeLater(new HintPopup(hintProvider, hint, duration));
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      width -= this.getMarginLeft() + this.getMarginRight();
      height -= this.getMarginTop() + this.getMarginBottom();
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
      XYRect fmExtent = this.getDelegate().getExtent();
      this.setExtent(fmExtent.width, fmExtent.height);
      XYRect rect = this.getExtent();
      this._hintProvider.getHintPosition(rect);
      this.setPosition(rect.x, rect.y);
   }

   public static final void transformToScreen(Field field, XYRect rect) {
      while (field != null) {
         XYRect extent = field.getExtent();
         rect.translate(extent.x, extent.y);
         if (field instanceof Object) {
            Manager manager = (Manager)field;
            int dx = manager.getHorizontalScroll();
            int dy = manager.getVerticalScroll();
            rect.translate(-dx, -dy);
         }

         field = field.getManager();
      }
   }
}

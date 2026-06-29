package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UserInputEventListener;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;

public final class HintPopup extends PopupScreen implements Runnable, UserInputEventListener {
   private final HintPopup$PopScreenRunnable _popScreenRunnable = new HintPopup$PopScreenRunnable(this);
   private Screen _screen;
   private int _duration;
   private int _invokeDismissId = -1;
   private int _invokePopupId = -1;
   private HintPollingThread$HintProvider _hintProvider;
   private static Tag TAG = Tag.create("inplace");
   private static final int DEFAULT_HINT_DURATION = 2750;

   private HintPopup(HintPollingThread$HintProvider hintProvider, Object hint, int duration, Manager manager, long style) {
      super(manager, style);
      this.createHintPopup(hintProvider, hint, duration);
   }

   private HintPopup(HintPollingThread$HintProvider hintProvider, Object hint, int duration) {
      super(new VerticalFieldManager(0), 0);
      this.createHintPopup(hintProvider, hint, duration);
   }

   private final void createHintPopup(HintPollingThread$HintProvider hintProvider, Object hint, int duration) {
      this.setTag(TAG);
      this.setAcceptsInput(false);
      if (hint instanceof Field) {
         this.add((Field)hint);
      } else {
         this.add(new LabelField(hint.toString(), 45035996273704960L));
      }

      this._hintProvider = hintProvider;
      this._duration = duration;
   }

   public final void dismissPopup() {
      if (this._invokeDismissId == -1) {
         this._invokeDismissId = Application.getApplication().invokeLater(new HintPopup$1(this), 2750, false);
      }
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

      Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
      ThemeAttributeSet$Writer themeAttributeSetWriter = themeWriter.createThemeAttributeSetWriter(null);
      themeAttributeSetWriter.setColor(0, 16448210);
      themeAttributeSetWriter.setColor(1, 0);
      this.setThemeAttributesSpecial(themeAttributeSetWriter.getThemeAttributeSet(), null);
   }

   private final void dismiss() {
      if (this._invokePopupId != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(this._invokePopupId);
         this._invokePopupId = -1;
      }

      if (this._invokeDismissId != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(this._invokeDismissId);
         this._invokeDismissId = -1;
      }

      this._popScreenRunnable.run();
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

   static final void show(HintPollingThread$HintProvider hintProvider) {
      show(hintProvider, 2750);
   }

   static final void show(HintPollingThread$HintProvider hintProvider, int duration) {
      Object hint = hintProvider.getHint();
      if (hint != null) {
         UiApplication.getUiApplication().invokeLater(new HintPopup(hintProvider, hint, duration));
      }
   }

   @Override
   public final void run() {
      UiApplication app = UiApplication.getUiApplication();
      this._popScreenRunnable.init();
      if ((this._invokePopupId = app.invokeLater(this._popScreenRunnable, this._duration, false)) != -1) {
         this._screen = app.getActiveScreen();
         this._screen.getUiEngine().addUserInputEventListener(this);
         app.pushScreen(this);
      }
   }

   @Override
   public final void onUserInput(int device, int flags) {
      if (this._screen != null && this._screen.getUiEngine() != null) {
         this._screen.getUiEngine().removeUserInputEventListener(this);
         this.dismiss();
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this.dismiss();
      return !(this._screen instanceof QmMainScreen) ? super.trackwheelClick(status, time) : ((QmMainScreen)this._screen).internalTrackwheelClick(status, time);
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      this.dismiss();
      return !(this._screen instanceof QmMainScreen)
         ? super.trackwheelRoll(amount, status, time)
         : ((QmMainScreen)this._screen).internalTrackwheelRoll(amount, status, time);
   }

   public static final void transformToScreen(Field field, XYRect rect) {
      while (field != null) {
         XYRect extent = field.getExtent();
         rect.translate(extent.x, extent.y);
         if (field instanceof Manager) {
            Manager manager = (Manager)field;
            int dx = manager.getHorizontalScroll();
            int dy = manager.getVerticalScroll();
            rect.translate(-dx, -dy);
         }

         field = field.getManager();
      }
   }

   static final int access$000(HintPopup x0) {
      return x0._invokePopupId;
   }

   static final int access$002(HintPopup x0, int x1) {
      return x0._invokePopupId = x1;
   }

   static final void access$100(HintPopup x0) {
      x0.dismiss();
   }
}

package net.rim.tid.im.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.TextInputDialog;
import net.rim.device.api.ui.component.TextInputObscuringScreen;
import net.rim.device.api.ui.theme.Tag;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLInputMethod;
import net.rim.tid.im.conv.SLVariants;
import net.rim.tid.itie.EventHandler;

public class MultilineLookup extends Lookup implements TextInputDialog {
   private LookupManager _manager;
   public static final int INPUT_METHOD_LOOKUP_PRIORITY = 550;
   private static final Tag TAG = Tag.create("input-method-popup");

   public MultilineLookup() {
      this(new LookupManager());
   }

   public MultilineLookup(LookupManager manager) {
      super(manager);
      this.setTrackballSensitivityYOffset(-35);
      this.setMargin(0, 0, 0, 0);
      this.setTag(TAG);
      this._manager = manager;
      manager.setDelegator(this);
   }

   @Override
   public void setLabels(String leftLabel, String rightLabel) {
      this._manager.setLabels(leftLabel, rightLabel);
   }

   @Override
   public void init(SLInputMethod im, Locale locale, int aType) {
      this._manager.init(im, locale, aType);
   }

   @Override
   public int actionPerformed(Object aSrc, int anAction, Object aParameter) {
      return this._manager.actionPerformed(aSrc, anAction, aParameter);
   }

   @Override
   public int getPreferredWidth() {
      return 100;
   }

   public int getPrefferedHeight() {
      return 70;
   }

   @Override
   public void setVariants(SLVariants aVariants) {
      this._manager.setVariants(aVariants);
      this.invalidate(0, 0, this.getWidth(), this.getHeight());
   }

   public void removeVariant(Object aVariant) {
      this._manager.removeVariant(aVariant);
      this.invalidate(0, 0, this.getWidth(), this.getHeight());
   }

   @Override
   public void composedTextChanged(XYRect composedBounds) {
      this._manager.composedTextChanged(composedBounds);
      this.invalidateLayout0();
      this.doLayout();
      this.invalidate(0, 0, this.getWidth(), this.getHeight());
   }

   @Override
   public void setBounds(int x, int y, int width, int height, int composedHeight) {
   }

   @Override
   protected void onObscured() {
      if (Ui.getUiEngine().getActiveScreen() instanceof TextInputObscuringScreen) {
         this.setVisible(false);
         this.setVisible(true);
      }
   }

   @Override
   public void setVisible(boolean isVisible) {
      UiEngine engine = Ui.getUiEngine();
      if (isVisible) {
         Screen screen = ((Field)InputContext.getInstance().getInputComponent()).getScreen();
         if (screen.isGlobal()) {
            engine.pushGlobalScreen(this, engine.getGlobalPriority(screen), 4);
         } else {
            engine.pushScreen(this);
         }
      } else {
         engine.popScreen(this);
         if (engine.getActiveScreen() != null) {
            engine.getActiveScreen().setGateInput(false);
         }
      }

      this.invalidate(0, 0, this.getWidth(), this.getHeight());
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      switch (event) {
         case 513:
         case 514:
         case 520:
            if (Keypad.key(keycode) == 0) {
               keycode = 32768;
            }

            return EventHandler.getInstance().processKeyEvent(event, keycode, key, keycode, time, true);
         default:
            return 0;
      }
   }

   @Override
   public boolean processNavigationEvent(int event, int dx, int dy, int status, int time) {
      return EventHandler.getInstance().processNavigationEvent(event, dx, dy, status, time);
   }

   @Override
   protected void sublayout(int width, int height) {
      super.sublayout(width, height);
      XYRect bounds = this._manager.getBounds();
      this.setPosition(bounds.x, bounds.y);
   }

   @Override
   public XYRect getBounds() {
      return this._manager.getBounds();
   }

   @Override
   public void setStyle(byte style) {
      this._manager.setStyle(style);
   }

   @Override
   public void reset() {
      this._manager.reset();
   }

   @Override
   public int getSelectedIndex() {
      return 0;
   }
}

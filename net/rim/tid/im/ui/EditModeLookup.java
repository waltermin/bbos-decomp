package net.rim.tid.im.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.TextInputDialog;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLInputMethod;
import net.rim.tid.im.conv.SLVariants;
import net.rim.tid.itie.EventHandler;

public class EditModeLookup extends Screen implements TextInputDialog, LookupIf {
   private LookupManager _manager;
   private static final int PADDING_LEFT;
   private static final int PADDING_RIGHT;
   private static final int PADDING_TOP;
   private static final int PADDING_BOTTOM;

   public void removeVariant(Object aVariant) {
      this._manager.removeVariant(aVariant);
      this.invalidate(0, 0, this.getWidth(), this.getHeight());
   }

   @Override
   public void setVariants(SLVariants aVariants) {
      this._manager.setVariants(aVariants);
      this.invalidate(0, 0, this.getWidth(), this.getHeight());
   }

   @Override
   public int actionPerformed(Object aSrc, int anAction, Object aParameter) {
      return this._manager.actionPerformed(aSrc, anAction, aParameter);
   }

   @Override
   public void composedTextChanged(XYRect composedBounds) {
      this._manager.composedTextChanged(composedBounds);
      this.invalidateLayout0();
      this.doLayout();
      this.invalidate(0, 0, this.getWidth(), this.getHeight());
   }

   @Override
   public void setStyle(byte style) {
      this._manager.setStyle(style);
   }

   @Override
   public void init(SLInputMethod im, Locale locale, int aType) {
      this._manager.init(im, locale, aType);
   }

   @Override
   public void setLabels(String leftLabel, String rightLabel) {
      this._manager.setLabels(leftLabel, rightLabel);
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
   protected void sublayout(int width, int height) {
      this._manager.sublayout(width, height);
      XYRect bounds = this._manager.getBounds();
      this.setPosition(bounds.x, bounds.y);
      this.setExtent(bounds.width + 2 + 2, bounds.height + 2 + 2);
   }

   public EditModeLookup(LookupManager manager) {
      super(manager);
      this.setTrackballSensitivityYOffset(-30);
      this._manager = manager;
      manager.setDelegator(this);
      manager.setStyle((byte)1);
      manager.setAlignWithComposedTextX(true);
      manager.setAllowRevertedVariants(true);
      manager.setDrawHorizontalSeparators(true);
      manager.setAvoidJumpinness(false);
   }

   public EditModeLookup() {
      this(new EditModeLookup$EditModeLookupManager());
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
   protected synchronized void paint(Graphics graphics) {
      int fgPrevious = graphics.getColor();
      int bgPrevious = graphics.getBackgroundColor();
      graphics.setColor(ThemeAttributeSet.getColor(this, 1));
      graphics.setBackgroundColor(ThemeAttributeSet.getColor(this, 0));
      XYRect rect = this._manager.getBounds();
      rect.x = 0;
      rect.y = 0;
      graphics.drawRoundRect(0, 0, rect.width + 2 + 2, rect.height + 2 + 2, 3, 3);
      graphics.setColor(fgPrevious);
      graphics.setBackgroundColor(bgPrevious);
      rect.width += 2;
      rect.height += 2;
      graphics.pushContext(rect, 2, 2);
      graphics.setBackgroundColor(16119260);
      super.paint(graphics);
      graphics.popContext();
   }
}

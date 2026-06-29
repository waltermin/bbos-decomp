package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.apps.internal.browser.ui.ButtonBorder;
import net.rim.device.internal.ui.Border;

class HTMLButtonField extends ButtonField implements FieldChangeListener {
   private int _width;
   private int _height;
   private int _foregroundColor;
   private HTMLInput _controller;
   private static final Tag BUTTON_TAG = Tag.create("browser-button");

   public void clickInternal() {
      this.trackwheelClick(0, 0);
   }

   protected void doClickAction(boolean warn) {
      this._controller.eventOccurred(5, warn);
   }

   @Override
   public void fieldChanged(Field _1, int _2) {
      throw null;
   }

   @Override
   public boolean trackwheelClick(int status, int time) {
      return (status & 1) == 0 ? super.trackwheelClick(status, time) : false;
   }

   @Override
   protected boolean keyRepeat(int keycode, int time) {
      return Keypad.map(keycode) == '\n';
   }

   HTMLButtonField(HTMLInput controller, String label, long style, int width, int height, int backgroundColor, int foregroundColor, Border parentBorder) {
      super(label, style | 65536);
      this._controller = controller;
      boolean color = Graphics.isColor();
      if (color) {
         this.setTag(BUTTON_TAG);
      }

      this.setChangeListener(this);
      this._width = width;
      this._height = height;
      if (color) {
         if (backgroundColor == -1) {
            backgroundColor = 14079694;
         }

         ButtonBorder border = new ButtonBorder(0, backgroundColor, parentBorder);
         this.setBorder(0, border);
         this.setBorder(7, border);
         border = new ButtonBorder(1, backgroundColor, parentBorder);
         this.setBorder(6, border);
         this.setBorder(8, border);
         border = new ButtonBorder(2, backgroundColor, parentBorder);
         this.setBorder(4, border);
         this._foregroundColor = foregroundColor;
      } else {
         this._foregroundColor = -1;
      }
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(direction);
      this._controller.eventOccurred(3);
   }

   @Override
   protected void onUnfocus() {
      super.onUnfocus();
      this._controller.eventOccurred(6);
   }

   @Override
   public int getPreferredWidth() {
      return this._width != -1 ? this._width : super.getPreferredWidth();
   }

   @Override
   public int getPreferredHeight() {
      return this._height != -1 ? this._height : super.getPreferredHeight();
   }

   @Override
   protected void applyTheme(Graphics graphics, boolean drawBackground) {
      super.applyTheme(graphics, drawBackground);
      if (this._foregroundColor != -1) {
         graphics.setColor(this._foregroundColor);
      }
   }

   @Override
   public void setThemeAttributesSpecial(ThemeAttributeSet themeAttributesSpecial, Graphics graphics) {
      super.setThemeAttributesSpecial(themeAttributesSpecial, graphics);
      if (graphics != null && this._foregroundColor != -1) {
         graphics.setColor(this._foregroundColor);
      }
   }
}

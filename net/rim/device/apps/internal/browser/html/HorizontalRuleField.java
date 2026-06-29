package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.SeparatorField;

final class HorizontalRuleField extends SeparatorField {
   private int _ruleWidth;
   private boolean _percentage;
   private boolean _filled;
   private int _color;
   private int _height;
   private int _stipple;
   private int _padding;
   public static final int STIPPLE_SOLID = -1;
   public static final int STIPPLE_DASHED = -252645136;
   public static final int STIPPLE_DOTTED = -858993460;
   public static final int STIPPLE_HIDDEN = 0;

   HorizontalRuleField(int ruleWidth, boolean percentage, long style, int size, boolean filled, int color, int stipple, int padding) {
      super(style);
      this._ruleWidth = ruleWidth;
      this._percentage = percentage;
      this._height = size;
      this._stipple = stipple;
      this._padding = padding;
      if (this._height < 1) {
         this._height = 1;
      } else if (this._height > 20) {
         this._height = 20;
      }

      this._filled = filled;
      if (Graphics.isColor()) {
         this._color = color;
      } else {
         this._color = 0;
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      int fieldWidth = this.getWidth();
      int actualRuleWidth = -1;
      if (this._percentage) {
         actualRuleWidth = fieldWidth * this._ruleWidth / 100;
      } else {
         actualRuleWidth = Math.min(fieldWidth, this._ruleWidth);
      }

      if (actualRuleWidth == -1) {
         actualRuleWidth = fieldWidth;
      }

      int xPosition = 0;
      if ((this.getStyle() & 12884901888L) == 12884901888L) {
         xPosition = (fieldWidth - actualRuleWidth) / 2;
      } else if ((this.getStyle() & 8589934592L) == 8589934592L) {
         xPosition = fieldWidth - actualRuleWidth;
      }

      int oldStipple = graphics.getStipple();
      graphics.setStipple(this._stipple);
      graphics.setColor(this._color);
      if (this._filled) {
         graphics.fillRect(xPosition, this._padding, actualRuleWidth, this._height);
      } else {
         graphics.drawRect(xPosition, this._padding, actualRuleWidth, this._height);
      }

      graphics.setStipple(oldStipple);
   }

   @Override
   public final int getPreferredHeight() {
      return this._height + 2 * this._padding;
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(width, this._height + 2 * this._padding);
   }
}

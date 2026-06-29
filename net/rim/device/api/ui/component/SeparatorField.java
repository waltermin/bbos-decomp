package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.theme.Tag;

public class SeparatorField extends Field {
   private int _fieldHeight;
   private int _linePosition;
   private static Tag TAG = Tag.create("separator");
   private static final int HEIGHT_THIN;
   private static final int HEIGHT_THICK;
   public static final long LINE_HORIZONTAL;
   public static final long LINE_VERTICAL;
   public static final long LINE_MASK;
   public static final long CHECK_FONT;

   public SeparatorField() {
      this(0);
   }

   public SeparatorField(long style) {
      super(style);
      this.setTag(TAG);
   }

   @Override
   public int getPreferredHeight() {
      return this.isThin() ? 1 : 3;
   }

   private final boolean isThin() {
      return this.isStyle(8388608) && this.getFont().getHeight() == 8;
   }

   private boolean isHorizontal() {
      long style = this.getStyle();
      if ((style & 196608) == 65536) {
         return true;
      } else {
         return (style & 196608) == 131072 ? false : !(this.getManager() instanceof HorizontalFieldManager);
      }
   }

   @Override
   protected void layout(int width, int height) {
      if (this.isThin()) {
         this._fieldHeight = 1;
         this._linePosition = 0;
      } else {
         this._fieldHeight = 3;
         this._linePosition = 1;
      }

      if (this.isHorizontal()) {
         this.setExtent(width, this._fieldHeight);
      } else {
         this.setExtent(this._fieldHeight, height);
      }
   }

   protected int getLinePosition() {
      return this._linePosition;
   }

   @Override
   public String toString() {
      return "--------------------";
   }

   @Override
   protected void paint(Graphics graphics) {
      if (this.isHorizontal()) {
         graphics.drawLine(0, this._linePosition, this.getContentWidth(), this._linePosition);
      } else {
         graphics.drawLine(this._linePosition, 0, this._linePosition, this.getContentHeight());
      }
   }
}

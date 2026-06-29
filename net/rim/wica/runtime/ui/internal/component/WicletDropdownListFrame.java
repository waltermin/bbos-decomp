package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.theme.Theme;

class WicletDropdownListFrame extends Manager {
   private Bitmap bitmapDown;
   private int _labelWidth;
   protected static final long USE_BORDER;
   private static final int BORDER_WIDTH;
   private static final int DOUBLE_BORDER_WIDTH;
   private static final int SUB_BORDER_PADDING;
   private static final int DOUBLE_SUB_BORDER_PADDING;
   private static final int H_FRAME_ARC;
   private static final int V_FRAME_ARC;
   private static final int MAX_EXTENT;

   protected WicletDropdownListFrame() {
      this(0);
   }

   protected WicletDropdownListFrame(long style) {
      super(1407374883553280L | style);
      if ((this.getStyle() & 1) != 0) {
         this.bitmapDown = Theme.getThemeBitmap(1);
      }
   }

   private int getWidthExtra() {
      return 4 + this.bitmapDown.getWidth();
   }

   private int getHeightExtra() {
      return 4;
   }

   @Override
   public int getPreferredHeight() {
      int height = this.getFont().getHeight();
      if ((this.getStyle() & 1) != 0) {
         height += this.getHeightExtra();
      }

      return height;
   }

   @Override
   public int getPreferredWidth() {
      int width = this.getField(0).getPreferredWidth();
      if ((this.getStyle() & 1) != 0) {
         width += this.getWidthExtra();
      }

      return width;
   }

   @Override
   protected void sublayout(int width, int height) {
      width = Math.min(width, this.getPreferredWidth());
      height = Math.min(height, this.getPreferredHeight());
      Field field = this.getField(0);
      int virtualHeight;
      int virtualWidth;
      if ((this.getStyle() & 1) != 0) {
         this.setPositionChild(field, 2, 2);
         this.layoutChild(field, 1073741823, height - this.getHeightExtra());
         virtualHeight = field.getHeight() + this.getHeightExtra();
         virtualWidth = field.getWidth() + this.getWidthExtra();
      } else {
         this.setPositionChild(field, 0, 0);
         this.layoutChild(field, 1073741823, height);
         virtualHeight = field.getHeight();
         virtualWidth = field.getWidth();
      }

      this.setExtent(width, height);
      this.setVirtualExtent(virtualWidth, virtualHeight);
      this._labelWidth = field.getFont().getBounds(((net.rim.device.api.ui.component.ChoiceField)field).getLabel());
   }

   @Override
   protected void subpaint(Graphics graphics) {
      if ((this.getStyle() & 1) == 0) {
         super.subpaint(graphics);
      } else {
         int verticalScroll = this.getVerticalScroll();
         int horizontalScroll = this.getHorizontalScroll();
         int width = this.getWidth();
         int height = this.getHeight();
         graphics.pushContext(horizontalScroll + 1 + 1, verticalScroll + 1 + 1, width - this.getWidthExtra(), height - this.getHeightExtra(), 0, 0);
         super.subpaint(graphics);
         graphics.popContext();
         graphics.drawRoundRect(horizontalScroll + this._labelWidth, verticalScroll, width - this._labelWidth, height, 8, 4);
         graphics.drawBitmap(
            width - this.bitmapDown.getWidth() - 1 - 1,
            height - this.bitmapDown.getHeight() >> 1,
            this.bitmapDown.getWidth(),
            this.bitmapDown.getHeight(),
            this.bitmapDown,
            0,
            0
         );
      }
   }
}

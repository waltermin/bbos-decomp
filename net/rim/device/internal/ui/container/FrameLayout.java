package net.rim.device.internal.ui.container;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.util.MathUtilities;

public class FrameLayout extends Manager implements FocusChangeListener {
   private static final int BORDER_WIDTH = 2;
   private static final int DOUBLE_BORDER_WIDTH = 4;
   private static final int SUB_BORDER_PADDING = 1;
   private static final int DOUBLE_SUB_BORDER_PADDING = 2;
   public static final long USE_ROUNDED_CORNERS = 1L;

   public FrameLayout() {
      this(0);
   }

   public FrameLayout(long style) {
      super(style);
   }

   @Override
   public int getFieldAtLocation(int x, int y) {
      x = MathUtilities.clamp(3, x, this.getWidth() - 3);
      y = MathUtilities.clamp(3, y, this.getHeight() - 3);
      return super.getFieldAtLocation(x, y);
   }

   @Override
   public int getFieldClosestToLocation(int x, int y, int status) {
      x = MathUtilities.clamp(3, x, this.getWidth() - 3);
      y = MathUtilities.clamp(3, y, this.getHeight() - 3);
      return super.getFieldClosestToLocation(x, y, status);
   }

   @Override
   public void add(Field field) {
      this.deleteAll();
      super.add(field);
      field.setFocusListener(null);
      field.setFocusListener(this);
   }

   @Override
   public void insert(Field field, int index) {
      this.deleteAll();
      super.add(field);
      field.setFocusListener(null);
      field.setFocusListener(this);
   }

   @Override
   public int getPreferredHeight() {
      return this.getField(0).getPreferredHeight() + 4 + 2;
   }

   @Override
   public int getPreferredWidth() {
      return this.getField(0).getPreferredWidth() + 4 + 2;
   }

   @Override
   protected void subpaint(Graphics graphics) {
      int virtualWidth = this.getVirtualWidth();
      int virtualHeight = this.getVirtualHeight();
      int verticalScroll = this.getVerticalScroll();
      int horizontalScroll = this.getHorizontalScroll();
      graphics.pushContext(-horizontalScroll, -verticalScroll, virtualWidth, virtualHeight, -horizontalScroll, -verticalScroll);
      if (this.isStyle(1)) {
         graphics.drawRoundRect(1, 1, virtualWidth - 2, virtualHeight - 2, 4, 4);
      } else {
         graphics.drawRect(1, 1, virtualWidth - 2, virtualHeight - 2);
      }

      if (this.getFieldWithFocus() != null) {
         if (this.isStyle(1)) {
            graphics.drawRoundRect(0, 0, virtualWidth, virtualHeight, 4, 4);
         } else {
            graphics.drawRect(0, 0, virtualWidth, virtualHeight);
         }
      }

      graphics.popContext();
      super.subpaint(graphics);
   }

   @Override
   protected void sublayout(int width, int height) {
      Field field = this.getField(0);
      this.setPositionChild(field, 3, 3);
      this.layoutChild(field, Math.max(width - 4 - 2, 0), Math.max(height - 4 - 2, 0));
      int virtualHeight = field.getHeight() + 4 + 2;
      int virtualWidth = field.getWidth() + 4 + 2;
      this.setExtent(Math.min(width, virtualWidth), virtualHeight);
      this.setVirtualExtent(virtualWidth, virtualHeight);
   }

   @Override
   public void focusChanged(Field field, int eventType) {
      if (field == this.getField(0)) {
         this.invalidate();
      }
   }
}

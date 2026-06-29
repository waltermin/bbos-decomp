package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class VerticalExtentIndicatorFieldManager extends SecureEmailMessageBlockManager {
   private int _extentIndicatorColourRGB;
   private static final int EXTENT_INDICATOR_WIDTH;
   private static final int EXTENT_INDICATOR_WIDTH_PLUS_SPACE;

   public VerticalExtentIndicatorFieldManager(int extentIndicatorColourRGB, long style) {
      super(style);
      this._extentIndicatorColourRGB = extentIndicatorColourRGB;
   }

   @Override
   public void add(Field field, int indentAmount) {
      super.add(field, indentAmount + 5);
   }

   @Override
   public void insert(Field field, int index, int indentAmount) {
      super.insert(field, index, indentAmount + 5);
   }

   @Override
   public void subpaint(Graphics graphics) {
      super.subpaint(graphics);
      graphics.setColor(this._extentIndicatorColourRGB);
      graphics.fillRect(0, 0, 3, this.getHeight());
   }

   public void setExtentIndicatorColourRGB(int extentIndicatorColourRGB) {
      this._extentIndicatorColourRGB = extentIndicatorColourRGB;
      this.invalidate(0, 0, 3, this.getHeight());
   }
}

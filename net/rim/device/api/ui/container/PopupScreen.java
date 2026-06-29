package net.rim.device.api.ui.container;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;

public class PopupScreen extends Screen {
   private static Tag TAG = Tag.create("popup");
   private static final int SCALE_FACTOR;

   public PopupScreen(Manager delegate) {
      this(delegate, 0);
   }

   public PopupScreen(Manager delegate, long style) {
      super(delegate, style);
      this.setTag(TAG);
      int marginHorizontal = Display.getWidth() * 5 / 100;
      int marginVertical = Display.getHeight() * 5 / 100;
      this.setMargin(marginVertical, marginHorizontal, marginVertical, marginHorizontal);
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
   }

   @Override
   protected void sublayout(int width, int height) {
      width -= this.getMarginLeft() + this.getMarginRight();
      height -= this.getMarginTop() + this.getMarginBottom();
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
      XYRect fmExtent = this.getDelegate().getExtent();
      int newX = width - fmExtent.width >> 1;
      int newY = height - fmExtent.height >> 1;
      this.setPosition(newX + this.getMarginLeft(), newY + this.getMarginTop());
      this.setExtent(fmExtent.width, fmExtent.height);
   }

   @Override
   protected void paint(Graphics graphics) {
      super.paint(graphics);
   }
}

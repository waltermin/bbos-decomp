package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;

public class Layer {
   int x;
   int y;
   int width;
   int height;
   boolean visible = true;

   Layer(int width, int height) {
      this.setWidthImpl(width);
      this.setHeightImpl(height);
   }

   public void setPosition(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void move(int dx, int dy) {
      this.x += dx;
      this.y += dy;
   }

   public final int getX() {
      return this.x;
   }

   public final int getY() {
      return this.y;
   }

   public final int getWidth() {
      return this.width;
   }

   public final int getHeight() {
      return this.height;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public final boolean isVisible() {
      return this.visible;
   }

   public void paint(Graphics _1) {
      throw null;
   }

   void setWidthImpl(int width) {
      if (width < 0) {
         throw new IllegalArgumentException();
      }

      this.width = width;
   }

   void setHeightImpl(int height) {
      if (height < 0) {
         throw new IllegalArgumentException();
      }

      this.height = height;
   }
}

package net.rim.device.internal.ui;

import net.rim.device.api.ui.Graphics;

final class IconCollection$ProxiedImage implements Image {
   private IconCollection _collection;
   private int _index;

   private IconCollection$ProxiedImage(IconCollection collection, int index) {
      this._collection = collection;
      this._index = index;
   }

   @Override
   public final int getHeight(int width, int height) {
      return this._collection.getHeight(width, height);
   }

   @Override
   public final int getWidth(int width, int height) {
      return this._collection.getWidth(width, height);
   }

   @Override
   public final void paint(Graphics graphics, int x, int y, int width, int height) {
      this._collection.paint(graphics, x, y, width, height, this._index);
   }

   IconCollection$ProxiedImage(IconCollection x0, int x1, IconCollection$1 x2) {
      this(x0, x1);
   }
}

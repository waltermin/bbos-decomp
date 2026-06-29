package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

final class SolidScreen extends MainScreen {
   int[] rgb = new int[]{
      16711680, 65280, 255, 16777215, 51, -805041250, 100692579, 11408, -1960902400, 8, 0, 1887132109, -1088858788, -1707288265, -766696410, 957803812
   };
   int cnt = 0;
   Bitmap surface = new Bitmap(Graphics.getScreenWidth(), Graphics.getScreenHeight());
   BitmapField surfaceField = new BitmapField(this.surface);
   Graphics graphics;

   @Override
   protected final boolean navigationUnclick(int status, int time) {
      return true;
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return true;
   }

   SolidScreen() {
      this.add(this.surfaceField);
      this.graphics = new Graphics(this.surface);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.deleteAll();
   }

   final void changeColor() {
      this.graphics.setColor(this.rgb[this.cnt++ % this.rgb.length]);
      this.graphics.fillRect(0, 0, Graphics.getScreenWidth(), Graphics.getScreenHeight());
   }
}

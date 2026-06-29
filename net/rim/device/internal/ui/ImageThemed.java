package net.rim.device.internal.ui;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;

public class ImageThemed implements Image {
   private String _moduleName;
   private String _name;
   private int _themeGeneration;
   private Image _image;

   public ImageThemed(String name, String moduleName) {
      this._moduleName = moduleName;
      this._name = name;
      this.update();
   }

   @Override
   public int getHeight(int width, int height) {
      this.update();
      return this._image.getHeight(width, height);
   }

   @Override
   public int getWidth(int width, int height) {
      this.update();
      return this._image.getWidth(width, height);
   }

   @Override
   public void paint(Graphics graphics, int x, int y, int width, int height) {
      this.update();
      this._image.paint(graphics, x, y, width, height);
   }

   private void update() {
      int themeGeneration = ThemeManager.getGeneration();
      if (this._themeGeneration != themeGeneration) {
         this._themeGeneration = themeGeneration;
         Theme theme = ThemeManager.getActiveTheme();
         EncodedImage encoded = theme.getImage(this._name, this._moduleName, false);
         this._image = ImageEncoded.create(encoded);
      }
   }
}

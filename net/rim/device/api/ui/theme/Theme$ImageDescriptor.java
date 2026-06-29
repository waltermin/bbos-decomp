package net.rim.device.api.ui.theme;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;

class Theme$ImageDescriptor {
   private String _filename;
   private String _name;
   private boolean _isDefault;
   private ResourceFetcher _resourceFetcher;
   private EncodedImage _image;

   Theme$ImageDescriptor(String name, ResourceFetcher resourceFetcher, boolean isDefault) {
      this._filename = name;
      this._resourceFetcher = resourceFetcher;
      this._isDefault = isDefault;
      if (name.endsWith(ThemeConstants.EXT_GIF) || name.endsWith(ThemeConstants.EXT_PNG)) {
         name = name.substring(0, name.length() - 4);
      }

      this._name = name;
   }

   EncodedImage getImage() {
      if (this._image == null) {
         byte[] bytes = this._resourceFetcher.fetchResource(this._filename);
         this._image = EncodedImage.createEncodedImage(bytes, 0, bytes.length);
         int decodeMode = (Graphics.getNumColors() > 2 ? 1 : 0) | 4;
         if (this._name.equals(ThemeConstants.NAVIGATION_UP_ARROW) || this._name.equals(ThemeConstants.NAVIGATION_DOWN_ARROW)) {
            decodeMode |= 2;
         }

         this._image.setDecodeMode(decodeMode);
      }

      return this._image;
   }

   String getName() {
      return this._name;
   }

   boolean isDefault() {
      return this._isDefault;
   }
}

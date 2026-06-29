package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;

class PMEGraphicsViewport$PMEConvertedImages {
   Object[] _image;
   Object[] _imageConverted;
   int _size;
   private final PMEGraphicsViewport this$0;

   PMEGraphicsViewport$PMEConvertedImages(PMEGraphicsViewport _1, int size) {
      this.this$0 = _1;
      this._size = size;
      this._image = new Object[this._size];
      this._imageConverted = new Object[this._size];
   }

   boolean grow(int size) {
      if (size > this._size) {
         this.this$0._platform.arrayResize(this._image, size);
         this.this$0._platform.arrayResize(this._imageConverted, size);
         return true;
      } else {
         return false;
      }
   }

   boolean setImage(Object image, int iImage) {
      if (iImage < 0) {
         return false;
      } else if (iImage >= this._size && this.grow(iImage + 1)) {
         this._image[iImage] = image;
         this._imageConverted[iImage] = this.convertImage(image);
         return true;
      } else if (this._image[iImage] != image) {
         this._image[iImage] = image;
         this._imageConverted[iImage] = this.convertImage(image);
         return true;
      } else {
         return false;
      }
   }

   Object convertImage(Object image) {
      return image != null && !(image instanceof ForeignObject) ? this.this$0._meGraphic.getBitmapObject(image) : null;
   }
}

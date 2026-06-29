package net.rim.device.apps.internal.docview.gui;

import net.rim.device.apps.internal.docview.core.ArznImageData;

public final class DocViewImageData extends DocViewObjectData implements ArznImageData {
   private int _originalWidth;
   private int _originalHeight;
   private String _imageName;

   public final int getOriginalHeight() {
      return this._originalHeight;
   }

   public final int getOriginalWidth() {
      return this._originalWidth;
   }

   public final byte[] getImageContents() {
      return this.getContents();
   }

   @Override
   public final void setDimensions(int width, int height) {
      this._originalWidth = width;
      this._originalHeight = height;
   }

   @Override
   public final void addContents(byte[] imageContents) {
      this.addData(imageContents);
   }

   @Override
   public final void setName(String name) {
      this._imageName = name;
   }

   DocViewImageData() {
   }

   @Override
   public final String toString() {
      return this._imageName;
   }
}

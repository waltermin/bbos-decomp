package net.rim.tools.compiler;

import java.io.InputStream;

public class ImageFile extends ResourceFile {
   private boolean _isIcon;
   private int _iconOrdinal = -1;

   public ImageFile() {
   }

   public ImageFile(String name, InputStream in, int size) {
      super(name, in, size);
   }

   public void markIcon() {
      this._isIcon = true;
   }

   public boolean isIcon() {
      return this._isIcon;
   }

   public void setOrdinal(int ordinal) {
      this._iconOrdinal = ordinal;
   }

   public int getOrdinal() {
      return this._iconOrdinal;
   }

   public void convertImage() {
   }
}

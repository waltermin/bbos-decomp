package net.rim.tid.text;

public final class AttributedString$PictureInfo {
   public AttributedString$Picture _picture;
   public int _x;
   public int _y;
   public int _width;
   public int _height;
   public int _advance;
   public int _lineBreakType;

   public AttributedString$PictureInfo(AttributedString$Picture aPicture) {
      if (aPicture == null) {
         throw new IllegalArgumentException();
      }

      this._picture = aPicture;
   }
}

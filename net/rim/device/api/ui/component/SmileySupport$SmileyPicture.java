package net.rim.device.api.ui.component;

import net.rim.device.api.ui.Graphics;
import net.rim.tid.text.AttributedString$Picture;
import net.rim.tid.text.AttributedString$PictureInfo;

class SmileySupport$SmileyPicture implements AttributedString$Picture {
   private int _id;
   private AttributedString$PictureInfo _info;
   private final SmileySupport this$0;

   public int getId() {
      return this._id;
   }

   @Override
   public void draw(Graphics graphics, int x, int y) {
      this.this$0._smileyFacility.drawEmoticon(graphics, this._id, x, y);
   }

   @Override
   public AttributedString$PictureInfo getInfo() {
      return this._info;
   }

   SmileySupport$SmileyPicture(SmileySupport _1, int id) {
      this.this$0 = _1;
      this._id = id;
      this._info = new AttributedString$PictureInfo(this);
      this._info._width = SmileySupport._size;
      this._info._height = SmileySupport._size;
      this._info._advance = SmileySupport._size + 1;
      int leading = SmileySupport._size >= 20 ? 3 : (SmileySupport._size >= 10 ? 2 : 1);
      this._info._y = -SmileySupport._size + leading;
   }
}

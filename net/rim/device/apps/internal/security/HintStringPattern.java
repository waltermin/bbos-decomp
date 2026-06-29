package net.rim.device.apps.internal.security;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.EmoticonStringPattern;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.apps.api.ui.ReturnKeyIcons;
import net.rim.device.internal.ui.Image;

final class HintStringPattern extends EmoticonStringPattern {
   private StringSet _stringset = new StringSet();
   private int _imageWidth;
   private int _imageHeight;
   private Image _sendImage;
   StringSetMatch _match = new StringSetMatch();
   private static HintStringPattern _instance;
   private static final String SEND = "￼";

   static final HintStringPattern getHintStringPattern() {
      if (_instance == null) {
         _instance = new HintStringPattern();
      }

      return _instance;
   }

   HintStringPattern() {
      this._stringset.add("￼", 0);
      this._sendImage = ReturnKeyIcons.ICONS.getImage(0);
   }

   @Override
   public final int emoticonSize() {
      return Math.max(this._imageWidth, this._imageHeight);
   }

   @Override
   public final void drawEmoticon(Graphics graphics, int id, int x, int y) {
      if (this._sendImage != null) {
         this._sendImage.paint(graphics, x, y + this._imageWidth - this._imageHeight, this._imageWidth, this._imageHeight);
      }
   }

   @Override
   public final String emoticonReplacementText(int id) {
      return "￼";
   }

   @Override
   public final String emoticonDescription(int id) {
      return null;
   }

   @Override
   public final int[][][] emoticonScreenLayouts() {
      return (int[][][])((int[][])null);
   }

   @Override
   public final boolean findMatch(AbstractString str, int start, int end, StringPattern$Match match) {
      if (str == null) {
         return false;
      } else if (this._stringset.findMatch(str, start, end, this._match)) {
         match.beginIndex = this._match._start;
         match.endIndex = this._match._end;
         match.id = this._match._key;
         return true;
      } else {
         return false;
      }
   }

   final void setEmoticonSize(int size) {
      if (this._sendImage != null) {
         this.calculateSizes(size);
      } else {
         this._imageWidth = size;
         this._imageHeight = size;
      }
   }

   private final void calculateSizes(int fontHeight) {
      if (this._sendImage != null) {
         this._imageHeight = this._sendImage.getHeight(fontHeight, fontHeight);
         this._imageWidth = this._sendImage.getWidth(fontHeight, fontHeight);
      }
   }
}

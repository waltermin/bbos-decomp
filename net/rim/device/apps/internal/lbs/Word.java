package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.Font;

final class Word {
   String _text;
   int _length;
   int _space;

   public Word() {
   }

   public Word(Font font, String text, int space) {
      this._text = text;
      this._length = font.getAdvance(text);
      this._space = space;
   }
}

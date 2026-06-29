package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui;

import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.internal.ui.ArticInterface$LineInfo;

public class ActiveRichTextFieldWrapper extends ActiveRichTextField {
   public ActiveRichTextFieldWrapper(String text, long style) {
      super(text, style);
   }

   @Override
   public int getDisplayLineCount() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public int getDisplayLineHeight(int pos) {
      ArticInterface$LineInfo info = this.getLineInfoForDocPos(pos, true);
      return info._line._boundsBottom - info._line._boundsTop;
   }

   @Override
   public void layout(int w, int h) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }
}

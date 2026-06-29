package net.rim.device.api.ui.text;

import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.ui.ArticInterface$LineInfo;
import net.rim.device.internal.ui.Formatter;
import net.rim.tid.text.AttributedString$Iterator;

public class TextRect extends TextArea {
   private AttributedString$Iterator _iterator;

   public TextRect(Field field) {
      this(field, null, 6);
   }

   public TextRect(Field field, int style) {
      this(field, null, style);
   }

   public TextRect(Field field, Object text, int style) {
      super(field, style != 0 ? style | 8 : 14);
      this.setText(text);
      this._iterator = this.getTextIterator();
   }

   @Override
   public void layout(int width, int height) {
      super.layout(width, height);
   }

   @Override
   protected void paint(Graphics graphics) {
      XYRect clip = graphics.getClippingRect();
      DrawTextParam drawTextParam = Ui.getTmpDrawTextParam();
      this.setDrawTextParamFromStyle(drawTextParam);
      ArticInterface$LineInfo info = this.getLineInfoForYPos(clip.y);
      Formatter.paint(graphics, drawTextParam, info, this._iterator, this.getField(), this);
      Ui.returnTmpDrawTextParam(drawTextParam);
   }
}

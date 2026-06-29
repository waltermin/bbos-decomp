package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.LabelField;

final class DocViewTextDisplayField$SkippedStatusField extends LabelField {
   private int _skippedRegions;
   private int _nextBlockIndex;
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$SkippedStatusField(DocViewTextDisplayField _1, int skippedRegions, int startBlockIndex, Font font) {
      super(null, 1170935903116329028L);
      this.this$0 = _1;
      if (skippedRegions <= 0) {
         throw new Object();
      }

      this.setFont(font);
      this._skippedRegions = skippedRegions;
      this._nextBlockIndex = startBlockIndex;
      this.calculateDisplayText();
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      XYRect tmp = (XYRect)(new Object());
      this.getFocusRect(tmp);
      if (Graphics.isColor()) {
         graphics.invert(tmp.x, tmp.y, tmp.width, tmp.height);
      } else {
         graphics.invert(tmp.x + 1, tmp.y + 1, tmp.width - 2, tmp.height - 2);
      }
   }

   @Override
   protected final void paint(Graphics g) {
      int crtFgColor = g.getColor();
      boolean isColorDevice = Graphics.isColor();
      g.setColor(isColorDevice ? 255 : 0);
      g.fillRect(0, 0, this.getContentWidth(), this.getPreferredHeight());
      g.setColor(isColorDevice ? 16776960 : 16777215);
      super.paint(g);
      g.setColor(crtFgColor);
   }

   @Override
   public final boolean isSelectionCopyable() {
      return false;
   }

   final void calculateDisplayText() {
      int bVal = this.this$0.getMoreAvailableBytes(this._skippedRegions);
      if (bVal > 0) {
         StringBuffer displayText = (StringBuffer)(new Object(DocViewDisplayField._resources.getString(62)));
         displayText.append(": ");
         int val = Math.max(bVal * 10 / 1024, 1);
         displayText.append(String.valueOf(val / 10));
         val %= 10;
         if (val != 0) {
            displayText.append('.');
            displayText.append(String.valueOf(val));
         }

         displayText.append(' ');
         displayText.append(DocViewDisplayField._resources.getString(49));
         this.setText(displayText.toString());
      }
   }

   static final int access$3506(DocViewTextDisplayField$SkippedStatusField x0) {
      return --x0._skippedRegions;
   }

   static final int access$3304(DocViewTextDisplayField$SkippedStatusField x0) {
      return ++x0._nextBlockIndex;
   }
}

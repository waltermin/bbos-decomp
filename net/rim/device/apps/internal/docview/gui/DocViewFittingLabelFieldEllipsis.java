package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.internal.ui.RichText;

final class DocViewFittingLabelFieldEllipsis extends LabelField {
   private int _prefixSize;
   private boolean _isRTL;
   private static final int SIGNAL_ARROW_WIDTH = Graphics.isColor() ? 12 : 7;

   DocViewFittingLabelFieldEllipsis(String value) {
      super((Object)null, (long)64);
      if (value != null) {
         this.setText(value);
      }
   }

   @Override
   public final int getPreferredWidth() {
      Manager manager = this.getManager();
      if (manager != null) {
         int widthAvailable = DocViewGUIInternalConstants.SCREEN_WIDTH - SIGNAL_ARROW_WIDTH;
         int fieldCount = manager.getFieldCount();

         for (int i = 0; i < fieldCount; i++) {
            Field field = manager.getField(i);
            if (field != this) {
               widthAvailable -= field.getPreferredWidth();
            }
         }

         return widthAvailable;
      } else {
         return super.getPreferredWidth();
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (!this._isRTL) {
         super.paint(graphics);
      } else {
         int x = this.getPosition();
         int y = 0;
         int width = this.getContentWidth() - 1 - x;
         String text = this.getText();
         int length = text.length();
         if (length > 0) {
            int fieldStyle = this.getFieldStyle();
            if (this._prefixSize <= 0) {
               RichText.drawTextWithEllipses(graphics, text, x, y, width, 2, fieldStyle);
               return;
            }

            int allowedPrefixSize = Math.min(this._prefixSize, length);
            String prefix = text.substring(0, allowedPrefixSize);
            int prefixWidth = this.getFont().getAdvance(prefix);
            graphics.drawText(prefix, x, y, fieldStyle, width);
            if (prefixWidth < width && allowedPrefixSize < length) {
               String info = text.substring(allowedPrefixSize);
               RichText.drawTextWithEllipses(graphics, info, x + prefixWidth, y, width - prefixWidth, 2, fieldStyle);
               return;
            }
         }
      }
   }

   @Override
   public final void setText(Object text) {
      if (text instanceof CellTextDescription) {
         this._prefixSize = ((CellTextDescription)text)._prefixSize;
         super.setText(((CellTextDescription)text)._textDescription);
         this._isRTL = false;
         String controlText = this.getText();
         if (controlText != null && controlText.length() > this._prefixSize) {
            this._isRTL = RichText.getLineDirection(controlText.substring(this._prefixSize)) == 2;
            return;
         }
      } else {
         this._prefixSize = 0;
         super.setText(text);
         this._isRTL = RichText.getLineDirection(this.getText()) == 2;
      }
   }
}

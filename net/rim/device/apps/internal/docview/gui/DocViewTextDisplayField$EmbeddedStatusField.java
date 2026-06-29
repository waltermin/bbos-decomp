package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.LabelField;

final class DocViewTextDisplayField$EmbeddedStatusField extends LabelField {
   String _domID;
   int _type;
   Field _previewField;
   boolean _isDummy;

   DocViewTextDisplayField$EmbeddedStatusField(String title, int type, String domID, Font font, Field previewField, boolean isDummy) {
      super(title, 1170935916001230916L);
      if (title == null) {
         throw new Object("Null embedded object name not allowed.");
      }

      this._previewField = previewField;
      this._isDummy = isDummy;
      this._domID = domID;
      this._type = type;
      this.setPosition(0);
      this.setFont(font);
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      XYRect tmp = (XYRect)(new Object());
      this.getFocusRect(tmp);
      if (Graphics.isColor()) {
         graphics.invert(tmp.x + 1, tmp.y + 1, tmp.width - 1, tmp.height - 1);
      } else {
         graphics.invert(tmp.x + 1, tmp.y + 1, tmp.width - 2, tmp.height - 1);
      }
   }

   @Override
   protected final void paint(Graphics g) {
      int crtFgColor = g.getColor();
      boolean isColorDevice = Graphics.isColor();
      g.setColor(isColorDevice ? 255 : 0);
      super.paint(g);
      if (isColorDevice) {
         g.setColor(13882323);
      }

      XYRect extentRect = this.getExtent();
      g.drawLine(0, 0, extentRect.width - 1, 0);
      g.drawLine(0, 0, 0, extentRect.height - 1);
      g.drawLine(extentRect.width - 1, 0, extentRect.width - 1, extentRect.height - 1);
      if (!(this._previewField instanceof CustomListField)) {
         int width = this.getPreferredWidth();
         int x1 = extentRect.width - width >> 1;
         int x2 = x1 + width;
         g.drawLine(0, extentRect.height - 1, x1 + 1, extentRect.height - 1);
         g.drawLine(x2 - 2, extentRect.height - 1, extentRect.width - 1, extentRect.height - 1);
      }

      g.setColor(crtFgColor);
   }

   @Override
   public final boolean isSelectionCopyable() {
      return false;
   }
}

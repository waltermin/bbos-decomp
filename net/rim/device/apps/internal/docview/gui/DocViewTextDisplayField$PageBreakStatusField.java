package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

final class DocViewTextDisplayField$PageBreakStatusField extends LabelField {
   private final String _domID;
   private Field _pageDisplay;
   private boolean _isDummy;
   private boolean _inProcess;
   private int _originalDisplayWidth;
   private int _originalDisplayHeight;
   private String _imageDomID;
   private final String _renderedName;
   private final int _pageIndex;
   private boolean _invisible;

   DocViewTextDisplayField$PageBreakStatusField(String title, long style, Font initialFont, String domID, String renderedName, boolean invisible, int pageIndex) {
      super(title, style | 36028797018963968L | 1152921504606846976L);
      this._domID = domID;
      this._renderedName = renderedName;
      this._invisible = invisible;
      this._pageIndex = pageIndex;
      this.setFont(initialFont);
   }

   @Override
   protected final void paint(Graphics g) {
      if (!this._invisible) {
         int h = this.getPreferredHeight() >> 1;
         int contentWidth = this.getContentWidth();
         int x1 = contentWidth - this.getPreferredWidth() >> 1;
         int x2 = x1 + this.getPreferredWidth();
         int crtColor = g.getColor();
         g.drawRect(0, h - 1, x1, 2);
         g.drawRect(x2, h - 1, contentWidth - this.getPreferredWidth() - x1, 2);
         g.setColor(crtColor);
         super.paint(g);
      }
   }

   @Override
   public final int getPreferredWidth() {
      return this._invisible ? 0 : super.getPreferredWidth();
   }

   @Override
   public final int getPreferredHeight() {
      return this._invisible ? 0 : super.getPreferredHeight();
   }
}

package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

public final class LinkField extends LabelField {
   private int _textColour;
   private boolean _underline;
   private static final int LINK_COLOR;

   public LinkField(String text) {
      this(text, 23776, true, 0);
   }

   public LinkField(String text, int textColour, boolean underline, long style) {
      super(text, 18014398509481984L | style);
      this.setEditable(true);
      this._textColour = textColour;
      this._underline = underline;
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      Font font = this.getFont();
      if (this._underline) {
         this.setFont(font.derive(font.getStyle() | 4));
      } else {
         this.setFont(font.derive(font.getStyle()));
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (this.isEditable() && key == '\n') {
         this.fieldChangeNotify(0);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (this.isEditable()) {
         this.fieldChangeNotify(0);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean stylusTap(int x, int y, int status, int time) {
      return this.trackwheelClick(status, time);
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (!this.isFocus()) {
         graphics.setColor(this._textColour);
      }

      super.paint(graphics);
   }

   @Override
   protected final void onUnfocus() {
      super.onUnfocus();
      this.invalidate();
   }
}

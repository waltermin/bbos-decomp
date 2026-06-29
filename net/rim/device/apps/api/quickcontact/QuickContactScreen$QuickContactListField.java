package net.rim.device.apps.api.quickcontact;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.ui.Image;

final class QuickContactScreen$QuickContactListField extends ListField {
   Image _imageUpDown = QuickContactIcons.MOVE_UP_DOWN_ICONS.getImage(0);
   XYRect _focus = (XYRect)(new Object());
   Tag tagMove = Tag.create("list-move");
   boolean _moveItemMode;

   QuickContactScreen$QuickContactListField() {
      super(0, 0);
   }

   final void setMoveItemMode(boolean on) {
      this._moveItemMode = on;
      if (on) {
         ThemeAttributeSet tas = ThemeManager.getActiveTheme().getAttributeSet(this.tagMove);
         this.setThemeAttributesSpecial(tas, null);
      } else {
         this.setThemeAttributesSpecial(null, null);
      }

      this.invalidate();
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      super.drawFocus(graphics, on);
      if (this._moveItemMode) {
         int rowHeight = this.getRowHeight();
         this.getFocusRect(this._focus);
         if (on) {
            graphics.drawRect(this._focus.x, this._focus.y, this._focus.width, this._focus.height);
            this._imageUpDown.paint(graphics, this._focus.width - rowHeight, this._focus.y, rowHeight, rowHeight);
         }
      }
   }

   @Override
   protected final void onFocus(int direction) {
      if (direction > 0) {
         direction = 0;
      }

      super.onFocus(direction);
   }
}

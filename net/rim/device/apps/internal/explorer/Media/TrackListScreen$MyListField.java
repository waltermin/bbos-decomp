package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.api.ui.component.ListFieldCallback;

final class TrackListScreen$MyListField extends KeywordFilterCollectionListField {
   private XYRect _focus = (XYRect)(new Object());
   private boolean _moveMode;

   TrackListScreen$MyListField(ReadableList list, ListFieldCallback listCallback) {
      super(list, listCallback);
   }

   final void setMoveMode(boolean on) {
      this._moveMode = on;
      this.invalidate();
   }

   final boolean getMoveMode() {
      return this._moveMode;
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      super.drawFocus(graphics, on);
      if (this.getMoveMode()) {
         this.getFocusRect(this._focus);
         if (on) {
            graphics.drawRect(this._focus.x, this._focus.y, this._focus.width, this._focus.height);
         }
      }
   }
}

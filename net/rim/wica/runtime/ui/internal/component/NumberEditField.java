package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.component.BasicEditField;
import net.rim.wica.runtime.ui.internal.MultiFocusable;
import net.rim.wica.runtime.ui.internal.ScientificNumberTextFilter;

final class NumberEditField extends BasicEditField implements MultiFocusable {
   private ScientificNumberTextFilter _numberFilter = new ScientificNumberTextFilter();

   NumberEditField(long style) {
      super(style);
      this.setFilter(this._numberFilter);
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      this._numberFilter.setCursorPosition(this.getCursorPosition());
      this._numberFilter.setText(this.getText());
      return super.processKeyEvent(event, key, keycode, time);
   }

   @Override
   public final void moveFocus(int x, int y, int status, int time) {
      if (!this.isCursorPositionSet()) {
         this.onFocus(1);
      }

      super.moveFocus(x, y, status, time);
   }

   @Override
   protected final void onFocus(int direction) {
      if (direction > 0) {
         this.setCursorPosition(0, 0);
      } else {
         if (direction < 0) {
            this.setCursorPosition(Math.max(0, this.getTextLength()), 0);
         }
      }
   }
}

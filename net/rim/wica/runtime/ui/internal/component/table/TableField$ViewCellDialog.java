package net.rim.wica.runtime.ui.internal.component.table;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.internal.ui.component.PopupDialog;

class TableField$ViewCellDialog extends PopupDialog {
   private BasicEditField _edit = (BasicEditField)(new Object(9007199254740992L));

   public TableField$ViewCellDialog() {
      super((Manager)(new Object(299067162755072L)));
      this.add(this._edit);
   }

   public void setText(String text) {
      this._edit.setText(text);
      this._edit.setCursorPosition(0);
   }

   @Override
   public boolean onMenu(int instance) {
      this.close(-1);
      return true;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         return true;
      }

      if (key != 27 && key != '\n') {
         return false;
      }

      this.close(-1);
      return true;
   }
}

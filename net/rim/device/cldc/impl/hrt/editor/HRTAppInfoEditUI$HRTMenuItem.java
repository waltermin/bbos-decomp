package net.rim.device.cldc.impl.hrt.editor;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.util.Arrays;

final class HRTAppInfoEditUI$HRTMenuItem extends MenuItem {
   int _id;
   private final HRTAppInfoEditUI this$0;

   HRTAppInfoEditUI$HRTMenuItem(HRTAppInfoEditUI _1, int id) {
      super(_1.pickString(HRTAppInfoEditUI.MENU_STRING_IDS[id]), HRTAppInfoEditUI.MENU_ORDER[id], 0);
      this.this$0 = _1;
      this._id = id;
   }

   HRTAppInfoEditUI$HRTMenuItem(HRTAppInfoEditUI _1, ResourceBundle rb, int id) {
      super(rb.getString(id), 0, 0);
      this.this$0 = _1;
      this._id = id;
   }

   @Override
   public final void run() {
      switch (this._id) {
         case 0:
            HRTAppDacDialog dx = new HRTAppDacDialog(this.this$0._hri.getDac(), this.this$0._netType, null);
            if (dx.go() == 1) {
               Arrays.add(this.this$0._dacStrings, dx.getRetObject());
               this.this$0._dacList.setSize(this.this$0._dacStrings.length);
               this.this$0._dacsDirty = true;
               return;
            }
            break;
         case 1:
            int index = this.this$0._dacList.getSelectedIndex();
            HRTAppDacDialog d = new HRTAppDacDialog(this.this$0._hri.getDac(), this.this$0._netType, this.this$0._dacStrings[index]);
            if (d.go() == 1) {
               this.this$0._dacStrings[index] = d.getRetObject();
               this.this$0._dacList.invalidate(index);
               this.this$0._dacsDirty = true;
               return;
            }
            break;
         case 2:
            Arrays.removeAt(this.this$0._dacStrings, this.this$0._dacList.getSelectedIndex());
            this.this$0._dacList.setSize(this.this$0._dacStrings.length);
            this.this$0._dacsDirty = true;
            return;
         case 18:
            if (HRTAppInfoEditUI.access$700(this.this$0)) {
               this.this$0.close();
            }
      }
   }
}

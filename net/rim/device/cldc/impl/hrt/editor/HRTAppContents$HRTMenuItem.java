package net.rim.device.cldc.impl.hrt.editor;

import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.internal.i18n.CommonResource;

final class HRTAppContents$HRTMenuItem extends MenuItem {
   private int _id;
   private final HRTAppContents this$0;

   public HRTAppContents$HRTMenuItem(HRTAppContents _1, int id, boolean common, int ord) {
      super(common ? CommonResource.getBundle() : _1._rb, id, ord, 0);
      this.this$0 = _1;
      this._id = id | (common ? 0 : Integer.MIN_VALUE);
   }

   @Override
   public final void run() {
      boolean edit = false;
      switch (this._id) {
         case -2147483532:
            HRUtils.getThunks().displayEditor(HRUtils.getRegistrationHRT(), 0);
            return;
         case -2147483531:
            HRUtils.getThunks().sendRegistrationRequest();
            int index;
            if (HRUtils.getNpcForActiveNetwork() != -1 && RadioInfo.getSignalLevel() != -256) {
               index = 119;
            } else {
               index = 118;
            }

            Dialog.inform(this.this$0._rb.getString(index));
         default:
            return;
         case 9:
            this.this$0.close();
            return;
         case 13:
            this.this$0.add();
            return;
         case 16:
            edit = true;
         case 14:
            this.this$0.viewItem(edit);
            return;
         case 17:
            this.this$0.deleteItem();
      }
   }
}

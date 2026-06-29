package net.rim.device.cldc.impl.sb.editor;

import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

final class SBAppEditRecordUI$MyMenuItem extends MenuItem {
   private int _id;
   private final SBAppEditRecordUI this$0;

   public SBAppEditRecordUI$MyMenuItem(SBAppEditRecordUI _1, ResourceBundle rb, int id) {
      super(rb.getString(id), id, id);
      this.this$0 = _1;
      this._id = id;
   }

   @Override
   public final void run() {
      switch (this._id) {
         case 18:
            if (SBAppEditRecordUI.access$300(this.this$0)) {
               this.this$0.close();
               return;
            }
            break;
         case 101:
            this.this$0._rec.setType(0);
            this.this$0._sb.commit();
            this.this$0.close();
            return;
         case 102:
            this.this$0._sb.removeRecord(this.this$0._rec);
            this.this$0.close();
            break;
         case 104:
            HostRoutingTable hrt = this.this$0._rec.getAttachedHrt();
            if (this.this$0._recHriUsageField.getSelectedIndex() == 0) {
               hrt = HRUtils.getDefaultHRT();
            } else if (hrt == null) {
               hrt = new HostRoutingTable();
               this.this$0._rec.setAttachedHrt(hrt);
            }

            HRUtils.getThunks().displayEditor(hrt, this.this$0._viewMode);
            return;
      }
   }
}

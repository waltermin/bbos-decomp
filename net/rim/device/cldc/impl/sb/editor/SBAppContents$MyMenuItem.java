package net.rim.device.cldc.impl.sb.editor;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.MenuItem;

final class SBAppContents$MyMenuItem extends MenuItem {
   private int _id;
   private final SBAppContents this$0;
   public static final int MENU_ADD;
   public static final int MENU_VIEW;
   public static final int MENU_EDIT;
   public static final int MENU_DELETE;
   public static final int MENU_ACCEPT;
   public static final int MENU_DECLINE;
   public static final int MENU_UNDELETE;

   public SBAppContents$MyMenuItem(SBAppContents _1, int id, int resourceId, ResourceBundle rb) {
      super(rb.getString(resourceId), id, id);
      this.this$0 = _1;
      this._id = id;
   }

   @Override
   public final void run() {
      ServiceRecord rec = this.this$0.getRecordAtIndex(this.this$0._list.getSelectedIndex());
      switch (this._id) {
         case 10:
         default:
            rec = (ServiceRecord)(new Object(4));
            SBAppEditRecordUI editUI = new SBAppEditRecordUI(this.this$0._sb, rec, true);
            editUI.go(0, true);
            return;
         case 11:
            this.this$0.viewRecord(rec, false);
            return;
         case 12:
            this.this$0.viewRecord(rec, true);
            return;
         case 13:
            this.this$0.deleteRecord(rec);
            return;
         case 14:
            rec.setType(0);
            this.this$0._sb.commit();
            return;
         case 15:
            this.this$0._sb.removeRecord(rec);
            return;
         case 16:
            ServiceRecord[] records = this.this$0._sb.findRecordsByType(2);
            records[0].setType(0);
            this.this$0._sb.commit();
         case 9:
      }
   }
}

package net.rim.device.apps.api.quickcontact;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class QuickContactScreen$QuickContactItemVerb extends Verb {
   private int _id;
   private QuickContactItem _item;
   private final QuickContactScreen this$0;
   static final int EDIT;
   static final int DELETE;
   static final int INVOKE;
   static final int VIEW;
   static final int MOVE;

   QuickContactScreen$QuickContactItemVerb(QuickContactScreen _1, QuickContactItem item, int id, int ordering) {
      super(ordering);
      this.this$0 = _1;
      this._item = item;
      this._id = id;
   }

   @Override
   public final String toString() {
      switch (this._id) {
         case -1:
         case 3:
            return "";
         case 0:
         default:
            return CommonResources.getString(3011);
         case 1:
            return CommonResources.getString(1000);
         case 2:
            return this._item.getInvokeVerbString();
         case 4:
            return CommonResources.getString(9120);
      }
   }

   @Override
   public final Object invoke(Object parameter) {
      switch (this._id) {
         case -1:
            break;
         case 0:
         default:
            this._item.edit();
            return null;
         case 1:
            if (this._item != null && Dialog.ask(2, CommonResources.getString(9121), 3) == 3) {
               this.this$0._quickContactList.delete(this._item);
               return null;
            }
            break;
         case 2:
            if (this._item.invoke()) {
               this.this$0.close();
            }
            break;
         case 3:
            this._item.view();
            return null;
         case 4:
            this.this$0.enableMoveMode(true);
            return null;
      }

      return null;
   }
}

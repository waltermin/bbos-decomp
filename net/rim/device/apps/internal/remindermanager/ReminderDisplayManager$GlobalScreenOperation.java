package net.rim.device.apps.internal.remindermanager;

import net.rim.device.api.ui.component.Dialog;

public final class ReminderDisplayManager$GlobalScreenOperation {
   private int _operation;
   private Dialog _dialog;
   private final ReminderDisplayManager this$0;
   static final int NONE = 0;
   static final int PUSH = 1;
   static final int POP = 2;

   public ReminderDisplayManager$GlobalScreenOperation(ReminderDisplayManager _1, Dialog dialog, int operation) {
      this.this$0 = _1;
      this._dialog = dialog;
      this._operation = operation;
   }

   public final void execute() {
      if (this._dialog != null) {
         switch (this._operation) {
            case 0:
               break;
            case 1:
            default:
               this.this$0._app.pushGlobalScreen(this._dialog, 500, 2);
               this._operation = 0;
               return;
            case 2:
               if (this._dialog.getUiEngine() != null) {
                  this.this$0._app.popScreen(this._dialog);
               }

               this._operation = 0;
         }
      }
   }
}

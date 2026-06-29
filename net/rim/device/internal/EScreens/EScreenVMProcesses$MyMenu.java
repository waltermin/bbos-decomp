package net.rim.device.internal.EScreens;

import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.ui.MenuItem;

class EScreenVMProcesses$MyMenu extends MenuItem {
   int _type;
   private final EScreenVMProcesses this$0;

   public EScreenVMProcesses$MyMenu(EScreenVMProcesses _1, String text, int type) {
      super(text, 0, 100);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public void run() {
      switch (this._type) {
         case 0:
            break;
         case 1:
         default:
            this.this$0.refresh();
            return;
         case 2:
            ApplicationProcess p = this.this$0._processes[this.this$0._list.getSelectedIndex()].getApplicationProcess();
            if (p != null) {
               p.destroy((Throwable)(new Object("Killed by process manager")));

               label30:
               try {
                  Thread.sleep(1000);
               } finally {
                  break label30;
               }

               this.this$0.refresh();
               return;
            }
            break;
         case 3:
         case 4:
         case 5:
            EScreenVMProcesses._displayType = this._type;
            this.this$0.refresh();
            this.this$0._list.invalidate();
            return;
         case 6:
            this.this$0.toggleTimer();
            this.setText(this.this$0.autoRefreshText());
      }
   }
}

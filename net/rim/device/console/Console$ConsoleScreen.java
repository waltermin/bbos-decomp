package net.rim.device.console;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.internal.system.ApplicationManagerInternal;

final class Console$ConsoleScreen extends MainScreen {
   private Console _console;

   Console$ConsoleScreen(Console console) {
      this._console = console;
   }

   @Override
   public final void close() {
   }

   @Override
   protected final boolean openProductionBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1161904965:
         case 1163084626:
            ApplicationManager am = ApplicationManager.getApplicationManager();

            try {
               am.runApplication(((ApplicationManagerInternal)am).getEngScreenDescriptor());
               return true;
            } finally {
               ;
            }
         default:
            return super.openProductionBackdoor(backdoorCode);
      }
   }

   @Override
   public final boolean navigationClick(int status, int time) {
      this._console.invokeSelected();
      return true;
   }
}

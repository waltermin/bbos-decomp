package net.rim.device.apps.internal.memorycleaner;

import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.ribbon.RibbonLauncher;

final class MemoryCleanerApp$MemoryCleanerEventListener implements GlobalEventListener {
   MemoryCleanerApp$MemoryCleanerEventListener() {
      this.eventOccurred(5924166216341050021L, 0, 0, null, null);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 5924166216341050021L) {
         RibbonLauncher ribbonBar = RibbonLauncher.getInstance();
         if (ribbonBar != null) {
            MemoryCleanerManager memoryCleanerManager = MemoryCleanerManager.getInstance();
            if (memoryCleanerManager.enabled() && memoryCleanerManager.getShowAppOnRibbon()) {
               ApplicationDescriptor appDescriptor = MemoryCleanerApp.getApplicationDescriptor();
               if (appDescriptor != null) {
                  ribbonBar.registerAction("Memory Cleaner", (EntryPointDescriptor)(new Object(appDescriptor)));
                  return;
               }
            } else {
               ribbonBar.unregisterAction("Memory Cleaner");
            }
         }
      }
   }
}

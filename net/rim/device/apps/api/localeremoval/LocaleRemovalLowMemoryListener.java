package net.rim.device.apps.api.localeremoval;

import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.vm.Memory;
import net.rim.vm.PersistentInteger;

class LocaleRemovalLowMemoryListener implements LowMemoryListener {
   private ApplicationDescriptor _wizardAppDescriptor = null;
   private static long GUID_REMOVAL_LOW_MEMORY_LISTENER = 3312329773377818998L;
   private static final int CHUNK_SIZE = 32768;
   private static final int ONE_MEGABYTE = 1048576;

   private LocaleRemovalLowMemoryListener() {
      this._wizardAppDescriptor = this.getWizardApplicationDescriptor();
      if (this._wizardAppDescriptor != null) {
         PersistentObject pobject = PersistentStore.getPersistentObject(GUID_REMOVAL_LOW_MEMORY_LISTENER);
         Object[] memoryPlaceHolders = (Object[])pobject.getContents();
         if (memoryPlaceHolders == null) {
            memoryPlaceHolders = new Object[32];

            for (int i = memoryPlaceHolders.length - 1; i > -1; i--) {
               memoryPlaceHolders[i] = new byte[32768];
            }

            pobject.setContents(memoryPlaceHolders, 51);
            pobject.forceCommit();
         }

         LowMemoryManager.addLowMemoryListener(this);
         ApplicationRegistry.getApplicationRegistry().put(GUID_REMOVAL_LOW_MEMORY_LISTENER, this);
      }
   }

   static void loadListener() {
      new LocaleRemovalLowMemoryListener();
   }

   static void unloadListner() {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      Object obj = appReg.get(GUID_REMOVAL_LOW_MEMORY_LISTENER);
      if (obj instanceof LocaleRemovalLowMemoryListener) {
         LocaleRemovalLowMemoryListener lowMemListener = (LocaleRemovalLowMemoryListener)obj;
         lowMemListener.unloadListenerInternal();
      }
   }

   static boolean hasRemovalLowMemoryActivated() {
      int id = PersistentInteger.getId(GUID_REMOVAL_LOW_MEMORY_LISTENER, 0);
      return PersistentInteger.get(id) != 0;
   }

   private void setRemovalLowMemoryActivated() {
      int id = PersistentInteger.getId(GUID_REMOVAL_LOW_MEMORY_LISTENER, 0);
      PersistentInteger.set(id, 1);
   }

   private void unloadListenerInternal() {
      ApplicationRegistry.getApplicationRegistry().remove(GUID_REMOVAL_LOW_MEMORY_LISTENER);
      LowMemoryManager.removeLowMemoryListener(this);
   }

   @Override
   public boolean freeStaleObject(int priority) {
      if (priority == 0 && Memory.getFlashNeeded(true) > 0) {
         this.unloadListenerInternal();
         this.setRemovalLowMemoryActivated();
         this.launchWizardReminder();
         PersistentObject pobject = PersistentStore.getPersistentObject(GUID_REMOVAL_LOW_MEMORY_LISTENER);
         Object[] memoryPlaceHolders = (Object[])pobject.getContents();
         if (memoryPlaceHolders != null) {
            for (int i = memoryPlaceHolders.length - 1; i > -1; i--) {
               LowMemoryManager.markAsRecoverable(memoryPlaceHolders[i]);
            }

            PersistentStore.destroyPersistentObject(GUID_REMOVAL_LOW_MEMORY_LISTENER);
         }

         return true;
      } else {
         return false;
      }
   }

   private void launchWizardReminder() {
      if (this._wizardAppDescriptor != null) {
         ApplicationDescriptor appDesc = (ApplicationDescriptor)(new Object(this._wizardAppDescriptor, new String[]{"low-memory"}));

         try {
            ApplicationManager.getApplicationManager().runApplication(appDesc);
         } finally {
            return;
         }
      }
   }

   private ApplicationDescriptor getWizardApplicationDescriptor() {
      ApplicationDescriptor appDescriptor = null;
      int handle = CodeModuleManager.getModuleHandle("net_rim_bb_setupwizard_app");
      if (handle > 0) {
         ApplicationDescriptor[] appDescriptors = CodeModuleManager.getApplicationDescriptors(handle);
         if (appDescriptors != null) {
            for (int i = appDescriptors.length - 1; i >= 0; i--) {
               if (appDescriptors[i].getPosition() != 0) {
                  appDescriptor = appDescriptors[0];
               }
            }
         }
      }

      return appDescriptor;
   }
}

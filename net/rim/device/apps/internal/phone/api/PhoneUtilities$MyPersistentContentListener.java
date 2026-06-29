package net.rim.device.apps.internal.phone.api;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;

class PhoneUtilities$MyPersistentContentListener implements PersistentContentListener {
   PhoneUtilities$MyPersistentContentListener() {
      PersistentContent.addListener(this);
   }

   @Override
   public void persistentContentStateChanged(int state) {
   }

   @Override
   public void persistentContentModeChanged(int generation) {
      try {
         PhoneUtilities.setLastNumberDialed(PhoneUtilities.getLastNumberDialed());
      } finally {
         return;
      }
   }
}

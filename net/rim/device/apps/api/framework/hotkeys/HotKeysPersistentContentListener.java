package net.rim.device.apps.api.framework.hotkeys;

import java.util.Enumeration;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.IntHashtable;

final class HotKeysPersistentContentListener implements PersistentContentListener {
   IntHashtable _intHashtable;

   HotKeysPersistentContentListener(IntHashtable intHashtable) {
      this._intHashtable = intHashtable;
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      Enumeration e1 = this._intHashtable.elements();

      while (e1.hasMoreElements()) {
         IntHashtable intHashtable = (IntHashtable)e1.nextElement();
         Enumeration e2 = intHashtable.elements();

         while (e2.hasMoreElements()) {
            HotKeyEntry hotKeyEntry = (HotKeyEntry)e2.nextElement();
            hotKeyEntry.reCrypt(true, true);
         }
      }
   }
}

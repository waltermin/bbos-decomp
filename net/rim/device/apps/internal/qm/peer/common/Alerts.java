package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class Alerts {
   private int[] _alerts = new int[0];

   private final int hash(Contact contact) {
      return contact != null ? contact.getIdHash() : 0;
   }

   public final boolean isSet(Contact contact) {
      return Arrays.binarySearch(this._alerts, this.hash(contact)) >= 0;
   }

   public final void set(Contact contact) {
      int hash = this.hash(contact);
      int index = Arrays.binarySearch(this._alerts, hash);
      if (index < 0) {
         index = -index - 1;
         int length = this._alerts.length;
         Array.resize(this._alerts, length + 1);
         System.arraycopy(this._alerts, index, this._alerts, index + 1, length - index);
         this._alerts[index] = hash;
      }
   }

   public final void clear(Contact contact) {
      if (contact != null) {
         int index = Arrays.binarySearch(this._alerts, contact.getIdHash());
         if (index >= 0) {
            int newLength = this._alerts.length - 1;
            System.arraycopy(this._alerts, index + 1, this._alerts, index, newLength - index);
            Array.resize(this._alerts, newLength);
         }
      }
   }
}

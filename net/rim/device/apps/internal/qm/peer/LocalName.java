package net.rim.device.apps.internal.qm.peer;

import net.rim.device.apps.internal.qm.resource.QmResources;

final class LocalName {
   private int _id;

   LocalName(int id) {
      this._id = id;
   }

   @Override
   public final String toString() {
      return QmResources.getString(this._id);
   }
}

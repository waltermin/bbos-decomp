package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;

final class ServiceKey implements Persistable {
   private String _name;
   private String _uid;

   public ServiceKey() {
   }

   ServiceKey(String name, String uid) {
      this.initialize(name, uid);
   }

   final void initialize(String name, String uid) {
      this._name = name;
      this._uid = uid;
   }

   final String getName() {
      return this._name;
   }

   final String getUid() {
      return this._uid;
   }

   @Override
   public final int hashCode() {
      return this._name.hashCode() ^ StringUtilities.hashCodeIgnoreCase(this._uid);
   }

   @Override
   public final boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof ServiceKey)) {
         return false;
      }

      ServiceKey other = (ServiceKey)object;
      return StringUtilities.strEqualIgnoreCase(this._name, other._name, 1701707776) && StringUtilities.strEqualIgnoreCase(this._uid, other._uid, 1701707776);
   }
}

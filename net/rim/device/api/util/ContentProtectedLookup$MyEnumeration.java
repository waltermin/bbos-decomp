package net.rim.device.api.util;

import java.util.Enumeration;
import net.rim.device.api.system.PersistentContent;

class ContentProtectedLookup$MyEnumeration implements Enumeration {
   private Enumeration _e;

   ContentProtectedLookup$MyEnumeration(Enumeration e) {
      this._e = e;
   }

   @Override
   public boolean hasMoreElements() {
      return this._e.hasMoreElements();
   }

   @Override
   public Object nextElement() {
      return PersistentContent.decode(this._e.nextElement());
   }
}

package net.rim.device.api.util;

import java.util.Enumeration;
import net.rim.device.api.system.PersistentContent;

class ContentProtectedEnumeration implements Enumeration {
   private Enumeration _enum;

   public ContentProtectedEnumeration(Enumeration enumeration) {
      this._enum = enumeration;
   }

   @Override
   public boolean hasMoreElements() {
      return this._enum.hasMoreElements();
   }

   @Override
   public Object nextElement() {
      return PersistentContent.decode(this._enum.nextElement());
   }
}

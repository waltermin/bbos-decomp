package net.rim.device.api.ui.theme;

import java.util.Enumeration;

class MergedEnumeration implements Enumeration {
   private Enumeration _a;
   private Enumeration _b;

   public MergedEnumeration(Enumeration a, Enumeration b) {
      this._a = a;
      this._b = b;
   }

   @Override
   public boolean hasMoreElements() {
      return this._a.hasMoreElements() || this._b.hasMoreElements();
   }

   @Override
   public Object nextElement() {
      return this._a.hasMoreElements() ? this._a.nextElement() : this._b.nextElement();
   }
}

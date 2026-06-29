package net.rim.blackberry.api.menuitem;

import net.rim.device.api.util.BitSet;
import net.rim.device.internal.applicationcontrol.ApplicationControl;

public class ApplicationMenuItem {
   int _order;
   Object _cookie;
   BitSet _flags;
   static final int EMAIL_ALLOWED_INDEX = 0;
   static final int PIM_ALLOWED_INDEX = 1;

   public ApplicationMenuItem(Object context, int order) {
      this(order);
   }

   public ApplicationMenuItem(int order) {
      this._order = order;
      this._flags = new BitSet();
      this._flags.set(0);
      this._flags.set(1);
      if (!ApplicationControl.isEmailAllowed(true)) {
         this._flags.clear(0);
      }

      if (!ApplicationControl.isPIMAllowed(true)) {
         this._flags.clear(1);
      }
   }

   public Object run(Object _1) {
      throw null;
   }

   @Override
   public String toString() {
      throw null;
   }

   void setInternalCookie(Object o) {
      this._cookie = o;
   }

   Object getInternalCookie() {
      return this._cookie;
   }

   @Override
   public int hashCode() {
      int result = 17;
      result = 37 * result + this._order;
      if (this._cookie != null) {
         result = 37 * result + this._cookie.hashCode();
      }

      return 37 * result + this.toString().hashCode();
   }
}

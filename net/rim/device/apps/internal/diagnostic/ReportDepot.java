package net.rim.device.apps.internal.diagnostic;

import java.util.Vector;
import net.rim.device.api.util.Persistable;

public final class ReportDepot implements Persistable {
   Vector v = new Vector();

   ReportDepot() {
   }

   public final void addElement(Object r) {
      this.v.addElement(r);
   }

   public final void removeAllElements() {
      this.v.removeAllElements();
   }

   public final void removeLastElement() {
      this.v.removeElementAt(this.v.size() - 1);
   }

   public final Object getLastElement() {
      return this.v.lastElement();
   }

   public final int size() {
      return this.v.size();
   }

   public final Object elementAt(int i) {
      return this.v.elementAt(i);
   }

   public final void removeElementAt(int i) {
      this.v.removeElementAt(i);
   }
}

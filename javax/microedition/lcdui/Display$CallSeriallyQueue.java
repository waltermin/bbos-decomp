package javax.microedition.lcdui;

import java.util.Vector;
import net.rim.device.api.system.Application;

final class Display$CallSeriallyQueue extends Vector implements Runnable {
   public final void addElement(Runnable r) {
      synchronized (this) {
         super.addElement(r);
         if (this.size() == 1) {
            Application.getApplication().invokeLaterSpecial(this, 1);
         }
      }
   }

   @Override
   public final void run() {
      Runnable r = null;

      try {
         r = (Runnable)this.elementAt(0);
      } catch (Exception var9) {
      } finally {
         if (r == null) {
            return;
         }
      }

      r.run();
      synchronized (this) {
         this.removeElementAt(0);
         if (this.size() > 0) {
            Application.getApplication().invokeLaterSpecial(this, 1);
         }
      }
   }

   private Display$CallSeriallyQueue() {
   }

   Display$CallSeriallyQueue(Display$1 x0) {
      this();
   }
}

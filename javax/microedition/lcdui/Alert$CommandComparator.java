package javax.microedition.lcdui;

import net.rim.device.api.util.Comparator;

class Alert$CommandComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      return ((Command)o1).getPriority() - ((Command)o2).getPriority();
   }
}

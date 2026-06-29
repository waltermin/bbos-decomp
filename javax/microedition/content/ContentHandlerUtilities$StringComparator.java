package javax.microedition.content;

import net.rim.device.api.util.Comparator;

final class ContentHandlerUtilities$StringComparator implements Comparator {
   public ContentHandlerUtilities$StringComparator() {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      String s1 = (String)o1;
      String s2 = (String)o2;
      return s1.compareTo(s2);
   }
}

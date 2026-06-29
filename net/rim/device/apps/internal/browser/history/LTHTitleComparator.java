package net.rim.device.apps.internal.browser.history;

import net.rim.device.api.util.Comparator;

final class LTHTitleComparator implements Comparator {
   @Override
   public final int compare(Object a, Object b) {
      LongTermHistoryNode node1 = (LongTermHistoryNode)a;
      LongTermHistoryNode node2 = (LongTermHistoryNode)b;
      int result = node1.getDomain().compareTo(node2.getDomain());
      if (result == 0) {
         result = node1.getTitle().compareTo(node2.getTitle());
         return result == 0 ? (int)(node1.getTimestamp() - node2.getTimestamp()) : result;
      } else {
         return result;
      }
   }
}

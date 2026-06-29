package net.rim.device.apps.internal.browser.history;

import net.rim.device.api.util.Comparator;

final class LTHTimeComparator implements Comparator {
   @Override
   public final int compare(Object a, Object b) {
      LongTermHistoryNode node1 = (LongTermHistoryNode)a;
      LongTermHistoryNode node2 = (LongTermHistoryNode)b;
      return (int)(node2.getTimestamp() - node1.getTimestamp());
   }
}

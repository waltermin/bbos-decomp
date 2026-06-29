package net.rim.device.apps.internal.browser.history;

import net.rim.device.api.util.Comparator;

final class LTHUrlComparator implements Comparator {
   @Override
   public final int compare(Object a, Object b) {
      LongTermHistoryNode node1 = (LongTermHistoryNode)a;
      LongTermHistoryNode node2 = (LongTermHistoryNode)b;
      int result = node1.getUrl().compareTo(node2.getUrl());
      return result == 0 ? (int)(node2.getTimestamp() - node1.getTimestamp()) : result;
   }
}

package net.rim.device.apps.internal.memo;

import net.rim.device.api.util.Comparator;
import net.rim.device.internal.i18n.CollatorImpl;

final class MemoCollectionImpl$MemoComparator implements Comparator {
   private static CollatorImpl _collator = (CollatorImpl)(new Object());

   @Override
   public final int compare(Object o1, Object o2) {
      return _collator.compare(((MemoModelImpl)o1).getSubjectModel(), ((MemoModelImpl)o2).getSubjectModel());
   }
}

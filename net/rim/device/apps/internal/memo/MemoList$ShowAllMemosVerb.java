package net.rim.device.apps.internal.memo;

import net.rim.device.apps.api.framework.verb.Verb;

final class MemoList$ShowAllMemosVerb extends Verb {
   private final MemoList this$0;

   MemoList$ShowAllMemosVerb(MemoList _1) {
      super(479488, 1419048744345184776L, "net.rim.device.apps.internal.resource.Memo", 250);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.setSearchPattern(null);
      return null;
   }
}

package net.rim.device.apps.internal.globalsearch;

import net.rim.device.apps.api.framework.verb.Verb;

final class GlobalSearchScreen$SelectVerb extends Verb {
   private boolean _selectAll;
   private final GlobalSearchScreen this$0;

   public GlobalSearchScreen$SelectVerb(GlobalSearchScreen _1, boolean selectAll) {
      super(16865360);
      this.this$0 = _1;
      this._selectAll = selectAll;
   }

   @Override
   public final String toString() {
      return GlobalSearchScreen._rb.getString(this._selectAll ? 4 : 5);
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.selectAll(this._selectAll);
      return null;
   }
}

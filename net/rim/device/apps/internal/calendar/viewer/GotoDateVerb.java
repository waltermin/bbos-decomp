package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.apps.api.calendar.calconstants.CalOptionCache;

final class GotoDateVerb extends CalendarViewerVerb {
   private int _type;
   private long _target;
   public static final int GOTO_DATE;
   public static final int GOTO_TODAY;
   public static final int GOTO_PICK;

   public GotoDateVerb(int displayStringId, int order, int hotkeyResourceId) {
      super(displayStringId, order, hotkeyResourceId);
   }

   public final void setTarget(long target) {
      this._target = target;
      this._type = 0;
   }

   public final void setTargetType(int type) {
      this._target = System.currentTimeMillis();
      this._type = type;
   }

   @Override
   public final Object invoke(Object parameter) {
      long target = this.getTarget();
      if (target != 0) {
         CalOptionCache.setTimeWithFocus(target);
      }

      return null;
   }

   private final long getTarget() {
      switch (this._type) {
         case 0:
            return this._target;
         case 1:
            return System.currentTimeMillis();
         case 2:
         default:
            SelectDate utilSelect = new SelectDate();
            utilSelect.setDate(System.currentTimeMillis());
            return utilSelect.doSelection() ? utilSelect.getDate() : 0;
      }
   }
}

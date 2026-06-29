package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;

final class ViewCalendarDatabaseVerbManager$ChooseCalendarVerb extends Verb {
   private final ViewCalendarDatabaseVerbManager this$0;

   public ViewCalendarDatabaseVerbManager$ChooseCalendarVerb(ViewCalendarDatabaseVerbManager _1) {
      super(1126480);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      ViewCalendarPopupScreen screen = new ViewCalendarPopupScreen(this.this$0._folders);
      UiApplication.getUiApplication().pushModalScreen(screen);
      return null;
   }

   @Override
   public final String toString() {
      return this.this$0._rb.getString(666);
   }
}

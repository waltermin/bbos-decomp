package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class NewCallScreen$CancelNewCallScreenVerb extends Verb {
   private NewCallScreen _screen;
   private final NewCallScreen this$0;

   NewCallScreen$CancelNewCallScreenVerb(NewCallScreen _1, NewCallScreen screen) {
      super(0, CommonResources.getResourceBundle(), 9042);
      this.this$0 = _1;
      this._screen = screen;
   }

   @Override
   public final Object invoke(Object parameter) {
      NewCallScreen.access$000(this.this$0, null);
      this.this$0._voiceApp.invokeLater(new NewCallScreen$CancelNewCallScreenVerb$1(this));
      return null;
   }
}

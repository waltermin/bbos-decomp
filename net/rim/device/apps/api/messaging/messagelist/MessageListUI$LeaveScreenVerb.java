package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.util.OTAFMControl;
import net.rim.device.internal.i18n.CommonResource;

class MessageListUI$LeaveScreenVerb extends Verb {
   private final MessageListUI this$0;

   MessageListUI$LeaveScreenVerb(MessageListUI _1) {
      super(268501008, CommonResource.getBundle(), 9);
      this.this$0 = _1;
   }

   @Override
   public Object invoke(Object parameter) {
      switch (this.this$0._exitAction) {
         case 1:
            UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
            this.this$0.updateLastSeenTimeStamp();
            this.this$0.initiateBulkMarkOldOperation();
            return null;
         case 2:
            UiApplication.getUiApplication().requestBackground();
            this.this$0.updateLastSeenTimeStamp();
            this.this$0.initiateBulkMarkOldOperation();
            OTAFMControl.getControlInstance().flushBatchedCommands();
         case 0:
            return null;
         case 3:
         case 4:
         default:
            Screen activeScreen = UiApplication.getUiApplication().getActiveScreen();
            if (activeScreen instanceof MessageListUI) {
               ((MessageListUI)activeScreen).haltFiltering();
            }

            this.this$0.updateLastSeenTimeStamp();
            this.this$0.initiateBulkMarkOldOperation();
            return null;
      }
   }
}

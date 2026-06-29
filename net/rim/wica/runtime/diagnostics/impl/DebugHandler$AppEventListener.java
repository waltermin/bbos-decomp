package net.rim.wica.runtime.diagnostics.impl;

import net.rim.wica.common.debug.protocol.messages.targetevents.ITargetEventMessage;
import net.rim.wica.common.debug.protocol.messages.targetevents.TargetEventMessageFactory;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.metadata.component.Msg;

final class DebugHandler$AppEventListener implements EventListener {
   private long _applicationId;
   private final DebugHandler this$0;

   DebugHandler$AppEventListener(DebugHandler this$0, long applicationId) {
      this.this$0 = this$0;
      this._applicationId = applicationId;
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      ITargetEventMessage messageBody = null;
      switch (event) {
         case 900:
         default:
            Msg sentMsg = (Msg)data;
            messageBody = TargetEventMessageFactory.createMessageReceivedTargetEventMessage(this._applicationId, sentMsg.getMsgName(), 0);
            this.this$0._manager.sendTargetEventMessage(6, messageBody);
            return;
         case 901:
            Msg receivedMsg = (Msg)data;
            messageBody = TargetEventMessageFactory.createMessageReceivedTargetEventMessage(this._applicationId, receivedMsg.getMsgName(), 0);
            this.this$0._manager.sendTargetEventMessage(7, messageBody);
         case 899:
      }
   }
}

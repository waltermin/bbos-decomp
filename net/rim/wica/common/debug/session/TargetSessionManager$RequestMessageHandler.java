package net.rim.wica.common.debug.session;

import net.rim.wica.common.debug.protocol.messages.request.IDetachRequestMessage;
import net.rim.wica.common.debug.protocol.messages.request.IHandshakeRequestMessage;
import net.rim.wica.common.debug.protocol.messages.request.IRequestMessage;
import net.rim.wica.common.debug.protocol.messages.request.ISessionResetRequestMessage;
import net.rim.wica.common.debug.protocol.messages.response.IResponseMessage;
import net.rim.wica.common.debug.protocol.messages.response.ResponseMessageFactory;

final class TargetSessionManager$RequestMessageHandler implements IRequestMessageHandler {
   private final TargetSessionManager this$0;

   private TargetSessionManager$RequestMessageHandler(TargetSessionManager this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void handleRequestMessage(int messageType, int messageId, IRequestMessage request) {
      try {
         switch (messageType) {
            case -1:
               this.this$0._messageHandler.handleRequestMessage(messageType, messageId, request);
               return;
            case 0:
            default:
               this.handleHandshake(messageId, (IHandshakeRequestMessage)request);
               break;
            case 1:
               this.handleSessionReset(messageId, (ISessionResetRequestMessage)request);
               break;
            case 2:
               this.handleDetach(messageId, (IDetachRequestMessage)request);
         }
      } catch (ProtocolStateMachine$InvalidStateException e) {
         System.err.println(e);
      }
   }

   private final void handleHandshake(int messageId, IHandshakeRequestMessage request) {
      IResponseMessage response = ResponseMessageFactory.createHandshakeResponseMessage(request.getHandshakePattern());
      this.this$0.sendResponseMessage(0, messageId, response);
      this.this$0._fsm.eventHandshakeSucceeded();
   }

   private final void handleSessionReset(int messageId, ISessionResetRequestMessage request) {
      IResponseMessage response = ResponseMessageFactory.createSessionResetResponseMessage(request.getNewSessionId());
      this.this$0.sendResponseMessage(1, messageId, response);
      this.this$0._fsm.eventSessionReset();
   }

   private final void handleDetach(int messageId, IDetachRequestMessage request) {
      this.this$0._msgReceiverThread.stopLoopWithInterrupt();
      IResponseMessage response = ResponseMessageFactory.createDetachResponseMessage(request.getSessionId());
      this.this$0.sendResponseMessage(2, messageId, response);
      this.this$0._fsm.eventDetached();
   }

   TargetSessionManager$RequestMessageHandler(TargetSessionManager x0, TargetSessionManager$1 x1) {
      this(x0);
   }
}

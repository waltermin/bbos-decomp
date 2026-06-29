package net.rim.wica.common.debug.protocol.messages.response;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;

public final class ResponseMessageFactory {
   private ResponseMessageFactory() {
   }

   public static final IHandshakeResponseMessage createHandshakeResponseMessage() {
      return new HandshakeResponseMessage();
   }

   public static final IHandshakeResponseMessage createHandshakeResponseMessage(int handshakePattern) {
      IHandshakeResponseMessage msg = createHandshakeResponseMessage();
      msg.setHandshakePattern(handshakePattern);
      return msg;
   }

   public static final ISessionResetResponseMessage createSessionResetResponseMessage() {
      return new SessionResetResponseMessage();
   }

   public static final ISessionResetResponseMessage createSessionResetResponseMessage(int sessionId) {
      ISessionResetResponseMessage msg = createSessionResetResponseMessage();
      msg.setNewSessionId(sessionId);
      return msg;
   }

   public static final IDetachResponseMessage createDetachResponseMessage() {
      return new DetachResponseMessage();
   }

   public static final IDetachResponseMessage createDetachResponseMessage(int sessionId) {
      IDetachResponseMessage msg = createDetachResponseMessage();
      msg.setSessionId(sessionId);
      return msg;
   }

   public static final IApplicationCommandResponseMessage createApplicationCommandResponseMessage() {
      return new ApplicationCommandResponseMessage();
   }

   public static final IApplicationCommandResponseMessage createApplicationCommandResponseMessage(long applicationId) {
      IApplicationCommandResponseMessage msg = createApplicationCommandResponseMessage();
      msg.setApplicationId(applicationId);
      return msg;
   }

   public static final IApplicationStatusResponseMessage createApplicationStatusResponseMessage() {
      return new ApplicationStatusResponseMessage();
   }

   public static final IApplicationStatusResponseMessage createApplicationStatusResponseMessage(long applicationId, boolean running) {
      IApplicationStatusResponseMessage msg = createApplicationStatusResponseMessage();
      msg.setApplicationId(applicationId);
      msg.setIsRunning(running);
      return msg;
   }

   public static final IResponseMessage extractResponseMessage(int messageType, IInputByteStreamAdapter msgStream) {
      IResponseMessage msg;
      switch (messageType) {
         case -1:
            throw new IllegalArgumentException("Unsupported messageType: " + messageType);
         case 0:
         default:
            msg = createHandshakeResponseMessage();
            break;
         case 1:
            msg = createSessionResetResponseMessage();
            break;
         case 2:
            msg = createDetachResponseMessage();
            break;
         case 3:
         case 4:
            msg = createApplicationCommandResponseMessage();
            break;
         case 5:
            msg = createApplicationStatusResponseMessage();
      }

      msg.deserialize(msgStream);
      return msg;
   }
}

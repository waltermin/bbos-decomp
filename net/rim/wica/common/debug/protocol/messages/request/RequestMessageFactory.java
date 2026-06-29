package net.rim.wica.common.debug.protocol.messages.request;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;

public final class RequestMessageFactory {
   private RequestMessageFactory() {
   }

   public static final IHandshakeRequestMessage createHandshakeRequestMessage() {
      return new HandshakeRequestMessage();
   }

   public static final ISessionResetRequestMessage createSessionResetMessage() {
      return new SessionResetRequestMessage();
   }

   public static final IDetachRequestMessage createDetachRequestMessage() {
      return new DetachRequestMessage();
   }

   public static final IApplicationCommandRequestMessage createApplicationCommandRequestMessage() {
      return new ApplicationCommandRequestMessage();
   }

   public static final IRequestMessage extractRequestMessage(int messageType, IInputByteStreamAdapter msgStream) {
      IRequestMessage msg;
      switch (messageType) {
         case -1:
            throw new IllegalArgumentException("Unsupported messageType: " + messageType);
         case 0:
         default:
            msg = createHandshakeRequestMessage();
            break;
         case 1:
            msg = createSessionResetMessage();
            break;
         case 2:
            msg = createDetachRequestMessage();
            break;
         case 3:
         case 4:
         case 5:
            msg = createApplicationCommandRequestMessage();
      }

      msg.deserialize(msgStream);
      return msg;
   }
}

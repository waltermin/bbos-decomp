package net.rim.wica.common.debug.protocol.messages;

import net.rim.wica.common.debug.io.ByteArrayInputByteStream;
import net.rim.wica.common.debug.protocol.messages.request.RequestMessageFactory;
import net.rim.wica.common.debug.protocol.messages.response.ResponseMessageFactory;
import net.rim.wica.common.debug.protocol.messages.targetevents.TargetEventMessageFactory;

public final class MessageFactory {
   public static final IMessageEnvelope createEmptyMessageEnvelope() {
      return new MessageEnvelope();
   }

   private static final MessageEnvelope createEmptyMessageEnvelopeImpl(int messageSet) {
      return new MessageEnvelope(messageSet);
   }

   private static final MessageEnvelope createEmptyResponseMessageImpl() {
      return createEmptyMessageEnvelopeImpl(1);
   }

   private static final MessageEnvelope createEmptyTargetEventMessageImpl() {
      return createEmptyMessageEnvelopeImpl(2);
   }

   public static final IMessageEnvelope createResponseMessage(int messageType, byte[] body) {
      MessageEnvelope msg = createEmptyResponseMessageImpl();
      msg.setBody(body);
      msg.setMessageType(messageType);
      return msg;
   }

   public static final IMessageEnvelope createTargetEventMessage(int messageType, byte[] body) {
      MessageEnvelope msg = createEmptyTargetEventMessageImpl();
      msg.setBody(body);
      msg.setMessageType(messageType);
      return msg;
   }

   public static final IBodyMessage extractBodyMessage(IMessageEnvelope msgEnvelope) {
      ByteArrayInputByteStream byteStream = new ByteArrayInputByteStream(msgEnvelope.getBody());
      IBodyMessage result = null;
      switch (msgEnvelope.getMessageSet()) {
         case 0:
         default:
            return RequestMessageFactory.extractRequestMessage(msgEnvelope.getMessageType(), byteStream);
         case 1:
            return ResponseMessageFactory.extractResponseMessage(msgEnvelope.getMessageType(), byteStream);
         case 2:
            result = TargetEventMessageFactory.extractTargetEventMessage(msgEnvelope.getMessageType(), byteStream);
         case -1:
            return result;
      }
   }

   private MessageFactory() {
   }
}

package net.rim.wica.common.debug.protocol.messages.targetevents;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;

public final class TargetEventMessageFactory {
   private TargetEventMessageFactory() {
   }

   public static final IApplicationTargetEventMessage createApplicationTargetEventMessage() {
      return new ApplicationTargetEventMessage();
   }

   public static final IApplicationTargetEventMessage createApplicationTargetEventMessage(long applicationId) {
      IApplicationTargetEventMessage msg = createApplicationTargetEventMessage();
      msg.setApplicationId(applicationId);
      return msg;
   }

   public static final IApplicationUpgradedTargetEventMessage createApplicationUpgradedTargetEventMessage() {
      return new ApplicationUpgradedTargetEventMessage();
   }

   public static final IApplicationUpgradedTargetEventMessage createApplicationUpgradedTargetEventMessage(long applicationId, long newApplicationId) {
      IApplicationUpgradedTargetEventMessage msg = createApplicationUpgradedTargetEventMessage();
      msg.setApplicationId(applicationId);
      msg.setNewApplicationId(newApplicationId);
      return msg;
   }

   public static final ITraceTargetEventMessage createTraceTargetEventMessage() {
      return new TraceTargetEventMessage();
   }

   public static final ITraceTargetEventMessage createTraceTargetEventMessage(long applicationId, String message) {
      ITraceTargetEventMessage msg = createTraceTargetEventMessage();
      msg.setApplicationId(applicationId);
      msg.setMessage(message);
      return msg;
   }

   public static final IMessageSentTargetEventMessage createMessageSentTargetEventMessage() {
      return new MessageSentTargetEventMessage();
   }

   public static final IMessageReceivedTargetEventMessage createMessageReceivedTargetEventMessage() {
      return new MessageReceivedTargetEventMessage();
   }

   public static final IMessageReceivedTargetEventMessage createMessageReceivedTargetEventMessage(long applicationId, String type, long correlationId) {
      IMessageReceivedTargetEventMessage msg = createMessageReceivedTargetEventMessage();
      msg.setApplicationId(applicationId);
      msg.setType(type);
      msg.setCorrelationId(correlationId);
      return msg;
   }

   public static final ITargetEventMessage extractTargetEventMessage(int messageType, IInputByteStreamAdapter msgStream) {
      ITargetEventMessage msg;
      switch (messageType) {
         case -1:
            throw new Object(((StringBuffer)(new Object("Unsupported messageType: "))).append(messageType).toString());
         case 0:
         case 1:
         case 3:
         case 4:
         default:
            msg = createApplicationTargetEventMessage();
            break;
         case 2:
            msg = createApplicationUpgradedTargetEventMessage();
            break;
         case 5:
            msg = createTraceTargetEventMessage();
            break;
         case 6:
            msg = createMessageSentTargetEventMessage();
            break;
         case 7:
            msg = createMessageReceivedTargetEventMessage();
      }

      msg.deserialize(msgStream);
      return msg;
   }
}

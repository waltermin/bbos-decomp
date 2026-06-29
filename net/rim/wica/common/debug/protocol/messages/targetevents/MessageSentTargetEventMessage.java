package net.rim.wica.common.debug.protocol.messages.targetevents;

final class MessageSentTargetEventMessage extends BaseMessageTargetEventMessage implements IMessageSentTargetEventMessage {
   @Override
   public final String toString() {
      return ((StringBuffer)(new Object("Message sent target event: messageType = "))).append(this.getType()).toString();
   }
}

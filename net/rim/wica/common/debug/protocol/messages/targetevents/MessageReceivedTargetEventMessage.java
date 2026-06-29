package net.rim.wica.common.debug.protocol.messages.targetevents;

final class MessageReceivedTargetEventMessage extends BaseMessageTargetEventMessage implements IMessageReceivedTargetEventMessage {
   @Override
   public final String toString() {
      return ((StringBuffer)(new Object("Message received target event: messageType = "))).append(this.getType()).toString();
   }
}

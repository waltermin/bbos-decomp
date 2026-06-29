package net.rim.wica.runtime.messaging;

public interface MessagingService {
   Message createMessageInstance();

   InboundQueueConnection getInboundQueueConnection(long var1);

   OutboundQueueConnection getOutboundQueueConnection(long var1);

   void registerServiceMessageConsumer(String var1, MessageConsumer var2);

   void deregisterServiceMessageConsumer(MessageConsumer var1);

   void registerSystemMessageConsumer(int[] var1, MessageConsumer var2);

   void deregisterSystemMessageConsumer(MessageConsumer var1);

   int getWicletIncomingMessageCount(long var1);

   int getWicletOutgoingMessageCount(long var1);

   int getWicletFlowControlState(long var1);

   Message sendMessage(Message var1);
}

package net.rim.wica.runtime.messaging;

public interface InboundQueueConnection {
   boolean isEmpty();

   void open(InboundQueueListener var1);

   void deactivate();

   void close();

   boolean isOpen();

   boolean isDeactivated();

   boolean isClosed();

   Message getNextMessage();
}

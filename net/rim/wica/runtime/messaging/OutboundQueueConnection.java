package net.rim.wica.runtime.messaging;

public interface OutboundQueueConnection {
   int QUEUE_OK;
   int QUEUE_CRITICAL;
   int QUEUE_FULL;

   int getQueueStatus();
}

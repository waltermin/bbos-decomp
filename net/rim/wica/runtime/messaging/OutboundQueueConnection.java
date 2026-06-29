package net.rim.wica.runtime.messaging;

public interface OutboundQueueConnection {
   int QUEUE_OK = 0;
   int QUEUE_CRITICAL = 1;
   int QUEUE_FULL = 2;

   int getQueueStatus();
}

package net.rim.device.api.streaming;

import net.rim.device.api.system.ApplicationRegistry;

public final class StreamingFactory {
   private static final long ID = -7849240269607795087L;

   private StreamingFactory() {
   }

   public static final void setStreamingSystem(Streaming streamingSystem) {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      Object prevStreaming = applicationRegistry.replace(-7849240269607795087L, streamingSystem);
      if (prevStreaming != null) {
         throw new IllegalStateException("Streaming system already present");
      }
   }

   public static final Streaming getStreamingSystem() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      return (Streaming)applicationRegistry.waitFor(-7849240269607795087L);
   }
}

package net.rim.device.api.streaming;

public interface Streaming {
   int OK;
   int ERROR_OUT_OF_SESSIONS;
   int ERROR_OUT_OF_MEMORY;
   int ERROR_UNDEFINED;
   int ERROR_SESSION_NOT_OPENED;

   boolean isSupported();

   int createSession(int var1, int var2);

   SourceSession manageAsSource(int var1, SourceListener var2);

   SinkSession manageAsSink(int var1, SinkListener var2);
}

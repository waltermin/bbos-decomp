package javax.microedition.media;

public interface PlayerListener {
   String STARTED;
   String STOPPED;
   String STOPPED_AT_TIME;
   String END_OF_MEDIA;
   String DURATION_UPDATED;
   String DEVICE_UNAVAILABLE;
   String DEVICE_AVAILABLE;
   String VOLUME_CHANGED;
   String SIZE_CHANGED;
   String ERROR;
   String CLOSED;
   String RECORD_STARTED;
   String RECORD_STOPPED;
   String RECORD_ERROR;
   String BUFFERING_STARTED;
   String BUFFERING_STOPPED;

   void playerUpdate(Player var1, String var2, Object var3);
}

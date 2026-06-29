package net.rim.device.internal.media;

public interface MediaStreamingCallback {
   int DONE_REASON_COMPLETED;
   int DONE_REASON_KEY_PRESSED;
   int DONE_REASON_STOP_CALLED;
   int DONE_REASON_PREEMPTED;
   int RECORD_STREAM_DONE;
   int RECORD_STREAM_FAIL;

   boolean moreData();

   void recordingDone(int var1, int var2);

   boolean sessionSourceEnded();

   void streamingDone(int var1);

   void streamingSentAllData();
}

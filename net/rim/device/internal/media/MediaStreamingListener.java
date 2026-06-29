package net.rim.device.internal.media;

public interface MediaStreamingListener {
   int DONE_REASON_COMPLETED;
   int DONE_REASON_KEY_PRESSED;
   int DONE_REASON_STOP_CALLED;
   int DONE_REASON_PREEMPTED;
   int RECORD_STREAM_DONE;
   int RECORD_STREAM_FAIL;

   void recordingDone(int var1);

   void streamingDone(int var1);
}

package net.rim.device.internal.media;

public interface MediaStreamingListener {
   int DONE_REASON_COMPLETED = 2;
   int DONE_REASON_KEY_PRESSED = 1;
   int DONE_REASON_STOP_CALLED = 3;
   int DONE_REASON_PREEMPTED = 1000;
   int RECORD_STREAM_DONE = 6148;
   int RECORD_STREAM_FAIL = 6150;

   void recordingDone(int var1);

   void streamingDone(int var1);
}

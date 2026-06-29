package net.rim.device.internal.media;

import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.internal.system.AudioInternalListener;

class MediaStreamingManagerImpl$MSMListener implements AudioInternalListener, GlobalEventListener {
   private final MediaStreamingManagerImpl this$0;

   MediaStreamingManagerImpl$MSMListener(MediaStreamingManagerImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -1270659756336956134L) {
         for (int index = this.this$0._sessions.length - 1; index >= 0; index--) {
            MediaStreamingManagerImpl$StreamingSessionImpl session = this.this$0._sessions[index];
            synchronized (session) {
               if (session._thread != null && !session._thread.isAlive()) {
                  session._callback = null;
                  session._thread = null;
                  session._threadApplication = null;
                  session._ringBuffer = null;
                  session._mediaPlayer = null;
                  session._codec = Integer.MAX_VALUE;
               }
            }
         }
      }
   }

   @Override
   public void recordStreamDone(int handle, int headerLength) {
      MediaStreamingManagerImpl$StreamingSessionImpl session = this.this$0.getStreamingSession(handle);
      if (session != null) {
         session.recordStreamDone(headerLength);
      }
   }

   @Override
   public void recordStreamFail(int handle) {
      MediaStreamingManagerImpl$StreamingSessionImpl session = this.this$0.getStreamingSession(handle);
      if (session != null) {
         session.recordStreamFail();
      }
   }

   @Override
   public void responseAVCModeChange(boolean success, int mode) {
   }

   @Override
   public void micStatusChange(boolean micEnabled) {
   }

   @Override
   public void dtmfDataAvailable() {
   }

   @Override
   public void dtmfDataBufferFull() {
   }
}

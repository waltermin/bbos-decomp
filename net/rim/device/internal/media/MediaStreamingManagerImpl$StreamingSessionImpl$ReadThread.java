package net.rim.device.internal.media;

import net.rim.device.internal.util.RingBuffer;

class MediaStreamingManagerImpl$StreamingSessionImpl$ReadThread extends Thread {
   private final Object _lock;
   private boolean _running;
   private int _state;
   private int _watermark;
   private int _spaceAvailable;
   private boolean _dataDesired;
   private boolean _streamingDone;
   private int _streamingDoneReason;
   private int _headerLength;
   private boolean _waitingForMessage;
   private boolean _allDataReceived;
   private boolean _sourceEnded;
   private final MediaStreamingManagerImpl$StreamingSessionImpl this$1;

   MediaStreamingManagerImpl$StreamingSessionImpl$ReadThread(MediaStreamingManagerImpl$StreamingSessionImpl _1) {
      this.this$1 = _1;
      this._lock = this.this$1;
      this._running = true;
      this._streamingDoneReason = -1;
      this.setPriority(4);
   }

   public void clear() {
      synchronized (this._lock) {
         while (!this._waitingForMessage && this._running && this._state < 4 && this.isAlive()) {
            try {
               this._lock.wait(50);
            } finally {
               continue;
            }
         }

         if (this._state != 4 && this.isAlive()) {
            this._dataDesired = false;
            this._allDataReceived = false;
            this._streamingDone = false;
            this._watermark = 0;
            this._state = 2;
         }
      }
   }

   public void init() {
      synchronized (this._lock) {
         if (this._state != 0 && this._state != 4) {
            this.this$1.stop();
         }

         long time = System.currentTimeMillis();

         while (this._state != 0 && this.this$1._ringBuffer != null && System.currentTimeMillis() - time < 500) {
            try {
               this._lock.notify();
               this._lock.wait(50);
            } finally {
               continue;
            }
         }

         this.notifyDone(this.this$1._callback);
         this._dataDesired = false;
         this._allDataReceived = false;
         this._streamingDone = false;
         this._watermark = 0;
         this._headerLength = 0;
         this._state = 1;
         this._streamingDoneReason = -1;
         this._sourceEnded = false;
      }
   }

   void notifyDone(MediaStreamingCallback callback) {
      if (callback != null) {
         if (!this.this$1._recording) {
            callback.streamingDone(this._streamingDoneReason);
         } else {
            callback.recordingDone(this._streamingDoneReason, this._headerLength);
         }

         this.this$1._callback = null;
         this.this$1.this$0.notifyListenersTuneDone(this.this$1._doneReason);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      while (this._running) {
         RingBuffer ringBuffer = this.this$1._ringBuffer;
         boolean moreToDo = false;
         boolean callbackMoreData = false;
         boolean callbackSentAllData = false;
         synchronized (this._lock) {
            if (this._state == 2 && this._dataDesired) {
               boolean var14 = false /* VF: Semaphore variable */;

               label259:
               try {
                  var14 = true;
                  if (!this._sourceEnded && ringBuffer != null) {
                     int written = ringBuffer.read(this.this$1._outputStream, this._spaceAvailable);
                     if (written != this._spaceAvailable && !ringBuffer.isEmpty()) {
                        this._dataDesired = false;
                     }

                     this._spaceAvailable -= written;
                     if (this._spaceAvailable == 0 || written == 0) {
                        this._dataDesired = false;
                     }

                     if (!ringBuffer.isFull()) {
                        callbackMoreData = true;
                        var14 = false;
                     } else {
                        var14 = false;
                     }
                  } else {
                     this._dataDesired = false;
                     var14 = false;
                  }
               } finally {
                  if (var14) {
                     System.out.println("AUDIOMANAGER: IOException");
                     this._dataDesired = false;
                     if (this.this$1._mediaPlayer == null) {
                        this.this$1.stop();
                     }
                     break label259;
                  }
               }

               moreToDo = true;
            } else if (this._watermark != 0) {
               this._watermark--;
               moreToDo = true;
               if (this._state == 2) {
                  this._dataDesired = true;
               } else if (this._state == 3) {
                  callbackSentAllData = true;
                  this._state = 1;
               }
            }

            if (this._state == 2 && this._allDataReceived && this._dataDesired && !this._streamingDone && ringBuffer != null && ringBuffer.isEmpty()) {
               label240:
               try {
                  if (!this._sourceEnded) {
                     this.this$1._outputStream.write(new byte[0], 0, 0);
                  }
               } finally {
                  break label240;
               }

               this._dataDesired = false;
               this._watermark = 0;
               this._state = 3;
            }
         }

         MediaStreamingCallback callback = this.this$1._callback;
         if (callback != null) {
            if (callbackMoreData) {
               this._allDataReceived = !callback.moreData();
            }

            if (callbackSentAllData) {
               callback.streamingSentAllData();
            }

            if (this._streamingDone) {
               this._state = 0;
               this.notifyDone(callback);
            }
         } else if (this._state != 0) {
            System.out
               .println(
                  ((StringBuffer)(new Object("AUDIOMANAGER: Stream ")))
                     .append(this.this$1._session)
                     .append(" callback==null and state==")
                     .append(this._state)
                     .toString()
               );
            this._state = 0;
         }

         this.waitForMessage(moreToDo);
      }

      MediaStreamingCallback callback = this.this$1._callback;
      if (callback != null) {
         this._streamingDoneReason = 1000;
         this.notifyDone(callback);
      }
   }

   public void sendRecordingDone(int streamingDoneReason, int headerLength) {
      synchronized (this._lock) {
         if (!this.this$1._recording) {
            throw new Object("sendRecordingDone: not recording");
         }

         if (this._state != 0 && this._state != 4) {
            this._state = 4;
            this._streamingDone = true;
            this._streamingDoneReason = streamingDoneReason;
            this._headerLength = headerLength;
            this._lock.notify();
         } else {
            throw new Object("sendStreamingDone: STATE_UNINITIALIZED");
         }
      }
   }

   public void sendSourceEnded() {
      synchronized (this._lock) {
         if (this._sourceEnded) {
            throw new Object("sendSourceEnded: sendSourceEnded already ended");
         }

         this._sourceEnded = true;
      }
   }

   public void sendStreamingDone(int streamingDoneReason) {
      synchronized (this._lock) {
         if (this.this$1._recording) {
            throw new Object("sendStreamingDone: recording");
         }

         this._state = 4;
         this._streamingDone = true;
         this._streamingDoneReason = streamingDoneReason;
         this._lock.notify();
      }
   }

   public void sendWatermark(int spaceAvailable) {
      synchronized (this._lock) {
         if (!this._streamingDone) {
            this._watermark++;
            this._spaceAvailable = spaceAvailable;
            this._lock.notify();
         }
      }
   }

   public void stopSelf() {
      boolean running = false;
      synchronized (this._lock) {
         if (this._running && this.isAlive()) {
            running = true;
         }

         this._running = false;
         this._lock.notifyAll();
      }

      if (running) {
         long mills = System.currentTimeMillis();

         while (this.isAlive() && System.currentTimeMillis() - mills < 500) {
            try {
               Thread.sleep(10);
            } finally {
               continue;
            }
         }
      }
   }

   void waitForMessage(boolean moreToDo) {
      synchronized (this._lock) {
         if (!moreToDo) {
            this._waitingForMessage = true;

            label32:
            try {
               this._lock.notifyAll();
               this._lock.wait();
            } finally {
               break label32;
            }

            this._waitingForMessage = false;
         }
      }
   }
}

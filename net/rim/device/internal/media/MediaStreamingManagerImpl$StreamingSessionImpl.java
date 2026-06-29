package net.rim.device.internal.media;

import net.rim.device.api.system.Application;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.streamingnatives.StreamingNatives;
import net.rim.device.internal.streamingnatives.StreamingNativesListener;
import net.rim.device.internal.util.RingBuffer;

public class MediaStreamingManagerImpl$StreamingSessionImpl extends MediaStreamingManager$StreamingSession implements StreamingNativesListener {
   private int _bufferSize;
   private int _channel;
   private RingBuffer _ringBuffer;
   private final MediaStreamingManagerImpl$OSOutputStream _outputStream;
   private final MediaStreamingManagerImpl$OSInputStream _inputStream;
   private int _session;
   private int _codec;
   private int _doneReason;
   private boolean _preemptedStop;
   private boolean _recording;
   private int _headerSize;
   private boolean _reserved;
   private MediaStreamingManagerImpl$StreamingSessionImpl$ReadThread _thread;
   private Application _threadApplication;
   private MediaStreamingCallback _callback;
   private MediaPlayer _mediaPlayer;
   private final MediaStreamingManagerImpl this$0;
   private static final int CODEC_NOT_IN_USE = Integer.MAX_VALUE;
   private static final int STATE_UNINITIALIZED = 0;
   private static final int STATE_WAITING = 1;
   private static final int STATE_STREAMING = 2;
   private static final int STATE_ALL_DATA_SENT = 3;
   private static final int STATE_CLOSING = 4;

   MediaStreamingManagerImpl$StreamingSessionImpl$ReadThread createReadThread() {
      return new MediaStreamingManagerImpl$StreamingSessionImpl$ReadThread(this);
   }

   int endSink() {
      return StreamingNatives.endSink(this._session);
   }

   synchronized boolean endSource() {
      if (this._thread != null) {
         this._thread.sendSourceEnded();
      }

      if (this._callback != null && this._callback.sessionSourceEnded()) {
         return true;
      }

      StreamingNatives.endSource(this._session);
      return false;
   }

   public synchronized void reserve(int codec) {
      this._reserved = true;
      this._codec = codec;
   }

   public synchronized int playStream(int interruptable, int volume) {
      int result = this.this$0.startSource(this._session);
      if (result != 0) {
         this.endSource();
         return -1;
      }

      result = this.this$0.playStreamSession(this._session, this._codec, this._channel, -1, interruptable);
      if (result == 0) {
         return this._session;
      }

      this.endSource();
      this._codec = Integer.MAX_VALUE;
      return -1;
   }

   synchronized void initializeStream(MediaStreamingCallback callback, RingBuffer buffer, int codec, int volume) {
      if (this._ringBuffer != null) {
         this.stop();
      }

      this.initializeReadThread();
      this._callback = callback;
      this._preemptedStop = true;
      this._recording = false;
      this.setVolume(volume);
      this._ringBuffer = buffer;
      this._codec = codec;
      if (this._mediaPlayer != null) {
         label30:
         try {
            this._mediaPlayer.unload();
         } finally {
            break label30;
         }

         if (this._mediaPlayer != null) {
            throw new IllegalStateException("AUDIOMANAGER: can't kill old MediaPlayer");
         }
      }
   }

   synchronized void initializeStreamForRecord(MediaStreamingCallback callback, RingBuffer buffer, int codec, int volume) {
      if (this._ringBuffer != null) {
         this.stop();
      }

      this.initializeReadThread();
      this._callback = callback;
      this._preemptedStop = true;
      this._recording = true;
      this.setVolume(volume);
      if (buffer != null) {
         int result = this.this$0.startSink(this._session);
         if (result == 3) {
            this.endSource();
            throw new IllegalStateException("STREAMING_ERROR_SESSION_NOT_OPENED");
         }
      }

      this._ringBuffer = buffer;
      this._codec = codec;
      if (this._mediaPlayer != null) {
         label37:
         try {
            this._mediaPlayer.unload();
         } finally {
            break label37;
         }

         if (this._mediaPlayer != null) {
            throw new IllegalStateException("AUDIOMANAGER: can't kill old MediaPlayer");
         }
      }
   }

   public synchronized int recordStreamingSession() {
      int result = MediaNatives.recordStart(this._session, this._codec);
      if (result == 0) {
         return this._session;
      }

      this.endSource();
      this._codec = Integer.MAX_VALUE;
      return -1;
   }

   public boolean isReserved() {
      return this._reserved;
   }

   public int getSessionHandle() {
      return this._session;
   }

   public int getCodec() {
      return this._codec;
   }

   public int getBufferSize() {
      return this._bufferSize;
   }

   StringBuffer appendDebugInfo(StringBuffer buffer, char separator) {
      String appId = MediaLogger.getApplicationId(this._threadApplication);
      return buffer.append(appId).append(separator).append(this._channel).append(separator).append(MediaStreamingManagerImpl.codecToString(this._codec));
   }

   public void recordStreamDone(int headerLength) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public synchronized void recordStreamFail() {
      this._headerSize = 0;
      if (this._thread != null) {
         this._thread.sendRecordingDone(6150, 0);
      }
   }

   @Override
   public synchronized void streamErrorFromSource(int session, int errorCode) {
      if (session == this._session) {
      }
   }

   @Override
   public synchronized void streamErrorFromSink(int session, int errorCode) {
      if (session == this._session && this._ringBuffer != null) {
         this.endSource();
         this._doneReason = 1;
         this._thread.sendStreamingDone(this._doneReason);
         this._codec = Integer.MAX_VALUE;
      }
   }

   @Override
   public synchronized void streamSessionClosed(int session) {
      if (session == this._session) {
      }
   }

   @Override
   public synchronized void streamLostData(int session, int bytesLost) {
      if (session == this._session) {
      }
   }

   @Override
   public synchronized void streamSinkDone(int session) {
      if (session == this._session) {
         if (this._doneReason == 1 && this._ringBuffer != null) {
            this.endSource();
         }

         this._codec = Integer.MAX_VALUE;
         if (this._ringBuffer != null) {
            RingBuffer _tmpBuffer = this._ringBuffer;
            this._ringBuffer = null;
            new MediaStreamingManagerImpl$StreamingSessionImpl$1(this, _tmpBuffer).start();
         }

         if (this._thread != null) {
            if (this._recording) {
               this._thread.sendRecordingDone(this._preemptedStop ? 1000 : 6148, this._headerSize);
            } else {
               this._thread.sendStreamingDone(this._doneReason);
            }
         }

         this._doneReason = 1;
      }
   }

   @Override
   public synchronized void streamSourceDone(int session) {
      if (session == this._session && this._ringBuffer != null && this._recording) {
         this.streamNewData(session, 0);
         this.endSink();
      }
   }

   @Override
   public synchronized void streamNewData(int session, int bytesAvailable) {
      if (session == this._session) {
         try {
            synchronized (this._inputStream) {
               if (this._ringBuffer != null) {
                  this._ringBuffer.write(this._inputStream);
               }
            }
         } finally {
            return;
         }
      }
   }

   @Override
   public synchronized void streamHitWatermark(int session, int bytesRemaining) {
      if (session == this._session) {
         int spaceAvailable = this._bufferSize - bytesRemaining;
         if (this._thread != null && spaceAvailable > 0) {
            this._thread.sendWatermark(spaceAvailable);
         }
      }
   }

   @Override
   public int setVolume(int volume) {
      return MediaNatives.setChannelVolume(this._channel, volume);
   }

   private synchronized void initializeReadThread() {
      if (this._threadApplication != Application.getApplication()) {
         if (this._thread != null) {
            this._thread.stopSelf();
         }

         this._threadApplication = Application.getApplication();
         this._thread = this.createReadThread();
         this._thread.start();
      } else {
         this._thread.init();
      }

      this._thread.clear();
   }

   @Override
   public synchronized void clear() {
      if (this._thread != null) {
         this._thread.clear();
      }
   }

   @Override
   public int getChannel() {
      return this._channel;
   }

   @Override
   public synchronized void clearCallback() {
      this._callback = null;
   }

   @Override
   public int recordPause() {
      return MediaNatives.recordPause();
   }

   @Override
   public int recordResume() {
      return MediaNatives.recordResume();
   }

   @Override
   public int getVolume() {
      return MediaNatives.getChannelVolume(this._channel);
   }

   @Override
   public int getHandle() {
      return this._session;
   }

   @Override
   public void flushBuffer() {
   }

   @Override
   public synchronized int stop() {
      if (this._thread == null || this._thread._state != 0 && this._thread._state != 4) {
         if (!this._recording) {
            if (this._codec >= Integer.MAX_VALUE) {
               this._preemptedStop = false;
               return 1;
            }

            this._doneReason = this._preemptedStop && this._doneReason != 3 ? 1000 : 3;
            this._preemptedStop = false;

            try {
               if (this._thread != null && !this._thread._sourceEnded && !this.endSource()) {
                  this.this$0.stopStreamSession(this._channel, true);
                  this._thread.sendStreamingDone(this._doneReason);
                  return 0;
               }
            } finally {
               return 0;
            }

            return 0;
         } else {
            if (this._codec >= Integer.MAX_VALUE) {
               this._preemptedStop = false;
               return 1;
            }

            MediaNatives.recordStop();
            if (this._ringBuffer != null) {
               label125:
               try {
                  if (this._thread != null && !this._thread._sourceEnded) {
                     this.endSource();
                  }
               } finally {
                  break label125;
               }
            }

            this._preemptedStop = false;
            this._doneReason = 3;
            return 0;
         }
      } else {
         return 1;
      }
   }

   @Override
   public synchronized void stopWhenComplete() {
      if (!this._recording && this._codec != Integer.MAX_VALUE) {
         this.endSource();
         this.this$0.stopStreamSession(this._channel, false);
         this._doneReason = 2;
      } else {
         throw new IllegalStateException();
      }
   }

   MediaStreamingManagerImpl$StreamingSessionImpl(MediaStreamingManagerImpl _1, int session, int channel, int bufferSize) {
      this.this$0 = _1;
      this._codec = Integer.MAX_VALUE;
      this._doneReason = 1;
      this._session = session;
      this._channel = channel;
      this._bufferSize = bufferSize;
      this._outputStream = new MediaStreamingManagerImpl$OSOutputStream(_1, session);
      this._inputStream = new MediaStreamingManagerImpl$OSInputStream(_1, session);
      this._headerSize = 0;
      StreamingNatives.addListener(Proxy.getInstance(), this);
   }

   @Override
   public synchronized boolean playUplinkBuffer() {
      return MediaNatives.playUplinkBuffer0(this._session);
   }

   @Override
   public synchronized void stopUplinkBuffer(boolean immediate) {
      MediaNatives.stopUplinkBuffer0(immediate);
   }

   @Override
   public synchronized void unregisterMediaPlayer(MediaPlayer mediaPlayer) {
      if (mediaPlayer != this._mediaPlayer) {
         throw new IllegalStateException("AUDIOMANAGER: unregisterMediaPlayer not registered");
      }

      this._mediaPlayer = null;
   }

   @Override
   public synchronized void registerMediaPlayer(MediaPlayer mediaPlayer) {
      if (mediaPlayer == null) {
         throw new IllegalArgumentException("AUDIOMANAGER: registerMediaPlayer null");
      }

      if (this._mediaPlayer != null) {
         throw new IllegalStateException("AUDIOMANAGER: registerMediaPlayer already registered");
      }

      this._mediaPlayer = mediaPlayer;
   }

   @Override
   public synchronized void release() {
      this._reserved = false;
      this._codec = Integer.MAX_VALUE;
   }
}

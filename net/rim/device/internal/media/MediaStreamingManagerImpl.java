package net.rim.device.internal.media;

import net.rim.device.api.system.Alert;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Audio;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.streamingnatives.StreamingNatives;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.util.RingBuffer;

public class MediaStreamingManagerImpl extends MediaStreamingManager {
   private MediaStreamingManagerImpl$MSMListener _listener = new MediaStreamingManagerImpl$MSMListener(this);
   private MediaStreamingManagerImpl$StreamingSessionImpl[] _sessions;
   private int[] _codecInstances;
   private Object[] _listeners;
   public static final int NO_SESSION = -1;
   private static final int WAVE_INDEX = 0;
   private static final int MP3_INDEX = 1;
   private static final int AMR_INDEX = 2;
   private static final int PCM_INDEX = 3;
   private static final int AAC_INDEX = 4;
   private static final int WMA_INDEX = 5;
   private static final int GSM610_INDEX = 6;
   private static final int RECORD_PCM_INDEX = 7;
   private static final int QCELP_INDEX = 8;
   private static final int EVRC_INDEX = 9;
   private static final int MAX_INDEX = 10;
   private static final boolean DEBUG = false;
   private static final int BIG_SESSION = 0;

   MediaStreamingManagerImpl() {
      int[] handles = MediaNatives.getStreamingSessionHandles();
      int[] bufferSizes = MediaNatives.getStreamingSessionBufferSizes();
      if (handles.length == bufferSizes.length && handles.length <= 2) {
         this._sessions = new MediaStreamingManagerImpl$StreamingSessionImpl[handles.length];

         for (int i = handles.length - 1; i >= 0; i--) {
            this._sessions[i] = this.createStreamingSession(handles[i], i, bufferSizes[i]);
         }

         this._codecInstances = new int[10];
         this._codecInstances[0] = MediaNatives.getStreamingDecodeInstances(0);
         this._codecInstances[1] = MediaNatives.getStreamingDecodeInstances(3);
         this._codecInstances[2] = MediaNatives.getStreamingDecodeInstances(7);
         this._codecInstances[3] = MediaNatives.getStreamingDecodeInstances(9);
         this._codecInstances[4] = MediaNatives.getStreamingDecodeInstances(10);
         this._codecInstances[5] = MediaNatives.getStreamingDecodeInstances(12);
         this._codecInstances[6] = MediaNatives.getStreamingDecodeInstances(11);
         this._codecInstances[7] = MediaNatives.getStreamingDecodeInstances(9);
         this._codecInstances[8] = MediaNatives.getStreamingDecodeInstances(13);
         this._codecInstances[9] = MediaNatives.getStreamingDecodeInstances(14);
         Audio.addListener(Proxy.getInstance(), this._listener);
         Proxy.getInstance().addGlobalEventListener(this._listener);
      } else {
         throw new Object("Number of stream sessions, buffers and channels do not match!");
      }
   }

   void attach() {
      for (int index = this._sessions.length - 1; index >= 0; index--) {
         StreamingNatives.addListener(Proxy.getInstance(), this._sessions[index]);
      }
   }

   protected MediaStreamingManagerImpl$StreamingSessionImpl createStreamingSession(int handle, int index, int bufferSize) {
      return new MediaStreamingManagerImpl$StreamingSessionImpl(this, handle, index, bufferSize);
   }

   void detach() {
      for (int index = this._sessions.length - 1; index >= 0; index--) {
         StreamingNatives.removeListener(Proxy.getInstance(), this._sessions[index]);
      }
   }

   @Override
   public void addListener(MediaStreamingListener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   public static void init() {
      if (InternalServices.isSoftwareCapable(2)) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         MediaStreamingManagerImpl instance = new MediaStreamingManagerImpl();
         ar.put(8461041122205944746L, instance);
      }
   }

   private void notifyListenersTuneDone(int reason) {
      Object[] listeners = this._listeners;
      if (listeners != null) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            ((MediaStreamingListener)listeners[i]).streamingDone(reason);
         }
      }
   }

   private MediaStreamingManagerImpl$StreamingSessionImpl getStreamingSession(int handle) {
      if (handle >= this._sessions[0].getSessionHandle() && handle <= this._sessions[this._sessions.length - 1].getSessionHandle()) {
         int index = this.getSessionIndex(handle);
         if (index != -1) {
            return this._sessions[index];
         }
      }

      return null;
   }

   @Override
   public synchronized MediaStreamingManager$StreamingSession playStream(
      MediaStreamingCallback callback, RingBuffer buffer, int codec, int size, int interruptable, int volume
   ) {
      MediaStreamingManagerImpl$StreamingSessionImpl session = this.getSessionForCodec(codec, size);
      if (session == null) {
         MediaLogger.logPlayFailed(this._sessions);
         return null;
      }

      if (Alert.isSingleSharedAudioChannel()) {
         Alert.stopMIDI();
      }

      volume = MathUtilities.clamp(0, volume, 100);
      session.initializeStream(callback, buffer, codec, volume);
      if (session.playStream(interruptable, volume) == -1) {
         MediaLogger.logPlayFailed(session);
         session = null;
      } else {
         MediaLogger.logPlayStream(session);
      }

      return session;
   }

   @Override
   public synchronized MediaStreamingManager$StreamingSession recordStream(MediaStreamingCallback callback, RingBuffer buffer, int codec, int volume) {
      MediaStreamingManagerImpl$StreamingSessionImpl session = this.getSessionForCodec(codec, -1);
      if (session == null) {
         MediaLogger.logRecordFailed(this._sessions);
         return null;
      }

      volume = MathUtilities.clamp(0, volume, 100);
      session.initializeStreamForRecord(callback, buffer, codec, volume);
      if (session.recordStreamingSession() == -1) {
         MediaLogger.logRecordFailed(session);
         session = null;
      } else {
         MediaLogger.logRecordStream(session);
      }

      return session;
   }

   @Override
   public synchronized MediaStreamingManager$StreamingSession openStream(
      MediaStreamingCallback callback, RingBuffer buffer, int codec, int size, int interruptable, boolean bigSession
   ) {
      MediaStreamingManagerImpl$StreamingSessionImpl session = null;
      if (bigSession && size > 262144) {
         session = this.preemptBigSession(codec);
      }

      if (session == null) {
         session = this.getSessionForCodec(codec, size);
         if (session == null) {
            MediaLogger.logOpenStreamFailed(this._sessions);
            return null;
         }
      }

      session.initializeStream(callback, buffer, codec, 0);
      MediaLogger.logOpenStream(session);
      return session;
   }

   @Override
   public synchronized MediaStreamingManager$StreamingSession reserveSession(int codec, int size) {
      MediaStreamingManagerImpl$StreamingSessionImpl session = null;
      session = this.getSessionForCodec(codec, size);
      if (session == null) {
         MediaLogger.logReserveSessionFailed(this._sessions);
         return null;
      } else {
         session.reserve(codec);
         MediaLogger.logReserveSession(session);
         return session;
      }
   }

   private void clearDeadSessions() {
      for (int index = this._sessions.length - 1; index >= 0; index--) {
         MediaStreamingManagerImpl$StreamingSessionImpl tempSession = this._sessions[index];
         synchronized (tempSession) {
            if (tempSession.getCodec() != Integer.MAX_VALUE && (tempSession._thread == null || !tempSession._thread.isAlive())) {
               tempSession.streamErrorFromSink(tempSession.getHandle(), 1000);
               MediaLogger.logClearDeadSession(tempSession);
               tempSession._codec = Integer.MAX_VALUE;
            }
         }
      }
   }

   private boolean isCodecInstanceAvailable(int codec) {
      this.clearDeadSessions();
      int totalInstances = 0;
      switch (codec) {
         case 0:
            totalInstances = this._codecInstances[0];
            break;
         case 3:
            totalInstances = this._codecInstances[1];
            break;
         case 7:
            totalInstances = this._codecInstances[2];
            break;
         case 9:
            totalInstances = this._codecInstances[3];
            break;
         case 10:
            totalInstances = this._codecInstances[4];
            break;
         case 11:
            totalInstances = this._codecInstances[6];
            break;
         case 12:
            totalInstances = this._codecInstances[5];
            break;
         case 13:
            totalInstances = this._codecInstances[8];
            break;
         case 14:
            totalInstances = this._codecInstances[9];
            break;
         case 1000:
            totalInstances = 1;
            break;
         default:
            return false;
      }

      int usedInstances = 0;
      if (totalInstances > 0) {
         for (int i = this._sessions.length - 1; i >= 0; i--) {
            if (this._sessions[i].getCodec() == codec) {
               usedInstances++;
            }
         }

         if (totalInstances > usedInstances) {
            return true;
         }
      }

      return false;
   }

   private MediaStreamingManagerImpl$StreamingSessionImpl getUnusedSession(int codec, int size) {
      MediaStreamingManagerImpl$StreamingSessionImpl session = null;
      int smallestSize = this.getSmallestSizeForCodec(codec, size);
      if (smallestSize == -1) {
         return null;
      }

      int previousSmallestSize = Integer.MAX_VALUE;

      for (int i = this._sessions.length - 1; i >= 0; i--) {
         int bufferSize = this._sessions[i].getBufferSize();
         if (this._sessions[i].getCodec() == Integer.MAX_VALUE && bufferSize >= smallestSize && bufferSize <= previousSmallestSize) {
            session = this._sessions[i];
            previousSmallestSize = bufferSize;
         }
      }

      return session;
   }

   private MediaStreamingManagerImpl$StreamingSessionImpl preemptBigSession(int codec) {
      if (codec != 10 && codec != 3 && codec != 0 && codec != 12) {
         return null;
      }

      if (this._sessions[0] != null && !this._sessions[0].isReserved()) {
         MediaStreamingManagerImpl$StreamingSessionImpl session = null;
         boolean available = this.isCodecInstanceAvailable(codec);
         if (!available) {
            if (this._sessions[0].getCodec() == codec && !this._sessions[0].isReserved()) {
               session = this._sessions[0];
            }

            for (int i = this._sessions.length - 1; session == null && i >= 0; i--) {
               if (this._sessions[i].getCodec() == codec && !this._sessions[i].isReserved()) {
                  session = this._sessions[i];
                  break;
               }
            }

            if (session == null) {
               return null;
            }

            session.stop();
         }

         return this._sessions[0];
      } else {
         return null;
      }
   }

   private MediaStreamingManagerImpl$StreamingSessionImpl preemptSession(int codec, int size, boolean codecInstanceAvailable) {
      MediaStreamingManagerImpl$StreamingSessionImpl session = null;

      for (int i = this._sessions.length - 1; i >= 0; i--) {
         if (this._sessions[i].getCodec() == codec && !this._sessions[i].isReserved()) {
            session = this._sessions[i];
            break;
         }
      }

      if (codecInstanceAvailable) {
         int smallestSize = this.getSmallestSizeForCodec(codec, size);
         if (smallestSize == -1) {
            return session;
         }

         int previousSmallestSize = Integer.MAX_VALUE;

         for (int i = this._sessions.length - 1; i >= 0; i--) {
            int bufferSize = this._sessions[i].getBufferSize();
            if (this._sessions[i].getCodec() != codec && !this._sessions[i].isReserved() && bufferSize >= smallestSize && bufferSize <= previousSmallestSize) {
               session = this._sessions[i];
               previousSmallestSize = bufferSize;
            }
         }
      }

      return session;
   }

   private MediaStreamingManagerImpl$StreamingSessionImpl getSessionForCodec(int codec, int size) {
      MediaStreamingManagerImpl$StreamingSessionImpl session = null;
      if (this.isCodecInstanceAvailable(codec)) {
         session = this.getUnusedSession(codec, size);
         if (session == null) {
            return this.preemptSession(codec, size, true);
         }
      } else {
         session = this.preemptSession(codec, size, false);
      }

      return session;
   }

   private int getSmallestSizeForCodec(int codec, int size) {
      boolean big = size == -1 || size > 262144;
      boolean session0InUse = this._sessions[0].getCodec() != Integer.MAX_VALUE;
      switch (codec) {
         case 0:
         case 3:
         case 10:
         case 12:
         case 13:
         case 14:
         case 1000:
            if (big && !session0InUse) {
               return 327680;
            }

            return 32768;
         case 7:
         case 11:
            return 32768;
         case 9:
            return 327680;
         default:
            return -1;
      }
   }

   private int getSessionIndex(int handle) {
      for (int i = this._sessions.length - 1; i >= 0; i--) {
         if (this._sessions[i].getSessionHandle() == handle) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public void stopSingleChannelAudio() {
      for (int i = this._sessions.length - 1; i >= 0; i--) {
         this._sessions[i].stop();
      }
   }

   static final String codecToString(int codec) {
      switch (codec) {
         case 0:
            return "WAV";
         case 3:
            return "MP3";
         case 7:
            return "AMR";
         case 9:
            return "PCM";
         case 10:
            return "AAC";
         case 11:
            return "GSM";
         case 12:
            return "WMA";
         case 13:
            return "QCP";
         case 14:
            return "EVW";
         case 1000:
            return "VID";
         case Integer.MAX_VALUE:
            return "";
         default:
            return "UKN";
      }
   }

   int playStreamSession(int handle, int codec, int channel, int length, int interruptable) {
      return MediaNatives.playStream(handle, codec, channel, length, interruptable);
   }

   int readBuffer(int session, byte[] buffer, int currentOffset, int length) {
      return StreamingNatives.readBuffer0(session, buffer, currentOffset, length);
   }

   int startSink(int session) {
      return StreamingNatives.startSink(session);
   }

   int startSource(int session) {
      return StreamingNatives.startSource(session);
   }

   int stopStreamSession(int channel, boolean immediate) {
      return MediaNatives.stopStream(channel, immediate);
   }

   int writeBuffer(int session, byte[] buffer, int currentOffset, int length) {
      return StreamingNatives.writeBuffer0(session, buffer, currentOffset, length);
   }
}

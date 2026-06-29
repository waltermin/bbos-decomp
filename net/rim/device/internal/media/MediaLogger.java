package net.rim.device.internal.media;

import java.util.Hashtable;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;

public final class MediaLogger {
   private static final long GUID = -6484753878348010781L;
   private static final String NAME = "ML";
   private static final char SEPARATOR = ',';
   private static final String MAKE_UNAVAILABLE = "MUN";
   private static final String PLAY_STREAM = "PLY";
   private static final String PLAY_STREAM_FAILED = "PLF";
   private static final String RECORD_STREAM = "REC";
   private static final String RECORD_STREAM_FAILED = "RCF";
   private static final String RESERVE_SESSION = "RES";
   private static final String RESERVE_SESSION_FAILED = "RVF";
   private static final String OPEN_STREAM = "OPS";
   private static final String OPEN_STREAM_FAILED = "OPF";
   private static final String CLEAR_DEAD_SESSION = "CDS";
   private static final String MEDIA_ERROR = "MDE";
   private static final String ACTIVE_MEDIA_PLAY_ERROR = "AVM";
   private static final int MAX_BUFFERS = 2;
   private static final StringBuffer[] BUFFERS = new Object[2];
   private static final Hashtable KNOWN_APPS = (Hashtable)(new Object());
   static Class class$net$rim$device$internal$media$MediaLogger;

   private MediaLogger() {
   }

   static final void logReserveSessionFailed(MediaStreamingManagerImpl$StreamingSessionImpl[] sessions) {
      logFailureMessage("RVF", sessions);
   }

   static final void logPlayFailed(MediaStreamingManagerImpl$StreamingSessionImpl[] sessions) {
      logFailureMessage("PLF", sessions);
   }

   static final void logOpenStreamFailed(MediaStreamingManagerImpl$StreamingSessionImpl[] sessions) {
      logFailureMessage("OPF", sessions);
   }

   static final void logRecordFailed(MediaStreamingManagerImpl$StreamingSessionImpl[] sessions) {
      logFailureMessage("RCF", sessions);
   }

   static final void logActiveMediaPlayFailed(Object data) {
      logGenericFailureMessage("AVM", data.toString());
   }

   static final void logMakeUnavailable(MediaStreamingManager$StreamingSession session, int codec) {
      logDebugMessage("MUN", session, codec);
   }

   static final void logMediaError(MediaStreamingManager$StreamingSession session, int codec) {
      logDebugMessage("MDE", session, codec);
      if (session == null) {
         logDebugMessage("MUN", codec);
      } else if (!(session instanceof MediaStreamingManagerImpl$StreamingSessionImpl)) {
         if (isWarningEnabled()) {
            logWarning(((StringBuffer)(new Object("Unknown StreamingSession implementation: "))).append(session.getClass().getName()).toString());
         }
      } else {
         MediaStreamingManagerImpl$StreamingSessionImpl sImpl = (MediaStreamingManagerImpl$StreamingSessionImpl)session;
         logDebugMessage("MUN", sImpl);
      }
   }

   private static final void logDebugMessage(String operation, MediaStreamingManager$StreamingSession session, int codec) {
      if (session == null) {
         logDebugMessage(operation, codec);
      } else if (session instanceof MediaStreamingManagerImpl$StreamingSessionImpl) {
         logDebugMessage(operation, (MediaStreamingManagerImpl$StreamingSessionImpl)session);
      } else {
         if (isWarningEnabled()) {
            logWarning(((StringBuffer)(new Object("Unknown StreamingSession implementation: "))).append(session.getClass().getName()).toString());
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final void logDebugMessage(String operation, int codec) {
      if (isDebugEnabled()) {
         try {
            StringBuffer buffer = checkOutBuffer();
            boolean var8 = false /* VF: Semaphore variable */;

            try {
               var8 = true;
               buffer.append(operation)
                  .append(',')
                  .append(getApplicationId(null))
                  .append(',')
                  .append(',')
                  .append(MediaStreamingManagerImpl.codecToString(codec));
               logDebug(buffer.toString());
               var8 = false;
            } finally {
               if (var8) {
                  checkInBuffer(buffer);
               }
            }

            checkInBuffer(buffer);
         } catch (Throwable var10) {
            handleInterrupted(e);
            return;
         }
      }
   }

   static final void logPlayFailed(MediaStreamingManagerImpl$StreamingSessionImpl session) {
      logDebugMessage("PLF", session);
   }

   static final void logPlayStream(MediaStreamingManagerImpl$StreamingSessionImpl session) {
      logDebugMessage("PLY", session);
   }

   static final void logRecordFailed(MediaStreamingManagerImpl$StreamingSessionImpl session) {
      logDebugMessage("RCF", session);
   }

   static final void logRecordStream(MediaStreamingManagerImpl$StreamingSessionImpl session) {
      logDebugMessage("REC", session);
   }

   static final void logReserveSession(MediaStreamingManagerImpl$StreamingSessionImpl session) {
      logDebugMessage("RES", session);
   }

   static final void logOpenStream(MediaStreamingManagerImpl$StreamingSessionImpl session) {
      logDebugMessage("OPS", session);
   }

   static final void logClearDeadSession(MediaStreamingManagerImpl$StreamingSessionImpl session) {
      logDebugMessage("CDS", session);
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final void logDebugMessage(String operation, MediaStreamingManagerImpl$StreamingSessionImpl session) {
      if (isDebugEnabled()) {
         try {
            StringBuffer buffer = checkOutBuffer();
            boolean var8 = false /* VF: Semaphore variable */;

            try {
               var8 = true;
               buffer.append(operation).append(',');
               session.appendDebugInfo(buffer, ',');
               logDebug(buffer.toString());
               var8 = false;
            } finally {
               if (var8) {
                  checkInBuffer(buffer);
               }
            }

            checkInBuffer(buffer);
         } catch (Throwable var10) {
            handleInterrupted(e);
            return;
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final void logFailureMessage(String operation, MediaStreamingManagerImpl$StreamingSessionImpl[] sessions) {
      if (isErrorEnabled()) {
         try {
            StringBuffer buffer = checkOutBuffer();
            boolean var9 = false /* VF: Semaphore variable */;

            try {
               var9 = true;
               buffer.append(operation);

               for (int i = sessions.length - 1; i >= 0; i--) {
                  buffer.append(',');
                  sessions[i].appendDebugInfo(buffer, ',');
               }

               logError(buffer.toString());
               var9 = false;
            } finally {
               if (var9) {
                  checkInBuffer(buffer);
               }
            }

            checkInBuffer(buffer);
         } catch (Throwable var11) {
            handleInterrupted(e);
            return;
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final void logGenericFailureMessage(String operation, String data) {
      if (isErrorEnabled()) {
         try {
            StringBuffer buffer = checkOutBuffer();
            boolean var8 = false /* VF: Semaphore variable */;

            try {
               var8 = true;
               buffer.append(operation);
               buffer.append(',');
               buffer.append(data);
               logError(buffer.toString());
               var8 = false;
            } finally {
               if (var8) {
                  checkInBuffer(buffer);
               }
            }

            checkInBuffer(buffer);
         } catch (Throwable var10) {
            handleInterrupted(e);
            return;
         }
      }
   }

   private static final void handleInterrupted(InterruptedException e) {
      if (EventLogger.getMinimumLevel() >= 5) {
         e.printStackTrace();
      }
   }

   private static final synchronized StringBuffer checkOutBuffer() {
      while (true) {
         for (int i = BUFFERS.length - 1; i >= 0; i--) {
            StringBuffer buffer = BUFFERS[i];
            if (buffer != null) {
               BUFFERS[i] = null;
               return buffer;
            }
         }

         (class$net$rim$device$internal$media$MediaLogger == null
               ? (class$net$rim$device$internal$media$MediaLogger = class$("net.rim.device.internal.media.MediaLogger"))
               : class$net$rim$device$internal$media$MediaLogger)
            .wait();
      }
   }

   private static final void checkInBuffer(StringBuffer buffer) {
      synchronized (class$net$rim$device$internal$media$MediaLogger == null
         ? (class$net$rim$device$internal$media$MediaLogger = class$("net.rim.device.internal.media.MediaLogger"))
         : class$net$rim$device$internal$media$MediaLogger) {
         for (int i = BUFFERS.length - 1; i >= 0; i--) {
            if (BUFFERS[i] == null) {
               BUFFERS[i] = buffer;
               buffer.setLength(0);
               (class$net$rim$device$internal$media$MediaLogger == null
                     ? (class$net$rim$device$internal$media$MediaLogger = class$("net.rim.device.internal.media.MediaLogger"))
                     : class$net$rim$device$internal$media$MediaLogger)
                  .notify();
               return;
            }
         }
      }

      if (isWarningEnabled()) {
         logWarning("Attempt to check in unknown StringBuffer");
      }
   }

   private static final boolean isDebugEnabled() {
      return EventLogger.getMinimumLevel() >= 5;
   }

   private static final boolean isWarningEnabled() {
      return EventLogger.getMinimumLevel() >= 3;
   }

   private static final boolean isErrorEnabled() {
      return EventLogger.getMinimumLevel() >= 2;
   }

   private static final void logDebug(String message) {
      log(message, 5);
   }

   private static final void logWarning(String message) {
      log(message, 3);
   }

   private static final void logError(String message) {
      log(message, 2);
   }

   private static final void log(String message, int level) {
      EventLogger.logEvent(-6484753878348010781L, message.getBytes(), level);
      if (DeviceInfo.isSimulator()) {
         System.err.println(((StringBuffer)(new Object("ML: "))).append(message).toString());
      }
   }

   static final String getApplicationId(Application app) {
      if (app == null) {
         app = Application.getApplication();
      }

      if (app == null) {
         return "";
      }

      String appClass = app.getClass().getName();
      if (KNOWN_APPS.containsKey(appClass)) {
         return (String)KNOWN_APPS.get(appClass);
      }

      int lastDot = appClass.lastIndexOf(46);
      return lastDot == -1 ? appClass : appClass.substring(lastDot + 1);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }

   static {
      KNOWN_APPS.put("net.rim.device.apps.internal.explorer.player.PlayerApplication", "PLY");
      KNOWN_APPS.put("net.rim.device.apps.internal.camera.CameraMain", "CAM");
      KNOWN_APPS.put("net.rim.device.apps.internal.vad.VADApplication", "VAD");
      KNOWN_APPS.put("net.rim.device.apps.internal.profiles.ProfilesApp", "PRF");
      KNOWN_APPS.put("net.rim.device.internal.proxy.Proxy", "PRX");
      KNOWN_APPS.put("net.rim.device.apps.internal.voicenotesrecorder.VoiceNotesRecorderMain", "VNR");

      for (int i = BUFFERS.length - 1; i >= 0; i--) {
         BUFFERS[i] = (StringBuffer)(new Object());
      }

      EventLogger.register(-6484753878348010781L, "ML", 2);
   }
}

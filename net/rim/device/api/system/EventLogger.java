package net.rim.device.api.system;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.vm.Array;
import net.rim.vm.EventLog;
import net.rim.vm.PersistentInteger;
import net.rim.vm.TraceBack;

public final class EventLogger {
   private int _logLevelId;
   private int _logLevel;
   private byte[] _intBuffer = new byte[4];
   private byte[] _longBuffer = new byte[8];
   private byte[] _byteBuffer = new byte[18];
   public static final long EVLV_GUID;
   public static final long SYSTEM_LOG_GUID;
   public static final int DEBUG_INFO;
   public static final int INFORMATION;
   public static final int WARNING;
   public static final int ERROR;
   public static final int SEVERE_ERROR;
   public static final int ALWAYS_LOG;
   public static final int VIEWER_NUMBER;
   public static final int VIEWER_STRING;
   public static final int VIEWER_EXCEPTION;
   private static long LOG_LEVEL = 1512962004565244438L;
   private static EventLogger _theLogger = ((ApplicationManagerImpl)ApplicationManager.getApplicationManager()).getEventLogger();
   private static EventLogger$MyPersistentContentListener _myPersistentContentListener;
   public static final long PERSISTENT_CONTENT_LISTENER_ID;

   EventLogger() {
      this._logLevelId = PersistentInteger.getId(LOG_LEVEL, 3);
      this._logLevel = PersistentInteger.get(this._logLevelId);
      EventLog.registerApp(-7188635411275533160L, 3, "Java Exception");
      EventLog.registerApp(-7509200465648525729L, 2, "System");
      _theLogger = this;
   }

   private static final void assertPermission() {
      ApplicationControl.assertChangeDeviceSettingsPermitted(true, CommonResource.getBundle(), 10133);
   }

   public static final boolean register(long guid, String name, int viewerType) {
      EventLog.registerApp(guid, viewerType, name);
      return true;
   }

   public static final boolean register(long guid, String name) {
      return register(guid, name, 1);
   }

   public static final int getRegisteredViewerType(long forGUID) {
      return EventLog.getRegisteredAppEventType(forGUID);
   }

   public static final String getRegisteredAppName(long forGUID) {
      return EventLog.getRegisteredAppName(forGUID);
   }

   public static final boolean logEvent(long guid, byte[] data, int level) {
      return checkLevel(level) ? true : logEventInternal(guid, data, level);
   }

   private static final boolean logEventInternal(long guid, byte[] data, int level) {
      if (data == null) {
         return false;
      }

      EventLog.logEvent(guid, System.currentTimeMillis(), (byte)level, data);
      return true;
   }

   public static final boolean logEvent(long guid, byte[] data) {
      return logEvent(guid, data, 0);
   }

   public static final boolean logEvent(long guid, int value, int level) {
      if (checkLevel(level)) {
         return true;
      }

      synchronized (_theLogger) {
         _theLogger._intBuffer[0] = (byte)(value >> 24 & 0xFF);
         _theLogger._intBuffer[1] = (byte)(value >> 16 & 0xFF);
         _theLogger._intBuffer[2] = (byte)(value >> 8 & 0xFF);
         _theLogger._intBuffer[3] = (byte)value;
         return logEventInternal(guid, _theLogger._intBuffer, level);
      }
   }

   public static final boolean logEvent(long guid, int value, byte[] data, int level) {
      if (data == null) {
         data = new byte[0];
      }

      if (checkLevel(level)) {
         return true;
      }

      synchronized (_theLogger) {
         byte[] mergedData = new byte[data.length + 5];
         mergedData[0] = (byte)(value >> 24 & 0xFF);
         mergedData[1] = (byte)(value >> 16 & 0xFF);
         mergedData[2] = (byte)(value >> 8 & 0xFF);
         mergedData[3] = (byte)value;
         mergedData[4] = 45;
         System.arraycopy(data, 0, mergedData, 5, data.length);
         return logEventInternal(guid, mergedData, level);
      }
   }

   public static final boolean logEvent(long guid, long value, int level) {
      if (checkLevel(level)) {
         return true;
      }

      synchronized (_theLogger) {
         _theLogger._longBuffer[0] = (byte)(value >> 56 & 255);
         _theLogger._longBuffer[1] = (byte)(value >> 48 & 255);
         _theLogger._longBuffer[2] = (byte)(value >> 40 & 255);
         _theLogger._longBuffer[3] = (byte)(value >> 32 & 255);
         _theLogger._longBuffer[4] = (byte)(value >> 24 & 255);
         _theLogger._longBuffer[5] = (byte)(value >> 16 & 255);
         _theLogger._longBuffer[6] = (byte)(value >> 8 & 255);
         _theLogger._longBuffer[7] = (byte)value;
         return logEventInternal(guid, _theLogger._longBuffer, level);
      }
   }

   public static final boolean logEvent(long guid, int value, int extra, int radix, int level) {
      return logEvent(guid, value, (long)extra, radix, level);
   }

   public static final boolean logEvent(long guid, int value, long extra, int radix, int level) {
      if (checkLevel(level)) {
         return true;
      }

      synchronized (_theLogger) {
         _theLogger._byteBuffer[0] = (byte)(value >> 24 & 0xFF);
         _theLogger._byteBuffer[1] = (byte)(value >> 16 & 0xFF);
         _theLogger._byteBuffer[2] = (byte)(value >> 8 & 0xFF);
         _theLogger._byteBuffer[3] = (byte)value;
         _theLogger._byteBuffer[4] = 32;
         if (radix == 10) {
            _theLogger._byteBuffer[5] = 32;
            _theLogger._byteBuffer[6] = 32;
         } else {
            _theLogger._byteBuffer[5] = 48;
            _theLogger._byteBuffer[6] = 120;
         }

         try {
            if (radix == 16 && extra < 0) {
               if (_theLogger._byteBuffer.length != 23) {
                  Array.resize(_theLogger._byteBuffer, 23);
               }

               for (int i = 22; i >= 7; i--) {
                  byte val = (byte)(extra & 15);
                  val = (byte)(val + (val >= 10 ? 55 : 48));
                  _theLogger._byteBuffer[i] = val;
                  extra >>>= 4;
               }
            } else {
               int length = Math.max(NumberUtilities.appendNumber(7, _theLogger._byteBuffer, extra, radix), 0);
               Array.resize(_theLogger._byteBuffer, 7 + length);
            }
         } catch (Throwable t) {
            return logEvent(guid, value, level);
         }

         return logEventInternal(guid, _theLogger._byteBuffer, level);
      }
   }

   public static final boolean logEvent(long guid, int value) {
      return logEvent(guid, value, 0);
   }

   public static final void logStackTrace(long guid, String stackTraceReason) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(out);
      if (stackTraceReason != null) {
         printStream.println(stackTraceReason);
      }

      TraceBack.printStackTrace(printStream);
      printStream.flush();
      logEvent(guid, out.toByteArray());
   }

   private static final boolean checkLevel(int level) {
      return level > getMinimumLevel();
   }

   public static final int getMinimumLevel() {
      return _theLogger._logLevel > 4 && _myPersistentContentListener.isEncryptionEnabled() ? 4 : _theLogger._logLevel;
   }

   public static final void setMinimumLevel(int level) {
      assertPermission();
      synchronized (_theLogger) {
         switch (level) {
            case 2:
               throw new IllegalArgumentException();
            case 3:
            case 4:
            case 5:
            default:
               _theLogger._logLevel = level;
               PersistentInteger.set(_theLogger._logLevelId, level);
         }
      }
   }

   public static final void clearLog() {
      assertPermission();
      EventLog.clear();
   }

   public static final int getInt(byte[] data) {
      int value = (data[3] & 255) << 0;
      value += (data[2] & 255) << 8;
      value += (data[1] & 255) << 16;
      return value + ((data[0] & 0xFF) << 24);
   }

   public static final boolean startEventLogViewer() {
      try {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         ApplicationDescriptor descriptor = (ApplicationDescriptor)ar.get(-1723378108824417453L);
         if (descriptor == null) {
            return false;
         }

         ApplicationManager.getApplicationManager().runApplication(descriptor);
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   static {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      if (applicationRegistry == null) {
         _myPersistentContentListener = new EventLogger$MyPersistentContentListener(null);
      } else {
         _myPersistentContentListener = (EventLogger$MyPersistentContentListener)applicationRegistry.getOrWaitFor(-3768474813176659123L);
         if (_myPersistentContentListener == null) {
            _myPersistentContentListener = new EventLogger$MyPersistentContentListener(null);
            applicationRegistry.put(-3768474813176659123L, _myPersistentContentListener);
         }
      }
   }
}

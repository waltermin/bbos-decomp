package net.rim.device.api.lbs;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;

public final class Logger {
   LoggingFlags _loggableFlags;
   private long _guid;
   public static final int GENERAL = 2;
   public static final int MAP_SCREEN = 4;
   public static final int UI = 8;
   public static final int GPS = 16;
   public static final int PROTOCOL = 32;
   public static final int SYNC = 64;
   public static final int FINDER = 128;
   public static final int ROUTE = 256;
   public static final int POI = 512;
   private static final long GUID = -6718592059891985282L;
   private static Logger INSTANCE;

   private Logger() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-6718592059891985282L);
      synchronized (persistentObject) {
         this._loggableFlags = (LoggingFlags)persistentObject.getContents();
         if (this._loggableFlags == null) {
            this._loggableFlags = new LoggingFlags();
            persistentObject.setContents(this._loggableFlags, 51);
            persistentObject.commit();
         }
      }
   }

   static final Logger getInstance() {
      if (INSTANCE == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         INSTANCE = (Logger)ar.getOrWaitFor(-6718592059891985282L);
         if (INSTANCE == null) {
            INSTANCE = new Logger();
            ar.put(-6718592059891985282L, INSTANCE);
         }
      }

      return INSTANCE;
   }

   public static final void register(long guid, String name) {
      getInstance()._guid = guid;
      EventLogger.register(guid, name, 2);
   }

   public static final void logEvent(String message, int type) {
      Logger logger = getInstance();
      if (logger._loggableFlags.getFlag(type)) {
         EventLogger.logEvent(logger._guid, message.getBytes(), 0);
         System.out.println("LBS Logger: " + message);
      }
   }

   public static final void logError(String message) {
      Logger logger = getInstance();
      EventLogger.logEvent(logger._guid, message.getBytes(), 2);
      System.out.println("LBS Logger error: " + message);
   }

   public static final boolean openConfigScreen(int backdoorCode) {
      if (backdoorCode == 1279740999) {
         new LBSLoggingConfigScreen();
         return true;
      } else {
         return false;
      }
   }

   static final void setLogEventType(int type) {
      getInstance()._loggableFlags.setFlag(type);
      commit();
   }

   static final void commit() {
      RIMPersistentStore.getPersistentObject(-6718592059891985282L).commit();
   }
}

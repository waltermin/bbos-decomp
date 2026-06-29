package net.rim.device.api.system;

import java.util.Vector;
import net.rim.vm.DebugSupport;
import net.rim.vm.Message;
import net.rim.vm.MessageQueue;
import net.rim.vm.Process;

public final class ApplicationProcess extends Process implements Runnable {
   private Application _app;
   private ApplicationDescriptor _descriptor;
   private ApplicationManagerImpl _appManager;
   private boolean _grabForegroundOnStartup;
   private boolean _acceptsEvents = true;
   private boolean _isHandlingEvents;
   private boolean _droppedDiagnosed;
   private boolean _isRIMProcess;
   private int _lastMessageType;
   private int _lastMagnitude;
   private int _lastHandle;
   private Vector _cleanupRunnables;
   private boolean _droppingKeys;
   private static final int LAST_MESSAGE_GENERIC;
   private static final int LAST_MESSAGE_THUMB_ROLL;
   private static final int LAST_MESSAGE_STYLUS_DRAG;
   private static final int LAST_MESSAGE_TRACKBALL_ROLL;
   private static final int LAST_MESSAGE_STREAMING_SESSION_WATERMARK;
   private static final int LAST_MESSAGE_MEDIA_STATUS;

   ApplicationProcess(ApplicationManagerImpl appManager, ApplicationDescriptor descriptor, boolean grabForegroundOnStartup) {
      this._appManager = appManager;
      this._descriptor = descriptor;
      this._grabForegroundOnStartup = grabForegroundOnStartup;
      this._isRIMProcess = ControlledAccess.verifyRRISignature(descriptor.getModuleHandle());
   }

   final ApplicationManagerImpl getApplicationManager() {
      return this._appManager;
   }

   final ApplicationDescriptor getApplicationDescriptor() {
      return this._descriptor;
   }

   final boolean grabForegroundOnStartup() {
      return this._grabForegroundOnStartup;
   }

   final boolean isRIMProcess() {
      return this._isRIMProcess;
   }

   public final synchronized void addCleanupRunnable(Runnable runnable) {
      if (this._cleanupRunnables == null) {
         this._cleanupRunnables = new Vector();
      }

      this._cleanupRunnables.addElement(runnable);
   }

   public final synchronized void removeCleanupRunnable(Runnable runnable) {
      if (this._cleanupRunnables != null) {
         this._cleanupRunnables.removeElement(runnable);
         if (this._cleanupRunnables.size() == 0) {
            this._cleanupRunnables = null;
         }
      }
   }

   final void appStarted(Application app) {
      if (this._app == null) {
         this._app = app;
      } else {
         throw new RuntimeException("application already running in this process");
      }
   }

   public final Application getApplication() {
      return this._app;
   }

   final void setAcceptsEvents(boolean on) {
      if (this.isAlive()) {
         synchronized (super._messageQueue) {
            if (!on) {
               super._messageQueue.flush();
            }

            this._acceptsEvents = on;
         }
      }
   }

   final void postMessage(Message message) {
      int device = message.getDevice();
      switch (device) {
         default:
            if (this._isHandlingEvents && this._app._listeners[device] == null) {
               return;
            }
         case 0:
         case 2:
         case 4:
         case 26:
         case 27:
         case 32:
         case 49:
         case 56:
            synchronized (super._messageQueue) {
               if (this._acceptsEvents) {
                  switch (device) {
                     case 2:
                        if (this.checkForKeyOverflow()) {
                           return;
                        }

                        if (message.getEvent() == 519) {
                           if (this._lastMessageType == 1 && super._messageQueue.getSize() != 0) {
                              this._lastMagnitude = this._lastMagnitude + message.getSubMessage();
                              message.setSubMessage(this._lastMagnitude);
                              super._messageQueue.replaceTail(message);
                              return;
                           }

                           this._lastMessageType = 1;
                           this._lastMagnitude = message.getSubMessage();
                        } else {
                           this._lastMessageType = 0;
                        }
                        break;
                     case 13:
                        if (message.getEvent() == 3329) {
                           int mediaHandle = message.getSubMessage();
                           if (this._lastMessageType == 4 && super._messageQueue.getSize() != 0 && this._lastHandle == mediaHandle) {
                              super._messageQueue.replaceTail(message);
                              return;
                           }

                           this._lastMessageType = 4;
                           this._lastHandle = mediaHandle;
                        } else {
                           this._lastMessageType = 0;
                        }
                        break;
                     case 20:
                        if (message.getEvent() == 8197) {
                           int mediaHandle = message.getData0();
                           if (this._lastMessageType == 5 && super._messageQueue.getSize() != 0 && this._lastHandle == mediaHandle) {
                              super._messageQueue.replaceTail(message);
                              return;
                           }

                           this._lastMessageType = 5;
                           this._lastHandle = mediaHandle;
                        } else {
                           this._lastMessageType = 0;
                        }
                        break;
                     case 26:
                        if (this.checkForKeyOverflow()) {
                           return;
                        }

                        if (message.getEvent() == 6657) {
                           if (this._lastMessageType == 2 && super._messageQueue.getSize() != 0) {
                              super._messageQueue.replaceTail(message);
                              return;
                           }

                           this._lastMessageType = 2;
                        } else {
                           this._lastMessageType = 0;
                        }
                        break;
                     case 27:
                        if (this.checkForKeyOverflow()) {
                           return;
                        }

                        if (message.getEvent() == 6913) {
                           if (this._lastMessageType == 3 && super._messageQueue.getSize() != 0) {
                              int subMessage = message.getSubMessage();
                              int newDX = this._lastMagnitude + subMessage;
                              int newDY = (this._lastMagnitude >>> 16) + (subMessage >>> 16);
                              this._lastMagnitude = newDX & 65535 | newDY << 16;
                              message.setSubMessage(this._lastMagnitude);
                              super._messageQueue.replaceTail(message);
                              return;
                           }

                           this._lastMessageType = 3;
                           this._lastMagnitude = message.getSubMessage();
                        } else {
                           this._lastMessageType = 0;
                        }
                        break;
                     case 49:
                        if (this.checkForKeyOverflow()) {
                           return;
                        }

                        this._lastMessageType = 0;
                        break;
                     case 56:
                        if (this.checkForKeyOverflow()) {
                           return;
                        }
                     default:
                        this._lastMessageType = 0;
                        break;
                     case 57:
                        if (super._messageQueue.getSize() > 150) {
                           return;
                        }

                        this._lastMessageType = 0;
                  }

                  if (!super._messageQueue.enqueue(message)) {
                     if (this._isHandlingEvents) {
                        this._acceptsEvents = false;
                        StringBuffer buf = new StringBuffer();
                        buf.append("Application ");
                        buf.append(this.toString());
                        buf.append(" is not responding; process terminated");
                        RuntimeException ex = new RuntimeException(buf.toString());
                        buf.append('\n');
                        String msg = buf.toString();
                        logMessageQueueOverflow(super._messageQueue, msg);
                        this.destroy(ex);
                        return;
                     }

                     if (!this._droppedDiagnosed) {
                        this._droppedDiagnosed = true;
                        StringBuffer buf = new StringBuffer();
                        buf.append("Process ");
                        buf.append(this.toString());
                        buf.append(" queue overflow; oldest event dropped");
                        EventLogger.logEvent(-7509200465648525729L, buf.toString().getBytes());
                     }
                  }
               }
            }
      }
   }

   static final void logMessageQueueOverflow(MessageQueue msgQueue, String msg) {
      System.out.println(msg);
      EventLogger.logEvent(-7509200465648525729L, msg.getBytes());
      msg = msgQueue.toString();
      System.out.println(msg);
      EventLogger.logEvent(-7509200465648525729L, msg.getBytes());
      DebugSupport.logStackTraces();
   }

   private final boolean checkForKeyOverflow() {
      if (super._messageQueue.getSize() > 30) {
         if (!this._droppingKeys) {
            StringBuffer buf = new StringBuffer();
            buf.append("Process ");
            buf.append(this.toString());
            buf.append(" queue too large; key/stylus event(s) dropped");
            EventLogger.logEvent(-7509200465648525729L, buf.toString().getBytes());
            this._droppingKeys = true;
         }

         return true;
      } else {
         if (this._droppingKeys) {
            this._droppingKeys = false;
         }

         return false;
      }
   }

   final void getMessage(Message message) {
      synchronized (super._messageQueue) {
         if (!this._isHandlingEvents) {
            this._isHandlingEvents = true;
            super._messageQueue.setProcessState(1);
         }

         super._messageQueue.dequeue(message, true);
      }
   }

   public final boolean acceptsForeground() {
      return this.isAlive() && this._app != null && this._app.acceptsForeground();
   }

   final int getOSTimerId(int processTimerId) {
      return this.getProcessId() & 134217727 | processTimerId << 27;
   }

   static final int getProcessTimerId(int osTimerId) {
      return osTimerId >> 27 & 31;
   }

   static final int getProcessIdFromOSTimerId(int osTimerId) {
      return osTimerId & 134217727;
   }

   final void cleanup() {
      if (this._cleanupRunnables != null) {
         new Thread(this).start();
      }
   }

   @Override
   public final void run() {
      for (int i = this._cleanupRunnables.size() - 1; i >= 0; i--) {
         Runnable runnable = (Runnable)this._cleanupRunnables.elementAt(i);

         try {
            runnable.run();
         } catch (Throwable ex) {
            StringBuffer buf = new StringBuffer();
            buf.append("Process ");
            buf.append(this.toString());
            buf.append(" cleanup failed");
            EventLogger.logEvent(-7509200465648525729L, buf.toString().getBytes());
         }
      }
   }
}

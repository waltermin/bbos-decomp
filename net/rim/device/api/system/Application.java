package net.rim.device.api.system;

import javax.microedition.io.file.FileSystemListener;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.crypto.vpn.VPNListener;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.NativeSocketListener;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.device.internal.system.CodeStore;
import net.rim.device.internal.system.EngineeringDataListener;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.Events;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.MessageListener;
import net.rim.device.internal.system.RadioStatusListenerFilter;
import net.rim.device.internal.system.UnhandledGlobalKeyListener;
import net.rim.tid.awt.im.InputContext;
import net.rim.vm.Message;
import net.rim.vm.MessageQueue;
import net.rim.vm.Process;
import net.rim.vm.TraceBack;

public class Application {
   Object[][][] _listeners;
   private ApplicationProcess _process;
   private int _processId;
   private ApplicationManagerImpl _appManager;
   private boolean _foregroundRequestInProgress;
   private boolean _ignoreKeyEvents;
   protected Thread _currentEventThread;
   private TimedRunnable[] _timedRunnables = new TimedRunnable[20];
   private int _lastTimedRunnableSlot;
   private TimedRunnable _cachedTimedRunnable;
   private Object _eventLock = new Object();
   private Message _invokeLaterMessage = new Message(0, 2);
   private Message _refreshDisplay = new Message(0, 3);
   private boolean _enableKeyUpMessages;
   private MessageListener _messageListener;
   private Object _invokeAndWaitMonitor = new Object();
   private EventDispatchManager _eventDispatchManager;
   private int[] _invokeLaterIds = new int[20];
   private int _uniqueInvokeLaterCounter;
   private static Application _application;
   private static final int NUM_TIMED_RUNNABLES = 20;
   private static final int NUM_EXTERNAL_TIMED_RUNNABLES = 16;
   private static final boolean DEBUG = false;

   protected Application() {
      this._listeners = new Object[59][][];
      this._process = (ApplicationProcess)Process.currentProcess();
      this._processId = this._process.getProcessId();
      this._appManager = this._process.getApplicationManager();
      this._eventDispatchManager = EventDispatchManager.getInstance();
      this._process.appStarted(this);
      if (this.acceptsForeground() && this._process.grabForegroundOnStartup() && this._appManager.setForegroundProcess(this._process, false)) {
         InternalServices.enableKeyUpMessages(this._enableKeyUpMessages);
         this._ignoreKeyEvents = true;
      }

      this._process.addCleanupRunnable(new Application$TimerCleanupRunnable(this));
   }

   public final void setMessageListener(MessageListener listener) {
      this._messageListener = listener;
   }

   protected boolean acceptsForeground() {
      return false;
   }

   protected boolean acceptLaunchRequest() {
      return this.acceptsForeground();
   }

   public void activate() {
   }

   public void deactivate() {
   }

   public final boolean isForeground() {
      return this._appManager.getForegroundProcessId() == this._processId;
   }

   public final boolean isForegroundRequestInProgress() {
      return this._foregroundRequestInProgress;
   }

   public final void requestBackground() {
      if (this.isForeground()) {
         this._appManager.requestForeground(this._process, true);
      }
   }

   public final void requestForeground() {
      this._appManager.requestForeground(this._process, false);
      this._foregroundRequestInProgress = true;
   }

   public final void enableKeyUpEvents(boolean enable) {
      this._enableKeyUpMessages = enable;
      synchronized (this._eventLock) {
         if (this.isForeground()) {
            InternalServices.enableKeyUpMessages(this._enableKeyUpMessages);
         }
      }
   }

   public final boolean acceptsKeyUpEvents() {
      return this._enableKeyUpMessages;
   }

   public synchronized void addListener(int device, Object listener) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (device == 57 && !(listener instanceof NativeSocketListener)) {
         throw new IllegalArgumentException();
      }

      this._listeners[device] = ListenerUtilities.addListener(this._listeners[device], listener);
   }

   public synchronized void removeListener(int device, Object listener) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._listeners[device] = ListenerUtilities.removeListener(this._listeners[device], listener);
   }

   public synchronized Object[] getListeners(int device) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      Object[] array = this._listeners[device];
      if (array == null) {
         return null;
      }

      Object[] copy = new Object[array.length];
      System.arraycopy(array, 0, copy, 0, array.length);
      return copy;
   }

   public void addKeyListener(KeyListener listener) {
      if (!ControlledAccess.verifyRRISignatures(true) && !CodeStore.isPartOfCurrentApp(TraceBack.getCallingModule(0))) {
         throw new ControlledAccessException("Unauthorized attempt to monitor key presses");
      }

      this.addListener(2, listener);
   }

   public void removeKeyListener(KeyListener listener) {
      this.removeListener(2, listener);
   }

   public void addLockedKeyListener(KeyListener listener) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this.addListener(49, listener);
   }

   public void removeLockedKeyListener(KeyListener listener) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this.removeListener(49, listener);
   }

   public void addStylusListener(StylusListener listener) {
      this.addListener(26, listener);
   }

   public void removeStylusListener(StylusListener listener) {
      this.removeListener(26, listener);
   }

   public void addTrackwheelListener(TrackwheelListener listener) {
      this.addListener(2, listener);
   }

   public void removeTrackwheelListener(TrackwheelListener listener) {
      this.removeListener(2, listener);
   }

   public void addRealtimeClockListener(RealtimeClockListener listener) {
      this.addListener(3, listener);
   }

   public void removeRealtimeClockListener(RealtimeClockListener listener) {
      this.removeListener(3, listener);
   }

   public void addSystemListener(SystemListener listener) {
      this.addListener(1, listener);
   }

   public void removeSystemListener(SystemListener listener) {
      this.removeListener(1, listener);
   }

   public void addHolsterListener(HolsterListener listener) {
      this.addListener(7, listener);
   }

   public void removeHolsterListener(HolsterListener listener) {
      this.removeListener(7, listener);
   }

   public void addRadioListener(RadioListener listener) {
      this.addRadioListener(-5, listener);
   }

   public void addRadioListener(int WAFFilter, RadioListener listener) {
      if (listener == null) {
         throw new NullPointerException();
      }

      if (listener instanceof RadioStatusListener) {
         if ((WAFFilter & 4) != 0) {
            ApplicationControl.assertWiFiPermitted(true, CommonResource.getBundle(), 10165);
         }

         RadioStatusListener rsl = (RadioStatusListener)listener;
         this.addListener(33, new RadioStatusListenerFilter(WAFFilter, rsl));
      }

      if (listener instanceof RadioPacketListener) {
         this.addListener(34, listener);
      }

      if (listener instanceof PhoneCallListener) {
         this.addListener(52, listener);
      }

      if (listener instanceof PhoneTimerListener) {
         this.addListener(53, listener);
      }

      if (listener instanceof PhoneControlListener) {
         this.addListener(54, listener);
      }

      if (listener instanceof EngineeringDataListener) {
         this.addListener(36, listener);
      }

      if (listener instanceof ModemListener) {
         this.addListener(48, listener);
      }

      if (listener instanceof DirectConnectListener) {
         this.addListener(37, listener);
      }

      if (listener instanceof WLANListenerInternal) {
         this.addListener(18, listener);
      }

      if (listener instanceof VPNListener) {
         this.addListener(51, listener);
      }

      if (listener instanceof GANStatusListener) {
         this.addListener(58, listener);
      }
   }

   public void removeRadioListener(RadioListener listener) {
      if (listener instanceof RadioStatusListener) {
         RadioStatusListener rsl = (RadioStatusListener)listener;
         this.removeListener(33, new RadioStatusListenerFilter(0, rsl));
      }

      if (listener instanceof RadioPacketListener) {
         this.removeListener(34, listener);
      }

      if (listener instanceof PhoneCallListener) {
         this.removeListener(52, listener);
      }

      if (listener instanceof PhoneTimerListener) {
         this.removeListener(53, listener);
      }

      if (listener instanceof PhoneControlListener) {
         this.removeListener(54, listener);
      }

      if (listener instanceof EngineeringDataListener) {
         this.removeListener(36, listener);
      }

      if (listener instanceof ModemListener) {
         this.removeListener(48, listener);
      }

      if (listener instanceof DirectConnectListener) {
         this.removeListener(37, listener);
      }

      if (listener instanceof WLANListenerInternal) {
         this.removeListener(18, listener);
      }

      if (listener instanceof VPNListener) {
         this.removeListener(51, listener);
      }

      if (listener instanceof GANStatusListener) {
         this.removeListener(58, listener);
      }
   }

   public void addIOPortListener(IOPortListener listener) {
      if (listener == null) {
         throw new NullPointerException();
      }

      ApplicationControl.assertLocalConnectionAllowed(true);
      if (listener instanceof USBPortListener) {
         this.addListener(14, listener);
      }
   }

   public void removeIOPortListener(IOPortListener listener) {
      this.removeListener(14, listener);
   }

   public void addFileSystemListener(FileSystemListener listener) {
      FileSystem.addFileSystemListener(listener, this, false);
   }

   public void removeFileSystemListener(FileSystemListener listener) {
      FileSystem.removeFileSystemListener(listener);
   }

   public void addFileSystemJournalListener(FileSystemJournalListener listener) {
      FileSystem.addJournalListener(listener, this, false);
   }

   public void removeFileSystemJournalListener(FileSystemJournalListener listener) {
      FileSystem.removeJournalListener(listener);
   }

   public void addGlobalEventListener(GlobalEventListener listener) {
      ApplicationControl.assertIPCAllowed(true);
      this.addListener(32, listener);
   }

   public void addGlobalEventListenerInternal(GlobalEventListener listener) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this.addListener(32, listener);
   }

   public void removeGlobalEventListener(GlobalEventListener listener) {
      this.removeListener(32, listener);
   }

   public void addPeripheralListener(PeripheralListener listener) {
   }

   public void removePeripheralListener(PeripheralListener listener) {
   }

   public void addAlertListener(AlertListener listener) {
      this.addListener(10, listener);
   }

   public void removeAlertListener(AlertListener listener) {
      this.removeListener(10, listener);
   }

   public void enterEventDispatcher() {
      try {
         synchronized (this._eventLock) {
            if (this._currentEventThread != null) {
               throw new IllegalStateException("Only one event dispatcher per process is allowed.");
            }

            Process.killProcessIfThisThreadDies(true);
            this._currentEventThread = Thread.currentThread();
            if (this.isForeground()) {
               this.activate();
               if (this._messageListener != null) {
                  this._messageListener.processMessage(this._eventLock, this._refreshDisplay, false);
               }
            }
         }

         Message message = new Message();

         while (true) {
            this.processNextMessage(message);
         }
      } finally {
         this._currentEventThread = null;
      }
   }

   public final void setAcceptEvents(boolean on) {
      this._process.setAcceptsEvents(on);
   }

   public static void setAcceptEventsForProcess(boolean on) {
      ApplicationProcess process = (ApplicationProcess)Process.currentProcess();
      process.setAcceptsEvents(on);
   }

   public boolean isAlive() {
      return this._process.isAlive();
   }

   protected void dispatchInvokeLater(Runnable runnable, Object notifier, int data0) {
      if (runnable != null) {
         runnable.run();
         if (notifier != null) {
            synchronized (notifier) {
               notifier.notifyAll();
               return;
            }
         }
      }
   }

   final void processNextMessage(Message message) {
      boolean handled = false;
      this._process.getMessage(message);
      synchronized (this._eventLock) {
         int device = message.getDevice();
         label564:
         switch (device) {
            case 0:
               switch (message.getEvent()) {
                  case 2:
                     this.dispatchInvokeLater((Runnable)message.getObject0(), message.getObject1(), message.getData0());
                     break label564;
                  case 12:
                     if (!this.isForeground()
                        && this.acceptLaunchRequest()
                        && this._appManager.setForegroundProcess(this._process, message.getSubMessage() == 1)) {
                        InternalServices.enableKeyUpMessages(this._enableKeyUpMessages);
                        this._ignoreKeyEvents = true;
                        this.activate();
                        if (this._messageListener != null) {
                           this._messageListener.processMessage(this._eventLock, message, false);
                           this._messageListener.processMessage(this._eventLock, this._refreshDisplay, false);
                        }
                     }

                     this._foregroundRequestInProgress = false;
                     return;
                  case 13:
                     InputContext.getInstance().notifyAppSwitch(false, this._processId);
                     this.deactivate();
                     if (this._messageListener != null) {
                        this._messageListener.processMessage(this._eventLock, message, false);
                     }

                     return;
                  default:
                     break label564;
               }
            case 1:
               Object[] var64 = this._listeners[1];
               int event = message.getEvent();
               if (event == 511) {
                  this.resetTimedRunnables();
               }

               if (var64 == null) {
                  break;
               }

               int subMessage = message.getSubMessage();
               Object object0 = message.getObject0();
               int dataLength = message.getDataLength();

               for (int i = var64.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchSystemEvent(event, subMessage, dataLength, object0, (SystemListener)var64[i]);
                  } catch (Throwable var34) {
                  }
               }
               break;
            case 2:
            case 56:
               int event = message.getEvent();
               if (this._ignoreKeyEvents) {
                  switch (event) {
                     case 512:
                        break;
                     case 513:
                     default:
                        this._ignoreKeyEvents = false;
                        break;
                     case 514:
                     case 515:
                        int keycode = message.getData0();
                        if (!this.allowKeyEventFromPreviousApp(event, keycode)) {
                           return;
                        }
                  }
               }

               Object[] listeners = this._listeners[2];
               if (listeners == null) {
                  break;
               }

               int subMessage = message.getSubMessage();
               int data0 = message.getData0();
               int data1 = message.getData1();
               switch (event) {
                  case 515:
                  case 518:
                     boolean isUnhandledGlobalKey = device == 56;
                     if (!isUnhandledGlobalKey && (data0 & 128) != 0 || listeners == null) {
                        break label564;
                     }

                     int i = listeners.length - 1;

                     for (; i >= 0; i--) {
                        Object o = listeners[i];
                        if (o instanceof KeyListener) {
                           KeyListener listener = (KeyListener)o;
                           if (!(listener instanceof UnhandledGlobalKeyListener) || isUnhandledGlobalKey) {
                              try {
                                 if (Events.dispatchKeyEvent(event, (char)subMessage, data0, data1, listener)) {
                                    handled = true;
                                    break label564;
                                 }
                              } catch (Throwable var39) {
                              }
                           }
                        }
                     }
                     break label564;
                  case 516:
                  case 517:
                  case 519:
                  default:
                     if ((data0 & 128) != 0 && !DeviceInfo.isInHolster()) {
                        break label564;
                     }

                     listeners = this._listeners[2];
                     if (listeners == null) {
                        break label564;
                     }

                     int i = listeners.length - 1;

                     while (true) {
                        if (i < 0) {
                           break label564;
                        }

                        Object o = listeners[i];
                        if (o instanceof TrackwheelListener) {
                           try {
                              TrackwheelListener listener = (TrackwheelListener)o;
                              if (Events.dispatchTrackwheelEvent(event, subMessage, data0, data1, listener)) {
                                 handled = true;
                                 break label564;
                              }
                           } catch (Throwable var40) {
                           }
                        }

                        i--;
                     }
               }
            case 3:
               Object[] var61 = this._listeners[3];
               if (var61 == null) {
                  break;
               }

               int event = message.getEvent();

               for (int i = var61.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchRealtimeClockEvent(event, (RealtimeClockListener)var61[i]);
                  } catch (Throwable var35) {
                  }
               }
               break;
            case 4:
               Runnable runnable = null;
               int id = ApplicationProcess.getProcessTimerId(message.getEvent());
               synchronized (this._timedRunnables) {
                  TimedRunnable tr = this._timedRunnables[id];
                  if (tr == null) {
                     break;
                  }

                  runnable = tr.getRunnable();
                  if (!tr.getRepeat()) {
                     this._cachedTimedRunnable = tr;
                     this._cachedTimedRunnable.clear();
                     this._timedRunnables[id] = null;
                     this._lastTimedRunnableSlot = id;
                  }
               }

               runnable.run();
               break;
            case 7:
               Object[] var60 = this._listeners[7];
               if (var60 == null) {
                  break;
               }

               int event = message.getEvent();

               for (int i = var60.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchHolsterEvent(event, (HolsterListener)var60[i]);
                  } catch (Throwable var33) {
                  }
               }
               break;
            case 10:
               Object[] var59 = this._listeners[10];
               if (var59 == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();

               for (int i = var59.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchAlertEvent(event, subMessage, (AlertListener)var59[i]);
                  } catch (Throwable var19) {
                  }
               }
               break;
            case 14:
               Object[] var58 = this._listeners[14];
               if (var58 == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();
               int dataLength = message.getDataLength();

               for (int i = var58.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchUSBPortEvent(event, subMessage, dataLength, (USBPortListener)var58[i]);
                  } catch (Throwable var21) {
                  }
               }
               break;
            case 18:
               Object[] var57 = this._listeners[18];
               if (var57 == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();
               int data0 = message.getData0();
               int data1 = message.getData1();

               for (int i = var57.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchWLANEvent(event, subMessage, data0, data1, (WLANListenerInternal)var57[i]);
                  } catch (Throwable var24) {
                  }
               }
               break;
            case 26:
               Object[] var56 = this._listeners[26];
               if (var56 == null) {
                  break;
               }

               int subMessage = message.getSubMessage();
               int x = subMessage & 65535;
               int y = subMessage >> 16 & 65535;
               int status = message.getData0();
               int time = message.getData1();
               int event = message.getEvent();

               for (int i = var56.length - 1; i >= 0; i--) {
                  Events.dispatchStylusEvent(event, x, y, status, time, (StylusListener)var56[i]);
               }
               break;
            case 27:
               int status = message.getData0();
               if ((status & 128) != 0) {
                  break;
               }

               Object[] var55 = this._listeners[2];
               if (var55 == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();
               int dy = -(subMessage >> 16);
               short var99;
               switch (event) {
                  case 6912:
                     var99 = 0;
                     break;
                  case 6913:
                     var99 = (short)(dy != 0 ? 519 : 0);
                     break;
                  case 6914:
                  default:
                     var99 = 516;
                     break;
                  case 6915:
                     var99 = 517;
               }

               if (var99 == 0) {
                  break;
               }

               int time = message.getData1();

               for (int i = var55.length - 1; i >= 0; i--) {
                  Object o = var55[i];
                  if (o instanceof TrackwheelListener) {
                     try {
                        TrackwheelListener listener = (TrackwheelListener)o;
                        if (Events.dispatchTrackwheelEvent(var99, dy, status, time, listener)) {
                           break label564;
                        }
                     } catch (Throwable var38) {
                     }
                  }
               }
               break;
            case 32:
               Object[] var54 = this._listeners[32];
               if (var54 == null) {
                  break;
               }

               long guid = ApplicationManagerImpl.getGlobalEventGUID(message);
               int data0 = message.getData0();
               int data1 = message.getData1();
               Object object0 = message.getObject0();
               Object object1 = message.getObject1();

               for (int i = var54.length - 1; i >= 0; i--) {
                  try {
                     ((GlobalEventListener)var54[i]).eventOccurred(guid, data0, data1, object0, object1);
                  } catch (Throwable var20) {
                  }
               }
               break;
            case 33:
               Object[] var53 = this._listeners[33];
               if (var53 == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();
               int data0 = message.getData0();
               int data1 = message.getData1();
               if (data1 == 0) {
                  data1 = -1;
               }

               for (int i = var53.length - 1; i >= 0; i--) {
                  try {
                     ((RadioStatusListenerFilter)var53[i]).dispatchEvent(event, subMessage, data0, data1);
                  } catch (Throwable var31) {
                  }
               }
               break;
            case 34:
               Object[] var52 = this._listeners[34];
               if (var52 == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();
               int data0 = message.getData0();
               int data1 = message.getData1();
               Object object0 = message.getObject0();
               Object object1 = message.getObject1();

               for (int i = var52.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchRadioPacketEvent(event, subMessage, data0, data1, object0, object1, (RadioPacketListener)var52[i]);
                  } catch (Throwable var32) {
                  }
               }
               break;
            case 36:
               Object[] var51 = this._listeners[36];
               if (var51 == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();
               int data0 = message.getData0();
               Object object0 = message.getObject0();

               for (int i = var51.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchRadioEngineeringEvent(event, subMessage, data0, object0, (EngineeringDataListener)var51[i]);
                  } catch (Throwable var27) {
                  }
               }
               break;
            case 37:
               Object[] var50 = this._listeners[37];
               if (var50 == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();
               int data0 = message.getData0();
               int data1 = message.getData1();

               for (int i = var50.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchRadioDirectConnectEvent(event, subMessage, data0, data1, (DirectConnectListener)var50[i]);
                  } catch (Throwable var25) {
                  }
               }
               break;
            case 48:
               Object[] var49 = this._listeners[48];
               if (var49 == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();

               for (int i = var49.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchRadioModemEvent(event, subMessage, (ModemListener)var49[i]);
                  } catch (Throwable var26) {
                  }
               }
               break;
            case 49:
               Object[] var48 = this._listeners[49];
               if (var48 == null) {
                  break;
               }

               int subMessage = message.getSubMessage();
               int data0 = message.getData0();
               int data1 = message.getData1();

               for (int i = var48.length - 1; i >= 0; i--) {
                  Object o = var48[i];
                  if (o instanceof KeyListener) {
                     KeyListener listener = (KeyListener)o;
                     int event = message.getEvent();

                     try {
                        if (Events.dispatchKeyEvent(event, (char)subMessage, data0, data1, listener)) {
                           handled = true;
                           break label564;
                        }
                     } catch (Throwable var37) {
                     }
                  }
               }
               break;
            case 51:
               Object[] var47 = this._listeners[51];
               if (var47 == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();
               int dataLength = message.getDataLength();
               int data0 = message.getData0();

               for (int i = var47.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchVPNEvent(event, dataLength, subMessage, data0, (VPNListener)var47[i]);
                  } catch (Throwable var23) {
                  }
               }
               break;
            case 52:
               Object[] var46 = this._listeners[52];
               if (var46 == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();
               int data0 = message.getData0();

               for (int i = var46.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchPhoneCallEvent(event, subMessage, data0, (PhoneCallListener)var46[i]);
                  } catch (Throwable var30) {
                  }
               }
               break;
            case 53:
               Object[] var45 = this._listeners[53];
               if (var45 == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();
               int data0 = message.getData0();

               for (int i = var45.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchPhoneTimerEvent(event, subMessage, data0, (PhoneTimerListener)var45[i]);
                  } catch (Throwable var29) {
                  }
               }
               break;
            case 54:
               Object[] var44 = this._listeners[54];
               if (var44 == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();
               int data0 = message.getData0();
               int data1 = message.getData1();
               Object object0 = message.getObject0();

               for (int i = var44.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchPhoneControlEvent(event, subMessage, data0, data1, object0, (PhoneControlListener)var44[i]);
                  } catch (Throwable var28) {
                  }
               }
               break;
            case 57:
               Object[] var43 = this._listeners[57];
               if (var43 != null) {
                  for (int i = var43.length - 1; i >= 0; i--) {
                     try {
                        Events.dispatchNativeSocketEvent(message, (NativeSocketListener)var43[i]);
                     } catch (Throwable var18) {
                     }
                  }
               }
               break;
            case 58:
               Object[] listeners = this._listeners[58];
               if (listeners == null) {
                  break;
               }

               int event = message.getEvent();
               int subMessage = message.getSubMessage();
               int data0 = message.getData0();
               int data1 = message.getData1();

               for (int i = listeners.length - 1; i >= 0; i--) {
                  try {
                     Events.dispatchGANEvent(event, subMessage, data0, data1, (GANStatusListener)listeners[i]);
                  } catch (Throwable var22) {
                  }
               }
               break;
            default:
               try {
                  this._eventDispatchManager.dispatch(device, message, this._listeners[device]);
               } catch (Throwable var17) {
               }
         }
      }

      if (this._messageListener != null) {
         try {
            this._messageListener.processMessage(this._eventLock, message, handled);
            return;
         } catch (Throwable var36) {
         }
      }
   }

   protected boolean allowKeyEventFromPreviousApp(int event, int keycode) {
      return false;
   }

   public static final boolean isEventDispatchThread() {
      return _application != null ? _application.isEventThread() : getApplication().isEventThread();
   }

   public boolean isEventThread() {
      return this._currentEventThread == Thread.currentThread();
   }

   public boolean hasEventThread() {
      return this._currentEventThread != null;
   }

   public final boolean isHandlingEvents() {
      return this._currentEventThread != null;
   }

   public static final Object getEventLock() {
      return getApplication().getAppEventLock();
   }

   public final Object getAppEventLock() {
      return this._eventLock;
   }

   public static final Application getApplication() {
      if (_application == null) {
         _application = ((ApplicationProcess)Process.currentProcess()).getApplication();
         if (_application == null) {
            throw new IllegalStateException("no application instance");
         }
      }

      if (!CodeStore.isPartOfCurrentApp(TraceBack.getCallingModule(3)) && !ApplicationControl.isIPCAllowed(true)) {
         throw new ControlledAccessException("Unauthorized attempt to attach to this application");
      } else {
         return _application;
      }
   }

   public final void invokeLater(Runnable runnable) {
      this.invokeLaterSpecial(runnable, 0);
   }

   public final void invokeLaterSpecial(Runnable runnable, int data0) {
      if (runnable == null) {
         throw new NullPointerException();
      }

      synchronized (this._invokeLaterMessage) {
         this._invokeLaterMessage.setObject0(runnable);
         this._invokeLaterMessage.setObject1(null);
         this._invokeLaterMessage.setData0(data0);
         this._process.postMessage(this._invokeLaterMessage);
         this._invokeLaterMessage.setObject0(null);
      }
   }

   public final int invokeLater(Runnable runnable, long time, boolean repeat) {
      return this.invokeLater(runnable, time, repeat, 16);
   }

   public final int invokeLaterInternal(Runnable runnable, long time, boolean repeat) {
      return this.invokeLater(runnable, time, repeat, 20);
   }

   private int invokeLater(Runnable runnable, long time, boolean repeat, int poolSize) {
      if (runnable == null) {
         throw new NullPointerException();
      }

      synchronized (this._timedRunnables) {
         int i = 0;

         for (int j = this._lastTimedRunnableSlot + 1; i < poolSize; j++) {
            if (j >= poolSize) {
               j = 0;
            }

            if (this._timedRunnables[j] == null) {
               if (InternalServices.setTimer(this._process.getOSTimerId(j), time, repeat)) {
                  if (this._cachedTimedRunnable == null) {
                     this._timedRunnables[j] = new TimedRunnable(runnable, time, repeat);
                  } else {
                     this._cachedTimedRunnable.reset(runnable, time, repeat);
                     this._timedRunnables[j] = this._cachedTimedRunnable;
                     this._cachedTimedRunnable = null;
                  }

                  this._uniqueInvokeLaterCounter = this._uniqueInvokeLaterCounter == Integer.MAX_VALUE ? 0 : this._uniqueInvokeLaterCounter + 1;
                  int slotId = (this._uniqueInvokeLaterCounter & 268435455) << 5 | j;
                  this._invokeLaterIds[j] = slotId;
                  return slotId;
               }

               return -1;
            }

            i++;
         }

         return -1;
      }
   }

   public final void cancelInvokeLater(int id) {
      synchronized (this._timedRunnables) {
         int slot = id & 31;
         if (slot >= 0 && slot < this._invokeLaterIds.length) {
            if (this._invokeLaterIds[slot] != id && this._invokeLaterIds[slot] != 0) {
               StringBuffer logData = new StringBuffer();
               logData.append("Trying to cancel invoke later for id: ");
               logData.append(Integer.toHexString(id));
               logData.append(" in slot: ");
               logData.append(slot);
               logData.append(" which has already run is now being used by another runnable.");
               Object tb = TraceBack.getTraceBack();
               StringBuffer stackTrace = new StringBuffer();
               int i = 0;

               while (true) {
                  String msg = TraceBack.getMessage(tb, i);
                  if (msg == null) {
                     EventLogger.logEvent(-7509200465648525729L, stackTrace.toString().trim().getBytes(), 0);
                     EventLogger.logEvent(-7509200465648525729L, logData.toString().getBytes(), 0);
                     return;
                  }

                  stackTrace.append(msg);
                  stackTrace.append("\n");
                  i++;
               }
            } else {
               this._invokeLaterIds[slot] = 0;
               if (slot >= 0 && slot < this._timedRunnables.length) {
                  if (this._timedRunnables[slot] != null) {
                     InternalServices.killTimer(this._process.getOSTimerId(slot));
                     this._cachedTimedRunnable = this._timedRunnables[slot];
                     this._cachedTimedRunnable.clear();
                     this._timedRunnables[slot] = null;
                     this._lastTimedRunnableSlot = slot;
                  }
               } else {
                  throw new IllegalArgumentException();
               }
            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   private void resetTimedRunnables() {
      synchronized (this._timedRunnables) {
         for (int i = 0; i < 20; i++) {
            if (this._timedRunnables[i] != null
               && !InternalServices.setTimer(this._process.getOSTimerId(i), this._timedRunnables[i].getTime(), this._timedRunnables[i].getRepeat())) {
               throw new RuntimeException("resetTimedRunnables() failed");
            }
         }
      }
   }

   public final void invokeAndWait(Runnable runnable) {
      if (this.isEventThread() || this._currentEventThread == null) {
         runnable.run();
      } else {
         if (runnable == null) {
            throw new NullPointerException();
         }

         synchronized (this._invokeAndWaitMonitor) {
            synchronized (this._invokeLaterMessage) {
               this._invokeLaterMessage.setObject0(runnable);
               this._invokeLaterMessage.setObject1(this._invokeLaterMessage);
               this._invokeLaterMessage.setData0(0);
               this._process.postMessage(this._invokeLaterMessage);
               this._invokeLaterMessage.setObject0(null);
               this._invokeLaterMessage.setObject1(null);

               try {
                  this._invokeLaterMessage.wait();
               } catch (InterruptedException var7) {
               }
            }
         }
      }
   }

   public final int getMessageQueueSize() {
      MessageQueue queue = this._process.getMessageQueue();
      synchronized (queue) {
         return queue.getSize();
      }
   }

   public final int getProcessId() {
      return this._processId;
   }

   public final void startModalEventThread(ModalEventThread modalEventThread) {
      if (!this.isEventThread()) {
         throw new RuntimeException("startModalEventThread called by a non-event thread");
      }

      this._currentEventThread = modalEventThread;
      modalEventThread.start();

      do {
         try {
            this.getAppEventLock().wait();
         } catch (InterruptedException var3) {
         }
      } while (!modalEventThread.isExiting());

      this._currentEventThread = Thread.currentThread();
   }
}

package net.rim.plazmic.internal.mediaengine.event;

import net.rim.device.internal.system.InternalServices;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.util.Platform;
import net.rim.plazmic.internal.mediaengine.util.SafeArray;
import net.rim.plazmic.mediaengine.MediaListener;

public class EventEngine {
   private long _time;
   private boolean _freezeTimeWhenStopped;
   private boolean _started;
   private long _startTime;
   private TimeSource _timeSource;
   private boolean _lockTime;
   private boolean _isRunning;
   private boolean _isProcessing;
   private EventHeap _heap;
   private EventHeap _processHeap;
   private EventEngine$EventsRunnable _timerRunnable;
   private EventEngine$EventsRunnable _asapRunnable;
   private EventEngine$APIRunnable _apiRunnable;
   private int _timedRunnableId = -1;
   private Platform _platform;
   private Event _event;
   private long _eventIdSource;
   private Event _processEvent;
   private SafeArray _eventPool;
   private EventSubscriptionHelper _subscription;
   public static final int ENGINE_START_EVENT;
   public static final int ENGINE_STOP_EVENT;
   public static final int ENGINE_NEW_TIME_EVENT;
   public static final int ENGINE_BEFORE_PROCESSING;
   public static final int ENGINE_AFTER_PROCESSING;
   public static final int NO_EVENTS_SENT;
   public static final int ASAP_TIME;
   private static final int ENGINE_DISPATCH_EVENT;
   private static final int INVOKE_ASAP_THRESHOLD;

   public EventEngine() {
      this._freezeTimeWhenStopped = true;
      this._subscription = new EventSubscriptionHelper();
      this._heap = new EventHeap(new EventArray());
      this._processHeap = new EventHeap(new EventArray());
      this._event = new Event();
      this._processEvent = new Event();
      this._timerRunnable = new EventEngine$EventsRunnable(this);
      this._asapRunnable = new EventEngine$EventsRunnable(this);
      this._eventPool = new SafeArray(true);
      this._platform = MediaFactory.getPlatform();
   }

   public void postEvent(Event ev, boolean inject) {
      synchronized (this._event) {
         if (this._isProcessing) {
            this._event.copy(ev);
            this._event._time = ev._time == Integer.MIN_VALUE ? this.getTime() : ev._time;
            this._event._uid = this.createEventId();
            if (inject && this._isRunning) {
               this._processHeap.put(this._event);
            } else {
               this._heap.put(this._event);
            }
         } else {
            long lastTime = this._heap.peek(this._event) ? this._event._time : Long.MAX_VALUE;
            this._event.copy(ev);
            this._event._time = ev._time == Integer.MIN_VALUE ? this.getTime() : ev._time;
            this._event._uid = this.createEventId();
            this._heap.put(this._event);
            if (this._event._time < lastTime) {
               this.schedule(this._event._time);
            }
         }

         this._event.clear();
      }
   }

   public void sendEvent(Event ev) {
      try {
         ((MediaListener)ev._listener).mediaEvent(this, ev._event, ev._eventParam, ev);
      } finally {
         return;
      }
   }

   public void cancelEvent(Event ev) {
      synchronized (this._event) {
         this._heap.remove(ev);
         this._processHeap.remove(ev);
      }
   }

   public void cancelEvent(int event, int eventParam) {
      synchronized (this._event) {
         this._heap.remove(event, eventParam);
         this._processHeap.remove(event, eventParam);
      }
   }

   public boolean eventScheduled(int event) {
      synchronized (this._event) {
         return this._heap.hasEvent(event) || this._processHeap.hasEvent(event);
      }
   }

   public boolean isEngineThread() {
      return this._platform.isPlatformThread();
   }

   private boolean isEngineThreadSafe() {
      return this.isEngineThread() || this._platform.hasPlatformThreadLock();
   }

   private void checkPlatformThread() {
      this._platform = MediaFactory.getPlatform();
      this._platform.checkPlatformThread();
   }

   public void start() {
      this.checkPlatformThread();
      if (this.isEngineThreadSafe()) {
         this.onStart();
      } else {
         this.postEngineEvent(868433339, 0);
      }
   }

   public void stop() {
      if (this.isEngineThreadSafe()) {
         this.onStop();
      } else {
         this.postEngineEvent(-1717674305, 0);
      }
   }

   public void setTime(long time) {
      this.checkPlatformThread();
      if (this.isEngineThreadSafe()) {
         this.onNewTime(time);
      } else {
         this.postEngineEvent(-1391809431, time);
      }
   }

   public void setFreezeTimeWhenStopped(boolean freeze) {
      this.checkPlatformThread();
      if (this.isEngineThreadSafe()) {
         if (freeze) {
            if (!this._isRunning && !this._freezeTimeWhenStopped) {
               this._time = this.getSourceTime() - this._startTime;
            }
         } else if (!this._isRunning && this._freezeTimeWhenStopped) {
            this._startTime = this.getSourceTime() - this._time;
         }

         this._freezeTimeWhenStopped = freeze;
      }
   }

   private void onStart() {
      this._isRunning = true;
      if (this._freezeTimeWhenStopped || !this._started) {
         this._startTime = this.getSourceTime() - this._time;
      }

      this._started = true;
      this._asapRunnable = new EventEngine$EventsRunnable(this);
      this.fireEvent(868433339, -1, null);
      this.processEvents();
   }

   private void onStop() {
      if (this._isRunning) {
         this._isRunning = false;
         this._time = this.getSourceTime() - this._startTime;
         this.fireEvent(-1717674305, -1, null);
      }
   }

   private void onNewTime(long time) {
      this._time = time;
      this._startTime = this.getSourceTime() - this._time;
      this.fireEvent(-1391809431, -1, null);
   }

   public void shutdown(boolean unconditional) {
      this.stop();
      this.cancelAllEvents();
   }

   public void cancelAllEvents() {
      synchronized (this._event) {
         this._processHeap.flush();
         this._heap.flush();
      }
   }

   public boolean isRunning() {
      return this._isRunning;
   }

   public boolean willProcessASAP() {
      return this._asapRunnable._isInQueue && this._isRunning;
   }

   public long getTime() {
      return !this._lockTime && (this._isRunning || !this._freezeTimeWhenStopped && this._started) ? this.getSourceTime() - this._startTime : this._time;
   }

   public void setTimeSource(TimeSource ts) {
      this._timeSource = ts;
   }

   public void dispatchEvents() {
      if (this.isEngineThreadSafe()) {
         this.processEvents();
      } else {
         boolean scheduled;
         synchronized (this._event) {
            scheduled = this.schedule(Integer.MIN_VALUE);
         }

         if (!scheduled) {
            this.postEngineEvent(-704554377, 0);
         }
      }
   }

   public boolean isEmpty() {
      return this._heap.getSize() == 0 && this._processHeap.getSize() == 0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void processEvents() {
      synchronized (this._processEvent) {
         this.lockTime(true);
         boolean var12 = false /* VF: Semaphore variable */;

         try {
            var12 = true;
            synchronized (this._event) {
               this._isProcessing = true;
               EventHeap swap = this._processHeap;
               this._processHeap = this._heap;
               this._heap = swap;
            }

            this.fireEvent(-979165487, -1, null);

            boolean noEventsSent;
            for (noEventsSent = true; this._processHeap.peek(this._processEvent) && this._processEvent._time <= this._time; noEventsSent = false) {
               this._processHeap.pop(this._processEvent);
               this.sendEvent(this._processEvent);
            }

            synchronized (this._event) {
               if (!this._heap.move(this._processHeap, this._processEvent)) {
                  this._processHeap.flush();
               }

               if (this._heap.peek(this._processEvent)) {
                  this.schedule(this._processEvent._time);
               } else {
                  if (this._asapRunnable._isInQueue) {
                     this._asapRunnable._isInQueue = false;
                  }

                  if (this._timedRunnableId != -1) {
                     this._platform.cancelInvokeLater(this._timedRunnableId);
                     this._timedRunnableId = -1;
                     this._timerRunnable._isInQueue = false;
                  }
               }

               this._isProcessing = false;
            }

            if (noEventsSent) {
               this.fireEvent(-1206114632, 239239, null);
            } else {
               this.fireEvent(-1206114632, -1, null);
            }

            this.lockTime(false);
            this._processEvent.clear();
            var12 = false;
         } finally {
            if (var12) {
               this.lockTime(false);
            }
         }

         this.lockTime(false);
      }
   }

   private void lockTime(boolean lock) {
      if (this._isRunning) {
         this._time = this.getSourceTime() - this._startTime;
      }

      this._lockTime = lock;
   }

   public void addListener(MediaListener l) {
      this._subscription.addListener(l);
   }

   public void addListener(int event, MediaListener listener) {
      this._subscription.addListener(event, listener);
   }

   public void addListener(int event, int eventParam, MediaListener listener) {
      this._subscription.addListener(event, eventParam, listener);
   }

   public void removeListener(MediaListener l) {
      this._subscription.removeListener(l);
   }

   private final void fireEvent(int event, int eventParam, Object data) {
      this._subscription.dispatchEvent(this, event, eventParam, data);
   }

   public Event getEventInstance() {
      Event e = null;
      synchronized (this._eventPool) {
         if (this._eventPool.count > 0) {
            e = (Event)this._eventPool.array[this._eventPool.count - 1];
            this._eventPool.remove(this._eventPool.count - 1);
         } else {
            e = new Event();
         }

         return e;
      }
   }

   public void releaseEventInstance(Event e) {
      synchronized (this._eventPool) {
         e.clear();
         this._eventPool.add(e);
      }
   }

   private boolean schedule(long time) {
      if (!this._isRunning) {
         return false;
      }

      if (this._timedRunnableId != -1) {
         this._platform.cancelInvokeLater(this._timedRunnableId);
         this._timedRunnableId = -1;
         this._timerRunnable._isInQueue = false;
      }

      if (this._asapRunnable._isInQueue) {
         return true;
      }

      long delay = time - this.getTime();
      if (delay > 500) {
         this._timedRunnableId = this._platform.invokeLater(this._timerRunnable, delay - 500);
         this._timerRunnable._isInQueue = this._timedRunnableId != -1;
         if (this._timerRunnable._isInQueue) {
            return true;
         }
      }

      this._asapRunnable._isInQueue = true;
      this._platform.invokeLater(this._asapRunnable);
      return true;
   }

   private final long getSourceTime() {
      return this._timeSource != null ? this._timeSource.getTime() : InternalServices.getUptime();
   }

   private final long createEventId() {
      long var10000 = this._eventIdSource;
      this._eventIdSource += 1;
      return var10000;
   }

   private void postEngineEvent(int event, long eventParamLong) {
      if (this._apiRunnable == null) {
         synchronized (this) {
            if (this._apiRunnable == null) {
               this._apiRunnable = new EventEngine$APIRunnable(this);
            }
         }
      }

      Event ev = this.getEventInstance();
      ev._event = event;
      ev._sender = this;
      ev._eventParamLong = eventParamLong;
      ev._uid = this.createEventId();
      boolean isInQueue;
      synchronized (this._apiRunnable) {
         isInQueue = this._apiRunnable._isInQueue;
         this._apiRunnable.postEvent(ev);
      }

      this.releaseEventInstance(ev);
      if (!isInQueue) {
         this._apiRunnable._isInQueue = true;
         this._platform.invokeLater(this._apiRunnable);
      }
   }
}

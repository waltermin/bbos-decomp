package net.rim.device.internal.ui;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.HolsterListener;
import net.rim.vm.WeakReference;

public final class AnimationThread extends Thread implements HolsterListener {
   private static Vector _waitQueue = new Vector();
   private static Application _theApplication;
   private static Thread _currentThread;
   private static boolean _shutdown;
   private static boolean _paused;

   private AnimationThread() {
      _theApplication.addHolsterListener(this);
   }

   public static final void addAnimation(Animation field) {
      synchronized (_waitQueue) {
         int size = _waitQueue.size();

         for (int i = 0; i < size; i++) {
            WeakReference ref = (WeakReference)_waitQueue.elementAt(i);
            Object obj = ref.get();
            if (obj == null) {
               _waitQueue.removeElementAt(i);
               size--;
               i--;
            } else if (obj == field) {
               return;
            }
         }

         _waitQueue.addElement(new WeakReference(field));
         _waitQueue.notify();
         if (_currentThread == null) {
            _shutdown = false;
            _theApplication = Application.getApplication();
            _currentThread = new AnimationThread();
            _currentThread.start();
         }
      }
   }

   public static final void removeAnimation(Animation field) {
      synchronized (_waitQueue) {
         int size = _waitQueue.size();

         for (int i = 0; i < size; i++) {
            WeakReference ref = (WeakReference)_waitQueue.elementAt(i);
            Object obj = ref.get();
            if (obj == null || obj == field) {
               _waitQueue.removeElementAt(i);
               size--;
               i--;
            }
         }
      }
   }

   public static final void pause() {
      synchronized (_waitQueue) {
         _paused = true;
      }
   }

   public static final void resume() {
      synchronized (_waitQueue) {
         _paused = false;
         _waitQueue.notify();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      Thread.currentThread().setPriority(1);
      boolean var29 = false /* VF: Semaphore variable */;

      try {
         var29 = true;
         long timeout = 0;

         label184:
         while (true) {
            Animation element = null;
            WeakReference ref = null;
            boolean repeat = true;
            long currentTime = System.currentTimeMillis();
            long shortestTimeout = Long.MAX_VALUE;
            int lastPos = 0;

            while (repeat) {
               shortestTimeout = Long.MAX_VALUE;
               element = null;
               ref = null;
               synchronized (_waitQueue) {
                  if (_shutdown) {
                     var29 = false;
                     break label184;
                  }

                  while (_paused) {
                     try {
                        _waitQueue.wait();
                     } catch (InterruptedException var32) {
                     }
                  }

                  int elementCount = _waitQueue.size();
                  currentTime = System.currentTimeMillis();
                  repeat = false;

                  for (int i = 0; i < elementCount && elementCount > 0; i++) {
                     int pos = (i + lastPos) % elementCount;
                     ref = (WeakReference)_waitQueue.elementAt(pos);
                     element = (Animation)ref.get();
                     if (element != null) {
                        long animationTime = element.getExecutionTime();
                        if (animationTime <= currentTime) {
                           repeat = true;
                           lastPos = i;
                           break;
                        }

                        if (shortestTimeout > animationTime) {
                           shortestTimeout = animationTime;
                        }

                        element = null;
                        ref = null;
                     } else {
                        _waitQueue.removeElementAt(pos);
                        elementCount--;
                        i--;
                        ref = null;
                     }
                  }
               }

               if (element != null && !element.animate()) {
                  _waitQueue.removeElement(ref);
               }
            }

            if (shortestTimeout == Long.MAX_VALUE) {
               timeout = 30000;
            } else {
               timeout = shortestTimeout - currentTime;
            }

            synchronized (_waitQueue) {
               if (!_shutdown && timeout > 0) {
                  try {
                     _waitQueue.wait(timeout);
                     if (_waitQueue.size() == 0) {
                        _shutdown = true;
                     }
                  } catch (InterruptedException var33) {
                  }
               }
            }
         }
      } finally {
         if (var29) {
            synchronized (_waitQueue) {
               _currentThread = null;
               _theApplication.removeHolsterListener(this);
               _shutdown = true;
            }
         }
      }

      synchronized (_waitQueue) {
         _currentThread = null;
         _theApplication.removeHolsterListener(this);
         _shutdown = true;
      }
   }

   @Override
   public final void inHolster() {
      pause();
   }

   @Override
   public final void outOfHolster() {
      resume();
   }
}

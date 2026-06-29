package net.rim.device.internal.synchronization.ota.util;

import java.util.Vector;

public class EventHandler extends Thread {
   private Vector _events = (Vector)(new Object());
   private boolean _done;

   @Override
   public void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/internal/synchronization/ota/util/EventHandler._done Z
      // 04: ifne 6d
      // 07: aconst_null
      // 08: astore 1
      // 09: aload 0
      // 0a: getfield net/rim/device/internal/synchronization/ota/util/EventHandler._events Ljava/util/Vector;
      // 0d: dup
      // 0e: astore 2
      // 0f: monitorenter
      // 10: aload 0
      // 11: getfield net/rim/device/internal/synchronization/ota/util/EventHandler._done Z
      // 14: ifne 2b
      // 17: aload 0
      // 18: getfield net/rim/device/internal/synchronization/ota/util/EventHandler._events Ljava/util/Vector;
      // 1b: invokevirtual java/util/Vector.isEmpty ()Z
      // 1e: ifeq 2b
      // 21: aload 0
      // 22: getfield net/rim/device/internal/synchronization/ota/util/EventHandler._events Ljava/util/Vector;
      // 25: invokevirtual java/lang/Object.wait ()V
      // 28: goto 10
      // 2b: aload 0
      // 2c: getfield net/rim/device/internal/synchronization/ota/util/EventHandler._done Z
      // 2f: ifeq 35
      // 32: aload 2
      // 33: monitorexit
      // 34: return
      // 35: aload 0
      // 36: getfield net/rim/device/internal/synchronization/ota/util/EventHandler._events Ljava/util/Vector;
      // 39: bipush 0
      // 3a: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 3d: checkcast net/rim/device/internal/synchronization/ota/util/Event
      // 40: astore 1
      // 41: aload 0
      // 42: getfield net/rim/device/internal/synchronization/ota/util/EventHandler._events Ljava/util/Vector;
      // 45: bipush 0
      // 46: invokevirtual java/util/Vector.removeElementAt (I)V
      // 49: aload 2
      // 4a: monitorexit
      // 4b: goto 53
      // 4e: astore 3
      // 4f: aload 2
      // 50: monitorexit
      // 51: aload 3
      // 52: athrow
      // 53: aload 0
      // 54: getfield net/rim/device/internal/synchronization/ota/util/EventHandler._done Z
      // 57: ifne 00
      // 5a: aload 1
      // 5b: ifnull 00
      // 5e: aload 1
      // 5f: invokevirtual net/rim/device/internal/synchronization/ota/util/Event.onExecute ()V
      // 62: goto 00
      // 65: astore 1
      // 66: goto 00
      // 69: astore 1
      // 6a: goto 00
      // 6d: return
      // try (10 -> 26): 40 null
      // try (27 -> 39): 40 null
      // try (40 -> 43): 40 null
      // try (3 -> 26): 53 null
      // try (27 -> 52): 53 null
      // try (3 -> 26): 55 null
      // try (27 -> 52): 55 null
   }

   public void addEvent(Event anEvent) {
      synchronized (this._events) {
         if (!this._done && anEvent.onBeforeAddingEvent(this._events)) {
            this._events.addElement(anEvent);
            this._events.notify();
         }
      }
   }

   public void kill() {
      synchronized (this._events) {
         this._done = true;
         this._events.notify();
      }
   }
}

package net.rim.wica.runtime.util;

public final class ReentrantLock {
   private Thread _owner;
   private int _count;

   public final synchronized void acquire() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokevirtual net/rim/wica/runtime/util/ReentrantLock.acquireWithoutBlocking ()Z
      // 04: ifne 1e
      // 07: aload 0
      // 08: invokevirtual java/lang/Object.wait ()V
      // 0b: goto 00
      // 0e: astore 1
      // 0f: aload 1
      // 10: invokevirtual java/lang/Throwable.printStackTrace ()V
      // 13: goto 00
      // 16: astore 1
      // 17: aload 1
      // 18: invokevirtual java/lang/Throwable.printStackTrace ()V
      // 1b: goto 00
      // 1e: return
      // try (3 -> 5): 6 null
      // try (3 -> 5): 10 null
   }

   public final synchronized void release() {
      if (!this.currentThreadHasLock()) {
         throw new Object("Thread calling release() doesn't own lock");
      }

      if (--this._count <= 0) {
         this._owner = null;
         this.notify();
      }
   }

   public final synchronized boolean acquireWithoutBlocking() {
      if (this._owner == null) {
         this._owner = Thread.currentThread();
         this._count = 1;
         return true;
      } else if (this.currentThreadHasLock()) {
         this._count++;
         return true;
      } else {
         return false;
      }
   }

   public final synchronized boolean currentThreadHasLock() {
      return this._owner == Thread.currentThread();
   }
}

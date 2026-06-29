package net.rim.device.apps.internal.docview.gui;

import net.rim.vm.Array;

final class SerialRunnableManager extends Thread {
   private Runnable[] _list = new Runnable[4];
   private int _headIndex;
   private int _tailIndex;
   private static SerialRunnableManager _runThread;

   private SerialRunnableManager() {
   }

   public static final void post(Runnable object) {
      if (object != null && _runThread == null) {
         _runThread = new SerialRunnableManager();
         _runThread.start();
      }

      _runThread.put(object);
   }

   private final synchronized void expandBuffer(int increase) {
      int oldLength = this._list.length;
      Array.resize(this._list, oldLength + increase);
      if (this._headIndex < this._tailIndex) {
         System.arraycopy(this._list, this._tailIndex, this._list, this._tailIndex + increase, oldLength - this._tailIndex);
         this._tailIndex += increase;
      }
   }

   private final synchronized void put(Runnable object) {
      if (object != null) {
         if (this._tailIndex - this._headIndex == 1 || this._tailIndex == 0 && this._headIndex == this._list.length - 1) {
            this.expandBuffer(4);
         }

         this._list[this._headIndex] = object;
         this._headIndex++;
         if (this._headIndex >= this._list.length) {
            this._headIndex = 0;
         }

         this.notify();
      }
   }

   private final synchronized Runnable get() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 1
      // 02: aload 0
      // 03: getfield net/rim/device/apps/internal/docview/gui/SerialRunnableManager._headIndex I
      // 06: aload 0
      // 07: getfield net/rim/device/apps/internal/docview/gui/SerialRunnableManager._tailIndex I
      // 0a: if_icmpne 1a
      // 0d: aload 0
      // 0e: invokevirtual java/lang/Object.wait ()V
      // 11: goto 02
      // 14: astore 2
      // 15: aconst_null
      // 16: areturn
      // 17: astore 2
      // 18: aconst_null
      // 19: areturn
      // 1a: aload 0
      // 1b: getfield net/rim/device/apps/internal/docview/gui/SerialRunnableManager._list [Ljava/lang/Runnable;
      // 1e: aload 0
      // 1f: getfield net/rim/device/apps/internal/docview/gui/SerialRunnableManager._tailIndex I
      // 22: aaload
      // 23: astore 1
      // 24: aload 0
      // 25: getfield net/rim/device/apps/internal/docview/gui/SerialRunnableManager._list [Ljava/lang/Runnable;
      // 28: aload 0
      // 29: getfield net/rim/device/apps/internal/docview/gui/SerialRunnableManager._tailIndex I
      // 2c: aconst_null
      // 2d: aastore
      // 2e: aload 0
      // 2f: aload 0
      // 30: getfield net/rim/device/apps/internal/docview/gui/SerialRunnableManager._tailIndex I
      // 33: bipush 1
      // 34: iadd
      // 35: putfield net/rim/device/apps/internal/docview/gui/SerialRunnableManager._tailIndex I
      // 38: aload 0
      // 39: getfield net/rim/device/apps/internal/docview/gui/SerialRunnableManager._tailIndex I
      // 3c: aload 0
      // 3d: getfield net/rim/device/apps/internal/docview/gui/SerialRunnableManager._list [Ljava/lang/Runnable;
      // 40: arraylength
      // 41: if_icmplt 49
      // 44: aload 0
      // 45: bipush 0
      // 46: putfield net/rim/device/apps/internal/docview/gui/SerialRunnableManager._tailIndex I
      // 49: aload 1
      // 4a: areturn
      // try (7 -> 9): 10 null
      // try (7 -> 9): 13 null
   }

   @Override
   public final void run() {
      while (true) {
         this.get().run();
      }
   }
}

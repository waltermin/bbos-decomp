package net.rim.device.apps.internal.browser.wml;

import net.rim.device.api.system.Application;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.page.PageTimer;

final class WMLTimer extends PageTimer {
   private WMLVariable _name;
   private WMLVariable _wmlValue;
   private int _id;
   private WMLContextManager _contextManager;

   WMLTimer(WMLVariable name, WMLVariable value, BrowserContentImpl browserContent, WMLContextManager contextManager) {
      super(0, browserContent);
      this._name = name;
      this._wmlValue = value;
      this._contextManager = contextManager;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void start() {
      int time = 0;
      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         super._value = Integer.parseInt(this._wmlValue.getName());
         var10 = false;
      } finally {
         if (var10) {
            return;
         }
      }

      if (this._name != null) {
         String value = this._name.getValue();
         if (value == null || value.length() == 0) {
            this._name.set(Integer.toString(super._value));
         }

         boolean var7 = false /* VF: Semaphore variable */;

         int timerValue;
         try {
            var7 = true;
            timerValue = Integer.parseInt(this._name.getValue());
            var7 = false;
         } finally {
            if (var7) {
               return;
            }
         }

         if (timerValue <= 0) {
            return;
         }

         time = timerValue * 100;
      } else {
         if (super._value <= 0) {
            return;
         }

         time = super._value * 100;
      }

      this._contextManager.setTimerValue(super._value);
      super._startTime = System.currentTimeMillis();
      super._invokeApp = Application.getApplication();
      if (super._invokeApp != null) {
         this._id = super._invokeApp.invokeLater(this, time, false);
         super._started = true;
      }
   }

   @Override
   public final void stop() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/browser/page/PageTimer._started Z
      // 04: ifeq 0e
      // 07: aload 0
      // 08: getfield net/rim/device/apps/internal/browser/wml/WMLTimer._name Lnet/rim/device/apps/internal/browser/wml/WMLVariable;
      // 0b: ifnonnull 14
      // 0e: aload 0
      // 0f: bipush 0
      // 10: putfield net/rim/device/apps/internal/browser/page/PageTimer._started Z
      // 13: return
      // 14: aload 0
      // 15: getfield net/rim/device/apps/internal/browser/page/PageTimer._invokeApp Lnet/rim/device/api/system/Application;
      // 18: aload 0
      // 19: getfield net/rim/device/apps/internal/browser/wml/WMLTimer._id I
      // 1c: invokevirtual net/rim/device/api/system/Application.cancelInvokeLater (I)V
      // 1f: invokestatic java/lang/System.currentTimeMillis ()J
      // 22: lstore 1
      // 23: aload 0
      // 24: getfield net/rim/device/apps/internal/browser/wml/WMLTimer._name Lnet/rim/device/apps/internal/browser/wml/WMLVariable;
      // 27: invokevirtual net/rim/device/apps/internal/browser/wml/WMLVariable.getValue ()Ljava/lang/String;
      // 2a: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 2d: istore 3
      // 2e: iload 3
      // 2f: bipush 100
      // 31: imul
      // 32: i2l
      // 33: lload 1
      // 34: lsub
      // 35: aload 0
      // 36: getfield net/rim/device/apps/internal/browser/page/PageTimer._startTime J
      // 39: ladd
      // 3a: bipush 100
      // 3c: i2l
      // 3d: ldiv
      // 3e: l2i
      // 3f: istore 4
      // 41: aload 0
      // 42: getfield net/rim/device/apps/internal/browser/wml/WMLTimer._name Lnet/rim/device/apps/internal/browser/wml/WMLVariable;
      // 45: iload 4
      // 47: invokestatic java/lang/Integer.toString (I)Ljava/lang/String;
      // 4a: invokevirtual net/rim/device/apps/internal/browser/wml/WMLVariable.set (Ljava/lang/String;)V
      // 4d: aload 0
      // 4e: getfield net/rim/device/apps/internal/browser/wml/WMLTimer._contextManager Lnet/rim/device/apps/internal/browser/wml/WMLContextManager;
      // 51: iload 4
      // 53: invokevirtual net/rim/device/apps/internal/browser/wml/WMLContextManager.setTimerValue (I)V
      // 56: aload 0
      // 57: bipush 0
      // 58: putfield net/rim/device/apps/internal/browser/page/PageTimer._started Z
      // 5b: aload 0
      // 5c: aconst_null
      // 5d: putfield net/rim/device/apps/internal/browser/page/PageTimer._invokeApp Lnet/rim/device/api/system/Application;
      // 60: return
      // 61: astore 3
      // 62: aload 0
      // 63: bipush 0
      // 64: putfield net/rim/device/apps/internal/browser/page/PageTimer._started Z
      // 67: aload 0
      // 68: aconst_null
      // 69: putfield net/rim/device/apps/internal/browser/page/PageTimer._invokeApp Lnet/rim/device/api/system/Application;
      // 6c: return
      // 6d: astore 5
      // 6f: aload 0
      // 70: bipush 0
      // 71: putfield net/rim/device/apps/internal/browser/page/PageTimer._started Z
      // 74: aload 0
      // 75: aconst_null
      // 76: putfield net/rim/device/apps/internal/browser/page/PageTimer._invokeApp Lnet/rim/device/api/system/Application;
      // 79: aload 5
      // 7b: athrow
      // try (17 -> 45): 52 null
      // try (17 -> 45): 60 null
      // try (52 -> 53): 60 null
      // try (60 -> 61): 60 null
   }

   @Override
   public final void run() {
      if (super._started) {
         super._started = false;
         if (this._name != null) {
            this._name.set("0");
         }

         this._contextManager.setTimerValue(0);
         super._browserContent.invokeOnTimer();
      }
   }
}

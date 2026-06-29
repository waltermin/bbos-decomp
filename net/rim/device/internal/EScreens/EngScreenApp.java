package net.rim.device.internal.EScreens;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.RadioInternal;

public final class EngScreenApp extends UiApplication {
   private boolean _alreadyViewing;
   private static final int CDMA_RADIO_SETUP_DM_ACCESS = 7;
   private static final int CDMA_RADIO_ENABLE_ENGINEERING_MODE = 4915;

   public static final void main(String[] args) {
      if (args != null && args.length != 0) {
         if (args.length == 1) {
            new EngScreenApp().enterEventDispatcher();
         }
      } else {
         ApplicationDescriptor appDescrip = ApplicationDescriptor.currentApplicationDescriptor();
         ((ApplicationManagerInternal)ApplicationManager.getApplicationManager())
            .setEngScreenDescriptor(new ApplicationDescriptor(appDescrip, new String[]{"Execute"}));
         if (InternalServices.isDeviceSecure() && RadioInfo.areWAFsSupported(2) && Branding.getData(16) != null) {
            RadioInternal.setup(7, 4915);
            return;
         }
      }
   }

   @Override
   public final void activate() {
      if (!this._alreadyViewing) {
         this._alreadyViewing = true;
         int accessLevel = EScreenAccess.getAccessLevel();
         if (accessLevel != -1 && !RadioInfo.areWAFsSupported(8)) {
            this.startupEScreens(accessLevel);
         } else {
            EngScreenSecurity security = new EngScreenSecurity(this);
            security.go();
         }

         super.activate();
      }
   }

   public final void startupEScreens(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 2
      // 02: ldc_w "net.rim.device.internal.EScreens.EScreenController"
      // 05: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 08: astore 2
      // 09: goto 0e
      // 0c: astore 4
      // 0e: aload 2
      // 0f: ifnull 34
      // 12: aload 2
      // 13: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 16: checkcast net/rim/device/internal/EScreens/EScreenRoot
      // 19: astore 3
      // 1a: aload 3
      // 1b: iload 1
      // 1c: invokeinterface net/rim/device/internal/EScreens/EScreenRoot.initEScreens (I)V 2
      // 21: aload 3
      // 22: invokeinterface net/rim/device/internal/EScreens/EScreenRoot.startupEScreens ()V 1
      // 27: return
      // 28: astore 4
      // 2a: goto 34
      // 2d: astore 4
      // 2f: goto 34
      // 32: astore 4
      // 34: aload 0
      // 35: new net/rim/device/internal/EScreens/EngScreenApp$Whoops
      // 38: dup
      // 39: aconst_null
      // 3a: invokespecial net/rim/device/internal/EScreens/EngScreenApp$Whoops.<init> (Lnet/rim/device/internal/EScreens/EngScreenApp$1;)V
      // 3d: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 40: return
      // try (2 -> 5): 6 null
      // try (9 -> 18): 19 null
      // try (9 -> 18): 21 null
      // try (9 -> 18): 23 null
   }
}

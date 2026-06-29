package net.rim.device.apps.internal.idlescreen;

import java.io.InputStream;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.internal.ribbon.skin.svg.MediaLayout;
import net.rim.vm.DebugSupport;

final class IdleScreenApplication extends UiApplication {
   private static IdleScreenApplication$GlobalData _globalData = (IdleScreenApplication$GlobalData)ApplicationRegistry.getApplicationRegistry()
      .get(5892374562583112875L);
   private static final long CONTENT_STYLE;
   private static final String _showArg;
   static final String OLD_USER_FILENAME;
   private static final String CONTENT_INJECTOR_FILENAME;
   private static final String BRAND_FILE_PATH;
   private static final long START_DELAY;
   static IdleScreenApplication$ElevatePriority _elevatePriority = new IdleScreenApplication$ElevatePriority();

   public static final void main(String[] args) {
      if ((Display.getProperties() & 16384) == 0) {
         if (DeviceInfo.isSimulator()) {
            String option = DebugSupport.getenv("DisableIdleScreen");
            if (option != null) {
               return;
            }
         }

         if (args != null && args.length == 1 && args[0].equals("show")) {
            new IdleScreenApplication().enterEventDispatcher();
         } else {
            _globalData = new IdleScreenApplication$GlobalData();
            ApplicationRegistry.getApplicationRegistry().put(5892374562583112875L, _globalData);
            _globalData._clockListener.start();
            registerVerbs();
            IdleScreenOptions.register();
            IdleScreenManagerImpl.register();
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final void show(boolean force) {
      synchronized (_globalData) {
         if (force || _globalData._instance == null && !_globalData._launchPending && !DeviceInfo.isInHolster() && !IdleScreenOptions.isDisabled()) {
            _globalData._launchPending = true;

            try {
               ApplicationManager.getApplicationManager().runApplication(_globalData._showDescriptor);
            } catch (Throwable var6) {
               _globalData._launchPending = false;
               System.out.println(e.toString());
               return;
            }
         }
      }
   }

   static final void hook(Application hook) {
      synchronized (_globalData) {
         _globalData._hook = hook;
         if (_globalData._launchPending) {
            label34:
            try {
               _globalData.wait(1000);
            } finally {
               break label34;
            }
         }

         if (_globalData._instance != null) {
            _globalData._instance.invokeLater(_elevatePriority);
         }
      }
   }

   static final void unhook() {
      _globalData._hook = null;
   }

   final Application getHook() {
      return _globalData._hook;
   }

   private static final IdleScreen createScreen() {
      Field field = getContentField();
      if (field == null) {
         field = getThemeField();
      }

      if (field == null) {
         field = getCarrierField();
      }

      if (field == null) {
         field = getBrandingField();
      }

      if (field == null) {
         field = getDefaultField();
      }

      if (field == null) {
         return null;
      }

      IdleScreen screen = new IdleScreen();
      RibbonBanner banner = RibbonBanner.getInstance();
      if (banner != null) {
         Field statusBanner = banner.getStatusBanner(null, 1);
         if (statusBanner != null) {
            screen.add(statusBanner);
         }
      }

      screen.add(field);
      return screen;
   }

   private static final Field getDefaultField() {
      ContextObject context = (ContextObject)(new Object());
      context.setPrivateFlag(3089937493992571440L, 1);
      MediaLayout field = MediaLayout.create(
         context,
         Display.getHeight() > 160 ? "cod://net_rim_bb_idlescreen_app/tachyon.pme" : "cod://net_rim_bb_idlescreen_app/quark.pme",
         3476778963869630464L,
         64424509440L
      );
      field.setStartDelay(500);
      field.setRestart(true);
      field.setStopIfNotVisible(true);
      return field;
   }

   private static final Field createImageField(EncodedImage image, boolean useOptionsScaling) {
      String scaleFPString = null;
      if (useOptionsScaling) {
         scaleFPString = IdleScreenOptions.getIdleScreenAttribute(2349937757985153567L);
      }

      if (scaleFPString == null) {
         if (image == null) {
            return null;
         }

         int valign = image.getHeight() < Display.getHeight() - 38 ? 32 : 40;
         return (Field)(image.getFrameCount() > 1
            ? new IdlescreenAnimatedBitmapField(image, valign | 4 | 2305843009213693952L)
            : new Object(image.getBitmap(), valign | 4 | 2305843009213693952L));
      } else {
         String rotationString = IdleScreenOptions.getIdleScreenAttribute(2550679879375249665L);
         String topXString = IdleScreenOptions.getIdleScreenAttribute(-7340185234772503578L);
         String topYString = IdleScreenOptions.getIdleScreenAttribute(-3942144983241110768L);
         int scaleFP = Integer.parseInt(scaleFPString);
         int rotation = rotationString == null ? 0 : Integer.parseInt(rotationString);
         int topX = topXString == null ? 0 : Integer.parseInt(topXString);
         int topY = topYString == null ? 0 : Integer.parseInt(topYString);
         int screenWidth = Display.getWidth();
         int screenHeight = Display.getHeight();
         Bitmap newBitmap = (Bitmap)(new Object(screenWidth, screenHeight));
         synchronized (Application.getEventLock()) {
            IdleScreenApplication$ZoomBitmapFieldExt zoom = new IdleScreenApplication$ZoomBitmapFieldExt(image);
            zoom.setScale(scaleFP, topX, topY, rotation);
            zoom.layoutHack(screenWidth, screenHeight);
            Graphics graphics = (Graphics)(new Object(newBitmap));
            zoom.paintHack(graphics);
         }

         return (Field)(new Object(newBitmap, 2305843009213693996L));
      }
   }

   private static final Field getThemeField() {
      Theme theme = ThemeManager.getActiveTheme();
      if (theme != null) {
         String idleScreenName = theme.getIdleScreenName();
         return getFieldFromURL(idleScreenName, false);
      } else {
         return null;
      }
   }

   private static final Field getBrandingField() {
      byte[] data = Branding.getData(16896);
      if (data == null) {
         return null;
      }

      try {
         String contentType = (String)(new Object(data, "UTF8"));
         data = Branding.getData(16897);
         return data == null ? null : createContent(data, contentType, false);
      } finally {
         ;
      }
   }

   private static final Field getContentField() {
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
      // 00: aconst_null
      // 01: astore 0
      // 02: invokestatic net/rim/device/apps/internal/idlescreen/IdleScreenOptions.getIdleScreenFilename ()Ljava/lang/String;
      // 05: astore 1
      // 06: aload 1
      // 07: ifnull 11
      // 0a: aload 1
      // 0b: invokevirtual java/lang/String.length ()I
      // 0e: ifne 15
      // 11: ldc_w "/store/appdata/rim/idlescreen/user/idle"
      // 14: astore 1
      // 15: new java/lang/Object
      // 18: dup
      // 19: ldc_w "file://"
      // 1c: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 1f: aload 1
      // 20: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 23: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 26: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 29: checkcast java/lang/Object
      // 2c: astore 0
      // 2d: aload 0
      // 2e: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 33: ifne 3d
      // 36: aconst_null
      // 37: invokestatic net/rim/device/apps/internal/idlescreen/IdleScreenOptions.setIdleScreenFilename (Ljava/lang/String;)V
      // 3a: goto 57
      // 3d: aload 0
      // 3e: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 43: bipush 1
      // 44: invokestatic net/rim/device/apps/internal/idlescreen/IdleScreenApplication.getFieldFromURL (Ljava/lang/String;Z)Lnet/rim/device/api/ui/Field;
      // 47: astore 2
      // 48: aload 0
      // 49: ifnull 55
      // 4c: aload 0
      // 4d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 52: aload 2
      // 53: areturn
      // 54: astore 3
      // 55: aload 2
      // 56: areturn
      // 57: aload 0
      // 58: ifnull 8a
      // 5b: aload 0
      // 5c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 61: aconst_null
      // 62: areturn
      // 63: astore 1
      // 64: aconst_null
      // 65: areturn
      // 66: astore 1
      // 67: aload 0
      // 68: ifnull 8a
      // 6b: aload 0
      // 6c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 71: aconst_null
      // 72: areturn
      // 73: astore 1
      // 74: aconst_null
      // 75: areturn
      // 76: astore 4
      // 78: aload 0
      // 79: ifnull 87
      // 7c: aload 0
      // 7d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 82: goto 87
      // 85: astore 5
      // 87: aload 4
      // 89: athrow
      // 8a: aconst_null
      // 8b: areturn
      // try (34 -> 36): 38 null
      // try (43 -> 45): 47 null
      // try (2 -> 32): 50 null
      // try (53 -> 55): 57 null
      // try (2 -> 32): 60 null
      // try (50 -> 51): 60 null
      // try (63 -> 65): 66 null
      // try (60 -> 61): 60 null
   }

   private static final Field getCarrierField() {
      return getFieldFromURL("file:///store/appdata/rim/idlescreen/carrier/idle", false);
   }

   private static final Field getFieldFromURL(String param0, boolean param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: ifnonnull 006
      // 004: aconst_null
      // 005: areturn
      // 006: aconst_null
      // 007: astore 2
      // 008: aload 0
      // 009: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 00c: checkcast java/lang/Object
      // 00f: astore 2
      // 010: aconst_null
      // 011: astore 3
      // 012: aload 2
      // 013: dup
      // 014: instanceof java/lang/Object
      // 017: ifne 01e
      // 01a: pop
      // 01b: goto 050
      // 01e: checkcast java/lang/Object
      // 021: astore 4
      // 023: aload 4
      // 025: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 02a: ifne 042
      // 02d: aconst_null
      // 02e: astore 5
      // 030: aload 2
      // 031: ifnull 03f
      // 034: aload 2
      // 035: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 03a: goto 03f
      // 03d: astore 6
      // 03f: aload 5
      // 041: areturn
      // 042: aload 4
      // 044: invokeinterface javax/microedition/io/file/FileConnection.getName ()Ljava/lang/String; 1
      // 049: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 04c: astore 3
      // 04d: goto 065
      // 050: aload 2
      // 051: dup
      // 052: instanceof java/lang/Object
      // 055: ifne 05c
      // 058: pop
      // 059: goto 065
      // 05c: checkcast java/lang/Object
      // 05f: invokeinterface javax/microedition/io/ContentConnection.getType ()Ljava/lang/String; 1
      // 064: astore 3
      // 065: aconst_null
      // 066: astore 4
      // 068: aconst_null
      // 069: astore 5
      // 06b: aload 2
      // 06c: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 071: astore 5
      // 073: aload 5
      // 075: invokestatic net/rim/device/api/io/IOUtilities.streamToBytes (Ljava/io/InputStream;)[B
      // 078: astore 4
      // 07a: aload 5
      // 07c: ifnull 0c6
      // 07f: aload 5
      // 081: invokevirtual java/io/InputStream.close ()V
      // 084: goto 0c6
      // 087: astore 6
      // 089: goto 0c6
      // 08c: astore 6
      // 08e: aconst_null
      // 08f: astore 7
      // 091: aload 5
      // 093: ifnull 0a0
      // 096: aload 5
      // 098: invokevirtual java/io/InputStream.close ()V
      // 09b: goto 0a0
      // 09e: astore 8
      // 0a0: aload 2
      // 0a1: ifnull 0af
      // 0a4: aload 2
      // 0a5: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0aa: goto 0af
      // 0ad: astore 8
      // 0af: aload 7
      // 0b1: areturn
      // 0b2: astore 9
      // 0b4: aload 5
      // 0b6: ifnull 0c3
      // 0b9: aload 5
      // 0bb: invokevirtual java/io/InputStream.close ()V
      // 0be: goto 0c3
      // 0c1: astore 10
      // 0c3: aload 9
      // 0c5: athrow
      // 0c6: aload 3
      // 0c7: ifnonnull 100
      // 0ca: aload 4
      // 0cc: ifnull 0f8
      // 0cf: aload 4
      // 0d1: arraylength
      // 0d2: bipush 4
      // 0d4: if_icmple 0f8
      // 0d7: aload 4
      // 0d9: bipush 1
      // 0da: baload
      // 0db: bipush 80
      // 0dd: if_icmpne 0f8
      // 0e0: aload 4
      // 0e2: bipush 2
      // 0e4: baload
      // 0e5: bipush 77
      // 0e7: if_icmpne 0f8
      // 0ea: aload 4
      // 0ec: bipush 3
      // 0ee: baload
      // 0ef: bipush 69
      // 0f1: if_icmpne 0f8
      // 0f4: ldc_w "application/x-vnd.rim.pme"
      // 0f7: astore 3
      // 0f8: aload 3
      // 0f9: ifnonnull 100
      // 0fc: ldc_w "image/png"
      // 0ff: astore 3
      // 100: aload 4
      // 102: aload 3
      // 103: iload 1
      // 104: invokestatic net/rim/device/apps/internal/idlescreen/IdleScreenApplication.createContent ([BLjava/lang/String;Z)Lnet/rim/device/api/ui/Field;
      // 107: astore 6
      // 109: aload 2
      // 10a: ifnull 118
      // 10d: aload 2
      // 10e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 113: goto 118
      // 116: astore 7
      // 118: aload 6
      // 11a: areturn
      // 11b: astore 3
      // 11c: aconst_null
      // 11d: astore 4
      // 11f: aload 2
      // 120: ifnull 12e
      // 123: aload 2
      // 124: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 129: goto 12e
      // 12c: astore 5
      // 12e: aload 4
      // 130: areturn
      // 131: astore 3
      // 132: aconst_null
      // 133: astore 4
      // 135: aload 2
      // 136: ifnull 144
      // 139: aload 2
      // 13a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 13f: goto 144
      // 142: astore 5
      // 144: aload 4
      // 146: areturn
      // 147: astore 11
      // 149: aload 2
      // 14a: ifnull 158
      // 14d: aload 2
      // 14e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 153: goto 158
      // 156: astore 12
      // 158: aload 11
      // 15a: athrow
      // try (27 -> 29): 30 null
      // try (59 -> 61): 62 null
      // try (51 -> 57): 64 null
      // try (69 -> 71): 72 null
      // try (75 -> 77): 78 null
      // try (51 -> 57): 81 null
      // try (64 -> 67): 81 null
      // try (84 -> 86): 87 null
      // try (81 -> 82): 81 null
      // try (126 -> 128): 129 null
      // try (6 -> 25): 132 null
      // try (33 -> 73): 132 null
      // try (81 -> 124): 132 null
      // try (137 -> 139): 140 null
      // try (6 -> 25): 143 null
      // try (33 -> 73): 143 null
      // try (81 -> 124): 143 null
      // try (148 -> 150): 151 null
      // try (6 -> 25): 154 null
      // try (33 -> 73): 154 null
      // try (81 -> 124): 154 null
      // try (132 -> 135): 154 null
      // try (143 -> 146): 154 null
      // try (157 -> 159): 160 null
      // try (154 -> 155): 154 null
   }

   private static final Field createContent(byte[] data, String contentType, boolean useOptionsScaling) {
      if (contentType == null || data == null) {
         return null;
      } else if (contentType.startsWith("application/x-vnd.rim.pme") || contentType.equals("image/pme")) {
         InputStream is = (InputStream)(new Object(data));
         ContextObject context = (ContextObject)(new Object());
         context.setPrivateFlag(3089937493992571440L, 1);
         MediaLayout field = MediaLayout.create(context, is, contentType, 64424509440L);
         field.setStartDelay(500);
         field.setRestart(true);
         field.setStopIfNotVisible(true);
         return field;
      } else {
         return contentType.startsWith("image/") ? createImageField(EncodedImage.createEncodedImage(data, 0, data.length), useOptionsScaling) : null;
      }
   }

   IdleScreenApplication() {
      synchronized (_globalData) {
         _globalData._launchPending = false;
         _globalData.notifyAll();
         IdleScreen screen = _globalData._screen;
         if (screen != null) {
            try {
               screen.show(this);
            } finally {
               ;
            }
         }

         if (screen == null) {
            screen = createScreen();
            _globalData._screen = screen;
            if (screen != null) {
               screen.show(this);
            }
         }

         _globalData._instance = this;
         _globalData._clockListener.stop();
         RIMGlobalMessagePoster.postGlobalEvent(-8246942408779309615L);
      }
   }

   final void exit() {
      synchronized (_globalData) {
         _globalData._instance = null;
         _globalData._clockListener.start();
         RIMGlobalMessagePoster.postGlobalEvent(6946990846586544061L);
         System.exit(0);
      }
   }

   @Override
   protected final boolean acceptsForeground() {
      return false;
   }

   private static final void registerVerbs() {
      VerbRepository.getVerbRepository(-2843135760572915788L).register(new SetAsIdleScreenVerb(_globalData), -753816125826020042L);
      VerbRepository.getVerbRepository(-2843135760572915788L).register(new ClearIdleScreenVerb(_globalData), 3333706745147511007L);
   }
}

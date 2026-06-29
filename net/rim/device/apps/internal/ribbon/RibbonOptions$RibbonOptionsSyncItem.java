package net.rim.device.apps.internal.ribbon;

import java.util.Enumeration;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager;

final class RibbonOptions$RibbonOptionsSyncItem extends OTASyncCapableSyncItem implements SyncEventListener, OTASyncPriorityProvider {
   private boolean _inSerialSync;
   private static final byte APPLICATION_PRIORITY_RECORD = 0;
   private static final byte APPLICATION_VISIBILITY_RECORD = 1;
   private static final byte HIERARCHY_INFORMATION_RECORD = 3;
   private static final byte BACKGROUND_IMAGE_NAME = 4;
   private static final byte BACKGROUND_IMAGE_THEME_NAME = 5;
   private static final byte BACKGROUND_IMAGE_PROPERTIES = 6;
   private static final byte BACKGROUND_IMAGE_DATA_RECORD = 7;
   private static final byte BACKGROUND_IMAGE_DATA_RECORD_COMPLETE = 0;
   private static final byte BACKGROUND_IMAGE_DATA_RECORD_THEME_NAME = 1;
   private static final byte BACKGROUND_IMAGE_DATA_RECORD_THEME_IMAGE_NAME = 2;
   private static final byte BACKGROUND_IMAGE_DATA_RECORD_THEME_IMAGE_PROPERTIES = 3;
   private static final byte BACKGROUND_IMAGE_DATA_RECORD_THEME_END = 4;
   private static final int DB_VERSION = 0;

   final void readBackgroundImageData(DataBuffer buffer) {
      synchronized (RibbonOptions._options._persistedRibbonOptions._backgroundImages) {
         try {
            String themeName = null;
            String imageName = null;
            String[] imageProperties = null;
            BackgroundImage image = null;

            while (!buffer.eof()) {
               switch (ConverterUtilities.getType(buffer, true)) {
                  case -1:
                     ConverterUtilities.skipField(buffer);
                     break;
                  case 0:
                  default:
                     ConverterUtilities.skipField(buffer);
                     return;
                  case 1:
                     themeName = ConverterUtilities.readString(buffer);
                     break;
                  case 2:
                     imageName = ConverterUtilities.readString(buffer);
                     image = new BackgroundImage();
                     image._name = imageName;
                     break;
                  case 3:
                     char[][][] properties = (char[][][])ConverterUtilities.readCharArrayArray(buffer, false);
                     imageProperties = new Object[properties.length];

                     for (int i = 0; i < properties.length; i++) {
                        imageProperties[i] = (String)(new Object((char[])properties[i]));
                     }

                     image._properties = imageProperties;
                     break;
                  case 4:
                     if (themeName != null && image != null) {
                        RibbonOptions._options._persistedRibbonOptions._backgroundImages.put(themeName, image);
                     }

                     themeName = null;
                     imageName = null;
                     imageProperties = null;
                     image = null;
                     ConverterUtilities.skipField(buffer);
               }
            }
         } finally {
            return;
         }
      }
   }

   final void writeBackgroundImageData(DataBuffer buffer) {
      synchronized (RibbonOptions._options._persistedRibbonOptions._backgroundImages) {
         Enumeration keys = RibbonOptions._options._persistedRibbonOptions._backgroundImages.keys();

         while (keys.hasMoreElements()) {
            String themeName = (String)keys.nextElement();
            BackgroundImage image = (BackgroundImage)RibbonOptions._options._persistedRibbonOptions._backgroundImages.get(themeName);
            if (image._name != null) {
               ConverterUtilities.writeStringSmart(buffer, 1, themeName);
               ConverterUtilities.writeStringSmart(buffer, 2, image._name);
               String[] prop = image._properties;
               if (prop != null) {
                  char[][][] propChar = new char[prop.length][][];

                  for (int i = 0; i < prop.length; i++) {
                     propChar[i] = (char[][])prop[i].toCharArray();
                  }

                  buffer.writeByte(3);
                  ConverterUtilities.writeCharArrayArray(buffer, 3, (char[][])propChar);
               }

               ConverterUtilities.writeEmptyField(buffer, 4);
            }
         }

         ConverterUtilities.writeEmptyField(buffer, 0);
      }
   }

   final void performSyncCleanup() {
      RIMGlobalMessagePoster.postGlobalEvent(7596964640041972456L);
   }

   @Override
   public final int getSyncPriority() {
      return 6;
   }

   @Override
   public final void syncEventOccurred(int eventId, Object object) {
      switch (eventId) {
         case 1:
         default:
            this._inSerialSync = true;
            return;
         case 2:
            this._inSerialSync = false;
            this.performSyncCleanup();
         case 0:
      }
   }

   @Override
   public final boolean setSyncData(DataBuffer param1, int param2) {
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
      // 000: invokestatic net/rim/device/apps/internal/ribbon/RibbonOptions.getOptions ()Lnet/rim/device/apps/internal/ribbon/RibbonOptions;
      // 003: astore 3
      // 004: aload 1
      // 005: invokevirtual net/rim/device/api/util/DataBuffer.readShort ()S
      // 008: pop
      // 009: aload 1
      // 00a: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 00d: istore 4
      // 00f: iload 4
      // 011: bipush 1
      // 012: if_icmpeq 02a
      // 015: bipush 0
      // 016: istore 5
      // 018: aload 3
      // 019: invokevirtual net/rim/device/apps/api/options/OptionsBase.commit ()V
      // 01c: aload 0
      // 01d: getfield net/rim/device/apps/internal/ribbon/RibbonOptions$RibbonOptionsSyncItem._inSerialSync Z
      // 020: ifne 027
      // 023: aload 0
      // 024: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions$RibbonOptionsSyncItem.performSyncCleanup ()V
      // 027: iload 5
      // 029: ireturn
      // 02a: invokestatic net/rim/device/apps/internal/ribbon/launcher/HierarchyManager.getInstance ()Lnet/rim/device/apps/internal/ribbon/launcher/HierarchyManager;
      // 02d: invokevirtual net/rim/device/apps/internal/ribbon/launcher/HierarchyManager.newOptionsSyncing ()V
      // 030: aload 3
      // 031: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions.getPriorities ()Lnet/rim/device/api/util/ToIntHashtable;
      // 034: invokevirtual net/rim/device/api/util/ToIntHashtable.clear ()V
      // 037: aload 3
      // 038: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions.getVisibilities ()Lnet/rim/device/api/util/ToIntHashtable;
      // 03b: invokevirtual net/rim/device/api/util/ToIntHashtable.clear ()V
      // 03e: aload 3
      // 03f: aload 1
      // 040: invokevirtual net/rim/device/api/util/DataBuffer.readBoolean ()Z
      // 043: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions.setShowHiddenApps (Z)V
      // 046: aconst_null
      // 047: astore 8
      // 049: aconst_null
      // 04a: astore 9
      // 04c: aconst_null
      // 04d: astore 10
      // 04f: aload 1
      // 050: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 053: ifeq 059
      // 056: goto 1be
      // 059: aload 1
      // 05a: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 05d: istore 5
      // 05f: iload 5
      // 061: tableswitch 51 -1 7 349 51 51 349 121 144 131 218 304
      // 094: aload 1
      // 095: invokevirtual net/rim/device/api/util/DataBuffer.readUTF ()Ljava/lang/String;
      // 098: astore 6
      // 09a: aload 1
      // 09b: invokevirtual net/rim/device/api/util/DataBuffer.readInt ()I
      // 09e: istore 7
      // 0a0: iload 5
      // 0a2: tableswitch 26 -1 1 -83 26 37
      // 0bc: aload 3
      // 0bd: aload 6
      // 0bf: iload 7
      // 0c1: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions.setPriority (Ljava/lang/String;I)V
      // 0c4: goto 04f
      // 0c7: aload 3
      // 0c8: aload 6
      // 0ca: iload 7
      // 0cc: ifeq 0d3
      // 0cf: bipush 1
      // 0d0: goto 0d4
      // 0d3: bipush 0
      // 0d4: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions.setVisible (Ljava/lang/String;Z)V
      // 0d7: goto 04f
      // 0da: invokestatic net/rim/device/apps/internal/ribbon/launcher/HierarchyManager.getInstance ()Lnet/rim/device/apps/internal/ribbon/launcher/HierarchyManager;
      // 0dd: aload 1
      // 0de: invokevirtual net/rim/device/apps/internal/ribbon/launcher/HierarchyManager.readHierarchyData (Lnet/rim/device/api/util/DataBuffer;)V
      // 0e1: goto 04f
      // 0e4: aload 1
      // 0e5: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readString (Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 0e8: astore 11
      // 0ea: aload 11
      // 0ec: astore 8
      // 0ee: goto 04f
      // 0f1: aload 1
      // 0f2: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readString (Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 0f5: astore 11
      // 0f7: aload 11
      // 0f9: ifnull 11b
      // 0fc: aload 11
      // 0fe: ldc_w "/home"
      // 101: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 104: ifeq 11b
      // 107: new java/lang/Object
      // 10a: dup
      // 10b: ldc_w "/store"
      // 10e: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 111: aload 11
      // 113: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 116: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 119: astore 11
      // 11b: aload 11
      // 11d: astore 9
      // 11f: aload 8
      // 121: ifnonnull 127
      // 124: goto 04f
      // 127: aload 9
      // 129: ifnonnull 12f
      // 12c: goto 04f
      // 12f: aload 3
      // 130: aload 8
      // 132: aload 9
      // 134: aconst_null
      // 135: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions.setBackgroundImage (Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V
      // 138: goto 04f
      // 13b: aload 1
      // 13c: bipush 0
      // 13d: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readCharArrayArray (Lnet/rim/device/api/util/DataBuffer;Z)[[C
      // 140: astore 12
      // 142: aload 12
      // 144: arraylength
      // 145: anewarray 609
      // 148: astore 10
      // 14a: bipush 0
      // 14b: istore 13
      // 14d: iload 13
      // 14f: aload 12
      // 151: arraylength
      // 152: if_icmpge 16c
      // 155: aload 10
      // 157: iload 13
      // 159: new java/lang/Object
      // 15c: dup
      // 15d: aload 12
      // 15f: iload 13
      // 161: aaload
      // 162: invokespecial java/lang/String.<init> ([C)V
      // 165: aastore
      // 166: iinc 13 1
      // 169: goto 14d
      // 16c: aload 8
      // 16e: ifnonnull 174
      // 171: goto 04f
      // 174: aload 9
      // 176: ifnonnull 17c
      // 179: goto 04f
      // 17c: aload 10
      // 17e: ifnonnull 184
      // 181: goto 04f
      // 184: aload 3
      // 185: aload 8
      // 187: aload 9
      // 189: aload 10
      // 18b: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions.setBackgroundImage (Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V
      // 18e: goto 04f
      // 191: aload 0
      // 192: aload 1
      // 193: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions$RibbonOptionsSyncItem.readBackgroundImageData (Lnet/rim/device/api/util/DataBuffer;)V
      // 196: invokestatic net/rim/device/api/ui/theme/ThemeManager.getActiveName ()Ljava/lang/String;
      // 199: astore 8
      // 19b: aload 3
      // 19c: aload 8
      // 19e: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions.getBackgroundImage (Ljava/lang/String;)Lnet/rim/device/apps/internal/ribbon/BackgroundImage;
      // 1a1: astore 13
      // 1a3: aload 13
      // 1a5: ifnonnull 1ab
      // 1a8: goto 04f
      // 1ab: aload 3
      // 1ac: aload 8
      // 1ae: aload 13
      // 1b0: getfield net/rim/device/apps/internal/ribbon/BackgroundImage._name Ljava/lang/String;
      // 1b3: aload 13
      // 1b5: getfield net/rim/device/apps/internal/ribbon/BackgroundImage._properties [Ljava/lang/String;
      // 1b8: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions.setBackgroundImage (Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V
      // 1bb: goto 04f
      // 1be: aload 3
      // 1bf: invokevirtual net/rim/device/apps/api/options/OptionsBase.commit ()V
      // 1c2: aload 0
      // 1c3: getfield net/rim/device/apps/internal/ribbon/RibbonOptions$RibbonOptionsSyncItem._inSerialSync Z
      // 1c6: ifne 1f6
      // 1c9: aload 0
      // 1ca: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions$RibbonOptionsSyncItem.performSyncCleanup ()V
      // 1cd: bipush 1
      // 1ce: ireturn
      // 1cf: astore 4
      // 1d1: aload 3
      // 1d2: invokevirtual net/rim/device/apps/api/options/OptionsBase.commit ()V
      // 1d5: aload 0
      // 1d6: getfield net/rim/device/apps/internal/ribbon/RibbonOptions$RibbonOptionsSyncItem._inSerialSync Z
      // 1d9: ifne 1f6
      // 1dc: aload 0
      // 1dd: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions$RibbonOptionsSyncItem.performSyncCleanup ()V
      // 1e0: bipush 1
      // 1e1: ireturn
      // 1e2: astore 14
      // 1e4: aload 3
      // 1e5: invokevirtual net/rim/device/apps/api/options/OptionsBase.commit ()V
      // 1e8: aload 0
      // 1e9: getfield net/rim/device/apps/internal/ribbon/RibbonOptions$RibbonOptionsSyncItem._inSerialSync Z
      // 1ec: ifne 1f3
      // 1ef: aload 0
      // 1f0: invokevirtual net/rim/device/apps/internal/ribbon/RibbonOptions$RibbonOptionsSyncItem.performSyncCleanup ()V
      // 1f3: aload 14
      // 1f5: athrow
      // 1f6: bipush 1
      // 1f7: ireturn
      // try (2 -> 13): 181 null
      // try (22 -> 172): 181 null
      // try (2 -> 13): 191 null
      // try (22 -> 172): 191 null
      // try (181 -> 182): 191 null
      // try (191 -> 192): 191 null
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      RibbonOptions options = RibbonOptions.getOptions();
      DataBuffer tmpBuffer = (DataBuffer)(new Object());
      tmpBuffer.setBigEndian(buffer.isBigEndian());
      tmpBuffer.writeBoolean(options.getShowHiddenApps());
      tmpBuffer.writeByte(3);
      HierarchyManager.getInstance().writeHierarchyData(tmpBuffer);
      String themeName = ThemeManager.getActiveName();
      BackgroundImage image = options.getBackgroundImage(themeName);
      String imageName = image != null ? image._name : null;
      tmpBuffer.writeByte(4);
      ConverterUtilities.writeStringSmart(tmpBuffer, 4, imageName);
      tmpBuffer.writeByte(5);
      ConverterUtilities.writeStringSmart(tmpBuffer, 5, themeName);
      String[] prop = image != null ? image._properties : null;
      if (prop != null) {
         char[][][] propChar = new char[prop.length][][];

         for (int i = 0; i < prop.length; i++) {
            propChar[i] = (char[][])prop[i].toCharArray();
         }

         tmpBuffer.writeByte(6);
         ConverterUtilities.writeCharArrayArray(tmpBuffer, 6, (char[][])propChar);
      }

      tmpBuffer.writeByte(7);
      this.writeBackgroundImageData(tmpBuffer);
      tmpBuffer.setPosition(0);
      int length = tmpBuffer.getLength();
      buffer.writeShort(length);
      buffer.writeByte(1);
      buffer.write(tmpBuffer, length);
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      RibbonOptions options = RibbonOptions.getOptions();
      HierarchyManager.getInstance().newOptionsSyncing();
      options.resetToDefaults();
      options.commit();
      RIMGlobalMessagePoster.postGlobalEvent(7596964640041972456L);
      return true;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final String getSyncName() {
      return "Theme Settings";
   }

   RibbonOptions$RibbonOptionsSyncItem() {
      SyncManager.getInstance().addSyncEventListener(this);
   }
}

package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.bluetooth.BluetoothME;

final class MediaUtilities {
   private MediaUtilities() {
   }

   static final boolean canDelete(String url) {
      String samples = "file:///store/samples";
      return !url.regionMatches(true, 0, samples, 0, samples.length()) || MIMETypeAssociations.getMediaType(url) == 3;
   }

   static final boolean canSend(String param0) {
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
      // 03: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 06: checkcast javax/microedition/io/file/FileConnection
      // 09: astore 1
      // 0a: aload 1
      // 0b: dup
      // 0c: instanceof net/rim/device/api/io/file/ExtendedFileConnection
      // 0f: ifne 16
      // 12: pop
      // 13: goto 42
      // 16: checkcast net/rim/device/api/io/file/ExtendedFileConnection
      // 19: astore 2
      // 1a: aload 2
      // 1b: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.isContentDRMForwardLocked ()Z 1
      // 20: ifne 30
      // 23: aload 2
      // 24: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.isContentBuiltIn ()Z 1
      // 29: ifne 30
      // 2c: bipush 1
      // 2d: goto 31
      // 30: bipush 0
      // 31: istore 3
      // 32: aload 1
      // 33: ifnull 40
      // 36: aload 1
      // 37: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 3c: iload 3
      // 3d: ireturn
      // 3e: astore 4
      // 40: iload 3
      // 41: ireturn
      // 42: aload 1
      // 43: ifnull 85
      // 46: aload 1
      // 47: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 4c: bipush 0
      // 4d: ireturn
      // 4e: astore 2
      // 4f: bipush 0
      // 50: ireturn
      // 51: astore 2
      // 52: aload 1
      // 53: ifnull 85
      // 56: aload 1
      // 57: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 5c: bipush 0
      // 5d: ireturn
      // 5e: astore 2
      // 5f: bipush 0
      // 60: ireturn
      // 61: astore 2
      // 62: aload 1
      // 63: ifnull 85
      // 66: aload 1
      // 67: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 6c: bipush 0
      // 6d: ireturn
      // 6e: astore 2
      // 6f: bipush 0
      // 70: ireturn
      // 71: astore 5
      // 73: aload 1
      // 74: ifnull 82
      // 77: aload 1
      // 78: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 7d: goto 82
      // 80: astore 6
      // 82: aload 5
      // 84: athrow
      // 85: bipush 0
      // 86: ireturn
      // try (26 -> 28): 30 null
      // try (35 -> 37): 39 null
      // try (2 -> 24): 42 null
      // try (45 -> 47): 49 null
      // try (2 -> 24): 52 null
      // try (55 -> 57): 59 null
      // try (2 -> 24): 62 null
      // try (42 -> 43): 62 null
      // try (52 -> 53): 62 null
      // try (65 -> 67): 68 null
      // try (62 -> 63): 62 null
   }

   static final void addDownloadTunesMenuItems(SystemEnabledMenu menu) {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(8855791131633157813L);
      if (verbRepository != null) {
         Verb[] downloadVerbs = verbRepository.getVerbs(-201905085362485851L);
         if (downloadVerbs != null) {
            int i = downloadVerbs.length;

            while (--i >= 0) {
               menu.add(downloadVerbs[i]);
            }
         }
      }
   }

   static final boolean checkBluetoothPolicy() {
      return !ITPolicy.getBoolean(34, 1, false) && !ITPolicy.getBoolean(34, 14, false) && BluetoothME.isSupported();
   }
}

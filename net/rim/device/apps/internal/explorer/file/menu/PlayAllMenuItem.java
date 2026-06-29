package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.explorer.file.ExploreManager;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.media.M3UPlaylist;

public final class PlayAllMenuItem extends MenuItem {
   private ExploreManager _explorer;
   private String _url;
   private int _resourceId;

   public PlayAllMenuItem(String url, ExploreManager mgr, int resourceId) {
      super(ExplorerResources.getResourceBundleFamily(), resourceId, 569351, Integer.MAX_VALUE);
      this._explorer = mgr;
      this._url = url;
      this._resourceId = resourceId;
   }

   @Override
   public final void run() {
      String[] files = new String[0];
      this.addDirectory(files, this._url, "");
      Arrays.sort(files, new PlayAllMenuItem$1(this));
      M3UPlaylist playlist = new M3UPlaylist();

      for (int i = 0; i < files.length; i++) {
         playlist.addUrl(files[i], null, -1);
      }

      try {
         this._explorer.openStream(playlist.openConnection(this._url));
      } finally {
         Dialog.alert(ExplorerResources.getString(202));
         return;
      }
   }

   private final void addDirectory(String[] param1, String param2, String param3) {
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
      // 01: astore 4
      // 03: aload 2
      // 04: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 07: checkcast javax/microedition/io/file/FileConnection
      // 0a: astore 4
      // 0c: aload 4
      // 0e: invokeinterface javax/microedition/io/file/FileConnection.list ()Ljava/util/Enumeration; 1
      // 13: astore 5
      // 15: aload 5
      // 17: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 1c: ifne 22
      // 1f: goto a3
      // 22: aload 5
      // 24: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 29: checkcast java/lang/String
      // 2c: astore 6
      // 2e: aload 6
      // 30: invokestatic net/rim/device/internal/io/file/FileUtilities.isDirectory (Ljava/lang/String;)Z
      // 33: ifeq 6d
      // 36: aload 0
      // 37: getfield net/rim/device/apps/internal/explorer/file/menu/PlayAllMenuItem._resourceId I
      // 3a: bipush 100
      // 3c: if_icmpne 15
      // 3f: aload 0
      // 40: aload 1
      // 41: new java/lang/StringBuffer
      // 44: dup
      // 45: invokespecial java/lang/StringBuffer.<init> ()V
      // 48: aload 2
      // 49: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 4c: aload 6
      // 4e: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 51: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 54: new java/lang/StringBuffer
      // 57: dup
      // 58: invokespecial java/lang/StringBuffer.<init> ()V
      // 5b: aload 3
      // 5c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 5f: aload 6
      // 61: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 64: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 67: invokespecial net/rim/device/apps/internal/explorer/file/menu/PlayAllMenuItem.addDirectory ([Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
      // 6a: goto 15
      // 6d: aload 6
      // 6f: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMediaType (Ljava/lang/String;)I
      // 72: istore 7
      // 74: iload 7
      // 76: bipush 2
      // 78: if_icmpeq 89
      // 7b: iload 7
      // 7d: bipush 7
      // 7f: if_icmpeq 89
      // 82: iload 7
      // 84: bipush 3
      // 86: if_icmpne 15
      // 89: aload 1
      // 8a: new java/lang/StringBuffer
      // 8d: dup
      // 8e: invokespecial java/lang/StringBuffer.<init> ()V
      // 91: aload 3
      // 92: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 95: aload 6
      // 97: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 9a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 9d: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // a0: goto 15
      // a3: aload 4
      // a5: ifnull db
      // a8: aload 4
      // aa: invokeinterface javax/microedition/io/Connection.close ()V 1
      // af: return
      // b0: astore 5
      // b2: return
      // b3: astore 5
      // b5: aload 4
      // b7: ifnull db
      // ba: aload 4
      // bc: invokeinterface javax/microedition/io/Connection.close ()V 1
      // c1: return
      // c2: astore 5
      // c4: return
      // c5: astore 8
      // c7: aload 4
      // c9: ifnull d8
      // cc: aload 4
      // ce: invokeinterface javax/microedition/io/Connection.close ()V 1
      // d3: goto d8
      // d6: astore 9
      // d8: aload 8
      // da: athrow
      // db: return
      // try (69 -> 71): 72 null
      // try (2 -> 67): 74 null
      // try (77 -> 79): 80 null
      // try (2 -> 67): 82 null
      // try (74 -> 75): 82 null
      // try (85 -> 87): 88 null
      // try (82 -> 83): 82 null
   }
}

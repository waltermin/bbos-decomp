package net.rim.device.internal.io.file;

import java.util.Enumeration;

class MetaDataFile$FileEnumeration implements Enumeration {
   private MetaDataFileInfo _info;
   private Enumeration _keys;
   private final MetaDataFile this$0;

   MetaDataFile$FileEnumeration(MetaDataFile _1) {
      this.this$0 = _1;
      this._info = new MetaDataFileInfo();
      this._info.setPath(_1._path);
      synchronized (_1) {
         if (_1._metaDataDatabase == null) {
            _1.openMetaDataDatabase(true);
         }
      }

      this._keys = _1._fileFilenameToEntry.keys();
   }

   @Override
   public boolean hasMoreElements() {
      synchronized (this.this$0) {
         if (this.this$0._metaDataDatabase == null) {
            return false;
         }
      }

      return this._keys.hasMoreElements();
   }

   @Override
   public Object nextElement() {
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
      // 01: getfield net/rim/device/internal/io/file/MetaDataFile$FileEnumeration.this$0 Lnet/rim/device/internal/io/file/MetaDataFile;
      // 04: dup
      // 05: astore 1
      // 06: monitorenter
      // 07: aload 0
      // 08: getfield net/rim/device/internal/io/file/MetaDataFile$FileEnumeration.this$0 Lnet/rim/device/internal/io/file/MetaDataFile;
      // 0b: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 0e: ifnull ae
      // 11: aload 0
      // 12: getfield net/rim/device/internal/io/file/MetaDataFile$FileEnumeration._keys Ljava/util/Enumeration;
      // 15: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 1a: checkcast java/lang/Object
      // 1d: astore 2
      // 1e: aload 0
      // 1f: getfield net/rim/device/internal/io/file/MetaDataFile$FileEnumeration.this$0 Lnet/rim/device/internal/io/file/MetaDataFile;
      // 22: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 25: aload 2
      // 26: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.get (Ljava/lang/String;)I
      // 29: istore 3
      // 2a: iload 3
      // 2b: bipush -1
      // 2d: if_icmpeq ae
      // 30: aconst_null
      // 31: astore 4
      // 33: aload 0
      // 34: getfield net/rim/device/internal/io/file/MetaDataFile$FileEnumeration.this$0 Lnet/rim/device/internal/io/file/MetaDataFile;
      // 37: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 3a: invokeinterface javax/microedition/io/file/FileConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 3f: astore 4
      // 41: aload 4
      // 43: aload 0
      // 44: getfield net/rim/device/internal/io/file/MetaDataFile$FileEnumeration.this$0 Lnet/rim/device/internal/io/file/MetaDataFile;
      // 47: getfield net/rim/device/internal/io/file/MetaDataFile._fileOffsets [J
      // 4a: iload 3
      // 4b: laload
      // 4c: invokevirtual java/io/DataInputStream.skip (J)J
      // 4f: pop2
      // 50: aload 0
      // 51: getfield net/rim/device/internal/io/file/MetaDataFile$FileEnumeration.this$0 Lnet/rim/device/internal/io/file/MetaDataFile;
      // 54: aload 4
      // 56: invokespecial net/rim/device/internal/io/file/MetaDataFile.readEntry (Ljava/io/DataInputStream;)[Ljava/lang/Object;
      // 59: astore 5
      // 5b: aload 0
      // 5c: getfield net/rim/device/internal/io/file/MetaDataFile$FileEnumeration._info Lnet/rim/device/internal/io/file/MetaDataFileInfo;
      // 5f: aload 2
      // 60: invokevirtual net/rim/device/internal/io/file/MetaDataFileInfo.setFileName (Ljava/lang/String;)V
      // 63: aload 0
      // 64: getfield net/rim/device/internal/io/file/MetaDataFile$FileEnumeration._info Lnet/rim/device/internal/io/file/MetaDataFileInfo;
      // 67: aload 5
      // 69: invokevirtual net/rim/device/internal/io/file/MetaDataFileInfo.setMetaData ([Ljava/lang/Object;)V
      // 6c: aload 0
      // 6d: getfield net/rim/device/internal/io/file/MetaDataFile$FileEnumeration._info Lnet/rim/device/internal/io/file/MetaDataFileInfo;
      // 70: astore 6
      // 72: aload 4
      // 74: ifnull 81
      // 77: aload 4
      // 79: invokevirtual java/io/DataInputStream.close ()V
      // 7c: goto 81
      // 7f: astore 7
      // 81: aload 1
      // 82: monitorexit
      // 83: aload 6
      // 85: areturn
      // 86: astore 5
      // 88: aload 4
      // 8a: ifnull ae
      // 8d: aload 4
      // 8f: invokevirtual java/io/DataInputStream.close ()V
      // 92: goto ae
      // 95: astore 5
      // 97: goto ae
      // 9a: astore 8
      // 9c: aload 4
      // 9e: ifnull ab
      // a1: aload 4
      // a3: invokevirtual java/io/DataInputStream.close ()V
      // a6: goto ab
      // a9: astore 9
      // ab: aload 8
      // ad: athrow
      // ae: aconst_null
      // af: aload 1
      // b0: monitorexit
      // b1: areturn
      // b2: astore 10
      // b4: aload 1
      // b5: monitorexit
      // b6: aload 10
      // b8: athrow
      // try (56 -> 58): 59 null
      // try (25 -> 54): 64 null
      // try (67 -> 69): 70 null
      // try (25 -> 54): 72 null
      // try (64 -> 65): 72 null
      // try (75 -> 77): 78 null
      // try (72 -> 73): 72 null
      // try (5 -> 62): 85 null
      // try (64 -> 84): 85 null
      // try (85 -> 88): 85 null
   }
}

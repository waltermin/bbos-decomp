package net.rim.device.internal.io.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.io.file.ExtendedFileConnection;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.util.StringCaseInsensitiveHashtable;
import net.rim.device.api.util.StringToIntCaseInsensitiveHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

public final class MetaDataFile {
   private String _path;
   private MetaDataFile$CacheItem _head;
   private MetaDataFile$CacheItem _tail;
   private StringCaseInsensitiveHashtable _cache = new StringCaseInsensitiveHashtable();
   private int _cacheSize;
   private int _maxCacheSize = 40;
   private FileConnection _metaDataDatabase;
   private StringToIntCaseInsensitiveHashtable _fileFilenameToEntry = new StringToIntCaseInsensitiveHashtable();
   private long[] _fileOffsets = new long[0];
   private long[] _fileModTimes = new long[0];
   private int _numEntries;
   private boolean _isRemovable;
   private static final int VERSION = 7;
   private static final int MAGIC = 604315651;
   static String THUMBS_DB_FILE = "BBThumbs.dat";
   private static String TEMP_DB_FILE = "BBThumbs.tmp";
   static final int DESIRED_THUMBNAIL_HEIGHT = 84;
   static final int DESIRED_THUMBNAIL_WIDTH = 112;
   private static final int ENTRY_TAG = 1;
   public static final int METADATA_THUMBNAIL_IMAGE = 0;
   public static final int METADATA_TITLE = 1;
   public static final int METADATA_DURATION = 2;
   public static final int METADATA_ARTIST = 3;
   public static final int METADATA_ALBUM = 4;
   public static final int METADATA_GENRE = 5;
   public static final int METADATA_TRACK_NUMBER = 6;
   public static final int METADATA_BOOKMARK = 7;
   public static final int METADATA_MAX = 8;
   private static final int METADATA_END = 255;
   private static final int MAX_SANE_STRING_SIZE = 1000;
   private static final int MAX_SANE_THUMB_SIZE = 37632;
   private static Object NO_DATA_AVAILABLE = new Object();
   private static Object[] EMPTY_META_DATA = new Object[8];
   private static WeakReference _tempData;
   private static final long METADATAFILE_GUID = 5383068212830684791L;
   private static Vector _openFileList;

   private MetaDataFile(String path) {
      this._path = path;
      this._isRemovable = FileUtilities.isRemovable(path);
   }

   static final MetaDataFile getIfOpen(String path) {
      if (_openFileList != null) {
         synchronized (_openFileList) {
            int i = _openFileList.size();

            while (--i >= 0) {
               WeakReference ref = (WeakReference)_openFileList.elementAt(i);
               MetaDataFile file = (MetaDataFile)ref.get();
               if (file == null) {
                  _openFileList.removeElement(ref);
               } else if (StringUtilities.strEqualIgnoreCase(file._path, path)) {
                  return file;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   public static final MetaDataFile getOrCreate(String path) {
      if (_openFileList == null) {
         ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
         _openFileList = (Vector)registry.getOrWaitFor(5383068212830684791L);
         if (_openFileList == null) {
            _openFileList = new Vector();
            registry.put(5383068212830684791L, _openFileList);
         }
      }

      if (path != null) {
         if (!path.endsWith("/")) {
            path = path + '/';
         }

         synchronized (_openFileList) {
            MetaDataFile file = getIfOpen(path);
            if (file == null) {
               file = new MetaDataFile(path);
               _openFileList.addElement(new WeakReference(file));
            }

            return file;
         }
      } else {
         return null;
      }
   }

   public final String getPath() {
      return this._path;
   }

   private final void readDatabase(boolean param1, boolean param2) {
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
      // 000: aload 0
      // 001: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 004: ifnonnull 00a
      // 007: goto 3d9
      // 00a: aload 0
      // 00b: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 00e: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 013: ifne 019
      // 016: goto 3d9
      // 019: aload 0
      // 01a: bipush 0
      // 01b: putfield net/rim/device/internal/io/file/MetaDataFile._numEntries I
      // 01e: aconst_null
      // 01f: astore 3
      // 020: aconst_null
      // 021: astore 4
      // 023: aconst_null
      // 024: astore 5
      // 026: bipush 0
      // 027: istore 6
      // 029: new net/rim/device/internal/io/file/CounterInputStream
      // 02c: dup
      // 02d: aload 0
      // 02e: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 031: invokeinterface javax/microedition/io/file/FileConnection.openInputStream ()Ljava/io/InputStream; 1
      // 036: invokespecial net/rim/device/internal/io/file/CounterInputStream.<init> (Ljava/io/InputStream;)V
      // 039: astore 4
      // 03b: new java/io/DataInputStream
      // 03e: dup
      // 03f: aload 4
      // 041: invokespecial java/io/DataInputStream.<init> (Ljava/io/InputStream;)V
      // 044: astore 3
      // 045: aload 3
      // 046: invokevirtual java/io/DataInputStream.readInt ()I
      // 049: ldc_w 604315651
      // 04c: if_icmpeq 057
      // 04f: new java/io/IOException
      // 052: dup
      // 053: invokespecial java/io/IOException.<init> ()V
      // 056: athrow
      // 057: aload 3
      // 058: invokevirtual java/io/DataInputStream.readByte ()B
      // 05b: bipush 7
      // 05d: if_icmpeq 068
      // 060: new java/io/IOException
      // 063: dup
      // 064: invokespecial java/io/IOException.<init> ()V
      // 067: athrow
      // 068: aload 0
      // 069: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 06c: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 071: lstore 7
      // 073: aload 0
      // 074: invokespecial net/rim/device/internal/io/file/MetaDataFile.filesystemUsesLocalTimestamps ()Z
      // 077: ifeq 083
      // 07a: invokestatic java/util/TimeZone.getDefault ()Ljava/util/TimeZone;
      // 07d: invokevirtual java/util/TimeZone.getRawOffset ()I
      // 080: goto 084
      // 083: bipush 0
      // 084: istore 9
      // 086: aload 0
      // 087: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 08a: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.clear ()V
      // 08d: bipush 1
      // 08e: i2l
      // 08f: lstore 10
      // 091: bipush 0
      // 092: istore 12
      // 094: aload 4
      // 096: invokevirtual net/rim/device/internal/io/file/CounterInputStream.getOffset ()J
      // 099: lstore 14
      // 09b: bipush 0
      // 09c: istore 16
      // 09e: aload 3
      // 09f: invokevirtual java/io/DataInputStream.read ()I
      // 0a2: dup
      // 0a3: istore 13
      // 0a5: bipush 1
      // 0a6: if_icmpeq 0ac
      // 0a9: goto 18c
      // 0ac: aload 3
      // 0ad: invokevirtual java/io/DataInputStream.readUTF ()Ljava/lang/String;
      // 0b0: astore 17
      // 0b2: aload 3
      // 0b3: invokevirtual java/io/DataInputStream.readLong ()J
      // 0b6: lstore 18
      // 0b8: aload 3
      // 0b9: invokevirtual java/io/DataInputStream.readInt ()I
      // 0bc: istore 20
      // 0be: lload 18
      // 0c0: iload 20
      // 0c2: iload 9
      // 0c4: isub
      // 0c5: i2l
      // 0c6: ladd
      // 0c7: lstore 18
      // 0c9: aload 3
      // 0ca: invokevirtual java/io/DataInputStream.readInt ()I
      // 0cd: istore 21
      // 0cf: lload 10
      // 0d1: iload 21
      // 0d3: i2l
      // 0d4: ladd
      // 0d5: lstore 10
      // 0d7: iinc 12 1
      // 0da: lload 7
      // 0dc: lload 10
      // 0de: iload 12
      // 0e0: i2l
      // 0e1: ldiv
      // 0e2: ldiv
      // 0e3: l2i
      // 0e4: istore 22
      // 0e6: aload 4
      // 0e8: invokevirtual net/rim/device/internal/io/file/CounterInputStream.getOffset ()J
      // 0eb: lstore 23
      // 0ed: iload 2
      // 0ee: istore 25
      // 0f0: iload 2
      // 0f1: ifne 16b
      // 0f4: aload 0
      // 0f5: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 0f8: dup
      // 0f9: astore 26
      // 0fb: monitorenter
      // 0fc: aload 0
      // 0fd: getfield net/rim/device/internal/io/file/MetaDataFile._cacheSize I
      // 100: aload 0
      // 101: getfield net/rim/device/internal/io/file/MetaDataFile._maxCacheSize I
      // 104: if_icmpge 15a
      // 107: iload 12
      // 109: iload 22
      // 10b: aload 0
      // 10c: getfield net/rim/device/internal/io/file/MetaDataFile._maxCacheSize I
      // 10f: isub
      // 110: if_icmplt 15a
      // 113: aload 0
      // 114: aload 3
      // 115: invokespecial net/rim/device/internal/io/file/MetaDataFile.readEntry (Ljava/io/DataInputStream;)[Ljava/lang/Object;
      // 118: astore 27
      // 11a: goto 128
      // 11d: astore 28
      // 11f: bipush 1
      // 120: istore 16
      // 122: aload 26
      // 124: monitorexit
      // 125: goto 18c
      // 128: aload 27
      // 12a: ifnull 15d
      // 12d: new net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 130: dup
      // 131: aconst_null
      // 132: invokespecial net/rim/device/internal/io/file/MetaDataFile$CacheItem.<init> (Lnet/rim/device/internal/io/file/MetaDataFile$1;)V
      // 135: astore 28
      // 137: aload 28
      // 139: aload 27
      // 13b: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._data [Ljava/lang/Object;
      // 13e: aload 28
      // 140: aload 17
      // 142: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._filename Ljava/lang/String;
      // 145: aload 0
      // 146: aload 28
      // 148: invokespecial net/rim/device/internal/io/file/MetaDataFile.insertTail (Lnet/rim/device/internal/io/file/MetaDataFile$CacheItem;)V
      // 14b: aload 0
      // 14c: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 14f: aload 17
      // 151: aload 28
      // 153: invokevirtual net/rim/device/api/util/StringCaseInsensitiveHashtable.put (Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
      // 156: pop
      // 157: goto 15d
      // 15a: bipush 1
      // 15b: istore 25
      // 15d: aload 26
      // 15f: monitorexit
      // 160: goto 16b
      // 163: astore 29
      // 165: aload 26
      // 167: monitorexit
      // 168: aload 29
      // 16a: athrow
      // 16b: iload 25
      // 16d: ifeq 178
      // 170: aload 3
      // 171: iload 21
      // 173: i2l
      // 174: invokevirtual java/io/DataInputStream.skip (J)J
      // 177: pop2
      // 178: aload 0
      // 179: lload 18
      // 17b: lload 23
      // 17d: aload 17
      // 17f: invokespecial net/rim/device/internal/io/file/MetaDataFile.listEntry (JJLjava/lang/String;)V
      // 182: aload 4
      // 184: invokevirtual net/rim/device/internal/io/file/CounterInputStream.getOffset ()J
      // 187: lstore 14
      // 189: goto 09e
      // 18c: iload 13
      // 18e: bipush -1
      // 190: if_icmpne 198
      // 193: iload 16
      // 195: ifeq 1a3
      // 198: aload 0
      // 199: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 19c: lload 14
      // 19e: invokeinterface javax/microedition/io/file/FileConnection.truncate (J)V 3
      // 1a3: new net/rim/device/api/util/StringToIntCaseInsensitiveHashtable
      // 1a6: dup
      // 1a7: aload 0
      // 1a8: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 1ab: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.size ()I
      // 1ae: invokespecial net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.<init> (I)V
      // 1b1: astore 17
      // 1b3: aload 0
      // 1b4: getfield net/rim/device/internal/io/file/MetaDataFile._path Ljava/lang/String;
      // 1b7: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 1ba: checkcast javax/microedition/io/file/FileConnection
      // 1bd: astore 5
      // 1bf: aload 5
      // 1c1: dup
      // 1c2: instanceof net/rim/device/api/io/file/ExtendedFileConnection
      // 1c5: ifne 1cc
      // 1c8: pop
      // 1c9: goto 230
      // 1cc: checkcast net/rim/device/api/io/file/ExtendedFileConnection
      // 1cf: astore 18
      // 1d1: aload 18
      // 1d3: ldc_w "*"
      // 1d6: bipush 0
      // 1d7: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.listWithDetails (Ljava/lang/String;Z)Ljava/util/Enumeration; 3
      // 1dc: astore 19
      // 1de: aload 19
      // 1e0: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 1e5: ifne 1eb
      // 1e8: goto 2f1
      // 1eb: aload 19
      // 1ed: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 1f2: checkcast net/rim/device/api/io/FileInfo
      // 1f5: astore 20
      // 1f7: aload 20
      // 1f9: invokevirtual net/rim/device/api/io/FileInfo.getFileName ()Ljava/lang/String;
      // 1fc: invokestatic net/rim/device/internal/io/file/FileUtilities.encodeString (Ljava/lang/String;)Ljava/lang/String;
      // 1ff: astore 21
      // 201: aload 0
      // 202: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 205: aload 21
      // 207: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.get (Ljava/lang/String;)I
      // 20a: istore 22
      // 20c: iload 22
      // 20e: bipush -1
      // 210: if_icmpeq 1de
      // 213: aload 0
      // 214: getfield net/rim/device/internal/io/file/MetaDataFile._fileModTimes [J
      // 217: iload 22
      // 219: laload
      // 21a: aload 20
      // 21c: invokevirtual net/rim/device/api/io/FileInfo.getLastModified ()J
      // 21f: lcmp
      // 220: ifne 1de
      // 223: aload 17
      // 225: aload 21
      // 227: iload 22
      // 229: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.put (Ljava/lang/String;I)I
      // 22c: pop
      // 22d: goto 1de
      // 230: aload 5
      // 232: invokeinterface javax/microedition/io/file/FileConnection.list ()Ljava/util/Enumeration; 1
      // 237: astore 18
      // 239: aload 18
      // 23b: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 240: ifne 246
      // 243: goto 2f1
      // 246: aload 18
      // 248: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 24d: checkcast java/lang/String
      // 250: invokestatic net/rim/device/internal/io/file/FileUtilities.encodeString (Ljava/lang/String;)Ljava/lang/String;
      // 253: astore 19
      // 255: aload 0
      // 256: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 259: aload 19
      // 25b: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.get (Ljava/lang/String;)I
      // 25e: istore 20
      // 260: iload 20
      // 262: bipush -1
      // 264: if_icmpeq 239
      // 267: aconst_null
      // 268: astore 21
      // 26a: new java/lang/StringBuffer
      // 26d: dup
      // 26e: invokespecial java/lang/StringBuffer.<init> ()V
      // 271: aload 0
      // 272: getfield net/rim/device/internal/io/file/MetaDataFile._path Ljava/lang/String;
      // 275: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 278: aload 19
      // 27a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 27d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 280: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 283: checkcast javax/microedition/io/file/FileConnection
      // 286: astore 21
      // 288: aload 21
      // 28a: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 28f: ifeq 2ae
      // 292: aload 21
      // 294: invokeinterface javax/microedition/io/file/FileConnection.lastModified ()J 1
      // 299: aload 0
      // 29a: getfield net/rim/device/internal/io/file/MetaDataFile._fileModTimes [J
      // 29d: iload 20
      // 29f: laload
      // 2a0: lcmp
      // 2a1: ifne 2ae
      // 2a4: aload 17
      // 2a6: aload 19
      // 2a8: iload 20
      // 2aa: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.put (Ljava/lang/String;I)I
      // 2ad: pop
      // 2ae: aload 21
      // 2b0: ifnull 239
      // 2b3: aload 21
      // 2b5: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2ba: goto 239
      // 2bd: astore 22
      // 2bf: goto 239
      // 2c2: astore 22
      // 2c4: aload 21
      // 2c6: ifnonnull 2cc
      // 2c9: goto 239
      // 2cc: aload 21
      // 2ce: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2d3: goto 239
      // 2d6: astore 22
      // 2d8: goto 239
      // 2db: astore 30
      // 2dd: aload 21
      // 2df: ifnull 2ee
      // 2e2: aload 21
      // 2e4: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2e9: goto 2ee
      // 2ec: astore 31
      // 2ee: aload 30
      // 2f0: athrow
      // 2f1: aload 0
      // 2f2: aload 17
      // 2f4: putfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 2f7: aload 3
      // 2f8: ifnull 304
      // 2fb: aload 3
      // 2fc: invokevirtual java/io/DataInputStream.close ()V
      // 2ff: goto 304
      // 302: astore 7
      // 304: iload 6
      // 306: ifeq 323
      // 309: aload 0
      // 30a: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 30d: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 312: goto 317
      // 315: astore 7
      // 317: aload 0
      // 318: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 31b: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.clear ()V
      // 31e: aload 0
      // 31f: bipush 0
      // 320: putfield net/rim/device/internal/io/file/MetaDataFile._numEntries I
      // 323: aload 5
      // 325: ifnull 3be
      // 328: aload 5
      // 32a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 32f: goto 3be
      // 332: astore 7
      // 334: goto 3be
      // 337: astore 7
      // 339: bipush 1
      // 33a: istore 6
      // 33c: aload 3
      // 33d: ifnull 349
      // 340: aload 3
      // 341: invokevirtual java/io/DataInputStream.close ()V
      // 344: goto 349
      // 347: astore 7
      // 349: iload 6
      // 34b: ifeq 368
      // 34e: aload 0
      // 34f: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 352: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 357: goto 35c
      // 35a: astore 7
      // 35c: aload 0
      // 35d: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 360: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.clear ()V
      // 363: aload 0
      // 364: bipush 0
      // 365: putfield net/rim/device/internal/io/file/MetaDataFile._numEntries I
      // 368: aload 5
      // 36a: ifnull 3be
      // 36d: aload 5
      // 36f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 374: goto 3be
      // 377: astore 7
      // 379: goto 3be
      // 37c: astore 32
      // 37e: aload 3
      // 37f: ifnull 38b
      // 382: aload 3
      // 383: invokevirtual java/io/DataInputStream.close ()V
      // 386: goto 38b
      // 389: astore 33
      // 38b: iload 6
      // 38d: ifeq 3aa
      // 390: aload 0
      // 391: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 394: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 399: goto 39e
      // 39c: astore 33
      // 39e: aload 0
      // 39f: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 3a2: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.clear ()V
      // 3a5: aload 0
      // 3a6: bipush 0
      // 3a7: putfield net/rim/device/internal/io/file/MetaDataFile._numEntries I
      // 3aa: aload 5
      // 3ac: ifnull 3bb
      // 3af: aload 5
      // 3b1: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 3b6: goto 3bb
      // 3b9: astore 33
      // 3bb: aload 32
      // 3bd: athrow
      // 3be: iload 1
      // 3bf: ifeq 3d9
      // 3c2: aload 0
      // 3c3: getfield net/rim/device/internal/io/file/MetaDataFile._numEntries I
      // 3c6: aload 0
      // 3c7: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 3ca: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.size ()I
      // 3cd: isub
      // 3ce: bipush 10
      // 3d0: if_icmple 3d9
      // 3d3: aload 0
      // 3d4: bipush 0
      // 3d5: invokevirtual net/rim/device/internal/io/file/MetaDataFile.compact (Z)Lnet/rim/device/internal/io/file/CounterOutputStream;
      // 3d8: pop
      // 3d9: return
      // try (136 -> 140): 141 null
      // try (125 -> 146): 175 null
      // try (147 -> 174): 175 null
      // try (175 -> 178): 175 null
      // try (316 -> 318): 319 null
      // try (286 -> 314): 321 null
      // try (325 -> 327): 328 null
      // try (286 -> 314): 330 null
      // try (321 -> 322): 330 null
      // try (333 -> 335): 336 null
      // try (330 -> 331): 330 null
      // try (344 -> 346): 347 null
      // try (350 -> 353): 354 null
      // try (363 -> 365): 366 null
      // try (20 -> 342): 368 null
      // try (373 -> 375): 376 null
      // try (379 -> 382): 383 null
      // try (392 -> 394): 395 null
      // try (20 -> 342): 397 null
      // try (368 -> 371): 397 null
      // try (400 -> 402): 403 null
      // try (406 -> 409): 410 null
      // try (419 -> 421): 422 null
      // try (397 -> 398): 397 null
   }

   private final void listEntry(long modTime, long offset, String filename) {
      int numEntries = this._numEntries;
      if (this._fileModTimes.length <= numEntries) {
         Array.extend(this._fileModTimes, 1);
      }

      if (this._fileOffsets.length <= numEntries) {
         Array.extend(this._fileOffsets, 1);
      }

      this._fileModTimes[numEntries] = modTime;
      this._fileOffsets[numEntries] = offset;
      this._fileFilenameToEntry.put(filename, numEntries);
      this._numEntries++;
   }

   private final void writeEntry(String param1, long param2, Object[] param4) {
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
      // 000: aload 0
      // 001: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 004: ifnonnull 00a
      // 007: goto 2ac
      // 00a: bipush 5
      // 00c: istore 5
      // 00e: bipush 0
      // 00f: istore 6
      // 011: iload 6
      // 013: aload 4
      // 015: arraylength
      // 016: if_icmpge 0ae
      // 019: aload 4
      // 01b: iload 6
      // 01d: aaload
      // 01e: astore 7
      // 020: aload 7
      // 022: dup
      // 023: instanceof java/lang/String
      // 026: ifne 02d
      // 029: pop
      // 02a: goto 056
      // 02d: checkcast java/lang/String
      // 030: ldc_w "utf-8"
      // 033: invokevirtual java/lang/String.getBytes (Ljava/lang/String;)[B
      // 036: arraylength
      // 037: bipush 5
      // 039: iadd
      // 03a: istore 8
      // 03c: iload 8
      // 03e: sipush 1000
      // 041: if_icmple 04c
      // 044: new java/lang/IllegalArgumentException
      // 047: dup
      // 048: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 04b: athrow
      // 04c: iload 5
      // 04e: iload 8
      // 050: iadd
      // 051: istore 5
      // 053: goto 0a8
      // 056: aload 7
      // 058: dup
      // 059: instanceof java/lang/Long
      // 05c: ifne 063
      // 05f: pop
      // 060: goto 079
      // 063: checkcast java/lang/Long
      // 066: invokevirtual java/lang/Long.longValue ()J
      // 069: lstore 8
      // 06b: lload 8
      // 06d: bipush 0
      // 06e: i2l
      // 06f: lcmp
      // 070: ifle 0a8
      // 073: iinc 5 13
      // 076: goto 0a8
      // 079: aload 7
      // 07b: dup
      // 07c: instanceof net/rim/device/api/system/EncodedImage
      // 07f: ifne 086
      // 082: pop
      // 083: goto 0a8
      // 086: checkcast net/rim/device/api/system/EncodedImage
      // 089: invokevirtual net/rim/device/api/system/EncodedImage.getLength ()I
      // 08c: bipush 5
      // 08e: iadd
      // 08f: istore 8
      // 091: iload 8
      // 093: ldc_w 37632
      // 096: if_icmple 0a1
      // 099: new java/lang/IllegalArgumentException
      // 09c: dup
      // 09d: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 0a0: athrow
      // 0a1: iload 5
      // 0a3: iload 8
      // 0a5: iadd
      // 0a6: istore 5
      // 0a8: iinc 6 1
      // 0ab: goto 011
      // 0ae: aconst_null
      // 0af: astore 6
      // 0b1: aconst_null
      // 0b2: astore 7
      // 0b4: aload 0
      // 0b5: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 0b8: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 0bd: ifeq 112
      // 0c0: aload 0
      // 0c1: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 0c4: aload 1
      // 0c5: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.remove (Ljava/lang/String;)I
      // 0c8: istore 8
      // 0ca: aload 0
      // 0cb: getfield net/rim/device/internal/io/file/MetaDataFile._numEntries I
      // 0ce: aload 0
      // 0cf: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 0d2: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.size ()I
      // 0d5: isub
      // 0d6: bipush 10
      // 0d8: if_icmple 0f3
      // 0db: aload 0
      // 0dc: bipush 1
      // 0dd: invokevirtual net/rim/device/internal/io/file/MetaDataFile.compact (Z)Lnet/rim/device/internal/io/file/CounterOutputStream;
      // 0e0: astore 6
      // 0e2: aload 6
      // 0e4: ifnonnull 112
      // 0e7: aload 0
      // 0e8: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 0eb: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 0f0: goto 112
      // 0f3: new net/rim/device/internal/io/file/CounterOutputStream
      // 0f6: dup
      // 0f7: aload 0
      // 0f8: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 0fb: ldc_w 2147483647
      // 0fe: i2l
      // 0ff: invokeinterface javax/microedition/io/file/FileConnection.openOutputStream (J)Ljava/io/OutputStream; 3
      // 104: aload 0
      // 105: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 108: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 10d: invokespecial net/rim/device/internal/io/file/CounterOutputStream.<init> (Ljava/io/OutputStream;J)V
      // 110: astore 6
      // 112: aload 6
      // 114: ifnull 125
      // 117: new java/io/DataOutputStream
      // 11a: dup
      // 11b: aload 6
      // 11d: invokespecial java/io/DataOutputStream.<init> (Ljava/io/OutputStream;)V
      // 120: astore 7
      // 122: goto 164
      // 125: aload 0
      // 126: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 129: invokeinterface javax/microedition/io/file/FileConnection.create ()V 1
      // 12e: aload 0
      // 12f: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 132: bipush 1
      // 133: invokeinterface javax/microedition/io/file/FileConnection.setHidden (Z)V 2
      // 138: new net/rim/device/internal/io/file/CounterOutputStream
      // 13b: dup
      // 13c: aload 0
      // 13d: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 140: invokeinterface javax/microedition/io/file/FileConnection.openDataOutputStream ()Ljava/io/DataOutputStream; 1
      // 145: invokespecial net/rim/device/internal/io/file/CounterOutputStream.<init> (Ljava/io/OutputStream;)V
      // 148: astore 6
      // 14a: new java/io/DataOutputStream
      // 14d: dup
      // 14e: aload 6
      // 150: invokespecial java/io/DataOutputStream.<init> (Ljava/io/OutputStream;)V
      // 153: astore 7
      // 155: aload 7
      // 157: ldc_w 604315651
      // 15a: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 15d: aload 7
      // 15f: bipush 7
      // 161: invokevirtual java/io/DataOutputStream.writeByte (I)V
      // 164: aload 7
      // 166: bipush 1
      // 167: invokevirtual java/io/DataOutputStream.writeByte (I)V
      // 16a: aload 7
      // 16c: aload 1
      // 16d: invokevirtual java/io/DataOutputStream.writeUTF (Ljava/lang/String;)V
      // 170: aload 7
      // 172: lload 2
      // 173: invokevirtual java/io/DataOutputStream.writeLong (J)V
      // 176: aload 0
      // 177: invokespecial net/rim/device/internal/io/file/MetaDataFile.filesystemUsesLocalTimestamps ()Z
      // 17a: ifeq 186
      // 17d: invokestatic java/util/TimeZone.getDefault ()Ljava/util/TimeZone;
      // 180: invokevirtual java/util/TimeZone.getRawOffset ()I
      // 183: goto 187
      // 186: bipush 0
      // 187: istore 8
      // 189: aload 7
      // 18b: iload 8
      // 18d: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 190: aload 7
      // 192: iload 5
      // 194: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 197: aload 6
      // 199: invokevirtual net/rim/device/internal/io/file/CounterOutputStream.getOffset ()J
      // 19c: lstore 9
      // 19e: bipush 0
      // 19f: istore 11
      // 1a1: iload 11
      // 1a3: aload 4
      // 1a5: arraylength
      // 1a6: if_icmplt 1ac
      // 1a9: goto 256
      // 1ac: aload 4
      // 1ae: iload 11
      // 1b0: aaload
      // 1b1: astore 12
      // 1b3: aload 12
      // 1b5: dup
      // 1b6: instanceof java/lang/String
      // 1b9: ifne 1c0
      // 1bc: pop
      // 1bd: goto 1e4
      // 1c0: checkcast java/lang/String
      // 1c3: ldc_w "utf-8"
      // 1c6: invokevirtual java/lang/String.getBytes (Ljava/lang/String;)[B
      // 1c9: astore 13
      // 1cb: aload 7
      // 1cd: iload 11
      // 1cf: invokevirtual java/io/DataOutputStream.write (I)V
      // 1d2: aload 7
      // 1d4: aload 13
      // 1d6: arraylength
      // 1d7: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 1da: aload 7
      // 1dc: aload 13
      // 1de: invokevirtual java/io/OutputStream.write ([B)V
      // 1e1: goto 250
      // 1e4: aload 12
      // 1e6: dup
      // 1e7: instanceof java/lang/Long
      // 1ea: ifne 1f1
      // 1ed: pop
      // 1ee: goto 219
      // 1f1: checkcast java/lang/Long
      // 1f4: invokevirtual java/lang/Long.longValue ()J
      // 1f7: lstore 13
      // 1f9: lload 13
      // 1fb: bipush 0
      // 1fc: i2l
      // 1fd: lcmp
      // 1fe: ifle 250
      // 201: aload 7
      // 203: iload 11
      // 205: invokevirtual java/io/DataOutputStream.write (I)V
      // 208: aload 7
      // 20a: bipush 8
      // 20c: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 20f: aload 7
      // 211: lload 13
      // 213: invokevirtual java/io/DataOutputStream.writeLong (J)V
      // 216: goto 250
      // 219: aload 12
      // 21b: dup
      // 21c: instanceof net/rim/device/api/system/EncodedImage
      // 21f: ifne 226
      // 222: pop
      // 223: goto 250
      // 226: checkcast net/rim/device/api/system/EncodedImage
      // 229: astore 13
      // 22b: aload 7
      // 22d: iload 11
      // 22f: invokevirtual java/io/DataOutputStream.write (I)V
      // 232: aload 7
      // 234: aload 13
      // 236: invokevirtual net/rim/device/api/system/EncodedImage.getLength ()I
      // 239: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 23c: aload 7
      // 23e: aload 13
      // 240: invokevirtual net/rim/device/api/system/EncodedImage.getData ()[B
      // 243: aload 13
      // 245: invokevirtual net/rim/device/api/system/EncodedImage.getOffset ()I
      // 248: aload 13
      // 24a: invokevirtual net/rim/device/api/system/EncodedImage.getLength ()I
      // 24d: invokevirtual java/io/DataOutputStream.write ([BII)V
      // 250: iinc 11 1
      // 253: goto 1a1
      // 256: aload 7
      // 258: sipush 255
      // 25b: invokevirtual java/io/DataOutputStream.write (I)V
      // 25e: aload 7
      // 260: bipush 0
      // 261: invokevirtual java/io/DataOutputStream.writeInt (I)V
      // 264: aload 0
      // 265: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 268: aload 6
      // 26a: invokevirtual net/rim/device/internal/io/file/CounterOutputStream.getOffset ()J
      // 26d: invokeinterface javax/microedition/io/file/FileConnection.truncate (J)V 3
      // 272: aload 0
      // 273: lload 2
      // 274: lload 9
      // 276: aload 1
      // 277: invokespecial net/rim/device/internal/io/file/MetaDataFile.listEntry (JJLjava/lang/String;)V
      // 27a: aload 7
      // 27c: ifnull 2ac
      // 27f: aload 7
      // 281: invokevirtual java/io/DataOutputStream.close ()V
      // 284: return
      // 285: astore 8
      // 287: return
      // 288: astore 8
      // 28a: aload 7
      // 28c: ifnull 2ac
      // 28f: aload 7
      // 291: invokevirtual java/io/DataOutputStream.close ()V
      // 294: return
      // 295: astore 8
      // 297: return
      // 298: astore 15
      // 29a: aload 7
      // 29c: ifnull 2a9
      // 29f: aload 7
      // 2a1: invokevirtual java/io/DataOutputStream.close ()V
      // 2a4: goto 2a9
      // 2a7: astore 16
      // 2a9: aload 15
      // 2ab: athrow
      // 2ac: return
      // try (282 -> 284): 285 null
      // try (85 -> 280): 287 null
      // try (290 -> 292): 293 null
      // try (85 -> 280): 295 null
      // try (287 -> 288): 295 null
      // try (298 -> 300): 301 null
      // try (295 -> 296): 295 null
   }

   private final Object[] readEntry(DataInputStream in) throws IOException {
      if (this._metaDataDatabase == null) {
         return null;
      }

      Object[] object = new Object[8];
      boolean reachedEnd = false;

      while (!reachedEnd) {
         int type = in.readByte() & 255;
         int length = in.readInt();
         switch (type) {
            case 0:
               if (0 <= length && length <= 37632) {
                  byte[] data = new byte[length];
                  in.readFully(data);
                  object[type] = EncodedImage.createEncodedImage(data, 0, data.length);
                  break;
               }

               throw new IOException();
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
               if (0 <= length && length <= 1000) {
                  byte[] data = new byte[length];
                  in.readFully(data);
                  object[type] = new String(data, "UTF-8");
                  break;
               }

               throw new IOException();
            case 2:
            case 7:
               if (length != 8) {
                  throw new IOException();
               }

               object[type] = new Long(in.readLong());
               break;
            case 255:
               reachedEnd = true;
            default:
               in.skip(length);
         }
      }

      for (int i = 0; i < 8; i++) {
         if (object[i] == null) {
            object[i] = NO_DATA_AVAILABLE;
         }
      }

      return object;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final CounterOutputStream compact(boolean forWrite) {
      CounterOutputStream outCounter = null;
      if (this._metaDataDatabase != null) {
         DataInputStream in = null;
         boolean eof = false;
         boolean errored = false;
         boolean var212 = false /* VF: Semaphore variable */;
         boolean var227 = false /* VF: Semaphore variable */;

         label2112: {
            try {
               label2073:
               try {
                  var227 = true;
                  var212 = true;
                  CounterInputStream e = new CounterInputStream(this._metaDataDatabase.openDataInputStream());
                  in = new DataInputStream(e);
                  in.readInt();
                  in.readByte();
                  StringToIntCaseInsensitiveHashtable validEntries = this._fileFilenameToEntry;
                  long[] validOffsets = this._fileOffsets;
                  long[] validModTimes = this._fileModTimes;
                  this._fileFilenameToEntry = new StringToIntCaseInsensitiveHashtable();
                  int maxEntries = validEntries.size();
                  this._fileOffsets = new long[maxEntries];
                  this._fileModTimes = new long[maxEntries];
                  this._numEntries = 0;

                  long lastRecordOffset;
                  do {
                     lastRecordOffset = e.getOffset();
                     if (in.read() != 1) {
                        eof = true;
                        break;
                     }

                     String filename = in.readUTF();
                     long fileModTime = in.readLong();
                     in.readInt();
                     int dataSize = in.readInt();
                     int validIndex = validEntries.get(filename);
                     long offset = e.getOffset();
                     in.skip(dataSize);
                     if (validIndex < 0 || offset != validOffsets[validIndex]) {
                        break;
                     }

                     this.listEntry(validModTimes[validIndex], offset, filename);
                  } while (this._numEntries < maxEntries);

                  outCounter = new CounterOutputStream(this._metaDataDatabase.openOutputStream(lastRecordOffset), lastRecordOffset);
                  DataOutputStream out = new DataOutputStream(outCounter);
                  if (this._numEntries < maxEntries) {
                     if (eof) {
                        var212 = false;
                        var227 = false;
                        break label2112;
                     }

                     byte[] tempData = new byte[0];

                     while (in.read() == 1) {
                        String filename = in.readUTF();
                        long fileModTime = in.readLong();
                        int saveModTimeAdjustment = in.readInt();
                        int dataSize = in.readInt();
                        int validIndex = validEntries.get(filename);
                        long offset = e.getOffset();
                        if (validIndex >= 0 && offset == validOffsets[validIndex]) {
                           if (dataSize > tempData.length) {
                              Array.resize(tempData, dataSize);
                           }

                           in.readFully(tempData, 0, dataSize);
                           out.writeByte(1);
                           out.writeUTF(filename);
                           out.writeLong(fileModTime);
                           out.writeInt(saveModTimeAdjustment);
                           out.writeInt(dataSize);
                           long fileOffset = outCounter.getOffset();
                           out.write(tempData, 0, dataSize);
                           this.listEntry(validModTimes[validIndex], fileOffset, filename);
                           if (this._numEntries == maxEntries) {
                              var212 = false;
                              var227 = false;
                              break label2112;
                           }
                        } else {
                           in.skip(dataSize);
                        }
                     }

                     var212 = false;
                     var227 = false;
                     break label2112;
                  }

                  var212 = false;
                  var227 = false;
                  break label2112;
               } finally {
                  if (var227) {
                     errored = true;
                     var212 = false;
                     break label2073;
                  }
               }
            } finally {
               if (var212) {
                  if (in != null) {
                     label1985:
                     try {
                        in.close();
                     } finally {
                        break label1985;
                     }
                  }

                  long truncationOffset = 0;
                  if (outCounter != null && (!forWrite || errored)) {
                     truncationOffset = outCounter.getOffset();

                     label1975:
                     try {
                        outCounter.close();
                     } finally {
                        break label1975;
                     }

                     CounterOutputStream var242 = null;
                  }

                  if (errored) {
                     truncationOffset = 0;

                     label1969:
                     try {
                        this._metaDataDatabase.delete();
                     } finally {
                        break label1969;
                     }

                     this.readDatabase(false, true);
                  }

                  if (truncationOffset > 0) {
                     label1965:
                     try {
                        this._metaDataDatabase.truncate(truncationOffset);
                     } finally {
                        break label1965;
                     }
                  }
               }
            }

            if (in != null) {
               label2013:
               try {
                  in.close();
               } finally {
                  break label2013;
               }
            }

            long truncationOffset = 0;
            if (outCounter != null && (!forWrite || errored)) {
               truncationOffset = outCounter.getOffset();

               label2005:
               try {
                  outCounter.close();
               } finally {
                  break label2005;
               }

               outCounter = null;
            }

            if (errored) {
               truncationOffset = 0;

               label1997:
               try {
                  this._metaDataDatabase.delete();
               } finally {
                  break label1997;
               }

               this.readDatabase(false, true);
            }

            if (truncationOffset > 0) {
               try {
                  this._metaDataDatabase.truncate(truncationOffset);
               } finally {
                  return outCounter;
               }
            }

            return outCounter;
         }

         if (in != null) {
            label2040:
            try {
               in.close();
            } finally {
               break label2040;
            }
         }

         long truncationOffset = 0;
         if (outCounter != null && (!forWrite || errored)) {
            truncationOffset = outCounter.getOffset();

            label2032:
            try {
               outCounter.close();
            } finally {
               break label2032;
            }

            outCounter = null;
         }

         if (errored) {
            truncationOffset = 0;

            label2024:
            try {
               this._metaDataDatabase.delete();
            } finally {
               break label2024;
            }

            this.readDatabase(false, true);
         }

         if (truncationOffset > 0) {
            try {
               this._metaDataDatabase.truncate(truncationOffset);
            } finally {
               return outCounter;
            }
         }
      }

      return outCounter;
   }

   final synchronized int removeEntry(String fileName) {
      this._fileFilenameToEntry.remove(fileName);
      return this._fileFilenameToEntry.size();
   }

   public final void setCacheSize(int size) {
      if (size <= 0) {
         throw new IllegalArgumentException();
      }

      synchronized (this._cache) {
         this._maxCacheSize = size;

         while (this._cacheSize > this._maxCacheSize) {
            MetaDataFile$CacheItem item = this.removeHead();
            this._cache.remove(item._filename);
         }
      }
   }

   private final void insertTail(MetaDataFile$CacheItem node) {
      if (this._tail != null) {
         this._tail._next = node;
         node._prev = this._tail;
         this._tail = node;
      } else {
         this._head = this._tail = node;
         node._prev = null;
      }

      node._next = null;
      this._cacheSize++;
   }

   private final MetaDataFile$CacheItem removeHead() {
      if (this._head == null) {
         return null;
      }

      MetaDataFile$CacheItem oldNode = this._head;
      if (this._tail == oldNode) {
         this._head = this._tail = null;
      } else {
         this._head = oldNode._next;
         this._head._prev = null;
      }

      oldNode._next = null;
      oldNode._prev = null;
      this._cacheSize--;
      return oldNode;
   }

   private final void removeItem(MetaDataFile$CacheItem item) {
      if (item._prev != null) {
         item._prev._next = item._next;
      }

      if (item._next != null) {
         item._next._prev = item._prev;
      }

      if (this._head == item) {
         this._head = item._next;
      }

      if (this._tail == item) {
         this._tail = item._prev;
      }

      this._cacheSize--;
   }

   public final void purgeMetadataFromCache(String filename) {
      synchronized (this._cache) {
         for (MetaDataFile$CacheItem cur = this._head; cur != null; cur = cur._next) {
            if (cur._filename.equals(filename)) {
               this.removeItem(cur);
               this._cache.remove(filename);
               break;
            }
         }
      }
   }

   public final Object getMetadataFromCache(String filename, int metaDataType) {
      if (metaDataType >= 0 && metaDataType < 8) {
         synchronized (this._cache) {
            MetaDataFile$CacheItem result = (MetaDataFile$CacheItem)this._cache.get(filename);
            if (result != null) {
               if (this._tail != result) {
                  this.removeItem(result);
                  this.insertTail(result);
               }

               return result._data[metaDataType];
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   public final Object getOrCreateMetadata(String filename, int metaDataType) {
      if (metaDataType >= 0 && metaDataType < 8) {
         Object obj = this.getMetadataFromCache(filename, metaDataType);
         if (obj != null) {
            return obj;
         }

         Object[] metaDataObjects = this.getOrCreateMetadata(filename, false);
         return metaDataObjects == null ? NO_DATA_AVAILABLE : metaDataObjects[metaDataType];
      } else {
         return null;
      }
   }

   public final Object[] getOrCreateMetadata(String param1, boolean param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 3
      // 002: aconst_null
      // 003: astore 4
      // 005: new java/lang/StringBuffer
      // 008: dup
      // 009: invokespecial java/lang/StringBuffer.<init> ()V
      // 00c: aload 0
      // 00d: getfield net/rim/device/internal/io/file/MetaDataFile._path Ljava/lang/String;
      // 010: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 013: aload 1
      // 014: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 017: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 01a: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 01d: checkcast javax/microedition/io/file/FileConnection
      // 020: astore 3
      // 021: aload 0
      // 022: aload 0
      // 023: astore 5
      // 025: monitorenter
      // 026: aload 0
      // 027: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 02a: ifnonnull 033
      // 02d: aload 0
      // 02e: iload 2
      // 02f: invokespecial net/rim/device/internal/io/file/MetaDataFile.openMetaDataDatabase (Z)Z
      // 032: pop
      // 033: aload 0
      // 034: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 037: aload 1
      // 038: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.get (Ljava/lang/String;)I
      // 03b: istore 6
      // 03d: iload 6
      // 03f: bipush -1
      // 041: if_icmpne 047
      // 044: goto 182
      // 047: aload 0
      // 048: getfield net/rim/device/internal/io/file/MetaDataFile._fileModTimes [J
      // 04b: iload 6
      // 04d: laload
      // 04e: aload 3
      // 04f: invokeinterface javax/microedition/io/file/FileConnection.lastModified ()J 1
      // 054: lcmp
      // 055: ifeq 05b
      // 058: goto 182
      // 05b: aload 0
      // 05c: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 05f: ifnonnull 065
      // 062: goto 182
      // 065: iload 2
      // 066: ifne 06c
      // 069: goto 125
      // 06c: getstatic net/rim/device/internal/io/file/MetaDataFile.EMPTY_META_DATA [Ljava/lang/Object;
      // 06f: astore 7
      // 071: aload 5
      // 073: monitorexit
      // 074: iload 2
      // 075: ifeq 07b
      // 078: goto 113
      // 07b: aload 4
      // 07d: astore 16
      // 07f: aload 16
      // 081: ifnonnull 08c
      // 084: getstatic net/rim/device/internal/io/file/MetaDataFile.EMPTY_META_DATA [Ljava/lang/Object;
      // 087: astore 16
      // 089: goto 0ac
      // 08c: bipush 0
      // 08d: istore 17
      // 08f: iload 17
      // 091: bipush 8
      // 093: if_icmpge 0ac
      // 096: aload 16
      // 098: iload 17
      // 09a: aaload
      // 09b: ifnonnull 0a6
      // 09e: aload 16
      // 0a0: iload 17
      // 0a2: getstatic net/rim/device/internal/io/file/MetaDataFile.NO_DATA_AVAILABLE Ljava/lang/Object;
      // 0a5: aastore
      // 0a6: iinc 17 1
      // 0a9: goto 08f
      // 0ac: aload 0
      // 0ad: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 0b0: dup
      // 0b1: astore 17
      // 0b3: monitorenter
      // 0b4: aload 0
      // 0b5: getfield net/rim/device/internal/io/file/MetaDataFile._cacheSize I
      // 0b8: aload 0
      // 0b9: getfield net/rim/device/internal/io/file/MetaDataFile._maxCacheSize I
      // 0bc: if_icmplt 0dd
      // 0bf: aload 0
      // 0c0: invokespecial net/rim/device/internal/io/file/MetaDataFile.removeHead ()Lnet/rim/device/internal/io/file/MetaDataFile$CacheItem;
      // 0c3: astore 18
      // 0c5: aload 18
      // 0c7: instanceof net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 0ca: ifeq 0dd
      // 0cd: aload 0
      // 0ce: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 0d1: aload 18
      // 0d3: checkcast net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 0d6: getfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._filename Ljava/lang/String;
      // 0d9: invokevirtual net/rim/device/api/util/StringCaseInsensitiveHashtable.remove (Ljava/lang/String;)Ljava/lang/Object;
      // 0dc: pop
      // 0dd: new net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 0e0: dup
      // 0e1: aconst_null
      // 0e2: invokespecial net/rim/device/internal/io/file/MetaDataFile$CacheItem.<init> (Lnet/rim/device/internal/io/file/MetaDataFile$1;)V
      // 0e5: astore 18
      // 0e7: aload 18
      // 0e9: aload 16
      // 0eb: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._data [Ljava/lang/Object;
      // 0ee: aload 18
      // 0f0: aload 1
      // 0f1: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._filename Ljava/lang/String;
      // 0f4: aload 0
      // 0f5: aload 18
      // 0f7: invokespecial net/rim/device/internal/io/file/MetaDataFile.insertTail (Lnet/rim/device/internal/io/file/MetaDataFile$CacheItem;)V
      // 0fa: aload 0
      // 0fb: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 0fe: aload 1
      // 0ff: aload 18
      // 101: invokevirtual net/rim/device/api/util/StringCaseInsensitiveHashtable.put (Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
      // 104: pop
      // 105: aload 17
      // 107: monitorexit
      // 108: goto 113
      // 10b: astore 19
      // 10d: aload 17
      // 10f: monitorexit
      // 110: aload 19
      // 112: athrow
      // 113: aload 3
      // 114: ifnull 122
      // 117: aload 3
      // 118: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 11d: goto 122
      // 120: astore 16
      // 122: aload 7
      // 124: areturn
      // 125: aconst_null
      // 126: astore 7
      // 128: aload 0
      // 129: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 12c: invokeinterface javax/microedition/io/file/FileConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 131: astore 7
      // 133: aload 7
      // 135: aload 0
      // 136: getfield net/rim/device/internal/io/file/MetaDataFile._fileOffsets [J
      // 139: iload 6
      // 13b: laload
      // 13c: invokevirtual java/io/DataInputStream.skip (J)J
      // 13f: pop2
      // 140: aload 0
      // 141: aload 7
      // 143: invokespecial net/rim/device/internal/io/file/MetaDataFile.readEntry (Ljava/io/DataInputStream;)[Ljava/lang/Object;
      // 146: astore 4
      // 148: aload 7
      // 14a: ifnull 182
      // 14d: aload 7
      // 14f: invokevirtual java/io/DataInputStream.close ()V
      // 152: goto 182
      // 155: astore 8
      // 157: goto 182
      // 15a: astore 8
      // 15c: aload 7
      // 15e: ifnull 182
      // 161: aload 7
      // 163: invokevirtual java/io/DataInputStream.close ()V
      // 166: goto 182
      // 169: astore 8
      // 16b: goto 182
      // 16e: astore 9
      // 170: aload 7
      // 172: ifnull 17f
      // 175: aload 7
      // 177: invokevirtual java/io/DataInputStream.close ()V
      // 17a: goto 17f
      // 17d: astore 10
      // 17f: aload 9
      // 181: athrow
      // 182: aload 4
      // 184: ifnull 18a
      // 187: goto 2aa
      // 18a: aload 1
      // 18b: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 18e: astore 7
      // 190: getstatic net/rim/device/internal/io/file/MetaDataFile._tempData Ljava/lang/ref/WeakReference;
      // 193: ifnull 1a5
      // 196: getstatic net/rim/device/internal/io/file/MetaDataFile._tempData Ljava/lang/ref/WeakReference;
      // 199: invokevirtual java/lang/ref/Reference.get ()Ljava/lang/Object;
      // 19c: checkcast [B
      // 19f: dup
      // 1a0: astore 8
      // 1a2: ifnonnull 1b8
      // 1a5: sipush 1024
      // 1a8: newarray 8
      // 1aa: astore 8
      // 1ac: new java/lang/ref/WeakReference
      // 1af: dup
      // 1b0: aload 8
      // 1b2: invokespecial java/lang/ref/WeakReference.<init> (Ljava/lang/Object;)V
      // 1b5: putstatic net/rim/device/internal/io/file/MetaDataFile._tempData Ljava/lang/ref/WeakReference;
      // 1b8: aload 8
      // 1ba: dup
      // 1bb: astore 9
      // 1bd: monitorenter
      // 1be: aload 7
      // 1c0: invokestatic net/rim/device/internal/io/file/MetaDataProvider.getProvider (Ljava/lang/String;)Lnet/rim/device/internal/io/file/MetaDataProvider;
      // 1c3: astore 10
      // 1c5: aload 10
      // 1c7: ifnull 1dc
      // 1ca: aload 10
      // 1cc: aload 3
      // 1cd: aconst_null
      // 1ce: aload 8
      // 1d0: bipush 112
      // 1d2: bipush 84
      // 1d4: bipush 8
      // 1d6: aconst_null
      // 1d7: invokevirtual net/rim/device/internal/io/file/MetaDataProvider.getMetaData (Ljavax/microedition/io/file/FileConnection;Lnet/rim/device/api/system/EncodedImage;[BIII[Ljava/lang/Object;)[Ljava/lang/Object;
      // 1da: astore 4
      // 1dc: aload 9
      // 1de: monitorexit
      // 1df: goto 1ea
      // 1e2: astore 11
      // 1e4: aload 9
      // 1e6: monitorexit
      // 1e7: aload 11
      // 1e9: athrow
      // 1ea: aload 4
      // 1ec: ifnonnull 1f2
      // 1ef: goto 2aa
      // 1f2: aload 0
      // 1f3: aload 1
      // 1f4: aload 3
      // 1f5: invokeinterface javax/microedition/io/file/FileConnection.lastModified ()J 1
      // 1fa: aload 4
      // 1fc: invokespecial net/rim/device/internal/io/file/MetaDataFile.writeEntry (Ljava/lang/String;J[Ljava/lang/Object;)V
      // 1ff: aload 0
      // 200: getfield net/rim/device/internal/io/file/MetaDataFile._path Ljava/lang/String;
      // 203: invokestatic net/rim/device/internal/io/file/FileUtilities.getRoot (Ljava/lang/String;)Ljava/lang/String;
      // 206: astore 9
      // 208: aload 0
      // 209: getfield net/rim/device/internal/io/file/MetaDataFile._path Ljava/lang/String;
      // 20c: bipush 7
      // 20e: aload 9
      // 210: invokevirtual java/lang/String.length ()I
      // 213: iadd
      // 214: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 217: astore 10
      // 219: aload 3
      // 21a: invokestatic net/rim/device/internal/io/file/MetaDataFile.isEncrypted (Ljavax/microedition/io/file/FileConnection;)Z
      // 21d: ifne 223
      // 220: goto 2aa
      // 223: aload 0
      // 224: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 227: ifnonnull 22d
      // 22a: goto 2aa
      // 22d: aload 0
      // 22e: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 231: invokestatic net/rim/device/internal/io/file/MetaDataFile.isEncrypted (Ljavax/microedition/io/file/FileConnection;)Z
      // 234: ifne 2aa
      // 237: aload 10
      // 239: invokestatic net/rim/device/internal/io/file/FileSystemOptions.isExternalEncryptionRequired (Ljava/lang/String;)Z
      // 23c: ifeq 2aa
      // 23f: aload 0
      // 240: getfield net/rim/device/internal/io/file/MetaDataFile._path Ljava/lang/String;
      // 243: invokestatic net/rim/device/internal/io/file/FileUtilities.isRemovable (Ljava/lang/String;)Z
      // 246: ifeq 2aa
      // 249: aload 0
      // 24a: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 24d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 252: aload 0
      // 253: aconst_null
      // 254: putfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 257: new java/lang/StringBuffer
      // 25a: dup
      // 25b: invokespecial java/lang/StringBuffer.<init> ()V
      // 25e: aload 0
      // 25f: getfield net/rim/device/internal/io/file/MetaDataFile._path Ljava/lang/String;
      // 262: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 265: getstatic net/rim/device/internal/io/file/MetaDataFile.THUMBS_DB_FILE Ljava/lang/String;
      // 268: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 26b: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 26e: astore 11
      // 270: new java/lang/StringBuffer
      // 273: dup
      // 274: invokespecial java/lang/StringBuffer.<init> ()V
      // 277: aload 0
      // 278: getfield net/rim/device/internal/io/file/MetaDataFile._path Ljava/lang/String;
      // 27b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 27e: getstatic net/rim/device/internal/io/file/MetaDataFile.TEMP_DB_FILE Ljava/lang/String;
      // 281: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 284: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 287: astore 12
      // 289: aload 11
      // 28b: getstatic net/rim/device/internal/io/file/MetaDataFile.TEMP_DB_FILE Ljava/lang/String;
      // 28e: bipush 1
      // 28f: invokestatic net/rim/device/internal/io/file/FileUtilities.rename (Ljava/lang/String;Ljava/lang/String;Z)V
      // 292: aload 12
      // 294: aload 11
      // 296: invokestatic net/rim/device/internal/io/file/FileUtilities.copyFile (Ljava/lang/String;Ljava/lang/String;)V
      // 299: aload 12
      // 29b: invokestatic net/rim/device/internal/io/file/FileUtilities.delete (Ljava/lang/String;)V
      // 29e: aload 0
      // 29f: aload 11
      // 2a1: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 2a4: checkcast javax/microedition/io/file/FileConnection
      // 2a7: putfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 2aa: aload 5
      // 2ac: monitorexit
      // 2ad: goto 2b8
      // 2b0: astore 13
      // 2b2: aload 5
      // 2b4: monitorexit
      // 2b5: aload 13
      // 2b7: athrow
      // 2b8: iload 2
      // 2b9: ifeq 2bf
      // 2bc: goto 357
      // 2bf: aload 4
      // 2c1: astore 16
      // 2c3: aload 16
      // 2c5: ifnonnull 2d0
      // 2c8: getstatic net/rim/device/internal/io/file/MetaDataFile.EMPTY_META_DATA [Ljava/lang/Object;
      // 2cb: astore 16
      // 2cd: goto 2f0
      // 2d0: bipush 0
      // 2d1: istore 17
      // 2d3: iload 17
      // 2d5: bipush 8
      // 2d7: if_icmpge 2f0
      // 2da: aload 16
      // 2dc: iload 17
      // 2de: aaload
      // 2df: ifnonnull 2ea
      // 2e2: aload 16
      // 2e4: iload 17
      // 2e6: getstatic net/rim/device/internal/io/file/MetaDataFile.NO_DATA_AVAILABLE Ljava/lang/Object;
      // 2e9: aastore
      // 2ea: iinc 17 1
      // 2ed: goto 2d3
      // 2f0: aload 0
      // 2f1: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 2f4: dup
      // 2f5: astore 17
      // 2f7: monitorenter
      // 2f8: aload 0
      // 2f9: getfield net/rim/device/internal/io/file/MetaDataFile._cacheSize I
      // 2fc: aload 0
      // 2fd: getfield net/rim/device/internal/io/file/MetaDataFile._maxCacheSize I
      // 300: if_icmplt 321
      // 303: aload 0
      // 304: invokespecial net/rim/device/internal/io/file/MetaDataFile.removeHead ()Lnet/rim/device/internal/io/file/MetaDataFile$CacheItem;
      // 307: astore 18
      // 309: aload 18
      // 30b: instanceof net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 30e: ifeq 321
      // 311: aload 0
      // 312: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 315: aload 18
      // 317: checkcast net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 31a: getfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._filename Ljava/lang/String;
      // 31d: invokevirtual net/rim/device/api/util/StringCaseInsensitiveHashtable.remove (Ljava/lang/String;)Ljava/lang/Object;
      // 320: pop
      // 321: new net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 324: dup
      // 325: aconst_null
      // 326: invokespecial net/rim/device/internal/io/file/MetaDataFile$CacheItem.<init> (Lnet/rim/device/internal/io/file/MetaDataFile$1;)V
      // 329: astore 18
      // 32b: aload 18
      // 32d: aload 16
      // 32f: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._data [Ljava/lang/Object;
      // 332: aload 18
      // 334: aload 1
      // 335: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._filename Ljava/lang/String;
      // 338: aload 0
      // 339: aload 18
      // 33b: invokespecial net/rim/device/internal/io/file/MetaDataFile.insertTail (Lnet/rim/device/internal/io/file/MetaDataFile$CacheItem;)V
      // 33e: aload 0
      // 33f: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 342: aload 1
      // 343: aload 18
      // 345: invokevirtual net/rim/device/api/util/StringCaseInsensitiveHashtable.put (Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
      // 348: pop
      // 349: aload 17
      // 34b: monitorexit
      // 34c: goto 357
      // 34f: astore 19
      // 351: aload 17
      // 353: monitorexit
      // 354: aload 19
      // 356: athrow
      // 357: aload 3
      // 358: ifnonnull 35e
      // 35b: goto 641
      // 35e: aload 3
      // 35f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 364: goto 641
      // 367: astore 16
      // 369: goto 641
      // 36c: astore 5
      // 36e: iload 2
      // 36f: ifeq 375
      // 372: goto 40d
      // 375: aload 4
      // 377: astore 16
      // 379: aload 16
      // 37b: ifnonnull 386
      // 37e: getstatic net/rim/device/internal/io/file/MetaDataFile.EMPTY_META_DATA [Ljava/lang/Object;
      // 381: astore 16
      // 383: goto 3a6
      // 386: bipush 0
      // 387: istore 17
      // 389: iload 17
      // 38b: bipush 8
      // 38d: if_icmpge 3a6
      // 390: aload 16
      // 392: iload 17
      // 394: aaload
      // 395: ifnonnull 3a0
      // 398: aload 16
      // 39a: iload 17
      // 39c: getstatic net/rim/device/internal/io/file/MetaDataFile.NO_DATA_AVAILABLE Ljava/lang/Object;
      // 39f: aastore
      // 3a0: iinc 17 1
      // 3a3: goto 389
      // 3a6: aload 0
      // 3a7: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 3aa: dup
      // 3ab: astore 17
      // 3ad: monitorenter
      // 3ae: aload 0
      // 3af: getfield net/rim/device/internal/io/file/MetaDataFile._cacheSize I
      // 3b2: aload 0
      // 3b3: getfield net/rim/device/internal/io/file/MetaDataFile._maxCacheSize I
      // 3b6: if_icmplt 3d7
      // 3b9: aload 0
      // 3ba: invokespecial net/rim/device/internal/io/file/MetaDataFile.removeHead ()Lnet/rim/device/internal/io/file/MetaDataFile$CacheItem;
      // 3bd: astore 18
      // 3bf: aload 18
      // 3c1: instanceof net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 3c4: ifeq 3d7
      // 3c7: aload 0
      // 3c8: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 3cb: aload 18
      // 3cd: checkcast net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 3d0: getfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._filename Ljava/lang/String;
      // 3d3: invokevirtual net/rim/device/api/util/StringCaseInsensitiveHashtable.remove (Ljava/lang/String;)Ljava/lang/Object;
      // 3d6: pop
      // 3d7: new net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 3da: dup
      // 3db: aconst_null
      // 3dc: invokespecial net/rim/device/internal/io/file/MetaDataFile$CacheItem.<init> (Lnet/rim/device/internal/io/file/MetaDataFile$1;)V
      // 3df: astore 18
      // 3e1: aload 18
      // 3e3: aload 16
      // 3e5: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._data [Ljava/lang/Object;
      // 3e8: aload 18
      // 3ea: aload 1
      // 3eb: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._filename Ljava/lang/String;
      // 3ee: aload 0
      // 3ef: aload 18
      // 3f1: invokespecial net/rim/device/internal/io/file/MetaDataFile.insertTail (Lnet/rim/device/internal/io/file/MetaDataFile$CacheItem;)V
      // 3f4: aload 0
      // 3f5: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 3f8: aload 1
      // 3f9: aload 18
      // 3fb: invokevirtual net/rim/device/api/util/StringCaseInsensitiveHashtable.put (Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
      // 3fe: pop
      // 3ff: aload 17
      // 401: monitorexit
      // 402: goto 40d
      // 405: astore 19
      // 407: aload 17
      // 409: monitorexit
      // 40a: aload 19
      // 40c: athrow
      // 40d: aload 3
      // 40e: ifnonnull 414
      // 411: goto 641
      // 414: aload 3
      // 415: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 41a: goto 641
      // 41d: astore 16
      // 41f: goto 641
      // 422: astore 5
      // 424: iload 2
      // 425: ifeq 42b
      // 428: goto 4c3
      // 42b: aload 4
      // 42d: astore 16
      // 42f: aload 16
      // 431: ifnonnull 43c
      // 434: getstatic net/rim/device/internal/io/file/MetaDataFile.EMPTY_META_DATA [Ljava/lang/Object;
      // 437: astore 16
      // 439: goto 45c
      // 43c: bipush 0
      // 43d: istore 17
      // 43f: iload 17
      // 441: bipush 8
      // 443: if_icmpge 45c
      // 446: aload 16
      // 448: iload 17
      // 44a: aaload
      // 44b: ifnonnull 456
      // 44e: aload 16
      // 450: iload 17
      // 452: getstatic net/rim/device/internal/io/file/MetaDataFile.NO_DATA_AVAILABLE Ljava/lang/Object;
      // 455: aastore
      // 456: iinc 17 1
      // 459: goto 43f
      // 45c: aload 0
      // 45d: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 460: dup
      // 461: astore 17
      // 463: monitorenter
      // 464: aload 0
      // 465: getfield net/rim/device/internal/io/file/MetaDataFile._cacheSize I
      // 468: aload 0
      // 469: getfield net/rim/device/internal/io/file/MetaDataFile._maxCacheSize I
      // 46c: if_icmplt 48d
      // 46f: aload 0
      // 470: invokespecial net/rim/device/internal/io/file/MetaDataFile.removeHead ()Lnet/rim/device/internal/io/file/MetaDataFile$CacheItem;
      // 473: astore 18
      // 475: aload 18
      // 477: instanceof net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 47a: ifeq 48d
      // 47d: aload 0
      // 47e: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 481: aload 18
      // 483: checkcast net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 486: getfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._filename Ljava/lang/String;
      // 489: invokevirtual net/rim/device/api/util/StringCaseInsensitiveHashtable.remove (Ljava/lang/String;)Ljava/lang/Object;
      // 48c: pop
      // 48d: new net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 490: dup
      // 491: aconst_null
      // 492: invokespecial net/rim/device/internal/io/file/MetaDataFile$CacheItem.<init> (Lnet/rim/device/internal/io/file/MetaDataFile$1;)V
      // 495: astore 18
      // 497: aload 18
      // 499: aload 16
      // 49b: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._data [Ljava/lang/Object;
      // 49e: aload 18
      // 4a0: aload 1
      // 4a1: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._filename Ljava/lang/String;
      // 4a4: aload 0
      // 4a5: aload 18
      // 4a7: invokespecial net/rim/device/internal/io/file/MetaDataFile.insertTail (Lnet/rim/device/internal/io/file/MetaDataFile$CacheItem;)V
      // 4aa: aload 0
      // 4ab: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 4ae: aload 1
      // 4af: aload 18
      // 4b1: invokevirtual net/rim/device/api/util/StringCaseInsensitiveHashtable.put (Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
      // 4b4: pop
      // 4b5: aload 17
      // 4b7: monitorexit
      // 4b8: goto 4c3
      // 4bb: astore 19
      // 4bd: aload 17
      // 4bf: monitorexit
      // 4c0: aload 19
      // 4c2: athrow
      // 4c3: aload 3
      // 4c4: ifnonnull 4ca
      // 4c7: goto 641
      // 4ca: aload 3
      // 4cb: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 4d0: goto 641
      // 4d3: astore 16
      // 4d5: goto 641
      // 4d8: astore 5
      // 4da: iload 2
      // 4db: ifeq 4e1
      // 4de: goto 579
      // 4e1: aload 4
      // 4e3: astore 16
      // 4e5: aload 16
      // 4e7: ifnonnull 4f2
      // 4ea: getstatic net/rim/device/internal/io/file/MetaDataFile.EMPTY_META_DATA [Ljava/lang/Object;
      // 4ed: astore 16
      // 4ef: goto 512
      // 4f2: bipush 0
      // 4f3: istore 17
      // 4f5: iload 17
      // 4f7: bipush 8
      // 4f9: if_icmpge 512
      // 4fc: aload 16
      // 4fe: iload 17
      // 500: aaload
      // 501: ifnonnull 50c
      // 504: aload 16
      // 506: iload 17
      // 508: getstatic net/rim/device/internal/io/file/MetaDataFile.NO_DATA_AVAILABLE Ljava/lang/Object;
      // 50b: aastore
      // 50c: iinc 17 1
      // 50f: goto 4f5
      // 512: aload 0
      // 513: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 516: dup
      // 517: astore 17
      // 519: monitorenter
      // 51a: aload 0
      // 51b: getfield net/rim/device/internal/io/file/MetaDataFile._cacheSize I
      // 51e: aload 0
      // 51f: getfield net/rim/device/internal/io/file/MetaDataFile._maxCacheSize I
      // 522: if_icmplt 543
      // 525: aload 0
      // 526: invokespecial net/rim/device/internal/io/file/MetaDataFile.removeHead ()Lnet/rim/device/internal/io/file/MetaDataFile$CacheItem;
      // 529: astore 18
      // 52b: aload 18
      // 52d: instanceof net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 530: ifeq 543
      // 533: aload 0
      // 534: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 537: aload 18
      // 539: checkcast net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 53c: getfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._filename Ljava/lang/String;
      // 53f: invokevirtual net/rim/device/api/util/StringCaseInsensitiveHashtable.remove (Ljava/lang/String;)Ljava/lang/Object;
      // 542: pop
      // 543: new net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 546: dup
      // 547: aconst_null
      // 548: invokespecial net/rim/device/internal/io/file/MetaDataFile$CacheItem.<init> (Lnet/rim/device/internal/io/file/MetaDataFile$1;)V
      // 54b: astore 18
      // 54d: aload 18
      // 54f: aload 16
      // 551: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._data [Ljava/lang/Object;
      // 554: aload 18
      // 556: aload 1
      // 557: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._filename Ljava/lang/String;
      // 55a: aload 0
      // 55b: aload 18
      // 55d: invokespecial net/rim/device/internal/io/file/MetaDataFile.insertTail (Lnet/rim/device/internal/io/file/MetaDataFile$CacheItem;)V
      // 560: aload 0
      // 561: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 564: aload 1
      // 565: aload 18
      // 567: invokevirtual net/rim/device/api/util/StringCaseInsensitiveHashtable.put (Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
      // 56a: pop
      // 56b: aload 17
      // 56d: monitorexit
      // 56e: goto 579
      // 571: astore 19
      // 573: aload 17
      // 575: monitorexit
      // 576: aload 19
      // 578: athrow
      // 579: aload 3
      // 57a: ifnonnull 580
      // 57d: goto 641
      // 580: aload 3
      // 581: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 586: goto 641
      // 589: astore 16
      // 58b: goto 641
      // 58e: astore 14
      // 590: iload 2
      // 591: ifeq 597
      // 594: goto 62f
      // 597: aload 4
      // 599: astore 16
      // 59b: aload 16
      // 59d: ifnonnull 5a8
      // 5a0: getstatic net/rim/device/internal/io/file/MetaDataFile.EMPTY_META_DATA [Ljava/lang/Object;
      // 5a3: astore 16
      // 5a5: goto 5c8
      // 5a8: bipush 0
      // 5a9: istore 17
      // 5ab: iload 17
      // 5ad: bipush 8
      // 5af: if_icmpge 5c8
      // 5b2: aload 16
      // 5b4: iload 17
      // 5b6: aaload
      // 5b7: ifnonnull 5c2
      // 5ba: aload 16
      // 5bc: iload 17
      // 5be: getstatic net/rim/device/internal/io/file/MetaDataFile.NO_DATA_AVAILABLE Ljava/lang/Object;
      // 5c1: aastore
      // 5c2: iinc 17 1
      // 5c5: goto 5ab
      // 5c8: aload 0
      // 5c9: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 5cc: dup
      // 5cd: astore 17
      // 5cf: monitorenter
      // 5d0: aload 0
      // 5d1: getfield net/rim/device/internal/io/file/MetaDataFile._cacheSize I
      // 5d4: aload 0
      // 5d5: getfield net/rim/device/internal/io/file/MetaDataFile._maxCacheSize I
      // 5d8: if_icmplt 5f9
      // 5db: aload 0
      // 5dc: invokespecial net/rim/device/internal/io/file/MetaDataFile.removeHead ()Lnet/rim/device/internal/io/file/MetaDataFile$CacheItem;
      // 5df: astore 18
      // 5e1: aload 18
      // 5e3: instanceof net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 5e6: ifeq 5f9
      // 5e9: aload 0
      // 5ea: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 5ed: aload 18
      // 5ef: checkcast net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 5f2: getfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._filename Ljava/lang/String;
      // 5f5: invokevirtual net/rim/device/api/util/StringCaseInsensitiveHashtable.remove (Ljava/lang/String;)Ljava/lang/Object;
      // 5f8: pop
      // 5f9: new net/rim/device/internal/io/file/MetaDataFile$CacheItem
      // 5fc: dup
      // 5fd: aconst_null
      // 5fe: invokespecial net/rim/device/internal/io/file/MetaDataFile$CacheItem.<init> (Lnet/rim/device/internal/io/file/MetaDataFile$1;)V
      // 601: astore 18
      // 603: aload 18
      // 605: aload 16
      // 607: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._data [Ljava/lang/Object;
      // 60a: aload 18
      // 60c: aload 1
      // 60d: putfield net/rim/device/internal/io/file/MetaDataFile$CacheItem._filename Ljava/lang/String;
      // 610: aload 0
      // 611: aload 18
      // 613: invokespecial net/rim/device/internal/io/file/MetaDataFile.insertTail (Lnet/rim/device/internal/io/file/MetaDataFile$CacheItem;)V
      // 616: aload 0
      // 617: getfield net/rim/device/internal/io/file/MetaDataFile._cache Lnet/rim/device/api/util/StringCaseInsensitiveHashtable;
      // 61a: aload 1
      // 61b: aload 18
      // 61d: invokevirtual net/rim/device/api/util/StringCaseInsensitiveHashtable.put (Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
      // 620: pop
      // 621: aload 17
      // 623: monitorexit
      // 624: goto 62f
      // 627: astore 19
      // 629: aload 17
      // 62b: monitorexit
      // 62c: aload 19
      // 62e: athrow
      // 62f: aload 3
      // 630: ifnull 63e
      // 633: aload 3
      // 634: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 639: goto 63e
      // 63c: astore 16
      // 63e: aload 14
      // 640: athrow
      // 641: aload 4
      // 643: areturn
      // try (159 -> 161): 162 null
      // try (142 -> 157): 164 null
      // try (167 -> 169): 170 null
      // try (142 -> 157): 172 null
      // try (164 -> 165): 172 null
      // try (175 -> 177): 178 null
      // try (172 -> 173): 172 null
      // try (207 -> 224): 225 null
      // try (225 -> 228): 225 null
      // try (20 -> 56): 313 null
      // try (140 -> 312): 313 null
      // try (313 -> 316): 313 null
      // try (4 -> 56): 402 null
      // try (140 -> 318): 402 null
      // try (4 -> 56): 487 null
      // try (140 -> 318): 487 null
      // try (4 -> 56): 572 null
      // try (140 -> 318): 572 null
      // try (4 -> 56): 657 null
      // try (140 -> 318): 657 null
      // try (402 -> 403): 657 null
      // try (487 -> 488): 657 null
      // try (572 -> 573): 657 null
      // try (657 -> 658): 657 null
      // try (688 -> 728): 729 null
      // try (603 -> 643): 644 null
      // try (518 -> 558): 559 null
      // try (433 -> 473): 474 null
      // try (348 -> 388): 389 null
      // try (86 -> 126): 127 null
      // try (729 -> 732): 729 null
      // try (644 -> 647): 644 null
      // try (559 -> 562): 559 null
      // try (474 -> 477): 474 null
      // try (389 -> 392): 389 null
      // try (127 -> 130): 127 null
      // try (736 -> 738): 739 null
      // try (652 -> 654): 655 null
      // try (567 -> 569): 570 null
      // try (482 -> 484): 485 null
      // try (397 -> 399): 400 null
      // try (134 -> 136): 137 null
   }

   public static final int getDesiredThumbnailHeight() {
      return 84;
   }

   public static final int getDesiredThumbnailWidth() {
      return 112;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean openMetaDataDatabase(boolean createOnly) {
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         this._metaDataDatabase = (FileConnection)Connector.open(this._path + THUMBS_DB_FILE);
         this.readDatabase(true, createOnly);
         var4 = false;
      } finally {
         if (var4) {
            this._metaDataDatabase = null;
            return this._metaDataDatabase != null;
         }
      }

      return this._metaDataDatabase != null;
   }

   public final synchronized void saveMetaDataImage(String param1, EncodedImage param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 04: ifnonnull 10
      // 07: aload 0
      // 08: bipush 1
      // 09: invokespecial net/rim/device/internal/io/file/MetaDataFile.openMetaDataDatabase (Z)Z
      // 0c: ifne 10
      // 0f: return
      // 10: aconst_null
      // 11: astore 3
      // 12: new java/lang/StringBuffer
      // 15: dup
      // 16: invokespecial java/lang/StringBuffer.<init> ()V
      // 19: aload 0
      // 1a: getfield net/rim/device/internal/io/file/MetaDataFile._path Ljava/lang/String;
      // 1d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 20: aload 1
      // 21: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 24: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 27: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 2a: checkcast javax/microedition/io/file/FileConnection
      // 2d: astore 3
      // 2e: aload 3
      // 2f: invokeinterface javax/microedition/io/file/FileConnection.lastModified ()J 1
      // 34: lstore 4
      // 36: aload 0
      // 37: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 3a: aload 1
      // 3b: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.get (Ljava/lang/String;)I
      // 3e: istore 6
      // 40: iload 6
      // 42: bipush -1
      // 44: if_icmpeq 54
      // 47: aload 0
      // 48: getfield net/rim/device/internal/io/file/MetaDataFile._fileModTimes [J
      // 4b: iload 6
      // 4d: laload
      // 4e: lload 4
      // 50: lcmp
      // 51: ifeq 6e
      // 54: bipush 8
      // 56: anewarray 4148
      // 59: astore 7
      // 5b: aload 7
      // 5d: bipush 0
      // 5e: aload 2
      // 5f: aastore
      // 60: aload 0
      // 61: aload 1
      // 62: lload 4
      // 64: aload 7
      // 66: invokespecial net/rim/device/internal/io/file/MetaDataFile.writeEntry (Ljava/lang/String;J[Ljava/lang/Object;)V
      // 69: aload 0
      // 6a: aload 1
      // 6b: invokevirtual net/rim/device/internal/io/file/MetaDataFile.purgeMetadataFromCache (Ljava/lang/String;)V
      // 6e: aload 3
      // 6f: ifnull a5
      // 72: aload 3
      // 73: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 78: return
      // 79: astore 4
      // 7b: return
      // 7c: astore 4
      // 7e: aload 4
      // 80: athrow
      // 81: astore 4
      // 83: aload 3
      // 84: ifnull a5
      // 87: aload 3
      // 88: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 8d: return
      // 8e: astore 4
      // 90: return
      // 91: astore 8
      // 93: aload 3
      // 94: ifnull a2
      // 97: aload 3
      // 98: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 9d: goto a2
      // a0: astore 9
      // a2: aload 8
      // a4: athrow
      // a5: return
      // try (57 -> 59): 60 null
      // try (10 -> 55): 62 null
      // try (10 -> 55): 65 null
      // try (68 -> 70): 71 null
      // try (10 -> 55): 73 null
      // try (62 -> 66): 73 null
      // try (76 -> 78): 79 null
      // try (73 -> 74): 73 null
   }

   final synchronized Object[] loadMetaData(String filename) {
      return this._metaDataDatabase == null && !this.openMetaDataDatabase(true) ? null : this.getOrCreateMetadata(filename, false);
   }

   final synchronized Object[] saveMetaDataBookmark(String param1, long param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush 8
      // 02: anewarray 4228
      // 05: astore 4
      // 07: aload 4
      // 09: bipush 7
      // 0b: new java/lang/Long
      // 0e: dup
      // 0f: lload 2
      // 10: invokespecial java/lang/Long.<init> (J)V
      // 13: aastore
      // 14: aload 0
      // 15: getfield net/rim/device/internal/io/file/MetaDataFile._metaDataDatabase Ljavax/microedition/io/file/FileConnection;
      // 18: ifnonnull 2d
      // 1b: aload 0
      // 1c: bipush 1
      // 1d: invokespecial net/rim/device/internal/io/file/MetaDataFile.openMetaDataDatabase (Z)Z
      // 20: ifne 2d
      // 23: lload 2
      // 24: bipush 0
      // 25: i2l
      // 26: lcmp
      // 27: ifne 2d
      // 2a: aload 4
      // 2c: areturn
      // 2d: lload 2
      // 2e: bipush 0
      // 2f: i2l
      // 30: lcmp
      // 31: ifne 42
      // 34: aload 0
      // 35: getfield net/rim/device/internal/io/file/MetaDataFile._fileFilenameToEntry Lnet/rim/device/api/util/StringToIntCaseInsensitiveHashtable;
      // 38: aload 1
      // 39: invokevirtual net/rim/device/api/util/StringToIntCaseInsensitiveHashtable.get (Ljava/lang/String;)I
      // 3c: ifge 42
      // 3f: aload 4
      // 41: areturn
      // 42: aconst_null
      // 43: astore 5
      // 45: aconst_null
      // 46: astore 6
      // 48: new java/lang/StringBuffer
      // 4b: dup
      // 4c: invokespecial java/lang/StringBuffer.<init> ()V
      // 4f: aload 0
      // 50: getfield net/rim/device/internal/io/file/MetaDataFile._path Ljava/lang/String;
      // 53: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 56: aload 1
      // 57: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 5a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 5d: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 60: checkcast javax/microedition/io/file/FileConnection
      // 63: astore 5
      // 65: aload 5
      // 67: invokeinterface javax/microedition/io/file/FileConnection.lastModified ()J 1
      // 6c: lstore 7
      // 6e: aload 0
      // 6f: aload 1
      // 70: lload 7
      // 72: aload 4
      // 74: invokespecial net/rim/device/internal/io/file/MetaDataFile.writeEntry (Ljava/lang/String;J[Ljava/lang/Object;)V
      // 77: aload 4
      // 79: astore 6
      // 7b: aload 5
      // 7d: ifnull c0
      // 80: aload 5
      // 82: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 87: goto c0
      // 8a: astore 7
      // 8c: goto c0
      // 8f: astore 7
      // 91: aload 7
      // 93: athrow
      // 94: astore 7
      // 96: aload 5
      // 98: ifnull c0
      // 9b: aload 5
      // 9d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // a2: goto c0
      // a5: astore 7
      // a7: goto c0
      // aa: astore 9
      // ac: aload 5
      // ae: ifnull bd
      // b1: aload 5
      // b3: invokeinterface javax/microedition/io/Connection.close ()V 1
      // b8: goto bd
      // bb: astore 10
      // bd: aload 9
      // bf: athrow
      // c0: aload 6
      // c2: areturn
      // try (64 -> 66): 67 null
      // try (40 -> 62): 69 null
      // try (40 -> 62): 72 null
      // try (75 -> 77): 78 null
      // try (40 -> 62): 80 null
      // try (69 -> 73): 80 null
      // try (83 -> 85): 86 null
      // try (80 -> 81): 80 null
   }

   public final Enumeration enumerateFiles() {
      return new MetaDataFile$FileEnumeration(this);
   }

   private static final boolean isEncrypted(FileConnection connection) {
      return !(connection instanceof ExtendedFileConnection) ? false : ((ExtendedFileConnection)connection).isFileEncrypted();
   }

   private final boolean filesystemUsesLocalTimestamps() {
      return this._isRemovable;
   }

   static {
      for (int i = 7; i >= 0; i--) {
         EMPTY_META_DATA[i] = NO_DATA_AVAILABLE;
      }
   }
}

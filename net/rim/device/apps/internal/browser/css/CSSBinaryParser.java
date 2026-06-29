package net.rim.device.apps.internal.browser.css;

import java.util.Vector;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.vm.Array;

public final class CSSBinaryParser implements CSSParser {
   private DataBuffer _in;
   private DocumentHandler _handler;
   private String _url;
   private int _token;
   private String _stringTable;
   private Vector _freeList = (Vector)(new Object());
   private int[][] _currentSelector = new int[5][];
   public static final byte VERSION = 1;
   private static final byte IMPORT = 1;
   private static final byte SELECTOR = 2;
   private static final byte PROPERTY = 3;
   private static final byte TAG_MASK = 7;
   private static final int SIZE_SHIFT = 3;
   private static final int MAX_SIZE = 15;
   private static final byte SIZE_MASK = 120;
   private static final byte SIZE_BIT = -128;
   private static final String[] DEFAULT_MEDIA_LIST = new Object[0];

   public CSSBinaryParser(DocumentHandler handler, String url) {
      this._handler = handler;
      this._url = url;
   }

   @Override
   public final void parseStyleSheet(String source) {
   }

   @Override
   public final void parseStyleSheet(byte[] param1) {
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
      // 01: getfield net/rim/device/apps/internal/browser/css/CSSBinaryParser._handler Lnet/rim/device/apps/internal/browser/css/DocumentHandler;
      // 04: ifnonnull 08
      // 07: return
      // 08: aload 0
      // 09: new java/lang/Object
      // 0c: dup
      // 0d: aload 1
      // 0e: bipush 0
      // 0f: aload 1
      // 10: arraylength
      // 11: bipush 1
      // 12: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 15: putfield net/rim/device/apps/internal/browser/css/CSSBinaryParser._in Lnet/rim/device/api/util/DataBuffer;
      // 18: aload 0
      // 19: getfield net/rim/device/apps/internal/browser/css/CSSBinaryParser._in Lnet/rim/device/api/util/DataBuffer;
      // 1c: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 1f: pop
      // 20: aload 0
      // 21: getfield net/rim/device/apps/internal/browser/css/CSSBinaryParser._in Lnet/rim/device/api/util/DataBuffer;
      // 24: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 27: pop
      // 28: aload 0
      // 29: new java/lang/Object
      // 2c: dup
      // 2d: aload 0
      // 2e: getfield net/rim/device/apps/internal/browser/css/CSSBinaryParser._in Lnet/rim/device/api/util/DataBuffer;
      // 31: invokevirtual net/rim/device/api/util/DataBuffer.readByteArray ()[B
      // 34: invokespecial java/lang/String.<init> ([B)V
      // 37: putfield net/rim/device/apps/internal/browser/css/CSSBinaryParser._stringTable Ljava/lang/String;
      // 3a: goto 3f
      // 3d: astore 2
      // 3e: return
      // 3f: aload 0
      // 40: getfield net/rim/device/apps/internal/browser/css/CSSBinaryParser._handler Lnet/rim/device/apps/internal/browser/css/DocumentHandler;
      // 43: aconst_null
      // 44: invokeinterface net/rim/device/apps/internal/browser/css/DocumentHandler.startDocument (Ljava/lang/String;)V 2
      // 49: aload 0
      // 4a: invokespecial net/rim/device/apps/internal/browser/css/CSSBinaryParser.nextToken ()I
      // 4d: bipush 7
      // 4f: iand
      // 50: bipush 1
      // 51: if_icmpne 67
      // 54: aload 0
      // 55: invokespecial net/rim/device/apps/internal/browser/css/CSSBinaryParser.parseImportRule ()V
      // 58: goto 49
      // 5b: astore 2
      // 5c: aload 0
      // 5d: getfield net/rim/device/apps/internal/browser/css/CSSBinaryParser._handler Lnet/rim/device/apps/internal/browser/css/DocumentHandler;
      // 60: aconst_null
      // 61: invokeinterface net/rim/device/apps/internal/browser/css/DocumentHandler.endDocument (Ljava/lang/String;)V 2
      // 66: return
      // 67: aload 0
      // 68: getfield net/rim/device/apps/internal/browser/css/CSSBinaryParser._token I
      // 6b: bipush 7
      // 6d: iand
      // 6e: bipush 2
      // 70: if_icmpne 8b
      // 73: aload 0
      // 74: invokespecial net/rim/device/apps/internal/browser/css/CSSBinaryParser.parseRuleSet ()V
      // 77: aload 0
      // 78: invokespecial net/rim/device/apps/internal/browser/css/CSSBinaryParser.nextToken ()I
      // 7b: pop
      // 7c: goto 67
      // 7f: astore 2
      // 80: aload 0
      // 81: getfield net/rim/device/apps/internal/browser/css/CSSBinaryParser._handler Lnet/rim/device/apps/internal/browser/css/DocumentHandler;
      // 84: aconst_null
      // 85: invokeinterface net/rim/device/apps/internal/browser/css/DocumentHandler.endDocument (Ljava/lang/String;)V 2
      // 8a: return
      // 8b: aload 0
      // 8c: getfield net/rim/device/apps/internal/browser/css/CSSBinaryParser._handler Lnet/rim/device/apps/internal/browser/css/DocumentHandler;
      // 8f: aconst_null
      // 90: invokeinterface net/rim/device/apps/internal/browser/css/DocumentHandler.endDocument (Ljava/lang/String;)V 2
      // 95: return
      // 96: astore 3
      // 97: aload 0
      // 98: getfield net/rim/device/apps/internal/browser/css/CSSBinaryParser._handler Lnet/rim/device/apps/internal/browser/css/DocumentHandler;
      // 9b: aconst_null
      // 9c: invokeinterface net/rim/device/apps/internal/browser/css/DocumentHandler.endDocument (Ljava/lang/String;)V 2
      // a1: aload 3
      // a2: athrow
      // try (14 -> 30): 31 null
      // try (43 -> 45): 46 null
      // try (58 -> 63): 64 null
      // try (33 -> 47): 75 null
      // try (52 -> 65): 75 null
      // try (75 -> 76): 75 null
   }

   @Override
   public final void parseStyleDeclaration(String source) {
   }

   private final void parseImportRule() {
      int urlIndex = this._in.readCompressedInt();
      String uri = this.getURL(urlIndex);
      if (uri != null) {
         if (this._url != null) {
            uri = URI.getAbsoluteURL(uri, this._url);
         }

         this._handler.importStyle(uri, DEFAULT_MEDIA_LIST);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void parseRuleSet() {
      int selectorListSize = this.parseSelectorList();
      this.nextToken();
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         this._handler.startSelector(this._currentSelector, selectorListSize);
         this.parseStyleDeclaration();
         var6 = false;
      } finally {
         if (var6) {
            this._handler.endSelector(this._currentSelector, selectorListSize);

            for (int i = 0; i < selectorListSize; i++) {
               this.deallocateArray(this._currentSelector[i]);
            }
         }
      }

      this._handler.endSelector(this._currentSelector, selectorListSize);

      for (int i = 0; i < selectorListSize; i++) {
         this.deallocateArray(this._currentSelector[i]);
      }
   }

   private final int parseSelectorList() {
      int selectorListSize;
      if ((this._token & -128) != 0) {
         selectorListSize = this._in.readCompressedInt();
      } else {
         selectorListSize = (this._token & 120) >> 3;
      }

      int length = this._currentSelector.length;
      if (selectorListSize > length) {
         Array.resize(this._currentSelector, selectorListSize);
      }

      for (int i = 0; i < selectorListSize; i++) {
         this._currentSelector[i] = this.parseArray(false);
      }

      return selectorListSize;
   }

   private final void parseStyleDeclaration() {
      int styleDeclarationSize;
      if ((this._token & -128) != 0) {
         styleDeclarationSize = this._in.readCompressedInt();
      } else {
         styleDeclarationSize = (this._token & 120) >> 3;
      }

      for (int i = 0; i < styleDeclarationSize; i++) {
         int property = this._in.readCompressedInt();
         boolean supported = CSSUtilities.isStylePropertySupported(property);
         int[] expression = this.parseArray(!supported);
         if (expression != null) {
            if (supported && expression[expression.length - 1] > 0) {
               boolean important = (expression[0] & 512) != 0;
               if (important) {
                  expression[0] &= -513;
               }

               this._handler.property(property, expression, important);
            }

            this.deallocateArray(expression);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int nextToken() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         this._token = this._in.readByte();
         var3 = false;
      } finally {
         if (var3) {
            this._token = 0;
            return this._token;
         }
      }

      return this._token;
   }

   @Override
   public final String getSource() {
      return this._stringTable;
   }

   @Override
   public final int getStringStartIndex(int stringIndex) {
      return stringIndex;
   }

   @Override
   public final int getStringEndIndex(int stringIndex) {
      return this._stringTable.indexOf(0, stringIndex);
   }

   @Override
   public final CSSString getString(int stringIndex) {
      return stringIndex < this._stringTable.length() ? new CSSString(this._stringTable, stringIndex, this.getStringEndIndex(stringIndex)) : null;
   }

   @Override
   public final String getURL(int urlIndex) {
      CSSString url = this.getString(urlIndex);
      return url != null ? url.getString() : null;
   }

   private final int[] parseArray(boolean ignore) {
      int[] array = null;
      int size = this._in.readCompressedInt();
      if (!ignore) {
         array = this.allocateArray(size);

         for (int i = 0; i < size; i++) {
            array[i] = this._in.readCompressedInt();
         }
      } else {
         for (int i = 0; i < size; i++) {
            this._in.readCompressedInt();
         }
      }

      return array;
   }

   private final int[] allocateArray(int size) {
      int freeSize = this._freeList.size();
      if (size < 10 && freeSize >= 1) {
         int[] result = (int[])this._freeList.elementAt(freeSize - 1);
         this._freeList.removeElementAt(freeSize - 1);
         result[result.length - 1] = size;
         return result;
      } else {
         int[] result = new int[Math.max(size + 1, 11)];
         result[result.length - 1] = size;
         return result;
      }
   }

   private final void deallocateArray(int[] array) {
      if (array != null) {
         this._freeList.addElement(array);
      }
   }
}

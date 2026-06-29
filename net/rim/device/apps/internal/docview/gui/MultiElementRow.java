package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

final class MultiElementRow extends Field {
   private ResourceBundle _resBundle;
   private byte _buffer = 3;
   private byte _gridLook;
   private short _style;
   private int _columnCount;
   private boolean _startWithFirstColumn;
   private int _maxHorizontalSize;
   private boolean _indicateLastElementRow;
   private ColumnInfo[] _dataVector;
   private StringBuffer _strBuffer;
   private int _currentFontSize;
   private FontFactory _fontFactory;
   private boolean _isLastRow;
   private static final Bitmap _embImagePlaceholder = Bitmap.getBitmapResource("net_rim_DocView-img.gif");

   MultiElementRow(StringBuffer sheetDataBuffer, FontFactory fontFactory) {
      super(0);
      this._strBuffer = sheetDataBuffer;
      this._fontFactory = fontFactory;
   }

   final void initialize(
      ColumnInfo[] dataVector,
      int validSize,
      short style,
      byte look,
      byte nBuffer,
      boolean startWithFirstColumn,
      boolean indicateLastElementRow,
      boolean isLastRow
   ) {
      this._dataVector = dataVector;
      this._buffer = nBuffer;
      this._columnCount = validSize;
      this._startWithFirstColumn = startWithFirstColumn;
      this._style = style;
      this._gridLook = look;
      this._indicateLastElementRow = indicateLastElementRow;
      this._isLastRow = isLastRow;
      this._maxHorizontalSize = DocViewGUIInternalConstants.SCREEN_WIDTH;
   }

   @Override
   public final void setFont(Font font) {
      super.setFont(font);
      this.setCurrentFontSize(font.getHeight(4194307));
   }

   final short getDrawStyle() {
      return this._style;
   }

   @Override
   public final boolean isFocusable() {
      return true;
   }

   @Override
   protected final void layout(int width, int height) {
   }

   @Override
   public final void paint(Graphics graphics) {
   }

   public final void paint(Graphics param1, int param2, boolean param3, int param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._columnCount I
      // 004: ifgt 008
      // 007: return
      // 008: iload 3
      // 009: ifne 011
      // 00c: iload 4
      // 00e: ifne 01c
      // 011: aload 1
      // 012: aload 0
      // 013: invokevirtual net/rim/device/api/ui/Field.getFont ()Lnet/rim/device/api/ui/Font;
      // 016: invokevirtual net/rim/device/api/ui/Graphics.setFont (Lnet/rim/device/api/ui/Font;)V
      // 019: goto 029
      // 01c: aload 1
      // 01d: aload 0
      // 01e: invokevirtual net/rim/device/api/ui/Field.getFont ()Lnet/rim/device/api/ui/Font;
      // 021: iload 4
      // 023: invokevirtual net/rim/device/api/ui/Font.derive (I)Lnet/rim/device/api/ui/Font;
      // 026: invokevirtual net/rim/device/api/ui/Graphics.setFont (Lnet/rim/device/api/ui/Font;)V
      // 029: aload 0
      // 02a: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._style S
      // 02d: sipush 4096
      // 030: iand
      // 031: ifeq 038
      // 034: bipush 1
      // 035: goto 039
      // 038: bipush 0
      // 039: istore 5
      // 03b: aload 0
      // 03c: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._style S
      // 03f: sipush 256
      // 042: iand
      // 043: ifeq 04a
      // 046: bipush 1
      // 047: goto 04b
      // 04a: bipush 0
      // 04b: istore 6
      // 04d: invokestatic net/rim/device/api/ui/Graphics.isColor ()Z
      // 050: istore 7
      // 052: bipush 1
      // 053: istore 8
      // 055: aload 1
      // 056: invokevirtual net/rim/device/api/ui/Graphics.getFont ()Lnet/rim/device/api/ui/Font;
      // 059: invokevirtual net/rim/device/api/ui/Font.getHeight ()I
      // 05c: bipush 2
      // 05e: iadd
      // 05f: istore 9
      // 061: aload 1
      // 062: invokevirtual net/rim/device/api/ui/Graphics.getColor ()I
      // 065: istore 10
      // 067: aload 1
      // 068: ldc_w 16777215
      // 06b: invokevirtual net/rim/device/api/ui/Graphics.setColor (I)V
      // 06e: aload 1
      // 06f: bipush 0
      // 070: iload 2
      // 071: aload 0
      // 072: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._maxHorizontalSize I
      // 075: iload 9
      // 077: invokevirtual net/rim/device/api/ui/Graphics.fillRect (IIII)V
      // 07a: aload 1
      // 07b: iload 10
      // 07d: invokevirtual net/rim/device/api/ui/Graphics.setColor (I)V
      // 080: aload 1
      // 081: invokevirtual net/rim/device/api/ui/Graphics.getFont ()Lnet/rim/device/api/ui/Font;
      // 084: sipush 8230
      // 087: invokevirtual net/rim/device/api/ui/Font.getBounds (C)I
      // 08a: istore 11
      // 08c: bipush 0
      // 08d: istore 12
      // 08f: bipush -1
      // 091: istore 13
      // 093: bipush -1
      // 095: istore 14
      // 097: bipush 1
      // 098: istore 15
      // 09a: bipush 0
      // 09b: istore 16
      // 09d: aconst_null
      // 09e: astore 17
      // 0a0: aconst_null
      // 0a1: astore 18
      // 0a3: bipush 0
      // 0a4: istore 19
      // 0a6: iload 19
      // 0a8: aload 0
      // 0a9: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._columnCount I
      // 0ac: if_icmplt 0b2
      // 0af: goto 6da
      // 0b2: aload 0
      // 0b3: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._dataVector [Lnet/rim/device/apps/internal/docview/gui/ColumnInfo;
      // 0b6: iload 19
      // 0b8: aaload
      // 0b9: astore 20
      // 0bb: bipush 0
      // 0bc: istore 21
      // 0be: bipush 0
      // 0bf: istore 22
      // 0c1: bipush 1
      // 0c2: istore 15
      // 0c4: aload 20
      // 0c6: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnInfo Ljava/lang/Object;
      // 0c9: dup
      // 0ca: instanceof net/rim/device/apps/internal/docview/gui/DocViewCellInfo
      // 0cd: ifne 0d4
      // 0d0: pop
      // 0d1: goto 0e2
      // 0d4: checkcast net/rim/device/apps/internal/docview/gui/DocViewCellInfo
      // 0d7: getfield net/rim/device/apps/internal/docview/gui/DocViewCellInfo._textDirection B
      // 0da: bipush 2
      // 0dc: if_icmpne 0e2
      // 0df: bipush 0
      // 0e0: istore 15
      // 0e2: bipush 0
      // 0e3: istore 23
      // 0e5: iload 15
      // 0e7: ifeq 100
      // 0ea: aload 0
      // 0eb: iload 19
      // 0ed: bipush 1
      // 0ee: invokespecial net/rim/device/apps/internal/docview/gui/MultiElementRow.getAvailableDrawingWidth (IZ)I
      // 0f1: aload 0
      // 0f2: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._maxHorizontalSize I
      // 0f5: iload 8
      // 0f7: isub
      // 0f8: invokestatic java/lang/Math.min (II)I
      // 0fb: istore 23
      // 0fd: goto 125
      // 100: iload 16
      // 102: ifeq 10f
      // 105: aload 20
      // 107: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 10a: istore 23
      // 10c: goto 125
      // 10f: aload 0
      // 110: iload 19
      // 112: bipush 0
      // 113: invokespecial net/rim/device/apps/internal/docview/gui/MultiElementRow.getAvailableDrawingWidth (IZ)I
      // 116: iload 8
      // 118: bipush 1
      // 119: isub
      // 11a: aload 20
      // 11c: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 11f: iadd
      // 120: invokestatic java/lang/Math.min (II)I
      // 123: istore 23
      // 125: bipush 1
      // 126: istore 24
      // 128: iload 5
      // 12a: ifne 130
      // 12d: goto 269
      // 130: iload 15
      // 132: ifeq 138
      // 135: goto 269
      // 138: iload 23
      // 13a: aload 20
      // 13c: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 13f: if_icmpgt 145
      // 142: goto 269
      // 145: iload 5
      // 147: ifeq 161
      // 14a: aload 0
      // 14b: aload 1
      // 14c: iload 7
      // 14e: iload 6
      // 150: iload 8
      // 152: iload 2
      // 153: aload 20
      // 155: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 158: iload 9
      // 15a: bipush 1
      // 15b: invokespecial net/rim/device/apps/internal/docview/gui/MultiElementRow.outlineCell (Lnet/rim/device/api/ui/Graphics;ZZIIIIZ)V
      // 15e: bipush 0
      // 15f: istore 24
      // 161: iload 23
      // 163: aload 20
      // 165: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 168: isub
      // 169: bipush 2
      // 16b: isub
      // 16c: istore 25
      // 16e: iload 19
      // 170: bipush 1
      // 171: isub
      // 172: istore 26
      // 174: iload 8
      // 176: bipush 2
      // 178: isub
      // 179: istore 27
      // 17b: iload 25
      // 17d: ifge 183
      // 180: goto 269
      // 183: iload 26
      // 185: ifgt 19f
      // 188: iload 26
      // 18a: ifeq 190
      // 18d: goto 269
      // 190: aload 0
      // 191: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._startWithFirstColumn Z
      // 194: ifeq 19f
      // 197: iload 6
      // 199: ifeq 19f
      // 19c: goto 269
      // 19f: aload 0
      // 1a0: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._dataVector [Lnet/rim/device/apps/internal/docview/gui/ColumnInfo;
      // 1a3: iload 26
      // 1a5: aaload
      // 1a6: astore 28
      // 1a8: aload 1
      // 1a9: invokevirtual net/rim/device/api/ui/Graphics.getColor ()I
      // 1ac: istore 29
      // 1ae: ldc_w 16777215
      // 1b1: istore 30
      // 1b3: iload 7
      // 1b5: ifeq 1f7
      // 1b8: aload 28
      // 1ba: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._hasBgColor Z
      // 1bd: ifeq 1d1
      // 1c0: aload 28
      // 1c2: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._bgColor I
      // 1c5: bipush -1
      // 1c7: if_icmpeq 1d1
      // 1ca: aload 28
      // 1cc: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._bgColor I
      // 1cf: istore 30
      // 1d1: aload 28
      // 1d3: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnInfo Ljava/lang/Object;
      // 1d6: dup
      // 1d7: instanceof net/rim/device/apps/internal/docview/gui/DocViewCellInfo
      // 1da: ifne 1e1
      // 1dd: pop
      // 1de: goto 1f7
      // 1e1: checkcast net/rim/device/apps/internal/docview/gui/DocViewCellInfo
      // 1e4: astore 31
      // 1e6: aload 31
      // 1e8: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewCellInfo.getCellBgColor ()I
      // 1eb: bipush -1
      // 1ed: if_icmpeq 1f7
      // 1f0: aload 31
      // 1f2: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewCellInfo.getCellBgColor ()I
      // 1f5: istore 30
      // 1f7: aload 1
      // 1f8: iload 30
      // 1fa: invokevirtual net/rim/device/api/ui/Graphics.setColor (I)V
      // 1fd: iload 2
      // 1fe: ifle 205
      // 201: iload 2
      // 202: goto 208
      // 205: iload 2
      // 206: bipush 1
      // 207: iadd
      // 208: istore 31
      // 20a: aload 1
      // 20b: iload 27
      // 20d: iload 31
      // 20f: iload 27
      // 211: iload 31
      // 213: iload 5
      // 215: ifeq 21f
      // 218: iload 9
      // 21a: bipush 1
      // 21b: isub
      // 21c: goto 221
      // 21f: iload 9
      // 221: iadd
      // 222: iload 2
      // 223: ifle 22a
      // 226: bipush 0
      // 227: goto 22b
      // 22a: bipush 1
      // 22b: isub
      // 22c: iload 5
      // 22e: ifne 23c
      // 231: aload 0
      // 232: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._isLastRow Z
      // 235: ifeq 23c
      // 238: bipush 1
      // 239: goto 23d
      // 23c: bipush 0
      // 23d: isub
      // 23e: bipush 1
      // 23f: isub
      // 240: invokevirtual net/rim/device/api/ui/Graphics.drawLine (IIII)V
      // 243: aload 1
      // 244: iload 29
      // 246: invokevirtual net/rim/device/api/ui/Graphics.setColor (I)V
      // 249: aload 28
      // 24b: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 24e: aload 0
      // 24f: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._buffer B
      // 252: iadd
      // 253: istore 32
      // 255: iload 27
      // 257: iload 32
      // 259: isub
      // 25a: istore 27
      // 25c: iload 25
      // 25e: iload 32
      // 260: isub
      // 261: istore 25
      // 263: iinc 26 -1
      // 266: goto 17b
      // 269: aload 0
      // 26a: aload 1
      // 26b: iload 7
      // 26d: aload 20
      // 26f: iload 15
      // 271: ifeq 279
      // 274: bipush 6
      // 276: goto 27b
      // 279: bipush 5
      // 27b: iload 8
      // 27d: iload 15
      // 27f: ifne 287
      // 282: iload 23
      // 284: ifgt 28c
      // 287: iload 8
      // 289: goto 297
      // 28c: iload 8
      // 28e: iload 23
      // 290: aload 20
      // 292: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 295: isub
      // 296: isub
      // 297: iload 2
      // 298: iload 23
      // 29a: iload 9
      // 29c: iload 3
      // 29d: iload 11
      // 29f: iload 5
      // 2a1: invokespecial net/rim/device/apps/internal/docview/gui/MultiElementRow.drawCellText (Lnet/rim/device/api/ui/Graphics;ZLnet/rim/device/apps/internal/docview/gui/ColumnInfo;IIIIIIZIZ)I
      // 2a4: istore 25
      // 2a6: iload 15
      // 2a8: ifne 2ae
      // 2ab: goto 350
      // 2ae: iload 25
      // 2b0: ifle 2d2
      // 2b3: iload 25
      // 2b5: istore 12
      // 2b7: iload 19
      // 2b9: istore 13
      // 2bb: iload 8
      // 2bd: istore 14
      // 2bf: iload 25
      // 2c1: aload 20
      // 2c3: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 2c6: if_icmpgt 2cc
      // 2c9: goto 447
      // 2cc: bipush 1
      // 2cd: istore 16
      // 2cf: goto 447
      // 2d2: iload 23
      // 2d4: ifgt 2da
      // 2d7: goto 447
      // 2da: aload 20
      // 2dc: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnInfo Ljava/lang/Object;
      // 2df: dup
      // 2e0: instanceof net/rim/device/apps/internal/docview/gui/DocViewCellInfo
      // 2e3: ifne 2ea
      // 2e6: pop
      // 2e7: goto 447
      // 2ea: checkcast net/rim/device/apps/internal/docview/gui/DocViewCellInfo
      // 2ed: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewCellInfo.getCellBgColor ()I
      // 2f0: istore 26
      // 2f2: iload 7
      // 2f4: ifeq 309
      // 2f7: iload 26
      // 2f9: bipush -1
      // 2fb: if_icmpeq 309
      // 2fe: iload 26
      // 300: ldc_w 16777215
      // 303: if_icmpeq 309
      // 306: bipush 1
      // 307: istore 22
      // 309: aload 20
      // 30b: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._embObjectType I
      // 30e: bipush -1
      // 310: if_icmpne 316
      // 313: goto 447
      // 316: aload 0
      // 317: aload 20
      // 319: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._embObjectType I
      // 31c: aload 1
      // 31d: iload 8
      // 31f: iload 2
      // 320: bipush 1
      // 321: iadd
      // 322: aload 20
      // 324: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 327: iload 9
      // 329: bipush 2
      // 32b: isub
      // 32c: iload 7
      // 32e: ifeq 345
      // 331: iload 26
      // 333: bipush -1
      // 335: if_icmpeq 345
      // 338: iload 26
      // 33a: ldc_w 16777215
      // 33d: if_icmpeq 345
      // 340: iload 26
      // 342: goto 348
      // 345: ldc_w 16777215
      // 348: iload 10
      // 34a: invokespecial net/rim/device/apps/internal/docview/gui/MultiElementRow.drawEmptyCellEmbIndicator (ILnet/rim/device/api/ui/Graphics;IIIIII)V
      // 34d: goto 447
      // 350: iload 5
      // 352: ifne 358
      // 355: goto 447
      // 358: iload 23
      // 35a: aload 20
      // 35c: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 35f: if_icmpgt 365
      // 362: goto 447
      // 365: iload 23
      // 367: aload 20
      // 369: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 36c: isub
      // 36d: bipush 2
      // 36f: isub
      // 370: istore 26
      // 372: iload 19
      // 374: bipush 1
      // 375: isub
      // 376: istore 27
      // 378: iload 8
      // 37a: bipush 2
      // 37c: isub
      // 37d: istore 28
      // 37f: iload 25
      // 381: aload 20
      // 383: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 386: isub
      // 387: bipush 2
      // 389: isub
      // 38a: istore 29
      // 38c: iload 26
      // 38e: ifge 394
      // 391: goto 447
      // 394: iload 27
      // 396: ifgt 3b0
      // 399: iload 27
      // 39b: ifeq 3a1
      // 39e: goto 447
      // 3a1: aload 0
      // 3a2: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._startWithFirstColumn Z
      // 3a5: ifeq 3b0
      // 3a8: iload 6
      // 3aa: ifeq 3b0
      // 3ad: goto 447
      // 3b0: aload 0
      // 3b1: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._dataVector [Lnet/rim/device/apps/internal/docview/gui/ColumnInfo;
      // 3b4: iload 27
      // 3b6: aaload
      // 3b7: astore 30
      // 3b9: iload 29
      // 3bb: ifgt 420
      // 3be: aload 1
      // 3bf: invokevirtual net/rim/device/api/ui/Graphics.getColor ()I
      // 3c2: istore 31
      // 3c4: aload 1
      // 3c5: iload 7
      // 3c7: ifeq 3d0
      // 3ca: ldc_w 13882323
      // 3cd: goto 3d1
      // 3d0: bipush 0
      // 3d1: invokevirtual net/rim/device/api/ui/Graphics.setColor (I)V
      // 3d4: iload 2
      // 3d5: ifle 3dc
      // 3d8: iload 2
      // 3d9: goto 3df
      // 3dc: iload 2
      // 3dd: bipush 1
      // 3de: iadd
      // 3df: istore 32
      // 3e1: aload 1
      // 3e2: iload 28
      // 3e4: iload 32
      // 3e6: iload 28
      // 3e8: iload 32
      // 3ea: iload 5
      // 3ec: ifeq 3f6
      // 3ef: iload 9
      // 3f1: bipush 1
      // 3f2: isub
      // 3f3: goto 3f8
      // 3f6: iload 9
      // 3f8: iadd
      // 3f9: iload 2
      // 3fa: ifle 401
      // 3fd: bipush 0
      // 3fe: goto 402
      // 401: bipush 1
      // 402: isub
      // 403: iload 5
      // 405: ifne 413
      // 408: aload 0
      // 409: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._isLastRow Z
      // 40c: ifeq 413
      // 40f: bipush 1
      // 410: goto 414
      // 413: bipush 0
      // 414: isub
      // 415: bipush 1
      // 416: isub
      // 417: invokevirtual net/rim/device/api/ui/Graphics.drawLine (IIII)V
      // 41a: aload 1
      // 41b: iload 31
      // 41d: invokevirtual net/rim/device/api/ui/Graphics.setColor (I)V
      // 420: aload 30
      // 422: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 425: aload 0
      // 426: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._buffer B
      // 429: iadd
      // 42a: istore 31
      // 42c: iload 28
      // 42e: iload 31
      // 430: isub
      // 431: istore 28
      // 433: iload 26
      // 435: iload 31
      // 437: isub
      // 438: istore 26
      // 43a: iload 29
      // 43c: iload 31
      // 43e: isub
      // 43f: istore 29
      // 441: iinc 27 -1
      // 444: goto 38c
      // 447: iload 24
      // 449: ifne 44f
      // 44c: goto 618
      // 44f: bipush 1
      // 450: istore 21
      // 452: goto 618
      // 455: astore 23
      // 457: aload 0
      // 458: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._startWithFirstColumn Z
      // 45b: ifeq 489
      // 45e: iload 19
      // 460: ifne 489
      // 463: aload 0
      // 464: aload 1
      // 465: iload 7
      // 467: aload 20
      // 469: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnInfo Ljava/lang/Object;
      // 46c: checkcast java/lang/Object
      // 46f: bipush 68
      // 471: iload 8
      // 473: iload 2
      // 474: aload 20
      // 476: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 479: bipush 1
      // 47a: iadd
      // 47b: iload 9
      // 47d: bipush 1
      // 47e: isub
      // 47f: aload 0
      // 480: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._indicateLastElementRow Z
      // 483: invokespecial net/rim/device/apps/internal/docview/gui/MultiElementRow.drawHeaderCell (Lnet/rim/device/api/ui/Graphics;ZLjava/lang/String;IIIIIZ)V
      // 486: goto 618
      // 489: iload 19
      // 48b: ifeq 4b6
      // 48e: aload 0
      // 48f: aload 1
      // 490: iload 7
      // 492: aload 20
      // 494: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnInfo Ljava/lang/Object;
      // 497: checkcast java/lang/Object
      // 49a: bipush 68
      // 49c: iload 8
      // 49e: bipush 1
      // 49f: isub
      // 4a0: iload 2
      // 4a1: aload 20
      // 4a3: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 4a6: aload 0
      // 4a7: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._buffer B
      // 4aa: iadd
      // 4ab: bipush 1
      // 4ac: isub
      // 4ad: iload 9
      // 4af: bipush 0
      // 4b0: invokespecial net/rim/device/apps/internal/docview/gui/MultiElementRow.drawHeaderCell (Lnet/rim/device/api/ui/Graphics;ZLjava/lang/String;IIIIIZ)V
      // 4b3: goto 618
      // 4b6: aload 0
      // 4b7: aload 1
      // 4b8: iload 7
      // 4ba: aload 20
      // 4bc: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnInfo Ljava/lang/Object;
      // 4bf: checkcast java/lang/Object
      // 4c2: bipush 68
      // 4c4: iload 8
      // 4c6: iload 2
      // 4c7: aload 20
      // 4c9: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 4cc: bipush 1
      // 4cd: iadd
      // 4ce: iload 9
      // 4d0: bipush 1
      // 4d1: isub
      // 4d2: bipush 0
      // 4d3: invokespecial net/rim/device/apps/internal/docview/gui/MultiElementRow.drawHeaderCell (Lnet/rim/device/api/ui/Graphics;ZLjava/lang/String;IIIIIZ)V
      // 4d6: goto 618
      // 4d9: astore 23
      // 4db: iload 6
      // 4dd: ifeq 508
      // 4e0: aload 0
      // 4e1: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._startWithFirstColumn Z
      // 4e4: ifeq 508
      // 4e7: iload 19
      // 4e9: ifne 508
      // 4ec: aload 0
      // 4ed: aload 1
      // 4ee: iload 7
      // 4f0: aconst_null
      // 4f1: bipush 68
      // 4f3: iload 8
      // 4f5: iload 2
      // 4f6: aload 20
      // 4f8: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 4fb: bipush 1
      // 4fc: iadd
      // 4fd: iload 9
      // 4ff: bipush 1
      // 500: isub
      // 501: bipush 0
      // 502: invokespecial net/rim/device/apps/internal/docview/gui/MultiElementRow.drawHeaderCell (Lnet/rim/device/api/ui/Graphics;ZLjava/lang/String;IIIIIZ)V
      // 505: goto 618
      // 508: iload 7
      // 50a: ifne 510
      // 50d: goto 5d8
      // 510: aload 20
      // 512: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._hasBgColor Z
      // 515: ifne 51b
      // 518: goto 5d8
      // 51b: aload 20
      // 51d: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._bgColor I
      // 520: bipush -1
      // 522: if_icmpne 528
      // 525: goto 5d8
      // 528: aload 20
      // 52a: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._bgColor I
      // 52d: ldc_w 16777215
      // 530: if_icmpne 536
      // 533: goto 5d8
      // 536: aload 1
      // 537: aload 20
      // 539: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._bgColor I
      // 53c: invokevirtual net/rim/device/api/ui/Graphics.setColor (I)V
      // 53f: aload 1
      // 540: iload 8
      // 542: bipush 1
      // 543: if_icmple 54d
      // 546: iload 8
      // 548: bipush 1
      // 549: isub
      // 54a: goto 54f
      // 54d: iload 8
      // 54f: iload 2
      // 550: ifle 557
      // 553: iload 2
      // 554: goto 55a
      // 557: iload 2
      // 558: bipush 1
      // 559: iadd
      // 55a: aload 20
      // 55c: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 55f: aload 0
      // 560: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._buffer B
      // 563: iadd
      // 564: iload 8
      // 566: bipush 1
      // 567: if_icmple 577
      // 56a: iload 5
      // 56c: ifeq 573
      // 56f: bipush 1
      // 570: goto 582
      // 573: bipush 0
      // 574: goto 582
      // 577: iload 5
      // 579: ifeq 581
      // 57c: bipush 2
      // 57e: goto 582
      // 581: bipush 1
      // 582: isub
      // 583: iload 5
      // 585: ifeq 59b
      // 588: iload 12
      // 58a: aload 20
      // 58c: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 58f: if_icmple 59b
      // 592: iload 15
      // 594: ifeq 59b
      // 597: bipush 1
      // 598: goto 59c
      // 59b: bipush 0
      // 59c: iadd
      // 59d: iload 5
      // 59f: ifeq 5a9
      // 5a2: iload 9
      // 5a4: bipush 1
      // 5a5: isub
      // 5a6: goto 5ab
      // 5a9: iload 9
      // 5ab: iload 2
      // 5ac: ifle 5b3
      // 5af: bipush 0
      // 5b0: goto 5b4
      // 5b3: bipush 1
      // 5b4: isub
      // 5b5: iload 5
      // 5b7: ifne 5c5
      // 5ba: aload 0
      // 5bb: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._isLastRow Z
      // 5be: ifeq 5c5
      // 5c1: bipush 1
      // 5c2: goto 5c6
      // 5c5: bipush 0
      // 5c6: isub
      // 5c7: invokevirtual net/rim/device/api/ui/Graphics.fillRect (IIII)V
      // 5ca: aload 1
      // 5cb: iload 10
      // 5cd: invokevirtual net/rim/device/api/ui/Graphics.setColor (I)V
      // 5d0: iload 15
      // 5d2: ifeq 5d8
      // 5d5: bipush 1
      // 5d6: istore 22
      // 5d8: aload 20
      // 5da: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._embObjectType I
      // 5dd: bipush -1
      // 5df: if_icmpeq 615
      // 5e2: aload 0
      // 5e3: aload 20
      // 5e5: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._embObjectType I
      // 5e8: aload 1
      // 5e9: iload 8
      // 5eb: iload 2
      // 5ec: bipush 1
      // 5ed: iadd
      // 5ee: aload 20
      // 5f0: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 5f3: iload 9
      // 5f5: bipush 2
      // 5f7: isub
      // 5f8: iload 7
      // 5fa: ifeq 60d
      // 5fd: aload 20
      // 5ff: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._hasBgColor Z
      // 602: ifeq 60d
      // 605: aload 20
      // 607: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._bgColor I
      // 60a: goto 610
      // 60d: ldc_w 16777215
      // 610: iload 10
      // 612: invokespecial net/rim/device/apps/internal/docview/gui/MultiElementRow.drawEmptyCellEmbIndicator (ILnet/rim/device/api/ui/Graphics;IIIIII)V
      // 615: bipush 1
      // 616: istore 21
      // 618: iload 22
      // 61a: ifeq 665
      // 61d: iload 13
      // 61f: iflt 665
      // 622: iload 14
      // 624: iflt 665
      // 627: iload 12
      // 629: ifle 665
      // 62c: aload 17
      // 62e: ifnonnull 64a
      // 631: bipush 1
      // 632: newarray 10
      // 634: astore 17
      // 636: bipush 1
      // 637: newarray 10
      // 639: astore 18
      // 63b: aload 17
      // 63d: bipush 0
      // 63e: iload 13
      // 640: iastore
      // 641: aload 18
      // 643: bipush 0
      // 644: iload 14
      // 646: iastore
      // 647: goto 665
      // 64a: iload 13
      // 64c: aload 17
      // 64e: aload 17
      // 650: arraylength
      // 651: bipush 1
      // 652: isub
      // 653: iaload
      // 654: if_icmple 665
      // 657: aload 17
      // 659: iload 13
      // 65b: invokestatic net/rim/device/api/util/Arrays.add ([II)V
      // 65e: aload 18
      // 660: iload 14
      // 662: invokestatic net/rim/device/api/util/Arrays.add ([II)V
      // 665: iload 21
      // 667: ifeq 6b6
      // 66a: iload 5
      // 66c: ifeq 6b6
      // 66f: bipush 1
      // 670: istore 23
      // 672: iload 15
      // 674: ifeq 6a1
      // 677: iload 12
      // 679: aload 20
      // 67b: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 67e: if_icmpgt 687
      // 681: bipush 0
      // 682: istore 12
      // 684: goto 6a1
      // 687: iload 12
      // 689: aload 20
      // 68b: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 68e: isub
      // 68f: aload 0
      // 690: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._buffer B
      // 693: isub
      // 694: istore 12
      // 696: iload 12
      // 698: ifge 69e
      // 69b: bipush 0
      // 69c: istore 12
      // 69e: bipush 0
      // 69f: istore 23
      // 6a1: aload 0
      // 6a2: aload 1
      // 6a3: iload 7
      // 6a5: iload 6
      // 6a7: iload 8
      // 6a9: iload 2
      // 6aa: aload 20
      // 6ac: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 6af: iload 9
      // 6b1: iload 23
      // 6b3: invokespecial net/rim/device/apps/internal/docview/gui/MultiElementRow.outlineCell (Lnet/rim/device/api/ui/Graphics;ZZIIIIZ)V
      // 6b6: iload 8
      // 6b8: aload 20
      // 6ba: getfield net/rim/device/apps/internal/docview/gui/ColumnInfo._columnWidth S
      // 6bd: iadd
      // 6be: istore 8
      // 6c0: iload 19
      // 6c2: aload 0
      // 6c3: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._columnCount I
      // 6c6: bipush 1
      // 6c7: isub
      // 6c8: if_icmpge 6d4
      // 6cb: iload 8
      // 6cd: aload 0
      // 6ce: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._buffer B
      // 6d1: iadd
      // 6d2: istore 8
      // 6d4: iinc 19 1
      // 6d7: goto 0a6
      // 6da: aload 17
      // 6dc: ifnull 73c
      // 6df: aload 18
      // 6e1: ifnull 73c
      // 6e4: bipush 0
      // 6e5: istore 19
      // 6e7: iload 19
      // 6e9: aload 17
      // 6eb: arraylength
      // 6ec: if_icmpge 736
      // 6ef: aload 0
      // 6f0: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._dataVector [Lnet/rim/device/apps/internal/docview/gui/ColumnInfo;
      // 6f3: aload 17
      // 6f5: iload 19
      // 6f7: iaload
      // 6f8: aaload
      // 6f9: astore 20
      // 6fb: aload 0
      // 6fc: aload 1
      // 6fd: iload 7
      // 6ff: aload 20
      // 701: bipush 6
      // 703: aload 18
      // 705: iload 19
      // 707: iaload
      // 708: aload 18
      // 70a: iload 19
      // 70c: iaload
      // 70d: iload 2
      // 70e: aload 0
      // 70f: aload 17
      // 711: iload 19
      // 713: iaload
      // 714: bipush 1
      // 715: invokespecial net/rim/device/apps/internal/docview/gui/MultiElementRow.getAvailableDrawingWidth (IZ)I
      // 718: aload 0
      // 719: getfield net/rim/device/apps/internal/docview/gui/MultiElementRow._maxHorizontalSize I
      // 71c: aload 18
      // 71e: iload 19
      // 720: iaload
      // 721: isub
      // 722: invokestatic java/lang/Math.min (II)I
      // 725: iload 9
      // 727: iload 3
      // 728: iload 11
      // 72a: iload 5
      // 72c: invokespecial net/rim/device/apps/internal/docview/gui/MultiElementRow.drawCellText (Lnet/rim/device/api/ui/Graphics;ZLnet/rim/device/apps/internal/docview/gui/ColumnInfo;IIIIIIZIZ)I
      // 72f: pop
      // 730: iinc 19 1
      // 733: goto 6e7
      // 736: aconst_null
      // 737: astore 17
      // 739: aconst_null
      // 73a: astore 18
      // 73c: aload 1
      // 73d: iload 10
      // 73f: invokevirtual net/rim/device/api/ui/Graphics.setColor (I)V
      // 742: return
      // try (100 -> 534): 535 null
      // try (100 -> 534): 604 null
   }

   private final void outlineCell(
      Graphics graphics, boolean isColorDisplay, boolean isDisplayHeaders, int xpos, int ypos, int width, int height, boolean drawEndLine
   ) {
      int crtFgColor = graphics.getColor();
      graphics.setColor(isColorDisplay ? 13882323 : 0);
      graphics.drawLine(xpos - 1, ypos + height - 1, xpos + width + 1, ypos + height - 1);
      if (drawEndLine) {
         graphics.drawLine(xpos + width + 1, ypos, xpos + width + 1, ypos + height - 1);
      }

      if (!isDisplayHeaders) {
         if (xpos == 1) {
            graphics.drawLine(0, ypos, 0, ypos + height - 1);
         }

         if (ypos == 0) {
            graphics.drawLine(xpos - 1, ypos, xpos + width + 1, ypos);
         }
      } else if (!this._startWithFirstColumn && xpos == 1) {
         graphics.drawLine(0, ypos, 0, ypos + height - 1);
      }

      graphics.setColor(crtFgColor);
   }

   private final void drawLastRetrievedRowIndicator(Graphics graphics, int xpos, int width, int ypos, int height) {
      int iYMedian = ypos + (height >> 1);
      graphics.drawLine(xpos + width - 1, iYMedian, xpos + width - 3, iYMedian - 2);
      graphics.drawLine(xpos + width - 1, iYMedian, xpos + width - 3, iYMedian + 2);
      graphics.drawLine(xpos + width - 3, iYMedian + 2, xpos + width - 3, iYMedian - 2);
      graphics.drawPoint(xpos + width - 2, iYMedian);
   }

   private final void drawHeaderCell(
      Graphics graphics, boolean isColorDisplay, String text, int drawStyle, int xpos, int ypos, int width, int height, boolean indicateLastElementRow
   ) {
      if (!isColorDisplay) {
         if (ypos == 0) {
            if (isColorDisplay) {
               if (xpos == 1) {
                  graphics.drawRect(xpos - 1, ypos, width + this._buffer - 1, height + 1);
               } else {
                  graphics.drawRect(xpos - 1, ypos, width + this._buffer - 1, height);
               }
            }
         } else {
            graphics.drawRect(xpos - 1, ypos - 1, width + this._buffer - 1, height + 2);
         }

         if (indicateLastElementRow) {
            this.drawLastRetrievedRowIndicator(graphics, xpos, width, ypos, height);
         }

         graphics.drawText(text, xpos, ypos + 1, drawStyle, width);
         if (!isColorDisplay) {
            if (ypos == 0) {
               if (xpos != 1) {
                  graphics.invert(xpos - 1, ypos, width + this._buffer - 1, height);
                  graphics.drawLine(xpos - 1, ypos + height - 1, xpos + width, ypos + height - 1);
                  return;
               }

               graphics.invert(xpos, ypos, width + this._buffer - 2, height);
               if (!this._startWithFirstColumn) {
                  graphics.drawLine(xpos - 1, ypos + height, xpos + width, ypos + height);
                  return;
               }
            } else {
               graphics.invert(xpos - 1, ypos - 1, width + this._buffer - 2, height + 2);
            }
         }
      } else {
         int crtFgColor = graphics.getColor();
         switch (this._gridLook) {
            case 0:
               break;
            case 1:
            default:
               graphics.setColor(13882323);
               if (ypos == 0) {
                  graphics.fillRect(xpos + 1, ypos + 2, width, height - 2);
               } else {
                  graphics.fillRect(xpos + 1, ypos + 1, width, height - 1);
               }
               break;
            case 2:
               graphics.setColor(255);
               if (ypos == 0) {
                  graphics.fillRect(xpos, ypos + 1, width + 1, height - 1);
               } else {
                  graphics.fillRect(xpos, ypos, width + 1, height);
               }
         }

         graphics.setColor(0);
         if (xpos == 1) {
            graphics.drawLine(xpos - 1, ypos, xpos - 1, ypos + height);
         }

         if (ypos == 0) {
            graphics.drawLine(xpos - 1, ypos, xpos + width, ypos);
         }

         graphics.setColor(8421504);
         if (xpos == 1) {
            graphics.drawLine(xpos, ypos + height, xpos + width, ypos + height);
         } else {
            graphics.drawLine(xpos, ypos + height - 1, xpos + width, ypos + height - 1);
         }

         graphics.drawLine(xpos + width, ypos, xpos + width, ypos + height);
         graphics.setColor(crtFgColor);
         if (indicateLastElementRow) {
            graphics.setColor(this._gridLook == 1 ? 0 : 16777215);
            this.drawLastRetrievedRowIndicator(graphics, xpos, width, ypos, height);
            graphics.setColor(crtFgColor);
         }

         if (text != null) {
            graphics.setColor(this._gridLook == 1 ? 0 : 16777215);
            graphics.drawText(text, xpos, ypos + 1, drawStyle, width);
            graphics.setColor(crtFgColor);
            return;
         }
      }
   }

   private final int drawCellText(
      Graphics graphics,
      boolean isColorDisplay,
      ColumnInfo column,
      int drawStyle,
      int xposOriginal,
      int xpos,
      int ypos,
      int width,
      int height,
      boolean useOriginalFont,
      int defaultEllipsisSize,
      boolean outlineCells
   ) {
      DocViewCellInfo cellInfo = (DocViewCellInfo)column._columnInfo;
      int crtFgColor = graphics.getColor();
      int crtBgColor = 16777215;
      boolean isBgColorDefault = true;
      if (isColorDisplay && column._hasBgColor && cellInfo.getCellBgColor() == -1) {
         crtBgColor = column._bgColor;
         isBgColorDefault = false;
      }

      if (isColorDisplay && cellInfo.getCellBgColor() != -1) {
         crtBgColor = cellInfo.getCellBgColor();
         isBgColorDefault = false;
      }

      if (width <= 0) {
         return 0;
      }

      boolean ltrTextDirection = cellInfo._textDirection != 2;
      if (crtBgColor != 16777215) {
         graphics.setColor(crtBgColor);
         graphics.fillRect(
            xposOriginal > 1 ? xposOriginal - 1 : xposOriginal,
            ypos > 0 ? ypos : ypos + 1,
            column._columnWidth
               + this._buffer
               - (xposOriginal > 1 ? (outlineCells ? 1 : 0) : (outlineCells ? 2 : 1))
               + (ltrTextDirection && width > column._columnWidth ? 1 : 0),
            (outlineCells ? height - 1 : height) - (ypos > 0 ? 0 : 1) - (!outlineCells && this._isLastRow ? 1 : 0)
         );
         graphics.setColor(crtFgColor);
      }

      int[] regionsOffset = cellInfo.getRegionsOffsetIndeces();
      int[] regionsFontType = cellInfo.getRegionsFontType();
      int[] regionsForeColor = cellInfo.getRegionsForeColor();
      int[] regionsBgColor = cellInfo.getRegionsBgColor();
      int availableWidth = width;
      int regionsOffsetLength = regionsOffset.length;
      if (regionsOffsetLength == regionsFontType.length * 2
         && regionsOffsetLength == regionsForeColor.length * 2
         && regionsOffsetLength == regionsBgColor.length * 2) {
         Font defaultFont = graphics.getFont();
         int defStyle = defaultFont.getStyle();
         int crtDrawingOffset = xpos;
         if (ltrTextDirection) {
            for (int i = 0;
               i < regionsOffsetLength - 1
                  && regionsOffset[i + 1] > 0
                  && (regionsOffset[i + 1] != 1 || this._strBuffer.charAt(regionsOffset[i]) != '\n')
                  && availableWidth > 1;
               i += 2
            ) {
               int attrIndex = i >> 1;
               int definedFontType = regionsFontType[attrIndex];
               Font fontToUse = (useOriginalFont || defStyle == 0) && definedFontType != 0
                  ? this._fontFactory.getFont(definedFontType, this._currentFontSize)
                  : defaultFont;
               int ellipsesSize = fontToUse == defaultFont ? defaultEllipsisSize : fontToUse.getBounds('…');
               if (availableWidth < ellipsesSize && i < regionsOffsetLength - 2) {
                  break;
               }

               if (fontToUse != defaultFont) {
                  graphics.setFont(fontToUse);
               }

               int foreColor = regionsForeColor[attrIndex];
               if (isColorDisplay) {
                  int realBgColor = crtBgColor;
                  boolean isBgDefault = isBgColorDefault;
                  int defBgColor = regionsBgColor[attrIndex];
                  if (defBgColor != -1) {
                     graphics.setColor(defBgColor);
                     int regionWidth = Math.min(
                        graphics.getFont().getBounds(this._strBuffer, regionsOffset[i], regionsOffset[i + 1]), Math.min(availableWidth, column._columnWidth)
                     );
                     graphics.fillRect(
                        crtDrawingOffset,
                        ypos > 0 ? ypos : ypos + 1,
                        regionWidth,
                        (outlineCells ? height - 1 : height) - (ypos > 0 ? 0 : 1) - (!outlineCells && this._isLastRow ? 1 : 0)
                     );
                     realBgColor = defBgColor;
                     isBgDefault = false;
                  }

                  if (foreColor != -1) {
                     graphics.setColor(
                        !isBgDefault && foreColor != realBgColor ? foreColor : DocViewDisplayField.adjustForeColorDefault(foreColor, realBgColor)
                     );
                  } else {
                     graphics.setColor(DocViewDisplayField.adjustForeColorDefault(0, realBgColor));
                  }
               }

               int textSize = Math.min(fontToUse.getAdvance(this._strBuffer, regionsOffset[i], regionsOffset[i + 1]), availableWidth);
               graphics.drawText(this._strBuffer, regionsOffset[i], regionsOffset[i + 1], crtDrawingOffset, ypos + 1, drawStyle | 64, availableWidth);
               crtDrawingOffset += textSize;
               availableWidth -= textSize;
               if (isColorDisplay && foreColor != -1) {
                  graphics.setColor(crtFgColor);
               }

               if (fontToUse != defaultFont) {
                  graphics.setFont(defaultFont);
               }
            }
         } else if (availableWidth > defaultEllipsisSize) {
            int totalSize = 0;

            for (int i = 0; i < regionsOffsetLength && (regionsOffset[i + 1] != 1 || this._strBuffer.charAt(regionsOffset[i]) != '\n'); i += 2) {
               totalSize += regionsOffset[i + 1];
            }

            if (totalSize > 0) {
               int foreColor = 0;
               if (isColorDisplay) {
                  boolean isForeColorDefault = true;
                  if (regionsOffsetLength == 2 && regionsForeColor[0] != -1) {
                     foreColor = regionsForeColor[0];
                     isForeColorDefault = false;
                  }

                  if (!isBgColorDefault && !isForeColorDefault && foreColor != crtBgColor) {
                     graphics.setColor(foreColor);
                  } else {
                     graphics.setColor(DocViewDisplayField.adjustForeColorDefault(foreColor, crtBgColor));
                  }
               }

               availableWidth -= AttachmentViewerFactory.drawTextWithEllipses(
                  graphics, this._strBuffer, regionsOffset[0], totalSize, crtDrawingOffset, ypos + 1, availableWidth, 2, drawStyle | 64
               );
               if (isColorDisplay) {
                  graphics.setColor(crtFgColor);
               }
            }
         }
      }

      if (availableWidth < 0) {
         availableWidth = 0;
      }

      return width - availableWidth;
   }

   private final void setCurrentFontSize(int size) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final int getCurrentFontSize() {
      return this._currentFontSize;
   }

   private final int getAvailableDrawingWidth(int columnIndex, boolean isLTR) {
      int width = this._dataVector[columnIndex]._columnWidth;
      if (isLTR) {
         for (int i = columnIndex + 1; i < this._columnCount && this.isCellEmpty(i); i++) {
            width += this._dataVector[i]._columnWidth + (i < this._columnCount - 1 ? this._buffer : 0);
         }
      } else {
         for (int i = columnIndex - 1; i >= 0 && this.isCellEmpty(i); i--) {
            width += this._dataVector[i]._columnWidth + this._buffer;
         }
      }

      return width;
   }

   private final boolean isCellEmpty(int index) {
      ColumnInfo colInfo = this._dataVector[index];
      return colInfo._embObjectType == -1
         && (colInfo._columnInfo == null || colInfo._columnInfo instanceof DocViewCellInfo && !((DocViewCellInfo)colInfo._columnInfo).hasText(true));
   }

   private final void drawEmptyCellEmbIndicator(int embType, Graphics graphics, int x, int y, int width, int height, int cellBgColor, int crtForeColor) {
      if (embType == 6 && _embImagePlaceholder != null) {
         int startX = x;
         int imageWidth = _embImagePlaceholder.getWidth();
         if (width > imageWidth + 1) {
            startX += width - imageWidth >> 1;
         }

         int startY = y;
         int imageHeight = _embImagePlaceholder.getHeight();
         if (height > imageHeight + 1) {
            startY += height - imageHeight >> 1;
         }

         graphics.drawBitmap(startX, startY, startX == x ? width : imageWidth, startY == y ? height : imageHeight, _embImagePlaceholder, 0, 0);
      } else {
         String description = this.getEmbeddedObjectDescription(embType);
         if (description != null) {
            description = ((StringBuffer)(new Object())).append('[').append(description).append(']').toString();
            graphics.setColor(cellBgColor != -1 && cellBgColor != 16777215 ? ~cellBgColor : 0);
            graphics.drawText(description, x, y, 70, width);
            graphics.setColor(crtForeColor);
         }
      }
   }

   private final String getEmbeddedObjectDescription(int embObjType) {
      if (this._resBundle == null) {
         this._resBundle = ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView");
      }

      switch (embObjType) {
         case 0:
         case 2:
            return null;
         case 1:
         default:
            return this._resBundle.getString(45);
         case 3:
            return this._resBundle.getString(37);
         case 4:
            return this._resBundle.getString(61);
         case 5:
            return this._resBundle.getString(87);
         case 6:
            return this._resBundle.getString(96);
      }
   }
}

package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

public final class DEntry implements Persistable {
   public Maplet _maplet;
   public short[] _header;
   public int[] _x;
   public int[] _y;
   public int[] _startpoints;
   public int _type;
   public static final boolean USE_COMPRESSED_HEADERS = false;

   public static final int getBLX(short[] header) {
      return header[0];
   }

   public static final int getBLY(short[] header) {
      return header[1];
   }

   public static final int getTRX(short[] header) {
      return header[2];
   }

   public static final int getTRY(short[] header) {
      return header[3];
   }

   public DEntry(Maplet maplet, Layer layer, short[] header, byte[] points) {
      this._maplet = maplet;
      this._header = header;
      this._type = layer.getLayerAttribute((byte)2);
      this.decodeDEntry(maplet, layer, header, points);
   }

   private final void decodeDEntry(Maplet maplet, Layer layer, short[] header, byte[] points) {
      int dx = getBLX(header);
      int dy = getBLY(header);
      int startCount = 0;
      int startPoint = 0;
      int lastX = dx;
      int lastY = dy;
      this._x = new int[points.length];
      this._y = new int[points.length];
      this._startpoints = new int[points.length];
      int nibbleCnt = points.length << 1;
      int ix = 0;
      int pntCnt = 0;
      boolean EOP = false;

      while (ix < nibbleCnt) {
         short val = this.readNibble(points, ix++);
         byte yn = (byte)(val >> 2 & 3);
         byte xn = (byte)(val & 3);
         if (xn == 0 & yn == 0) {
            if (EOP) {
               break;
            }

            lastX = dx;
            lastY = dy;
            this._startpoints[startCount++] = startPoint;
            startPoint = pntCnt;
            EOP = true;
         } else {
            EOP = false;
            val = this.readNibble(points, ix++);
            val = (short)(val | (val >= 8 ? 65520 : 0));
            switch (xn) {
               case 3:
               default:
                  val = (short)(val << 4);
                  val = (short)(val | this.readNibble(points, ix++));
               case 2:
                  val = (short)(val << 4);
                  val = (short)(val | this.readNibble(points, ix++));
               case 1:
                  val = (short)(val << 4);
                  val = (short)(val | this.readNibble(points, ix++));
               case 0:
                  this._x[pntCnt] = val + lastX;
                  val = this.readNibble(points, ix++);
                  val = (short)(val | (val >= 8 ? 65520 : 0));
                  switch (yn) {
                     case 3:
                     default:
                        val = (short)(val << 4);
                        val = (short)(val | this.readNibble(points, ix++));
                     case 2:
                        val = (short)(val << 4);
                        val = (short)(val | this.readNibble(points, ix++));
                     case 1:
                        val = (short)(val << 4);
                        val = (short)(val | this.readNibble(points, ix++));
                     case 0:
                        this._y[pntCnt] = val + lastY;
                        lastX = this._x[pntCnt];
                        lastY = this._y[pntCnt++];
                  }
            }
         }
      }

      Array.resize(this._x, pntCnt);
      Array.resize(this._y, pntCnt);
      if (!Array.isContiguous(this._x)) {
         int[] contig_x = new int[pntCnt];
         System.arraycopy(this._x, 0, contig_x, 0, pntCnt);
         this._x = contig_x;
      }

      if (!Array.isContiguous(this._y)) {
         int[] contig_y = new int[pntCnt];
         System.arraycopy(this._y, 0, contig_y, 0, pntCnt);
         this._y = contig_y;
      }

      if (this._startpoints != null) {
         this._startpoints[startCount++] = pntCnt;
         Array.resize(this._startpoints, startCount);
         if (this._startpoints.length == 1) {
            this._startpoints = null;
         }

         if (this._startpoints != null && !Array.isContiguous(this._startpoints)) {
            int[] contig_startpoints = new int[this._startpoints.length];
            System.arraycopy(this._startpoints, 0, contig_startpoints, 0, this._startpoints.length);
            this._startpoints = contig_startpoints;
         }
      }
   }

   private final byte readNibble(byte[] array, int index) {
      int byte_offset = index >> 1;
      return (index & 1) == 1 ? (byte)(array[byte_offset] & 15) : (byte)(array[byte_offset] >> 4 & 15);
   }

   public final void render(Graphics param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/lbs/maplet/DEntry._type I
      // 04: lookupswitch 243 2 3 28 5 49
      // 20: aload 1
      // 21: aload 0
      // 22: getfield net/rim/device/apps/internal/lbs/maplet/DEntry._x [I
      // 25: aload 0
      // 26: getfield net/rim/device/apps/internal/lbs/maplet/DEntry._y [I
      // 29: aconst_null
      // 2a: aload 0
      // 2b: getfield net/rim/device/apps/internal/lbs/maplet/DEntry._startpoints [I
      // 2e: bipush 0
      // 2f: invokevirtual net/rim/device/api/ui/Graphics.drawPathOutline ([I[I[B[IZ)V
      // 32: goto f7
      // 35: aload 1
      // 36: aload 0
      // 37: getfield net/rim/device/apps/internal/lbs/maplet/DEntry._x [I
      // 3a: aload 0
      // 3b: getfield net/rim/device/apps/internal/lbs/maplet/DEntry._y [I
      // 3e: aconst_null
      // 3f: aload 0
      // 40: getfield net/rim/device/apps/internal/lbs/maplet/DEntry._startpoints [I
      // 43: invokevirtual net/rim/device/api/ui/Graphics.drawFilledPath ([I[I[B[I)V
      // 46: return
      // 47: astore 2
      // 48: ldc2_w -1037010874164756539
      // 4b: lstore 3
      // 4c: new java/lang/StringBuffer
      // 4f: dup
      // 50: invokespecial java/lang/StringBuffer.<init> ()V
      // 53: astore 5
      // 55: aload 5
      // 57: ldc_w "Maplet render out of memory error, "
      // 5a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 5d: aload 0
      // 5e: getfield net/rim/device/apps/internal/lbs/maplet/DEntry._type I
      // 61: bipush 3
      // 63: if_icmpne 6c
      // 66: ldc_w "SHAPE_LINE"
      // 69: goto 6f
      // 6c: ldc_w "SHAPE_POLYGON."
      // 6f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 72: new java/lang/StringBuffer
      // 75: dup
      // 76: ldc_w " x: "
      // 79: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 7c: aload 0
      // 7d: getfield net/rim/device/apps/internal/lbs/maplet/DEntry._x [I
      // 80: arraylength
      // 81: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 84: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 87: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 8a: pop
      // 8b: aload 5
      // 8d: new java/lang/StringBuffer
      // 90: dup
      // 91: ldc_w ", y: "
      // 94: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 97: aload 0
      // 98: getfield net/rim/device/apps/internal/lbs/maplet/DEntry._y [I
      // 9b: arraylength
      // 9c: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 9f: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // a2: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // a5: pop
      // a6: aload 5
      // a8: ldc_w ", startPoints: {"
      // ab: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // ae: pop
      // af: aload 0
      // b0: aload 5
      // b2: aload 0
      // b3: getfield net/rim/device/apps/internal/lbs/maplet/DEntry._startpoints [I
      // b6: invokespecial net/rim/device/apps/internal/lbs/maplet/DEntry.appendErrMsg (Ljava/lang/StringBuffer;[I)V
      // b9: aload 5
      // bb: ldc_w "}"
      // be: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // c1: pop
      // c2: lload 3
      // c3: aload 5
      // c5: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // c8: invokevirtual java/lang/String.getBytes ()[B
      // cb: bipush 2
      // cd: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // d0: pop
      // d1: return
      // d2: astore 2
      // d3: getstatic java/lang/System.out Ljava/io/PrintStream;
      // d6: new java/lang/StringBuffer
      // d9: dup
      // da: ldc_w "Error in render: "
      // dd: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // e0: aload 2
      // e1: invokevirtual java/lang/StringBuffer.append (Ljava/lang/Object;)Ljava/lang/StringBuffer;
      // e4: ldc_w ", msg: "
      // e7: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // ea: aload 2
      // eb: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // ee: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // f1: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // f4: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // f7: return
      // try (0 -> 23): 24 null
      // try (0 -> 23): 86 null
   }

   private final void appendErrMsg(StringBuffer msg, int[] arr) {
      if (arr != null) {
         for (int i = 0; i < arr.length; i++) {
            msg.append(arr[i]).append(i < arr.length - 1 ? ", " : "");
         }
      } else {
         msg.append("<null>");
      }
   }
}

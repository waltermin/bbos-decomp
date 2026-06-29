package net.rim.device.internal.media;

import java.io.InputStream;
import javax.microedition.media.Control;
import javax.microedition.media.control.ToneControl;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.Alert;
import net.rim.vm.Array;

public class TonePlayer extends TunePlayer implements ToneControl, SimpleTonePlayer {
   private int _curTime = -1;
   static final short[] _noteFreq = new short[]{
      8,
      9,
      9,
      10,
      10,
      11,
      12,
      12,
      13,
      14,
      15,
      15,
      16,
      17,
      18,
      19,
      21,
      22,
      23,
      24,
      26,
      28,
      29,
      31,
      33,
      35,
      37,
      39,
      41,
      44,
      46,
      49,
      52,
      55,
      58,
      62,
      65,
      69,
      73,
      78,
      82,
      87,
      92,
      98,
      104,
      110,
      117,
      123,
      131,
      139,
      147,
      156,
      166,
      175,
      185,
      196,
      208,
      220,
      233,
      247,
      262,
      277,
      294,
      311,
      330,
      349,
      370,
      392,
      415,
      440,
      466,
      494,
      523,
      554,
      587,
      622,
      659,
      698,
      740,
      784,
      831,
      880,
      932,
      988,
      1046,
      1109,
      1175,
      1245,
      1319,
      1397,
      1480,
      1568,
      1661,
      1760,
      1865,
      1976,
      2093,
      2217,
      2349,
      2489,
      2637,
      2794,
      2960,
      3136,
      3322,
      3520,
      3729,
      3951,
      4186,
      4435,
      4699,
      4978,
      5274,
      5588,
      5920,
      6272,
      6645,
      7040,
      7459,
      7902,
      8372,
      8870,
      9397,
      9956,
      10548,
      11175,
      11840,
      12544,
      4,
      -12278,
      11025,
      0,
      12000,
      0,
      8000,
      0,
      0,
      0,
      4,
      -12278,
      22050,
      0,
      24000,
      0,
      16000,
      0,
      0,
      0,
      1,
      -12284,
      35,
      0,
      1,
      -12278,
      51,
      0,
      4,
      -12278,
      -21436,
      0,
      -17536,
      0,
      32000,
      0,
      0,
      0,
      9,
      -12278,
      95,
      0,
      103,
      0,
      118,
      0,
      134,
      0,
      148,
      0,
      159,
      0,
      204,
      0,
      244,
      0,
      39,
      0,
      32,
      -12280,
      2400,
      2140,
      1900,
      1700,
      1510,
      1350,
      1200,
      1070,
      950,
      850,
      760,
      670,
      600,
      540,
      480,
      430,
      380,
      340,
      300,
      270,
      240,
      210,
      190,
      170,
      150,
      130,
      120,
      100,
      90,
      80,
      70,
      60,
      256,
      28534,
      25963,
      4428,
      11,
      -19199,
      197,
      4,
      17412,
      28826,
      31143,
      28493,
      6,
      26884,
      -5089,
      1024,
      8041,
      -3092,
      1024,
      8041,
      -3092,
      6470,
      149,
      3078,
      -24985,
      11,
      7942,
      -30356,
      130,
      27654,
      25858,
      2048,
      2048,
      -32218,
      2048,
      6952
   };

   public TonePlayer() {
      this.setContentType("audio/x-tone-seq");
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void playSimpleMidiSequence(int[] sequence) {
      byte volume = (byte)Alert.getVolume();
      if (volume != 0) {
         byte defaultTempo = 30;
         byte defaultResolution = 64;
         this.setLevel(volume);
         byte[] toneData = new byte[]{-2, 1, -3, defaultTempo, -4, defaultResolution};
         int offset = toneData.length;
         Array.resize(toneData, toneData.length + sequence.length);

         for (int i = 0; i < sequence.length; i += 2) {
            int duration = sequence[i + 1];
            byte toneDuration = (byte)(duration * defaultResolution * (defaultTempo << 2) / 240000 & 127);
            if (toneDuration == 0) {
               toneDuration = 1;
            }

            toneData[offset + i] = (byte)sequence[i];
            toneData[offset + i + 1] = toneDuration;
         }

         this.setSequence(toneData);

         try {
            this.start();
         } catch (Throwable var11) {
            throw new Object(e.toString());
         }
      }
   }

   @Override
   public void setSequence(byte[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokevirtual net/rim/device/internal/media/PlayerImpl.getState ()I
      // 004: sipush 300
      // 007: if_icmplt 015
      // 00a: new java/lang/Object
      // 00d: dup
      // 00e: ldc_w "cannot set seq after prefetched"
      // 011: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 014: athrow
      // 015: bipush 120
      // 017: istore 2
      // 018: bipush 64
      // 01a: istore 3
      // 01b: bipush 1
      // 01c: istore 4
      // 01e: bipush 0
      // 01f: istore 5
      // 021: bipush 0
      // 022: newarray 9
      // 024: astore 6
      // 026: invokestatic net/rim/device/api/system/Alert.isBuzzerSupported ()Z
      // 029: istore 7
      // 02b: new java/lang/Object
      // 02e: dup
      // 02f: invokespecial java/util/Stack.<init> ()V
      // 032: astore 8
      // 034: new java/lang/Object
      // 037: dup
      // 038: invokespecial java/util/Hashtable.<init> ()V
      // 03b: astore 9
      // 03d: new java/lang/Object
      // 040: dup
      // 041: invokespecial java/util/Hashtable.<init> ()V
      // 044: astore 10
      // 046: bipush 0
      // 047: istore 11
      // 049: bipush 0
      // 04a: istore 12
      // 04c: bipush 0
      // 04d: istore 13
      // 04f: bipush 2
      // 051: istore 17
      // 053: bipush 0
      // 054: istore 14
      // 056: bipush 0
      // 057: istore 18
      // 059: aload 1
      // 05a: bipush 0
      // 05b: baload
      // 05c: bipush -2
      // 05e: if_icmpne 068
      // 061: aload 1
      // 062: bipush 1
      // 063: baload
      // 064: bipush 1
      // 065: if_icmpeq 070
      // 068: new java/lang/Object
      // 06b: dup
      // 06c: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 06f: athrow
      // 070: aload 1
      // 071: iload 17
      // 073: baload
      // 074: bipush -3
      // 076: if_icmpne 09c
      // 079: aload 1
      // 07a: iload 17
      // 07c: bipush 1
      // 07d: iadd
      // 07e: baload
      // 07f: bipush 5
      // 081: if_icmpge 08c
      // 084: new java/lang/Object
      // 087: dup
      // 088: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 08b: athrow
      // 08c: aload 1
      // 08d: iload 17
      // 08f: bipush 1
      // 090: iadd
      // 091: baload
      // 092: bipush 127
      // 094: iand
      // 095: bipush 2
      // 097: ishl
      // 098: istore 2
      // 099: iinc 17 2
      // 09c: aload 1
      // 09d: iload 17
      // 09f: baload
      // 0a0: bipush -4
      // 0a2: if_icmpne 0c0
      // 0a5: aload 1
      // 0a6: iload 17
      // 0a8: bipush 1
      // 0a9: iadd
      // 0aa: baload
      // 0ab: ifgt 0b6
      // 0ae: new java/lang/Object
      // 0b1: dup
      // 0b2: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 0b5: athrow
      // 0b6: aload 1
      // 0b7: iload 17
      // 0b9: bipush 1
      // 0ba: iadd
      // 0bb: baload
      // 0bc: istore 3
      // 0bd: iinc 17 2
      // 0c0: iload 2
      // 0c1: iload 3
      // 0c2: imul
      // 0c3: istore 4
      // 0c5: iload 17
      // 0c7: istore 16
      // 0c9: iload 16
      // 0cb: aload 1
      // 0cc: arraylength
      // 0cd: if_icmplt 0d3
      // 0d0: goto 279
      // 0d3: aload 1
      // 0d4: iload 16
      // 0d6: baload
      // 0d7: istore 15
      // 0d9: iload 15
      // 0db: bipush -9
      // 0dd: if_icmplt 0f5
      // 0e0: iload 15
      // 0e2: ifge 0ec
      // 0e5: iload 15
      // 0e7: bipush -1
      // 0e9: if_icmpne 0fd
      // 0ec: aload 1
      // 0ed: iload 16
      // 0ef: bipush 1
      // 0f0: iadd
      // 0f1: baload
      // 0f2: ifgt 0fd
      // 0f5: new java/lang/Object
      // 0f8: dup
      // 0f9: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 0fc: athrow
      // 0fd: iload 15
      // 0ff: tableswitch 49 -10 -2 358 182 236 270 120 49 350 350 350
      // 130: iload 11
      // 132: ifne 16f
      // 135: aload 1
      // 136: iload 16
      // 138: bipush 1
      // 139: iadd
      // 13a: baload
      // 13b: ifge 146
      // 13e: new java/lang/Object
      // 141: dup
      // 142: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 145: athrow
      // 146: aload 1
      // 147: iload 16
      // 149: bipush 1
      // 14a: iadd
      // 14b: baload
      // 14c: istore 12
      // 14e: bipush 1
      // 14f: istore 11
      // 151: aload 10
      // 153: new java/lang/Object
      // 156: dup
      // 157: iload 12
      // 159: invokespecial java/lang/Integer.<init> (I)V
      // 15c: new java/lang/Object
      // 15f: dup
      // 160: iload 16
      // 162: invokespecial java/lang/Integer.<init> (I)V
      // 165: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 168: pop
      // 169: bipush 0
      // 16a: istore 13
      // 16c: goto 273
      // 16f: new java/lang/Object
      // 172: dup
      // 173: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 176: athrow
      // 177: iload 11
      // 179: ifeq 1ad
      // 17c: aload 1
      // 17d: iload 16
      // 17f: bipush 1
      // 180: iadd
      // 181: baload
      // 182: iload 12
      // 184: if_icmpne 1a5
      // 187: bipush 0
      // 188: istore 11
      // 18a: aload 9
      // 18c: new java/lang/Object
      // 18f: dup
      // 190: iload 12
      // 192: invokespecial java/lang/Integer.<init> (I)V
      // 195: new java/lang/Object
      // 198: dup
      // 199: iload 13
      // 19b: invokespecial java/lang/Integer.<init> (I)V
      // 19e: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 1a1: pop
      // 1a2: goto 273
      // 1a5: new java/lang/Object
      // 1a8: dup
      // 1a9: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 1ac: athrow
      // 1ad: new java/lang/Object
      // 1b0: dup
      // 1b1: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 1b4: athrow
      // 1b5: aload 1
      // 1b6: iload 16
      // 1b8: bipush 1
      // 1b9: iadd
      // 1ba: baload
      // 1bb: bipush 2
      // 1bd: if_icmpge 1c8
      // 1c0: new java/lang/Object
      // 1c3: dup
      // 1c4: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 1c7: athrow
      // 1c8: aload 1
      // 1c9: iload 16
      // 1cb: bipush 2
      // 1cd: iadd
      // 1ce: baload
      // 1cf: istore 15
      // 1d1: iload 15
      // 1d3: bipush -1
      // 1d5: if_icmpne 1db
      // 1d8: goto 273
      // 1db: iload 15
      // 1dd: iflt 1e3
      // 1e0: goto 273
      // 1e3: new java/lang/Object
      // 1e6: dup
      // 1e7: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 1ea: athrow
      // 1eb: aload 1
      // 1ec: iload 16
      // 1ee: bipush 1
      // 1ef: iadd
      // 1f0: baload
      // 1f1: iflt 1ff
      // 1f4: aload 1
      // 1f5: iload 16
      // 1f7: bipush 1
      // 1f8: iadd
      // 1f9: baload
      // 1fa: bipush 100
      // 1fc: if_icmple 207
      // 1ff: new java/lang/Object
      // 202: dup
      // 203: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 206: athrow
      // 207: iinc 14 2
      // 20a: goto 273
      // 20d: aload 9
      // 20f: new java/lang/Object
      // 212: dup
      // 213: aload 1
      // 214: iload 16
      // 216: bipush 1
      // 217: iadd
      // 218: baload
      // 219: invokespecial java/lang/Integer.<init> (I)V
      // 21c: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 21f: ifnonnull 22a
      // 222: new java/lang/Object
      // 225: dup
      // 226: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 229: athrow
      // 22a: aload 9
      // 22c: new java/lang/Object
      // 22f: dup
      // 230: aload 1
      // 231: iload 16
      // 233: bipush 1
      // 234: iadd
      // 235: baload
      // 236: invokespecial java/lang/Integer.<init> (I)V
      // 239: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 23c: checkcast java/lang/Object
      // 23f: invokevirtual java/lang/Integer.intValue ()I
      // 242: istore 18
      // 244: iload 11
      // 246: ifeq 253
      // 249: iload 13
      // 24b: iload 18
      // 24d: iadd
      // 24e: istore 13
      // 250: goto 273
      // 253: iload 14
      // 255: iload 18
      // 257: iadd
      // 258: istore 14
      // 25a: goto 273
      // 25d: new java/lang/Object
      // 260: dup
      // 261: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 264: athrow
      // 265: iload 11
      // 267: ifeq 270
      // 26a: iinc 13 2
      // 26d: goto 273
      // 270: iinc 14 2
      // 273: iinc 16 2
      // 276: goto 0c9
      // 279: iload 11
      // 27b: ifeq 286
      // 27e: new java/lang/Object
      // 281: dup
      // 282: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 285: athrow
      // 286: iload 14
      // 288: newarray 9
      // 28a: astore 6
      // 28c: aload 0
      // 28d: bipush 0
      // 28e: putfield net/rim/device/internal/media/TonePlayer._curTime I
      // 291: bipush 0
      // 292: istore 5
      // 294: iload 17
      // 296: istore 16
      // 298: bipush 1
      // 299: istore 19
      // 29b: iload 16
      // 29d: aload 1
      // 29e: arraylength
      // 29f: if_icmplt 2a5
      // 2a2: goto 3cf
      // 2a5: aload 1
      // 2a6: iload 16
      // 2a8: baload
      // 2a9: istore 15
      // 2ab: iload 15
      // 2ad: tableswitch 39 -10 -5 131 120 264 54 104 39
      // 2d4: iinc 16 2
      // 2d7: aload 1
      // 2d8: iload 16
      // 2da: baload
      // 2db: bipush -6
      // 2dd: if_icmpne 2d4
      // 2e0: goto 3b5
      // 2e3: aload 8
      // 2e5: new java/lang/Object
      // 2e8: dup
      // 2e9: iload 16
      // 2eb: bipush 2
      // 2ed: iadd
      // 2ee: invokespecial java/lang/Integer.<init> (I)V
      // 2f1: invokevirtual java/util/Stack.push (Ljava/lang/Object;)Ljava/lang/Object;
      // 2f4: pop
      // 2f5: aload 10
      // 2f7: new java/lang/Object
      // 2fa: dup
      // 2fb: aload 1
      // 2fc: iload 16
      // 2fe: bipush 1
      // 2ff: iadd
      // 300: baload
      // 301: invokespecial java/lang/Integer.<init> (I)V
      // 304: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 307: checkcast java/lang/Object
      // 30a: invokevirtual java/lang/Integer.intValue ()I
      // 30d: bipush 2
      // 30f: iadd
      // 310: istore 16
      // 312: goto 29b
      // 315: aload 8
      // 317: invokevirtual java/util/Stack.pop ()Ljava/lang/Object;
      // 31a: checkcast java/lang/Object
      // 31d: invokevirtual java/lang/Integer.intValue ()I
      // 320: istore 16
      // 322: goto 29b
      // 325: aload 1
      // 326: iload 16
      // 328: bipush 1
      // 329: iadd
      // 32a: baload
      // 32b: istore 19
      // 32d: goto 3b5
      // 330: invokestatic net/rim/device/api/system/Alert.isBuzzerSupported ()Z
      // 333: ifeq 359
      // 336: iload 15
      // 338: bipush -1
      // 33a: if_icmpne 349
      // 33d: aload 6
      // 33f: iload 5
      // 341: iinc 5 1
      // 344: bipush 0
      // 345: sastore
      // 346: goto 364
      // 349: aload 6
      // 34b: iload 5
      // 34d: iinc 5 1
      // 350: iload 15
      // 352: invokestatic net/rim/device/internal/media/TonePlayer.getFrequency (I)S
      // 355: sastore
      // 356: goto 364
      // 359: aload 6
      // 35b: iload 5
      // 35d: iinc 5 1
      // 360: iload 15
      // 362: i2s
      // 363: sastore
      // 364: aload 1
      // 365: iload 16
      // 367: bipush 1
      // 368: iadd
      // 369: baload
      // 36a: bipush 127
      // 36c: iand
      // 36d: iload 19
      // 36f: imul
      // 370: ldc_w 240000
      // 373: imul
      // 374: iload 4
      // 376: idiv
      // 377: istore 20
      // 379: iload 7
      // 37b: ifeq 38f
      // 37e: aload 6
      // 380: iload 5
      // 382: iinc 5 1
      // 385: dup2
      // 386: saload
      // 387: iload 20
      // 389: iadd
      // 38a: i2s
      // 38b: sastore
      // 38c: goto 3a7
      // 38f: aload 6
      // 391: iload 5
      // 393: iinc 5 1
      // 396: dup2
      // 397: saload
      // 398: aload 1
      // 399: iload 16
      // 39b: bipush 1
      // 39c: iadd
      // 39d: baload
      // 39e: bipush 127
      // 3a0: iand
      // 3a1: bipush 4
      // 3a3: imul
      // 3a4: iadd
      // 3a5: i2s
      // 3a6: sastore
      // 3a7: aload 0
      // 3a8: aload 0
      // 3a9: getfield net/rim/device/internal/media/TonePlayer._curTime I
      // 3ac: iload 20
      // 3ae: iadd
      // 3af: putfield net/rim/device/internal/media/TonePlayer._curTime I
      // 3b2: bipush 1
      // 3b3: istore 19
      // 3b5: iinc 16 2
      // 3b8: goto 29b
      // 3bb: astore 8
      // 3bd: aload 8
      // 3bf: athrow
      // 3c0: astore 8
      // 3c2: new java/lang/Object
      // 3c5: dup
      // 3c6: aload 8
      // 3c8: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 3cb: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 3ce: athrow
      // 3cf: invokestatic net/rim/device/api/system/Alert.isBuzzerSupported ()Z
      // 3d2: ifeq 3dd
      // 3d5: aload 0
      // 3d6: aload 6
      // 3d8: bipush 0
      // 3d9: invokevirtual net/rim/device/internal/media/TunePlayer.setTune ([SI)V
      // 3dc: return
      // 3dd: aload 0
      // 3de: aload 6
      // 3e0: iload 7
      // 3e2: ifeq 3eb
      // 3e5: sipush -6360
      // 3e8: goto 3ec
      // 3eb: iload 3
      // 3ec: iload 2
      // 3ed: invokestatic net/rim/device/internal/media/MidiUtilities.convertNoteDurationToMidi ([SII)[B
      // 3f0: invokevirtual net/rim/device/internal/media/TunePlayer.setMIDITune ([B)V
      // 3f3: return
      // try (22 -> 462): 462 null
      // try (22 -> 462): 465 null
   }

   @Override
   protected void doRealize() {
      super.doRealize();
      this._curTime = 0;
   }

   @Override
   public Control getControl(String controlType) {
      this.chkClosed(true);
      controlType = this.getFullyQualifiedControlType(controlType);
      return "javax.microedition.media.control.ToneControl".equals(controlType) ? this : super.getControl(controlType);
   }

   @Override
   public Control[] getControls() {
      this.chkClosed(true);
      return super.getControls();
   }

   public static short getFrequency(int note) {
      return _noteFreq[note];
   }

   @Override
   public long getDuration() {
      this.chkClosed(false);
      return this._curTime >= 0 ? (long)this._curTime * 1000 : -1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void read(InputStream stream) {
      try {
         this.setSequence(IOUtilities.streamToBytes(stream));
      } catch (Throwable var4) {
         throw new Object(e.toString());
      }
   }
}

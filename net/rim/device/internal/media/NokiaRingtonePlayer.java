package net.rim.device.internal.media;

import java.io.DataInputStream;
import java.io.InputStream;
import javax.microedition.media.MediaException;

class NokiaRingtonePlayer extends TunePlayer {
   private int _cachedByte;
   private int _bitsRemaining;
   private int _scale = 1;
   private int _tempo;
   private int _style;
   private DataInputStream _din;
   private String _tuneName;
   private boolean _containsErrors;
   private static String TYPE = "rng";
   private static final byte COMMAND_END = 0;
   private static final byte CANCEL_COMMAND = 5;
   private static final byte RINGING_TONE_PROGRAMMING = 37;
   private static final byte SOUND = 29;
   private static final byte UNICODE = 34;
   private static final byte BASIC_SONG_TYPE = 1;
   private static final byte TEMPORARY_SONG_TYPE = 2;
   private static final byte MIDI_SONG_TYPE = 3;
   private static final byte DIGITISED_SONG_TYPE = 4;
   private static final byte POLYPHONIC_SONG_TYPE = 6;
   private static final byte PATTERN_HEADER_ID = 0;
   private static final byte ALREADY_DEFINED_PATTERN = 0;
   private static final byte NOTE_INSTRUCTION_ID = 1;
   private static final byte SCALE_INSTRUCTION_ID = 2;
   private static final byte STYLE_INSTRUCTION_ID = 3;
   private static final byte TEMPO_INSTRUCTION_ID = 4;
   private static final byte VOLUME_INSTRUCTION_ID = 5;
   private static final byte SCALE1 = 0;
   private static final byte SCALE2 = 1;
   private static final byte SCALE3 = 2;
   private static final byte SCALE4 = 3;
   private static final byte STYLE_NATURAL = 0;
   private static final byte STYLE_CONTINUOUS = 1;
   private static final byte STYLE_STACCATO = 2;
   private static final byte FULL_NOTE = 0;
   private static final byte HALF_NOTE = 1;
   private static final byte QUARTER_NOTE = 2;
   private static final byte EIGHTH_NOTE = 3;
   private static final byte SIXTEENTH_NOTE = 4;
   private static final byte THIRTY_SECONDTH_NOTE = 5;
   private static final byte NO_SPECIAL_DURATION = 0;
   private static final byte DOTTED_NOTE = 1;
   private static final byte DOUBLE_DOTTED_NOTE = 2;
   private static final byte TWO_THIRD_LENGTH = 3;
   private static final short[] TEMPOS = new short[]{
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
      2048
   };
   static final short[] _noteFreq = new short[]{
      0,
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
      0,
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
      0,
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
      0,
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
      11,
      -12284,
      2304,
      11828,
      11827,
      11824,
      12849,
      52,
      25,
      -12284,
      5888,
      25938,
      25971,
      29281,
      26723,
      18720,
      8302,
      28493,
      26996,
      28271,
      19488,
      25716,
      46,
      0,
      83,
      -12284,
      20736,
      27977,
      27760,
      28005,
      28261,
      24948,
      26996,
      28271,
      28448,
      8294,
      25965,
      26980,
      11873,
      17440,
      8303,
      28526,
      8308,
      26988,
      27502,
      29728,
      8303,
      26740,
      29545,
      27680,
      25193,
      24946
   };

   public NokiaRingtonePlayer() {
      this._tempo = TEMPOS[8];
      this._style = 0;
      this.setContentType(TYPE);
   }

   public boolean isComplete() {
      return !this._containsErrors;
   }

   public String getTuneName() {
      return this._tuneName;
   }

   @Override
   public void read(InputStream stream) throws MediaException {
      this._din = new DataInputStream(stream);
      this._containsErrors = this.compile();
      if (this.getBuzzerTune().length == 0) {
         throw new MediaException();
      }
   }

   private int readBits(int numBits) {
      int[] masks = new int[]{
         0,
         128,
         192,
         224,
         240,
         248,
         252,
         254,
         255,
         -804782028,
         17170432,
         19267861,
         21627191,
         24248669,
         27197832,
         30540216,
         494,
         36307467,
         40763979,
         45744787,
         51380964,
         57672511,
         64750500,
         68550656,
         77005909,
         86443229,
         96994677,
         108856864,
         122226400,
         1976,
         145295405,
         163121453,
         183110221,
         205523856,
         230690042,
         258936465
      };
      if (numBits >= 0 && numBits <= 8) {
         int byteToReturn;
         if (numBits <= this._bitsRemaining) {
            int mask = masks[numBits];
            byteToReturn = this._cachedByte & mask;
            this._bitsRemaining -= numBits;
            this._cachedByte = this._cachedByte << numBits & 0xFF;
         } else {
            int nextByte = this._din.readUnsignedByte();
            int getBits = numBits - this._bitsRemaining;
            int mask = masks[getBits];
            int nextBits = nextByte & mask;
            byteToReturn = this._cachedByte | nextBits >> this._bitsRemaining;
            this._bitsRemaining = 8 - getBits;
            this._cachedByte = nextByte << getBits & 0xFF;
         }

         return byteToReturn >> 8 - numBits;
      } else {
         throw new IllegalStateException();
      }
   }

   private String readString(int numChars) {
      int bytesPerChar = 1;
      int numBytes = bytesPerChar * numChars;
      byte[] stringData = new byte[numBytes];

      for (int i = 0; i < numBytes; i++) {
         stringData[i] = (byte)this.readBits(8);
      }

      return new String(stringData);
   }

   private boolean compile() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/internal/media/NokiaRingtonePlayer._din Ljava/io/DataInputStream;
      // 04: invokevirtual java/io/DataInputStream.readUnsignedByte ()I
      // 07: dup
      // 08: istore 1
      // 09: ifeq 2a
      // 0c: iload 1
      // 0d: istore 2
      // 0e: bipush 0
      // 0f: istore 3
      // 10: iload 3
      // 11: iload 2
      // 12: if_icmpge 00
      // 15: aload 0
      // 16: invokespecial net/rim/device/internal/media/NokiaRingtonePlayer.readCommandPart ()Z
      // 19: ifne 1e
      // 1c: bipush 0
      // 1d: ireturn
      // 1e: iinc 3 1
      // 21: goto 10
      // 24: astore 2
      // 25: bipush 0
      // 26: ireturn
      // 27: astore 2
      // 28: bipush 0
      // 29: ireturn
      // 2a: bipush 1
      // 2b: ireturn
      // try (0 -> 17): 20 null
      // try (18 -> 20): 20 null
      // try (0 -> 17): 23 null
      // try (18 -> 20): 23 null
   }

   private boolean readCommandPart() {
      int nextByte = this.readBits(7);
      switch (nextByte) {
         case 5:
            int cancelCommandSpecifier = this.readBits(7);
            if (cancelCommandSpecifier != 34) {
               return false;
            }
            break;
         case 29:
            if (!this.readSoundCommandSpecifier()) {
               return false;
            }
         case 34:
         case 37:
            break;
         default:
            return false;
      }

      this.readBits(this._bitsRemaining);
      return true;
   }

   private boolean readSoundCommandSpecifier() {
      int nextByte = this.readBits(3);
      switch (nextByte) {
         case 0:
            return false;
         case 1:
         default:
            this.readSongTitle();
            return this.readTemporarySong();
         case 2:
            return this.readTemporarySong();
      }
   }

   private void readSongTitle() {
      int textLength = this.readBits(4);
      this._tuneName = this.readString(textLength);
   }

   private boolean readTemporarySong() {
      int songSequenceLength = this.readBits(8);

      for (int i = 0; i < songSequenceLength; i++) {
         if (!this.readSongPattern()) {
            return false;
         }
      }

      return true;
   }

   private boolean readSongPattern() {
      int patternHeaderId = this.readBits(3);
      if (patternHeaderId != 0) {
         return false;
      }

      this.readBits(2);
      this.readBits(4);
      int patternSpecifier = this.readBits(8);
      if (patternSpecifier == 0) {
         return true;
      }

      int patternLength = patternSpecifier;

      for (int i = 0; i < patternLength; i++) {
         if (!this.readPatternInstruction()) {
            return false;
         }
      }

      return true;
   }

   private boolean readPatternInstruction() {
      int nextByte = this.readBits(3);
      switch (nextByte) {
         case 0:
            return false;
         case 1:
         default:
            return this.readNoteInstruction();
         case 2:
            this._scale = this.readBits(2);
            return true;
         case 3:
            this._style = this.readBits(2);
            return true;
         case 4:
            this._tempo = TEMPOS[this.readBits(5)];
            return true;
         case 5:
            this.readBits(4);
            return true;
      }
   }

   private boolean readNoteInstruction() {
      int noteValue = this.readBits(4);
      int noteDuration = this.readBits(3);
      int noteDurationSpecifier = this.readBits(2);
      int freq = _noteFreq[noteValue + this._scale * 13];
      int duration;
      switch (noteDuration) {
         case -1:
            return false;
         case 0:
         default:
            duration = this._tempo << 2;
            break;
         case 1:
            duration = this._tempo << 1;
            break;
         case 2:
            duration = this._tempo;
            break;
         case 3:
            duration = this._tempo >> 1;
            break;
         case 4:
            duration = this._tempo >> 2;
            break;
         case 5:
            duration = this._tempo >> 3;
      }

      switch (noteDurationSpecifier) {
         case -1:
            return false;
         case 0:
            break;
         case 1:
         default:
            duration = 3 * (duration >> 1);
            break;
         case 2:
            duration = 7 * (duration >> 2);
            break;
         case 3:
            duration = duration / 3 << 1;
      }

      int gap = 0;
      switch (this._style) {
         case -1:
            return false;
         case 0:
         default:
            gap = this._tempo >> 5;
         case 1:
            break;
         case 2:
            gap = this._tempo >> 4;
      }

      duration -= gap;
      short[] tune = this.getBuzzerTune();
      if (tune.length >= 2) {
         int previousNote = tune[tune.length - 2];
         if (previousNote == freq) {
            this.addNote(0, this._tempo >> 5);
         }
      }

      this.addNote(freq, duration);
      if (gap != 0) {
         this.addNote(0, gap);
      }

      return true;
   }
}

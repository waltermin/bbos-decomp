package net.rim.device.internal.media;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Vector;

public class MidiUtilities {
   public static final short SMPTE_MILLISECOND_RESOLUTION = -6360;
   public static final int DEFAULT_TEMPO = 120;
   public static final int UNDEFINED_RESOLUTION = 0;
   private static final int NUM_HEADER_BYTES = 14;
   private static final int NUM_NOTE_EVENT_BYTES = 3;
   private static final int END_OF_TRACK_EVENT = 16723712;
   private static final int TEMPO_EVENT = 16732419;

   public static byte[] convertNoteDurationToMidi(short[] tune, int timestampResolution, int tempo) {
      int numTrackBytes = 8;
      int numDataBytes = tune.length >> 1;
      Vector timeStamps = new Vector();

      for (int i = 1; i < tune.length; i += 2) {
         byte[] currentVLQTimeStamp = getVariableLengthQuantityValue(tune[i] & 65535);
         timeStamps.addElement(currentVLQTimeStamp);
         numDataBytes += currentVLQTimeStamp.length;
      }

      numDataBytes += tune.length * 3;
      if (tempo != 120) {
         numDataBytes += 11;
      } else {
         numDataBytes += 4;
      }

      numTrackBytes += numDataBytes;

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream(14 + numTrackBytes);
         DataOutputStream resultDos = new DataOutputStream(baos);
         resultDos.writeInt(1297377380);
         resultDos.writeInt(6);
         resultDos.writeShort(0);
         resultDos.writeShort(1);
         if (timestampResolution == 0) {
            resultDos.writeShort(-6360);
         } else {
            resultDos.writeShort(timestampResolution);
         }

         resultDos.writeInt(1297379947);
         resultDos.writeInt(numDataBytes);
         if (tempo != 120) {
            int tempoInMicrosecondsPerQuarterNote = 60000000 / tempo;
            resultDos.writeInt(16732419);
            resultDos.writeByte(tempoInMicrosecondsPerQuarterNote >> 16 & 0xFF);
            resultDos.writeByte(tempoInMicrosecondsPerQuarterNote >> 8 & 0xFF);
            resultDos.writeByte(tempoInMicrosecondsPerQuarterNote & 0xFF);
         }

         for (int i = 0; i < tune.length / 2; i++) {
            int note = tune[2 * i];
            resultDos.writeByte(0);
            resultDos.writeByte(144);
            if (note == -1) {
               note = 0;
               resultDos.writeByte(note);
               resultDos.writeByte(0);
            } else {
               resultDos.writeByte(note);
               resultDos.writeByte(127);
            }

            resultDos.write((byte[])timeStamps.elementAt(i));
            resultDos.writeByte(128);
            resultDos.writeByte(note);
            resultDos.writeByte(127);
         }

         resultDos.writeInt(16723712);
         resultDos.flush();
         resultDos.close();
         return baos.toByteArray();
      } finally {
         ;
      }
   }

   public static byte[] getVariableLengthQuantityValue(int value) {
      if (value > 268435455) {
         throw new IllegalArgumentException();
      }

      int buffer = value & 127;
      int numBytes = 1;

      while ((value >>= 7) != 0) {
         numBytes++;
         buffer <<= 8;
         buffer |= value & 127 | 128;
      }

      byte[] result = new byte[numBytes];

      for (int i = 0; i < numBytes; i++) {
         byte currentByte = (byte)(buffer >> i * 8 & 0xFF);
         result[i] = currentByte;
      }

      return result;
   }
}

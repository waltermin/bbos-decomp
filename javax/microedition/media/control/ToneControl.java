package javax.microedition.media.control;

import javax.microedition.media.Control;

public interface ToneControl extends Control {
   byte VERSION;
   byte TEMPO;
   byte RESOLUTION;
   byte BLOCK_START;
   byte BLOCK_END;
   byte PLAY_BLOCK;
   byte SET_VOLUME;
   byte REPEAT;
   byte C4;
   byte SILENCE;

   void setSequence(byte[] var1);
}

package javax.microedition.media.control;

import javax.microedition.media.Control;

public interface MIDIControl extends Control {
   int NOTE_ON;
   int CONTROL_CHANGE;

   boolean isBankQuerySupported();

   int[] getProgram(int var1);

   int getChannelVolume(int var1);

   void setProgram(int var1, int var2, int var3);

   void setChannelVolume(int var1, int var2);

   int[] getBankList(boolean var1);

   int[] getProgramList(int var1);

   String getProgramName(int var1, int var2);

   void shortMidiEvent(int var1, int var2, int var3);

   int longMidiEvent(byte[] var1, int var2, int var3);

   String getKeyName(int var1, int var2, int var3);
}

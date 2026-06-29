package net.rim.device.internal.vad;

import net.rim.vm.Persistable;

public final class VADParameters implements Persistable {
   public int _confirmation = 0;
   public int _sensitivity = 65;
   public boolean _playPrompts = true;
   public boolean _playDigits = true;
   public boolean _playNames = true;
   public int _ttsSpeed = 125;
   public int _ttsVolume = 0;
   public int _language = 1;
   public int _location = 2;
   public int _extension = 1;
   public static final int VAD_PARAM_AUTO = 0;
   public static final int VAD_PARAM_ON = 1;
   public static final int VAD_PARAM_OFF = 2;
}

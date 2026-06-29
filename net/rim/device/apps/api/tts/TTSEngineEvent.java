package net.rim.device.apps.api.tts;

public class TTSEngineEvent {
   private int _type;
   private int _id;
   private Object _listener;
   public static final int TTS_EVENT_STARTED;
   public static final int TTS_EVENT_ENDED;
   public static final int TTS_EVENT_CANCELLED;
   public static final int TTS_ENGINE_STARTED;
   public static final int TTS_ENGINE_STOPPED;
   public static final int TTS_ENGINE_DEFOCUSED;

   public TTSEngineEvent(int type, int id, Object listener) {
      this._type = type;
      this._id = id;
      this._listener = listener;
   }

   public TTSEngineEvent(int type) {
      this._type = type;
   }

   public int getType() {
      return this._type;
   }

   public int getId() {
      return this._id;
   }

   public Object getListener() {
      return this._listener;
   }
}

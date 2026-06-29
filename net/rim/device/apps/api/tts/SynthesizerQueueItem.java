package net.rim.device.apps.api.tts;

public class SynthesizerQueueItem {
   private TTSListener _source;
   private TTSManager _engine;
   private String _text;
   private Object _listener;
   private int _id;
   private int _state = 1;
   private boolean _cancelPending;
   private boolean _cancelling;
   public static final int STATE_QUEUED;
   public static final int STATE_STARTING;
   public static final int STATE_STARTED;
   public static final int STATE_INTERRUPTED;
   public static final int STATE_COMPLETE;

   public SynthesizerQueueItem(TTSListener source, TTSManager engine, String text, Object listener, int id) {
      this._source = source;
      this._engine = engine;
      this._text = text;
      this._listener = listener;
      this._id = id;
   }

   public Object getListener() {
      return this._listener;
   }

   public TTSListener getTTSEngineListener() {
      return this._source;
   }

   public String getText() {
      return this._text;
   }

   public int getId() {
      return this._id;
   }

   public void cancel() {
      if (this._state == 1) {
         this._source.notifyTTSEngineListener(new TTSEngineEvent(117440544, this._id, this._listener));
      } else if (this._state == 2) {
         this._cancelPending = true;
      } else {
         if (this._state == 4) {
            this._cancelling = true;
            this._engine.cancelTTS();
         }
      }
   }

   public void updateState(int state) {
      this._state = state;
      switch (this._state) {
         case 4:
            this._source.notifyTTSEngineListener(new TTSEngineEvent(117440514, this._id, this._listener));
            if (this._cancelPending) {
               this._cancelling = true;
               this._engine.cancelTTS();
               return;
            }
            break;
         case 8:
            this._source.notifyTTSEngineListener(new TTSEngineEvent(117440544, this._id, this._listener));
            if (!this._cancelling) {
               this._source.clearQueueTop(this);
               this._source.notifyTTSEngineListener(new TTSEngineEvent(3));
               return;
            }
            break;
         case 16:
            this._source.notifyTTSEngineListener(new TTSEngineEvent(117440516, this._id, this._listener));
            this._source.clearQueueTop(this);
      }
   }
}

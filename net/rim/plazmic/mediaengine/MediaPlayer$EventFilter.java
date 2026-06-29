package net.rim.plazmic.mediaengine;

class MediaPlayer$EventFilter implements MediaListener {
   final int[] PUBLIC_EVENTS;
   private final MediaPlayer this$0;

   MediaPlayer$EventFilter(MediaPlayer _1) {
      this.this$0 = _1;
      this.PUBLIC_EVENTS = new int[]{3, 7, -805044219, 1651663662, 10, -804651007, 51, -805044187};
   }

   @Override
   public final void mediaEvent(Object sender, int event, int eventParam, Object data) {
      if (this.this$0._internalListener != null) {
         this.this$0._internalListener.mediaEvent(sender, event, eventParam, data);
      }

      for (int k = 0; k < this.PUBLIC_EVENTS.length; k++) {
         if (this.PUBLIC_EVENTS[k] == event) {
            this.this$0.fireMediaEvent(event, eventParam, data);
            return;
         }
      }
   }
}

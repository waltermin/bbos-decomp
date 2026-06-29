package net.rim.device.apps.internal.videorecorder;

import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;

final class VideoRecorderScreen$TranscodeWrapperVerb extends Verb {
   private Verb _wrappedVerb;
   private Object _invokeParameter;
   private final VideoRecorderScreen this$0;

   public VideoRecorderScreen$TranscodeWrapperVerb(VideoRecorderScreen _1, Verb wrappedVerb) {
      super(wrappedVerb.getOrdering());
      this.this$0 = _1;
      this._wrappedVerb = wrappedVerb;
   }

   @Override
   public final int getOrdering() {
      return this._wrappedVerb.getOrdering();
   }

   @Override
   public final RIMModel getRIMModel() {
      return this._wrappedVerb.getRIMModel();
   }

   @Override
   public final int getVerbGroupId() {
      return this._wrappedVerb.getVerbGroupId();
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.transcodeVideo();
      this._invokeParameter = parameter;
      this.this$0._delayedAction = new VideoRecorderScreen$TranscodeWrapperVerb$1(this);
      return null;
   }

   @Override
   public final String toString() {
      return this._wrappedVerb.toString();
   }
}

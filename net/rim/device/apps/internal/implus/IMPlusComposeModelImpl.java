package net.rim.device.apps.internal.implus;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.implus.IMPlusComposeModel;

final class IMPlusComposeModelImpl implements IMPlusComposeModel {
   private Verb _useOnceVerb;
   private Recognizer _recognizer;
   private String _useEntryPrefix;
   private long _objectType;

   public IMPlusComposeModelImpl(Verb useOnceVerb, Recognizer recognizer, String useEntryPrefix, long objectType) {
      this._useOnceVerb = useOnceVerb;
      this._recognizer = recognizer;
      this._useEntryPrefix = useEntryPrefix;
      this._objectType = objectType;
   }

   @Override
   public final Verb getUseOnceVerb() {
      return this._useOnceVerb;
   }

   @Override
   public final Recognizer getRecognizer() {
      return this._recognizer;
   }

   @Override
   public final String getUseEntryPrefix() {
      return this._useEntryPrefix;
   }

   @Override
   public final long getObjectType() {
      return this._objectType;
   }
}

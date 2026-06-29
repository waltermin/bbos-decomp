package net.rim.device.apps.api.framework.verb;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.RIMModel;

public class LastUsedDefaultVerbProvider implements DefaultVerbProvider {
   private RIMModel _model;
   private ContextObject _context;

   public LastUsedDefaultVerbProvider(RIMModel model) {
      this(model, null);
   }

   public LastUsedDefaultVerbProvider(RIMModel model, Object context) {
      this._context = ContextObject.clone(context);
      this._context.setFlag(36);
      this._model = model;
   }

   @Override
   public Verb getDefaultVerb(Verb[] verbs) {
      Verb defaultVerb = null;
      if (verbs != null && verbs.length > 1 && this._model instanceof DefaultProvider) {
         DefaultProvider defaultProvider = (DefaultProvider)this._model;
         this._context.put(6609423255094033855L, new Object(verbs[0].getVerbGroupId()));
         this._context.put(666175809445784644L, verbs);
         defaultVerb = (Verb)defaultProvider.getDefault(null, this._context);
         this._context.remove(666175809445784644L);
         this._context.remove(6609423255094033855L);
      }

      return defaultVerb;
   }
}

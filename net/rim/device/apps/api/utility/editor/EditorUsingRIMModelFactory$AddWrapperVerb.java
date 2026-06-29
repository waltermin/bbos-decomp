package net.rim.device.apps.api.utility.editor;

import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;

public class EditorUsingRIMModelFactory$AddWrapperVerb extends Verb {
   Verb _addModelVerb;
   private final EditorUsingRIMModelFactory this$0;

   public EditorUsingRIMModelFactory$AddWrapperVerb(EditorUsingRIMModelFactory _1, Verb addModelVerb) {
      super(addModelVerb.getOrdering());
      this.this$0 = _1;
      this._addModelVerb = addModelVerb;
   }

   public Verb getWrappedVerb() {
      return this._addModelVerb;
   }

   @Override
   public String toString() {
      return this._addModelVerb.toString();
   }

   @Override
   public Object invoke(Object context) {
      Object result = this._addModelVerb.invoke(context);
      if (result instanceof RIMModel) {
         this.this$0.insertModel(result);
         this.this$0._modelInsertedViaMenu = true;
         return null;
      }

      if (result instanceof RIMModel[]) {
         RIMModel[] models = (RIMModel[])result;

         for (int i = 0; i < models.length; i++) {
            if (models[i] != null) {
               this.this$0.insertModel(models[i]);
               this.this$0._modelInsertedViaMenu = true;
            }
         }
      }

      return null;
   }
}

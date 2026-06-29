package net.rim.device.apps.internal.implus;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.editor.UseOnceEditorScreen;

final class IMPlusUseOnceAddressVerb extends Verb {
   private boolean _doCompose;
   private int _promptResourceId;
   private int _menuResourceId;
   private long _objectType;

   private IMPlusUseOnceAddressVerb(int menuOrdering, int menuResourceId, int promptResourceId, long objectType, boolean doCompose) {
      super(menuOrdering);
      this._promptResourceId = promptResourceId;
      this._menuResourceId = menuResourceId;
      this._objectType = objectType;
      this._doCompose = doCompose;
   }

   public static final Verb newUseOnceAddressVerb(long objectType, boolean doCompose) {
      IMPlusUseOnceAddressVerb newVerb = null;
      if (objectType == 4439968724864684903L) {
         return new IMPlusUseOnceAddressVerb(327957, 8, 7, 4439968724864684903L, doCompose);
      }

      if (objectType == -7875293227724358566L) {
         return new IMPlusUseOnceAddressVerb(328192, 15, 14, -7875293227724358566L, doCompose);
      }

      if (objectType == 3797587162219887872L) {
         return new IMPlusUseOnceAddressVerb(327973, 19, 18, 3797587162219887872L, doCompose);
      }

      if (objectType == 2862138288634470671L) {
         newVerb = new IMPlusUseOnceAddressVerb(328016, 23, 22, 2862138288634470671L, doCompose);
      }

      return newVerb;
   }

   @Override
   public final Object invoke(Object param) {
      String initialValue = (String)ContextObject.get(param, 253);
      Object result = UseOnceEditorScreen.showUseOnceScreen(IMPlusResources.getString(this._promptResourceId), this._objectType, initialValue, param);
      if (result != null && this._doCompose) {
         Verb composeVerb = IMPlusCmimeListener.getInstance()._composeIMPlusVerb;
         ContextObject contextObject = ContextObject.clone(param);
         contextObject.remove(253);
         contextObject.put(254, result);
         result = composeVerb.invoke(contextObject);
      }

      return result;
   }

   @Override
   public final String toString() {
      return IMPlusResources.getString(this._menuResourceId);
   }
}

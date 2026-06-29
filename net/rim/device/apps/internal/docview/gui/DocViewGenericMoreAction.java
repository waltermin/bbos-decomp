package net.rim.device.apps.internal.docview.gui;

import net.rim.device.apps.api.framework.model.ContextObject;

final class DocViewGenericMoreAction {
   private ContextObject _context;
   private ClientRequest _customRequest;
   private ClientRequest _originalRequest;
   private DocViewMoreVerb _moreVerb;
   private DocViewMoreVerb _moreAllVerb;
   private DocViewMoreVerb _autoMoreVerb;
   private DocViewMoreVerb _autoMoreAllVerb;
   private boolean _validMore;
   private boolean _validMoreAll;

   DocViewGenericMoreAction(
      Object context,
      ClientRequest originalRequest,
      DocViewMoreVerb moreVerb,
      DocViewMoreVerb moreAllVerb,
      DocViewMoreVerb autoMoreVerb,
      DocViewMoreVerb autoMoreAllVerb
   ) {
      this._context = ContextObject.castOrCreate(context).clone();
      this._customRequest = new ClientRequest();
      this._originalRequest = originalRequest;
      ContextObject.put(this._context, -7432523643332070209L, this._customRequest);
      this._moreVerb = moreVerb;
      this._moreAllVerb = moreAllVerb;
      this._autoMoreVerb = autoMoreVerb;
      this._autoMoreAllVerb = autoMoreAllVerb;
      this._validMore = this._moreVerb != null && this._autoMoreVerb != null;
      this._validMoreAll = this._moreAllVerb != null && this._autoMoreAllVerb != null;
   }

   final boolean invoke(MoreDescriptor descriptor, boolean autoMore, boolean allowDuplicateRequest) {
      if (!this._validMore) {
         return false;
      }

      this._customRequest.setRequest(this._originalRequest);
      if (descriptor._chunkSize != -1 && !this._validMoreAll) {
         descriptor._chunkSize = -1;
         if (descriptor._partIndex == 1006) {
            descriptor._partIndex = 1005;
         }
      }

      this._customRequest.setCustomRequest(descriptor);
      DocViewMoreVerb targetVerb = null;
      if (descriptor._chunkSize != -1) {
         targetVerb = autoMore ? this._autoMoreAllVerb : this._moreAllVerb;
      } else {
         targetVerb = autoMore ? this._autoMoreVerb : this._moreVerb;
      }

      boolean idleProcessorRequest = ContextObject.getPrivateFlag(this._context, 2945628545186852484L, 0);
      if (!idleProcessorRequest && targetVerb._viewerScreen != null && !targetVerb._viewerScreen.preMoreRequest(this._context)) {
         return false;
      }

      boolean crtDuplicateValue = targetVerb._duplicateMore;
      boolean changeBack = false;
      if (crtDuplicateValue != allowDuplicateRequest) {
         targetVerb._duplicateMore = allowDuplicateRequest;
         changeBack = true;
      }

      targetVerb.invoke(this._context);
      if (changeBack) {
         targetVerb._duplicateMore = crtDuplicateValue;
      }

      return true;
   }
}

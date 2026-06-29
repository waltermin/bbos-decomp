package net.rim.device.apps.api.messaging.ui;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

final class ViewFolderVerb$SelectWrapperVerb extends SelectFolderVerb {
   private Verb _selectVerb;
   private Object _context;
   private boolean _selected;

   private ViewFolderVerb$SelectWrapperVerb(Verb selectVerb, Object context) {
      super(selectVerb.getOrdering());
      this._selectVerb = selectVerb;
      this._context = context;
   }

   @Override
   public final String toString() {
      return this._selectVerb.toString();
   }

   @Override
   public final Object invoke(Object context) {
      this._selected = true;
      ContextObject.setFlag(this._context, 39);
      this._selectVerb.invoke(context);
      return this._context;
   }

   @Override
   public final boolean selectionMade() {
      return this._selected;
   }

   @Override
   public final void clearSelection() {
      this._selected = false;
   }

   ViewFolderVerb$SelectWrapperVerb(Verb x0, Object x1, ViewFolderVerb$1 x2) {
      this(x0, x1);
   }
}

package net.rim.device.apps.internal.mms.ui;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;

class MMSPresentationField$1 implements Enumeration {
   int _nextIndex;
   Vector _fields;
   private final MMSPresentationField this$0;

   MMSPresentationField$1(MMSPresentationField _1) {
      this.this$0 = _1;
   }

   @Override
   public boolean hasMoreElements() {
      if (this._fields == null) {
         this._fields = this.findValidFields();
      }

      return this._fields.size() == 0 ? false : this._nextIndex <= this._fields.size();
   }

   @Override
   public Object nextElement() {
      Object retObject;
      if (this._nextIndex < this._fields.size()) {
         retObject = ((MMSAttachment)this._fields.elementAt(this._nextIndex)).getName();
      } else {
         MMSPresentationModel presentation = this.this$0;
         retObject = presentation.getName();
      }

      this._nextIndex++;
      return retObject;
   }

   private Vector findValidFields() {
      Vector fields = (Vector)(new Object());
      int count = this.this$0.getFieldCount();

      for (int idx = 0; idx < count; idx++) {
         Field f = this.this$0.getField(idx);
         if (f instanceof MMSAttachment) {
            fields.addElement(f);
         }
      }

      return fields;
   }
}

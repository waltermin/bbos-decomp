package net.rim.device.apps.internal.mms.ui;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;

public final class RecipientFieldManager extends VerticalFieldManager {
   private Object _context;
   private String _label;
   boolean _editable;
   boolean _dirty;

   public RecipientFieldManager(String label, boolean editable, Object context) {
      this._label = label;
      this._editable = editable;
      this._context = ContextObject.clone(context);
      ContextObject.setFlag(this._context, 1);
      ContextObject.remove(this._context, 252);
   }

   public RecipientFieldManager(Vector vector, String label, boolean editable, Object context) {
      this(label, editable, context);
      if (vector != null) {
         this.addRecipients(vector);
      }
   }

   @Override
   public final boolean isDirty() {
      return this._dirty || super.isDirty();
   }

   @Override
   public final void setDirty(boolean dirty) {
      this._dirty = dirty;
      super.setDirty(dirty);
   }

   public final void addRecipients(Vector vector) {
      int count = vector.size();

      for (int idx = 0; idx < count; idx++) {
         this.addRecipient(vector.elementAt(idx), null);
      }
   }

   public final void addRecipient(Object recipient, Object addressEntry) {
      Object context = ContextObject.clone(this._context);
      if (addressEntry != null) {
         ContextObject.put(context, 252, addressEntry);
      }

      this.add(new RecipientField(this._label, recipient, context));
   }

   public final void replaceRecipient(Field originalField, Object recipient) {
      Object context = ContextObject.clone(this._context);
      this.replace(originalField, new RecipientField(this._label, recipient, context));
   }

   public final Vector getRecipients() {
      Vector vector = null;
      int count = this.getFieldCount();
      if (count > 0) {
         vector = (Vector)(new Object());

         for (int idx = 0; idx < count; idx++) {
            Field field = this.getField(idx);
            Object recipient = field.getCookie();
            vector.addElement(recipient);
         }
      }

      return vector;
   }
}

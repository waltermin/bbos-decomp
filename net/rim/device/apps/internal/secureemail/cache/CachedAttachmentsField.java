package net.rim.device.apps.internal.secureemail.cache;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.Array;

public class CachedAttachmentsField extends CachedField {
   private Object[] _attachments = new Object[0];

   public void fillManagerHeader(Manager manager, Object context) {
      if (!ContextObject.getFlag(context, 101)) {
         int numAttachments = this._attachments.length;
         if (numAttachments > 0) {
            StringBuffer buffer = (StringBuffer)(new Object());
            buffer.append('[');
            buffer.append(numAttachments);
            buffer.append(numAttachments > 1 ? EmailResources.getString(141) : EmailResources.getString(140));
            buffer.append(']');
            manager.add((Field)(new Object(buffer.toString())));
         }
      }
   }

   public void fillManagerFooter(Manager manager, Object context) {
      if (!ContextObject.getFlag(context, 101)) {
         int numAttachments = this._attachments.length;
         if (numAttachments > 0) {
            manager.add((Field)(new Object()));
         }

         for (int i = 0; i < numAttachments; i++) {
            Object var10000 = this._attachments[i];
            if (this._attachments[i] instanceof Object) {
               FieldProvider fieldProvider = (FieldProvider)var10000;
               manager.add(fieldProvider.getField(context));
            }
         }
      }
   }

   public void addAttachment(Object attachment) {
      int numAttachments = this._attachments.length;
      Array.resize(this._attachments, numAttachments + 1);
      this._attachments[numAttachments] = attachment;
   }
}

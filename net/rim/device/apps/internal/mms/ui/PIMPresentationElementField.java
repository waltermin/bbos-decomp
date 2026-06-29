package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.verbs.DeleteFieldVerb;

final class PIMPresentationElementField extends HorizontalFieldManager implements PresentationElement, MMSAttachment {
   private MMSAttachment _attachment;
   private boolean _isEditable;
   private boolean _moveMode;

   public PIMPresentationElementField(MMSAttachment attachment, boolean isEditable, Bitmap bitmap, long factoryID) {
      super(1152921504606846976L);
      this._attachment = attachment;
      this._isEditable = isEditable;
      Object pimObject = null;
      byte[] data = MMSUtilities.decrypt(attachment.getData());
      if (data != null && factoryID != 0) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         Object item = ar.get(factoryID);
         if (item instanceof Factory) {
            Factory factory = (Factory)item;
            ContextObject contextObject = new ContextObject();
            contextObject.put(8849067667159082262L, data);
            String encoding = attachment.getCharset();
            if (encoding == null) {
               encoding = "ASCII";
            }

            contextObject.put(253, encoding);
            contextObject.put(4086083307293257364L, Boolean.TRUE);
            pimObject = factory.createInstance(contextObject);
         }
      }

      if (pimObject == null || bitmap == null) {
         bitmap = Bitmap.getBitmapResource("BrokenImage.gif");
      }

      Verb deleteVerb = this._isEditable ? new DeleteFieldVerb(this) : null;
      this.add(new BitmapField(bitmap));
      this.add(new PIMPresentationElementField$PIMField(this, pimObject, attachment.getName(), deleteVerb));
   }

   @Override
   public final void copyTo(MMSPresentationModel target) {
      target.addPresentationElement(this._attachment.getName(), this._attachment.getType(), this._isEditable);
   }

   @Override
   public final boolean canMove() {
      return this._isEditable;
   }

   @Override
   public final void move(boolean mode) {
      if (this._moveMode != mode) {
         this._moveMode = mode;
         this.invalidate();
      }
   }

   @Override
   public final String getName() {
      return this._attachment.getName();
   }

   @Override
   public final int getType() {
      return this._attachment.getType();
   }

   @Override
   public final byte[] getData() {
      return this._attachment.getData();
   }

   @Override
   public final String getCharset() {
      return this._attachment.getCharset();
   }

   @Override
   public final int getDataSize() {
      return this._attachment.getDataSize();
   }
}

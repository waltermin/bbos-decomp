package net.rim.device.apps.internal.mms.model;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.ui.MMSPresentationField;

final class DraftPresentationModel implements MMSPresentationModel, FieldProvider {
   Vector _content = (Vector)(new Object());
   static final int ATTACHMENT_NAME;
   static final int ATTACHMENT_TYPE;
   static final int IS_EDITABLE;
   static final int SLIDE_BREAK;

   DraftPresentationModel() {
   }

   DraftPresentationModel(MMSAttachment attachment) {
      this.initialize(attachment.getData());
   }

   @Override
   public final int getType() {
      return 65536;
   }

   @Override
   public final String getName() {
      return "net_rim_DraftPresentation";
   }

   @Override
   public final byte[] getData() {
      DataBuffer dataBuffer = (DataBuffer)(new Object());
      SyncBuffer syncBuffer = (SyncBuffer)(new Object(dataBuffer, 0, 0));
      int count = this._content.size();

      for (int idx = 0; idx < count; idx++) {
         ((PresentationPart)this._content.elementAt(idx)).writeData(syncBuffer);
      }

      return dataBuffer.toArray();
   }

   @Override
   public final String getCharset() {
      return null;
   }

   @Override
   public final int getDataSize() {
      int dataSize = 0;
      int count = this._content.size();

      for (int idx = 0; idx < count; idx++) {
         dataSize += ((PresentationPart)this._content.elementAt(idx)).getTaggedFieldSize();
      }

      return dataSize;
   }

   @Override
   public final void addPresentationElement(MMSAttachment attachment, boolean isEditable) {
      this.addPresentationElement(attachment.getName(), attachment.getType(), isEditable);
   }

   @Override
   public final void addPresentationElement(MMSAttachment attachment, boolean isEditable, boolean isForwardLocked) {
      this.addPresentationElement(attachment.getName(), attachment.getType(), isEditable, isForwardLocked);
   }

   @Override
   public final void addPresentationElement(String filename, int type, boolean isEditable) {
      this.addPresentationElement(filename, type, isEditable, false);
   }

   @Override
   public final void addPresentationElement(String filename, int type, boolean isEditable, boolean isForwardLocked) {
      switch (type) {
         default:
            this._content.addElement(new ElementPart(filename, type, isEditable, isForwardLocked));
         case 8:
         case 20:
         case 65537:
         case 65540:
      }
   }

   @Override
   public final void addSlideBreak(int duration, boolean isEditable) {
      this._content.addElement(new SlideBreakPart(duration, isEditable));
   }

   @Override
   public final boolean canAddPresentationElement(int attachmentType, long attachmentSize) {
      return true;
   }

   @Override
   public final void copyTo(MMSPresentationModel target) {
      int count = this._content.size();

      for (int idx = 0; idx < count; idx++) {
         ((PresentationPart)this._content.elementAt(idx)).copyTo(target);
      }
   }

   @Override
   public final Field getField(Object context) {
      return new MMSPresentationField(this, context);
   }

   @Override
   public final int getOrder(Object context) {
      return 5700;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   private final void initialize(byte[] data) {
      try {
         DataBuffer dataBuffer = (DataBuffer)(new Object());
         dataBuffer.setData(data, 0, data.length);
         SyncBuffer syncBuffer = (SyncBuffer)(new Object(dataBuffer, 0, 0));
         int type = -1;
         boolean isEditable = false;

         while (!syncBuffer.isEmpty()) {
            int position = syncBuffer.getPosition();
            int fieldType = syncBuffer.getFieldType(true);
            switch (fieldType) {
               case 0:
                  break;
               case 1:
                  String name = syncBuffer.getString();
                  this.addPresentationElement(name, type, isEditable);
                  break;
               case 2:
               default:
                  type = syncBuffer.getInt();
                  break;
               case 3:
                  isEditable = syncBuffer.getInt() != 0;
                  break;
               case 4:
                  this.addSlideBreak(syncBuffer.getInt(), isEditable);
            }

            syncBuffer.setPosition(position);
            syncBuffer.skipField();
         }
      } finally {
         return;
      }
   }
}

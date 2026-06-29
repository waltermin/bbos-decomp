package net.rim.device.apps.internal.mms.ui;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.model.AliasedAttachment;
import net.rim.device.apps.internal.mms.model.PresentationModelFactory;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.apps.internal.mms.service.DRMConverter;
import net.rim.device.internal.ui.Image;

public final class MMSPresentationField extends VerticalFieldManager implements MMSPresentationModel, AttachmentDataProvider {
   private MMSMessageModel _message;
   private MMSPresentationModel _immutablePresentation;
   private boolean _hasEditableElement;
   private Field _fieldToMove;
   private int _originalIndex;
   private int _currentIndex;
   public static final int ABSOLUTE_MAX_MESSAGE_SIZE;
   private static final int MAX_NAME_LENGTH;

   final boolean isImmutable() {
      return this._immutablePresentation != null;
   }

   final boolean hasTemplate() {
      return this.hasAttachment("net_rim_Template");
   }

   public final void checkSlideBreaks() {
      if (this.hasNonSMILContent()) {
         this.deleteAllSlideBreaks();
      } else {
         this.deleteRedundantBreaks();
         this.addRequiredBreaks();
      }
   }

   final void reduceImages(int bytesToTrim, int maxImageWidth, int maxImageHeight) {
      Vector v = (Vector)(new Object());
      int total = 0;
      int count = this.getFieldCount();

      for (int idx = 0; idx < count; idx++) {
         Field f = this.getField(idx);
         if (f instanceof BitmapPresentationElementField) {
            BitmapPresentationElementField bitmapField = (BitmapPresentationElementField)f;
            v.addElement(bitmapField);
            total += bitmapField.getDataSize();
         }
      }

      count = v.size();

      for (int idx = 0; idx < count; idx++) {
         BitmapPresentationElementField bitmapField = (BitmapPresentationElementField)v.elementAt(idx);
         int targetSize = (int)((long)bitmapField.getDataSize() * (total - bytesToTrim) / total);
         bitmapField.reduce(targetSize, maxImageWidth, maxImageHeight);
      }
   }

   @Override
   public final void addPresentationElement(String name, int type, boolean isEditable, boolean isForwardLocked) {
      this.addPresentationElement(this._message.getAttachmentDataProvider().getAttachment(name), isEditable, isForwardLocked);
   }

   @Override
   public final void addPresentationElement(MMSAttachment attachment, boolean isEditable) {
      this.addPresentationElement(attachment, isEditable, false);
   }

   @Override
   public final void addPresentationElement(MMSAttachment attachment, boolean isEditable, boolean isForwardLocked) {
      if (isEditable) {
         this._hasEditableElement = true;
      }

      while (attachment.getType() == 72) {
         attachment = DRMConverter.unwrap(attachment);
         if (attachment == null) {
            System.out.println("MMS - DRM parse failure.");
            return;
         }
      }

      if (!isEditable || this.canAddPresentationElement(attachment.getType(), attachment.getDataSize())) {
         int type = attachment.getType();
         attachment = this.ensureUniqueAttachmentName(attachment);
         Field newField = null;
         if (MMSUtilities.isImageType(type)) {
            newField = this.addImage(attachment, isEditable, isForwardLocked);
         } else if (MMSUtilities.isAudioType(type)) {
            newField = this.addAudio(attachment, isEditable);
         } else if (MMSUtilities.isTextType(type)) {
            newField = this.addText(attachment, isEditable);
         } else if (MMSUtilities.isVideoType(type)) {
            this.addVideo(attachment, isEditable);
         } else {
            switch (type) {
               case 2:
               case 8:
               case 15:
               case 20:
               case 38:
               case 59:
               case 69:
               case 65537:
               case 65540:
               case 65541:
                  this.addBrowserContent(attachment, isEditable);
                  break;
               case 6:
               case 773:
                  newField = this.addVCalendar(attachment, isEditable);
                  break;
               case 7:
                  newField = this.addVCard(attachment, isEditable);
                  break;
               case 62:
                  if (attachment.getName().equals("net_rim_Template")) {
                     this.add(new PDUPresentationElementField(attachment, isEditable));
                  }
               case 65536:
                  break;
               default:
                  this.addUnsupportedAttachment(attachment, isEditable);
            }
         }

         if (this._hasEditableElement) {
            this.checkSlideBreaks();
         }

         if (newField != null && isEditable) {
            Object eventLock = this.getAppEventLock();
            if (eventLock != null) {
               synchronized (eventLock) {
                  newField.setFocus();
                  return;
               }
            }
         }
      }
   }

   @Override
   public final void addSlideBreak(int duration, boolean isEditable) {
      this.add(new SlideBreakPresentationElementField(duration, isEditable));
   }

   @Override
   public final void copyTo(MMSPresentationModel target) {
      if (this.isImmutable()) {
         this._immutablePresentation.copyTo(target);
      } else {
         int count = this.getFieldCount();

         for (int idx = 0; idx < count; idx++) {
            PresentationElement element = (PresentationElement)this.getField(idx);
            element.copyTo(target);
         }
      }
   }

   @Override
   public final int getType() {
      return this.isImmutable() ? this._immutablePresentation.getType() : 65536;
   }

   @Override
   public final String getName() {
      return this.isImmutable() ? this._immutablePresentation.getName() : "net_rim_DraftPresentation";
   }

   @Override
   public final byte[] getData() {
      if (!this.isImmutable()) {
         MMSPresentationModel draft = PresentationModelFactory.createInstance(65536);
         this.copyTo(draft);
         this._immutablePresentation = draft;
      }

      return this._immutablePresentation.getData();
   }

   @Override
   public final String getCharset() {
      return this.isImmutable() ? this._immutablePresentation.getCharset() : null;
   }

   @Override
   public final int getDataSize() {
      return this.isImmutable() ? this._immutablePresentation.getDataSize() : 450 + this.getFieldCount() * 50;
   }

   @Override
   public final boolean hasAttachments() {
      return this.isImmutable() ? this._message.getAttachmentDataProvider().hasAttachments() : this.getFieldCount() > 0;
   }

   @Override
   public final boolean hasAttachment(String attachmentName) {
      if (this.isImmutable()) {
         return this._message.getAttachmentDataProvider().hasAttachment(attachmentName);
      }

      MMSPresentationModel presentation = this;
      if (presentation.getName().equals(attachmentName)) {
         return true;
      }

      int count = this.getFieldCount();

      for (int idx = 0; idx < count; idx++) {
         Field f = this.getField(idx);
         if (f instanceof MMSAttachment) {
            MMSAttachment attachment = (MMSAttachment)f;
            if (attachmentName.equals(attachment.getName())) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public final int getAttachmentType(String attachmentName) {
      if (this.isImmutable()) {
         return this._message.getAttachmentDataProvider().getAttachmentType(attachmentName);
      }

      MMSPresentationModel presentation = this;
      if (presentation.getName().equals(attachmentName)) {
         return presentation.getType();
      }

      int count = this.getFieldCount();

      for (int idx = 0; idx < count; idx++) {
         Field f = this.getField(idx);
         if (f instanceof MMSAttachment) {
            MMSAttachment attachment = (MMSAttachment)f;
            if (attachmentName.equals(attachment.getName())) {
               return attachment.getType();
            }
         }
      }

      return -1;
   }

   @Override
   public final int getTotalAttachmentDataSize() {
      if (this.isImmutable()) {
         return this._message.getAttachmentDataProvider().getTotalAttachmentDataSize();
      }

      int dataSize = this.getDataSize();
      int count = this.getFieldCount();

      for (int idx = 0; idx < count; idx++) {
         Field f = this.getField(idx);
         if (f instanceof MMSAttachment) {
            MMSAttachment attachment = (MMSAttachment)f;
            dataSize += attachment.getDataSize();
         }
      }

      return dataSize;
   }

   @Override
   public final MMSAttachment getAttachment(String attachmentName) {
      if (this.isImmutable()) {
         return this._message.getAttachmentDataProvider().getAttachment(attachmentName);
      }

      MMSPresentationModel presentation = this;
      if (presentation.getName().equals(attachmentName)) {
         return presentation;
      }

      int count = this.getFieldCount();

      for (int idx = 0; idx < count; idx++) {
         Field f = this.getField(idx);
         if (f instanceof MMSAttachment) {
            MMSAttachment attachment = (MMSAttachment)f;
            if (attachmentName.equals(attachment.getName())) {
               return attachment;
            }
         }
      }

      return null;
   }

   @Override
   public final Enumeration attachmentNames() {
      return this.isImmutable() ? this._message.getAttachmentDataProvider().attachmentNames() : new MMSPresentationField$1(this);
   }

   @Override
   public final void addPresentationElement(String name, int type, boolean isEditable) {
      this.addPresentationElement(name, type, isEditable, false);
   }

   @Override
   public final boolean canAddPresentationElement(int attachmentType, long attachmentSize) {
      if (this.isAttachmentTooBig(attachmentType, attachmentSize, 1500000, false)) {
         Dialog.alert(MMSResources.getString(103));
         return false;
      }

      switch (MMSClientServiceBook.getRestrictedSizeMode()) {
         case 0:
            break;
         case 1:
         default:
            if (this.isAttachmentTooBig(
               attachmentType, attachmentSize, MMSClientServiceBook.getMaxMessageSize(), MMSClientServiceBook.allowImageReductionBeforeSend()
            )) {
               Dialog.alert(MMSResources.getString(103));
               return false;
            }
            break;
         case 2:
            if (this.isAttachmentTooBig(attachmentType, attachmentSize, MMSClientServiceBook.getMaxMessageSize(), false)
               && Dialog.ask(3, MMSResources.getString(104)) == -1) {
               return false;
            }
      }

      return true;
   }

   private final Field addVideo(MMSAttachment attachment, boolean isEditable) {
      boolean isStandardContent = MMSUtilities.isVideoType(attachment.getType(), false);
      if (isEditable && !isStandardContent) {
         switch (MMSClientServiceBook.getRestrictedSendMode()) {
            case 0:
               break;
            case 1:
            default:
               Dialog.alert(MMSResources.getString(108));
               return null;
            case 2:
               if (Dialog.ask(3, MMSResources.getString(109)) == -1) {
                  return null;
               }
         }
      }

      Field field = new VideoPresentationElementField(attachment, 1, isEditable, this._message.getAttachmentDataProvider(), this._message.isForwardLocked());
      this.add(field);
      return field;
   }

   private final void addBrowserContent(MMSAttachment attachment, boolean isEditable) {
      this.add(new BrowserPresentationElementField(attachment, isEditable, this._message.getAttachmentDataProvider(), this._message.isForwardLocked()));
   }

   private final Field addVCard(MMSAttachment attachment, boolean isEditable) {
      Bitmap bitmap = MMSResources.getAddressIcon();
      Field field = new PIMPresentationElementField(attachment, isEditable, bitmap, 9048770516632928843L);
      this.add(field);
      return field;
   }

   private final Field addVCalendar(MMSAttachment attachment, boolean isEditable) {
      Bitmap bitmap = MMSResources.getCalendarIcon();
      Field field = new PIMPresentationElementField(attachment, isEditable, bitmap, 7277824740467201558L);
      this.add(field);
      return field;
   }

   private final void addUnsupportedAttachment(MMSAttachment attachment, boolean isEditable) {
      this.add(new PIMPresentationElementField(attachment, isEditable, null, 0));
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      if (this.isMoveMoveActive()) {
         this.moveField(amount);
         return true;
      } else {
         return super.trackwheelRoll(amount, status, time);
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      if (this.isMoveMoveActive()) {
         this.moveField(dy);
         return true;
      } else {
         return super.navigationMovement(dx, dy, status, time);
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (this.isMoveMoveActive()) {
         this.endMoveMode();
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (this.isMoveMoveActive()) {
               this.endMoveMode();
               return true;
            }
         default:
            return super.invokeAction(action);
      }
   }

   private final Field addAudio(MMSAttachment attachment, boolean isEditable) {
      boolean isStandardContent = MMSUtilities.isAudioType(attachment.getType(), false);
      if (isEditable && !isStandardContent) {
         switch (MMSClientServiceBook.getRestrictedSendMode()) {
            case 0:
               break;
            case 1:
            default:
               Dialog.alert(MMSResources.getString(108));
               return null;
            case 2:
               if (Dialog.ask(3, MMSResources.getString(109)) == -1) {
                  return null;
               }
         }
      }

      String label = null;
      if (attachment.getType() == 65539) {
         label = MMSResources.getString(79);
      }

      Field field = new AudioPresentationElementField(attachment, 1, label, isEditable);
      this.add(field);
      return field;
   }

   private final Field addText(MMSAttachment attachment, boolean isEditable) {
      Field field = new TextPresentationElementField(attachment, isEditable);
      this.add(field);
      return field;
   }

   @Override
   public final void delete(Field field) {
      if (this.isImmutable()) {
         throw new Object();
      }

      Object eventLock = this.getAppEventLock();
      if (eventLock != null) {
         synchronized (eventLock) {
            super.delete(field);
         }
      } else {
         super.delete(field);
      }
   }

   private final Object getAppEventLock() {
      Screen screen = this.getScreen();
      if (screen != null) {
         Application app = screen.getApplication();
         if (app != null) {
            return app.getAppEventLock();
         }
      }

      return null;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (!this.isImmutable() && !this.hasTemplate() && this.getFieldCount() > 1) {
         for (Field field = this.getLeafFieldWithFocus(); field.getManager() != null; field = field.getManager()) {
            if (field instanceof PresentationElement) {
               PresentationElement element = (PresentationElement)field;
               if (element.canMove()) {
                  menu.add(new MMSPresentationField$MoveFieldMenuItem(this, field));
                  return;
               }
               break;
            }
         }
      }
   }

   private final boolean isAttachmentTooBig(int attachmentType, long attachmentSize, long maxMessageSize, boolean allowReduction) {
      long totalSize = attachmentSize + this.getTotalAttachmentDataSize();
      if (totalSize <= maxMessageSize) {
         return false;
      }

      if (allowReduction) {
         int imageSize = this.getTotalImageAttachmentDataSize();
         if (MMSUtilities.isImageType(attachmentType)) {
            imageSize = (int)(imageSize + attachmentSize);
         }

         long reducedImageSize = imageSize / 4;
         long newTotalSize = totalSize - imageSize + reducedImageSize;
         if (newTotalSize <= maxMessageSize) {
            return false;
         }
      }

      return true;
   }

   private final int getTotalImageAttachmentDataSize() {
      int dataSize = this.getDataSize();
      int count = this.getFieldCount();

      for (int idx = 0; idx < count; idx++) {
         Field f = this.getField(idx);
         if (f instanceof MMSAttachment) {
            MMSAttachment attachment = (MMSAttachment)f;
            if (MMSUtilities.isImageType(attachment.getType())) {
               dataSize += attachment.getDataSize();
            }
         }
      }

      return dataSize;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (this.isMoveMoveActive()) {
         if (key == '\n') {
            this.endMoveMode();
            return true;
         }

         if (key == 27) {
            this.cancelMoveMode();
            return true;
         }
      }

      return super.keyChar(key, status, time);
   }

   private final Field addImage(MMSAttachment attachment, boolean isEditable, boolean isForwardLocked) {
      Field field = BitmapPresentationElementField.createInstance(attachment, isEditable, isForwardLocked);
      if (field != null) {
         this.add(field);
      }

      return field;
   }

   private final void beginMoveMode(Field fieldToMove) {
      int size = this.getFieldCount();

      for (int i = 0; i < size; i++) {
         Field f = this.getField(i);
         if (f == fieldToMove) {
            this._originalIndex = i;
            break;
         }
      }

      this._currentIndex = this._originalIndex;
      this._fieldToMove = fieldToMove;
      ((PresentationElement)this._fieldToMove).move(true);
   }

   private final void endMoveMode() {
      ((PresentationElement)this._fieldToMove).move(false);
      this._fieldToMove = null;
      this.checkSlideBreaks();
      this.setDirty(true);
   }

   private final void cancelMoveMode() {
      if (this._originalIndex != this._currentIndex) {
         this.delete(this._fieldToMove);
         this.insert(this._fieldToMove, this._originalIndex);
         this._fieldToMove.setFocus();
      }

      ((PresentationElement)this._fieldToMove).move(false);
      this._fieldToMove = null;
   }

   private final boolean isMoveMoveActive() {
      return this._fieldToMove != null;
   }

   private final void moveField(int count) {
      int newIndex = MathUtilities.clamp(0, this._currentIndex + count, this.getFieldCount() - 1);
      if (newIndex != this._currentIndex) {
         Field fieldWithFocus = this.getLeafFieldWithFocus();
         this.delete(this._fieldToMove);
         this.insert(this._fieldToMove, newIndex);
         fieldWithFocus.setFocus();
         this._currentIndex = newIndex;
      }
   }

   public static final void drawMoveFocus(Graphics graphics, Field fieldWithFocus) {
      XYRect extent = fieldWithFocus.getExtent();
      graphics.drawRect(0, 0, extent.width, extent.height);
      Image moveImage = MMSResources.getMoveImage();
      int width = moveImage.getWidth(extent.width, extent.height);
      int height = moveImage.getHeight(extent.width, extent.height);
      int posX = extent.width - width;
      int posY = (extent.height - height) / 2;
      moveImage.paint(graphics, posX, posY, width, height);
   }

   @Override
   public final void add(Field field) {
      if (this.isImmutable()) {
         throw new Object();
      }

      Object eventLock = this.getAppEventLock();
      if (eventLock != null) {
         synchronized (eventLock) {
            super.add(field);
         }
      } else {
         super.add(field);
      }
   }

   private final MMSAttachment ensureUniqueAttachmentName(MMSAttachment attachment) {
      String name = attachment.getName();
      if (!this.hasAttachment(name) && name.length() <= 98) {
         return attachment;
      }

      int dotIndex = name.lastIndexOf(46);
      String root;
      String suffix;
      if (dotIndex < 0) {
         root = name;
         suffix = "";
      } else {
         root = name.substring(0, dotIndex);
         suffix = name.substring(dotIndex);
      }

      if (name.length() > 98) {
         root = root.substring(0, 98 - suffix.length());
         name = ((StringBuffer)(new Object())).append(root).append(suffix).toString();
         if (!this.hasAttachment(name)) {
            return new AliasedAttachment(name, attachment);
         }
      }

      int i = 1;

      while (true) {
         name = ((StringBuffer)(new Object())).append(root).append(Integer.toString(i)).append(suffix).toString();
         if (!this.hasAttachment(name)) {
            return new AliasedAttachment(name, attachment);
         }

         i++;
      }
   }

   public MMSPresentationField(MMSPresentationModel presentation, Object context) {
      super(1152921504606846976L);
      this._message = (MMSMessageModel)ContextObject.get(context, -7651695713744129224L);
      presentation.copyTo(this);
      boolean editableView = ContextObject.getFlag(context, 0);
      if (presentation.getType() != 65536) {
         editableView = false;
      } else if (editableView && !this._hasEditableElement) {
         editableView = false;
      }

      if (!editableView) {
         this._immutablePresentation = presentation;
      }
   }

   private final boolean hasAudioAttachment() {
      for (int index = this.getFieldCount() - 1; index >= 0; index--) {
         Field f = this.getField(index);
         if (f instanceof AudioPresentationElementField) {
            return true;
         }
      }

      return false;
   }

   private final boolean hasNonSMILContent() {
      for (int index = 0; index < this.getFieldCount(); index++) {
         Field f = this.getField(index);
         if (f instanceof MMSAttachment) {
            MMSAttachment attachment = (MMSAttachment)f;
            int type = attachment.getType();
            if (!MMSUtilities.isImageType(type) && !MMSUtilities.isTextType(type) && !MMSUtilities.isAudioType(type) && !MMSUtilities.isVideoType(type)) {
               return true;
            }
         }
      }

      return false;
   }

   private final void deleteAllSlideBreaks() {
      for (int index = this.getFieldCount() - 1; index >= 0; index--) {
         Field f = this.getField(index);
         if (f instanceof SlideBreakPresentationElementField) {
            this.delete(f);
         }
      }
   }

   private final void deleteRedundantBreaks() {
      TextPresentationElementField textField = null;
      Field nonTextField = null;
      Field prevSlideBreakField = null;

      for (int index = 0; index < this.getFieldCount(); index++) {
         Field f = this.getField(index);
         if (f instanceof SlideBreakPresentationElementField) {
            if (nonTextField == null && textField != null && textField.getTextLength() == 0) {
               this.delete(textField);
               this.delete(f);
               textField = null;
               index -= 2;
            } else if (nonTextField == null && textField == null) {
               this.delete(f);
               index--;
            } else {
               textField = null;
               nonTextField = null;
               prevSlideBreakField = f;
            }
         } else if (!(f instanceof TextPresentationElementField)) {
            nonTextField = f;
         } else {
            TextPresentationElementField newTextField = (TextPresentationElementField)f;
            if (textField == null) {
               textField = newTextField;
            } else if (newTextField.getTextLength() == 0) {
               this.delete(newTextField);
               index--;
            } else if (textField.getTextLength() == 0) {
               this.delete(textField);
               index--;
               textField = newTextField;
            }
         }
      }

      if (prevSlideBreakField != null && nonTextField == null && textField != null && textField.getTextLength() == 0) {
         this.delete(textField);
         this.delete(prevSlideBreakField);
      } else {
         if (prevSlideBreakField != null && nonTextField == null && textField == null) {
            this.delete(prevSlideBreakField);
         }
      }
   }

   private final void addRequiredBreaks() {
      Field textField = null;
      Field imageField = null;
      Field audioField = null;
      Field videoField = null;

      for (int index = 0; index < this.getFieldCount(); index++) {
         Field f = this.getField(index);
         if (f instanceof SlideBreakPresentationElementField) {
            if (textField == null) {
               this.insert(new TextPresentationElementField("", true), index);
               index++;
            }

            textField = null;
            imageField = null;
            audioField = null;
            videoField = null;
         } else if (f instanceof TextPresentationElementField) {
            if (textField != null) {
               this.insert(new SlideBreakPresentationElementField(true), index);
               index++;
               imageField = null;
               audioField = null;
               videoField = null;
            }

            textField = f;
         } else if (f instanceof BitmapPresentationElementField) {
            if (imageField != null || videoField != null) {
               if (textField == null) {
                  this.insert(new TextPresentationElementField("", true), index);
                  index++;
               }

               this.insert(new SlideBreakPresentationElementField(true), index);
               index++;
               textField = null;
               audioField = null;
               videoField = null;
            }

            imageField = f;
         } else if (f instanceof AudioPresentationElementField) {
            if (audioField != null || videoField != null) {
               if (textField == null) {
                  this.insert(new TextPresentationElementField("", true), index);
                  index++;
               }

               this.insert(new SlideBreakPresentationElementField(true), index);
               index++;
               textField = null;
               imageField = null;
               videoField = null;
            }

            audioField = f;
         } else if (f instanceof VideoPresentationElementField) {
            if (videoField != null || imageField != null || audioField != null) {
               if (textField == null) {
                  this.insert(new TextPresentationElementField("", true), index);
                  index++;
               }

               this.insert(new SlideBreakPresentationElementField(true), index);
               index++;
               textField = null;
               audioField = null;
               imageField = null;
            }

            videoField = f;
         }
      }

      if (textField == null) {
         this.add(new TextPresentationElementField("", true));
      }
   }
}

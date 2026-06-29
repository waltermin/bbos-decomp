package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.icons.FileIcon;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.ui.component.ImageField;

final class EmailViewerScreen$AttachmentInfoModel implements RIMModel, FieldProvider {
   private int _attachmentCount;

   EmailViewerScreen$AttachmentInfoModel(int attachmentCount) {
      this._attachmentCount = attachmentCount;
   }

   @Override
   public final Field getField(Object context) {
      StringBuffer buffer = (StringBuffer)(new Object());
      buffer.append(this._attachmentCount);
      buffer.append(this._attachmentCount > 1 ? EmailResources.getString(141) : EmailResources.getString(140));
      Field attachmentInfo = (Field)(new Object(buffer, 18014398509481984L));
      attachmentInfo.setTag(ThemeUtilities.EMAIL_ATTACHMENT_STATUS_TAG);
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object());
      ImageField iconField = (ImageField)(new Object(65536));
      iconField.setImage(FileIcon.getFileIconImage(0));
      hfm.add(iconField);
      hfm.add(attachmentInfo);
      return hfm;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final int getOrder(Object context) {
      return 2750;
   }
}

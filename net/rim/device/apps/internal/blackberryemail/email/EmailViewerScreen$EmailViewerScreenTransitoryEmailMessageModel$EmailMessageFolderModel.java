package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;

final class EmailViewerScreen$EmailViewerScreenTransitoryEmailMessageModel$EmailMessageFolderModel extends TransitoryTextModel {
   @Override
   protected final String getString(RIMModel model) {
      EmailMessageModel message = (EmailMessageModel)model;
      Folder folder = FolderHierarchies.getFolder(message.getFolderId());
      return folder != null ? folder.getFriendlyName() : null;
   }
}

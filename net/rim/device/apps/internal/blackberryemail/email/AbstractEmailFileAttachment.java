package net.rim.device.apps.internal.blackberryemail.email;

import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.DeleteConfirmationProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.icons.FileIcon;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.vm.Array;

public class AbstractEmailFileAttachment
   implements PersistableRIMModel,
   ConversionProvider,
   FieldProvider,
   Persistable,
   CloneProvider,
   DeleteConfirmationProvider,
   VerbProvider {
   private String _file;
   private long _fileSize;
   private short _contentPartId;
   private String _displayName;
   private String _contentType;

   public short getContentPartId() {
      return this._contentPartId;
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 43) && verbs != null) {
         switch (MIMETypeAssociations.getMediaTypeFromMIMEType(this.getContentType())) {
            case 0:
            case 5:
            case 6:
               break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 7:
            default:
               Array.resize(verbs, verbs.length + 1);
               verbs[verbs.length - 1] = new OpenNativeAttachmentVerb(this);
               return verbs[verbs.length - 1];
         }
      }

      return null;
   }

   public String getDisplayName() {
      return this._displayName;
   }

   public void setDisplayName(String displayName) {
      this._displayName = this.convertFullPathToFileName(displayName);
   }

   public String getFile() {
      return this._file;
   }

   @Override
   public boolean convert(Object _1, Object _2) {
      throw null;
   }

   @Override
   public Object clone(Object _1) {
      throw null;
   }

   public void setContentType(String type) {
      this._contentType = type == null ? NativeAttachmentRequestProcessor$Helper.getUnknownMimeType() : type;
   }

   public long getFileSize() {
      return this._fileSize;
   }

   public void setFileSize(long size) {
      this._fileSize = size;
   }

   public InputConnection getInputConnection() {
      try {
         return (FileConnection)Connector.open(this.getFile());
      } finally {
         ;
      }
   }

   public String getContentType() {
      return this._contentType;
   }

   public void setFile(String file) {
      this._file = file;
   }

   protected String convertFullPathToFileName(String path) {
      return path == null ? null : FileUtilities.getDisplayName(path);
   }

   public void setContentPartId(short contentId) {
      this._contentPartId = contentId;
   }

   @Override
   public boolean confirmDelete(Object context) {
      return true;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public int getOrder(Object context) {
      return 6500;
   }

   @Override
   public Field getField(Object context) {
      HorizontalFieldManager hfm = new HorizontalFieldManager();
      ImageField iconField = new ImageField(65536);
      iconField.setImage(FileIcon.getFileIconImage(this.getContentType()));
      hfm.add(iconField);
      AutoTextEditField field = new AutoTextEditField(null, this.getDisplayName());
      field.setEditable(false);
      hfm.add(field);
      hfm.setEditable(false);
      return hfm;
   }
}

package net.rim.device.apps.internal.qm.peer;

import java.io.ByteArrayInputStream;
import net.rim.device.api.browser.util.StaticHttpConnection;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.file.ExplorerServices;
import net.rim.device.apps.internal.qm.peer.common.QmPopupStatus;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class FileMessageField extends MessageField implements FieldChangeListener {
   FileMessage _message;
   private ButtonField _saveButton;
   private ButtonField _cancelButton;
   private ButtonField _viewButton;
   private ButtonField _playButton;
   private VerticalFieldManager _vfm;
   private HorizontalFieldManager _hfm;
   private FlowFieldManager _buttonManager;
   private boolean _isImage;
   private boolean _isPlayable;
   VerticalFieldManager _manager = new VerticalFieldManager();

   FileMessageField(FileMessage message) {
      this._message = message;
      Bitmap bitmap = null;
      if (this._message.getContentType().startsWith("image")) {
         this._isImage = true;

         label72:
         try {
            bitmap = Bitmap.createBitmapFromBytes(this._message.getData(), 0, this._message.getData().length, 7);
         } finally {
            break label72;
         }
      } else {
         bitmap = Bitmap.getBitmapResource("file.PNG");
      }

      if (this._message.getContentType().startsWith("audio")) {
         this._isPlayable = true;
      }

      this._vfm = new VerticalFieldManager();
      this._vfm.add(new RichTextField(this._message.getText(), 36028797018963968L));
      int state = message.getState();
      Field rtf = this.getTextField(state);
      if (rtf != null) {
         this._vfm.add(rtf);
      }

      this._buttonManager = new FlowFieldManager();
      if (this._isImage) {
         this._viewButton = new ButtonField(PeerResources.getString(2053), 12884901888L);
         this._viewButton.setChangeListener(this);
         this._buttonManager.add(this._viewButton);
      }

      if (this._isPlayable) {
         this._playButton = new ButtonField(QmResources.getString(59), 12884901888L);
         this._playButton.setChangeListener(this);
         this._buttonManager.add(this._playButton);
      }

      if (state == 0) {
         if (!this._isImage || bitmap != null) {
            this._saveButton = new ButtonField(PeerResources.getString(5), 12884901888L);
            this._saveButton.setChangeListener(this);
            this._buttonManager.add(this._saveButton);
         }

         this._cancelButton = new ButtonField(PeerResources.getString(6), 12884901888L);
         this._cancelButton.setChangeListener(this);
         this._buttonManager.add(this._cancelButton);
      }

      this._vfm.add(this._buttonManager);
      this._hfm = new HorizontalFieldManager();
      if (bitmap != null) {
         BitmapField thumbnail = new BitmapField(bitmap, 51539607552L);
         thumbnail.setSpace(5, 5);
         this._hfm.add(thumbnail);
      }

      this._hfm.add(this._vfm);
      this._manager.add(new SeparatorField());
      this._manager.add(this._hfm);
      this._manager.add(new SeparatorField());
      this.add(this._manager);
   }

   @Override
   public final void update() {
   }

   @Override
   public final MessengerMessage getMessage() {
      return this._message;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field != this._saveButton) {
         if (field == this._cancelButton) {
            this._message.setState(2);
            this._buttonManager.deleteAll();
            this._vfm.delete(this._buttonManager);
            Field addedField = this.getTextField(2);
            if (addedField != null) {
               this._vfm.add(addedField);
            }

            if (this._isImage) {
               this._buttonManager.add(this._viewButton);
            }

            if (this._isPlayable) {
               this._buttonManager.add(this._playButton);
            }

            this._vfm.add(this._buttonManager);
            return;
         }

         if (field == this._viewButton || field == this._playButton) {
            HttpHeaders headers = new HttpHeaders();
            headers.addProperty("content-type", this._message.getContentType());
            headers.addProperty("content-length", "" + this._message.getData().length);
            StaticHttpConnection connection = new StaticHttpConnection(this._message.getFilename(), this._message.getData(), headers);
            PeerApplication.getInstance().displayRenderScreen(connection);
         }
      } else {
         String savedFilename = ExplorerServices.saveInputStream(
            this._message.getFilename(),
            new ByteArrayInputStream(this._message.getData()),
            MIMETypeAssociations.getMediaTypeFromMIMEType(this._message.getContentType()),
            true,
            false
         );
         if (savedFilename != null) {
            this._message.setState(1);
            QmPopupStatus.show(PeerResources.getString(2029), 800);
            this._buttonManager.deleteAll();
            this._vfm.delete(this._buttonManager);
            Field addedField = this.getTextField(1);
            if (addedField != null) {
               this._vfm.add(addedField);
            }

            if (this._isImage) {
               this._buttonManager.add(this._viewButton);
            }

            if (this._isPlayable) {
               this._buttonManager.add(this._playButton);
            }

            this._vfm.add(this._buttonManager);
            return;
         }
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      Field field = this.getLeafFieldWithFocus();
      if (field != this._saveButton && field != this._viewButton && field != this._cancelButton && field != this._playButton) {
         return super.trackwheelClick(status, time);
      }

      this.fieldChanged(field, 0);
      return true;
   }

   private final Field getTextField(int state) {
      RichTextField rtf = null;
      switch (state) {
         case 1:
         default:
            rtf = new RichTextField(PeerResources.getString(890), 36028797018963968L);
            rtf.setAttributes(new int[]{32768, -805044223, 1, -805037994}, new int[]{16777215, 207814912, 1851099757, 1956816537});
            return rtf;
         case 2:
            rtf = new RichTextField(PeerResources.getString(891), 36028797018963968L);
            rtf.setAttributes(new int[]{16711680, -805044213, 775162112, 774909491}, new int[]{16777215, 207814912, 1851099757, 1956816537});
            return rtf;
         case 3:
            rtf = new RichTextField(PeerResources.getString(892), 36028797018963968L);
            rtf.setAttributes(new int[]{16711680, -805044213, 775162112, 774909491}, new int[]{16777215, 207814912, 1851099757, 1956816537});
         case 0:
            return rtf;
      }
   }
}

package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;

public class StatusField extends CursorProviderVerticalIndentFieldManager implements ContextMenuDelegate, StatusProviderField {
   private int _status;
   private CursorProviderHorizontalFieldManager _imageAndTextManager;
   private ContextMenuProxyImageField _imageField;
   private RichTextField _textField;
   private boolean _showShortForm;
   private Application _displayApp;
   private SecureEmailMessageManager _secureEmailMessageManager;
   public static final int HORIZONTAL_SPACER_PIXEL_WIDTH;
   public static final int LARGE_IMAGE_PIXEL_WIDTH;
   public static final int LARGE_IMAGE_PIXEL_HEIGHT;
   public static final int SMALL_IMAGE_PIXEL_WIDTH;
   public static final int SMALL_IMAGE_PIXEL_HEIGHT;

   protected Image getImage() {
      throw null;
   }

   protected String getText() {
      throw null;
   }

   public int getPriority() {
      throw null;
   }

   public long getStatusType() {
      throw null;
   }

   public String getShortText() {
      throw null;
   }

   public ImageField getImageField() {
      return this._imageField;
   }

   public StatusField(Application app) {
      this(app, 0);
   }

   public StatusField(Application app, int initialStatus) {
      this(app, initialStatus, false);
   }

   public StatusField(Application app, int status, boolean showShortForm) {
      this._displayApp = app;
      this._status = status;
      this._showShortForm = showShortForm;
      this._imageAndTextManager = new CursorProviderHorizontalFieldManager();
      this._imageField = new ContextMenuProxyImageField(this, null, 18014450049155072L);
      this._textField = (RichTextField)(new Object(36028848558571520L));
      this.updateFields();
   }

   public void updateStatus() {
      synchronized (this._displayApp.getAppEventLock()) {
         this._imageField.setImage(this.getImage());
         this._textField.setText(this.getText());
         if (this._secureEmailMessageManager != null) {
            this._secureEmailMessageManager.handleStatusUpdate();
         }
      }
   }

   private synchronized void updateFields() {
      synchronized (this._displayApp.getAppEventLock()) {
         if (this._showShortForm) {
            this._imageAndTextManager.deleteAll();
            this.deleteAll();
            this._imageField.setPreferredSize(20, 15);
         } else {
            this._imageField.setPreferredSize(26, 20);
            this._imageAndTextManager.add(this._imageField);
            int twoLinesOfTextHeight = Font.getDefault().getHeight() * 2;
            this._imageAndTextManager.add(new StatusField$BlankField(3, twoLinesOfTextHeight));
            this._imageAndTextManager.add(this._textField);
            this.add(this._imageAndTextManager);
            this.add((Field)(new Object()));
         }
      }
   }

   public void setStatus(int status) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public int getStatus() {
      return this._status;
   }

   @Override
   public synchronized void showShortForm(boolean showShortForm) {
      if (this._showShortForm != showShortForm) {
         this._showShortForm = showShortForm;
         this.updateFields();
      }
   }

   @Override
   public StatusField[] getStatusFields() {
      return new StatusField[]{this};
   }

   @Override
   public void setSecureEmailMessageManager(SecureEmailMessageManager secureEmailMessageManager) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public void makeDelegateContextMenu(ContextMenu contextMenu) {
      if (this._secureEmailMessageManager != null) {
         this._secureEmailMessageManager.makeDelegateContextMenu(contextMenu);
      }
   }
}

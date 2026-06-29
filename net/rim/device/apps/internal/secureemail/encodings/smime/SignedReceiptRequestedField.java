package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.cms.CMSSignedDataInputStream;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.secureemail.ContextMenuDelegate;
import net.rim.device.apps.internal.secureemail.MessageClosedListener;
import net.rim.device.apps.internal.secureemail.SecureEmailListener;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;

public final class SignedReceiptRequestedField extends HorizontalFieldManager implements ContextMenuDelegate, MessageClosedListener {
   private CMSSignedDataInputStream _signedStream;
   private boolean _signedReceiptSent;
   private VerbMenuItem _sendSignedReceiptVerb;
   private EmailMessageModel _messageModel;
   private RichTextField _richTextField;
   private boolean _inbound;

   protected final String getText() {
      return this._signedReceiptSent ? SMIMEResources.getString(2068) : SMIMEResources.getString(2069);
   }

   protected final Image getImage() {
      return CryptoIcons.getImage(14);
   }

   @Override
   public final void makeDelegateContextMenu(ContextMenu contextMenu) {
      if (!this._signedReceiptSent && this._inbound) {
         if (this._sendSignedReceiptVerb == null) {
            this._sendSignedReceiptVerb = (VerbMenuItem)(new Object(
               new SignedReceiptRequestedField$SendSignedReceiptVerb(this, this._signedStream, this._messageModel), 10
            ));
         }

         contextMenu.addItem(this._sendSignedReceiptVerb);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void messageClosed(EmailMessageModel messageModel) {
      UiApplication app = UiApplication.getUiApplication();
      boolean paitingSuspended = app.isPaintingSuspended();
      if (paitingSuspended) {
         app.suspendPainting(false);
      }

      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         SignedReceiptHelper.processSignedReceiptRequest(this._signedStream, messageModel);
         var6 = false;
      } finally {
         if (var6) {
            if (paitingSuspended) {
               app.suspendPainting(true);
            }
         }
      }

      if (paitingSuspended) {
         app.suspendPainting(true);
      }
   }

   public SignedReceiptRequestedField(CMSSignedDataInputStream signedStream, boolean inbound, Object context) {
      this._signedStream = signedStream;
      this._inbound = inbound;
      this._messageModel = (EmailMessageModel)ContextObject.get(context, 246);
      if (this._messageModel != null) {
         SecureEmailListener.getInstance().registerMessageClosedListener(this._messageModel, this);
         this._signedReceiptSent = this._messageModel.flagsSet(16384);
      }

      ImageField imageField = (ImageField)(new Object(this, this.getImage(), 18014450049155072L));
      imageField.setPreferredSize(26, 20);
      this.add(imageField);
      this.add((Field)(new Object(3)));
      this._richTextField = (RichTextField)(new Object(this.getText(), 36028848558571520L));
      this.add(this._richTextField);
   }
}

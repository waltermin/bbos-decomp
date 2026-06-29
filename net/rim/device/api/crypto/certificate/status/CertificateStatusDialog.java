package net.rim.device.api.crypto.certificate.status;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.CertificateStatusManager;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.HorizontalSpacerField;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.component.VerticalSpacerField;

public class CertificateStatusDialog extends PopupDialog implements FieldChangeListener {
   private CertificateStatusQuery _query;
   private Certificate _endEntityCertificate;
   private KeyStore _keyStore;
   private boolean _allowDetails;
   private VerticalFieldManager _vfm;
   private ButtonField _okButton;
   private ButtonField _detailsButton;
   private Font[] _fontFormats;
   private static final char BOLD_FLAG = '*';
   private static final byte FONT_REGULAR = 0;
   private static final byte FONT_BOLD = 1;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(-7644390350925054654L, "net.rim.device.internal.resource.crypto.StatusProviders");

   public CertificateStatusDialog(CertificateStatusQuery query, KeyStore keyStore, boolean allowDetails) {
      this(query, keyStore, allowDetails, 0);
   }

   public CertificateStatusDialog(CertificateStatusQuery query, KeyStore keyStore, boolean allowDetails, long style) {
      super(new VerticalFieldManager(), style);
      if (query == null) {
         throw new IllegalArgumentException();
      }

      Font font = Font.getDefault();
      this._fontFormats = new Font[]{font, font.derive(font.getStyle() | 1)};
      this._query = query;
      this._endEntityCertificate = this._query.getCertChain()[0];
      this._keyStore = keyStore;
      this._allowDetails = allowDetails;
      this.addFields();
   }

   @Override
   public void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this._okButton.setFocus();
      }
   }

   private void addFields() {
      VerticalFieldManager delegate = (VerticalFieldManager)this.getDelegate();
      Certificate[] certificateChain = this._query.getCertChain();
      boolean checkEntireChain = this._query.checkEntireChain();
      boolean returnedStatusObsolete = this._query.returnedStatusObsolete();
      LabelField titleField = new LabelField(this._endEntityCertificate.getSubjectFriendlyName(), 36028797018964032L);
      titleField.setFont(this._fontFormats[1]);
      delegate.add(titleField);
      delegate.add(new SeparatorField());
      CertificateStatusManager manager = CertificateStatusManager.getInstance();
      CertificateStatus overallStatus = manager.getStatus(this._endEntityCertificate);
      if (checkEntireChain) {
         int certificateChainLength = certificateChain.length;

         for (int i = 1; i < certificateChainLength; i++) {
            CertificateStatus nextStatus = manager.getStatus(certificateChain[i]);
            if (CertificateStatusUtilities.compareStatusCertificateChain(nextStatus, overallStatus) > 0) {
               overallStatus = nextStatus;
            }
         }
      }

      this._vfm = new VerticalFieldManager(299067162755072L);
      String prefix = checkEntireChain ? _rb.getString(19) : _rb.getString(18);
      if (returnedStatusObsolete) {
         RichTextField obsoleteTextField = new RichTextField(_rb.getString(24), 51539607552L);
         this._vfm.add(obsoleteTextField);
         this._vfm.add(new SeparatorField());
      }

      int overallStatusInt = overallStatus != null ? overallStatus.getStatus() : -1;
      HorizontalFieldManager imageStatusManager = new HorizontalFieldManager();
      Image returnedStatusImage;
      switch (overallStatusInt) {
         case -1:
            returnedStatusImage = CryptoIcons.getImage(5);
            break;
         case 0:
         default:
            returnedStatusImage = checkEntireChain ? CryptoIcons.getImage(6) : CryptoIcons.getImage(7);
            break;
         case 1:
            returnedStatusImage = CryptoIcons.getImage(4);
      }

      ImageField imageField = CryptoIcons.getLargeImageField(returnedStatusImage, 51539607552L);
      imageStatusManager.add(imageField);
      imageStatusManager.add(new HorizontalSpacerField(4));
      RichTextField statusTextField = new RichTextField(51539607552L);
      String statusText;
      switch (overallStatusInt) {
         case -1:
            statusText = _rb.getString(21);
            break;
         case 0:
         default:
            statusText = _rb.getString(20);
            break;
         case 1:
            statusText = _rb.getString(22);
      }

      long producedAt = overallStatus != null ? overallStatus.getProducedAtTime() : -1;
      if (producedAt > 0) {
         String date = DateFormat.getInstance(40).formatLocal(producedAt);
         String time = DateFormat.getInstance(5).formatLocal(producedAt);
         this.setRTFText(statusTextField, MessageFormat.format(_rb.getString(23), new String[]{prefix, statusText, date, time}));
      } else {
         this.setRTFText(statusTextField, MessageFormat.format(_rb.getString(66), new String[]{prefix, statusText}));
      }

      imageStatusManager.add(statusTextField);
      this._vfm.add(imageStatusManager);
      HorizontalFieldManager buttonManager = new HorizontalFieldManager(8589934592L);
      this._okButton = new ButtonField(CommonResource.getString(100));
      this._okButton.setChangeListener(this);
      buttonManager.add(this._okButton);
      if (this._allowDetails) {
         this._detailsButton = new ButtonField(_rb.getString(16));
         this._detailsButton.setChangeListener(this);
         buttonManager.add(this._detailsButton);
      }

      this._vfm.add(new VerticalSpacerField(4));
      this._vfm.add(buttonManager);
      delegate.add(this._vfm);
   }

   private void setRTFText(RichTextField field, String text) {
      int length = text.length();
      char[] chars = text.toCharArray();
      int sections = 0;
      int sectionLength = 0;

      for (int i = 0; i < length; i++) {
         if (chars[i] == '*') {
            if (sectionLength > 0) {
               sections++;
            }

            sectionLength = 0;
         } else {
            sectionLength++;
         }
      }

      if (sectionLength > 0) {
         sections++;
      }

      int readOffset = 0;
      int writeOffset = 0;
      int formatSection = 0;
      int[] formatOffsets = new int[sections + 1];
      byte[] formatIndices = new byte[sections];
      boolean isBold = false;
      formatOffsets[0] = 0;
      sectionLength = 0;

      while (readOffset < length) {
         char data = chars[readOffset++];
         if (data == '*') {
            if (sectionLength > 0) {
               formatIndices[formatSection] = (byte)(isBold ? 1 : 0);
               formatOffsets[++formatSection] = writeOffset;
            }

            sectionLength = 0;
            isBold = !isBold;
         } else {
            chars[writeOffset++] = data;
            sectionLength++;
         }
      }

      if (sectionLength > 0) {
         formatIndices[formatSection] = (byte)(isBold ? 1 : 0);
         formatOffsets[formatSection + 1] = writeOffset;
      }

      field.setText(new String(chars, 0, writeOffset), formatOffsets, formatIndices, this._fontFormats);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         return true;
      } else if (key == 27) {
         this.close(1);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._okButton) {
         this.close(1);
      } else {
         if (field == this._detailsButton) {
            CertificateUtilities.displayCertificateDetails(this._endEntityCertificate, this._keyStore, false, null);
         }
      }
   }
}

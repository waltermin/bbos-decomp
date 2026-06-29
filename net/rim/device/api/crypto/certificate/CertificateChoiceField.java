package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.internal.resource.crypto.CryptoIndicatorImages;
import net.rim.vm.WeakReference;

public class CertificateChoiceField extends ChoiceField {
   private Certificate[] _certificates;
   private String[] _labels;
   private int[] _iconIndices;
   private Application _app;
   private KeyStore _keyStore;
   private KeyStore _trustedKeyStore;
   private CryptoSystemProperties _cryptoSystemProperties;
   private int _epoch;
   private WeakReference _certificateChangeOptionChoiceFieldWR;

   public CertificateChoiceField(String label, KeyStore keyStore, KeyStore trustedKeyStore) {
      this(label, keyStore, trustedKeyStore, null);
   }

   public CertificateChoiceField(String label, KeyStore keyStore, KeyStore trustedKeyStore, CryptoSystemProperties cryptoSystemProperties) {
      super(label, 0, 0);
      this._keyStore = keyStore;
      this._trustedKeyStore = trustedKeyStore;
      this._cryptoSystemProperties = cryptoSystemProperties;
      this._certificateChangeOptionChoiceFieldWR = new WeakReference(null);
      this._app = Application.getApplication();
   }

   public synchronized Certificate getSelectedCertificate() {
      int selectedIndex = this.getSelectedIndex();
      return selectedIndex < 0 ? null : this._certificates[selectedIndex];
   }

   @Override
   public Object getChoice(int index) {
      if (this._certificates.length == 0 && index == 0) {
         return null;
      } else if (index >= 0 && index < this._certificates.length) {
         return this._labels[index];
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   protected int getWidthOfChoice(int index) {
      int width = super.getWidthOfChoice(index);
      if (this._certificates[index] != null) {
         width += CryptoIndicatorImages.getImageWidth() + 2;
      }

      return width;
   }

   @Override
   protected int getHeightOfChoices() {
      return Math.max(CryptoIndicatorImages.getImageHeight(), this.getFont().getHeight());
   }

   @Override
   protected void drawChoice(int index, Graphics graphics, int x, int y, int flags, int width) {
      if (this._certificates[index] != null) {
         int imageWidth = CryptoIndicatorImages.getImageWidth();
         int imageX = x;
         if ((flags & 7) == 6) {
            x += imageWidth + 2;
         } else {
            imageX += width - imageWidth;
         }

         width -= imageWidth + 2;
         CryptoIndicatorImages.drawIcon(graphics, imageX, y, this._iconIndices[index]);
      }

      super.drawChoice(index, graphics, x, y, flags, width);
   }

   @Override
   protected ChoiceField getChangeOptionChoiceField(String label) {
      ChoiceField field = new CertificateChoiceField$CertificateChangeOptionChoiceField(
         this, this.getLabel(), this.getSize(), this.getSelectedIndex(), this.getStyle()
      );
      field.setChangeListener(this.getChangeListener());
      this._certificateChangeOptionChoiceFieldWR.set(field);
      return field;
   }

   @Override
   protected void invalidate() {
      super.invalidate();
      if (this._certificateChangeOptionChoiceFieldWR != null) {
         CertificateChoiceField$CertificateChangeOptionChoiceField certificateChangeOptionChoiceField = (CertificateChoiceField$CertificateChangeOptionChoiceField)this._certificateChangeOptionChoiceFieldWR
            .get();
         if (certificateChangeOptionChoiceField != null) {
            certificateChangeOptionChoiceField.underlyingFieldInvalidated();
         }
      }
   }

   @Override
   protected void setSize(int size) {
      super.setSize(size);
      if (this._certificateChangeOptionChoiceFieldWR != null) {
         CertificateChoiceField$CertificateChangeOptionChoiceField certificateChangeOptionChoiceField = (CertificateChoiceField$CertificateChangeOptionChoiceField)this._certificateChangeOptionChoiceFieldWR
            .get();
         if (certificateChangeOptionChoiceField != null) {
            certificateChangeOptionChoiceField.setSize(size);
         }
      }
   }

   public synchronized void setCertificates(Certificate[] certificates, String[] labels, int selectedIndex) {
      this._epoch++;
      this._certificates = certificates;
      this._labels = labels;
      int length = certificates != null ? certificates.length : 0;
      this._iconIndices = new int[length];

      for (int i = 0; i < length; i++) {
         this._iconIndices[i] = 0;
      }

      synchronized (this._app.getAppEventLock()) {
         this.setSize(length);
      }

      this.setSelectedIndex(selectedIndex);
      new CertificateChoiceField$IconUpdateThread(this, this._certificates, this._iconIndices, selectedIndex, this._epoch).start();
   }
}

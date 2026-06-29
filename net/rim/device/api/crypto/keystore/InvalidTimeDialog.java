package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;
import net.rim.device.internal.ui.security.component.VendorModuleStackDialog;

final class InvalidTimeDialog extends PopupDialog implements FieldChangeListener {
   private Certificate _certificate;
   private Font _boldFont;
   private ButtonField _accept;
   private ButtonField _reject;
   private ButtonField _details;

   public final void showDetails() {
      CertificateUtilities.displayCertificateDetails(this._certificate, null, false, null);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._accept) {
         this.close(0);
      } else if (field == this._reject) {
         this.close(-1);
      } else {
         if (field == this._details) {
            this.showDetails();
         }
      }
   }

   public InvalidTimeDialog(Certificate certificate, CertificateStatus status, long style) {
      super(new VerticalIndentFieldManager(1153220571769602048L), style);
      VerticalIndentFieldManager vifm = (VerticalIndentFieldManager)this.getDelegate();
      this._certificate = certificate;
      this._boldFont = Font.getDefault();
      this._boldFont = this._boldFont.derive(this._boldFont.getStyle() | 1);
      RichTextField label = new RichTextField(KeyStoreResources.getString(9), 45035996273704960L);
      vifm.add(label);
      LabelField yourTimeLabel = new LabelField(KeyStoreResources.getString(12));
      yourTimeLabel.setFont(this._boldFont);
      vifm.add(yourTimeLabel);
      SimpleDateFormat format = new SimpleDateFormat(54);
      RichTextField yourTime = new RichTextField(format.formatLocal(System.currentTimeMillis()), 45035996273704960L);
      vifm.add(yourTime);
      LabelField theirTimeLabel = new LabelField(KeyStoreResources.getString(13));
      theirTimeLabel.setFont(this._boldFont);
      vifm.add(theirTimeLabel);
      RichTextField theirTime = new RichTextField(format.formatLocal(status.getProducedAtTime()), 45035996273704960L);
      vifm.add(theirTime);
      HorizontalFieldManager buttonManager = new HorizontalFieldManager(12884901888L);
      this._accept = new ButtonField(KeyStoreResources.getString(10));
      this._accept.setChangeListener(this);
      buttonManager.add(this._accept);
      this._reject = new ButtonField(KeyStoreResources.getString(11));
      this._reject.setChangeListener(this);
      buttonManager.add(this._reject);
      vifm.add(buttonManager);
      HorizontalFieldManager detailManager = new HorizontalFieldManager(12884901888L);
      this._details = new ButtonField(KeyStoreResources.getString(14));
      this._details.setChangeListener(this);
      detailManager.add(this._details);
      vifm.add(detailManager);
      VendorModuleStackDialog.populateVendorApplicationModulesStack(vifm);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.close(-1);
         return true;
      } else if (key == '\n') {
         this.fieldChanged(this.getLeafFieldWithFocus(), 0);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }
}

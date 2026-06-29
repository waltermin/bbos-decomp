package net.rim.device.api.crypto.tls.wtls20;

import net.rim.device.api.crypto.tls.SessionResumption;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

public final class WTLSOptionsItem extends SaveableMainScreenOptionsListItem {
   private BooleanChoiceField _encryptionStrengthField;
   private BooleanChoiceField _promptForServerTrustField;
   private WTLSOptionStore _optionStore;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(3628098008949486223L, "net.rim.device.internal.resource.crypto.WTLS");

   public WTLSOptionsItem() {
      super(_rb.getFamily(), 8, 5294015899860238835L);
      ContextObject.put(super._context, 244, new Integer(32785));
      this._optionStore = WTLSOptionStore.getOptions();
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this._encryptionStrengthField = new BooleanChoiceField(_rb.getString(10), _rb.getStringArray(16), this._optionStore.allowExportCipherSuites(), 268435456);
      this._encryptionStrengthField.setEditable(ITPolicy.getInteger(29, 1, 2) == 2);
      mainScreen.add(this._encryptionStrengthField);
      this._promptForServerTrustField = new BooleanChoiceField(_rb.getString(11), 0, this._optionStore.getDisplayServerCertificateWarnings());
      mainScreen.add(this._promptForServerTrustField);
   }

   @Override
   protected final boolean save() {
      this._optionStore.setAllowExportCipherSuites(this._encryptionStrengthField.isAffirmative());
      this._optionStore.setDisplayServerCertificateWarnings(this._promptForServerTrustField.isAffirmative());
      return super.save();
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1465077829:
            return super.openDevelopmentBackdoor(backdoorCode);
         case 1465077830:
         default:
            if (SimpleChoiceDialog.askYesNoQuestion(_rb.getString(23))) {
               SessionResumption resumption = new SessionResumption();
               resumption.removeAllSessions();
               resumption.purgeNvStore();
               return true;
            } else {
               return true;
            }
      }
   }
}

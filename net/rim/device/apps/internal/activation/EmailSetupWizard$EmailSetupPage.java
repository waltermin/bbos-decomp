package net.rim.device.apps.internal.activation;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.setupwizard.ListWizardPage;
import net.rim.device.apps.api.setupwizard.SavableWizardPage;
import net.rim.device.apps.internal.bis.invoke.BISClientInvoke;

public final class EmailSetupWizard$EmailSetupPage extends ListWizardPage implements SavableWizardPage {
   private Object _bisCookie;
   private Object _besCookie;
   private Object[] _cookies;
   private final EmailSetupWizard this$0;

   public EmailSetupWizard$EmailSetupPage(EmailSetupWizard _1) {
      super(EmailSetupWizard._resources, 160, 10, null, 655360);
      this.this$0 = _1;
      this._bisCookie = new Object();
      this._besCookie = new Object();
   }

   @Override
   protected final void populateFields() {
      this._cookies = null;
      this.this$0._bisSetupUID = EmailSetupWizard.findEmailSetupBrowserConfigUID();
      boolean bisAvailable = EmailSetupWizard.isBisAvailable() && this.this$0._bisSetupUID != null || BISClientInvoke.canBeInvoked();
      boolean besAvailable = EmailSetupWizard.isBesAvailable();
      if (!bisAvailable && !besAvailable) {
         this.setHeaderField(new LabelField(EmailSetupWizard._resources.getString(163)));
      } else if (RadioInfo.getState() != 1) {
         this.setHeaderField(new LabelField(EmailSetupWizard._resources.getString(161)));
      } else {
         LabelField header = new LabelField(EmailSetupWizard._resources.getString(164));
         header.setFont(this.getHeaderFont());
         this.setHeaderField(header);
         int numItems = 2;
         if (bisAvailable && besAvailable) {
            numItems++;
         }

         String[] items = new String[numItems];
         this._cookies = new Object[numItems];
         int i = 0;
         if (bisAvailable) {
            items[i] = EmailSetupWizard._resources.getString(165);
            this._cookies[i] = this._bisCookie;
            if (this.this$0._setupMode == 1) {
               this.setSelectedIndex(i);
            }

            i++;
         }

         if (besAvailable) {
            items[i] = EmailSetupWizard._resources.getString(166);
            this._cookies[i] = this._besCookie;
            if (this.this$0._setupMode == 2) {
               this.setSelectedIndex(i);
            }

            i++;
         }

         items[i] = EmailSetupWizard._resources.getString(167);
         if (this.this$0._setupMode == 0) {
            this.setSelectedIndex(i);
         }

         this.setVerticalSpacing(10);
         this.setListItems(items);
      }
   }

   @Override
   protected final boolean saveWizard(Verb sender) {
      Object cookie = null;
      int index = this.getSelectedIndex();
      if (this._cookies != null && index < this._cookies.length) {
         cookie = this._cookies[index];
      }

      if (cookie == this._bisCookie) {
         this.this$0._setupMode = 1;
         return true;
      } else if (cookie == this._besCookie) {
         this.this$0._setupMode = 2;
         return true;
      } else {
         this.this$0._setupMode = 0;
         return true;
      }
   }

   @Override
   public final int[] getPageOptions() {
      return new int[]{this.this$0._setupMode};
   }

   @Override
   public final void setPageOptions(int[] pageOptions) {
      this.this$0._setupMode = Arrays.copy(pageOptions)[0];
   }

   @Override
   public final byte getPageKey() {
      return 2;
   }
}

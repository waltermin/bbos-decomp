package net.rim.device.apps.internal.ldap;

import net.rim.device.api.crypto.certificate.status.CertificateStatusProvider;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.keystore.browser.LaunchKeyStoreBrowserVerb;

class LDAPBrowser$LDAPBrowserScreen extends AppsMainScreen {
   private final LDAPBrowser this$0;

   LDAPBrowser$LDAPBrowserScreen(LDAPBrowser _1) {
      super(0);
      this.this$0 = _1;
      if (_1._contextString.equals("PGP")) {
         this.setHelp("net_rim_bb_secureemail_help/pgp_certificates");
      } else if (_1._contextString.equals("X509")) {
         this.setHelp("net_rim_bb_secureemail_help/smime_certificates");
      }

      this.setDefaultClose(false);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (super.keyChar(key, status, time)) {
         return true;
      }

      Field focus = this.this$0._UiEngine.getActiveScreen().getLeafFieldWithFocus();
      boolean retval = false;
      switch (key) {
         case '\n':
            if (focus != null) {
               if (focus == this.this$0._list) {
                  this.this$0.doCertView();
                  retval = true;
               } else if (focus == this.this$0._editEmail) {
                  if (this.this$0.isValidSearchCriteria()) {
                     this.this$0.doSearch();
                  } else {
                     this.this$0._editEmail.setFocus();
                  }

                  retval = true;
               }
            }
            break;
         case '\u001b':
            retval = true;
            this.this$0.doClose();
            break;
         case ' ':
            if (focus != null && focus == this.this$0._list) {
               if (this.this$0._list.getSize() > 0) {
                  LDAPBrowserContainer container = (LDAPBrowserContainer)this.this$0._containers.elementAt(this.this$0._list.getSelectedIndex());
                  StringBuffer buffer = (StringBuffer)(new Object());
                  buffer.append(LDAPBrowser.getString(36));
                  buffer.append(' ');
                  buffer.append(container.getLabel());
                  Dialog.inform(buffer.toString());
               }

               retval = true;
            }
      }

      return retval;
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this.this$0.isValidSearchCriteria()) {
         if (this.this$0._searchVerb == null) {
            this.this$0._searchVerb = new LDAPBrowser$LDAPBrowserVerb(this.this$0, 1, 1146960);
         }

         menu.add(this.this$0._searchVerb);
         menu.setDefault(this.this$0._searchVerb);
      }

      if (this.this$0._context.isFetchRootApplicable()) {
         if (this.this$0._fetchRootVerb == null) {
            this.this$0._fetchRootVerb = new LDAPBrowser$LDAPBrowserVerb(this.this$0, 4, 1146976);
         }

         menu.add(this.this$0._fetchRootVerb);
      }

      if (this.this$0._launchKeyStoreBrowserVerb == null) {
         this.this$0._launchKeyStoreBrowserVerb = (LaunchKeyStoreBrowserVerb)(new Object(this.this$0._context.getKeyStoreBrowserContextString(), null));
      }

      menu.add(this.this$0._launchKeyStoreBrowserVerb);
      Field field = this.getLeafFieldWithFocus();
      if (field != this.this$0._list) {
         if (field == this.this$0._serverChoice) {
            this.this$0._newServerVerb = new LDAPBrowser$LDAPBrowserVerb(this.this$0, 5, 589904);
            menu.add(this.this$0._newServerVerb);
            if (this.this$0._serverChoice.getSelectedIndex() > 0) {
               if (this.this$0._viewServerVerb == null) {
                  this.this$0._viewServerVerb = new LDAPBrowser$LDAPBrowserVerb(this.this$0, 6, 590080);
               }

               menu.add(this.this$0._viewServerVerb);
            }

            if (this.this$0._serverChoice.getSize() == 1) {
               menu.setDefault(this.this$0._newServerVerb);
            } else if (this.this$0._serverChoice.isDirty()) {
               if (this.this$0._serverChoice.getSelectedIndex() == 0) {
                  menu.setDefault(this.this$0._newServerVerb);
               } else {
                  menu.setDefault(this.this$0._viewServerVerb);
               }
            }
         }
      } else {
         int[] indices = this.this$0._list.getSelection();
         int selectedIndex = this.this$0._list.getSelectedIndex();
         if (indices.length == 1 && selectedIndex != -1) {
            LDAPBrowserContainer container = (LDAPBrowserContainer)this.this$0._containers.elementAt(selectedIndex);
            if (this.this$0._certViewVerb == null) {
               this.this$0._certViewVerb = new LDAPBrowser$LDAPBrowserVerb(this.this$0, 2, 1200208);
            }

            menu.add(this.this$0._certViewVerb);
            menu.setDefault(this.this$0._certViewVerb);
            if (CertificateStatusProvider.queryStatusAvailability()) {
               if (this.this$0._certStatusVerb == null) {
                  this.this$0._certStatusVerb = new LDAPBrowser$LDAPBrowserVerb(this.this$0, 7, 1200240);
               }

               menu.add(this.this$0._certStatusVerb);
            }

            boolean certVerbAdded = false;

            try {
               if (!this.this$0._context.isCertificateInKeyStore(container.getCertificate())) {
                  if (this.this$0._certAddVerb == null) {
                     this.this$0._certAddVerb = new LDAPBrowser$LDAPBrowserVerb(this.this$0, 3, 1200226);
                  }

                  this.this$0._certAddVerb.setMultiple(false);
                  menu.add(this.this$0._certAddVerb);
                  if (this.this$0.isMatched(selectedIndex)) {
                     menu.setDefault(this.this$0._certAddVerb);
                  }

                  certVerbAdded = true;
               }
            } catch (LDAPBrowserException e) {
               if (this.this$0._certAddVerb == null) {
                  this.this$0._certAddVerb = new LDAPBrowser$LDAPBrowserVerb(this.this$0, 3, 1200226);
               }

               this.this$0._certAddVerb.setMultiple(false);
               menu.add(this.this$0._certAddVerb);
               if (this.this$0.isMatched(selectedIndex)) {
                  menu.setDefault(this.this$0._certAddVerb);
               }

               certVerbAdded = true;
            }

            try {
               if (!certVerbAdded && this.this$0._context.isCrossCertificateAvailable(container.getEntry())) {
                  if (this.this$0._crossCertVerb == null) {
                     this.this$0._crossCertVerb = new LDAPBrowser$LDAPBrowserVerb(this.this$0, 8, 1200230);
                  }

                  menu.add(this.this$0._crossCertVerb);
               }
            } catch (LDAPBrowserException var16) {
            }
         } else if (indices != null) {
            int numCertsToFetch = 0;
            int numCertsToAdd = 0;
            boolean crossCertsToAdd = false;

            for (int currentIndex : indices) {
               if (currentIndex >= 0) {
                  LDAPBrowserContainer container = (LDAPBrowserContainer)this.this$0._containers.elementAt(currentIndex);
                  if (container != null && container.getCertificate() == null) {
                     numCertsToFetch++;
                  }

                  try {
                     if (container != null && !this.this$0._context.isCertificateInKeyStore(container.getCertificate())) {
                        numCertsToAdd++;
                     }
                  } catch (LDAPBrowserException e) {
                     numCertsToAdd++;
                  }

                  try {
                     if (container != null && !crossCertsToAdd && this.this$0._context.isCrossCertificateAvailable(container.getEntry())) {
                        crossCertsToAdd = true;
                     }
                  } catch (LDAPBrowserException var14) {
                  }
               }
            }

            if (numCertsToAdd > 0) {
               if (this.this$0._certAddVerb == null) {
                  this.this$0._certAddVerb = new LDAPBrowser$LDAPBrowserVerb(this.this$0, 3, 1200226);
               }

               this.this$0._certAddVerb.setMultiple(numCertsToAdd != 1);
               menu.add(this.this$0._certAddVerb);
               if (numCertsToFetch <= 0) {
                  menu.setDefault(this.this$0._certAddVerb);
               }
            }

            if (numCertsToAdd == 0 && crossCertsToAdd) {
               if (this.this$0._crossCertVerb == null) {
                  this.this$0._crossCertVerb = new LDAPBrowser$LDAPBrowserVerb(this.this$0, 8, 1200230);
               }

               menu.add(this.this$0._crossCertVerb);
            }
         }
      }

      if (this.this$0._optionVerb == null) {
         this.this$0._optionVerb = new LDAPBrowser$LDAPBrowserVerb(this.this$0, 9, 16986368);
      }

      menu.add(this.this$0._optionVerb);
      if (this.this$0._closeVerb == null) {
         this.this$0._closeVerb = new LDAPBrowser$LDAPBrowserVerb(this.this$0, 10, 268501008);
      }

      menu.add(this.this$0._closeVerb);
   }
}

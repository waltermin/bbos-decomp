package net.rim.device.api.crypto.certificate;

import java.util.Vector;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.ui.component.TreeFieldCallback;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.vm.Array;

final class CertificateChainInfoDialog extends PopupDialog implements TreeFieldCallback, FieldChangeListener {
   TreeField _treeField;
   ButtonField _button;
   KeyStore _keyStore;

   public CertificateChainInfoDialog(String title, Object[][][] certChains, KeyStore keyStore, long style) {
      super((Manager)(new Object(299067162755072L)), style);
      this._keyStore = keyStore;
      this.add((Field)(new Object(title)));
      this.add((Field)(new Object()));
      this._treeField = (TreeField)(new Object(this, 0));
      this._treeField.setIndentWidth(10);
      this.add(this._treeField);
      this.setCancelAllowed(true);
      this.processChain(certChains, 0, 0);
      this._button = (ButtonField)(new Object(CommonResource.getString(9), 18014411394383872L));
      this._button.setChangeListener(this);
      this.add(this._button);
   }

   private final boolean processChain(Object[][][] certChains, int index, int parent) {
      int nextIndex = index + 1;
      Vector groups = this.groupChains(certChains, index);
      boolean somethingAdded = false;

      for (int j = 0; j < groups.size(); j++) {
         Object[][][] groupCerts = (Object[][])groups.elementAt(j);
         Object currentCertificate = groupCerts[0][index];
         int newParent = this._treeField.addChildNode(parent, currentCertificate);
         somethingAdded = true;
         if (!this.processChain(groupCerts, nextIndex, newParent) && currentCertificate instanceof Certificate) {
            Certificate currentCert = (Certificate)currentCertificate;
            if (!currentCert.isRoot()) {
               this._treeField.addChildNode(newParent, CertificateResources.getString(218));
            }
         }
      }

      return somethingAdded;
   }

   private final Vector groupChains(Object[][][] certChains, int index) {
      int numChains = certChains.length;
      Vector groups = (Vector)(new Object(numChains));

      for (int i = 0; i < numChains; i++) {
         Object[] currentChain = certChains[i];
         if (currentChain.length > index) {
            Object currentCert = currentChain[index];
            boolean chainAddedToGroup = false;

            for (int j = 0; j < groups.size(); j++) {
               Object[][][] groupCerts = (Object[][])groups.elementAt(j);
               Object[] firstChainInGroup = groupCerts[0];
               if (firstChainInGroup[index] == currentCert) {
                  Array.resize(groupCerts, groupCerts.length + 1);
                  groupCerts[groupCerts.length - 1] = currentChain;
                  chainAddedToGroup = true;
                  break;
               }
            }

            if (!chainAddedToGroup) {
               Object[][][] newGroup = new Object[][][]{currentChain};
               groups.addElement(newGroup);
            }
         }
      }

      return groups;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._button) {
         this.close(-1);
      }
   }

   @Override
   public final void drawTreeItem(TreeField treeField, Graphics graphics, int node, int y, int width, int indent) {
      Object cookie = treeField.getCookie(node);
      String label = null;
      if (!(cookie instanceof Certificate)) {
         if (cookie instanceof Object) {
            label = (String)cookie;
         }
      } else {
         Certificate cert = (Certificate)cookie;
         label = cert.getSubjectFriendlyName();
      }

      if (label != null) {
         graphics.drawText(label, indent, y, 64, width);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      boolean handled = super.keyChar(key, status, time);
      if (!handled) {
         if (key == '\n') {
            this.displayCurrentNode();
            return true;
         }

         if (key == 27 && this.isCancelAllowed()) {
            this.close(-1);
            return true;
         }
      }

      return handled;
   }

   private final void displayCurrentNode() {
      int idFocus = this._treeField.getCurrentNode();
      if (idFocus >= 0) {
         Object cookie = this._treeField.getCookie(idFocus);
         if (cookie instanceof Certificate) {
            Certificate cert = (Certificate)cookie;
            if (cert != null) {
               CertificateUtilities.displayCertificateDetails(cert, this._keyStore);
            }
         }
      }
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      if (this.getFieldWithFocus() == this._button) {
         this.close(-1);
         return true;
      } else {
         this.displayCurrentNode();
         return true;
      }
   }
}

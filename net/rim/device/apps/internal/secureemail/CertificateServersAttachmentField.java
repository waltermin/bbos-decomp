package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.api.crypto.certificate.CertificateServers;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.vm.Array;

public class CertificateServersAttachmentField extends HorizontalFieldManager implements ListFieldCallback, VerbProvider, CollectionListener {
   private CertificateServers _servers;
   private CertificateServersAttachmentModel _attachmentModel;
   private ListField _listField;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(
      -3843310740975580338L, "net.rim.device.internal.resource.crypto.CertificateServersOptions"
   );

   public CertificateServersAttachmentField(CertificateServersAttachmentModel attachmentModel) {
      this._attachmentModel = attachmentModel;
      this._servers = CertificateServers.getInstance();
      this.add(CryptoIcons.getLargeImageField(this._attachmentModel.getImage(), 0));
      this.add((Field)(new Object(3)));
      this._listField = (ListField)(new Object(1, 51539607552L));
      this._listField.setCookie(this);
      this._listField.setCallback(this);
      this.add(this._listField);
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      if (!ContextObject.getFlag(context, 87) && contextObject.getFlag(2)) {
         Verb defaultVerb = null;

         try {
            CertificateServerInfo serverInfo = this._attachmentModel.getCertificateServerInfo();
            if (serverInfo != null) {
               defaultVerb = new CertificateServersAttachmentField$ServerVerb(this, 1, 1200208);
               Array.resize(verbs, verbs.length + 1);
               verbs[verbs.length - 1] = defaultVerb;
               if (!this._servers.contains(serverInfo)) {
                  Array.resize(verbs, verbs.length + 1);
                  verbs[verbs.length - 1] = new CertificateServersAttachmentField$ServerVerb(this, 2, 1200226);
               }
            }
         } finally {
            return defaultVerb;
         }

         return defaultVerb;
      } else {
         return null;
      }
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      graphics.drawText(this._attachmentModel.getLabel(index), 0, y, 0, width);
   }

   @Override
   public Object get(ListField listField, int index) {
      return this._attachmentModel.getLabel(index);
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.handleElementAddedOrRemoved(collection, element);
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      this.handleElementAddedOrRemoved(collection, element);
   }

   @Override
   public void reset(Collection collection) {
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   private void handleElementAddedOrRemoved(Collection collection, Object element) {
      CertificateServerInfo serverInfo = this._attachmentModel.getCertificateServerInfo();
      if (serverInfo != null) {
         CertificateServerInfo elementServerInfo = (CertificateServerInfo)element;
         if (elementServerInfo != null) {
            if (elementServerInfo.equals(serverInfo)) {
               this._listField.invalidate(0);
            }
         }
      }
   }
}

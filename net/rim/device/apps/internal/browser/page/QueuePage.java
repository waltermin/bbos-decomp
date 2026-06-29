package net.rim.device.apps.internal.browser.page;

import java.util.Vector;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.cldc.io.utility.URIDecoder;

public final class QueuePage extends Page implements CollectionListener {
   private String _name;
   private Vector _requestFields;

   public QueuePage(FetchRequest fetchRequest) {
      super(fetchRequest, null, 0);
      RenderingSession renderingSession = BrowserDaemonRegistry.getInstance().getCurrentRenderingSession();
      RenderingOptions renderingOptions = renderingSession.getRenderingOptions();
      this.setBrowserContent(
         (BrowserContentImpl)(new Object(null, super._modelResult.getURL(), null, this, renderingOptions, super._modelResult.getNavigation()))
      );
      this._name = URIDecoder.decode(super._modelResult.getURL(), "iso-8859-1").substring(6);
      if (this._name.length() == 0) {
         this.setTitle(BrowserResources.getString(647));
      } else {
         String[] title = new Object[]{this._name};
         this.setTitle(MessageFormat.format(BrowserResources.getString(638), title));
      }

      this.populate();
   }

   private final void populate() {
      if (this._name.length() == 0) {
         String[] names = BrowserDaemonRegistry.getInstance().getOfflineQueue().getNames();
         if (names.length == 0) {
            HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(12884901888L));
            LabelField emptyField = (LabelField)(new Object(BrowserResources.getString(646)));
            hfm.add(emptyField);
            this.getBrowserContent().getContentManager().add(hfm);
            return;
         }

         for (int i = 0; i < names.length; i++) {
            this.getBrowserContent().getContentManager().add(new QueuePage$QueueField(names[i]));
         }
      } else {
         ReadableList queue = (ReadableList)BrowserDaemonRegistry.getInstance().getOfflineQueue().getQueue(this._name);
         if (queue == null || queue.size() == 0) {
            HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(12884901888L));
            LabelField emptyField = (LabelField)(new Object(BrowserResources.getString(639)));
            hfm.add(emptyField);
            this.getBrowserContent().getContentManager().add(hfm);
            return;
         }

         this._requestFields = (Vector)(new Object());
         int size = queue.size();

         for (int i = 0; i < size; i++) {
            QueuePage$RequestField field = new QueuePage$RequestField(this._name, (PageModel)queue.getAt(i));
            this.getBrowserContent().getContentManager().add(field);
            this._requestFields.addElement(field);
         }

         ((CollectionEventSource)queue).addCollectionListener(new Object(this));
      }
   }

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (this._requestFields != null) {
         for (int i = this._requestFields.size() - 1; i >= 0; i--) {
            QueuePage$RequestField field = (QueuePage$RequestField)this._requestFields.elementAt(i);
            if (field.getPageModel() == newElement) {
               field.update();
               return;
            }
         }
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (this._requestFields != null) {
         for (int i = this._requestFields.size() - 1; i >= 0; i--) {
            QueuePage$RequestField field = (QueuePage$RequestField)this._requestFields.elementAt(i);
            if (field.getPageModel() == element) {
               this.getBrowserContent().getContentManager().delete(field);
               return;
            }
         }
      }
   }
}

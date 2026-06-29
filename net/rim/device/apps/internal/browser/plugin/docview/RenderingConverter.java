package net.rim.device.apps.internal.browser.plugin.docview;

import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.docview.gui.DocViewManager;
import net.rim.device.apps.internal.docview.gui.DocViewRequestMore;

public class RenderingConverter extends BrowserContentProvider {
   private static final String[] ACCEPT = new String[]{"application/vnd.rim.ucs"};

   @Override
   public String[] getAccept(RenderingOptions renderingOptions) {
      return ACCEPT;
   }

   @Override
   public String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public BrowserContent getBrowserContent(BrowserContentProviderContext providerContext) {
      HttpConnection httpConnection = providerContext.getHttpConnection();
      RenderingApplication renderingApplication = providerContext.getRenderingApplication();
      UCSPluginParseNotify parseNotify = new UCSPluginParseNotify();
      UCSPluginNotify moreNotify = null;
      if (renderingApplication != null) {
         moreNotify = new UCSPluginNotify(httpConnection, renderingApplication);
      }

      boolean displayImageAsSlideshow = false;
      boolean isPresentation = false;
      String linkedFileName = httpConnection.getFile();
      if (linkedFileName != null) {
         int idx = linkedFileName.lastIndexOf(46);
         if (idx >= 0 && idx < linkedFileName.length() - 1) {
            String extension = linkedFileName.substring(idx + 1).toLowerCase();
            if (extension.compareTo("ppt") == 0) {
               displayImageAsSlideshow = true;
               isPresentation = true;
            } else if (extension.compareTo("pdf") == 0) {
               displayImageAsSlideshow = true;
               isPresentation = false;
            }
         }
      }

      label108:
      try {
         DocViewManager.processData(httpConnection.openInputStream(), true, -1, -1, parseNotify, moreNotify, displayImageAsSlideshow, isPresentation);
      } finally {
         break label108;
      }

      synchronized (parseNotify) {
         if (!parseNotify._notified) {
            label100:
            try {
               parseNotify.wait();
            } finally {
               break label100;
            }
         }
      }

      if (parseNotify._fld != null) {
         RenderingOptions renderingOptions = providerContext.getRenderingSession().getRenderingOptions();
         int flags = providerContext.getFlags();
         VerticalFieldManager mainManager = new VerticalFieldManager(3459045988797251584L);
         mainManager.add(parseNotify._fld);
         if (moreNotify != null) {
            UCSPluginNotify notify = moreNotify;
            UiApplication.getUiApplication().invokeLater(new RenderingConverter$1(this, notify), 500, false);
         }

         return new BrowserContentImpl(httpConnection, mainManager, (DocViewRequestMore)parseNotify._fld, renderingApplication, renderingOptions, flags);
      } else {
         return null;
      }
   }
}

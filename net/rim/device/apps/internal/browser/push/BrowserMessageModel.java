package net.rim.device.apps.internal.browser.push;

import net.rim.device.api.collection.WritableSet;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.internal.browser.page.BrowserPageModel;

public final class BrowserMessageModel extends BrowserPushModel {
   private Folder _browserFolder;
   private BrowserPageModel _pageModel;
   private String _url;

   public BrowserMessageModel(Folder browserFolder, BrowserPageModel model, String url) {
      this._pageModel = model;
      this._browserFolder = browserFolder;
      this._url = url;
   }

   @Override
   public final void run() {
      this.run(32, this._url);
   }

   @Override
   protected final void doAction() {
      WritableSet browserMessages = (WritableSet)this._browserFolder.getContainedItems();
      if (!this._pageModel.checkCrypt(true, true)) {
         this._pageModel.reCrypt(true, true);
      }

      browserMessages.add(this._pageModel);
      this._pageModel.changeStatus(3);
   }

   public final String getUrl() {
      return this._url;
   }
}

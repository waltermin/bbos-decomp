package net.rim.wica.runtime.script.internal;

import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.LongHashtable;
import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.common.metadata.component.EnumCollection;
import net.rim.wica.runtime.access.invoker.AccessInvokeService;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.REError;
import net.rim.wica.runtime.metadata.component.ui.ScreenManager;
import net.rim.wica.runtime.metadata.component.ui.ScreenModel;
import net.rim.wica.runtime.script.internal.appenv.ESApplication;
import net.rim.wica.runtime.script.internal.appenv.ESCollectionPrototype;
import net.rim.wica.runtime.script.internal.appenv.ESControlPrototype;
import net.rim.wica.runtime.script.internal.appenv.ESData;
import net.rim.wica.runtime.script.internal.appenv.ESDataPrototype;
import net.rim.wica.runtime.script.internal.appenv.ESEnumPrototype;
import net.rim.wica.runtime.script.internal.appenv.ESMDSArrayPrototype;
import net.rim.wica.runtime.script.internal.appenv.ESMenuItemPrototype;
import net.rim.wica.runtime.script.internal.appenv.ESMenuPrototype;
import net.rim.wica.runtime.script.internal.appenv.ESMessagePrototype;
import net.rim.wica.runtime.script.internal.appenv.ESMetaEnum;
import net.rim.wica.runtime.script.internal.appenv.ESScreenPrototype;
import net.rim.wica.runtime.script.internal.appenv.ESStringBundlePrototype;
import net.rim.wica.runtime.script.internal.appenv.ESTableElementPrototype;
import net.rim.wica.runtime.script.internal.handler.FieldHandlerFactory;
import net.rim.wica.runtime.ui.UiService;

public final class WicaAppContext {
   private ScriptEngineInternal _engine;
   private Wiclet _wiclet;
   private ScreenManager _screenManager;
   private AccessInvokeService _accessServ;
   private UiService _uiServ;
   private WicletRuntime _runtime;
   private TypeConverter _converter;
   private ESApplication _application;
   private ESMDSError _error;
   private ESDebug _debug;
   private ESCollectionPrototype _collectionPrototype;
   private ESControlPrototype _controlPrototype;
   private ESDataPrototype _dataPrototype;
   private ESEnumPrototype _enumPrototype;
   private ESMDSArrayPrototype _arrayPrototype;
   private ESMenuItemPrototype _menuItemPrototype;
   private ESMenuPrototype _menuPrototype;
   private ESMessagePrototype _messagePrototype;
   private ESScreenPrototype _screenPrototype;
   private ESStringBundlePrototype _stringBundlePrototype;
   private ESTableElementPrototype _tablePrototype;
   private LongHashtable _dataInstances;
   private IntHashtable _enumInstances;

   WicaAppContext(ScriptEngineInternal engine) {
      this._engine = engine;
      this._converter = new TypeConverter(this);
      FieldHandlerFactory.setWicaAppContext(this);
      this._dataInstances = (LongHashtable)(new Object(32));
      this._enumInstances = (IntHashtable)(new Object(10));
   }

   public final ScriptEngineInternal getEngine() {
      return this._engine;
   }

   public final TypeConverter getConverter() {
      return this._converter;
   }

   public final ESCollectionPrototype getCollectionPrototype() {
      return this._collectionPrototype;
   }

   public final ESDataPrototype getDataPrototype() {
      return this._dataPrototype;
   }

   public final ESEnumPrototype getEnumPrototype() {
      return this._enumPrototype;
   }

   public final ESMDSArrayPrototype getArrayPrototype() {
      return this._arrayPrototype;
   }

   public final ESMessagePrototype getMessagePrototype() {
      return this._messagePrototype;
   }

   public final ESScreenPrototype getScreenPrototype() {
      return this._screenPrototype;
   }

   public final ESControlPrototype getControlPrototype() {
      return this._controlPrototype;
   }

   public final ESMenuPrototype getMenuPrototype() {
      return this._menuPrototype;
   }

   public final ESMenuItemPrototype getMenuItemPrototype() {
      return this._menuItemPrototype;
   }

   public final ESTableElementPrototype getTablePrototype() {
      return this._tablePrototype;
   }

   public final ESStringBundlePrototype getStringBundlePrototype() {
      return this._stringBundlePrototype;
   }

   public final Object getResource(String name) {
      Object resource = null;
      int resourceId = this._wiclet.getDefHandle(name);
      if (resourceId != -1) {
         String uri = this._wiclet.getResources().getResourceURI(resourceId);
         resource = this._wiclet.getContext().getWicletStore().getResource(uri);
      }

      return resource;
   }

   public final void setScreenManager(ScreenManager screenManager) {
      this._screenManager = screenManager;
   }

   public final ScreenManager getScreenManager() {
      return this._screenManager;
   }

   public final void setRuntime(WicletRuntime runtime) {
      this._runtime = runtime;
   }

   public final WicletRuntime getRuntime() {
      return this._runtime;
   }

   public final long createDataInstance(DataCollection collection, long handle) {
      long result = Value.NULL;
      if (handle != -1) {
         if (collection == null || collection.contains(handle)) {
            ESData esObject = (ESData)this._dataInstances.get(handle);
            if (esObject == null) {
               esObject = new ESData(collection, handle, this._dataPrototype);
               this._dataInstances.put(handle, esObject);
            }

            result = Value.makeObjectValue(esObject);
         } else if (this._dataInstances.containsKey(handle)) {
            this._dataInstances.remove(handle);
         }
      }

      return result;
   }

   public final ESMetaEnum createEnumeration(String name, int type) {
      ESMetaEnum metaEnum = (ESMetaEnum)this._enumInstances.get(type);
      if (metaEnum == null) {
         metaEnum = new ESMetaEnum(name, type, this);
         this._enumInstances.put(type, metaEnum);
      }

      return metaEnum;
   }

   public final long createEnumValue(int value, int type) {
      return this.createEnumeration(((StringBuffer)(new Object("Unknown"))).append(type).toString(), type).getESEnum(value);
   }

   public final EnumCollection getEnumCollection() {
      return this._wiclet.getEnums();
   }

   public final void load(Wiclet wiclet) {
      this._wiclet = wiclet;
      this._application = new ESApplication(this, wiclet.getGlobals());
      GlobalObject globalObject = this._engine.getGlobalObject();
      this._error = new ESMDSError();
      this._debug = new ESDebug(this);
      this._collectionPrototype = new ESCollectionPrototype(this);
      this._enumPrototype = new ESEnumPrototype();
      this._dataPrototype = new ESDataPrototype(this);
      this._screenPrototype = new ESScreenPrototype();
      this._arrayPrototype = new ESMDSArrayPrototype();
      this._messagePrototype = new ESMessagePrototype();
      this._controlPrototype = new ESControlPrototype();
      this._menuPrototype = new ESMenuPrototype();
      this._menuItemPrototype = new ESMenuItemPrototype();
      this._tablePrototype = new ESTableElementPrototype(this);
      this._stringBundlePrototype = new ESStringBundlePrototype();
      int defaultFlags = 5;
      globalObject.addField("Error", defaultFlags, Value.makeObjectValue(this._error));
      globalObject.addField("Screen", defaultFlags, Value.makeObjectValue(new ESNavigation(this)));
      globalObject.addField("Dialog", defaultFlags, Value.makeObjectValue(new ESPopupScreen(this)));
      globalObject.addField("System", defaultFlags, Value.makeObjectValue(new ESSystem(this)));
      globalObject.addField("Logger", defaultFlags, Value.makeObjectValue(new ESLogger()));
      globalObject.addField("Debug", defaultFlags, Value.makeObjectValue(this._debug));
      globalObject.setRedirectionObject(this._application);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void dispose() {
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         int functionId = this._wiclet.getDefHandle("onExit");
         if (functionId != -1) {
            this._engine.call(functionId, (long[])null);
            var4 = false;
         } else {
            var4 = false;
         }
      } finally {
         if (var4) {
            this._application.dispose();
         }
      }

      this._application.dispose();
   }

   public final long[] getFunctionParams(long[] args) {
      int length = args.length;
      long[] params = new long[length];
      DataCollection collection = null;

      for (int i = 0; i < length; i++) {
         collection = this._wiclet.getDataCollection((int)(args[i] >> 32));
         params[i] = this.createDataInstance(collection, args[i]);
      }

      return params;
   }

   public final void setMDSError(REError error) {
      this._error.setError(error);
   }

   public final AccessInvokeService getAccessServ() {
      return this._accessServ;
   }

   public final void setAccessServ(AccessInvokeService serv) {
      this._accessServ = serv;
   }

   public final void setUiServ(UiService serv) {
      this._uiServ = serv;
   }

   public final ScreenModel getCurrentScreenModel() {
      return this._screenManager.getCurrentScreenModel();
   }

   public final int displayDlg(String message, int dialogType) {
      return this._uiServ.displayModalDialog(dialogType, message);
   }

   public final Wiclet getWiclet() {
      return this._wiclet;
   }
}

package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;

public final class ProxyModel implements PersistableRIMModel, FieldProvider, VerbProvider, ConversionProvider, ActionProvider {
   private long _root;
   private int _objecthandle;
   public static final long PROXY_MODEL_ID;
   public static final long PROXY_MODEL_ROOTID;
   public static final long PROXY_MODEL_OBJECTHANDLE;

   public final void set(long rootId, int objecthandle) {
      this._root = rootId;
      this._objecthandle = objecthandle;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Object o = this.getObject();
      if (!(o instanceof Object)) {
         return null;
      }

      VerbProvider vb = (VerbProvider)o;
      return vb.getVerbs(context, verbs);
   }

   public final long getRootId() {
      return this._root;
   }

   public final int getObjectHandle() {
      return this._objecthandle;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean perform(long actionId, Object context) {
      boolean retval = false;
      ContextObject contextObject = ContextObject.castOrCreate(context);
      Object oldProxyModel = contextObject.get(5983802064804519487L);
      boolean var11 = false /* VF: Semaphore variable */;

      try {
         var11 = true;
         contextObject.put(5983802064804519487L, this);
         Object o = this.getObject();
         if (!(o instanceof Object)) {
            var11 = false;
         } else {
            ActionProvider ap = (ActionProvider)o;
            retval = ap.perform(actionId, context);
            var11 = false;
         }
      } finally {
         if (var11) {
            if (oldProxyModel != null) {
               contextObject.put(5983802064804519487L, oldProxyModel);
            } else {
               contextObject.remove(5983802064804519487L);
            }
         }
      }

      if (oldProxyModel != null) {
         contextObject.put(5983802064804519487L, oldProxyModel);
      } else {
         contextObject.remove(5983802064804519487L);
      }

      return retval;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      Object o = this.getObject();
      if (!(o instanceof Object)) {
         return false;
      }

      ConversionProvider cp = (ConversionProvider)o;
      return cp.convert(context, target);
   }

   public final Object getObject() {
      ProxyModelStore store = ProxyModelStoreManager.getProxyModelStore(this._root);
      return store == null ? null : store.get(this._objecthandle);
   }

   public final void clear() {
      this._root = this._objecthandle = 0;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      Object o = this.getObject();
      if (!(o instanceof Object)) {
         return false;
      }

      FieldProvider fp = (FieldProvider)o;
      return fp.grabDataFromField(field, context);
   }

   @Override
   public final boolean validate(Field field, Object context) {
      Object o = this.getObject();
      if (!(o instanceof Object)) {
         return false;
      }

      FieldProvider fp = (FieldProvider)o;
      return fp.validate(field, context);
   }

   @Override
   public final int getOrder(Object context) {
      Object o = this.getObject();
      if (!(o instanceof Object)) {
         return 0;
      }

      FieldProvider fp = (FieldProvider)o;
      return fp.getOrder(context);
   }

   @Override
   public final Field getField(Object context) {
      Object o = this.getObject();
      if (!(o instanceof Object)) {
         return null;
      }

      FieldProvider fp = (FieldProvider)o;
      return fp.getField(context);
   }

   public ProxyModel(long rootId, int objecthandle) {
      this._root = rootId;
      this._objecthandle = objecthandle;
   }

   public ProxyModel(Object context) {
      Object o = ContextObject.get(context, 1480271510226910726L);
      if (o instanceof Object) {
         Long l = (Long)o;
         this._root = l;
      }

      o = ContextObject.get(context, -4673191042266301337L);
      if (o instanceof Object) {
         Integer i = (Integer)o;
         this._objecthandle = i;
      }
   }
}

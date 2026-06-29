package net.rim.ecmascript.util;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.ecmascript.resources.ECMACompilerResource;
import net.rim.vm.Reflect;

public class Resources implements ECMACompilerResource {
   private static boolean _init;
   private static ResourceBundle _rb;
   private static String[] _resources;

   public static synchronized String getString(int id) {
      if (!_init) {
         _init = true;
         if (Misc.getPlatform() == 0) {
            try {
               int clazz = Reflect.getClassClass(Class.forName("net.rim.ecmascript.resources.J2SEResources"));
               int[] fields = Reflect.getFields(clazz, false);
               _resources = (Object[])Misc.toObject(Reflect.getWordField(clazz, fields[0], null));
            } finally {
               return _resources != null ? _resources[id] : _rb.getString(id);
            }
         } else {
            _rb = ResourceBundle.getBundle(1788996868539233407L, "net.rim.ecmascript.resources.ECMACompiler");
         }
      }

      return _resources != null ? _resources[id] : _rb.getString(id);
   }
}

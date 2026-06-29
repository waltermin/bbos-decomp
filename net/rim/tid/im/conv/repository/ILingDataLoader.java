package net.rim.tid.im.conv.repository;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.tid.itie.LingDataRegistry;
import net.rim.tid.itie.LinguisticData;

public class ILingDataLoader {
   protected String[][] _resNames;
   protected Locale[] _locales;
   protected int[] _types;
   protected int[] _versions;

   protected void registerData() {
      String module = ApplicationDescriptor.currentApplicationDescriptor().getModuleName();
      StringBuffer message = (StringBuffer)(new Object());

      for (int i = 0; i < this._resNames.length; i++) {
         byte[][] data = this.loadRes(module, this._resNames[i]);
         if (data != null) {
            message.setLength(0);
            this.composeDiagnosticMessage(message, module, this._resNames[i], this._locales[i]);
            LingDataRegistry.registerLingData(
               this._locales[i].getCode(), (LinguisticData)(new Object(module, this._types[i], this._versions[i], data, message.toString(), module))
            );
         }
      }
   }

   private void composeDiagnosticMessage(StringBuffer result, String module, String[] chunks, Locale locale) {
      result.append(" Ling data from ");
      result.append(module);
      result.append('[');

      for (int i = 0; i < chunks.length; i++) {
         result.append(chunks[i]);
         result.append(" ");
      }

      result.append(']');
      result.append(" was rejected for locale ");
      result.append(locale.toString());
   }

   private byte[][] loadRes(String module, String[] names) {
      Resource resource = Resource$Internal.getResourceClass(module);
      if (resource == null) {
         return (byte[][])null;
      }

      byte[][] result = new byte[names.length][];

      for (int i = 0; i < names.length; i++) {
         result[i] = resource.getResource(names[i]);
         if (result[i] == null) {
            System.out.println(((StringBuffer)(new Object("WARNING: can't load "))).append(names[i]).append(" from ").append(module).toString());
            return (byte[][])null;
         }
      }

      return result;
   }
}

package net.rim.tid.data;

import java.io.OutputStream;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.tid.io.ContinuousInputStream;

class MinimalResourceSpec implements IResourceSpec {
   public byte[] loadRimRes(String libName, String toLoad) {
      Resource resource = Resource$Internal.getResourceClass(libName);
      if (resource == null) {
         return null;
      }

      String name = toLoad;
      return resource.getResource(name);
   }

   @Override
   public ContinuousInputStream getInputStream(String location, Class toLoad, boolean needCache) {
      String fileName = location;
      String path = "";
      int index = location.lastIndexOf(92);
      if (index == -1) {
         index = location.lastIndexOf(47);
      }

      if (index != -1) {
         fileName = location.substring(index + 1);
         path = location.substring(0, index);
         if (path.length() > 0 && (path.charAt(0) == '\\' || path.charAt(0) == '/')) {
            path = path.substring(1);
         }
      }

      int extIndex = fileName.lastIndexOf(46);
      String extention = "";
      if (extIndex != -1) {
         extention = fileName.substring(extIndex + 1);
         fileName = fileName.substring(0, extIndex);
      }

      String libName = path.replace('\\', '_').replace('/', '_');
      byte[] data = this.loadRimRes(libName, fileName + '.' + extention);
      return data != null ? new ContinuousInputStream(data) : null;
   }

   @Override
   public OutputStream getOutputStream(String location) {
      return null;
   }

   @Override
   public String getProtocol() {
      return "file:";
   }
}

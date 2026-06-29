package net.rim.tid.data;

import java.util.Vector;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.tid.io.ContinuousInputStream;

public class RIMResourceSpec extends ResourceSpec {
   @Override
   protected ContinuousInputStream getInputStream0(String location, Class toLoad) {
      return this.loadRimRes(location);
   }

   protected ContinuousInputStream loadRimRes(String location) {
      location = this.deleteProtocol(location);
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
      byte[] data = this.loadRimRes(libName, ((StringBuffer)(new Object())).append(fileName).append('.').append(extention).toString());
      return data != null ? new ContinuousInputStream(data) : this.multipleSearch(libName, fileName, extention);
   }

   public byte[] loadRimRes(String libName, String toLoad) {
      Resource resource = Resource$Internal.getResourceClass(libName);
      if (resource == null) {
         return null;
      }

      String name = toLoad;
      return resource.getResource(name);
   }

   public ContinuousInputStream multipleSearch(String libName, String fileName, String ext) {
      Vector result = (Vector)(new Object());
      int counter = 1;
      byte[] data = null;

      do {
         data = this.loadRimRes(
            ((StringBuffer)(new Object())).append(libName).append("_").append(fileName).append("_00").append(counter).toString(),
            ((StringBuffer)(new Object())).append(fileName).append(".00").append(counter).append(".").append(ext).toString()
         );
         if (data != null) {
            result.addElement(data);
            System.err.println(((StringBuffer)(new Object("result="))).append(counter).toString());
         }

         counter++;
      } while (data != null);

      if (result.size() == 0) {
         return null;
      }

      byte[][][] tmp = new byte[result.size()][][];
      result.copyInto(tmp);
      return new ContinuousInputStream(tmp);
   }
}

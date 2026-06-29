package net.rim.tid.data;

import java.io.OutputStream;
import java.util.Vector;
import net.rim.tid.io.ContinuousInputStream;

public class DataManager {
   private IResourceSpec iDefaultSpec;
   private Vector iSpecList = (Vector)(new Object());
   private static DataManager iInstance;

   private DataManager() {
      this.iDefaultSpec = new RIMResourceSpec();
   }

   public ContinuousInputStream getInputStream(String location, Class toLoad, boolean needCache) {
      if (location == null) {
         return null;
      }

      IResourceSpec spec = this.getResourceSpec(location);
      return spec.getInputStream(location, toLoad, needCache);
   }

   public ContinuousInputStream getInputStream(String location, boolean needCache) {
      Class to_load = null;
      return this.getInputStream(location, to_load, needCache);
   }

   public OutputStream getOutputStream(String location) {
      if (location == null) {
         return null;
      }

      IResourceSpec spec = this.getResourceSpec(location);
      return spec.getOutputStream(location);
   }

   protected IResourceSpec getResourceSpec(String location) {
      String protocol = this.iDefaultSpec.getProtocol();
      if (location.substring(0, protocol.length()).equals(protocol)) {
         return this.iDefaultSpec;
      }

      int size = this.iSpecList.size();

      for (int i = 0; i < size; i++) {
         IResourceSpec temp = (IResourceSpec)this.iSpecList.elementAt(i);
         protocol = temp.getProtocol();
         if (location.substring(0, protocol.length()).equals(protocol)) {
            return temp;
         }
      }

      return this.iDefaultSpec;
   }

   public void addResourceSpec(IResourceSpec toAdd) {
      this.iSpecList.addElement(toAdd);
   }

   public void setDefaultResourceSpec(IResourceSpec toAdd) {
      this.iDefaultSpec = toAdd;
   }

   public static DataManager getInstance() {
      if (iInstance == null) {
         iInstance = new DataManager();
      }

      return iInstance;
   }
}

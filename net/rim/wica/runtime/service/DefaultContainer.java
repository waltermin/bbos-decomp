package net.rim.wica.runtime.service;

import java.util.Hashtable;
import java.util.Vector;

public class DefaultContainer implements Container {
   private ServiceProvider _parentProvider;
   private Hashtable _componentTable = new Hashtable();
   private Vector _components = new Vector();
   static Class class$net$rim$wica$runtime$service$Container;

   public DefaultContainer() {
      this(null);
   }

   public DefaultContainer(ServiceProvider parentProvider) {
      this._parentProvider = parentProvider;
   }

   @Override
   public void registerComponent(Class componentInterface, Class componentImpl) {
      this.registerComponent(componentInterface, new DefaultComponentAdapter(componentImpl), false);
   }

   @Override
   public void registerComponent(Class componentInterface, Class componentImpl, boolean promote) {
      this.registerComponent(componentInterface, new DefaultComponentAdapter(componentImpl), promote);
   }

   @Override
   public void registerComponent(Class componentInterface, ComponentAdapter adapter) {
      this.registerComponent(componentInterface, adapter, false);
   }

   @Override
   public void registerComponent(Class componentInterface, ComponentAdapter adapter, boolean promote) {
      if (componentInterface == null) {
         throw new NullPointerException("componentInterface cannot be null");
      }

      if (promote && this._parentProvider != null) {
         Object parentContainer = this._parentProvider
            .getService(
               class$net$rim$wica$runtime$service$Container == null
                  ? (class$net$rim$wica$runtime$service$Container = class$("net.rim.wica.runtime.service.Container"))
                  : class$net$rim$wica$runtime$service$Container
            );
         if (parentContainer != null) {
            ((Container)parentContainer).registerComponent(componentInterface, adapter, promote);
         }
      }

      String name = componentInterface.getName();
      if (this._componentTable.containsKey(name)) {
         throw new IllegalArgumentException("The component " + componentInterface.getName() + "already exists in the container");
      }

      ((DefaultComponentAdapter)adapter).setContainer(this);
      this._components.addElement(adapter);
      this._componentTable.put(name, adapter);
   }

   @Override
   public void registerComponent(Class componentInterface, Object componentImpl) {
      if (componentInterface == null) {
         throw new NullPointerException("componentInterface cannot be null");
      }

      String name = componentInterface.getName();
      if (this._componentTable.containsKey(name)) {
         throw new IllegalArgumentException("The component " + componentInterface.getName() + "already exists in the container");
      }

      this._components.addElement(componentImpl);
      this._componentTable.put(name, componentImpl);
   }

   @Override
   public void stop() {
      for (int i = this._components.size() - 1; i >= 0; i--) {
         Object component = this._components.elementAt(i);
         if (component instanceof Startable) {
            ((Startable)component).stop();
         }
      }
   }

   @Override
   public Object getService(Class serviceInterface) {
      String name = serviceInterface.getName();
      Object result = this._componentTable.get(name);
      if (result == null && this._parentProvider != null) {
         result = this._parentProvider.getService(serviceInterface);
      }

      return !(result instanceof ComponentAdapter) ? result : ((ComponentAdapter)result).getInstance();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}

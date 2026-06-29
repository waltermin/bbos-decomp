package net.rim.wica.runtime.service;

public class DefaultComponentAdapter implements ComponentAdapter, Startable {
   private Class _componentImpl;
   private Object _component;
   private DefaultContainer _container;

   public DefaultComponentAdapter(Class componentImpl) {
      this._componentImpl = componentImpl;
   }

   private void service() {
      if (this._component instanceof Serviceable) {
         ((Serviceable)this._component).setServices(this._container);
      }
   }

   void setContainer(DefaultContainer container) {
      this._container = container;
   }

   @Override
   public void start() {
      if (this._component instanceof Startable) {
         ((Startable)this._component).start();
      }
   }

   @Override
   public void stop() {
      if (this._component instanceof Startable) {
         ((Startable)this._component).stop();
      }
   }

   @Override
   public Object getInstance() {
      if (this._component != null) {
         return this._component;
      }

      try {
         this._component = this._componentImpl.newInstance();
         this.service();
         this.start();
         return this._component;
      } finally {
         ;
      }
   }
}

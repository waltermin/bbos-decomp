package net.rim.device.api.browser.field;

public interface Destroyable {
   int DESTROY_METHOD_SELF_ON_UNDISPLAY;
   int DESTROY_METHOD_MANAGED_DESTROY;

   void setDestroyMethod(int var1);

   void destroy();
}

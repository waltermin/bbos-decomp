package net.rim.device.api.browser.field;

public interface Destroyable {
   int DESTROY_METHOD_SELF_ON_UNDISPLAY = 0;
   int DESTROY_METHOD_MANAGED_DESTROY = 1;

   void setDestroyMethod(int var1);

   void destroy();
}

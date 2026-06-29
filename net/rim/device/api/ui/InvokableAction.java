package net.rim.device.api.ui;

public interface InvokableAction {
   void actionPerformed(Object var1);

   int getActionId();

   boolean isDefault();
}

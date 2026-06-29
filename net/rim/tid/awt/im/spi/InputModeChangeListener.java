package net.rim.tid.awt.im.spi;

public interface InputModeChangeListener {
   int LISTENER_ALREADY_SET;
   int NOTIFIER_IS_NOT_INITIALIZED;
   int NOT_SUPPORTED;

   int inputModeChanged(int var1);
}

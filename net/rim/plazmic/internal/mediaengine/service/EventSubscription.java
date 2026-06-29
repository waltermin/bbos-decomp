package net.rim.plazmic.internal.mediaengine.service;

import net.rim.plazmic.mediaengine.MediaListener;

public interface EventSubscription {
   String ID = "EventSubscription";

   void addListener(MediaListener var1);

   void addListener(int var1, MediaListener var2);

   void addListener(int var1, int var2, MediaListener var3);

   void removeListener(MediaListener var1);
}

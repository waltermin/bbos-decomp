package net.rim.device.api.io;

public interface Seekable {
   long getPosition();

   long getSize();

   void setPosition(long var1);
}

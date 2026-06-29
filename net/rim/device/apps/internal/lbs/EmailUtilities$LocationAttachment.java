package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.api.messaging.messagelist.MessageAttachment;

final class EmailUtilities$LocationAttachment implements MessageAttachment {
   String _name;
   byte[] _data;

   EmailUtilities$LocationAttachment(String name, byte[] data) {
      this._name = name;
      this._data = data;
   }

   @Override
   public final String getProperty(String name) {
      if (name.equalsIgnoreCase("Content-Type")) {
         return "text/vnd.rim.location";
      } else if (name.equalsIgnoreCase("Content-Length")) {
         return Integer.toString(this._data.length);
      } else {
         return name.equalsIgnoreCase("Content-Location") ? this._name : null;
      }
   }

   @Override
   public final Object getData() {
      return this._data;
   }
}

package net.rim.device.apps.api.framework.model;

public interface URLProvider extends RIMModel {
   int URL_TYPE_UNKNOWN = 0;
   int URL_TYPE_HTTP = 1;
   int URL_TYPE_MAILTO = 2;
   int URL_TYPE_TEL = 3;

   String getURL();

   int getURLType();
}

package net.rim.device.apps.api.framework.model;

public interface URLProvider extends RIMModel {
   int URL_TYPE_UNKNOWN;
   int URL_TYPE_HTTP;
   int URL_TYPE_MAILTO;
   int URL_TYPE_TEL;

   String getURL();

   int getURLType();
}

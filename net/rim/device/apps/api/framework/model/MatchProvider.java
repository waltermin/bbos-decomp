package net.rim.device.apps.api.framework.model;

public interface MatchProvider extends RIMModel {
   int NOTAPPLICABLE;
   int NOTMATCHED;
   int MATCHED;

   int match(Object var1);
}

package net.rim.wica.runtime.lifecycle;

import net.rim.device.api.util.StringTokenizer;

public final class ApplicationVersion {
   private int _dataVersion;
   private int _messageVersion;
   private int _featureVersion;
   private static final char DELIMETER = '.';

   public ApplicationVersion(String version) {
      StringTokenizer st = (StringTokenizer)(new Object(version, '.'));
      this._dataVersion = Integer.parseInt(st.nextToken());
      this._messageVersion = Integer.parseInt(st.nextToken());
      this._featureVersion = Integer.parseInt(st.nextToken());
   }

   public final int getDataVersion() {
      return this._dataVersion;
   }

   public final int getMessageVersion() {
      return this._messageVersion;
   }

   public final int getFeatureVersion() {
      return this._featureVersion;
   }

   public final boolean isIncompatibleWith(ApplicationVersion other) {
      return !this.isDataCompatibleWith(other);
   }

   public final boolean isDataCompatibleWith(ApplicationVersion other) {
      return this._dataVersion == other._dataVersion;
   }

   public final boolean isMessageCompatibleWith(ApplicationVersion other) {
      return this._messageVersion == other._messageVersion;
   }
}

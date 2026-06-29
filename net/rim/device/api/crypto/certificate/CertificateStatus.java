package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.keystore.CertificateStatusManager;
import net.rim.device.api.util.Persistable;

public final class CertificateStatus implements Persistable {
   private int _status;
   private long _producedAt;
   private long _thisUpdate;
   private long _nextUpdate;
   private long _revocationDate;
   private int _revocationReason;
   private long _creationDate;
   public static final int GOOD = 0;
   public static final int REVOKED = 1;
   public static final int UNKNOWN = -1;

   public CertificateStatus() {
      this(-1, -1, -1, -1, -1, -1);
   }

   public CertificateStatus(int status, long producedAt, long thisUpdate, long nextUpdate, long revocationDate, int revocationReason) {
      if (status == 1) {
         if (revocationDate <= 0) {
            throw new Object();
         }

         if (revocationReason == -1) {
            revocationReason = 0;
         }
      } else if (revocationDate > 0 || revocationReason != -1) {
         throw new Object();
      }

      this._status = status;
      this._producedAt = producedAt;
      this._thisUpdate = thisUpdate;
      this._nextUpdate = nextUpdate;
      this._revocationDate = revocationDate;
      this._revocationReason = revocationReason;
      this._creationDate = System.currentTimeMillis();
   }

   public CertificateStatus(int status, long producedAt, long thisUpdate, long nextUpdate, long revocationDate) {
      this(status, producedAt, thisUpdate, nextUpdate, revocationDate, -1);
   }

   public final int getStatus() {
      return this._status;
   }

   public final long getProducedAtTime() {
      return this._producedAt;
   }

   public final long getThisUpdateTime() {
      return this._thisUpdate;
   }

   public final long getNextUpdateTime() {
      return this._nextUpdate;
   }

   public final long getRevocationTime() {
      return this._revocationDate;
   }

   public final int getRevocationReason() {
      return this._revocationReason;
   }

   public final long getCreationDate() {
      return this._creationDate;
   }

   public final boolean isStale() {
      return this.isStale(Long.MAX_VALUE);
   }

   public final boolean isStale(long maxAge) {
      long age = System.currentTimeMillis() - this.getProducedAtTime();
      return age > maxAge || age > CertificateStatusManager.getStaleTime();
   }

   public final int compare(int status) {
      if (this._status == status) {
         return 0;
      } else {
         return this._status > status ? 1 : -1;
      }
   }

   public final int compare(CertificateStatus status) {
      return this.compare(status.getStatus());
   }

   @Override
   public final boolean equals(Object obj) {
      if (!(obj instanceof CertificateStatus)) {
         return false;
      }

      CertificateStatus status = (CertificateStatus)obj;
      return status._status == this._status
         && status._producedAt == this._producedAt
         && status._thisUpdate == this._thisUpdate
         && status._nextUpdate == this._nextUpdate
         && status._revocationDate == this._revocationDate
         && status._revocationReason == this._revocationReason;
   }
}

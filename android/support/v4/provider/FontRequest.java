package android.support.v4.provider;

import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.util.Preconditions;
import android.util.Base64;
import java.util.List;

public final class FontRequest
{
  private final List<List<byte[]>> mCertificates;
  private final int mCertificatesArray;
  private final String mIdentifier;
  private final String mProviderAuthority;
  private final String mProviderPackage;
  private final String mQuery;

  public FontRequest(@NonNull String paramString1, @NonNull String paramString2, @NonNull String paramString3, @ArrayRes int paramInt)
  {
    this.mProviderAuthority = ((String)Preconditions.checkNotNull(paramString1));
    this.mProviderPackage = ((String)Preconditions.checkNotNull(paramString2));
    this.mQuery = ((String)Preconditions.checkNotNull(paramString3));
    this.mCertificates = null;
    if (paramInt != 0);
    for (boolean bool = true; ; bool = false)
    {
      Preconditions.checkArgument(bool);
      this.mCertificatesArray = paramInt;
      this.mIdentifier = (this.mProviderAuthority + "-" + this.mProviderPackage + "-" + this.mQuery);
      return;
    }
  }

  public FontRequest(@NonNull String paramString1, @NonNull String paramString2, @NonNull String paramString3, @NonNull List<List<byte[]>> paramList)
  {
    this.mProviderAuthority = ((String)Preconditions.checkNotNull(paramString1));
    this.mProviderPackage = ((String)Preconditions.checkNotNull(paramString2));
    this.mQuery = ((String)Preconditions.checkNotNull(paramString3));
    this.mCertificates = ((List)Preconditions.checkNotNull(paramList));
    this.mCertificatesArray = 0;
    this.mIdentifier = (this.mProviderAuthority + "-" + this.mProviderPackage + "-" + this.mQuery);
  }

  @Nullable
  public List<List<byte[]>> getCertificates()
  {
    return this.mCertificates;
  }

  @ArrayRes
  public int getCertificatesArrayResId()
  {
    return this.mCertificatesArray;
  }

  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public String getIdentifier()
  {
    return this.mIdentifier;
  }

  public String getProviderAuthority()
  {
    return this.mProviderAuthority;
  }

  public String getProviderPackage()
  {
    return this.mProviderPackage;
  }

  public String getQuery()
  {
    return this.mQuery;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("FontRequest {mProviderAuthority: " + this.mProviderAuthority + ", mProviderPackage: " + this.mProviderPackage + ", mQuery: " + this.mQuery + ", mCertificates:");
    for (int i = 0; i < this.mCertificates.size(); i++)
    {
      localStringBuilder.append(" [");
      List localList = (List)this.mCertificates.get(i);
      for (int j = 0; j < localList.size(); j++)
      {
        localStringBuilder.append(" \"");
        localStringBuilder.append(Base64.encodeToString((byte[])localList.get(j), 0));
        localStringBuilder.append("\"");
      }
      localStringBuilder.append(" ]");
    }
    localStringBuilder.append("}");
    localStringBuilder.append("mCertificatesArray: " + this.mCertificatesArray);
    return localStringBuilder.toString();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.provider.FontRequest
 * JD-Core Version:    0.6.0
 */
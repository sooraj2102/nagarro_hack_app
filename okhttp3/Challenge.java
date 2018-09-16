package okhttp3;

import okhttp3.internal.Util;

public final class Challenge
{
  private final String realm;
  private final String scheme;

  public Challenge(String paramString1, String paramString2)
  {
    this.scheme = paramString1;
    this.realm = paramString2;
  }

  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof Challenge)) && (Util.equal(this.scheme, ((Challenge)paramObject).scheme)) && (Util.equal(this.realm, ((Challenge)paramObject).realm));
  }

  public int hashCode()
  {
    if (this.realm != null);
    for (int i = this.realm.hashCode(); ; i = 0)
    {
      int j = 31 * (i + 899);
      String str = this.scheme;
      int k = 0;
      if (str != null)
        k = this.scheme.hashCode();
      return j + k;
    }
  }

  public String realm()
  {
    return this.realm;
  }

  public String scheme()
  {
    return this.scheme;
  }

  public String toString()
  {
    return this.scheme + " realm=\"" + this.realm + "\"";
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     okhttp3.Challenge
 * JD-Core Version:    0.6.0
 */
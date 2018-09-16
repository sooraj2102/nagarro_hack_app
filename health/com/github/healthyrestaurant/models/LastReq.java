package dubeyanurag.com.github.healthyrestaurant.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastReq
{

  @Expose
  @SerializedName("bmr")
  public String bmr;

  @Expose
  @SerializedName("query")
  public String query;

  public LastReq(String paramString1, String paramString2)
  {
    this.query = paramString1;
    this.bmr = paramString2;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.models.LastReq
 * JD-Core Version:    0.6.0
 */
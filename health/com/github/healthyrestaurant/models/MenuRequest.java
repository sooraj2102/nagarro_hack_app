package dubeyanurag.com.github.healthyrestaurant.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MenuRequest
{

  @Expose
  @SerializedName("cnt")
  public String cnt;

  @Expose
  @SerializedName("heart")
  public Integer heart;

  @Expose
  @SerializedName("Lat")
  public String lat;

  @Expose
  @SerializedName("Lon")
  public String lon;

  public MenuRequest(String paramString1, String paramString2, String paramString3, Integer paramInteger)
  {
    this.lat = paramString1;
    this.lon = paramString2;
    this.cnt = paramString3;
    this.heart = paramInteger;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.models.MenuRequest
 * JD-Core Version:    0.6.0
 */
package dubeyanurag.com.github.healthyrestaurant.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantReq
{

  @Expose
  @SerializedName("Rid")
  public String Rid;

  public RestaurantReq(String paramString)
  {
    this.Rid = paramString;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.models.RestaurantReq
 * JD-Core Version:    0.6.0
 */
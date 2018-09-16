package dubeyanurag.com.github.healthyrestaurant.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastResponse
{

  @Expose
  @SerializedName("error")
  public Boolean error;

  @Expose
  @SerializedName("message")
  public String message;
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.models.LastResponse
 * JD-Core Version:    0.6.0
 */
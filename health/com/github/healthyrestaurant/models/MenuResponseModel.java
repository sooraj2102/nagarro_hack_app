package dubeyanurag.com.github.healthyrestaurant.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MenuResponseModel
{

  @Expose
  @SerializedName("dish_details")
  private DishDetails dishDetails;

  @Expose
  @SerializedName("name")
  private String name;

  @Expose
  @SerializedName("Zomato_id")
  private Integer zomatoId;

  public Float getCalories()
  {
    return Float.valueOf(Float.parseFloat(this.dishDetails.getCalories()));
  }

  public Float getCarbohydrate()
  {
    return Float.valueOf(Float.parseFloat(this.dishDetails.getCarbohydrate()));
  }

  public Float getCholesterol()
  {
    return Float.valueOf(Float.parseFloat(this.dishDetails.getCholesterol()));
  }

  public String getDish()
  {
    return this.dishDetails.getDish();
  }

  public DishDetails getDishDetails()
  {
    return this.dishDetails;
  }

  public String getDishId()
  {
    return this.dishDetails.getDishId();
  }

  public String getFat()
  {
    return this.dishDetails.getFat();
  }

  public Float getFiber()
  {
    return Float.valueOf(Float.parseFloat(this.dishDetails.getFiber()));
  }

  public String getName()
  {
    return this.name;
  }

  public String getPotassium()
  {
    return this.dishDetails.getPotassium();
  }

  public Float getProtein()
  {
    return Float.valueOf(Float.parseFloat(this.dishDetails.getProtein()));
  }

  public Float getSaturatedFat()
  {
    return Float.valueOf(Float.parseFloat(this.dishDetails.getSaturatedFat()));
  }

  public String getSodium()
  {
    return this.dishDetails.getSodium();
  }

  public String getState()
  {
    return this.dishDetails.getState();
  }

  public Float getSugar()
  {
    return Float.valueOf(Float.parseFloat(this.dishDetails.getSugar()));
  }

  public String getThumb()
  {
    return this.dishDetails.getThumb();
  }

  public String getWeight()
  {
    return this.dishDetails.getWeight();
  }

  public Integer getZomatoId()
  {
    return this.zomatoId;
  }

  public void setDishDetails(DishDetails paramDishDetails)
  {
    this.dishDetails = paramDishDetails;
  }

  public void setZomatoId(Integer paramInteger)
  {
    this.zomatoId = paramInteger;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.models.MenuResponseModel
 * JD-Core Version:    0.6.0
 */
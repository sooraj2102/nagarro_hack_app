package dubeyanurag.com.github.healthyrestaurant.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantDetailResponse
{

  @Expose
  @SerializedName("address")
  public String address;

  @Expose
  @SerializedName("city")
  public String city;

  @Expose
  @SerializedName("cost")
  public String cost;

  @Expose
  @SerializedName("cuisines")
  public String cuisines;

  @Expose
  @SerializedName("delivery")
  public String delivery;

  @Expose
  @SerializedName("latitude")
  public String latitude;

  @Expose
  @SerializedName("locality")
  public String locality;

  @Expose
  @SerializedName("longitude")
  public String longitude;

  @Expose
  @SerializedName("menu")
  public String menu;

  @Expose
  @SerializedName("name")
  public String name;

  @Expose
  @SerializedName("offers")
  public String offers;

  @Expose
  @SerializedName("photo")
  public String photo;

  @Expose
  @SerializedName("photos_url")
  public String photosUrl;

  @Expose
  @SerializedName("rating")
  public String rating;

  @Expose
  @SerializedName("rating_color")
  public String ratingColor;

  @Expose
  @SerializedName("rating_text")
  public String ratingText;

  @Expose
  @SerializedName("table_booking")
  public String tableBooking;

  @Expose
  @SerializedName("url")
  public String url;

  @Expose
  @SerializedName("votes")
  public String votes;

  @Expose
  @SerializedName("zomato_id")
  public Integer zomatoId;

  public String getAddress()
  {
    return this.address;
  }

  public String getCity()
  {
    return this.city;
  }

  public String getCost()
  {
    return this.cost;
  }

  public String getCuisines()
  {
    return this.cuisines;
  }

  public String getDelivery()
  {
    return this.delivery;
  }

  public String getLatitude()
  {
    return this.latitude;
  }

  public String getLocality()
  {
    return this.locality;
  }

  public String getLongitude()
  {
    return this.longitude;
  }

  public String getMenu()
  {
    return this.menu;
  }

  public String getName()
  {
    return this.name;
  }

  public String getOffers()
  {
    return this.offers;
  }

  public String getPhoto()
  {
    return this.photo;
  }

  public String getPhotosUrl()
  {
    return this.photosUrl;
  }

  public String getRating()
  {
    return this.rating;
  }

  public String getRatingColor()
  {
    return this.ratingColor;
  }

  public String getRatingText()
  {
    return this.ratingText;
  }

  public String getTableBooking()
  {
    return this.tableBooking;
  }

  public String getUrl()
  {
    return this.url;
  }

  public String getVotes()
  {
    return this.votes;
  }

  public Integer getZomatoId()
  {
    return this.zomatoId;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.models.RestaurantDetailResponse
 * JD-Core Version:    0.6.0
 */
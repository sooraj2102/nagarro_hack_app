package dubeyanurag.com.github.healthyrestaurant.interfaces;

import dubeyanurag.com.github.healthyrestaurant.models.LastReq;
import dubeyanurag.com.github.healthyrestaurant.models.LastResponse;
import dubeyanurag.com.github.healthyrestaurant.models.MenuRequest;
import dubeyanurag.com.github.healthyrestaurant.models.MenuResponseModel;
import dubeyanurag.com.github.healthyrestaurant.models.RestaurantDetailResponse;
import dubeyanurag.com.github.healthyrestaurant.models.RestaurantReq;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public abstract interface APIEndPoints
{
  @POST("last_api/")
  public abstract Call<List<LastResponse>> requestDishDetails(@Body LastReq paramLastReq);

  @POST("rest_detail/")
  public abstract Call<List<RestaurantDetailResponse>> requestRestaurantDetails(@Body RestaurantReq paramRestaurantReq);

  @POST("rest/")
  public abstract Call<List<MenuResponseModel>> requestRestaurants(@Body MenuRequest paramMenuRequest);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.interfaces.APIEndPoints
 * JD-Core Version:    0.6.0
 */
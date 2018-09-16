package dubeyanurag.com.github.healthyrestaurant.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsIntent.Builder;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import dubeyanurag.com.github.healthyrestaurant.interfaces.APIEndPoints;
import dubeyanurag.com.github.healthyrestaurant.models.RestaurantDetailResponse;
import dubeyanurag.com.github.healthyrestaurant.models.RestaurantReq;
import dubeyanurag.com.github.healthyrestaurant.networking.ServiceGenerator;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailActivity extends AppCompatActivity
{
  CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

  @BindView(2131296494)
  CollapsingToolbarLayout collapsingToolbarLayout;

  @BindView(2131296314)
  TextView count;
  CustomTabsIntent customTabsIntent = this.builder.build();

  @BindView(2131296363)
  ImageView img1;

  @BindView(2131296364)
  ImageView img2;

  @BindView(2131296402)
  TextView morePhotos;

  @BindView(2131296425)
  RatingBar ratingBar;

  @BindView(2131296426)
  TextView ratingText;
  RestaurantDetailResponse responseBody;

  @BindView(2131296456)
  TextView seeMenu;

  @BindView(2131296480)
  TextView text1;

  @BindView(2131296481)
  ShimmerFrameLayout text1ViewContainer;

  @BindView(2131296482)
  TextView text2;

  @BindView(2131296483)
  ShimmerFrameLayout text2ViewContainer;

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2131427357);
    Toolbar localToolbar = (Toolbar)findViewById(2131296493);
    ButterKnife.bind(this);
    this.text1ViewContainer.startShimmerAnimation();
    this.text2ViewContainer.startShimmerAnimation();
    setSupportActionBar(localToolbar);
    ((APIEndPoints)ServiceGenerator.createService(APIEndPoints.class)).requestRestaurantDetails(new RestaurantReq(getIntent().getStringExtra("rid"))).enqueue(new Callback(localToolbar)
    {
      public void onFailure(Call<List<RestaurantDetailResponse>> paramCall, Throwable paramThrowable)
      {
        paramThrowable.printStackTrace();
        Toast.makeText(RestaurantDetailActivity.this, "Please check connectivity", 1).show();
      }

      public void onResponse(Call<List<RestaurantDetailResponse>> paramCall, Response<List<RestaurantDetailResponse>> paramResponse)
      {
        RestaurantDetailActivity.this.responseBody = ((RestaurantDetailResponse)((List)paramResponse.body()).get(0));
        if (paramResponse.code() == 200)
        {
          this.val$toolbar.setTitle(RestaurantDetailActivity.this.responseBody.getName());
          RestaurantDetailActivity.this.collapsingToolbarLayout.setTitle(RestaurantDetailActivity.this.responseBody.getName());
          RestaurantDetailActivity.this.setTitle(RestaurantDetailActivity.this.responseBody.getName());
          if (!RestaurantDetailActivity.this.responseBody.getPhoto().equals(""))
          {
            Picasso.with(RestaurantDetailActivity.this).load(RestaurantDetailActivity.this.responseBody.getPhoto()).into(RestaurantDetailActivity.this.img1);
            Picasso.with(RestaurantDetailActivity.this).load(RestaurantDetailActivity.this.responseBody.getPhoto()).into(RestaurantDetailActivity.this.img2);
          }
          RestaurantDetailActivity.this.ratingBar.getProgressDrawable().setColorFilter(Color.parseColor("#" + RestaurantDetailActivity.this.responseBody.getRatingColor()), PorterDuff.Mode.SRC_ATOP);
          RestaurantDetailActivity.this.ratingBar.setRating(Float.parseFloat(RestaurantDetailActivity.this.responseBody.getRating()));
          RestaurantDetailActivity.this.ratingText.setText(RestaurantDetailActivity.this.responseBody.getRating());
          RestaurantDetailActivity.this.count.setText(RestaurantDetailActivity.this.responseBody.getVotes());
          RestaurantDetailActivity.this.text1.setText(Html.fromHtml("<h3>Cuisines :</h3> " + RestaurantDetailActivity.this.responseBody.cuisines + "\n<h3>Cost for 2 : </h3> ₹ " + RestaurantDetailActivity.this.responseBody.cost));
          TextView localTextView = RestaurantDetailActivity.this.text2;
          StringBuilder localStringBuilder1 = new StringBuilder().append("<h3>Delivery available :</h3> ");
          String str1;
          StringBuilder localStringBuilder2;
          if (RestaurantDetailActivity.this.responseBody.getDelivery().equals("0"))
          {
            str1 = "No";
            localStringBuilder2 = localStringBuilder1.append(str1).append("\n<h3>Advanced table booking : </h3>");
            if (!RestaurantDetailActivity.this.responseBody.getTableBooking().equals("0"))
              break label477;
          }
          label477: for (String str2 = "No"; ; str2 = "Yes")
          {
            localTextView.setText(Html.fromHtml(str2 + "<h3>Address :</h3>" + RestaurantDetailActivity.this.responseBody.getAddress()));
            RestaurantDetailActivity.this.text1ViewContainer.stopShimmerAnimation();
            RestaurantDetailActivity.this.text2ViewContainer.stopShimmerAnimation();
            return;
            str1 = "Yes";
            break;
          }
        }
        Toast.makeText(RestaurantDetailActivity.this, "Please check connectivity " + paramResponse.raw(), 1).show();
      }
    });
  }

  @OnClick({2131296329})
  public void onDirectionsClick()
  {
    try
    {
      startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?daddr=" + this.responseBody.getLatitude() + "," + this.responseBody.getLongitude())));
      return;
    }
    catch (Exception localException)
    {
    }
  }

  @OnClick({2131296402})
  public void onPhotosClick()
  {
    try
    {
      this.customTabsIntent.launchUrl(this, Uri.parse(this.responseBody.getPhotosUrl()));
      return;
    }
    catch (Exception localException)
    {
    }
  }

  @OnClick({2131296456})
  public void onSeeMenuClick()
  {
    try
    {
      this.customTabsIntent.launchUrl(this, Uri.parse(this.responseBody.getMenu()));
      return;
    }
    catch (Exception localException)
    {
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.activities.RestaurantDetailActivity
 * JD-Core Version:    0.6.0
 */
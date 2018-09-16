package dubeyanurag.com.github.healthyrestaurant.activities;

import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dubeyanurag.com.github.healthyrestaurant.interfaces.APIEndPoints;
import dubeyanurag.com.github.healthyrestaurant.models.LastReq;
import dubeyanurag.com.github.healthyrestaurant.models.LastResponse;
import dubeyanurag.com.github.healthyrestaurant.networking.ServiceGenerator;
import dubeyanurag.com.github.healthyrestaurant.utils.DbHandler;
import java.util.Iterator;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NlpActivity extends AppCompatActivity
{

  @BindView(2131296353)
  Button go;

  @BindView(2131296419)
  LinearLayout progressLayout;

  @BindView(2131296422)
  EditText q;

  @BindView(2131296428)
  TextView res;

  @OnClick({2131296353})
  public void go()
  {
    Call localCall = ((APIEndPoints)ServiceGenerator.createService(APIEndPoints.class)).requestDishDetails(new LastReq(this.q.getText().toString(), DbHandler.getString(this, "caloriesNeeded", "1905")));
    this.progressLayout.setVisibility(0);
    this.q.setVisibility(8);
    this.go.setVisibility(8);
    localCall.enqueue(new Callback()
    {
      public void onFailure(Call<List<LastResponse>> paramCall, Throwable paramThrowable)
      {
        NlpActivity.this.q.setVisibility(0);
        NlpActivity.this.go.setVisibility(0);
        NlpActivity.this.progressLayout.setVisibility(8);
        Toast.makeText(NlpActivity.this, "Please check connectivity", 1).show();
      }

      @RequiresApi(api=23)
      public void onResponse(Call<List<LastResponse>> paramCall, Response<List<LastResponse>> paramResponse)
      {
        List localList = (List)paramResponse.body();
        if (paramResponse.code() == 200)
        {
          NlpActivity.this.progressLayout.setVisibility(8);
          NlpActivity.this.res.setVisibility(0);
          String str = "";
          Iterator localIterator = localList.iterator();
          while (localIterator.hasNext())
          {
            LastResponse localLastResponse = (LastResponse)localIterator.next();
            str = str + localLastResponse.message + "\n";
          }
          NlpActivity.this.res.setText(str);
          if (((LastResponse)localList.get(0)).error.booleanValue())
          {
            NlpActivity.this.res.setTextColor(NlpActivity.this.getColor(2131099748));
            return;
          }
          NlpActivity.this.res.setTextColor(NlpActivity.this.getColor(2131099715));
          return;
        }
        NlpActivity.this.q.setVisibility(0);
        NlpActivity.this.go.setVisibility(0);
        NlpActivity.this.progressLayout.setVisibility(8);
        Toast.makeText(NlpActivity.this, "Please check connectivity", 1).show();
      }
    });
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2131427356);
    ButterKnife.bind(this);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.activities.NlpActivity
 * JD-Core Version:    0.6.0
 */
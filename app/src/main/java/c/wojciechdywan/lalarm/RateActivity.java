package c.wojciechdywan.lalarm;

import android.content.Intent;
import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

public class RateActivity extends AppCompatActivity {
    Button btnSave;
    RatingBar ratingBar;
    Database myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        btnSave = (Button) findViewById(R.id.btnSave);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        myDb = new Database(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               int rate = (int)ratingBar.getRating();
               if(rate<1){
                   rate = 1;
               }
                if(myDb.updateRate(rate)){
                    Toast.makeText(RateActivity.this, "Dodana ocena", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RateActivity.this, "Błąd", Toast.LENGTH_SHORT).show();
                }

                Intent changeActivity = new Intent(RateActivity.this,MainActivity.class);
                startActivity(changeActivity);
            }
        });
     }
}

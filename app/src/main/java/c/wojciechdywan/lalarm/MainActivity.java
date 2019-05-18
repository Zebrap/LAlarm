package c.wojciechdywan.lalarm;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view){
        Intent intent = null;
        switch (view.getId()){
            case R.id.btnClock:
                intent = new Intent(MainActivity.this,ClockActivity.class);
                startActivity(intent);
                break;
            case R.id.btnStats:
                intent = new Intent(MainActivity.this,StatsActivity.class);
                startActivity(intent);
                break;
        }
    }



}

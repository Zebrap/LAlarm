package c.wojciechdywan.lalarm;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StatsActivity extends AppCompatActivity {
    Database myDb;
    EditText etId;
    Button btnAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myDb = new Database(this);

        etId = (EditText) findViewById(R.id.etId);

    }

    public void click(View view){
        switch (view.getId()){
            case R.id.btnStats:
                viewAll();
                break;
            case R.id.btnDelate:
                if(!etId.getText().toString().isEmpty()){
                    int i = myDb.delateData(etId.getText().toString());
                    if(i==1){
                        Toast.makeText(StatsActivity.this, "Dana została usunięta", Toast.LENGTH_SHORT).show();
                    }else{

                        Toast.makeText(StatsActivity.this, "Błąd", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(StatsActivity.this, "Wpisz ID", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public void viewAll(){
        Cursor res = myDb.getAllData();
        if(res.getCount() == 0){
            // show masage no data
            showMassage("Error","Nothing found");
        }else{
            int i=0;
            StringBuffer buffer = new StringBuffer();
            while(res.moveToNext()){
                long time = Long.parseLong(res.getString(1))/60000;
                buffer.append(i+".\n");
                buffer.append("ID : "+ res.getString(0)+"\n");
                buffer.append("Time : "+ res.getString(1)+" = "+time+"min\n");
                buffer.append("Vibre : "+ res.getString(2)+"\n");
                buffer.append("Volume : "+ res.getString(3)+"\n");
                buffer.append("Rate : "+ res.getString(4)+"\n");
                buffer.append("Song : "+ res.getString(5)+"\n\n");
                i++;
            }
            showMassage("Data",buffer.toString());
            // show all data
        }
    }

    public void showMassage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true).setTitle(title).setMessage(Message).show();
    }


}

package com.villager.mezi.dama;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    Arbiter arbiter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        Point dimension = new Point();
        getWindowManager().getDefaultDisplay().getSize(dimension);
        int width = dimension.x;
        int height = dimension.y;
        arbiter = new Arbiter();
        GuiDisplayer displayer = new GuiDisplayer(this, arbiter.getBoard(), arbiter, width, height);
        container.addView(displayer);
        container.setVerticalGravity(Gravity.CENTER_VERTICAL);

        arbiter.startGame();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem egregnaItem = menu.findItem(R.id.egregna);
        MenuItem tankegnaItem = menu.findItem(R.id.tankegna);

        if (Player.isEgregna()) {
            egregnaItem.setChecked(true);
        } else {
            tankegnaItem.setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.restart) {
            arbiter.restartGame();
            return true;
        } else if (id ==R.id.egregna){
            if (item.isChecked()) {
                Player.setIsEgregna(false);
                item.setChecked(false);
            } else {
                Player.setIsEgregna(true);
                item.setChecked(true);
            }
            return true;
        } else if (id == R.id.tankegna){
            if (item.isChecked()) {
                Player.setIsEgregna(true);
                item.setChecked(false);
            } else {
                Player.setIsEgregna(false);
                item.setChecked(true);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

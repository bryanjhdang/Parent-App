package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlip;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlipManager;

public class CoinFlipActivity extends AppCompatActivity {
    private CoinFlipManager coinFlipManager = CoinFlipManager.getInstance();
    private ChildManager childManager = ChildManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add dummy coin flips
        Child Abel = new Child("Abel");
        Child Betty = new Child("Betty");
        CoinFlip cf1 = new CoinFlip(Abel, true);
        CoinFlip cf2 = new CoinFlip(Abel, false);
        CoinFlip cf3 = new CoinFlip(Betty, true);
        CoinFlip cf4 = new CoinFlip(Betty, false);
        CoinFlip cf5 = new CoinFlip();

        coinFlipManager.addCoinFlip(cf1);
        coinFlipManager.addCoinFlip(cf2);
        coinFlipManager.addCoinFlip(cf3);
        coinFlipManager.addCoinFlip(cf4);
        coinFlipManager.addCoinFlip(cf5);

        childManager.addChild("Abel");
        childManager.addChild("Betty");
        childManager.addChild("Cain");

        setContentView(R.layout.coinflip_activity);
        showCoinFlipList();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, CoinFlipActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCoinFlipList();
    }

    void showCoinFlipList() {



        //TextView dummy = findViewById(R.id.coinFlipTitle);

        ListView coinFlipView = findViewById(R.id.coinFlipList);
        ArrayAdapter<CoinFlip> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.coinflip_list,
                coinFlipManager.getCoinFlipList());
        coinFlipView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_coin_flip, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.addCoinFlip) {
            Intent addGameIntent = AddCoinFlipActivity.makeIntent(this);
            startActivity(addGameIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlip;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlipManager;

public class CoinFlipActivity extends AppCompatActivity {
    CoinFlipManager coinFlipManager = CoinFlipManager.getInstance();
    ChildManager childManager = ChildManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coinflip_activity);

        showCoinFlipList();
    }

    void showCoinFlipList() {

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

        //TextView dummy = findViewById(R.id.coinFlipTitle);

        ListView coinFlipView = findViewById(R.id.coinFlipList);
        ArrayAdapter<CoinFlip> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.coinflip_list,
                coinFlipManager.getCoinFlipList());
        coinFlipView.setAdapter(arrayAdapter);
    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, CoinFlipActivity.class);
    }
}
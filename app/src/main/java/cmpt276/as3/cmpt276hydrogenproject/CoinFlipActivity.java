package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlip;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlipManager;

public class CoinFlipActivity extends AppCompatActivity {
    private CoinFlipManager coinFlipManager = CoinFlipManager.getInstance();
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coinflip_activity);
        setActionBar();
        setDeleteButton();
        sp = getSharedPreferences("Hydrogen", Context.MODE_PRIVATE);
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

    private void setActionBar() {
        getSupportActionBar().setTitle("Coin Flip");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
    }

    private void setDeleteButton() {
        FloatingActionButton deleteBtn = findViewById(R.id.coinflipDeleteBtn);
        deleteBtn.setOnClickListener(view -> {

            if (coinFlipManager.getCoinFlipListSize() == 0) {
                String msg = "No history to delete!";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                        .show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(CoinFlipActivity.this);
                builder.setTitle("Delete history? It cannot be restored!");

                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    coinFlipManager.clearCoinFlipList();
                    showCoinFlipList();
                });

                builder.setNegativeButton("Cancel", null);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    void showCoinFlipList() {
        ListView coinFlipView = findViewById(R.id.coinFlipList);
        ArrayAdapter<CoinFlip> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.coinflip_list,
                coinFlipManager.getCoinFlipList());
        coinFlipView.setAdapter(arrayAdapter);

        TextView emptyMessage = findViewById(R.id.emptyFlipListMessage);
        coinFlipView.setEmptyView(emptyMessage);
        saveCoinFlips();
        arrayAdapter.notifyDataSetChanged();
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

    void saveCoinFlips() {
        SharedPreferences.Editor editor = sp.edit();
        Gson myGson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                new TypeAdapter<LocalDateTime>() {
                    @Override
                    public void write(JsonWriter jsonWriter,
                                      LocalDateTime localDateTime) throws IOException {
                        jsonWriter.value(localDateTime.toString());
                    }
                    @Override
                    public LocalDateTime read(JsonReader jsonReader) throws IOException {
                        return LocalDateTime.parse(jsonReader.nextString());
                    }
                }).create();
        String jsonString = myGson.toJson(coinFlipManager.getCoinFlipList());
        Log.d("SAVE FLIPS", jsonString);
        editor.putString("coinFlipList", jsonString);
        editor.commit();
    }
}
package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
    private final CoinFlipManager coinFlipManager = CoinFlipManager.getInstance();
    private final ChildManager childManager = ChildManager.getInstance();
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
        Objects.requireNonNull(getSupportActionBar()).setTitle("Coin Flip");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
    }

    private void setDeleteButton() {
        FloatingActionButton deleteBtn = findViewById(R.id.coinFlipDeleteBtn);
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
        ArrayAdapter<CoinFlip> arrayAdapter = new CoinFlipListAdapter();
        coinFlipView.setAdapter(arrayAdapter);

        TextView emptyMessage = findViewById(R.id.emptyFlipListMessage);
        coinFlipView.setEmptyView(emptyMessage);
        saveCoinFlips();
        arrayAdapter.notifyDataSetChanged();
    }

    private class CoinFlipListAdapter extends ArrayAdapter<CoinFlip> {
        public CoinFlipListAdapter() {
            super(CoinFlipActivity.this, R.layout.coinflip_list, coinFlipManager.getCoinFlipList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.coinflip_list, parent, false);
            }

            CoinFlip coinFlip = coinFlipManager.getCoinFlipAt(position);

            // Get name from child list and set TextView to that name
            String result = coinFlip.toString();
            TextView coinFlipTxt = view.findViewById(R.id.coinFlipTxt);
            coinFlipTxt.setText(result);

            // Set green (win), red (lost), or white (no child) icon next to corresponding result
            ImageView resultIconImg = view.findViewById(R.id.resultIconImg);
            Child choosingChild = coinFlip.getChoosingChild();
            Bitmap childProfilePic = null;
            if(choosingChild != null) {
                childProfilePic = childManager.decodeToBase64(choosingChild.getProfilePicture());
            }
            if (coinFlip.getWinStatus()) {
                resultIconImg.setImageBitmap(childProfilePic);
                resultIconImg.setBackgroundColor(Color.GREEN);
            } else {
                resultIconImg.setImageBitmap(childProfilePic);
                resultIconImg.setBackgroundColor(Color.RED);
            }
            if (coinFlip.getChoosingChild() == null) {
                resultIconImg.setImageResource(R.drawable.ic_baseline_person_24);
                resultIconImg.setBackgroundColor(Color.GRAY);
            }
            return view;
        }
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

    private void saveCoinFlips() {
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
        editor.putString("coinFlipList", jsonString);
        editor.apply();
    }

    private void saveChildQueue() {

    }
}
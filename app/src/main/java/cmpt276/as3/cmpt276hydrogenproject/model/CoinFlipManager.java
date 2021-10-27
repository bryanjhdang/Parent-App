package cmpt276.as3.cmpt276hydrogenproject.model;

import java.util.ArrayList;

public class CoinFlipManager {
    private final ArrayList<CoinFlip> COIN_FLIP_LIST = new ArrayList<>();
    private static CoinFlipManager instance;

    //Make constructor private so it cannor be instantiated elsewhere
    private CoinFlipManager() {
    }

    public static CoinFlipManager getInstance() {
        if(instance == null) {
            instance = new CoinFlipManager();
        }
        return instance;
    }

    public ArrayList<CoinFlip> getCoinFlipList() {
        return COIN_FLIP_LIST;
    }

    public void addCoinFlip(boolean result) {
        CoinFlip coinFlip = new CoinFlip(result);
        COIN_FLIP_LIST.add(coinFlip);
    }

    public void removeCoinFlip(int index) {
        COIN_FLIP_LIST.remove(index);
    }
}

package cmpt276.as3.cmpt276hydrogenproject.model;

import java.util.ArrayList;

public class CoinFlipManager {
    private final ArrayList<CoinFlip> COIN_FLIP_LIST = new ArrayList<>();
    private static CoinFlipManager instance;
    private Child previousPick;

    //Make constructor private so it cannot be instantiated elsewhere
    private CoinFlipManager() {
    }

    public static CoinFlipManager getInstance() {
        if (instance == null) {
            instance = new CoinFlipManager();
        }
        return instance;
    }

    public ArrayList<CoinFlip> getCoinFlipList() {
        return COIN_FLIP_LIST;
    }

    public void addCoinFlip(CoinFlip coinFlip) {
        COIN_FLIP_LIST.add(coinFlip);
        previousPick = coinFlip.getChoosingChild();
    }

    public CoinFlip getCoinFlipAt(int index) {
        return COIN_FLIP_LIST.get(index);
    }

    public void removeCoinFlipAt(int index) {
        COIN_FLIP_LIST.remove(index);
    }

    public Child getPreviousPick() {
        return previousPick;
    }

    public void clearCoinFlipList() {
        COIN_FLIP_LIST.clear();
    }
}

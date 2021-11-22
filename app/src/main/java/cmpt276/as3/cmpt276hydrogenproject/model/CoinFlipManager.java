package cmpt276.as3.cmpt276hydrogenproject.model;

import java.util.ArrayList;

public class CoinFlipManager {
    private ArrayList<CoinFlip> COIN_FLIP_LIST = new ArrayList<>();
    private static CoinFlipManager instance;
    private Child previousPick;

    public static CoinFlipManager getInstance() {
        if (instance == null) {
            instance = new CoinFlipManager();
        }
        return instance;
    }

    public ArrayList<CoinFlip> getCoinFlipList() {
        return COIN_FLIP_LIST;
    }

    public void setCoinFlipList(ArrayList<CoinFlip> coinFlipList) {
        this.COIN_FLIP_LIST = coinFlipList;
    }

    public void addCoinFlip(CoinFlip coinFlip) {
        COIN_FLIP_LIST.add(coinFlip);
        if (coinFlip.getChoosingChild() != null) {
            previousPick = coinFlip.getChoosingChild();
        }
    }

    public void updateCoinFlipChild(String previous, String current) {
        for (CoinFlip cf : COIN_FLIP_LIST) {
            if (cf.getChoosingChild().getName().equals(previous)) {
                cf.getChoosingChild().setName(current);
            }
        }
    }

    public void updateChildNames(Child editedChild, String newName) {
        int childID = editedChild.getChildID();
        for (CoinFlip cf : COIN_FLIP_LIST) {
            if (cf.getChoosingChild().getChildID() == childID) {
                cf.getChoosingChild().setName(newName);
            }
        }
    }

    public int getCoinFlipListSize() {
        return COIN_FLIP_LIST.size();
    }

    public CoinFlip getCoinFlipAt(int index) {
        return COIN_FLIP_LIST.get(index);
    }

    public Child getPreviousPick() {
        return previousPick;
    }

    public void clearCoinFlipList() {
        COIN_FLIP_LIST.clear();
    }
}

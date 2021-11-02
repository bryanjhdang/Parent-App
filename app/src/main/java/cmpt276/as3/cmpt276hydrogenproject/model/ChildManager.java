package cmpt276.as3.cmpt276hydrogenproject.model;

import java.util.ArrayList;

public class ChildManager {
    private final ArrayList<Child> CHILD_LIST = new ArrayList<>();
    private static ChildManager instance;

    /**
     * Make constructor private to prevent anyone from instantiating the class.
     */
    private ChildManager() {
    }

    /**
     * Method to retrieve the class without accessing it by the constructor
     * @return the only instance of the class
     */
    public static ChildManager getInstance() {
        if (instance == null) {
            instance = new ChildManager();
        }
        return instance;
    }

    public int getChildListSize() {
        return CHILD_LIST.size();
    }

    public Child getChild(int idx) {
        return CHILD_LIST.get(idx);
    }

    public void addChild(String name) {
        Child child = new Child(name);
        CHILD_LIST.add(child);
    }

    public void removeChild(int idx) {
        CHILD_LIST.remove(idx);
    }

    public void editChildName(int idx, String name) {
        Child child = CHILD_LIST.get(idx);
        child.setName(name);
    }
}

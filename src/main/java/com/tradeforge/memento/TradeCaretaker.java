package com.tradeforge.memento;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * DESIGN PATTERN 9: MEMENTO PATTERN (Caretaker)
 * 
 * Manages saved TradeMemento snapshots.
 * Provides undo/restore capability for the user in the TradePanel.
 */
public class TradeCaretaker {

    // Stack to support undoing to previous saved drafts
    private final Deque<TradeMemento> mementoHistory = new ArrayDeque<>();

    /**
     * Saves a new memento snapshot to history.
     */
    public void save(TradeMemento memento) {
        if (memento != null) {
            mementoHistory.push(memento);
        }
    }

    /**
     * Retrieves and pops the most recent saved snapshot (Undo).
     * Returns null if no state has been saved yet.
     */
    public TradeMemento undo() {
        if (mementoHistory.isEmpty()) {
            return null;
        }
        return mementoHistory.pop();
    }

    /**
     * Peeks at the latest saved snapshot without removing it.
     */
    public TradeMemento peek() {
        return mementoHistory.peek();
    }

    /**
     * Checks if at least one saved snapshot exists.
     */
    public boolean hasSavedState() {
        return !mementoHistory.isEmpty();
    }

    /**
     * Clears all saved snapshots.
     */
    public void clear() {
        mementoHistory.clear();
    }
}

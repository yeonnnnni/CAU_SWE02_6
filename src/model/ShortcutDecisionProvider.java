package model;

public interface ShortcutDecisionProvider {
    boolean shouldUseShortcut(String direction);
}
package controller;

import model.Node;

@FunctionalInterface
public interface ShortcutDecisionProvider {
    Node chooseShortcut(Node shortcutEntryNode);  // 지름길 진입 노드 → 반환할 노드 선택
}

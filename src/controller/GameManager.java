package controller;

import model.DiceManager;
import model.Horse;
import model.Node;
import model.YutResult;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<Horse> horses = new ArrayList<>();
    private Node startNode;

    public void startGame() {
        // 보드 초기화 (간단한 직선 예시)
        startNode = new Node();
        Node current = startNode;
        for (int i = 0; i < 19; i++) {
            Node next = new Node();
            current.addNext(next);
            current = next;
        }
        for (int i = 0; i < 4; i++) {
            horses.add(new Horse(startNode));
        }
    }

    public YutResult rollYut() {
        return DiceManager.rollYut();
    }

    public List<Horse> getMovableHorses(YutResult result) {
        List<Horse> resultList = new ArrayList<>();
        for (Horse horse : horses) {
            if (!horse.isFinished()) {
                resultList.add(horse);
            }
        }
        return resultList;
    }

    public void moveHorse(Horse horse, int steps) {
        Node current = horse.getCurrentPosition();
        for (int i = 0; i < steps && !current.getNextNodes().isEmpty(); i++) {
            current = current.getNextNodes().get(0);
        }
        horse.moveTo(current);
        if (current.getNextNodes().isEmpty()) {
            horse.setFinished(true);
        }
    }

    public void checkWin() {
        boolean allFinished = horses.stream().allMatch(Horse::isFinished);
        if (allFinished) {
            System.out.println("Game Over! All horses finished.");
        }
    }
}
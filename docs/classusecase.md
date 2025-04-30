| 유스케이스       | 대응 클래스               | 메서드 예시                         | 상태     |
|------------------|---------------------------|--------------------------------------|----------|
| 게임 시작        | GameController, MainFrame | startGame()                          | ✅ 완료  |
| 주사위 던지기    | DiceManager, DicePanel    | rollRandom()                         | ✅ 완료  |
| 말 선택          | GameController, Horse     | handleMoveSelection()                | ✅ 완료  |
| 말 이동          | Horse, Node               | moveTo(Node)                         | ⚠️ 일부 구현 필요 |
| 잡기/그룹핑      | GameManager, Horse        | isCaptured(), isGroupable()          | ⚠️ 설계 예정 |
| 승리 조건 확인   | GameManager               | isWin()                              | ✅ 완료  |
| UI 반영          | BoardPanel                | updateUI()                           | ✅ 완료  |
| 턴 전환          | GameController            | nextTurn()                           | ✅ 완료  |
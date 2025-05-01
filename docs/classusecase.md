| 유스케이스       | 대응 클래스               | 메서드 예시                         | 상태     |
|------------------|---------------------------|--------------------------------------|----------|
| 게임 시작        | GameController, MainFrame | mainFrame(),(비어있음)                | ⚠️ 일부 구현 필요  |
| 주사위 던지기    | DiceManager, DicePanel    | rollRandom(),rollManual()                | ✅ 완료  |
| 말 선택          | GameController, Horse     | (관련 매서드 없음)                     | ❌ 미구현  |
| 말 이동          | Horse, Node               | getId()                            | ❌ 미구현 |
| 잡기/그룹핑      | GameManager, Horse        | (전혀없음)                            | ⚠️ 일부 구현 필요 |
| 승리 조건 확인   | GameManager               | (전혀없음)                              | ⚠️ 일부 구현 필요  |
| UI 반영          | BoardPanel                | updatePiecePosotion()                | ✅ 완료  |
| 턴 전환          | GameController            | (매서드없음)                          | ❌ 미구현  |
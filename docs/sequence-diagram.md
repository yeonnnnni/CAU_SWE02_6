# 📑 시퀀스 다이어그램 초안

사용자
↓ 입력
MainFrame
↓ 호출
GameController
├── startGame() → 참여자/말 수 입력받고 초기화
├── handleDiceRoll() → DiceManager에 주사위 요청
↓
DiceManager
├── rollRandom() or rollManual()
└── convertToSteps() → 이동거리 큐 반환
↓
GameController
└── handleMoveSelection() → 사용자 말 선택 UI 띄움
↓
Horse
└── moveTo(Node) → 위치 갱신
↓
GameManager
├── isCaptured() → 잡기 가능 여부 판단
├── isGroupable() → 그룹핑 여부 판단
└── isWin() → 승리 조건 확인
↓
BoardPanel
└── updateUI() → 말 위치 반영
MainFrame
└── nextTurn() → 다음 플레이어로 전환


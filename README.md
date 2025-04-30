# 🧠 프로젝트 구조 개요 (OOAD + MVC 기반)
```
src/
├── model/
│   ├── Horse.java
│   ├── Node.java
│   ├── DiceManager.java
│   └── YutResult.java
├── view/
│   ├── BoardPanel.java
│   ├── DicePanel.java
│   └── MainFrame.java
├── controller/
│   ├── GameManager.java
│   └── TurnManager.java (optional)
├── App.java

```
## 📁 src/model/
- Horse: 말 객체, 위치 및 상태 저장
- Node: 판 위의 위치 노드 구조
- DiceManager: 주사위 결과 생성 및 변환
- YutResult: 윷 결과 Enum

## 📁 src/view/
- BoardPanel: 윷판 UI 구성 (GridLayout)
- DicePanel: 주사위 던지기 UI 구성

## 📁 src/controller/
- GameManager: 게임의 시작~종료 흐름 제어
- TurnManager: 플레이어 순서 및 턴 전환 관리

## 🧩 클래스 책임 분리 (OOAD 적용)
- Model: 데이터를 저장하고 계산
- View: 사용자에게 보여주는 화면 구성
- Controller: 흐름을 제어하고 Model과 View 연결

> 이 구조를 통해 각 클래스가 명확하게 분리되어 유지보수 및 확장이 쉬운 구조로 설계됨.

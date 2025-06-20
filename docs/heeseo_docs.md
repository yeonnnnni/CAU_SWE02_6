1. AllScenarioTest
    1.  testBoardTypeSelection
        - 목적 : 다양한 보드 타입(square, pentagon, hexagon)에 대해 BoardFactory가 알맞은 BoardBuilder를 생성하고, buildBoard() 호출 시 정상적으로 노드를 반환하는지 확인.
        - 내용 : BoardFactory.create("타입")을 호출해 각 타입의 빌더 생성 후, .buildBoard()로 실제 보드를 생성해 null 여부 확인.
        - 결과 : 모든 타입에 대해 buildBoard()의 반환값이 null이 아니므로 정상 작동함을 확인함.
    2. testRandomDiceRoll
        - 목적 : 랜덤 윷 던지기 결과(List<YutResult>)가 비어있지 않고, 각 결과가 유효한 범위(-1~5)에 속하는지 확인.
        - 내용 : rollRandomSequence() 호출 후 결과 리스트 길이 확인 및 각 YutResult.getSteps() 값이 범위 안에 있는지 확인.
        - 결과 : 결과 리스트는 비어 있지 않으며, 모든 값이 -1~5 사이에 포함됨을 확인함.
    3.  testManualDiceRollValidInputs
        - 목적 : 수동 입력으로 윷 결과를 주었을 때, 입력값과 YutResult.getSteps() 값이 일치하는지 확인.
        - 내용 : 1, 1~5까지 입력해 rollManual(i) 호출 후 반환값과 입력값 비교.
        - 결과 : 모든 입력값에 대해 결과의 getSteps() 값이 정확히 일치함을 확인함.
    4. testManualDiceRollInvalidInputs
        - 목적 : 잘못된 입력값이 주어졌을 때 예외가 발생하는지 확인.
        - 내용 : 6을 입력했을 때 IllegalArgumentException, 문자열을 파싱할 경우 NumberFormatException 발생 여부 테스트.
        - 결과 : 각각의 예외가 정상적으로 발생하여 입력 검증이 잘 작동함을 확인함.
    5. testGameRestart
        - 목적: GameManager.restartGame() 호출 시, 각 팀의 말 상태가 초기화되는지 확인.
        - 결과 : 재시작 후 모든 말의 상태가 초기화되어 FINISHED가 아님을 확인함.
        - 내용 : 말 하나의 상태를 FINISHED로 설정 후 등록 → restartGame() 호출 → isFinished() 결과가 false로 바뀌는지 확인.
    6. testGameEndCondition
        - 목적 : 팀의 모든 말이 FINISHED 상태일 때 Team.isWin()이 true를 반환하는지 확인.
        - 내용 : team.getHorses()에 있는 모든 말을 FINISHED로 설정한 뒤, isWin() 결과 확인.
        - 결과 : 모든 말이 완주한 경우 isWin() == true를 반환함을 확인함.

1. OptionScenarioTest
    1. electHorseCount
        - 목적 : 사용자가 보유한 말 수가 유효한 범위(2~5마리)에 속하는지 확인
        - 내용 : 3마리의 말을 생성 후, 그 크기가 조건을 만족하는지 검증
        - 결과 : 리스트 크기가 2~5 사이이므로 정상 처리됨을 확인
    2. SelectHorseToMove
        - 목적 : 이동 가능한 말 리스트에서 하나를 선택하는 UI 동작이 정상 수행되는지 확인
        - 내용 : 더미 UI는 항상 첫 번째 말을 선택하게 되어 있으며, 선택된 말이 null이 아닌지 검증
        - 결과 : 리스트의 첫 번째 말이 정상적으로 반환됨
    3. SelectYutResultToApply
        - 목적 : 여러 윷 결과 중 사용자 선택에 따라 특정 윷이 선택되는지 확인
        - 내용 : UI에 세 가지 윷 결과를 전달하면, 더미 UI는 항상 첫 번째 결과를 반환함
        - 결과 : DO가 반환되어 예상대로 동작함

1. StackingAndCapturingTest
    1. StackingSameTeam
        - 목적 : 같은 팀의 두 말이 동일한 노드에 진입했을 때 업기 상태로 공존 가능한지 확인
        - 내용 : horseA1, horseA2가 같은 팀이며, 동일한 노드 A2에 진입함. 노드에 두 말 모두 존재해야 함
        - 결과 : 두 말 모두 노드 리스트에 포함되어 있고, 업기 처리가 정상적으로 되었음
    2. CapturingOtherTeam
    - 목적 : 다른 팀의 말이 있는 노드에 진입했을 때 잡기(capture) 처리가 정상적으로 이루어지는지 확인
    - 내용 : horseB1이 먼저 A2에 진입, 이후 horseA1이 들어오며 horseB1이 사라지고 자신만 남음
    - 결과 : horseB1은 null 위치(잡힘), horseA1만 노드에 남아 있음. 잡기 로직 정상 작동 확인

1. SquareScenarioTest
    1. NormalMove
        - 목적 : 일반 경로로 말이 정방향으로 정상 이동하는지 확인
        - 내용 : A2에서 시작하여 2칸 이동 시 N1 노드에 정확히 도착하는지 검증
        - 결과 : 말이 N1에 도착함. 일반 이동 경로 동작 정상 확인
    2. BackDoMove
        - 목적 : 백도(-1) 이동 시 말이 역방향으로 이동하는지 확인
        - 내용 : 1칸 전진 후 -1칸 이동하여 위치가 바뀌는지 확인
        - 결과 : 위치가 변경됨. 백도 기능 정상 작동 확인
    3. EnterShortcut
        - 목적 : 특정 지점(D2)에서 지름길 진입이 가능한지 확인
        - 내용 : A2에서 5칸 이동해 D2 도달 후, YES_SHORTCUT 선택 시 D1 진입 여부 확인
        - 결과 : D1로 진입함. 지름길 선택 로직 정상 작동 확인
    4. BestPathEntry
        - 목적 : 중심 노드(00) 진입 후 최적 경로(A0)로 진입하는지 확인
        - 내용 : 5칸 + 3칸 이동하여 중심 진입, 이후 A 방향으로 진행하는지 확인
        - 결과 : A* 노드 또는 00에 위치. 최적 경로 분기 정상 작동 확인
    5. SkipThroughShortcut
        - 목적 : 지름길을 단순 경유하여 일반 경로(N4)로 넘어가는 동작 확인
        - 내용 : N2 → D2 밟고 → N4로 이동되는지 확인
        - 결과 : N4 도달함. 경유 및 진입 건너뛰기 로직 정상 작동 확인
    6. ReachGoal
        - 목적 : 골인 노드(A2) 도달 시 정상적으로 도착 처리되는지 확인
        - 내용 : A1에서 1칸 이동하여 A2 도착 → 도착 여부 검증
        - 결과 : A2에 정확히 도달. 골인 처리 정상 작동 확인
    7. TeamVictory
        - 목적 : 팀의 모든 말이 도착했을 때 승리 조건이 충족되는지 확인
        - 내용 : 팀 내 말 2개 모두 A2 도달 처리 후 team.isWin() 호출
        - 결과 : isWin() 결과 true 반환. 팀 승리 판정 정상 작동 확인

1. PentagonScenarioTest
    1. NormalMove
        - 목적 : 일반 경로에서 직진 이동이 정상적으로 수행되는지 확인
        - 내용 : A2에서 2칸 이동하면 N1에 도착해야 함
        - 결과 : 이동 후 horse의 위치는 N1 → 정상적으로 직진 처리됨
    2. BackDoMove
        - 목적 : 백도(-1칸 후진) 이동이 정상적으로 작동하는지 확인
        - 내용 : 1칸 전진 후 -1칸 후진 → 위치 변화가 발생해야 함
        - 결과 : 전후 위치가 다름 → 백도 정상 처리됨
    3. EnterShortcutFromD2
        - 목적 : D2 노드에서 지름길 진입이 가능한지 확인
        - 내용 : D2에서 YES_SHORTCUT으로 1칸 이동 → D1 지름길 진입
        - 결과 : D1에 도달 → 지름길 진입 성공
    4. EnterShortcutFromC2
        - 목적 : C2 노드에서 지름길 진입이 가능한지 확인
        - 내용 : C2에서 YES_SHORTCUT으로 1칸 이동 → C1 지름길 진입
        - 결과 : C1에 도달 → 지름길 진입 성공
    5. SkipViaShortcut
        - 목적 : 지름길을 이용해 중간 노드를 건너뛰는 이동이 가능한지 확인
        - 내용 : N3에서 2칸 이동 → D2를 밟고 N4로 직행
        - 결과 : N4에 도달 → 지름길 통한 스킵 정상 작동
    6. OptimalPathToA0
        - 목적 : 중심 노드(00)에서 최적 경로 A0로 진입하는지 확인
        - 내용 : C0 → 00 → A0로 이동 유도
        - 결과 : A로 시작하는 노드(A0) 도달 확인 → 최적 경로 진입 성공
    7. AlternateCenterPathToB0
        - 목적 : 중심 노드(00)에서 다른 경로(B0)로의 분기 여부 확인
        - 내용 : D0에서 2칸 이동 → 중심 노드 통해 B0로 진입
        - 결과 : B0에 도달 → 분기 경로 정상 작동
    8. ReachGoalA2
        - 목적 : A2 노드에 도달 시 도착(완주) 처리가 되는지 확인
        - 내용 : A1 → A2로 이동하여 isFinished 상태 확인
        - 결과 : A2에 도달하고 horse.isFinished() = true → 도착 처리 정상
    9. TeamVictory
        - 목적 : 팀 내 모든 말이 완주 시 승리 조건이 충족되는지 확인
        - 내용 : 모든 말을 A2까지 이동시킨 후 team.isWin() 확인
        - 결과 : 모든 말 도착 후 team.isWin() = true → 승리 조건 충족

1. HexagonScenarioTest
    1. NormalMove
        - 목적 : 일반 경로에서 정방향 이동이 정상적으로 이루어지는지 확인
        - 내용 : A2에서 시작하여 2칸 이동 시 N1에 도착
        - 결과 : 말의 위치가 N1로 정확히 이동됨을 확인
    2. BackDoMove
        - 목적 : 백도(-1) 이동 시 역방향 이동이 적용되는지 확인
        - 내용 : 한 칸 전진 후, -1칸 이동 → 두 위치가 서로 달라야 함
        - 결과 : 말의 위치가 역방향으로 이동하여 변경됨을 확인
    3. EnterShortcut_D2
        - 목적 : D2에서 지름길로 진입 가능한지 확인
        - 내용 : D2에 위치한 말이 1칸 이동 시 지름길 D1로 진입
        - 결과 : D1로 이동 완료, 지름길 진입 로직 정상 작동
    4. EnterShortcut_C2
        - 목적 : C2에서 지름길 진입 여부 확인
        - 내용 : C2에 위치한 말이 1칸 이동 시 지름길 C1로 이동
        - 결과 : C1로 이동 완료, 지름길 진입 로직 정상 작동
    5. SkipThroughShortcut
        - 목적 : 지름길을 통해 중간 노드를 건너뛰는 로직 확인
        - 내용 : N3 → 2칸 이동 시 D2 밟고 N4 도달
        - 결과 : N4에 정상 도달, 지름길을 통한 스킵 동작 확인
    6. OptimalToA0
        - 목적 : 중심 노드(00) 도달 후 최적 경로(A0)로 진입 확인
        - 내용 : C0 → 중심 → A0 경로 진입 유도
        - 결과 : 말의 위치가 A로 시작하는 노드(A0 등)로 정확히 진입
    7. AlternateToB0
        - 목적 : 중심 노드에서 대안 경로(B0)로 진입하는지 확인
        - 내용 : D0 → 중심 → B0 경로로 이동
        - 결과 : B0로 정확히 이동됨을 확인
    8. ReachGoal
        - 목적 : 도착 노드 A2에 도달 시 말의 상태가 FINISHED로 변경되는지 확인
        - 내용 : A1에서 1칸 이동 후 A2 도착
        - 결과 : A2에 도착, isFinished() true 반환 확인
    9. TeamVictory
        - 목적 : 팀 내 모든 말이 도착했을 때 isWin()이 true 반환되는지 확인
        - 내용 : 모든 말을 A1에 배치 후 1칸 이동 → A2 도달
        - 결과 : 모든 말이 완주 후 팀의 승리 조건 충족
2. TeamSoreFXTest
    1. InitialScoreSetting
        - 목적 : 생성자 초기화로 설정된 점수 상태가 올바르게 저장되는지 확인
        - 내용 : "Team A", 3, 2, 1로 객체 생성 후, 각각의 waiting, moving, finished 값 확인
        - 결과 : 각 Property가 초기값(3, 2, 1)으로 정확히 설정되어 있음
    2. FinishedScoreUpdate
        - 목적 : 말 도착 수(finished) Property를 동적으로 변경할 수 있는지 검증
        - 내용 : finishedProperty().set(5) 실행 후 .get()으로 변경값 확인
        - 결과 : finished가 5로 정상 변경, setter 동작 및 상태 반영 정상

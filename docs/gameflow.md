# 🎮 게임 전체 흐름도

1. 게임 시작
   - 참여자 수 및 말 개수 설정
   - Board 초기화, 말 배치, UI 준비

2. 플레이어 턴 시작
   - DicePanel에서 주사위 굴림 (랜덤 or 지정)
   - 결과를 Queue로 저장

3. 말 선택
   - 움직일 말 선택 (UI 버튼 클릭)
   - 선택된 말에 결과 큐의 첫 값을 적용해 이동

4. 이동 처리
   - 경로 계산 후 말 이동
   - 도착 위치에 따라 다음 이동 또는 종료
   - 중심점 / 지름길 여부 판단

5. 추가 처리
   - 잡을 수 있는 말이 있다면 → 잡기 처리
   - 같은 말이 있다면 → 그룹핑 처리

6. 이동 반복 or 턴 종료
   - 결과 큐에 남은 값이 있으면 다시 3번으로
   - 없으면 턴 종료 → 다음 사람으로 넘어감

7. 승리 판정
   - 모든 말을 완주시킨 플레이어가 승리
   - 승리 메시지 출력 및 재시작/종료 선택
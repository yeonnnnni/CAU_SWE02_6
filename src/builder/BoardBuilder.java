package builder;

import model.Node;

import java.awt.Point;
import java.util.Map;

/**
 * BoardBuilder는 다양한 형태의 윷놀이 보드를 생성하기 위한 인터페이스입니다.
 * 이 인터페이스를 구현하는 클래스는 보드를 구성할 노드 목록과 시각적 위치 정보를 정의합니다.
 */
public interface BoardBuilder {
    /**
     * 보드를 구성하는 노드들을 생성하여 리스트로 반환합니다.
     * @return 보드 위에 배치될 노드 리스트
     */
    java.util.List<Node> buildBoard();

    /**
     * 각 노드의 ID와 해당 노드의 화면 좌표를 포함한 맵을 반환합니다.
     * @return 노드 ID와 Point 좌표를 매핑한 정보
     */
    Map<String, Point> getNodePositions();
}


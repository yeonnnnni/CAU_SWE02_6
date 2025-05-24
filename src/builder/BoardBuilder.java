package builder;

import model.Node;
import java.awt.Point;
import java.util.List;
import java.util.Map;

/**
 * BoardBuilder 인터페이스는 윷놀이 게임에서 다양한 형태의 보드
 * (예: 사각형, 오각형, 육각형 등)를 생성하기 위한 공통 인터페이스입니다.
 *
 * 이 인터페이스는 빌더 패턴을 기반으로, 보드의 노드를 생성하고 위치 정보를
 * 제공하는 메서드를 정의합니다. 이를 통해 각기 다른 보드 구조를 일관되게 구성할 수 있습니다.
 */
public interface BoardBuilder {

    /**
     * 보드의 노드들을 생성하고 연결하여 리스트로 반환합니다.
     * 구현체는 이 메서드에서 실제 노드를 구성하는 로직을 수행합니다.
     *
     * @return 보드에 사용되는 Node 객체들의 리스트
     */
    List<Node> buildBoard();

    /**
     * 보드 위 각 노드의 위치(Point 좌표)를 반환합니다.
     * UI에서 노드를 시각적으로 배치할 때 이 좌표를 사용합니다.
     *
     * @return 노드 ID(String)를 키로 하고 위치(Point)를 값으로 갖는 맵
     */
    Map<String, Point> getNodePositions();
}
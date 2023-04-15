package abcscp.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class Tuple3<K, V1, V2> {
    private K t1;
    private V1 t2;
    private V2 t3;
}

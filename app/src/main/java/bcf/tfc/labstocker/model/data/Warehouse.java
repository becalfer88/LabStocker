package bcf.tfc.labstocker.model.data;

import java.util.Map;

/**
 * Class Warehouse. This class is used to represent a warehouse.
 *
 * @author Beatriz Calzo
 */
public class Warehouse extends Location {

    public Warehouse() {
        super();
    }

    public Warehouse(String address, Boolean read) {
        super(address, read);
    }


    /**
     * Deserializes a warehouse from a map.
     * @param map
     * @return
     */
    public static Warehouse fromMap(Map<String, Object> map) {
        Warehouse warehouse = new Warehouse();
        Location.fromMap(warehouse, map);
        return warehouse;
    }

}

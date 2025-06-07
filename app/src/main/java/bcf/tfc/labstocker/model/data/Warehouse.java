package bcf.tfc.labstocker.model.data;

import java.util.Map;


public class Warehouse extends Location {

    public Warehouse() {
        super();
    }

    public Warehouse(String address, Boolean read) {
        super(address, read);
    }


    public static Warehouse fromMap(Map<String, Object> map) {
        Warehouse warehouse = new Warehouse();
        Location.fromMap(warehouse, map);
        return warehouse;
    }

}

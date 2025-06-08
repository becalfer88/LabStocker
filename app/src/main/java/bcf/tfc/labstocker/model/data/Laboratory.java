package bcf.tfc.labstocker.model.data;

import java.util.Map;

import bcf.tfc.labstocker.model.DataModel;

/**
 * Class Laboratory. It is a subclass of Location
 *
 * @author Beatriz Calzo
 */
public class Laboratory extends Location {


    private Warehouse warehouse;

    /**
     * Empty constructor for Firebase
     */
    public Laboratory() {
        super();
    }

    public Laboratory(String address, Warehouse warehouse, Boolean read) {
        super(address, read);
        this.warehouse = warehouse;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    /**
     * Serializes the object into a map for Firebase
     * @return
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("warehouse", warehouse.getId());
        return map;
    }

    /**
     * Deserializes the object from a map for Firebase
     * @param map
     * @return
     */
    public static Laboratory fromMap(Map<String, Object> map) {
        Laboratory lab = new Laboratory();
        Location.fromMap(lab, map);
        String warehouse = (String) map.get("warehouse");
        if (warehouse != null) {
            Warehouse wh = DataModel.getWarehouseById(warehouse);
            lab.setWarehouse(wh);
        }

        return lab;
    }

}

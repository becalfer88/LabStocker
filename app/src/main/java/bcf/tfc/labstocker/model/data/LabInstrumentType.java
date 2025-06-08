package bcf.tfc.labstocker.model.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Class LabInstrumentType. Represents a type of lab instrument
 *
 * @author Beatriz Calzo
 */
public class LabInstrumentType {
    public int id;
    public String description;

    public LabInstrumentType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Serialize the object into a map for Firebase
     *
     * @return Map representation of the object
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("description", description);
        return map;
    }

    /**
     * Deserialize the object from a map
     *
     * @param map Map representation of the object
     * @return LabInstrumentType object
     */
    public static LabInstrumentType fromMap(Map<String, Object> map) {
        return new LabInstrumentType((int) (long) map.get("id"), (String) map.get("description"));
    }
}

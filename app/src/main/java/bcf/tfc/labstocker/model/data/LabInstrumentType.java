package bcf.tfc.labstocker.model.data;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("description", description);
        return map;
    }

    public static LabInstrumentType fromMap(Map<String, Object> map) {
        return new LabInstrumentType((int) (long) map.get("id"), (String) map.get("description"));
    }
}

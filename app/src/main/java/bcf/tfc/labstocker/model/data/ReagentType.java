package bcf.tfc.labstocker.model.data;

import java.util.HashMap;
import java.util.Map;

public class ReagentType {
    private int id;
    private String description;

    public ReagentType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
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

    public static ReagentType fromMap(Map<String, Object> map) {
        return new ReagentType((int)(long) map.get("id"), (String) map.get("description"));
    }
}

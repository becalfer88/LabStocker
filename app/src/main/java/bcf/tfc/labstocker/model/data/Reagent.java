package bcf.tfc.labstocker.model.data;

import java.util.HashMap;
import java.util.Map;

import bcf.tfc.labstocker.utils.Utils;

public class Reagent {

    private String id;
    private String formula;
    private ReagentType type;
    private String status;
    private String description;
    private String concentration;

    /**
     * Empty constructor for Firebase
     */
    public Reagent() {}

    public Reagent(String id, String formula, ReagentType type, String status, String description, String concentration) {
        this.formula = formula;
        this.type = type;
        this.status = status;
        this.description = description;
        this.concentration = concentration;
        if (id == null) {
            this.id = "R"+System.currentTimeMillis();
        } else {
            this.id = id;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public ReagentType getType() {
        return type;
    }

    public void setType(ReagentType type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    @Override
    public String toString() {
        return description;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("formula", formula);
        map.put("type", type);
        map.put("status", status);
        map.put("description", description);
        map.put("concentration", concentration);
        return map;
    }

    public static Reagent fromMap(Map<String, Object> map) {
        String id = (String) map.get("id");
        String formula = (String) map.get("formula");
        ReagentType type = ReagentType.fromMap((Map<String, Object>) map.get("type"));
        String status = (String) map.get("status");
        String description = (String) map.get("description");
        String concentration = (String) map.get("concentration");
        return new Reagent(id, formula, type, status, description, concentration);
    }
}

package bcf.tfc.labstocker.model.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Class Reagent. Represents a reagent that can be used in a practice and stored in a location
 *
 * @author Beatriz Calzo
 */
public class Reagent {

    private String id;
    private String formula;
    private ReagentType type;
    private String description;
    private String concentration;

    /**
     * Empty constructor for Firebase
     */
    public Reagent() {}

    public Reagent(String id, String formula, ReagentType type, String description, String concentration) {
        this.formula = formula;
        this.type = type;
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

    /**
     * Serializes the object into a map
     * @return
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("formula", formula);
        map.put("type", type);
        map.put("description", description);
        map.put("concentration", concentration);
        return map;
    }

    /**
     * Deserializes the object from a map
     * @param map
     * @return
     */
    public static Reagent fromMap(Map<String, Object> map) {
        String id = (String) map.get("id");
        String formula = (String) map.get("formula");
        ReagentType type = ReagentType.fromMap((Map<String, Object>) map.get("type"));
        String description = (String) map.get("description");
        String concentration = (String) map.get("concentration");
        return new Reagent(id, formula, type, description, concentration);
    }
}

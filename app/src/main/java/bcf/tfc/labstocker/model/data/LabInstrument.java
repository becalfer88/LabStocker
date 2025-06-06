package bcf.tfc.labstocker.model.data;

import java.util.HashMap;
import java.util.Map;

public class LabInstrument {

    private String id;
    private String name;
    private LabInstrumentType type;
    private String material;
    private String observations;


    /**
     * Empty constructor for Firebase
     */
    public LabInstrument(){}

    public LabInstrument(String id, String name, LabInstrumentType type,String material, String observations) {
        this.name = name;
        this.type = type;
        this.material = material;
        this.observations = observations;
        if (id == null) {
            this.id = "I" + System.currentTimeMillis();
        }
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public LabInstrumentType getType() {
        return type;
    }
    public void setType(LabInstrumentType type) {
        this.type = type;
    }

    public String getMaterial() {
        return material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }
    public String getObservations() {
        return observations;
    }
    public void setObservations(String observations) {
        this.observations = observations;
    }

    @Override
    public String toString() {
        return name;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("type", type.toString());
        map.put("material", material);
        map.put("observations", observations);
        return map;
    }

    public static LabInstrument fromMap(Map<String, Object> map) {
        String id = (String) map.get("id");
        String name = (String) map.get("name");
        LabInstrumentType type = LabInstrumentType.fromMap((Map<String, Object>) map.get("type"));
        String material = (String) map.get("material");
        String observations = (String) map.get("observations");
        return new LabInstrument(id, name, type, material, observations);
    }
}

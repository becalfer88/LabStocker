package bcf.tfc.labstocker.model.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Location {

    private static int lastId = 0;

    private String id;
    private String address;
    private HashMap<LabInstrument, Quantity> labInstruments;
    private HashMap<Reagent, Quantity> reagents;

    /**
     * Empty constructor for Firebase
     */
    public Location() {
        this.labInstruments = new HashMap<>();
        this.reagents = new HashMap<>();
    }

    public Location (String address, Boolean read) {
        this.address = address;
        this.labInstruments = new HashMap<>();
        this.reagents = new HashMap<>();

        if (!read) {
            lastId++;
            this.id = "LOC" + lastId;
        }
    }

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public void addReagent(Reagent reagent, float value, String unit) {
        this.reagents.put(reagent, new Quantity(value, unit));
    }

    public Reagent getReagent(String formula) {
        for (Reagent reagent : this.reagents.keySet()) {
            if (reagent.getFormula().equals(formula)) {
                return reagent;
            }
        }
        return null;
    }

    public Quantity getReagentQuantity(Reagent reagent) {
        return this.reagents.get(reagent);
    }

    public void addLabInstrument(LabInstrument labInstrument, float value, String unit) {
        this.labInstruments.put(labInstrument, new Quantity(value, unit));
    }

    public LabInstrument getLabInstrument(String name) {
        for (LabInstrument labInstrument : this.labInstruments.keySet()) {
            if (labInstrument.getName().equals(name)) {
                return labInstrument;
            }
        }
        return null;
    }

    public Quantity getLabInstrumentQuantity(LabInstrument instrument) {
        return this.labInstruments.get(instrument);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("address", address);

        List<Map<String, Object>> reagentsList = new ArrayList<>();
        for (Map.Entry<Reagent, Quantity> entry : reagents.entrySet()) {
            Map<String, Object> rMap = entry.getKey().toMap();
            rMap.putAll(entry.getValue().toMap());
            reagentsList.add(rMap);
        }

        List<Map<String, Object>> instrumentsList = new ArrayList<>();
        for (Map.Entry<LabInstrument, Quantity> entry : labInstruments.entrySet()) {
            Map<String, Object> iMap = entry.getKey().toMap();
            iMap.putAll(entry.getValue().toMap());
            instrumentsList.add(iMap);
        }

        map.put("reagents", reagentsList);
        map.put("labInstruments", labInstruments);
        return map;
    }

    public static void fromMap(Location location, Map<String, Object> map) {
        location.id = (String) map.get("id");
        location.address = (String) map.get("address");

        location.reagents = new HashMap<>();
        List<Map<String, Object>> reagents = (List<Map<String, Object>>) map.get("reagents");
        if (reagents != null) {
            for (Map<String, Object> entry : reagents) {
                Reagent r = Reagent.fromMap(entry);
                Quantity q = Quantity.fromMap(entry);
                location.reagents.put(r, q);
            }
        }

        location.labInstruments = new HashMap<>();
        List<Map<String, Object>> instruments = (List<Map<String, Object>>) map.get("labInstruments");
        if (instruments != null) {
            for (Map<String, Object> entry : instruments) {
                LabInstrument i = LabInstrument.fromMap(entry);
                Quantity q = Quantity.fromMap(entry);
                location.labInstruments.put(i, q);
            }
        }
    }



}

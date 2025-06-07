package bcf.tfc.labstocker.model.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bcf.tfc.labstocker.model.DataModel;
import bcf.tfc.labstocker.utils.Utils;

public class Practice {
    private static int lastId = 0;

    private String id;
    private String name;
    private HashMap<LabInstrument, Quantity> labInstruments;
    private HashMap<Reagent, Quantity> reagents;

    public Practice() {
        this.labInstruments = new HashMap<>();
        this.reagents = new HashMap<>();
    }

    public Practice(String id, String name) {
        this.name = name;
        this.labInstruments = new HashMap<>();
        this.reagents = new HashMap<>();
        lastId++;
        if(id == null) {
            this.id = Utils.generateId("P", name, lastId+"");
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void addAllLabInstruments(HashMap<LabInstrument, Quantity> labInstruments) {
        this.labInstruments.putAll(labInstruments);
    }

    public void addAllReagents(HashMap<Reagent, Quantity> reagents) {
        this.reagents.putAll(reagents);
    }

    @Override
    public String toString() {
        return name;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);

        List<Map<String, Object>> labInstList = new ArrayList<>();
        for (Map.Entry<LabInstrument, Quantity> entry : labInstruments.entrySet()) {
            Map<String, Object> iMap = new HashMap<>();
            iMap.put("id",entry.getKey().getId());
            iMap.put("value", entry.getValue().getValue());
            iMap.put("unit", entry.getValue().getUnit());
            labInstList.add(iMap);
        }
        map.put("instruments", labInstList);

        List<Map<String, Object>> reagentList = new ArrayList<>();
        for (Map.Entry<Reagent, Quantity> entry : reagents.entrySet()) {
            Map<String, Object> rMap = new HashMap<>();
            rMap.put("id",entry.getKey().getId());
            rMap.put("value", entry.getValue().getValue());
            rMap.put("unit", entry.getValue().getUnit());
            reagentList.add(rMap);
        }
        map.put("reagents", reagentList);

        return map;
    }

    public static Practice fromMap(Map<String, Object> map) {
        String id = (String) map.get("id");
        String name = (String) map.get("name");

        Practice practice = new Practice(id, name);

        HashMap<LabInstrument, Quantity> instruments = new HashMap<>();
        List<Map<String, Object>> instList = (List<Map<String, Object>>) map.get("instruments");
        if (instList != null) {
            for (Map<String, Object> entry : instList) {
                LabInstrument i = DataModel.getLabInstrument((String) entry.get("id"));
                Quantity q = Quantity.fromMap(entry);
                instruments.put(i, q);
            }
        }

        HashMap<Reagent, Quantity> reagents = new HashMap<>();
        List<Map<String, Object>> reagentList = (List<Map<String, Object>>) map.get("reagents");
        if (reagentList != null) {
            for (Map<String, Object> entry : reagentList) {
                Reagent r = DataModel.getReagent((String) entry.get("id"));
                Quantity q = Quantity.fromMap(entry);
                reagents.put(r, q);
            }
        }

        practice.addAllLabInstruments(instruments);
        practice.addAllReagents(reagents);

        return practice;
    }

    public void removeItem(String id) {
        if (id.contains("I")){
            this.labInstruments.remove(DataModel.getLabInstrument(id));
        } else {
            this.reagents.remove(DataModel.getReagent(id));
        }
    }
}

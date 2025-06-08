package bcf.tfc.labstocker.model.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bcf.tfc.labstocker.adapters.ItemFeed;
import bcf.tfc.labstocker.adapters.SimpleItem;
import bcf.tfc.labstocker.model.DataModel;

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
        lastId++;
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

    public void updateReagent(Reagent reagent, float value, String unit) {
        this.reagents.put(reagent, new Quantity(value, unit));
    }

    public void removeReagent(Reagent reagent) {
        this.reagents.remove(reagent);
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

    public void updateLabInstrument(LabInstrument labInstrument, float value, String unit) {
        this.labInstruments.put(labInstrument, new Quantity(value, unit));
    }

    public void removeLabInstrument(LabInstrument labInstrument) {
        this.labInstruments.remove(labInstrument);
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
                Reagent r =  DataModel.getReagent((String) entry.get("id"));
                Quantity q = Quantity.fromMap(entry);
                location.reagents.put(r, q);
            }
        }

        location.labInstruments = new HashMap<>();
        List<Map<String, Object>> instruments = (List<Map<String, Object>>) map.get("instruments");
        if (instruments != null) {
            for (Map<String, Object> entry : instruments) {
                LabInstrument i = DataModel.getLabInstrument((String) entry.get("id"));
                Quantity q = Quantity.fromMap(entry);
                location.labInstruments.put(i, q);
            }
        }
    }

    protected HashMap<LabInstrument, Quantity> getLabInstruments() {
        return labInstruments;
    }

    protected HashMap<Reagent, Quantity> getReagents() {
        return reagents;
    }

    public void removeItem(String id) {
        if (id.contains("I")){
            this.getLabInstruments().remove(DataModel.getLabInstrument(id));
        } else {
            this.getReagents().remove(DataModel.getReagent(id));
        }
    }

    public ArrayList<ItemFeed> getReagentFeed() {
        ArrayList<ItemFeed> feed = new ArrayList<ItemFeed>();
        for (Map.Entry<Reagent, Quantity> entry : this.reagents.entrySet()) {
            feed.add(new ItemFeed(entry.getKey().getId(), entry.getKey().getFormula(), entry.getValue().toString(), this.id ,this.getClass().getSimpleName(), null));
        }
        return feed;
    }

    public ArrayList<ItemFeed> getInstrumentFeed() {
        ArrayList<ItemFeed> feed = new ArrayList<ItemFeed>();
        for (Map.Entry<LabInstrument, Quantity> entry : this.labInstruments.entrySet()) {
            feed.add(new ItemFeed(entry.getKey().getId(), entry.getKey().getName(), entry.getValue().toString(), this.id ,this.getClass().getSimpleName(), null));
        }
        return feed;
    }

    public ArrayList<SimpleItem> search(String query) {
        ArrayList<SimpleItem> feed = new ArrayList<>();
        String name;
        String description;

        for (Map.Entry<Reagent, Quantity> entry : this.reagents.entrySet()) {
            name = entry.getKey().getFormula();
            description = entry.getKey().getDescription();
            if (name.toLowerCase().contains(query.toLowerCase()) || (description != null && description.toLowerCase().contains(query.toLowerCase()))) {
                feed.add(new SimpleItem(entry.getKey().getId(), entry.getKey().getFormula(), true));
            }
        }

        for (Map.Entry<LabInstrument, Quantity> entry : this.labInstruments.entrySet()) {
            name = entry.getKey().getName();
            description = entry.getKey().getObservations();
            if (name.toLowerCase().contains(query.toLowerCase()) || (description != null && description.toLowerCase().contains(query.toLowerCase())))  {
                feed.add(new SimpleItem(entry.getKey().getId(), entry.getKey().getName(), false));
            }
        }
        return feed;
    }
}

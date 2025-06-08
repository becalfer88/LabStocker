package bcf.tfc.labstocker.adapters;

public class SimpleItem {
    private String id;
    private String label;
    private boolean isReagent;

    public SimpleItem(String id, String label, boolean isReagent) {
        this.id = id;
        this.label = label;
        this.isReagent = isReagent;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public boolean isReagent() {
        return isReagent;
    }
}

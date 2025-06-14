package bcf.tfc.labstocker.model.data;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Class Quantity. Represents a quantity with a value and a unit
 *
 * @author Beatriz Calzo
 */
public class Quantity {

    private double value;
    private String unit;

    /**
     * Empty constructor for Firebase
     */
    public Quantity() {}

    public Quantity(double value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Serializes the quantity to a map
     * @return
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("value", value);
        map.put("unit", unit);
        return map;
    }

    /**
     * Deserializes the quantity from a map
     * @param map
     * @return
     */
    public static Quantity fromMap(Map<String, Object> map) {
        double value = ((Number) map.get("value")).doubleValue();
        String unit = (String) map.get("unit");
        return new Quantity(value, unit);
    }

    @NonNull
    @Override
    public String toString() {
        return (this.unit !=null) ? value + " " + unit: value + "";
    }
}

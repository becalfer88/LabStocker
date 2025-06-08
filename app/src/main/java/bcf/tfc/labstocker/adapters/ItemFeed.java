package bcf.tfc.labstocker.adapters;

import android.content.Context;

import bcf.tfc.labstocker.model.DataModel;
import bcf.tfc.labstocker.model.data.Laboratory;
import bcf.tfc.labstocker.model.data.Practice;
import bcf.tfc.labstocker.model.data.Subject;
import bcf.tfc.labstocker.model.data.Warehouse;

/**
 * Custom feed item, reusable on {@link FeedAdapter}.
 * Attributes:  id - id of the item
 *              name - name or any other descriptor of the item
 *              quantity - quantity of the item
 *              idParent - id of the parent item if it is necessary
 *              parentType - type of the parent item if it is necessary
 *              subject - subject of the item. Only for practices
 *
 * @author Beatriz Calzo
 */
public class ItemFeed {

    private String id;
    private String name;
    private String quantity;
    private String idParent;
    private String parentType;
    private Subject subject;


    public ItemFeed(String id, String value, String quantity, String idParent, String parentType, Subject subject) {
        this.id = id;
        this.name = value;
        this.quantity = quantity;
        this.idParent = idParent;
        this.parentType = parentType;
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdParent() {
        return idParent;
    }

    public void setIdParent(String idParent) {
        this.idParent = idParent;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /**
     * Remove the item from its parent, depending on its type
     * @param context
     */
    public void delete(Context context){

        switch (this.parentType){
            case "Laboratory":{
                Laboratory laboratory = (Laboratory) DataModel.getLocation(this.idParent);
                DataModel.removeItem(context, laboratory, this.id);
                break;
            }
            case "Warehouse":{
                Warehouse warehouse = (Warehouse) DataModel.getLocation(this.idParent);
                DataModel.removeItem(context, warehouse, this.id);
                break;
            }
            case "Practice":{
                Practice practice = DataModel.getPractice(this.idParent);
                DataModel.removeItem(context, practice, this.id);
                break;
            }
            case "Subject":{
                Subject subject = DataModel.getSubject(this.idParent);
                DataModel.removeItem(context, subject, this.id);
                break;
            }
        }
    }
}

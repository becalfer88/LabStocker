package bcf.tfc.labstocker.adapters;

import bcf.tfc.labstocker.model.DBManager;
import bcf.tfc.labstocker.model.DataModel;
import bcf.tfc.labstocker.model.data.Laboratory;
import bcf.tfc.labstocker.model.data.Practice;
import bcf.tfc.labstocker.model.data.Subject;
import bcf.tfc.labstocker.model.data.Warehouse;

public class ItemFeed {

    private String id;
    private String name;
    private String quantity;
    private String idParent;


    public ItemFeed(String id, String value, String quantity, String idParent) {
        this.id = id;
        this.name = value;
        this.quantity = quantity;
        this.idParent = idParent;
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

    public void delete(){
        switch (this.idParent){
            case "laboratories":{
                Laboratory laboratory = (Laboratory) DataModel.getLocation(this.idParent);
                DataModel.removeItem(laboratory, this.id);
                break;
            }
            case "warehouses":{
                Warehouse warehouse = (Warehouse) DataModel.getLocation(this.idParent);
                DataModel.removeItem(warehouse, this.id);
                break;
            }
            case "practices":{
                Practice practice = DataModel.getPractice(this.idParent);
                DataModel.removeItem(practice, this.id);
                break;
            }
            case "subjects":{
                Subject subject = DataModel.getSubject(this.idParent);
                DataModel.removeItem(subject, this.id);
                break;
            }
        }
    }
}

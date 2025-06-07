package bcf.tfc.labstocker.model.data.user;

public enum AccountType {

    BASIC (4), TECHNICIAN (3), PROFESSOR (2), ADMIN (1);

    private int id;

    AccountType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static AccountType fromId(int id) {
        for (AccountType accountType : AccountType.values()) {
            if (accountType.getId() == id) {
                return accountType;
            }
        }
        return null;
    }
}

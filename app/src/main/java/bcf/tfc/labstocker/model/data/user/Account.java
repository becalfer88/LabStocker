package bcf.tfc.labstocker.model.data.user;

import java.util.HashMap;
import java.util.Map;

/**
 * Class Account. Represents a client account.
 * It can contain a maximum of 4 users.
 *
 */
public class Account {

    private int id;
    private String email;
    private String name;
    private String password;
    private AccountType type;

    /**
     * Empty constructor for Firebase
     */
    public Account(){}

    /**
     * Constructor with extra parameters
     *
     * @param email
     * @param password
     * @param read    It determine if the account is read from the database on if it is a new one
     */
    public Account(String email, String password, Boolean read) {
        this.email = email;
        this.password = password;
        this.type = AccountType.BASIC;
        if (!read) {
            this.name = email.split("@")[0];
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public boolean validateName(String name) {
        Boolean valid = false;
        if (!name.isEmpty() && name.length() <= 20) {
            if (name.matches("[a-zA-Z0-9]*")) {
                valid = true;
            }
        }
        return valid;
    }

    public boolean isAdmin() {
        return this.type == AccountType.ADMIN;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("email", email);
        map.put("name", name);
        map.put("password", password);
        map.put("type", type.getId());
        return map;
    }

    public static Account fromMap(Map<String, Object> map) {
        Account account = new Account();
        account.setId((int) (long) map.get("id"));
        account.setEmail((String) map.get("email"));
        account.setName((String) map.get("name"));
        account.setPassword((String) map.get("password"));
        account.setType(AccountType.values()[(int) (long) map.get("type")]);
        return account;
    }
}
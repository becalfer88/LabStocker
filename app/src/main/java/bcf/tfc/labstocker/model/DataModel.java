package bcf.tfc.labstocker.model;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bcf.tfc.labstocker.model.data.DBCallback;
import bcf.tfc.labstocker.model.data.user.Account;
import bcf.tfc.labstocker.model.data.user.AccountType;
import bcf.tfc.labstocker.model.data.LabInstrument;
import bcf.tfc.labstocker.model.data.LabInstrumentType;
import bcf.tfc.labstocker.model.data.Laboratory;
import bcf.tfc.labstocker.model.data.Location;
import bcf.tfc.labstocker.model.data.Practice;
import bcf.tfc.labstocker.model.data.Quantity;
import bcf.tfc.labstocker.model.data.Reagent;
import bcf.tfc.labstocker.model.data.ReagentType;
import bcf.tfc.labstocker.model.data.Subject;
import bcf.tfc.labstocker.model.data.Warehouse;

/**
 * Class that manages all the app data
 */
public class DataModel {

    //Lists to be used by the app processes
    public static ArrayList<Account> accounts = new ArrayList<>();
    public static ArrayList<Subject> subjects = new ArrayList<>();
    public static ArrayList<Practice> practices = new ArrayList<>();
    public static ArrayList<Location> locations = new ArrayList<>();
    public static ArrayList<Reagent> reagents = new ArrayList<>();
    public static ArrayList<LabInstrument> labInstruments = new ArrayList<>();

    //
    public static void dataInit(DBCallback<Boolean> finalCB) {

        chargeAccounts(() ->
                chargeReagents(() ->
                        chargeInstruments(() ->
                                chargeWarehouses(() ->
                                        chargeLaboratories(() ->
                                                chargeSubjects(() ->
                                                        fillPractices(0, () -> finalCB.onSuccess(true))
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static void chargeAccounts(Runnable onComplete) {
        DBManager.getAllAccounts(new DBCallback<List<Account>>() {
            @Override
            public void onSuccess(List<Account> result) {
                accounts.addAll(result);
                onComplete.run();
            }

            @Override
            public void onFailure(Exception e) {
                onComplete.run();
            }
        });
    }

    private static void chargeSubjects(Runnable onComplete) {
        DBManager.getSubjects(new DBCallback<List<Subject>>() {
            @Override
            public void onSuccess(List<Subject> resultSubjects) {
                subjects.clear();
                subjects.addAll(resultSubjects);
                onComplete.run();
            }

            @Override
            public void onFailure(Exception e) {
                onComplete.run(); // Puedes registrar el error si quieres
            }
        });
    }

    public static void fillPractices(int index, Runnable onComplete) {
        if (index >= subjects.size()) {
            onComplete.run();
            return;
        }

        Subject subject = subjects.get(index);

        DBManager.getPractices(subject, new DBCallback<List<Practice>>() {
            @Override
            public void onSuccess(List<Practice> practicesList) {
                subject.addAllPractices(new ArrayList<>(practicesList));
                practices.addAll(practicesList);
                fillPractices(index + 1, onComplete);
            }

            @Override
            public void onFailure(Exception e) {
                onComplete.run(); // Go on even if there is an error
            }
        });
    }

    private static void chargeReagents(Runnable onComplete) {
        DBManager.getReagents(new DBCallback<List<Reagent>>() {
            @Override
            public void onSuccess(List<Reagent> result) {
                reagents.clear();
                reagents.addAll(result);
                onComplete.run();
            }

            @Override
            public void onFailure(Exception e) {
                onComplete.run();
            }
        });
    }

    private static void chargeInstruments(Runnable onComplete) {
        DBManager.getLabInstruments(new DBCallback<List<LabInstrument>>() {
            @Override
            public void onSuccess(List<LabInstrument> result) {
                labInstruments.clear();
                labInstruments.addAll(result);
                onComplete.run();
            }

            @Override
            public void onFailure(Exception e) {
                onComplete.run();
            }
        });
    }

    private static void chargeWarehouses(Runnable onComplete) {
        DBManager.getWarehouses(new DBCallback<List<Warehouse>>() {
            @Override
            public void onSuccess(List<Warehouse> result) {
                locations.addAll(result);
                onComplete.run();
            }

            @Override
            public void onFailure(Exception e) {
                onComplete.run();
            }
        });
    }

    private static void chargeLaboratories(Runnable onComplete) {
        DBManager.getLabs(new DBCallback<List<Laboratory>>() {
            @Override
            public void onSuccess(List<Laboratory> result) {
                locations.addAll(result);
                onComplete.run();
            }

            @Override
            public void onFailure(Exception e) {
                onComplete.run();
            }
        });
    }


    // Accounts
    public static Account addAccount(String email, String password, String name) {
        Account account = new Account(email, password, name);
        accounts.add(account);
        return account;
    }

    public static Account addAccount(Account account) {
        accounts.add(account);
        return account;
    }

    public static Account getAccount(String email) {
        for (Account account : accounts) {
            if (account.getEmail().equals(email)) {
                return account;
            }
        }
        return null;
    }

    public static Account getAccount(int id) {
        for (Account account : accounts) {
            if (account.getId() == id) {
                return account;
            }
        }
        return null;
    }

    public static void removeAccount(Account account) {
        accounts.remove(account);
    }

    public static void addSubject(String id, String name, String career, int year, int semester) {
        Subject subject = new Subject(id, name, career, year, semester);
        subjects.add(subject);
    }

    public static void updateSubject( Subject subject, String name, String career, int year, int semester) {
        subject.setName(name);
        subject.setCareer(career);
        subject.setYear(year);
        subject.setSemester(semester);
    }
    public static Subject getSubject(String id) {
        for (Subject subject : subjects) {
            if (subject.getId().equals(id)) {
                return subject;
            }
        }
        return null;
    }

    public static Subject getSubject(String name, String career) {
        for (Subject subject : subjects) {
            if (subject.getName().equals(name) && subject.getCareer().equals(career)) {
                return subject;
            }
        }
        return null;
    }

    public static void deleteSubject(Subject subject) {
        subjects.remove(subject);
        DBManager.deleteSubject(subject.getId(), new DBCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Log.i("Subject", "Subject deleted");
            }
            @Override
            public void onFailure(Exception e) {
                // Do nothing
            }
        });
    }

    public static ArrayList<Subject> getSubjectsByCareer(String career) {
        ArrayList<Subject> subjectsResult = new ArrayList<>();
        for (Subject subject : subjects) {
            if (subject.getCareer().equals(career)) {
                subjectsResult.add(subject);
            }
        }
        return subjectsResult;
    }

    public static ArrayList<String> getAllCareers() {
        ArrayList<String> careers = new ArrayList<>();
        for (Subject subject : subjects) {
            if (!careers.contains(subject.getCareer())) {
                careers.add(subject.getCareer());
            }
        }
        return careers;
    }

    public static void addPractice(String name, Subject subject) {
        Practice practice = new Practice(null, name);
        practices.add(practice);
    }

    public static Practice getPractice(String id) {
        for (Practice practice : practices) {
            if (practice.getId().equals(id)) {
                return practice;
            }
        }
        return null;
    }

    public static Practice getPractice(String name, Subject subject) {
        return subject.getPractice(name);
    }

    public static ArrayList<Practice> getPracticesBySubject(Subject subject) {
        ArrayList<Practice> practicesResult = new ArrayList<>();
        for (Practice practice : practices) {
            if (subject.getPractice(practice.getName()) != null) {
                practicesResult.add(practice);
            }
        }
        return practicesResult;
    }

    public static void addReagent(String formula, ReagentType type, String status, String description, String concentration) {
        Reagent reagent = new Reagent(null, formula, type, description, concentration);
        reagents.add(reagent);
    }

    public static Reagent getReagent(String id) {
        for (Reagent reagent : reagents) {
            if (reagent.getId().equals(id)) {
                return reagent;
            }
        }
        return null;
    }

    public static Reagent getReagent(String formula, String concentration) {
        for (Reagent reagent : reagents) {
            if (reagent.getFormula().equals(formula) && reagent.getConcentration().equals(concentration)) {
                return reagent;
            }
        }
        return null;
    }

    public static HashMap<Reagent, Quantity> getReagentsByPractice(Practice practice) {
        HashMap<Reagent, Quantity> reagentsResult = new HashMap<>();
        for (Reagent r : reagents) {
            Reagent reagent = practice.getReagent(r.getFormula());
            if (reagent != null) {
                reagentsResult.put(reagent, practice.getReagentQuantity(reagent));
            }
        }
        return reagentsResult;
    }

    public static HashMap<Reagent, Quantity> getReagentsByLocation(Location location) {
        HashMap<Reagent, Quantity> reagentsResult = new HashMap<>();
        for (Reagent r : reagents) {
            Reagent reagent = location.getReagent(r.getFormula());
            if (reagent != null) {
                reagentsResult.put(reagent, location.getReagentQuantity(reagent));
            }
        }
        return reagentsResult;
    }

    public static void addLabInstrument(String name, LabInstrumentType type, String material, String observations) {
        LabInstrument labInstrument = new LabInstrument(null, name, type, material, observations);
        labInstruments.add(labInstrument);
    }

    public static LabInstrument getLabInstrument(String id) {
        for (LabInstrument labInstrument : labInstruments) {
            if (labInstrument.getId().equals(id)) {
                return labInstrument;
            }
        }
        return null;
    }

    public static HashMap<LabInstrument, Quantity> getLabInstrumentsByPractice(Practice practice) {
        HashMap<LabInstrument, Quantity> labInstrumentsResult = new HashMap<>();
        for (LabInstrument l : labInstruments) {
            LabInstrument labInstrument = practice.getLabInstrument(l.getName());
            if (labInstrument != null) {
                labInstrumentsResult.put(labInstrument, practice.getLabInstrumentQuantity(labInstrument));
            }
        }
        return labInstrumentsResult;
    }

    public static HashMap<LabInstrument, Quantity> getLabInstrumentsByLocation(Location location) {
        HashMap<LabInstrument, Quantity> labInstrumentsResult = new HashMap<>();
        for (LabInstrument l : labInstruments) {
            LabInstrument labInstrument = location.getLabInstrument(l.getName());
            if (labInstrument != null) {
                labInstrumentsResult.put(labInstrument, location.getLabInstrumentQuantity(labInstrument));
            }
        }
        return labInstrumentsResult;
    }

    public static void addLaboratory(String labAddress, Warehouse warehouse, Boolean read) {
        Laboratory laboratory = new Laboratory(labAddress, warehouse, read);
        locations.add(laboratory);
    }

    public static void updateLaboratory(Laboratory laboratory,String address, String warehouse) {
        laboratory.setAddress(address);
        laboratory.setWarehouse(getWarehouse(warehouse));
    }

    public static Laboratory getLaboratory(String address) {
        for (Location location : locations) {
            if (location.getAddress().equals(address) && location instanceof Laboratory) {
                return (Laboratory) location;
            }
        }
        return null;
    }

    public static void addWarehouse(String address, Boolean read) {
        Warehouse warehouse = new Warehouse(address, read);
        setNextLocationId(warehouse);
        locations.add(warehouse);
    }

    public static void updateWarehouse(Warehouse warehouse,String address) {
        warehouse.setAddress(address);
    }

    public static Warehouse getWarehouse(String address) {
        for (Location location : locations) {
            if (location.getAddress().equals(address) && location instanceof Warehouse) {
                return (Warehouse) location;
            }
        }
        return null;
    }

    public static Warehouse getWarehouseById(String id) {
        for (Location location : locations) {
            if (location.getId().equals(id)) {
                return (Warehouse) location;
            }
        }
        return null;
    }

    public static ArrayList<Location> getLocations(String type) {
        ArrayList<Location> locationsResult = new ArrayList<>();
        switch (type) {
            case "warehouses":
                for (Location location : locations) {
                    if (location instanceof Warehouse) {
                        locationsResult.add(location);
                    }
                }
                break;
            case "laboratories":
                for (Location location : locations) {
                    if (location instanceof Laboratory) {
                        locationsResult.add(location);
                    }
                }
                break;
        }
        return locationsResult;
    }

    public static Location getLocation(String id) {
        for (Location location : locations) {
            if (location.getId().equals(id)) {
                return location;
            }
        }
        return null;
    }

    public static void deleteLocation(Location location) {
        locations.remove(location);
        DBManager.deleteLocation(location, new DBCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Log.d("Location deleted", location.getAddress());
            }
            @Override
            public void onFailure(Exception e) {
                // Do nothing
            }
        });
    }

    public static void setNextLocationId(Location location) {

        DBManager.getLastLocationId(new DBCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                int lastId = result;
                location.setId("LOC" + (lastId + 1));
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Error", e.getMessage());
            }
        });

    }

    public static boolean isAdmin(String email) {
        Account account = getAccount(email);
        return account != null && account.isAdmin();
    }

    public static void removeItem(Object parent, String id) {
        if (parent instanceof Practice) {
            ((Practice) parent).removeItem(id);
        } else if (parent instanceof Location) {
            ((Location) parent).removeItem(id);
        } else if (parent instanceof Subject) {
            ((Subject) parent).removeItem(id);
        }
    }
}

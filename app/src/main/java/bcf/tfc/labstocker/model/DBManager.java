package bcf.tfc.labstocker.model;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import bcf.tfc.labstocker.model.data.DBCallback;
import bcf.tfc.labstocker.model.data.LabInstrument;
import bcf.tfc.labstocker.model.data.Laboratory;
import bcf.tfc.labstocker.model.data.Location;
import bcf.tfc.labstocker.model.data.Practice;
import bcf.tfc.labstocker.model.data.Reagent;
import bcf.tfc.labstocker.model.data.Subject;
import bcf.tfc.labstocker.model.data.Warehouse;
import bcf.tfc.labstocker.model.data.user.Account;

public class DBManager {




    // Account
    public static void upsertAccount(Account account, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.document("users/account" + account.getId())
                .set(account.toMap())
                .addOnSuccessListener(docRef -> {
                    cb.onSuccess(true);
                })
                .addOnFailureListener(cb::onFailure);

    }

    public static void getAccount(String email, DBCallback<Account> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(qSnapshot -> {
                    if (!qSnapshot.isEmpty()) {
                        DocumentSnapshot doc = qSnapshot.getDocuments().get(0);
                        Account account = Account.fromMap(doc.getData());
                        cb.onSuccess(account);
                    } else {
                        cb.onSuccess(null);
                    }
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void deleteAccount(int id, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.document("users/account" + id)
                .delete()
                .addOnSuccessListener(docRef -> {
                    cb.onSuccess(true);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void findAccount(String email, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        cb.onSuccess(true);
                    } else {
                        cb.onSuccess(false);
                    }
                })
                .addOnFailureListener(cb::onFailure);
    }

    // Subject
    public static void upsertSubject(Subject subject, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.document("subjects/" + subject.getId())
                .set(subject.toMap())
                .addOnSuccessListener(docRef -> {
                    cb.onSuccess(true);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void getSubjects(DBCallback<List<Subject>> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("subjects")
                .get()
                .addOnSuccessListener(docRef -> {
                    List<Subject> subjects = docRef.toObjects(Subject.class);
                    cb.onSuccess(subjects);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void deleteSubject(String id, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.document("subjects/" + id)
                .delete()
                .addOnSuccessListener(docRef -> {
                    cb.onSuccess(true);
                })
                .addOnFailureListener(cb::onFailure);
    }

    // Practices TODO
    public static void upsertPractice(Subject subject, Practice practice, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> newPractice = subject.getPractice(practice.getId()).toMap();

        db.document("subjects/" + subject.getId() + "/practices/" + practice.getId())
                .set(newPractice)
                .addOnSuccessListener(docRef -> {
                    cb.onSuccess(true);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void getPractices(Subject subject, DBCallback<List<Practice>> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("subjects/" + subject.getId() + "/practices")
                .get()
                .addOnSuccessListener(docRef -> {
                    List<Practice> practices = docRef.toObjects(Practice.class);
                    cb.onSuccess(practices);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void deletePractice(Subject subject, String id, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.document("subjects/" + subject.getId() + "/practices/" + id)
                .delete()
                .addOnSuccessListener(docRef -> {
                    cb.onSuccess(true);
                })
                .addOnFailureListener(cb::onFailure);
    }

    // Supplies
    public static void upsertReagent(Reagent reagent, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.document("supplies/data/reagents")
                .update("regent" + reagent.getId(),reagent.toMap())
                .addOnSuccessListener(docRef -> {
                    cb.onSuccess(true);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void getReagents(DBCallback<List<Reagent>> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("supplies/data/reagents")
                .get()
                .addOnSuccessListener(docRef -> {
                    List<Reagent> reagents = docRef.toObjects(Reagent.class);
                    cb.onSuccess(reagents);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void upsertLabInstrument(LabInstrument labInstrument, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.document("supplies/data/instruments")
                .update("instrument" + labInstrument.getId(),  labInstrument.toMap())
                .addOnSuccessListener(docRef -> {
                    cb.onSuccess(true);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void getLabInstruments(DBCallback<List<LabInstrument>> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("supplies/data/instruments")
                .get()
                .addOnSuccessListener(docRef -> {
                    List<LabInstrument> labInstruments = docRef.toObjects(LabInstrument.class);
                    cb.onSuccess(labInstruments);
                })
                .addOnFailureListener(cb::onFailure);
    }

    // Locations
    public static void upsertLocation(Location location, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> newLocation = location.toMap();

        if (location instanceof Warehouse) {
            db.document("locations/data/warehouses")
                    .update("warehouse" + location.getId(), newLocation)
                    .addOnSuccessListener(docRef -> {
                        cb.onSuccess(true);
                    })
                    .addOnFailureListener(cb::onFailure);
        } else {
            db.document("locations/data/laboratories")
                    .update("laboratory" + location.getId(), newLocation)
                    .addOnSuccessListener(docRef -> {
                        cb.onSuccess(true);
                    })
                    .addOnFailureListener(cb::onFailure);
        }
    }

    public static void getLabs(DBCallback<List<Laboratory>> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("locations/data/laboratories")
                .get()
                .addOnSuccessListener(docRef -> {
                    List<Laboratory> locations = docRef.toObjects(Laboratory.class);
                    cb.onSuccess(locations);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void getWarehouses(DBCallback<List<Warehouse>> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("locations/data/warehouses")
                .get()
                .addOnSuccessListener(docRef -> {
                    List<Warehouse> locations = docRef.toObjects(Warehouse.class);
                    cb.onSuccess(locations);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void deleteLocation(Location location, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (location instanceof Warehouse) {
            db.document("locations/data/warehouses")
                    .update("warehouse" + location.getId(), FieldValue.delete())
                    .addOnSuccessListener(docRef -> {
                        cb.onSuccess(true);
                    })
                    .addOnFailureListener(cb::onFailure);
        } else {
            db.document("locations/data/laboratories")
                    .update("laboratory" + location.getId(), FieldValue.delete())
                    .addOnSuccessListener(docRef -> {
                        cb.onSuccess(true);
                    })
                    .addOnFailureListener(cb::onFailure);
        }
    }

    public static void getLastLocationId(DBCallback<Integer> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference labsRef = db.collection("locations/data/laboratories");
        CollectionReference warehousesRef = db.collection("locations/data/warehouses");

        List<Task<QuerySnapshot>> tasks = Arrays.asList(
                labsRef.get(),
                warehousesRef.get()
        );

        Tasks.whenAllSuccess(tasks)
                .addOnSuccessListener(results -> {
                    int maxID = 0;
                    for (Object result : results) {
                        QuerySnapshot resultCollection = (QuerySnapshot) result;
                        for (DocumentSnapshot doc : resultCollection.getDocuments()) {
                            String id = doc.getId();
                            if (id.toUpperCase().matches("LOC\\d+")) {
                                int currentID = Integer.parseInt(id.substring(3));
                                if (currentID > maxID) {
                                    maxID = currentID;
                                }
                            }
                        }
                    }
                    cb.onSuccess(maxID);
                })
                .addOnFailureListener(cb::onFailure);
    }


}

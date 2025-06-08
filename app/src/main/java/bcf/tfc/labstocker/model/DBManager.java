package bcf.tfc.labstocker.model;

import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import bcf.tfc.labstocker.R;
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

    public static DBCallback<Boolean> getSimpleBoolCallback() {
        return new DBCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {}

            @Override
            public void onFailure(Exception e) {}
        };
    }

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

    public static void getAllAccounts(DBCallback<List<Account>> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnSuccessListener(collectionRef -> {
                    List<Account> accounts = new ArrayList<>();
                    for (DocumentSnapshot doc : collectionRef.getDocuments()) {
                        accounts.add(Account.fromMap(doc.getData()));
                    }
                    cb.onSuccess(accounts);
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
        DocumentReference subjectRef = db.document("subjects/" + subject.getId());

        db.document("subjects/" + subject.getId())
                .set(subject.toMap())
                .addOnSuccessListener(docRef -> {
                    List<Practice> practices = subject.getPractices();
                    if (practices == null || practices.isEmpty()) {
                        cb.onSuccess(true); // No practices, ends here
                        return;
                    }

                    // Contador para saber cuándo ha terminado
                    final int total = practices.size();
                    final int[] successCount = {0};

                    for (Practice p : practices) {
                        subjectRef.collection("practices").document(p.getId())
                                .set(p.toMap())
                                .addOnSuccessListener(doc -> {
                                    successCount[0]++;
                                    if (successCount[0] == total) {
                                        cb.onSuccess(true);
                                    }
                                })
                                .addOnFailureListener(cb::onFailure);
                    }
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void getSubjects(DBCallback<List<Subject>> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("subjects")
                .get()
                .addOnSuccessListener(collectionRef -> {
                    List<Subject> subjects = new ArrayList<>();
                    for (DocumentSnapshot doc : collectionRef.getDocuments()) {
                        subjects.add(Subject.fromMap(doc.getData()));
                    }
                    cb.onSuccess(subjects);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void deleteSubject(String id, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference subjectRef = db.document("subjects/" + id);

        // Primero borra la subcolección de prácticas
        subjectRef.collection("practices")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : querySnapshot) {
                        batch.delete(doc.getReference());
                    }

                    // Aplica el borrado de las prácticas
                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                // Ahora borra el documento Subject
                                subjectRef.delete()
                                        .addOnSuccessListener(aVoid2 -> cb.onSuccess(true))
                                        .addOnFailureListener(cb::onFailure);
                            })
                            .addOnFailureListener(cb::onFailure);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void getPractices(Subject subject, DBCallback<List<Practice>> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("subjects/" + subject.getId() + "/practices")
                .get()
                .addOnSuccessListener(collectionRef -> {
                    List<Practice> practices = new ArrayList<>();
                    for (DocumentSnapshot doc : collectionRef.getDocuments()) {
                        practices.add(Practice.fromMap(doc.getData()));
                    }
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

        db.document("reagents/"+reagent.getId())
                .update(reagent.toMap())
                .addOnSuccessListener(docRef -> {
                    cb.onSuccess(true);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void getReagents(DBCallback<List<Reagent>> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("reagents")
                .get()
                .addOnSuccessListener(collectionRef -> {
                    List<Reagent> reagents = new ArrayList<>();
                    for (DocumentSnapshot doc : collectionRef.getDocuments()) {
                        reagents.add(Reagent.fromMap(doc.getData()));
                    }
                    cb.onSuccess(reagents);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void upsertLabInstrument(LabInstrument labInstrument, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.document("instruments"+ labInstrument.getId())
                .update(labInstrument.toMap())
                .addOnSuccessListener(docRef -> {
                    cb.onSuccess(true);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void getLabInstruments(DBCallback<List<LabInstrument>> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("instruments")
                .get()
                .addOnSuccessListener(collectionRef -> {
                    List<LabInstrument> labInstruments = new ArrayList<>();
                    for (DocumentSnapshot doc : collectionRef.getDocuments()) {
                        labInstruments.add(LabInstrument.fromMap(doc.getData()));
                    }
                    cb.onSuccess(labInstruments);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void transferResources(Location source, Location destination, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        String sourcePath = (source instanceof Warehouse) ? "warehouses/W" : "laboratories/L";
        String destinationPath = (destination instanceof Warehouse) ? "warehouses/W" : "laboratories/L";

        DocumentReference sourceRef = db.document(sourcePath + source.getId());
        DocumentReference destinationRef = db.document(destinationPath + destination.getId());

        batch.set(sourceRef, source.toMap());
        batch.set(destinationRef, destination.toMap());

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    cb.onSuccess(true);
                })
                .addOnFailureListener(cb::onFailure);
    }


    // Locations
    public static void upsertLocation(Location location, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> newLocation = location.toMap();

        String collection = (location instanceof Warehouse) ? "warehouses" : "laboratories";
        String id = (location instanceof Warehouse) ? "W" : "L";

        db.collection(collection)
                .document(id + location.getId())
                .set(newLocation)
                .addOnSuccessListener(docRef -> {
                    cb.onSuccess(true);
                })
                .addOnFailureListener(cb::onFailure);

    }

    public static void getLabs(DBCallback<List<Laboratory>> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("laboratories")
                .get()
                .addOnSuccessListener(collectionRef -> {
                    List<Laboratory> locations =new ArrayList<>();
                    for (DocumentSnapshot doc : collectionRef.getDocuments()) {
                        locations.add(Laboratory.fromMap(doc.getData()));
                    }
                    cb.onSuccess(locations);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void getWarehouses(DBCallback<List<Warehouse>> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("warehouses")
                .get()
                .addOnSuccessListener(collectionRef -> {
                    List<Warehouse> locations = new ArrayList<>();
                    for (DocumentSnapshot doc : collectionRef.getDocuments()) {
                        locations.add(Warehouse.fromMap(doc.getData()));
                    }
                    cb.onSuccess(locations);
                })
                .addOnFailureListener(cb::onFailure);
    }

    public static void deleteLocation(Location location, DBCallback<Boolean> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (location instanceof Warehouse) {
            db.document("warehouses/w" + location.getId())
                    .delete()
                    .addOnSuccessListener(docRef -> {
                        cb.onSuccess(true);
                    })
                    .addOnFailureListener(cb::onFailure);
        } else {
            db.document("laboratories/l" + location.getId())
                    .delete()
                    .addOnSuccessListener(docRef -> {
                        cb.onSuccess(true);
                    })
                    .addOnFailureListener(cb::onFailure);
        }
    }

    public static void getLastLocationId(DBCallback<Integer> cb) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference labsRef = db.collection("laboratories");
        CollectionReference warehousesRef = db.collection("warehouses");

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
                            String id = doc.getString("id");
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

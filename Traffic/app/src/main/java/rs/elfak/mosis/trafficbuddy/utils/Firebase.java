package rs.elfak.mosis.trafficbuddy.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Firebase {
    // realtime db
    public static final String DB_USERS = "users"; // "tables"
    public static final String DB_REPORTS = "reports"; // "tables"

    private static final DatabaseReference dbRef = FirebaseDatabase.getInstance("https://trafficbuddy-47de3-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

    public static DatabaseReference getDbRef() {
        return dbRef;
    }

    // storage
    public static final String STORAGE_USER_PHOTOS = "users/photos";
    public static final String STORAGE_DISCO_PHOTOS = "report/photos";

    private static final StorageReference storageRef = FirebaseStorage.getInstance("gs://trafficbuddy-47de3.appspot.com").getReference();

    public static StorageReference getStorageRef() {
        return storageRef;
    }

    // auth
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public static FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }
}

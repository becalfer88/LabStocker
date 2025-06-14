package bcf.tfc.labstocker.model.data;

/**
 * Callback for database operations
 *
 * @param <T> result type
 *
 * @author Beatriz Calzo
 */
public interface DBCallback<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}

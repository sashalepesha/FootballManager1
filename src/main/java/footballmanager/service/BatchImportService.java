package footballmanager.service;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.function.Consumer;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchImportService {

    private static final int BATCH_SIZE = 50;

    private final EntityManager entityManager;

    public BatchImportService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public <T> int batchImport(List<T> entities) {
        Session session = entityManager.unwrap(Session.class);

        Integer previousBatchSize = session.getJdbcBatchSize();

        session.setJdbcBatchSize(BATCH_SIZE);

        try {
            int totalSaved = 0;

            for (T entity : entities) {
                entityManager.persist(entity);
                totalSaved++;

                if (totalSaved % BATCH_SIZE == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }

            entityManager.flush();
            entityManager.clear();

            return totalSaved;
        } finally {
            session.setJdbcBatchSize(previousBatchSize);
        }
    }

    @Transactional
    public <T> int saveAllWithoutBatch(List<T> entities, Consumer<List<T>> saveAction) {
        if (entities.isEmpty()) {
            return 0;
        }

        Session session = entityManager.unwrap(Session.class);

        Integer previousBatchSize = session.getJdbcBatchSize();
        session.setJdbcBatchSize(0);

        try {
            saveAction.accept(entities);
            entityManager.flush();
            return entities.size();
        } finally {
            session.setJdbcBatchSize(previousBatchSize);
        }
    }
}

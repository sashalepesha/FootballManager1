package footballmanager.repository;

import footballmanager.domain.ApplicationScript;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationScriptRepository extends JpaRepository<ApplicationScript, Long> {
    Optional<ApplicationScript> findByName(String name);
}

package main.model.repositories;
import main.model.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSetting, Integer> {

@Transactional
@Modifying
@Query(value = "UPDATE global_settings SET value = ?1 WHERE code = 'MULTIUSER_MODE'", nativeQuery = true)
int setMultiuserMode(String multiuserMode);

@Transactional
@Modifying
@Query(value = "UPDATE global_settings SET value = ?1 WHERE code = 'POST_PREMODERATION'", nativeQuery = true)
int setPostPremoderation(String preModeration);

@Transactional
@Modifying
@Query(value = "UPDATE global_settings SET value = ?1 WHERE code = 'STATISTICS_IS_PUBLIC'", nativeQuery = true)
int setStatistics(String statistics);

@Query(value = "SELECT gs.value FROM global_settings AS gs WHERE gs.code = 'MULTIUSER_MODE'", nativeQuery = true)
String findMultiuserModeValue();

@Query(value = "SELECT gs.value FROM global_settings AS gs WHERE gs.code = 'POST_PREMODERATION'", nativeQuery = true)
String findPremoderationValue();

}


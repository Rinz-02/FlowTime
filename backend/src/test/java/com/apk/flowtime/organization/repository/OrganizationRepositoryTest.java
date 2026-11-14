
package com.apk.flowtime.organization.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.apk.flowtime.config.JpaConfig;
import com.apk.flowtime.entity.Organization;
import com.apk.flowtime.repository.OrganizationRepository;

@DataJpaTest
@AutoConfigureTestDatabase(
    replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(JpaConfig.class)
class OrganizationRepositoryTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldGenerateUuidAndAuditTimestamps() throws InterruptedException {

        // 1. Create organization
        Organization organization = new Organization(
                "FlowTime Test",
                "flowtime-test-" + UUID.randomUUID(),
                "Asia/Malaysia"
        );

        // 2. Persist entity
        Organization saved = organizationRepository.saveAndFlush(organization);

        // 3. Verify generated UUID
        assertNotNull(saved.getId());

        // 4. Verify auditing timestamps
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());

        Instant originalCreatedAt = saved.getCreatedAt();
        Instant originalUpdatedAt = saved.getUpdatedAt();

        // 5. Allow time to pass before updating
        Thread.sleep(1100);

        // 6. Update organization
        saved.updateName("FlowTime Updated");

        organizationRepository.saveAndFlush(saved);

        // 7. Force database reload
        entityManager.clear();

        Organization updated = organizationRepository
                .findById(saved.getId())
                .orElseThrow();

        // 8. Verify updated values
        assertEquals("FlowTime Updated", updated.getName());

        // 9. Verify creation timestamp remains unchanged
        assertEquals(originalCreatedAt, updated.getCreatedAt());

        // 10. Verify modification timestamp changed
        assertTrue(
                updated.getUpdatedAt().isAfter(originalUpdatedAt),
                "updatedAt should be later than the original timestamp"
        );
    }
}

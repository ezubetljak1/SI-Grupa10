package ba.unsa.si.docflow.config;

import ba.unsa.si.docflow.dao.RoleDAO;
import ba.unsa.si.docflow.entity.RoleEntity;
import ba.unsa.si.docflow.entity.enums.RoleName;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleDataInitializer implements ApplicationRunner {

    private final RoleDAO roleDAO;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (RoleName roleName : RoleName.values()) {
            if (roleDAO.findByName(roleName) != null) {
                continue;
            }

            RoleEntity role = new RoleEntity();
            role.setName(roleName);
            roleDAO.persist(role);
            log.info("Seeded role: {}", roleName);
        }
    }
}

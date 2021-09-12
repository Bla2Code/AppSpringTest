package com.appspring.listener;

import com.appspring.entity.AuditRevision;
import com.appspring.util.PrincipalUtils;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Класс слушателя аудита. Сохраняет имя пользователя для записи ревизии.
 */
public class AuditableListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {

        AuditRevision auditRevision = (AuditRevision) revisionEntity;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            auditRevision.setUsername("admin");
        } else {
            auditRevision.setUsername(PrincipalUtils.getLoggedUsername());
        }
    }
}

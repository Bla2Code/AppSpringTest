package com.appspring.entity;

import com.appspring.listener.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Класс для аудита действий пользователя средствами Hibernate Envers
 * В базе данных создается таблица "audit_revision", в которой хранятся ссылки на ревизии
 * из таблиц с постфиксом _aud, временем и именем пользователя
 * Для подключения сущности к аудиту необходимо добавить аннотацию @Audited к классу сущности.
 * в таблице сохраняется только имя пользователя (является уникальным) из таблицы users.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@RevisionEntity(value = AuditableListener.class)
public class AuditRevision {

    @Id
    @GeneratedValue
    @RevisionNumber
    private int id;

    @RevisionTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    private String username;
}

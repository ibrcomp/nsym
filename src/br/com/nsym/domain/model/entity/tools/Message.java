package br.com.nsym.domain.model.entity.tools;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.nsym.domain.model.entity.PersistentEntity;
import br.com.nsym.domain.model.entity.converter.UserConverter;
import br.com.nsym.domain.model.security.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
  * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.1.0, 19/10/2016
 */
@Entity
@Table(name = "messages")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Message extends PersistentEntity {

    private static final long serialVersionUID = -4104220686947914701L;
	@Getter
    @Setter
//    @NotNull(message = "{message.title}")
    @Column(name = "title", nullable = false, length = 90)
    private String title;
    @Getter
    @Setter
//    @NotNull(message = "{message.content}")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    @Getter
    @Setter
    @Column(name = "deleted")
    private boolean deleted;
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "priority_type", nullable = false)
    private MessagePriorityType priorityType;
    
    @Getter
    @Setter
    @Convert(converter = UserConverter.class)
    @Column(name = "sender", nullable = false)
    private User sender;
    
    @Getter
    @Setter
    @Transient
    private List<User> recipients;

    /**
     * 
     */
    public Message() {
        this.priorityType = MessagePriorityType.LOW;
        this.recipients = new ArrayList<>();
    }
    
    /**
     * 
     * @param sender 
     */
    public Message(User sender) {
        this();
        this.sender = sender;
    }

    /**
     * @return o nome da pessoa que envio a mensagem
     */
    public String getSenderName() {
        return this.sender.getName();
    }
    
    /**
     * @return se nossa mensagem tem ou nao destinatarios
     */
    public boolean hasRecipients() {
        return !this.recipients.isEmpty();
    }
}

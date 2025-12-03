package br.com.nsym.domain.model.entity.tools;

/**
 *
  * @author Ibrahim Yousef Quatani
 *
 * @version 2.0.0
 * @since 1.1.0, 19/10/2016
 */
public enum MessagePriorityType {

    HIGH("message-priority-type.high"),
    LOW("message-priority-type.low"),
    MEDIUM("message-priority-type.medium");
    
    private final String description;

    /**
     * @param description 
     */
    private MessagePriorityType(String description) {
        this.description = description;
    }

    /**
     * @return 
     */
    @Override
    public String toString() {
        return this.description;
    }
}

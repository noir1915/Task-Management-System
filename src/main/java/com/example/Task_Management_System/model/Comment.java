package com.example.Task_Management_System.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    @NotNull
    private Task task;

    @Column(name = "task_id", insertable = false, updatable = false)
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @NotNull
    private User author;

    @Column(name = "author_id", insertable = false, updatable = false)
    private Long authorId;

    @NotBlank
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public void setAuthor(User author) {
        author.getComments().add(this);
        this.author = author;
    }

    public void setTask(Task task) {
        task.getComments().add(this);
        this.task = task;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
//            return false;
//        Comment comment = (Comment) o;
//        return Objects.equals(this.author.getId(), comment.author.getId()) &&  // author.getId() or authorId
//                Objects.equals(this.content, comment.getContent()) &&
//                Objects.equals(created, comment.getCreated());
//    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(authorId, content, created);
//    }

    @Override
    public String toString() {
        return Hibernate.getClass(this).getSimpleName() + "(" +
                "id = " + id + ", " +
                "content = " + content + ", " +
                "created = " + created + ", " +
                "authorId = " + authorId + ", " + // (author != null ? author.getId() : null) or authorId
                "taskId = " + taskId + ", " +  // (task != null ? task.getId() : null) or taskId
                ")";
    }
}


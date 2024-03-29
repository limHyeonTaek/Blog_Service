package com.blogProject.common.comment.entity;

import com.blogProject.common.entity.BaseTimeEntity;
import com.blogProject.common.member.entity.Member;
import com.blogProject.common.post.entity.Post;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String comments;

  @ManyToOne
  private Member member;

  @ManyToOne
  private Post post;

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private Comment parentComment;

  @Builder.Default
  @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
  private List<Comment> childComments = new ArrayList<>();

  public boolean isWrittenBy(Member member) {
    return this.member.equals(member);
  }

}

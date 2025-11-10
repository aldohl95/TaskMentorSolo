package com.taskmentor.taskmentor.repository;

import com.taskmentor.taskmentor.enums.Category;
import com.taskmentor.taskmentor.model.MentorProfile;
import com.taskmentor.taskmentor.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByMentor(MentorProfile mentor);

    List<Task> findByMentor_MentorId(Long mentorId);

    List<Task> findByCategory(Category category);

    List<Task> findByMentorAndCategory(MentorProfile mentor, Category category);
}

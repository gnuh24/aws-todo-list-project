package aws.todolist.taskflow.service;


import java.util.List;

import aws.todolist.taskflow.entity.Section;

public interface SectionService {

    List<Section> getAllSection();

    Section getSectionById(String id);

    Section addSection(Section section);

    Section updateSection(String id, Section updateSection);

    Section removeSection(String id);
}

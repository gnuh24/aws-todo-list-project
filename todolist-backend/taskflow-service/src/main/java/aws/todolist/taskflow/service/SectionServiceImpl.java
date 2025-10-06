package aws.todolist.taskflow.service;

import org.springframework.stereotype.Service;


import java.util.List;

import aws.todolist.taskflow.entity.Section;



@Service
public class SectionServiceImpl implements SectionService {
    @Override
    public List<Section> getAllSection() {
        return List.of();
    }

    @Override
    public Section getSectionById(String id) {
        return null;
    }

    @Override
    public Section addSection(Section section) {
        return null;
    }

    @Override
    public Section updateSection(String id, Section updateSection) {
        return null;
    }

    @Override
    public Section removeSection(String id) {
        return null;
    }
}

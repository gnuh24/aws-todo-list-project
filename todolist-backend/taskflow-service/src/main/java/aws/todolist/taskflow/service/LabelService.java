package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.personalLabel.PersonalLabelRequestDTO;
import aws.todolist.taskflow.dto.personalLabel.PersonalLabelResponseDTO;
import aws.todolist.taskflow.dto.projectLabel.ProjectLabelResponseDTO;
import aws.todolist.taskflow.dto.taskLabel.TaskLabelRequestDTO;
import aws.todolist.taskflow.dto.taskLabel.TaskLabelResponseDTO;
import aws.todolist.taskflow.entity.*;
import aws.todolist.taskflow.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LabelService {
	
	@Autowired
	private PersonalLabelRepository personalLabelRepository;
	
	@Autowired
	private ProjectLabelRepository projectLabelRepository;
	
	@Autowired
	private TaskLabelRepository taskLabelRepository;
	
	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	public List<PersonalLabelResponseDTO> getPersonalLabels(String accountId) {
		List<PersonalLabel> labels = personalLabelRepository.findAllByAccountIdAndIsDeletedFalse(accountId);
		return labels.stream()
		    .map(l -> new PersonalLabelResponseDTO(l.getId(), l.getName()))
		    .collect(Collectors.toList());
	}
	
	public List<ProjectLabelResponseDTO> getProjectLabels(String projectId) {
		List<ProjectLabel> labels = projectLabelRepository.findAllByProjectIdAndIsDeletedFalse(projectId);
		return labels.stream()
		    .map(l -> new ProjectLabelResponseDTO(l.getId(), l.getName()))
		    .collect(Collectors.toList());
	}
	
	@Transactional
	public PersonalLabelResponseDTO createPersonalLabel(PersonalLabelRequestDTO req, Account user) {
		
		PersonalLabel label = PersonalLabel.builder()
		    .id(UUID.randomUUID().toString())
		    .account(user)
		    .name(req.getName())
		    .createdAt(LocalDateTime.now())
		    .updatedAt(LocalDateTime.now())
		    .isDeleted(false)
		    .build();
		
		personalLabelRepository.save(label);
		
		return new PersonalLabelResponseDTO(label.getId(), label.getName());
	}
	
	
	@Transactional
	public TaskLabelResponseDTO addLabelToTask(String projectId, String taskId, TaskLabelRequestDTO req, Account user) {
		
		Task task = taskRepository.findById(taskId)
		    .orElseThrow(() -> new EntityNotFoundException("Task không tồn tại"));
		
		Project project = projectRepository.findById(projectId)
		    .orElseThrow(() -> new EntityNotFoundException("Project không tồn tại"));
		
		TaskLabel taskLabel;
		
		// ===== CASE 1: ProjectLabel (có projectLabelId) =====
		if (req.getProjectLabelId() != null && !req.getProjectLabelId().isBlank()) {
			ProjectLabel projectLabel = projectLabelRepository.findById(req.getProjectLabelId())
			    .orElseThrow(() -> new EntityNotFoundException("ProjectLabel không tồn tại"));
			
			taskLabel = TaskLabel.builder()
			    .id(UUID.randomUUID().toString())
			    .task(task)
			    .projectLabel(projectLabel)
			    .createdAt(LocalDateTime.now())
			    .build();
			
			taskLabelRepository.save(taskLabel);
			
			return toResponseDTO(taskLabel);
		}
		
		// ===== CASE 2: PersonalLabel =====
		if (req.getPersonalLabelId() != null && !req.getPersonalLabelId().isBlank()) {
			
			PersonalLabel personalLabel = personalLabelRepository.findById(req.getPersonalLabelId())
			    .orElseThrow(() -> new EntityNotFoundException("PersonalLabel không tồn tại"));
			
			// Check ProjectLabel trùng tên
			ProjectLabel existing = projectLabelRepository
			    .findByProjectIdAndName(projectId, personalLabel.getName());
			
			ProjectLabel finalLabel;
			
			if (existing != null) {
				finalLabel = existing;
			} else {
				// Tạo project label mới
				ProjectLabel newLabel = ProjectLabel.builder()
				    .id(UUID.randomUUID().toString())
				    .project(project)
				    .name(personalLabel.getName())
				    .description(personalLabel.getDescription())
				    .createdBy(user)
				    .createdAt(LocalDateTime.now())
				    .updatedAt(LocalDateTime.now())
				    .isDeleted(false)
				    .build();
				
				finalLabel = projectLabelRepository.save(newLabel);
			}
			
			// Tạo TaskLabel bằng ProjectLabel
			taskLabel = TaskLabel.builder()
			    .id(UUID.randomUUID().toString())
			    .task(task)
			    .projectLabel(finalLabel)
			    .createdAt(LocalDateTime.now())
			    .build();
			
			taskLabelRepository.save(taskLabel);
			
			return toResponseDTO(taskLabel);
		}
		
		throw new IllegalArgumentException("Vui lòng truyền projectLabelId hoặc personalLabelId");
	}
	
	private TaskLabelResponseDTO toResponseDTO(TaskLabel taskLabel) {
		return TaskLabelResponseDTO.builder()
		    .id(taskLabel.getId())
		    .name(taskLabel.getProjectLabel().getName())
		    .isAiGenerated(false)
		    .confidence(0.0F)
		    .build();
	}
	
	
	public void removeLabelFromTask(String projectId, String taskId, String taskLabelId) {
		
		TaskLabel label = taskLabelRepository.findById(taskLabelId)
		    .orElseThrow(() -> new EntityNotFoundException("TaskLabel không tồn tại"));
		
		if (!label.getTask().getId().equals(taskId)) {
			throw new RuntimeException("TaskLabel không thuộc task này");
		}
		
		taskLabelRepository.delete(label);
	}
	
	
}

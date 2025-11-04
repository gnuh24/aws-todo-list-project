import React, { useEffect, useState } from "react";
import { Share2, LayoutList, MessageSquare } from "lucide-react";
import AddTaskModal from "../../component/Modal/AddTaskModal";
import MainLayout from "../../layout/MainLayout";
import ProjectHeader from "../../component/Header/ProjectHeader";
import InlineAddSection from "../../component/Modal/InlineAddSection";
import SectionItem from "../../component/Section/SectionItem";
import { useParams } from "react-router-dom";
import { https_taskflow } from "../../service/api";
import { message } from "antd";

export default function ProjectPage() {
  const [showModal, setShowModal] = useState(false);
  const [currentSection, setCurrentSection] = useState(null);
  const [sections, setSections] = useState([]);
  const [isAddingSection, setIsAddingSection] = useState(false);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [newSectionName, setNewSectionName] = useState("");
  const { projectName, projectId } = useParams();

  const idUser = JSON.parse(localStorage.getItem("USER_REGISTER"));
  const { id } = idUser;

  // ✅ Thêm Task vào section cụ thể

  useEffect(() => {
    const fetchSections = async () => {
      try {
        setLoading(true);

        const res = await https_taskflow.get(
          `/v1/projects/${projectId}/sections`
        );

        if (res.data?.status === 200 && Array.isArray(res.data.data)) {
          setSections(res.data.data);

          console.log("sections: ", sections);
        } else {
          console.warn("API không trả về mảng hợp lệ:", res.data);
          setSections([]); // fallback để tránh lỗi .map
        }
      } catch (err) {
        console.error("Lỗi khi fetch sections:", err);
        setError(err.message || "Không thể tải dữ liệu sections");
      } finally {
        setLoading(false);
      }
    };

    fetchSections();
  }, [projectId]);

  const handleDeleteTask = (sectionId, taskId) => {
    setSections((prev) =>
      prev.map((section) =>
        section.id === sectionId
          ? {
              ...section,
              tasks: section.tasks.filter((task) => task.id !== taskId),
            }
          : section
      )
    );
  };
  const handleAddTask = async (newTask) => {
    if (!currentSection) {
      message.warning("Vui lòng chọn Section để thêm Task!");
      return;
    }

    try {
      // ✅ Gọi API POST /v1/projects/:projectId/tasks
      const res = await https_taskflow.post(`/v1/projects/${projectId}/tasks`, {
        title: newTask.title,
        description: newTask.description || "",
        sectionId: currentSection,
        deadline: newTask.deadline || null,
        priority: newTask.priority || "MEDIUM",
        idAccountAssign: "22222222-2222-2222-2222-222222222222",
      });

      if (res.status === 200 && res.data?.data) {
        const createdTask = res.data.data;

        // ✅ Cập nhật UI (thêm task mới vào section tương ứng)
        setSections((prev) =>
          prev.map((section) =>
            section.id === currentSection
              ? { ...section, tasks: [...section.tasks, createdTask] }
              : section
          )
        );

        alert("✅ Thêm task thành công!");
      } else {
        alert("❌ Không thể thêm task!");
      }
    } catch (error) {
      console.error("❌ Lỗi khi thêm task:", error);
      message.error("Lỗi khi thêm task!");
    } finally {
      setShowModal(false);
      setCurrentSection(null);
    }
  };

  // ✅ Thêm Section mới
  const handleAddSection = async () => {
    if (!newSectionName.trim()) return;

    try {
      // ✅ Gọi API POST tới /v1/projects/:id/sections
      const res = await https_taskflow.post(
        `/v1/projects/${projectId}/sections`,
        {
          name: newSectionName, // payload gửi lên backend
        }
      );

      if (res.data?.status === 200 || res.status === 200) {
        const newSection = res.data.data; // lấy section mới từ API response

        // ✅ Cập nhật lại danh sách section trong UI
        setSections((prev) => [...prev, newSection]);

        console.log("✅ Section added:", newSection);
      } else {
        console.warn("⚠️ API không trả về thành công:", res);
      }
    } catch (error) {
      console.error("❌ Lỗi khi thêm section:", error);
    } finally {
      // ✅ Reset form
      setNewSectionName("");
      setIsAddingSection(false);
    }
  };
  // const handleUpdateTask = (sectionId, updatedTask) => {
  //   setSections((prev) =>
  //     prev.map((section) =>
  //       section.id === sectionId
  //         ? {
  //             ...section,
  //             tasks: section.tasks.map((task) =>
  //               task.id === updatedTask.id ? updatedTask : task
  //             ),
  //           }
  //         : section
  //     )
  //   );
  // };
  const handleUpdateTask = (sectionId, updatedTask) => {
    setSections((prev) =>
      prev.map((section) =>
        section.id === sectionId
          ? {
              ...section,
              tasks: section.tasks.map((t) =>
                t.id === updatedTask.id ? updatedTask : t
              ),
            }
          : section
      )
    );
  };

  return (
    <MainLayout>
      <div className="min-h-screen bg-white">
        {/* HEADER */}
        <ProjectHeader></ProjectHeader>

        {/* MAIN CONTENT */}
        <main className="px-10 py-6">
          <h1 className="text-2xl font-bold mb-4">{projectName}</h1>

          {/* Section list */}

          <div className="space-y-4">
            {sections.map((section) => (
              <SectionItem
                handleDeleteTask={handleDeleteTask}
                projectId={projectId}
                handleUpdateTask={handleUpdateTask}
                key={section.id}
                section={section}
                onAddTaskClick={(id) => {
                  setCurrentSection(id);
                  setShowModal(true);
                }}
              />
            ))}
          </div>

          <InlineAddSection
            newSectionName={newSectionName}
            setNewSectionName={setNewSectionName}
            handleAddSection={handleAddSection}
            setIsAddingSection={setIsAddingSection}
          ></InlineAddSection>
        </main>

        {/* MODAL ADD TASK */}
        <AddTaskModal
          open={showModal}
          onCancel={() => {
            setShowModal(false);
            setCurrentSection(null);
          }}
          onAdd={handleAddTask}
        />
      </div>
    </MainLayout>
  );
}

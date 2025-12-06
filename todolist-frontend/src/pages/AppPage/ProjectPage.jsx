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
import { toast } from "sonner";

export default function ProjectPage() {
  const [showModal, setShowModal] = useState(false);
  const [currentSection, setCurrentSection] = useState(null);
  const [sections, setSections] = useState([]);
  const [isAddingSection, setIsAddingSection] = useState(false);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [newSectionName, setNewSectionName] = useState("");
  const { projectName, projectId } = useParams();
  const [selectedProject, setSelectedProject] = useState(null);
  const [selectedSection, setSelectedSection] = useState(null);

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
  const userInfo = localStorage.getItem("USER_INFO");

  if (!userInfo) {
    console.warn("Không có USER_INFO trong localStorage");
    return; // ⛔ dừng hàm và không lỗi
  }

  const parsed = JSON.parse(userInfo);

  if (!parsed || !parsed.id) {
    console.warn("USER_INFO không hợp lệ hoặc không có id");
    return; // ⛔ dừng hàm
  }

  const { id } = parsed; // ✔ an toàn

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
    if (!currentSection && !selectedSection) {
      message.warning("Vui lòng chọn Section để thêm Task!");
      return;
    }

    // ❗ Nếu user chọn section trong modal → dùng selectedSection
    // ❗ Nếu không chọn gì → dùng section nơi họ bấm nút Add → currentSection
    const finalSectionId = selectedSection || currentSection;
    const finalProjectId = selectedProject || projectId;

    try {
      const res = await https_taskflow.post(
        `/v1/projects/${finalProjectId}/tasks`,
        {
          title: newTask.title,
          description: newTask.description || "",
          sectionId: finalSectionId,
          deadline: newTask.deadline || null,
          priority: newTask.priority || "MEDIUM",
          idAccountAssign: id,
        }
      );

      if (res.status === 200 && res.data?.data) {
        const createdTask = res.data.data;

        // 🟢 Cập nhật UI
        setSections((prev) =>
          prev.map((section) =>
            section.id === finalSectionId
              ? { ...section, tasks: [...section.tasks, createdTask] }
              : section
          )
        );

        toast.success("Thêm task thành công!");
      }
    } catch (error) {
      console.error("Lỗi khi thêm task:", error);
      toast.error("Lỗi khi thêm task!");
    } finally {
      setShowModal(false);
      setCurrentSection(null);
      setSelectedSection(null); // reset để modal không bị nhớ section cũ
      setSelectedProject(null);
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

        toast.success("Section added");
      } else {
        console.warn("⚠️ API không trả về thành công:", res);
      }
    } catch (error) {
      toast.error("Lỗi khi thêm section");
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

  const handleSaveEdit = async (sectionId, newName, projectId) => {
    console.log("newName: ", newName);
    console.log("projectId: ", projectId.projectId);
    console.log("sectionId: ", sectionId);

    try {
      const res = await https_taskflow.patch(
        `/v1/project/${projectId.projectId}/sections/${sectionId}/update-name`,
        {
          name: newName,
        }
      );

      if (res.status === 200) {
        setSections((prev) =>
          prev.map((s) => (s.id === sectionId ? { ...s, name: newName } : s))
        );
      }
    } catch (err) {
      toast.error("Lỗi update section:", err);
    }
  };
  const handleDeleteSection = async (sectionId) => {
    try {
      const res = await https_taskflow.delete(
        `/v1/projects/${projectId}/sections/${sectionId}`
      );

      if (res.status === 200) {
        // Xóa ngay trong UI
        setSections((prev) => prev.filter((sec) => sec.id !== sectionId));
        toast.success("Section deleted successfully!");
      } else {
        toast.error("Failed to delete section!");
      }
    } catch (err) {
      console.error("❌ Error deleting section:", err);
      toast.error("Error deleting section!");
    }
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
                handleSaveEdit={handleSaveEdit}
                handleDeleteSection={handleDeleteSection}
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
          onSelectProjectSection={(data) => {
            console.log("📌 PROJECT:", data.projectId);
            console.log("📌 SECTION:", data.sectionId);

            setSelectedProject(data.projectId);
            setSelectedSection(data.sectionId);
          }}
        />
      </div>
    </MainLayout>
  );
}

import { useEffect, useState } from "react";
import { Spin } from "antd";
import { toast } from "sonner";

import InboxHeader from "../../component/Header/InboxHeader";
import SectionItem from "../../component/Section/SectionItem";
import AddTaskModal from "../../component/Modal/AddTaskModal";
import { https_taskflow } from "../../service/api";

export default function InboxPage() {
    const [project, setProject] = useState(null);
    const [sections, setSections] = useState([]);
    const [loading, setLoading] = useState(false);

    const [showModal, setShowModal] = useState(false);
    const [currentSection, setCurrentSection] = useState(null);

    // ============================
    // 📥 FETCH DEFAULT PROJECT (INBOX)
    // ============================
    useEffect(() => {
        fetchDefaultProject();
    }, []);

    const fetchDefaultProject = async () => {
        try {
            setLoading(true);

            const res = await https_taskflow.get("/v1/project-default");

            if (res.data?.status === 200) {
                const projectData = res.data.data;
                setProject(projectData);
                setSections(projectData.sections || []);
            }
        } catch (err) {
            console.error(err);
            toast.error("Không thể tải Inbox");
        } finally {
            setLoading(false);
        }
    };

    // ============================
    // ➕ ADD TASK (GIỐNG PROJECT PAGE)
    // ============================
    const handleAddTask = async (newTask) => {
        if (!currentSection) {
            toast.warning("Vui lòng chọn section");
            return;
        }

        try {
            const res = await https_taskflow.post(
                `/v1/projects/${project.id}/tasks`,
                {
                    title: newTask.title,
                    description: newTask.description || "",
                    sectionId: currentSection,
                    deadline: newTask.deadline || null,
                    startTime: newTask.startTime || null,
                    priority: newTask.priority || "MEDIUM",
                }
            );

            if (res.status === 200 && res.data?.data) {
                const createdTask = res.data.data;

                setSections((prev) =>
                    prev.map((section) =>
                        section.id === currentSection
                            ? { ...section, tasks: [...section.tasks, createdTask] }
                            : section
                    )
                );

                toast.success("Thêm task thành công!");
            }
        } catch (err) {
            toast.error("Lỗi khi thêm task");
        } finally {
            setShowModal(false);
            setCurrentSection(null);
        }
    };

    // ============================
    // 🗑 DELETE TASK
    // ============================
    const handleDeleteTask = (sectionId, taskId) => {
        setSections((prev) =>
            prev.map((section) =>
                section.id === sectionId
                    ? {
                        ...section,
                        tasks: section.tasks.filter((t) => t.id !== taskId),
                    }
                    : section
            )
        );
    };

    // ============================
    // ✏ UPDATE TASK
    // ============================
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

    if (loading) {
        return (
            <>
                <InboxHeader />
                <div className="flex justify-center mt-20">
                    <Spin />
                </div>
            </>
        );
    }

    return (
        <>
            {/* <InboxHeader /> */}

            <div className="min-h-screen bg-white px-10 py-6">
                {/* TITLE */}
                <h1 className="text-2xl font-bold mb-4">
                    {project?.name || "Inbox"}
                </h1>

                {/* SECTION LIST */}
                <div className="space-y-4">
                    {sections.map((section) => (
                        <SectionItem
                            key={section.id}
                            section={section}
                            projectId={project?.id}
                            handleDeleteTask={handleDeleteTask}
                            handleUpdateTask={handleUpdateTask}
                            onAddTaskClick={(sectionId) => {
                                setCurrentSection(sectionId);
                                setShowModal(true);
                            }}
                        />
                    ))}
                </div>
            </div>

            {/* ADD TASK MODAL */}
            <AddTaskModal
                open={showModal}
                onCancel={() => {
                    setShowModal(false);
                    setCurrentSection(null);
                }}
                onAdd={handleAddTask}
            />
        </>
    );
}

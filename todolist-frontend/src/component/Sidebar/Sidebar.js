import {
  InboxOutlined,
  FilterOutlined,
  CheckCircleOutlined,
  FolderOutlined,
  SearchOutlined,
  PlusOutlined,
  BellOutlined,
  MailOutlined,
  AppstoreOutlined,
  DownOutlined,
  PlusCircleOutlined,
  EditOutlined,
  StarOutlined,
  CopyOutlined,
  ShareAltOutlined,
  UploadOutlined,
  DownloadOutlined,
  CalendarOutlined,
  HistoryOutlined,
  DeleteOutlined,
} from "@ant-design/icons";
import { Button, Dropdown, Menu } from "antd";
import { useState, useEffect } from "react";

import { useLocation, useNavigate } from "react-router-dom";
import { https_taskflow } from "../../service/api";
import HeaderSidebar from "../Header/HeaderSidebar";
import AddProjectModal from "../Modal/AddProjectModal";
import AddTaskModal from "../Modal/AddTaskModal";
import EditProjectModal from "../Modal/EditProjectModal";
import SearchCommandModal from "../Modal/SearchCommandModal";
import UserMenuDropdown from "../UserMenu/UserMenuDropdown";
import SidebarItem from "./SidebarItem";
import { message } from "antd";
import { toast } from "sonner";
export default function Sidebar() {
  const location = useLocation();
  const [selectedSection, setSelectedSection] = useState(null);

  const [sections, setSections] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [openModal, setOpenModal] = useState(false);
  const [openSearch, setOpenSearch] = useState(false);
  const navigate = useNavigate();
  const [openAddProject, setOpenAddProject] = useState(false);
  const [error, setError] = useState(null);
  // ✅ Danh sách project từ API
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showProjectMenu, setShowProjectMenu] = useState(false);
  const [activeProjectId, setActiveProjectId] = useState(null);

  const [editModalOpen, setEditModalOpen] = useState(false);
  const [editProject, setEditProject] = useState(null);
  const [currentSection, setCurrentSection] = useState(null);
  const [selectedProject, setSelectedProject] = useState(null);
  const handleAddTask = async (newTask) => {
    if (!selectedProject || !selectedSection) {
      message.warning("Vui lòng chọn Project & Section!");
      return;
    }

    try {
      const res = await https_taskflow.post(
        `/v1/projects/${selectedProject}/tasks`,
        {
          title: newTask.title,
          description: newTask.description || "",
          sectionId: selectedSection,
          deadline: newTask.deadline || null,
          priority: newTask.priority || "MEDIUM",
          idAccountAssign: id,
        }
      );

      if (res.status === 200 && res.data?.data) {
        const createdTask = res.data.data;

        // 🟢 Update UI ngay lập tức
        setSections((prev) =>
          prev.map((section) =>
            section.id === selectedSection
              ? { ...section, tasks: [...section.tasks, createdTask] }
              : section
          )
        );

        toast.error("Thêm task thành công!");
      }
    } catch (err) {
      console.error("Lỗi khi thêm task:", err);
      message.error("Lỗi khi thêm task!");
    } finally {
      // reset state + đóng modal
      setShowModal(false);
      setSelectedProject(null);
      setSelectedSection(null);
    }
  };

  // ✅ Sau này call API thật, giờ là dữ liệu mock
  useEffect(() => {
    const fetchProjects = async () => {
      try {
        setLoading(true);
        const res = await https_taskflow.get("/v1/projects");
        if (res.data?.status === 200 && Array.isArray(res.data.data)) {
          setProjects(res.data.data);
          console.log("res.data.data: ", res.data.data);
        } else {
        }
      } catch (err) {
      } finally {
        setLoading(false);
      }
    };

    fetchProjects();
  }, []);
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

  // ✅ Kiểm tra route active
  const isActive = (path) => location.pathname === path;

  // ✅ Dropdown khi click nút +
  const projectMenu = (
    <Menu
      items={[
        {
          key: "1",
          label: "Add project",
          icon: <FolderOutlined />,
          onClick: () => setOpenAddProject(true),
        },
        {
          key: "2",
          label: "Browse templates",
          icon: <AppstoreOutlined />,
          onClick: () => toast.error("Browse templates clicked"),
        },
      ]}
    />
  );
  const deleteProject = async (project) => {
    if (
      !window.confirm(
        `Are you sure you want to delete project: ${project.name}?`
      )
    ) {
      return;
    }

    try {
      const res = await https_taskflow.delete(`/v1/projects/${project.id}`);

      if (res.status === 200 || res.data?.status === 200) {
        // Xóa khỏi UI
        setProjects((prev) => prev.filter((p) => p.id !== project.id));
        console.log("Deleted:", project.id);
      }
    } catch (err) {
      console.error("Delete failed", err);
      toast.error("Cannot delete project");
    }
  };
  const archiveProject = async (project) => {
    if (
      !window.confirm(
        `Are you sure you want to archive project: ${project.name}?`
      )
    ) {
      return;
    }

    try {
      // Call API update status
      const res = await https_taskflow.patch(`/v1/projects/${project.id}`, {
        isArchived: "true",
      });

      if (res.status === 200 || res.data?.status === 200) {
        // cập nhật UI – bỏ project ra khỏi list hiện tại
        setProjects((prev) => prev.filter((p) => p.id !== project.id));

        console.log("Archived:", project.id);
        toast.success("Project archived");
      }
    } catch (err) {
      console.error("Archive failed", err);
      toast.error("Cannot archive project");
    }
  };

  const openProjectMenu = (project) => {
    console.log("Open project menu for:", project);

    // hoặc mở menu thật
    setSelectedProject(project);
    setShowProjectMenu(true);
  };

  return (
    <div className="w-64 border-r bg-white flex flex-col justify-between shadow-sm">
      {/* Header */}
      <HeaderSidebar></HeaderSidebar>

      {/* Main Navigation */}
      <div className="flex-1 overflow-y-auto">
        <div className="px-3 py-2">
          <div className="space-y-[2px]">
            <SidebarItem
              icon={<PlusCircleOutlined />}
              label="Add task"
              onClick={() => setShowModal(true)}
            />

            <AddTaskModal
              open={showModal}
              onCancel={() => {
                setShowModal(false);
                setSelectedProject(null);
                setSelectedSection(null);
              }}
              onAdd={handleAddTask}
              onSelectProjectSection={(data) => {
                console.log("📌 Selected PROJECT:", data.projectId);
                console.log("📌 Selected SECTION:", data.sectionId);

                setSelectedProject(data.projectId);
                setSelectedSection(data.sectionId);
              }}
            />

            <SidebarItem
              onClick={() => setOpenSearch(true)}
              icon={<SearchOutlined />}
              label="Search"
            />
            <SearchCommandModal
              open={openSearch}
              onClose={() => setOpenSearch(false)}
            />
            {/* <SidebarItem
              onClick={() => navigate("/app/inbox")}
              icon={<InboxOutlined />}
              label="Inbox"
              active={isActive("/app/inbox")}
            /> */}
            <SidebarItem
              onClick={() => navigate("/app/today")}
              icon={<CalendarOutlined />}
              label="Today"
              count={2}
              active={isActive("/app/today")}
              countClass="text-[11px] text-red-500 font-medium"
            />
            <SidebarItem
              onClick={() => navigate("/app/upcoming")}
              icon={<CalendarOutlined />}
              label="Upcoming"
              active={isActive("/app/upcoming")}
            />
            {/* <SidebarItem
              onClick={() => navigate("/app/filters")}
              icon={<FilterOutlined />}
              label="Filters & Labels"
              active={isActive("/app/filters")}
            /> */}
            <SidebarItem
              onClick={() => navigate("/app/activity")}
              icon={<CheckCircleOutlined />}
              label="Completed"
              active={isActive("/app/activity")}
            />
          </div>

          {/* Projects Section */}
          <div className="mt-6 px-2">
            <div
              className={`
    flex items-center justify-between mb-1 px-3 py-2 rounded-md cursor-pointer
    transition-colors duration-150 select-none
    ${
      isActive("/app/archive")
        ? "bg-red-50 text-red-600 font-medium"
        : "text-gray-500 hover:bg-gray-50"
    }
  `}
              onClick={() => navigate("/app/archive")}
            >
              <div className="text-xs font-semibold uppercase tracking-wide">
                My Projects
              </div>

              <Dropdown overlay={projectMenu} trigger={["click"]}>
                <Button
                  type="text"
                  icon={<PlusOutlined />}
                  size="small"
                  className="hover:bg-gray-100 rounded-md"
                  onClick={(e) => e.stopPropagation()}
                />
              </Dropdown>
            </div>

            {projects
              .filter((p) => p.isArchived === false)
              .map((project) => (
                <SidebarItem
                  onEdit={(project) => {
                    setEditProject(project);
                    setEditModalOpen(true);
                  }}
                  onArchiveProject={archiveProject}
                  onDeleteProject={deleteProject}
                  key={project.id}
                  icon={<FolderOutlined />}
                  label={project.name}
                  active={activeProjectId === project.id}
                  onClick={() => {
                    setActiveProjectId(project.id); // 👈 click để active
                    navigate(`/app/projects/${project.name}/${project.id}`);
                  }}
                  isProject={true}
                  project={project}
                />
              ))}
          </div>
        </div>
      </div>
      <EditProjectModal
        onUpdated={(id, newName) => {
          setProjects((prev) =>
            prev.map((p) => (p.id === id ? { ...p, name: newName } : p))
          );
        }}
        open={editModalOpen}
        project={editProject}
        onClose={() => setEditModalOpen(false)}
      />

      {/* Footer */}
      <div className="p-3 text-xs text-gray-400 border-t flex items-center gap-2 hover:text-gray-600 cursor-pointer">
        ⚙️ Help & resources
      </div>
      <AddProjectModal
        open={openAddProject}
        onCancel={() => setOpenAddProject(false)}
        onAdd={(newProject) => {
          // ✅ Cập nhật danh sách project ngay lập tức
          setProjects((prev) => [...prev, newProject]);
          setOpenAddProject(false);
        }}
      />
    </div>
  );
}

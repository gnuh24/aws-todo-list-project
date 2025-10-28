import {
  InboxOutlined,
  CalendarOutlined,
  FilterOutlined,
  CheckCircleOutlined,
  FolderOutlined,
  SearchOutlined,
  PlusOutlined,
  BellOutlined,
  AppstoreOutlined,
  DownOutlined,
  PlusCircleOutlined,
} from "@ant-design/icons";
import { Button, Dropdown, Menu } from "antd";
import { useState, useEffect } from "react";

import { useLocation, useNavigate } from "react-router-dom";
import { https_taskflow } from "../../service/api";
import HeaderSidebar from "../Header/HeaderSidebar";
import AddProjectModal from "../Modal/AddProjectModal";
import AddTaskModal from "../Modal/AddTaskModal";
import SearchCommandModal from "../Modal/SearchCommandModal";
import UserMenuDropdown from "../UserMenu/UserMenuDropdown";
import SidebarItem from "./SidebarItem";

export default function Sidebar() {
  const location = useLocation();
  const [openModal, setOpenModal] = useState(false);
  const [openSearch, setOpenSearch] = useState(false);
  const navigate = useNavigate();
  const [openAddProject, setOpenAddProject] = useState(false);
  const [error, setError] = useState(null);
  // ✅ Danh sách project từ API
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(false);

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
          onClick: () => alert("Browse templates clicked"),
        },
      ]}
    />
  );

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
              onClick={() => setOpenModal(true)}
            />
            <AddTaskModal
              open={openModal}
              onCancel={() => setOpenModal(false)}
              onAdd={(task) => {
                alert("New task:", task);

                setOpenModal(false);
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
            <SidebarItem
              onClick={() => navigate("/app/inbox")}
              icon={<InboxOutlined />}
              label="Inbox"
              active={isActive("/app/inbox")}
            />
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
            <SidebarItem
              onClick={() => navigate("/app/filters")}
              icon={<FilterOutlined />}
              label="Filters & Labels"
              active={isActive("/app/filters")}
            />
            <SidebarItem
              onClick={() => navigate("/app/activity")}
              icon={<CheckCircleOutlined />}
              label="Completed"
              active={isActive("/app/activity")}
            />
          </div>

          {/* Projects Section */}
          <div className="mt-6 px-2">
            <div className="flex items-center justify-between mb-1">
              <div className="text-xs text-gray-500 font-semibold uppercase tracking-wide">
                My Projects
              </div>

              <Dropdown overlay={projectMenu} trigger={["click"]}>
                <Button
                  type="text"
                  icon={<PlusOutlined />}
                  size="small"
                  className="hover:bg-gray-100 rounded-md"
                />
              </Dropdown>
            </div>

            {/* List of Projects */}
            <div className="space-y-[2px]">
              {projects.map((project) => (
                <SidebarItem
                  key={project.id}
                  icon={<FolderOutlined />}
                  label={`${project.name}`}
                  active={isActive(
                    `/app/projects/${project.name}/${project.id}`
                  )}
                  onClick={() =>
                    navigate(`/app/projects/${project.name}/${project.id}`)
                  }
                />
              ))}
            </div>
          </div>
        </div>
      </div>

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

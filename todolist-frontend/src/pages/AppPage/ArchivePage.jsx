import { useEffect, useState } from "react";
import {
  SearchOutlined,
  PlusOutlined,
  DownOutlined,
  UserOutlined,
} from "@ant-design/icons";

import MainLayout from "../../layout/MainLayout";
import { https_taskflow } from "../../service/api";
import { useNavigate } from "react-router-dom";

export default function MyProjectsPage() {
  const [allProjects, setAllProjects] = useState([]); // lưu toàn bộ
  const [showArchivedOnly, setShowArchivedOnly] = useState(true);
  const navigate = useNavigate();

  // GET ALL PROJECTS
  useEffect(() => {
    https_taskflow
      .get("/v1/projects")
      .then((res) => {
        const data = res.data?.data || res.data;
        setAllProjects(data);
      })
      .catch((err) => console.log("Error fetching projects:", err));
  }, []);

  // FILTER tùy toggle
  const projects = showArchivedOnly
    ? allProjects.filter((p) => p.isArchived === true)
    : allProjects.filter((p) => p.isArchived === false);

  return (
    <MainLayout>
      <div className="w-full px-20 py-10">
        {/* Title */}
        <div className="flex items-center gap-3 mb-1">
          <img
            src="https://cdn-icons-png.flaticon.com/512/147/147144.png"
            alt=""
            className="w-6 h-6 rounded-full"
          />
          <h1 className="text-2xl font-semibold">My Projects   </h1>
        </div>

        <p className="text-sm text-gray-500 mb-6">Free</p>

        {/* Search */}
        <div className="relative mb-4">
          <SearchOutlined className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            placeholder="Search projects"
            className="w-full border rounded-md pl-10 pr-4 py-2 text-sm 
                       focus:outline-none focus:ring-1 focus:ring-gray-300"
          />
        </div>

        {/* Toggle */}
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-2">
            <span className="text-sm text-gray-700">
              Archived projects only
            </span>

            <label className="relative inline-flex items-center cursor-pointer">
              <input
                type="checkbox"
                checked={showArchivedOnly}
                onChange={(e) => setShowArchivedOnly(e.target.checked)}
                className="sr-only peer"
              />
              <div
                className="w-10 h-5 bg-gray-200 rounded-full peer peer-checked:bg-red-500 
                        after:content-[''] after:absolute after:top-[2px] after:left-[2px]
                        after:bg-white after:h-4 after:w-4 after:rounded-full
                        after:transition-all peer-checked:after:translate-x-5"
              />
            </label>
          </div>

          <div className="flex items-center gap-1 border rounded-md px-3 py-1 cursor-pointer hover:bg-gray-100 text-sm">
            <PlusOutlined className="text-gray-600" />
            <span>Add</span>
            <DownOutlined className="text-xs text-gray-500" />
          </div>
        </div>

        {/* Count */}
        <p className="text-sm text-gray-700 mb-2">{projects.length} projects</p>

        {/* Project list */}
        <div className="border-t border-gray-200">
          {projects.length === 0 ? (
            <p className="text-gray-400 italic p-4">No projects found</p>
          ) : (
            projects.map((project) => (
              <div
                key={project.id}
                onClick={() => {
                  if (project.isArchived === false) {
                    navigate(`/app/projects/${project.name}/${project.id}`);
                  } else {
                    if (project.isArchived === true) {
                      navigate(`/app/archive/${project.name}/${project.id}`);
                    }
                  }
                }}
                className="flex items-center gap-3 px-2 py-3 border-b hover:bg-gray-50 cursor-pointer"
              >
                <span className="text-gray-500">#</span>
                <span className="text-gray-700">{project.name}</span>

                {project.isShared && (
                  <UserOutlined className="text-gray-500 text-xs" />
                )}
              </div>
            ))
          )}
        </div>
      </div>
    </MainLayout>
  );
}

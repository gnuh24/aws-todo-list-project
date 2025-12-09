import React, { useState, useEffect } from "react";
import {
  InboxOutlined,
  FolderOutlined,
  TagOutlined,
  DownOutlined,
} from "@ant-design/icons";
import { Dropdown, Input } from "antd";
import { https_taskflow } from "../../service/api";

export default function ProjectSelectDropdown({ selected = null, onSelect }) {
  const [search, setSearch] = useState("");
  const [projects, setProjects] = useState([]);

  const [sections, setSections] = useState({}); // 🟦 chứa sections theo projectId

  // 🟦 Fetch danh sách project
  const fetchProjects = async () => {
    try {
      const response = await https_taskflow.get("v1/projects");

      const apiProjects = response.data.data.map((item) => ({
        id: item.id,
        name: item.name,
        group: item.isDefault ? "default" : "My Projects",
        icon: item.isDefault ? <InboxOutlined /> : <FolderOutlined />,
      }));

      setProjects(apiProjects);
    } catch (error) {
      console.error("API get /projects error:", error);
    }
  };

  // 🟦 Fetch sections cho 1 project
  const fetchSections = async (projectId) => {
    if (sections[projectId]) return; // tránh gọi lại

    try {
      const res = await https_taskflow.get(
        `/v1/projects/${projectId}/sections`
      );
      setSections((prev) => ({
        ...prev,
        [projectId]: res.data.data, // lưu sections vào object
      }));
    } catch (err) {
      console.error("Error loading sections:", err);
    }
  };

  useEffect(() => {
    fetchProjects();
  }, []);

  const filtered = projects.filter((p) =>
    p.name.toLowerCase().includes(search.toLowerCase())
  );

  const menu = (
    <div className="bg-white rounded-xl shadow-lg p-2 w-72">
      <Input
        placeholder="Type a project name"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        className="mb-2"
        size="small"
      />

      <div className="max-h-64 overflow-y-auto">
        {/* Default Projects */}
        {filtered
          .filter((p) => p.group === "default")
          .map((p) => (
            <div key={p.id}>
              {/* Tên project */}
              <div
                className="flex items-center gap-2 px-3 py-2 hover:bg-gray-100 rounded-md cursor-pointer"
                onClick={() => {
                  fetchSections(p.id);
                  onSelect?.({
                    projectId: p.id,
                    projectName: p.name,
                    sectionId: null,
                    sectionName: null,
                  });
                  // click project → dùng default section
                }}
              >
                {p.icon} {p.name}
              </div>

              {/* Hiển thị sections */}
              {sections[p.id] &&
                sections[p.id].map((sec) => (
                  <div
                    key={sec.id}
                    className="pl-8 py-1 cursor-pointer hover:bg-gray-50 text-sm"
                    onClick={() =>
                      onSelect?.({
                        projectId: p.id,
                        projectName: p.name,
                        sectionId: sec.id,
                        sectionName: sec.name,
                      })
                    }
                  >
                    • {sec.name}
                  </div>
                ))}
            </div>
          ))}

        {/* My Projects Section Title */}
        {filtered.some((p) => p.group === "My Projects") && (
          <div className="font-semibold text-gray-700 px-3 py-2">
            My Projects
          </div>
        )}

        {/* My Projects List */}
        {filtered
          .filter((p) => p.group === "My Projects")
          .map((p) => (
            <div key={p.id}>
              {/* Project Line */}
              <div
                className="flex items-center justify-between px-3 py-2 rounded-md cursor-pointer hover:bg-gray-100"
                onClick={() => {
                  fetchSections(p.id);
                  onSelect?.({
                    projectId: p.id,
                    projectName: p.name,
                    sectionId: null,
                    sectionName: null,
                  });
                }}
              >
                <div className="flex items-center gap-2">
                  {p.icon} {p.name}
                </div>
              </div>

              {/* Render Sections */}
              {sections[p.id] &&
                sections[p.id].map((sec) => (
                  <div
                    key={sec.id}
                    className="pl-8 py-1 cursor-pointer hover:bg-gray-50 text-sm"
                    onClick={() =>
                      onSelect?.({
                        projectId: p.id,
                        projectName: p.name,
                        sectionId: sec.id,
                        sectionName: sec.name,
                      })
                    }
                  >
                    • {sec.name}
                  </div>
                ))}
            </div>
          ))}
      </div>
    </div>
  );

  return (
    <Dropdown overlay={menu} trigger={["click"]} placement="bottomLeft">
      <div className="flex items-center gap-1 cursor-pointer border rounded-md px-2 py-1 hover:bg-gray-50">
        <TagOutlined className="text-gray-500" />
        <span>
          {selected?.sectionName
            ? `${selected.projectName} / ${selected.sectionName}`
            : selected?.projectName
            ? selected.projectName
            : "Select project"}
        </span>

        <DownOutlined className="text-xs" />
      </div>
    </Dropdown>
  );
}

import React, { useState, useEffect  } from "react";
import {
  InboxOutlined,
  FolderOutlined,
  TagOutlined,
  DownOutlined,
} from "@ant-design/icons";
import { Dropdown, Input } from "antd";
import {https_taskflow} from "../../service/api";

export default function ProjectSelectDropdown({
    selectedProject = {},
    selectedSection = {},
    onSelectedSection,
    onSelectedProject,
    disabled
}) {
  const [search, setSearch] = useState("");
  const [projects, setProject] = useState([]);
  const [open, setOpen] = useState(false);

  // Fetch api để lấy danh sách các project và section
  useEffect(() => {

    const fetchProjects = async () => {
      try {
        const response = await https_taskflow.get("/v1/projects");
        // kiểm tra status
        if (response.status !== 200) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        setProject(response.data.data);
      } catch (err) {
        console.error("Error fetching tasks:", err);
      }
    };



    fetchProjects();
  }, []);

  const filtered = projects.filter((p) =>
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      p.section?.some((s) =>
          s.name?.toLowerCase().includes(search.toLowerCase())
      )
  );

    // Set up lại 2 đối tượng selectedProject và selectedSection nếu mà người dùng đã có sẵn thì thêm tên vào để hiển thị
    projects.forEach((project) => {
        if (project.id === selectedProject.id) {
            selectedProject.name = project.name;
            project.section.forEach((section) => {
                if (selectedSection.id === section.id) {
                    selectedSection.name = section.name;
                }
            })
        }
    })




  const menu = (
    <div className="bg-white rounded-xl shadow-lg p-2 w-72">
      {/* Search input */}
      <Input
        placeholder="Type a project name"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        className="mb-2"
        size="small"
      />

      {/*/!* Project list *!/*/}
      <div className="max-h-64 overflow-y-auto">
        {/* My Projects Section */}
        {(
          <>
            <div className="font-semibold text-gray-700 px-3 py-2">
              My Projects
            </div>
            {filtered.map((p) => (
                <div key={p.id}>
                  {/* Mục cha */}
                  <div
                      className={`flex items-center justify-between px-3 py-2 rounded-md cursor-pointer`}
                  >
                    <div className="flex items-center gap-2">
                      {p.icon} {p.name}
                    </div>
                  </div>

                  {/* Mục con (nếu có) */}
                  {p.section && p.section.length > 0 && (
                      <div className="ml-6 mt-1 space-y-1">
                        {p.section.map((s) => (
                            <div
                                key={s.id || s.name}
                                className={`flex items-center justify-between px-3 py-1.5 rounded cursor-pointer hover:bg-gray-50 ${
                                    selectedSection.name === s.name ? "bg-gray-100" : ""
                                }`}
                                onClick={(e) => {
                                  e.stopPropagation(); // không trigger cha
                                  onSelectedSection?.(s);
                                  onSelectedProject?.(p);
                                  setOpen(false);
                                }}
                            >
                              <div className="flex items-center gap-2 text-sm text-gray-700">
                                {s.icon || <div className="w-4" />} {/* placeholder nếu không có icon */}
                                {s.name}
                              </div>
                              {selectedSection.name === s.name && <span className="text-red-500 text-xs">✔</span>}
                            </div>
                        ))}
                      </div>
                  )}
                </div>
            ))}
          </>
        )}
      </div>
    </div>
  );

  return (
    <Dropdown overlay={menu} trigger={["click"]} placement="bottomLeft"  open={open  && !disabled}
              onOpenChange={(v) => setOpen(v)}>
      <div  onClick={(e) => {
          if (disabled) return; // không làm gì khi disabled
            }}
            className={`bg-white rounded-xl shadow-lg p-2 ${disabled ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}`}>
        <TagOutlined className="text-gray-500 pr-4" />
        <span>{selectedProject.id !== undefined ? selectedProject.name + " / "+ selectedSection.name : "Select project / section"}</span>
        <DownOutlined className="text-xs pl-3" />
      </div>
    </Dropdown>
  );
}
